package com.example.canoga;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    int tile_num;
    boolean to_cover, new_game;
    Game game;
    Button roll_or_choose, roll_one_btn, help_btn, log_btn;
    ImageView dice_one_img, dice_two_img;
    AlertDialog.Builder builder;
    Button player1_first_turn_indicator, player2_first_turn_indicator, player1_this_turn_indicator, player2_this_turn_indicator;
    TextView player1_score, player2_score, player1_name_label, player2_name_label;
    LinearLayout player1_tiles, player2_tiles;
    List<Integer> player_moves = new ArrayList<>(), help_option = new ArrayList<>(), dices_rolled;
    List<View> tile_views = new ArrayList<>();

    private static final int PERMISSION_REQUEST_CODE = 7;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    finish();
                    // Handle the Intent
                }
            });
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.side_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.save_game) {
            this.save_game();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* *********************************************************************
    Function Name: save_game
    Purpose: To provide the user with a dialog box to enter the name of the
                game to be saved.
    Parameters:
    Return Value: none (void)
    Algorithm:
        1) Get the name entered by the user and call the save_game function
            from the current_round by passing in the file name as parameter.
        2) Display Toasts according to the response returned from the save_game
            function.
    Assistance Received: none
    ********************************************************************* */
    @RequiresApi(api = Build.VERSION_CODES.R)
    void save_game(){
        final EditText edittext = new EditText(GameActivity.this);
        builder.setMessage("Enter a name for the game")
                .setTitle("Save game")
                .setView(edittext)
                .setPositiveButton("Save", (dialog, whichButton) -> {
                    //get file name and save the game
                    String file_name = edittext.getText().toString();
                    if(!(ContextCompat.checkSelfPermission(GameActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
                        ActivityCompat.requestPermissions(GameActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                    switch(game.current_round.save_game(file_name, getApplicationContext())){
                        case -1:
                            Toast.makeText(getApplicationContext(), "Cannot create directory", Toast.LENGTH_SHORT).show();
                            break;
                        case 0:
                            Toast.makeText(getApplicationContext(), "Game saved", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case 1:
                            Toast.makeText(getApplicationContext(), "Game name already exists", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "An error occured", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .setNegativeButton("Cancel", (dialog, whichButton) -> {
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            Toast.makeText(GameActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(GameActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        new_game = true;

        dice_one_img = findViewById(R.id.dice_one_img);
        dice_two_img = findViewById(R.id.dice_two_img);

        player1_tiles = findViewById(R.id.player1_tiles);
        player2_tiles = findViewById(R.id.player2_tiles);

        player1_first_turn_indicator = findViewById(R.id.player1_first_turn_indicator);
        player1_this_turn_indicator = findViewById(R.id.player1_current_turn_indicator);
        player2_first_turn_indicator = findViewById(R.id.player2_first_turn_indicator);
        player2_this_turn_indicator = findViewById(R.id.player2_current_turn_indicator);

        player1_score = findViewById(R.id.player1_score);
        player2_score = findViewById(R.id.player2_score);

        player1_name_label = findViewById(R.id.player1_name_label);
        player2_name_label = findViewById(R.id.player2_name_label);

        roll_one_btn = findViewById(R.id.roll_one_btn);
        roll_or_choose = findViewById(R.id.roll_or_choose);
        help_btn = findViewById(R.id.help_icon);
        log_btn = findViewById(R.id.log_icon);
        player1_tiles = findViewById(R.id.player1_tiles);
        player2_tiles = findViewById(R.id.player2_tiles);

        builder = new AlertDialog.Builder(this);

        Intent myIntent = getIntent(); // gets the previously created intent

        String player1_name = myIntent.getStringExtra("player1Name");
        String player2_name= myIntent.getStringExtra("player2Name");
        if(myIntent.hasExtra("tiles")){
            tile_num = Integer.parseInt(myIntent.getStringExtra("tiles"));
        }
        boolean singleP = !myIntent.getStringExtra("singleP").equals("false");

        int player1_score = 0, player2_score = 0;
        if (myIntent.hasExtra("player1Score")) {
            player1_score = Integer.parseInt(myIntent.getStringExtra("player1Score"));
            new_game = false;
        }
        if(myIntent.hasExtra("player2Score")) {
            player2_score = Integer.parseInt(myIntent.getStringExtra("player2Score"));
        }
        String player1_board_state, player2_board_state;
        boolean[] player1_board = new boolean[11], player2_board = new boolean[11];

        if(myIntent.hasExtra("player1BoardState") && myIntent.hasExtra("player2BoardState")){
            player1_board_state  = myIntent.getStringExtra("player1BoardState");
            player2_board_state  = myIntent.getStringExtra("player2BoardState");

            // int variable to count the number of squares
            int index = 0;
            for (int i = 0; i < player1_board_state.length(); i++) {
                char square_state = player1_board_state.charAt(i);
                if (square_state == '*') {
                    player1_board[index] = true;
                    ++(index);
                }
                else if (Character.isDigit(square_state)) {
                    boolean quit = false;
                    while (!quit) {
                        if (Integer.parseInt(String.valueOf(square_state)) > 9 && i < player1_board_state.length()){
                            if (!Character.isDigit(player1_board_state.charAt(i + 1))) {
                                quit = true;
                                continue;
                            }
                            else{
                                i++;
                            }
                        }
                        else{
                            quit = true;
                        }
                        i++;
                    }
                    player1_board[index] = false;
                    ++(index);
                }
            }
            // resetting index
            index = 0;
            for (int i = 0; i < player2_board_state.length(); i++) {
                char square_state = player2_board_state.charAt(i);
                if (square_state == '*') {
                    player2_board[index] = true;
                    ++(index);
                }
                else if (Character.isDigit(square_state)) {
                    boolean quit = false;
                    while (!quit) {
                        if (Integer.parseInt(String.valueOf(square_state)) > 9 && i < player2_board_state.length()){
                            if (!Character.isDigit(player2_board_state.charAt(i + 1))) {
                                quit = true;
                                continue;
                            }
                            else{
                                i++;
                            }
                        }
                        else{
                            quit = true;
                        }
                        i++;
                    }
                    player2_board[index] = false;
                    ++(index);
                }
            }
            tile_num = index;
        }
        player1_board = Arrays.copyOfRange(player1_board, 0, tile_num);
        player2_board = Arrays.copyOfRange(player2_board, 0, tile_num);

        this.game = new Game(singleP, player1_name, player2_name);
        
        if(!new_game){
            initialize_game(player1_score, player2_score, player1_board, player2_board);

            String first_turn = "", next_turn = "";
            if(myIntent.hasExtra("firstTurn") && myIntent.hasExtra("nextTurn")){
                first_turn = myIntent.getStringExtra("firstTurn");
                if(first_turn.equals(player1_name)){
                    this.game.current_round.player1.set_had_first_turn(true);
                    player1_first_turn_indicator.setVisibility(View.VISIBLE);
                }
                else if(first_turn.equals(player2_name)) {
                    this.game.current_round.player2.set_had_first_turn(true);
                    player2_first_turn_indicator.setVisibility(View.VISIBLE);
                }
                next_turn = myIntent.getStringExtra("nextTurn");
                if(next_turn.equals(player1_name)){
                    this.game.current_round.set_player_turns(this.game.current_round.player1, this.game.current_round.player2);
                    player1_this_turn_indicator.setVisibility(View.VISIBLE);
                }
                else if(next_turn.equals(player2_name)){
                    this.game.current_round.set_player_turns(this.game.current_round.player2, this.game.current_round.player1);
                    player2_this_turn_indicator.setVisibility(View.VISIBLE);
                }
                game.current_round.first_turn_of_round = false;
                if(game.current_round.nextPlayer.get_covered_squares().size() <= 1){
                    if(game.current_round.nextPlayer.get_covered_squares().size() == 0) {
                        game.current_round.first_turn_of_round = true;
                    }
                    else if(game.current_round.nextPlayer.get_covered_squares().size() == 1) {
                        if(game.current_round.nextPlayer.get_covered_squares().get(0) == game.current_round.nextPlayer.get_advantage_square()) {
                            game.current_round.first_turn_of_round = true;
                        }
                    }
                }
                if(myIntent.hasExtra("advantage")){
                    if (player1_name.equals(next_turn) && (this.game.current_round.player2.get_covered_squares().size() == 1)) {
                        this.game.current_round.player2.set_advantage_square(this.game.current_round.player2.get_covered_squares().get(0));
                    }
                    else if (player2_name.equals(next_turn) && (this.game.current_round.player1.get_covered_squares().size() == 1)) {
                        this.game.current_round.player1.set_advantage_square(this.game.current_round.player1.get_covered_squares().get(0));
                    }
                }
            }

            if(!first_turn.isEmpty() && !next_turn.isEmpty()){
                if(next_turn.equals("Computer")){
                    roll_or_choose.setText("NEXT");
                }
                else{
                    roll_or_choose.setText("ROLL");
                }
            }
            else{
                roll_or_choose.setText("BEGIN");
            }
            if(myIntent.hasExtra("loadedDiceRolls")){
                List<List<Integer>> dice_rolls = new ArrayList<>();
                String LDR = myIntent.getStringExtra("loadedDiceRolls");
                for(int i = 1; i < LDR.length() - 1; i++){
                    List<Integer> dice_roll = new ArrayList<>();
                    if(LDR.charAt(i) == '['){
                        i++;
                        dice_roll.add(Integer.parseInt(String.valueOf(LDR.charAt(i))));
                        i+=3;
                        dice_roll.add(Integer.parseInt(String.valueOf(LDR.charAt(i))));
                        i++;
                        dice_rolls.add(dice_roll);
                    }
                }
                game.current_round.set_l_dice_rolls(dice_rolls);
            }
        }
        else{
            initialize_game(player1_score, player2_score, new boolean[tile_num], new boolean[tile_num]);
        }
    }

    /* *********************************************************************
    Function Name: initialize_game
    Purpose: To initialize the game at the start of a game, build widget
                components and set widget states to display to the user.
    Parameters:
            p1_score, p2_score, int variables that hold the players' scores
                at the time of saving the game. In default cases, the values are 0.
            player1_board, player2_board, boolean array variables that hold
                the state of the players board at the time of saving the game.
                In default cases, the values are all false/uncovered.
    Return Value:
    Algorithm:
                1) Call the function to initialize a round by passing the tile numbers.
                2) Set the respective text labels from the player objects inside the
                    round object in the game object.
                3) Change the dice pics to blank/0.
                4) Generate the button widgets and place them in each player's side.
                5) Bind onClickListeners to buttons that can roll the dice.
    Assistance Received: none
    ********************************************************************* */
    @SuppressLint("SetTextI18n")
    void initialize_game(int p1_score, int p2_score, boolean[] player1_board, boolean[] player2_board){
        game.initialize_round(tile_num);
        player1_name_label.setText(this.game.current_round.player1.get_name());
        player2_name_label.setText(this.game.current_round.player2.get_name());
        this.game.current_round.player1.set_score(p1_score);
        this.game.current_round.player2.set_score(p2_score);
        this.game.current_round.player1.set_board(tile_num, player1_board);
        this.game.current_round.player2.set_board(tile_num, player2_board);
        player1_tiles.removeAllViews();
        player2_tiles.removeAllViews();
        List<View> player1_tile_views = new ArrayList<>();
        List<View> player2_tile_views = new ArrayList<>();

        change_dice_pics(new ArrayList<Integer>() {{
            add(0);
            add(0);
        }});

        LinearLayout.LayoutParams buttonlayout;
        AppCompatButton p1_btn, p2_btn;
        for(int i = 0; i < tile_num; i++){
            p1_btn = new AppCompatButton(this);
            p2_btn = new AppCompatButton(this);
            p1_btn.setText(String.valueOf(i+1));
            p2_btn.setText(String.valueOf(i+1));

            buttonlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonlayout.setMargins(0,0,0,5);

            p1_btn.setBackgroundColor(
                    !player1_board[i]?
                            getResources().getColor(R.color.red):
                            getResources().getColor(R.color.green));
            p2_btn.setBackgroundColor(
                    !player2_board[i]?
                            getResources().getColor(R.color.red):
                            getResources().getColor(R.color.green));
            p1_btn.setTextSize(20);
            p2_btn.setTextSize(20);
            p1_btn.setTag(i+1);
            p2_btn.setTag(i+1);
            p1_btn.setOnClickListener(this::player1_tile_click);
            p2_btn.setOnClickListener(this::player2_tile_click);
            p1_btn.setEnabled(false);
            p2_btn.setEnabled(false);
            player1_tiles.addView(p1_btn, buttonlayout);
            player2_tiles.addView(p2_btn, buttonlayout);
            player1_tile_views.add(p1_btn);
            player2_tile_views.add(p2_btn);
        }

        player1_score.setText(String.valueOf(p1_score));
        player2_score.setText(String.valueOf(p2_score));

        roll_one_btn.setEnabled(false);
        roll_one_btn.setOnClickListener(view -> {
            dices_rolled = game.current_round.roll_dice(true);
            int totalVal = 0;
            for(int dice:dices_rolled){ totalVal += dice;}
            change_dice_pics(dices_rolled);
            if(game.current_round.check_movability(totalVal)){
                String dice_rolled = String.valueOf(dices_rolled);
                roll_or_choose.setText("SELECT");
                game.current_round.log_record.add_log(game.current_round.currentPlayer.get_name() + " rolled " + dice_rolled);
                enableOrDisableTiles(true);
            }
            else{
                game.current_round.switch_turns();
                roll_or_choose.setText("SWITCH TURN");
            }
            roll_one_btn.setEnabled(false);
        });
        help_btn.setOnClickListener(view -> {
            int value = 0;
            for(int dice:dices_rolled){ value += dice;}
            help_option = game.current_round.provide_help(value);
            to_cover = help_option.get(0) == 0;
            help_option.remove(0);
            Log.d("HELP OPTION", help_option.toString());
            for(int help_tile: help_option){
                if(to_cover){
                    game.current_round.currentPlayer.get_tile_views()
                            .get(help_tile-1)
                            .setBackgroundColor(getResources().getColor(R.color.yellow));
                }
                else{
                    game.current_round.nextPlayer.get_tile_views()
                            .get(help_tile-1)
                            .setBackgroundColor(getResources().getColor(R.color.yellow));
                }
            }
            display_info(game.current_round.log_record.get_latest_log(),"Here's some help");
        });
        help_btn.setEnabled(false);
        log_btn.setOnClickListener(view -> display_info(game.current_round.log_record.get_all_log(), "All Log"));
        if((!game.current_round.player1.had_first_turn()
                && !game.current_round.player2.had_first_turn())
                || !(game.current_round.player1.get_previous_round_score()==0
                        && game.current_round.player2.get_previous_round_score()==0)){
            roll_or_choose.setText("BEGIN");
        }
        else{
            roll_or_choose.setText("ROLL");
        }
        game.current_round.player1.set_tile_views(player1_tile_views);
        game.current_round.player2.set_tile_views(player2_tile_views);
        player1_first_turn_indicator.setVisibility(View.INVISIBLE);
        player1_this_turn_indicator.setVisibility(View.INVISIBLE);
        player2_first_turn_indicator.setVisibility(View.INVISIBLE);
        player2_this_turn_indicator.setVisibility(View.INVISIBLE);
    }

    /* *********************************************************************
    Function Name: main_button_click
    Purpose: To simulate the button click event of the button at the center
                of the game.
    Parameters:
            view, a View object that stores the view of the button
                that was clicked.
    Return Value:
    Algorithm:
                1) Initialize a log message and get the text tha was on the button
                    when it was clicked.
                2) If the text was BEGIN, call the function to decide the first player
                    and turn on the indicators.
                3) If the text was ROLL, call the function to roll the dice for the
                    current player.
                4) If the text was SELECT, call the function to select player move.
                5) If the text was NEXT, call the computer function to select a move.
                6) If the text was SWITCH TURN, call the function to switch turns and
                    change the respective widget component.s
                7) For default cases, set the text to ROLL.
    Assistance Received: none
    ********************************************************************* */
    @SuppressLint("SetTextI18n")
    public void main_button_click(View view){
        String log_msg = "";
        String btn_text = ((Button) view).getText().toString();
        switch (btn_text){
            case "BEGIN":
                if(game.current_round.decide_first_player()){
                    if(game.current_round.currentPlayer == game.current_round.player1){
                        player1_first_turn_indicator.setVisibility(View.VISIBLE);
                        player1_this_turn_indicator.setVisibility(View.VISIBLE);
                        player2_first_turn_indicator.setVisibility(View.INVISIBLE);
                        player2_this_turn_indicator.setVisibility(View.INVISIBLE);
                    }
                    else{
                        player2_first_turn_indicator.setVisibility(View.VISIBLE);
                        player2_this_turn_indicator.setVisibility(View.VISIBLE);
                        player1_first_turn_indicator.setVisibility(View.INVISIBLE);
                        player1_this_turn_indicator.setVisibility(View.INVISIBLE);
                    }
                    if(game.current_round.currentPlayer.get_name().equals("Computer")){
                        ((Button) view).setText("NEXT");
                    }
                    else{
                        ((Button) view).setText("ROLL");
                    }
                }
                if(game.current_round.player1.get_advantage_square()!=0 &&
                    !game.current_round.player1.get_board_state()[game.current_round.player1.get_advantage_square()-1]){
                    log_msg += "Covering " + game.current_round.player1.get_name()
                            + "'s tile " + game.current_round.player1.get_advantage_square() + '\n'
                            + "Because " + game.current_round.player2.get_name()
                            + " went first in the last round.";
                    game.current_round.player1.cover(game.current_round.player1.get_advantage_square());
                    player1_tiles.getChildAt(game.current_round.player1.get_advantage_square() - 1)
                            .setBackgroundColor(getResources().getColor(R.color.green));
                }
                else if(game.current_round.player2.get_advantage_square()!=0 &&
                        !game.current_round.player2.get_board_state()[game.current_round.player2.get_advantage_square()-1]){
                    log_msg += "Covering " + game.current_round.player2.get_name()
                            + "'s tile " + game.current_round.player2.get_advantage_square() + '\n'
                            + "Because " + game.current_round.player1.get_name()
                            + " went first in the last round.";
                    game.current_round.player2.cover(game.current_round.player2.get_advantage_square());
                    player2_tiles.getChildAt(game.current_round.player2.get_advantage_square() - 1)
                            .setBackgroundColor(getResources().getColor(R.color.green));
                }
                display_info(game.current_round.log_record.get_latest_log() + '\n' + log_msg,"First turn");
                break;
            case "ROLL":
                dices_rolled = game.current_round.roll_dice(false);
                int totalVal = 0;
                for(int dice:dices_rolled){ totalVal += dice;}
                change_dice_pics(dices_rolled);
                if(game.current_round.check_movability(totalVal)){
                    String dice_rolled = String.valueOf(dices_rolled);
                    ((Button) view).setText("SELECT");
                    game.current_round.log_record.add_log(game.current_round.currentPlayer.get_name() + " rolled " + dice_rolled);

                    enableOrDisableTiles(true);
                    help_btn.setEnabled(true);
                }
                else{
                    game.current_round.switch_turns();
                    ((Button) view).setText("SWITCH TURN");
                }
                roll_one_btn.setEnabled(false);
                break;
            case "SELECT":
                if(!player_moves.isEmpty()){
                    select_move(view);
                    help_btn.setEnabled(false);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Select tiles first", Toast.LENGTH_SHORT).show();
                }
                break;
            case "NEXT":
                game.current_round.currentPlayer.reset_latest_move();
                if(game.current_round.check_roll_one()){
                    dices_rolled = game.current_round.roll_dice(game.current_round.currentPlayer.roll_one());
                }
                else{
                    dices_rolled = game.current_round.roll_dice(false);
                }
                Log.d("DICES ROLLED", dices_rolled.toString());

                int totalSum = 0;
                for(int dice:dices_rolled){totalSum += dice;}
                change_dice_pics(dices_rolled);
                if(game.current_round.check_movability(totalSum)){
                    String dice_rolled = String.valueOf(dices_rolled);
                    game.current_round.log_record.add_log(game.current_round.currentPlayer.get_name() + " rolled " + dice_rolled);
                    player_moves = game.current_round.choose_computer_move(totalSum);
                    Log.d("MOVES", player_moves.toString());

                    to_cover = player_moves.get(0) == 0;
                    player_moves.remove(0);
                    builder.setMessage(game.current_round.currentPlayer.get_latest_move())
                            .setTitle("Computer Move")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, id) -> {
                                select_move(view);
                                ((Button) view).setText("NEXT");
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else{
                    game.current_round.switch_turns();
                    ((Button) view).setText("SWITCH TURN");
                }
                break;
            case "SWITCH TURN":
                if(game.current_round.currentPlayer == game.current_round.player2){
                    player2_this_turn_indicator.setVisibility(View.VISIBLE);
                    player1_this_turn_indicator.setVisibility(View.INVISIBLE);
                }
                else{
                    player1_this_turn_indicator.setVisibility(View.VISIBLE);
                    player2_this_turn_indicator.setVisibility(View.INVISIBLE);
                }

                change_dice_pics(new ArrayList<Integer>() {{
                    add(0);
                    add(0);
                }});
                game.current_round.log_record.add_log("Turns switched.");

                if(game.current_round.currentPlayer.get_name().equals("Computer") || !game.current_round.l_dice_rolls.isEmpty()){
                    ((Button) view).setText("NEXT");
                    roll_one_btn.setEnabled(false);
                }
                else{
                    roll_one_btn.setEnabled(game.current_round.check_roll_one());
                    ((Button) view).setText("ROLL");
                }
                break;
            default:
                ((Button) view).setText("ROLL");
                break;
        }
    }

    /* *********************************************************************
    Function Name: select_move
    Purpose: To implement a user's selected option and switch turns.
    Parameters:
            view, a View object that stores the view of the button
                that was clicked to implement the select_move function.
    Return Value:
    Algorithm:
                1) If player move is the same as help option then, then
                    set the button's background to green if that tile on
                    currentPlayer's board is already green and red if it
                    was red. And same goes for the nextPlayer's board.
                2) If currentPlayer is not a computer player then cover the
                    currentPlayer's respective tiles and turn the button green
                    if currentPlayer intended to cover else uncover nextPlayer's
                    tiles and turn the button red if currentPlayer intended to uncover.
                3) Check if currentPlayer has won the game. If yes, then end the round.
                4) If no, then change button text to ROLL and disable the player tiles.
    Assistance Received: none
    ********************************************************************* */
    @SuppressLint("SetTextI18n")
    void select_move(View view){
        int totalDiceVal = 0, moves_sum = 0;
        if(!(player_moves == help_option)){
            for(int tile: help_option){
                if(game.current_round.currentPlayer.get_board_state()[tile-1]){
                    game.current_round.currentPlayer.get_tile_views()
                            .get(tile-1)
                            .setBackgroundColor(getResources().getColor(R.color.green));
                }
                else{
                    game.current_round.currentPlayer.get_tile_views()
                            .get(tile-1)
                            .setBackgroundColor(getResources().getColor(R.color.red));
                }
                if(game.current_round.nextPlayer.get_board_state()[tile-1]){
                    game.current_round.nextPlayer.get_tile_views()
                            .get(tile-1)
                            .setBackgroundColor(getResources().getColor(R.color.green));
                }
                else{
                    game.current_round.nextPlayer.get_tile_views()
                            .get(tile-1)
                            .setBackgroundColor(getResources().getColor(R.color.red));
                }
            }
        }
        if(!game.current_round.currentPlayer.get_name().equals("Computer")){
            for(int dice: dices_rolled){totalDiceVal += dice;}
            for(int move: player_moves){moves_sum += move;}
        }
        if(totalDiceVal == moves_sum){
            for(int i = 0; i < player_moves.size(); i++){
                if(to_cover){
                    game.current_round.currentPlayer.cover(player_moves.get(i));
                    game.current_round.currentPlayer.get_tile_views()
                            .get(player_moves.get(i)-1)
                            .setBackgroundColor(getResources().getColor(R.color.green));
                }
                else{
                    game.current_round.nextPlayer.uncover(player_moves.get(i));
                    game.current_round.nextPlayer.get_tile_views()
                            .get(player_moves.get(i)-1)
                            .setBackgroundColor(getResources().getColor(R.color.red));
                }
            }
            String chose = to_cover? " covered " : " uncovered ";
            game.current_round.log_record.add_log(game.current_round.currentPlayer.get_name() + chose + player_moves.toString());
            if(game.current_round.player_won()){
                game.current_round.end_round();
                //Uncomment the below code to Set the message and title from the strings.xml file
                String msg = "Winner: " + game.current_round.winner + '\n' +
                        "Winning Score: " + game.current_round.winning_score + '\n' +
                        "Points: " + '\n' +
                        game.current_round.player1.get_name() + ": " + game.current_round.player1.get_score() + '\n' +
                        game.current_round.player2.get_name() + ": " + game.current_round.player2.get_score() + '\n' +
                        "Would you like to play another round?";
                tile_num = 10;
                builder.setMessage(msg)
                        .setTitle("Round Over")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            builder = new AlertDialog.Builder(GameActivity.this);
                            String[] tiles = {"9", "10", "11"};
                            builder.setTitle("Pick Tiles")
                                    .setSingleChoiceItems(tiles, 1, (dialogInterface, i) -> {
                                        // The user checked or unchecked a box
                                        switch (i) {
                                            case 0:
                                                tile_num = 9;
                                                break;
                                            case 1:
                                                break;
                                            case 2:
                                                tile_num = 11;
                                                break;
                                        }
                                    })
                                    .setPositiveButton("Start", (dialog1, which) -> {
                                        // start new round
                                        List<List<Integer>> temp_holder = null;
                                        if(!game.current_round.l_dice_rolls.isEmpty()){
                                            temp_holder = game.current_round.l_dice_rolls;
                                        }
                                        initialize_game(game.current_round.player1.get_score(), game.current_round.player2.get_score(), new boolean[tile_num], new boolean[tile_num]);
                                        game.current_round.set_l_dice_rolls(temp_holder);
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                            // go to game over screen
                            Intent myIntent = new Intent(getApplicationContext(), GameOverActivity.class);

                            myIntent.putExtra("player1Name",game.player1_name);
                            myIntent.putExtra("player2Name",game.player2_name);
                            myIntent.putExtra("player1Score", String.valueOf(game.current_round.player1.get_score()));
                            myIntent.putExtra("player2Score", String.valueOf(game.current_round.player2.get_score()));
                            mStartForResult.launch(myIntent);
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.show();
            }
            enableOrDisableTiles(false);
            if(game.current_round.currentPlayer.get_name().equals("Computer") || !game.current_round.l_dice_rolls.isEmpty()){
                roll_one_btn.setEnabled(false);
            }
            else{
                roll_one_btn.setEnabled(game.current_round.check_roll_one());
            }
            ((Button) view).setText("ROLL");
        }
        else{
            for(int i = 0; i < tile_views.size(); i++){
                if(to_cover) {
                    game.current_round.currentPlayer.get_tile_views()
                            .get(player_moves.get(i)-1)
                            .setBackgroundColor(getResources().getColor(R.color.red));
                }
                else{
                    game.current_round.nextPlayer.get_tile_views()
                            .get(player_moves.get(i)-1)
                            .setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
            Toast.makeText(getApplicationContext(), "Invalid move. Check the sum.", Toast.LENGTH_SHORT).show();
        }
        player_moves.clear();
        tile_views.clear();
    }

/* *********************************************************************
Function Name: enableOrDisableTiles
Purpose: To enable or disable player tiles whenever needed.
Parameters:
        enable, a Boolean variable that holds true if tiles need to be
            enabled and false if tiles need to be disabled.
Return Value:
Algorithm:
     1) Iterate through all the tiles in both players' sides.
     2) Set the button's enable property with the parameter that was
        passed.
Assistance Received: none
********************************************************************* */
    void enableOrDisableTiles(boolean enable){
        for ( int i = 0; i < player1_tiles.getChildCount();  i++ ){
            View v1 = player1_tiles.getChildAt(i);
            v1.setEnabled(enable);
            View v2 = player2_tiles.getChildAt(i);
            v2.setEnabled(enable);
        }
    }

    /* *********************************************************************
    Function Name: player1_tile_click
    Purpose: To simulate the clicking of a tile in player1's side.
    Parameters:
            view, a View object which consists of the view that was clicked by the
                    player on the screen
    Return Value:
    Algorithm:
         1) Get the tile_num by parsing the id received from getTag() call.
         2) Check if player1 clicked on it, if yes, check if the tile is
            uncovered. If player2 clicked on it, check if tile is covered.
         3) For player1, set to_cover to true if player_moves is empty.
            For player2, set to_cover to false if player_moves is empty.
         4) Check if to_cover is true for player1, and false for player2.
            If yes, check if player_moves already has that tile.
         5) If no, add the tile in player_moves and set background to white.
         6) If yes, check if background is white. If yes, then check whether
            to make it red or green.
    Assistance Received: none
    ********************************************************************* */
    void player1_tile_click(View view){
        int tile_num = Integer.parseInt(view.getTag().toString());
        /*
        * Algorithm
        * 1. A tile is clicked: either left side (player1) or right side (player2) tiles.
        * 2. Find out whether player1 or player2 clicked on the tile.
        * 3. Lets say player1 tiles were clicked. If player1 clicked on it, check if the
        * tile is uncovered. If player2 clicked on it, check if tile is covered.
        * 4. For player 1, set to_cover to true if player_moves is empty.
        * For player2, set to_cover to false if player_moves is empty.
        * 5. Check if to_cover is true for player1, and false for player2.
        * If yes, check if player_moves already has that tile.
        * 6. If no, add the tile in player_moves and set background to white.
        * 7. If yes, check if background is white. If yes, then check whether to make it red or green.
        * */

        if(game.current_round.currentPlayer == game.current_round.player1 && !game.current_round.player1.get_board_state()[tile_num-1]){
            if(player_moves.isEmpty()){
                to_cover = true;
            }
            if(to_cover){
                if(!player_moves.contains(tile_num)){
                    player_moves.add(tile_num);
                    tile_views.add(view);
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                }
                else if(((ColorDrawable)view.getBackground()).getColor() == getResources().getColor(R.color.white)){
                    tile_views.remove(player_moves.indexOf(tile_num));
                    player_moves.remove(Integer.valueOf(tile_num));
                    view.setBackgroundColor(getResources().getColor(R.color.red));
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Tile cannot be picked", Toast.LENGTH_SHORT).show();
            }
        }
        else if(game.current_round.currentPlayer == game.current_round.player2 && game.current_round.player1.get_board_state()[tile_num-1]){
            if(player_moves.isEmpty()){
                to_cover = false;
            }
            Log.d("FIRSTTURN", String.valueOf(game.current_round.first_turn_of_round));
            if(!to_cover && !game.current_round.first_turn_of_round){
                if(!player_moves.contains(tile_num)){
                    player_moves.add(tile_num);
                    tile_views.add(view);
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                }
                else if(((ColorDrawable)view.getBackground()).getColor() == getResources().getColor(R.color.white)){
                    tile_views.remove(player_moves.indexOf(tile_num));
                    player_moves.remove(Integer.valueOf(tile_num));
                    view.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Tile cannot be picked", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Tile cannot be picked", Toast.LENGTH_SHORT).show();
        }
    }

    /* *********************************************************************
    Function Name: player2_tile_click
    Purpose: To simulate the clicking of a tile in player2's side.
    Parameters:
            view, a View object which consists of the view that was clicked by the
                    player on the screen
    Return Value:
    Algorithm:
         1) Get the tile_num by parsing the id received from getTag() call.
         2) Check if player1 clicked on it, if yes, check if the tile is
            uncovered. If player2 clicked on it, check if tile is covered.
         3) For player1, set to_cover to true if player_moves is empty.
            For player2, set to_cover to false if player_moves is empty.
         4) Check if to_cover is true for player1, and false for player2.
            If yes, check if player_moves already has that tile.
         5) If no, add the tile in player_moves and set background to white.
         6) If yes, check if background is white. If yes, then check whether
            to make it red or green.
    Assistance Received: none
    ********************************************************************* */
    void player2_tile_click(View view){
        int tile_num = Integer.parseInt(view.getTag().toString());
        if(game.current_round.currentPlayer == game.current_round.player2 && !game.current_round.player2.get_board_state()[tile_num-1]){
            if(player_moves.isEmpty()){
                to_cover = true;
            }
            if( to_cover){
                if (!player_moves.contains(tile_num)){
                    player_moves.add(tile_num);
                    tile_views.add(view);
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                }
                else if(((ColorDrawable)view.getBackground()).getColor() == getResources().getColor(R.color.white)){
                    tile_views.remove(player_moves.indexOf(tile_num));
                    player_moves.remove(Integer.valueOf(tile_num));
                    view.setBackgroundColor(getResources().getColor(R.color.red));
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Tile cannot be picked", Toast.LENGTH_SHORT).show();
            }
        }
        else if(game.current_round.currentPlayer == game.current_round.player1 && game.current_round.player2.get_board_state()[tile_num-1]){
            if(player_moves.isEmpty()){
                to_cover = false;
            }
            if(!to_cover && !game.current_round.first_turn_of_round){
                if(!player_moves.contains(tile_num)){
                    player_moves.add(tile_num);
                    tile_views.add(view);
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                }
                else if(((ColorDrawable)view.getBackground()).getColor() == getResources().getColor(R.color.white)){
                    tile_views.remove(player_moves.indexOf(tile_num));
                    player_moves.remove(Integer.valueOf(tile_num));
                    view.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Tile cannot be picked", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Tile cannot be picked", Toast.LENGTH_SHORT).show();
        }
    }

    /* *********************************************************************
    Function Name: change_dice_pics
    Purpose: To change the dice pics in the game according to the dices
                rolled by a player.
    Parameters:
            dices_rolled, a List of Integers that holds the dice values
                rolled by the current player.
    Return Value:
    Algorithm:
         1) Iterate the list and change the dice image resource according
            to the dice number in the list.
    Assistance Received: none
    ********************************************************************* */
    void change_dice_pics(List<Integer> dices_rolled){
        List<Integer> dice_img_res = new ArrayList<>();

        for(int i = 0; i < dices_rolled.size(); i++){
            switch(dices_rolled.get(i)){
                case 0:
                    dice_img_res.add(R.drawable.zero);
                    break;
                case 1:
                    dice_img_res.add(R.drawable.one);
                    break;
                case 2:
                    dice_img_res.add(R.drawable.two);
                    break;
                case 3:
                    dice_img_res.add(R.drawable.three);
                    break;
                case 4:
                    dice_img_res.add(R.drawable.four);
                    break;
                case 5:
                    dice_img_res.add(R.drawable.five);
                    break;
                case 6:
                    dice_img_res.add(R.drawable.six);
                    break;
            }
        }
        if(dices_rolled.size()<=1 || dices_rolled.get(0).equals(0)){
            dice_img_res.add(R.drawable.zero);
        }

        dice_one_img.setImageResource(dice_img_res.get(0));
        dice_two_img.setImageResource(dice_img_res.get(1));
    }

    /* *********************************************************************
    Function Name: display_info
    Purpose: To display passed game information in a dialog box to the user.
    Parameters:
            dialog_message, the message to be printed on the dialog box
                It is a String variable.
            dialog_title, the title to be printed on top of the dialog
                box. It is also a String variable.
    Return Value:
    Algorithm:
         1) call the functions from the builder variable to display
            the dialog box.
         2) Instantiate an AlertBox object with the builder.create()
            function.
         3) Call the .show() function to show the dialog box.
    Assistance Received: none
    ********************************************************************* */
    void display_info(String dialog_message, String dialog_title){
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(dialog_message)
                .setTitle(dialog_title)
                .setCancelable(true)
                .setPositiveButton("", (dialog, id) -> {
                });

        //Creating dialog box
        LayoutInflater inflater= LayoutInflater.from(getApplicationContext());
        View dialogBoxView=inflater.inflate(R.layout.load_game_dialog_box, null);
        AlertDialog alert = builder.create();
        alert.setView(dialogBoxView);
        alert.show();
        alert.getWindow().setLayout(1000, 800);
    }
}