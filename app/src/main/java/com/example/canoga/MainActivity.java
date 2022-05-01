package com.example.canoga;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AlertDialog.Builder builder;
    ArrayList<String> filepaths = new ArrayList<>();
    String filepath;
    List<String> lines;
    List<List<Integer>> loaded_dice_rolls;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button new_game = findViewById(R.id.new_game_btn);
        Button load_game = findViewById(R.id.load_game_btn);
        Button instructions = findViewById(R.id.instructions_btn);
        new_game.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GameModeActivity.class);

            startActivity(intent);
        });
        load_game.setOnClickListener(view -> {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                Toast.makeText(MainActivity.this, "SD Card not mounted", Toast.LENGTH_SHORT).show();
                return;
            }
            File dir= new File(getApplicationContext().getFilesDir() + "/Canoga");
            filepaths = new ArrayList<>();
            getFiles(dir);
            if(filepaths.isEmpty()){
                Toast.makeText(getApplicationContext(), "There are no saved games.", Toast.LENGTH_LONG).show();
                return;
            }
            String msg = "Pick a game";
            String[] files = new String[filepaths.size()];
            for(int i = 0; i < filepaths.size(); i++){
                files[i] = filepaths.get(i).substring(filepaths.get(i).indexOf("/Canoga/") + 8, filepaths.get(i).indexOf(".txt"));
            }
            filepath = filepaths.get(0);
            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(msg)
                    .setSingleChoiceItems(files, 0, (dialogInterface, i) -> {
                        // The user checked or unchecked a box
                        if(i>=0 && i<filepaths.size()){
                            filepath = filepaths.get(i);
                        }
                    })
                    .setPositiveButton("Load", (dialog, i) -> {
                        // load game
                        Log.d("FILE_IO", filepath + " opened.");

                        read_file(filepath);
                        if(!load_file()) {
                            Toast.makeText(getApplicationContext(), "Game could not be loaded", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {

                    });

            //Creating dialog box
            LayoutInflater inflater= LayoutInflater.from(getApplicationContext());
            View dialogBoxView=inflater.inflate(R.layout.load_game_dialog_box, null);
            AlertDialog alert = builder.create();
            alert.setView(dialogBoxView);
            alert.show();
        });
        instructions.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), HowToPlayActivity.class);

            startActivity(intent);
        });
    }

    /* *********************************************************************
    Function Name: load_file
    Purpose: To load a game from a file.
    Parameters: none
    Return Value: a boolean variable that is true when the game successfully loads
                  and false when there is error loading the game
    Algorithm:
                1) Parse each line in the lines arraylist to get the required
                    game state information.
                2) Initialize a new intent for the GameActivity and put the information
                    as parameters to send to the intent.
                3) Start the intent.
    Assistance Received: none
    ********************************************************************* */
    private boolean load_file() {
        Intent myIntent = new Intent(getApplicationContext(), GameActivity.class);
        String player1_name, player2_name, first_turn, next_turn;
        String player1_score, player2_score;

        Log.d("TAG", lines.toString());

        player1_name = lines.get(0).substring(0, lines.get(0).indexOf(":"));
        player2_name = lines.get(4).substring(0, lines.get(4).indexOf(":"));

        player1_score = lines.get(2).substring(lines.get(2).indexOf(":") + 2);
        player2_score = lines.get(6).substring(lines.get(6).indexOf(":") + 2);

        first_turn = lines.get(8).substring(lines.get(8).indexOf(":") + 2);
        next_turn = lines.get(9).substring(lines.get(9).indexOf(":") + 2);

        String player1_board_state, player2_board_state;
        player1_board_state = lines.get(1).substring(lines.get(1).indexOf(":") + 2);
        player2_board_state = lines.get(5).substring(lines.get(5).indexOf(":") + 2);

        boolean singleP = player1_name.equals("Computer") || player2_name.equals("Computer");

        // checking if there was any advantage square that cannot be uncovered ( if its the first round )
        if (lines.get(8).substring(lines.get(8).indexOf("First") + 5).charAt(1) != 'T') {
            myIntent.putExtra("advantage", "Yes");
        }
        // booleans to check if the first player's name was assigned to first turn correctly
        if (first_turn.equals(player1_name)) {
            myIntent.putExtra("firstTurn", player1_name);
        }
        else if (first_turn.equals(player2_name)){
            myIntent.putExtra("firstTurn", player2_name);
        }
        if(next_turn.equals(player1_name)){
            myIntent.putExtra("nextTurn", player1_name);
        }
        else if(next_turn.equals(player2_name)){
            myIntent.putExtra("nextTurn", player2_name);
        }

        myIntent.putExtra("singleP", String.valueOf(singleP));
        myIntent.putExtra("player1Name", player1_name);
        myIntent.putExtra("player2Name",player2_name);
        myIntent.putExtra("player1Score",player1_score);
        myIntent.putExtra("player2Score",player2_score);
        myIntent.putExtra("player1BoardState",player1_board_state);
        myIntent.putExtra("player2BoardState",player2_board_state);
        myIntent.putExtra("loadedDiceRolls", loaded_dice_rolls.toString());

        startActivity(myIntent);

        return true;
    }

    /* *********************************************************************
    Function Name: read_file
    Purpose: To read a file and save file data into a String arraylist
    Parameters: filepath, String variable that holds the absolute file path
                        of the file to be read
    Return Value:
    Algorithm:
                1) Initialize lines arraylist and loaded_dice_rolls arraylist.
                2) Initialize a new file with the passed filepath.
                3) Initialize a new BufferedReader and read the file line by
                    line.
                4) Check for any dice rolls that are available.
    Assistance Received: none
    ********************************************************************* */
    private void read_file(String filepath) {
        lines = new ArrayList<>();
        loaded_dice_rolls = new ArrayList<>();
        File file = new File(filepath);
        //Read text from file
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean contains_dice_rolls = false;

            while ((line = br.readLine()) != null) {
                if(line.contains("Dice:")){
                    contains_dice_rolls = true;
                    continue;
                }
                if(contains_dice_rolls){
                    List<Integer> dice_roll = new ArrayList<>();
                    for(int i = 0; i < line.length(); i++){
                        if(Character.isDigit(line.charAt(i))){
                            dice_roll.add(Integer.parseInt(String.valueOf(line.charAt(i))));
                        }
                    }
                    loaded_dice_rolls.add(dice_roll);
                }
                else{
                    lines.add(line);
                }
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
    }

    /* *********************************************************************
    Function Name: getFiles
    Purpose: To recursively find all files inside a directory
    Parameters: dir, File variable that holds the directory to be checked
    Return Value:
    Algorithm:
                1) Initialize a File array with the listFiles() function.
                2) If there are files in the directory then go through every
                    file in the directory.
                3) Check if each file is directory or not. If yes, call the
                    getFiles function recursively.
                4) If the file is not a directory then add it to the list of
                    files.
    Assistance Received: none
    ********************************************************************* */
    void getFiles(File dir) {
        File[] file_list = dir.listFiles();

        if (file_list != null) {
            for (File file : file_list) {
                if (file.isDirectory()) {
                    // if its a directory need to get the files under that directory
                    getFiles(file);
                } else {
                    // add path of  files to your arraylist for later use
                    filepaths.add(file.getAbsolutePath());
                }
            }
        }
    }
}