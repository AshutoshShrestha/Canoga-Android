package com.example.canoga;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Round {
    String winner;
    Player nextPlayer, currentPlayer;
    Player player1, player2;
    int squares, winning_score;
    GameLog log_record;
    GameUtils game_utils;
    boolean first_turn_of_round;
    // loaded dice rolls
    List<List<Integer>> l_dice_rolls;

    /* *********************************************************************
    Function Name: Round
    Purpose: To construct a Round object
    Parameters:
                player1_name, player2_name, string variables that store
                    player names of the players playing in the round.
                single_p, boolean variable to store whether game is singlePlayer
                    or not
                tiles, int variable that indicates the number of squares the
                    user chose to play with
                l_dice, List of loaded dice rolls from a file, if any.

    Return Value: a Player object
    Algorithm:
                1) Set the passed variables to respective member variables.
                2) Check value of singleP and initialize players accordingly.
                3) Set the default wining score as 0.
                4) Instantiate game_log object.
                5) Instantiate game_utils object.
    Assistance Received: none
    ********************************************************************* */
    public Round(String player1_name, String player2_name, boolean single_p, int tiles, List<List<Integer>> l_dice) {
        if(single_p){
            if(player1_name.equals("Computer")){
                this.player2 = new Human(player2_name);
                this.player1 = new Computer(this.player2, false);
            }
            else{
                this.player1 = new Human(player1_name);
                this.player2 = new Computer(this.player1, false);
            }
        }
        else{
            this.player1 = new Human(player1_name);
            this.player2 = new Human(player2_name);
        }
        this.winning_score = 0;
        this.squares = tiles;
        this.log_record = new GameLog();
        this.game_utils = new GameUtils();
        this.first_turn_of_round = true;
        this.l_dice_rolls = l_dice;
    }

    /* *********************************************************************
    Function Name: switch_turns
    Purpose: To switch the player's turns.
            Parameters:
            Return Value:
            Algorithm:
                        1) Set the currentPlayer as the nextPlayer.
                        2) Set the nextPlayer as the currentPlayer.
                        3) Change the respective relevant widgets.
    Assistance Received: none
    ********************************************************************* */
    void switch_turns(){
        Player temp = this.currentPlayer;
        this.currentPlayer = this.nextPlayer;
        this.nextPlayer = temp;
        this.first_turn_of_round = false;
    }

    /* *********************************************************************
    Function Name: decide_first_player
    Purpose: To decide player turns at the start of the round.
            Parameters:
            Return Value: boolean, true if first turn was decided
                            false if first turn wasn't decided
            Algorithm:
                        1) Check if there was a previous round from player methods
                        and set previous_winner_score.
                        2) Check which player had the first turn last round and
                        provide advantage to the other player.
                        3) Roll dices for both players and compare the sum of dices.
                        If both are equal return false, else set the first turn
                        as the player that had the greater dice sum value and return
                        true.
    Assistance Received: none
    ********************************************************************* */
    boolean decide_first_player(){
        String log_msg = "";
        int previous_winner_score = this.player1.get_previous_round_score() != 0 ?
                this.player1.get_previous_round_score() : this.player2.get_previous_round_score();

        // had_first_turn will only be true for any player only at the end of this
        // function and this function is called only on the first turn. So this if else
        // block will never be executed on the first turn of the first round
        if (this.player1.had_first_turn() && player2.advantage_square == 0) {
            int adv_sq = give_advantage(this.player2, previous_winner_score, this.squares);
            player2.set_advantage_square(adv_sq);
            player1.set_advantage_square(0);
        }
        else if (this.player2.had_first_turn() && player1.advantage_square == 0) {
            int adv_sq = give_advantage(this.player1, previous_winner_score, this.squares);
            player1.set_advantage_square(adv_sq);
            player2.set_advantage_square(0);
        }
        // int variables to hold the sum of dice values rolled by the two players
        int player1Total, player2Total;
        List<Integer> player1_dice_val;
        List<Integer> player2_dice_val;

        player1_dice_val = this.roll_dice(false);
        log_msg += this.player1.get_name()+ " rolled " + player1_dice_val.toString() + '\n';
        player1Total = player1_dice_val.get(0) + player1_dice_val.get(1);
        player2_dice_val = this.roll_dice(false);
        log_msg += this.player2.get_name()+ " rolled " + player2_dice_val.toString() + '\n';
        player2Total = player2_dice_val.get(0) + player2_dice_val.get(1);

        if (player1Total == player2Total) {
            log_msg += "Press BEGIN again.";
            this.log_record.add_log(log_msg);
            return false;
        }
        else if (player1Total > player2Total) {
            this.currentPlayer = this.player1;
            this.nextPlayer = this.player2;
            this.player1.set_had_first_turn(true);
            this.player2.set_had_first_turn(false);
            log_msg += this.currentPlayer.get_name() + " goes first.";
        }
        else {
            currentPlayer = player2;
            nextPlayer = player1;
            this.player1.set_had_first_turn(false);
            this.player2.set_had_first_turn(true);
            log_msg += this.currentPlayer.get_name() + " goes first.";
        }
        this.log_record.add_log(log_msg);
        return true;
    }

    /* *********************************************************************
    Function Name: roll_dice
    Purpose: To simulate rolling a dice
            Parameters: rollOne, boolean variable that indicates whether
                            only a single die should be rolled or not
            Return Value: List of integers that represent dice values
            Algorithm:
                        1) Check if there are any loaded dice rolls available.
                        If yes, return the first value after deleting it
                        from the list.
                        If no, return an int list of random ints.
    Assistance Received: none
    ********************************************************************* */
    public List<Integer> roll_dice(boolean rollOne) {
        // a vector of ints to hold the random dice values that will be assigned to a player
        List<Integer> dice_values = new ArrayList<>();
        if(this.l_dice_rolls == null){
            this.l_dice_rolls = new ArrayList<>();
        }
        if (!this.l_dice_rolls.isEmpty()) {
            dice_values = this.l_dice_rolls.get(0);
            this.l_dice_rolls.remove(0);
        }
        else{
            Random rand = new Random(); //instance of random class
            int upper_bound = 6;
            if (rollOne) {
                dice_values.add(rand.nextInt(upper_bound) + 1);
                return dice_values;
            }
            dice_values.add(rand.nextInt(upper_bound) + 1);
            dice_values.add(rand.nextInt(upper_bound) + 1);
        }
        return dice_values;
    }

    /* *********************************************************************
    Function Name: give_advantage
    Purpose: To calculate which square should be covered to provide advantage
    # to the decided player, and then cover it.
    Parameters:
                player, a Player class variable. It holds the info of a player who has
                   to be given the advantage.
            Return Value:
            Algorithm:
                 1) Add every digit of score. If the digit is equal or more than the
                     number of squares, then repeat the process until it passes that condition.
                 2) The result should be passsed into the player's cover
                     function to cover that square.
    Assistance Received: none
    ********************************************************************* */
    int give_advantage(Player player, int score, int squares){
        int q = score, r;
        int sum = 0;
        while (q > 0) {
            r = q % 10;
            q = q / 10;
            sum += r;
            if (q <= 0 && sum > squares) {
                q = sum + score;
                sum = 0;
                if (q < 0) {
                    break;
                }
            }
        }
        if (sum > 0 && sum <= squares) {
            this.log_record.add_log(player.get_name() + " was given the advantage.\nThe square " + sum + " will be covered beforehand.");
//            player.cover(sum);
        }
        else {
            this.log_record.add_log("Advantange could not be given.");
            sum = 0;
        }
        Log.d("ADVANTAGELOG", this.log_record.get_latest_log());
        return sum;
    }

    /* *********************************************************************
    Function Name: check_roll_one
    Purpose: To check if a player is allowed to roll one die.
    Parameters: None
    Return Value: Boolean, true if player can roll one die.
                            false if player is not allowed to roll one die.
            Algorithm:
                 1) Call roll_one_availble function from game_utils object.
    Assistance Received: none
    ********************************************************************* */
    boolean check_roll_one(){
        return game_utils.roll_one_available(this.currentPlayer.get_uncovered_squares());
    }

    /* *********************************************************************
    Function Name: choose_computer_move
    Purpose: To choose a computer player move and return it
    Parameters: totalVal, int variable that holds the total sum of dice
                        values rolled by a player
    Return Value: List of integers that hold a computer chosen move.
            Algorithm:
                 1) Set the advantage square if it has been set.
                 2) Initialize two lists: cover_options and uncover options.
                 3) cover_options is a value returned from the all_available_cover_moves
                 function from game_utils
                 and uncover_options is a value returned form the all_available_uncover_moves
                 function from game_utils
                 4) Initialize a new temp Computer player and call the Computer player's
                 choose_a_move function and set it to move variable.
                 5) Set the latest move from temp player to current player.
                 6) Return move.
    Assistance Received: none
    ********************************************************************* */
    List<Integer> choose_computer_move(int totalVal){
        int advantage_sq = this.currentPlayer.get_advantage_square() != 0?
                this.currentPlayer.get_advantage_square():
                this.nextPlayer.get_advantage_square();
        List<List <Integer>> cover_options = this.game_utils.all_available_cover_moves(this.game_utils.all_possible_moves(totalVal), this.currentPlayer.get_uncovered_squares());
        List<List <Integer>> uncover_options = this.game_utils.all_available_uncover_moves(this.game_utils.all_possible_moves(totalVal), this.nextPlayer.get_covered_squares(), advantage_sq);

        Computer tempPlayer = new Computer(this.nextPlayer, false);
        tempPlayer.set_board(this.squares, this.currentPlayer.get_board_state());
        List<Integer> moves = tempPlayer.choose_a_move(cover_options, uncover_options);
        this.currentPlayer.set_latest_move(tempPlayer.latest_move);
        return moves;
    }

    /* *********************************************************************
    Function Name: choose_computer_move
    Purpose: To choose a computer player move and return it
    Parameters: totalVal, int variable that holds the total sum of dice
                        values rolled by a player
    Return Value: List of integers that hold a computer chosen move.
            Algorithm:
                 1) Set the advantage square if it has been set.
                 2) Initialize two lists: cover_options and uncover options.
                 3) cover_options is a value returned from the all_available_cover_moves
                 function from game_utils
                 and uncover_options is a value returned form the all_available_uncover_moves
                 function from game_utils
                 4) Initialize a new temp Computer player and call the Computer player's
                 choose_a_move function and set it to move variable.
                 5) Set the latest move from temp player to current player.
                 6) Return move.
    Assistance Received: none
    ********************************************************************* */
    boolean check_movability(int totalDiceVal){
        List<Integer> current_player_uncovered_sq = this.currentPlayer.get_uncovered_squares();
        List<Integer> next_player_covered_sq = this.nextPlayer.get_covered_squares();
        List<List<Integer>> all_possible_moves = game_utils.all_possible_moves(totalDiceVal);

        Log.d("ALL MOVES", all_possible_moves.toString());
        List<List<Integer>> available_cover_moves = game_utils.all_available_cover_moves(all_possible_moves, current_player_uncovered_sq);
        List<List<Integer>> available_uncover_moves = game_utils.all_available_cover_moves(all_possible_moves, next_player_covered_sq);

        Log.d("Can cover", current_player_uncovered_sq.toString());
        Log.d("Can uncover", next_player_covered_sq.toString());

        Log.d("COVER MOVES", available_cover_moves.toString());
        Log.d("UNCOVER MOVES", available_uncover_moves.toString());
        return available_cover_moves.size() != 0 || available_uncover_moves.size() != 0;
    }

    /* *********************************************************************
    Function Name: save_game
    Purpose: To save the player state and game state
    Parameters:
                file_name, String variable to hold the name of the file
                    entered by the user.
                context, application context to access directory files.
    Return Value: int variable, -1 if game directory couldn't be created
                                0 if game saved successfully
                                1 if game name already exists and wasn't saved
    Algorithm:
                1) Get all required player and game states.
                   (players board state, players names, next turn, first turn, and
                   loaded dice rolls, if any)
                2) Write them to a text file.
    Assistance Received: none
    ********************************************************************* */
    @RequiresApi(api = Build.VERSION_CODES.R)
    int save_game(String file_name, Context context){
        File dir= new File(context.getFilesDir() + "/Canoga");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d("DIR_ERR", "Cannot create directory.");
                return -1;
            }
            Log.d("DIR_CREATED", "Canoga directory created.");
        }
        File file = new File(dir, file_name + ".txt");

        if(file.exists()){
            return 1;
        }
        Log.d("FILE PATH", file.getPath());
        boolean[] player1_board = this.player1.get_board_state(), player2_board = this.player2.get_board_state();

        String player1_name = player1.get_name(), player2_name = player2.get_name();
        int player1_score = player1.get_score(), player2_score = player2.get_score();

        String first_turn = "";
        if(player1.had_first_turn()) {
            first_turn = player1.get_name();
        }
        else if(player2.had_first_turn()){
            first_turn = player2.get_name();
        }
        String next_turn = "";
        if(!(currentPlayer == null)){
            next_turn = currentPlayer.get_name();
        }
        StringBuilder game_data = new StringBuilder();
        game_data.append(player1_name).append(":").append("\n");
        game_data.append("    Squares: ");
        for (int i = 0; i < squares; i++) {
            game_data.append(!player1_board[i] ? (i + 1) + " " : "* ");
        }
        game_data.deleteCharAt(game_data.length()-1);
        game_data.append("\n");
        game_data.append("    Score: ");
        game_data.append(player1_score).append("\n\n");
        game_data.append(player2_name).append(":").append("\n");
        game_data.append("    Squares: ");
        for (int i = 0; i < squares; i++) {
            game_data.append(!player2_board[i] ? (i + 1) + " " : "* ");
        }
        game_data.deleteCharAt(game_data.length()-1);
        game_data.append("\n");
        game_data.append("    Score: ");
        game_data.append(player2_score).append("\n\n");
        game_data.append(first_turn_of_round ? "First  turn: " : "First turn: ");
        game_data.append(first_turn).append("\n");
        game_data.append("Next turn: ").append(next_turn).append("\n\n");

        if (!l_dice_rolls.isEmpty()) {
            game_data.append("Dice: \n");
            for (List<Integer> dice_roll: l_dice_rolls) {
                game_data.append("   ");
                game_data.append(dice_roll.toString()).append("\n");
            }
        }
        writeTextData(file, game_data.toString());
        return 0;
    }

    /* *********************************************************************
    Function Name: writeTextData
    Purpose: To write string data to a file
    Parameters: file, File variable that store the file where the game should
                    saved.
                data, String variable that stores the data to be written to
                    the file.
    Return Value: none
    Algorithm:
                1) Write the provided string to the provided file and check for any
                    exceptions.
    Assistance Received: none
    ********************************************************************* */
    private void writeTextData(File file, String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* *********************************************************************
    Function Name: player_won
    Purpose: To check if a player has won the game after they make a move.
    Parameters: None
    Return Value: true if player has won, false if the player hasn't won
    Algorithm:
                1) Get all covered squares from the current player and all uncovered
                squares from the next player.
                2) If the size of current player's covered squares is equal to the total
                number of squares i.e. all squares are covered, then the currentplayer
                has won.
                3) If the size of next player's uncovered squares is equal to the total
                number of seuares i.e. all squares are uncovered, unless it is the first
                turn of the round, the current player has won.
    Assistance Received: none
    ********************************************************************* */
    boolean player_won(){
        List<Integer> currentPlayer_covered_squares = this.currentPlayer.get_covered_squares();
        List<Integer> nextPlayer_uncovered_squares = this.nextPlayer.get_uncovered_squares();
        if (currentPlayer_covered_squares.size() == this.squares) {
            this.winner = currentPlayer.get_name();
            for (int i : nextPlayer_uncovered_squares) {
                this.winning_score += i;
            }
            return true;
        }

        // if it is the first turn, do not check uncovered squares
        if (!this.first_turn_of_round) {
            if (nextPlayer_uncovered_squares.size() == squares) {
                this.winner = currentPlayer.get_name();
                for (int i : currentPlayer_covered_squares) {
                    this.winning_score += i;
                }
                return true;
            }
        }

        return false;
    }

    /* *********************************************************************
    Function Name: provide_help
    Purpose: To provide suggestions to the user when making a move.
    Parameters:
                squares, an integer passed by value. It holds the total number of
                squares in a row the player chose to play with.
                value, an integer passed by value. It holds the sum of dice values
                rolled by the player.
                moving_possibility, an integer passed by value. It holds integers
                ranging from 1-3 which denotes whether the player has the option to
                cover or uncover or do either.
    Return Value: List of integers that contains the computer suggested move
    Algorithm:
                1) Instantiate helper, a pointer variable of the type Computer.
                2) Initialize the board.
                3) Choose the best move and return.
    ********************************************************************* */
    List<Integer> provide_help(int value){
        Computer helper = new Computer(this.nextPlayer, true);
        // the board should be initialized with the same board state as the human.
        helper.set_board(this.squares, this.currentPlayer.get_board_state());

        List<List <Integer>> cover_options = this.game_utils.all_available_cover_moves(this.game_utils.all_possible_moves(value), this.currentPlayer.get_uncovered_squares());
        List<List <Integer>> uncover_options = this.game_utils.all_available_uncover_moves(this.game_utils.all_possible_moves(value), this.nextPlayer.get_covered_squares(), this.nextPlayer.get_advantage_square());
        List<Integer> move = helper.choose_a_move(cover_options, uncover_options);
        this.log_record.add_log(helper.get_latest_move());
        return move;
    }

    // mutators
    /* *********************************************************************
    Function Name: set_player_turns
    Purpose: To set first parameter as currentPlayer and second parameter
                as nextPlayer
    Parameters:
                curr_p, a Player var. It stores the player that should
                play the current turn.
                next_p,a Player var. It stores the player that should
                play the next turn.
    Return Value: none (void)
    Algorithm:
                1) Assign the players and squares to this round's corresponding
                member variables.
    Assistance Received: none
    ********************************************************************* */
    void set_player_turns(Player curr_p, Player next_p){
        if (curr_p == null || next_p == null) {
            return;
        }
        this.currentPlayer = curr_p;
        this.nextPlayer = next_p;
    }

    /* *********************************************************************
    Function Name: set_l_dice_rolls
    Purpose: To assign the loaded dice rolls (if any) from a file
    Parameters:
                dice_rolls, a List of integers passed by value.
                It holds a series of dice rolls loaded from a saved serializer file.
    Return Value: none(void)
    Algorithm:
                1) Assign passed dice rolls into one of the round object's members.
    Assistance Received: none
    ********************************************************************* */
    void set_l_dice_rolls(List<List<Integer>> dice_rolls){
        this.l_dice_rolls = dice_rolls;
    }

    /* *********************************************************************
    Function Name: end_round
    Purpose: To set the round winner and the round winning score.
    Parameters: none
    Return Value:
    Algorithm:
                1) Add winning score into the winner's total score.
                2) Set the winning score as the winner's previous round score
                   and set 0 as the other player's previous round score.
    Assistance Received: none
    ********************************************************************* */
    void end_round() {
        if (this.winner.equals(this.player1.get_name())) {
            this.player1.set_score(this.player1.get_score() + this.winning_score);
            this.player1.set_previous_round_score(this.winning_score);
            this.player2.set_previous_round_score(0);
        } else {
            this.player2.set_score(this.player2.get_score() + this.winning_score);
            this.player2.set_previous_round_score(this.winning_score);
            this.player1.set_previous_round_score(0);
        }
    }
}
