package com.example.canoga;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class GameModeActivity extends AppCompatActivity {
    RadioGroup player_mode_choices, tile_num_choices;
    RadioButton player_mode, tile_num;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);

        player_mode_choices = findViewById(R.id.player_mode_toggle);
        RadioButton singleplayer = findViewById(R.id.singleplayer_toggle);
        RadioButton multiplayer = findViewById(R.id.multiplayer_toggle);

        tile_num_choices = findViewById(R.id.tiles_toggle);

        EditText player1_name = findViewById(R.id.player1_name);
        EditText player2_name = findViewById(R.id.player2_name);

        Button start_btn = findViewById(R.id.start_btn);

        singleplayer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                player1_name.setVisibility(View.VISIBLE);
                player2_name.setVisibility(View.INVISIBLE);
            }
        });

        multiplayer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                player1_name.setVisibility(View.VISIBLE);
                player2_name.setVisibility(View.VISIBLE);
            }
        });

        start_btn.setOnClickListener(view -> {
            Intent myIntent = new Intent(getApplicationContext(), GameActivity.class);
            String p1_name, p2_name;

            int singleP_int = check_player_mode();
            if(singleP_int < 0){
                Toast.makeText(getApplicationContext(),"Must choose single or multiplayer.",Toast.LENGTH_SHORT).show();
                return;
            }
            else if(singleP_int == 0){
                p1_name = player1_name.getText().toString();
                p2_name = player2_name.getText().toString();
                myIntent.putExtra("singleP", "false");
                if(p1_name.isEmpty() || p2_name.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Must enter players' names.",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            else{
                p1_name = player1_name.getText().toString();
                p2_name = "Computer";
                myIntent.putExtra("singleP", "true");
                if(p1_name.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Must enter player name.",Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            int tile_num = check_tiles();
            if(tile_num == -1){
                Toast.makeText(getApplicationContext(),"Must choose a tile number to play with.",Toast.LENGTH_SHORT).show();
                return;
            }
            myIntent.putExtra("player1Name",p1_name);
            myIntent.putExtra("player2Name",p2_name);
            myIntent.putExtra("tiles", String.valueOf(tile_num));
            mStartForResult.launch(myIntent);

        });
    }

    /* *********************************************************************
    Function Name: check_tiles
    Purpose: To return a tile if user has selected one.
    Parameters: none
    Return Value: int variable that holds the number of tiles the user chose
                    to play with.
    Algorithm:
                1) Check if a tile has been selected. If not, return -1. If
                    yes, parse the tile_num from view id and return it as an
                    int.
    Assistance Received: none
    ********************************************************************* */
    int check_tiles(){
        int tile_num_id = tile_num_choices.getCheckedRadioButtonId();
        if(tile_num_id == -1) {
            return -1;
        }
        tile_num = findViewById(tile_num_id);
        // parsing from char sequence to string to int
        return Integer.parseInt((String)tile_num.getText());
    }

    /* *********************************************************************
    Function Name: check_player_mode
    Purpose: To check what player mode the user chose.
    Parameters: none
    Return Value: Int variable that holds 0 if user selected two player mode,
                    and 1 if uesr selected player vs computer mode.
    Algorithm:
                1) Check if a player_mode has been selected.
                2) If yes return 0 if single player mode has been chosen and 1
                    two player mode has been chosen.
    Assistance Received: none
    ********************************************************************* */
    int check_player_mode(){
        int player_mode_id = player_mode_choices.getCheckedRadioButtonId();
        if(player_mode_id < 0) return player_mode_id;

        player_mode = findViewById(player_mode_id);
        String player_mode_str = (String) player_mode.getText();
        if(player_mode_str.equals("Player1 vs Computer")){
            return 1;
        }
        else{
            return 0;
        }
    }
}