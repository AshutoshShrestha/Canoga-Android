package com.example.canoga;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Player {
    int score, previous_round_score, advantage_square;
    boolean first_turn;
    boolean [] board_state;
    String name;
    String latest_move;
    List<View> tile_views;

    /* *********************************************************************
    Function Name: Player
    Purpose: To construct a Player object
    Parameters: none
    Return Value: a Player object
    Algorithm:
                1) Set the default values of the member variabes
    Assistance Received: none
    ********************************************************************* */
    Player() {
        this.score = 0;
        this.first_turn = false;
        this.advantage_square = 0;
    }

    /* *********************************************************************
    Function Name: Player
    Purpose: To construct a Player object
    Parameters: none
    Return Value: a Player object
    Algorithm:
                1) Set the default values of the member variabes
    Assistance Received: none
    ********************************************************************* */
    void set_board(int squares, boolean[] state){
        this.board_state = new boolean[squares];
        System.arraycopy(state, 0, this.board_state, 0, squares);
    }
    // virtual functions
    boolean roll_one() { return false; }

    // getters
    /* *********************************************************************
    Function Name: get_name
    Purpose: To return a player's name
    Parameters: none
    Return Value: String variable representing a player's name
    Algorithm:
                1) Return a default value.
    Assistance Received: none
    ********************************************************************* */
    String get_name(){
        return "Player";
    }
    /* *********************************************************************
    Function Name: get_score
    Purpose: To return a player's score.
    Parameters: none
    Return Value: an int holding a player's score.
    Algorithm:
                1) Return the player's score.
    Assistance Received: none
    ********************************************************************* */
    int get_score() { return this.score; }
    /* *********************************************************************
    Function Name: get_previous_round_score
    Purpose: To return a player's previous round score.
    Parameters: none
    Return Value: An int that holds a player's previous round score.
    Algorithm:
                1) Return the previous_round_score member variable.
    Assistance Received: none
    ********************************************************************* */
    int get_previous_round_score() { return this.previous_round_score; }
    /* *********************************************************************
    Function Name: get_advantage_square
    Purpose: To return a player's advantage square.
    Parameters: none
    Return Value: An int that holds a player's advantage square, if any
                    or else 0.
    Algorithm:
                1) Return a player's advantage square.
    Assistance Received: none
    ********************************************************************* */
    int get_advantage_square(){ return this.advantage_square;}

    /* *********************************************************************
    Function Name: get_covered_squares
    Purpose: To return a player's covered squares.
    Parameters: none
    Return Value: A List of integers that holds a player's covered squares.
    Algorithm:
                1) Iterate through a player's board
                2) If True is stored in a position, then push it into the vector
                to be returned.
    Assistance Received: none
    ********************************************************************* */
    List<Integer> get_covered_squares(){
        List<Integer> covered_square = new ArrayList<>();
        for (int i = 0; i < this.board_state.length; i++) {
            if (this.board_state[i]) {
                covered_square.add(i + 1);
            }
        }
        return covered_square;
    }

    /* *********************************************************************
    Function Name: get_uncovered_squares
    Purpose: To return a player's uncovered squares.
    Parameters: none
    Return Value: A List of integers that holds a player's uncovered squares.
    Algorithm:
                1) Iterate through a player's board
                2) If False is stored in a position, then push it into the vector
                to be returned.
    Assistance Received: none
    ********************************************************************* */
    List<Integer> get_uncovered_squares(){
        List<Integer> uncovered_square = new ArrayList<>();
        for (int i = 0; i < this.board_state.length; i++) {
            if (!this.board_state[i]) {
                uncovered_square.add(i + 1);
            }
        }
        return uncovered_square;
    }

    /* *********************************************************************
    Function Name: get_latest_move
    Purpose: To return a player's latest move.
    Parameters: none
    Return Value: A string that holds the player's latest move
    Algorithm:
                1) Return the latest move.
    Assistance Received: none
    ********************************************************************* */
    String get_latest_move(){
        return this.latest_move;
    }

    /* *********************************************************************
    Function Name: had_first_turn
    Purpose: To check whether a player had the first turn of the round or not
    Parameters: none
    Return Value: boolean variable. True if player had played the first turn.
                                    False if player had not played first turn.
    Algorithm:
                1) Return the latest move.
    Assistance Received: none
    ********************************************************************* */
    boolean had_first_turn() { return this.first_turn; }

    /* *********************************************************************
    Function Name: get_board_state
    Purpose: To return a player's board state.
    Parameters:
    Return Value: a boolean array that represents a player's board
    Algorithm:
                1) Return the board_state member variable.
    Assistance Received: none
    ********************************************************************* */
    boolean [] get_board_state() { return this.board_state; }

    /* *********************************************************************
    Function Name: get_tile_views
    Purpose: To return tile views for a player's tile buttons
    Parameters:
    Return Value: List of Views that hold a player's tile button views.
    Algorithm:
                1) Return the tile_views member variable.
    Assistance Received: none
    ********************************************************************* */
    List<View> get_tile_views(){ return this.tile_views; }

    /* *********************************************************************
    Function Name: cover
    Purpose: To cover an uncovered square
    Parameters:
                sq, an integer passed by value. It holds the number of
                squares in a row the player chose to play with.
    Return Value: none (void)
    Algorithm:
                1) Set the value from false to true in a given board square position.
    Assistance Received: none
    ********************************************************************* */
    void cover(int square){
        if(!this.board_state[square-1]){
            this.board_state[square-1] = true;
        }
    }

    /* *********************************************************************
    Function Name: uncover
    Purpose: To uncover a covered square
    Parameters:
                sq, an integer passed by value. It holds the number of
                squares in a row the player chose to play with.
    Return Value: none (void)
    Algorithm:
                1) Set the value from true to false in a given board square position.
    Assistance Received: none
    ********************************************************************* */
    void uncover(int square) {
        if (this.board_state[square - 1]) {
            this.board_state[square - 1] = false;
        }
    }

    // setters
    /* *********************************************************************
    Function Name: set_score
    Purpose: To set a player's score
    Parameters:
                score, an integer passed by value. It holds the score that
                are to be set as a player's score.
    Return Value:
    Algorithm:
                1) Check if points is a positive number or 0.
    Assistance Received: none
    ********************************************************************* */
    void set_score(int score){
        if(score>0){
            this.score = score;
        }
    }

    /* *********************************************************************
    Function Name: set_previous_round_score
    Purpose: To set a player's score from the previous round
    Parameters:
                score, an integer variable passed by value. It holds the
                score that is to be set as the score obtained by a player
                in the previous round.
    Return Value:
    Algorithm:
                1) Check if the score is a positive number or 0.
    Assistance Received: none
    ********************************************************************* */
    void set_previous_round_score(int score){
        if (score >= 0) {
            this.previous_round_score = score;
        }
    }

    /* *********************************************************************
    Function Name: reset_latest_move
    Purpose: To reset latest_move back to empty string
    Parameters:
    Return Value:
    Algorithm:
                1) Set latest_move as "".
    Assistance Received: none
    ********************************************************************* */
    void reset_latest_move(){
        this.latest_move = "";
    }

    /* *********************************************************************
    Function Name: set_tile_views
    Purpose: To set tile views associated with each player's tile buttons
    Parameters: tile_views, a List of Views containing all tile Views for
                    all tile buttons of a player
    Return Value:
    Algorithm:
                1) Set the passed parameter to tile_views member variable.
    Assistance Received: none
    ********************************************************************* */
    void set_tile_views(List<View> tile_views){
        this.tile_views = tile_views;
    }

    /* *********************************************************************
    Function Name: set_had_first_turn
    Purpose: To set a member variable that denotes whether the player played
             the first turn or not.
    Parameters:
                first, a boolean variable passed by value. It holds true if
                the player had played the first turn and vice versa.
    Return Value:
    Algorithm:
                1) Check if the value of first is null or not.
    Assistance Received: none
    ********************************************************************* */
    void set_had_first_turn(boolean first){ this.first_turn = first; }

    /* *********************************************************************
    Function Name: set_advantage_square
    Purpose: To set the advantage square for a decided player
    Parameters:
                square, an integer variable passed by value. It holds the
                square that is to be set as the advantage square for a
                decided player.
    Return Value:
    Algorithm:
                1) Check if the value of square is less than or equal to 0 or not.
    Assistance Received: none
    ********************************************************************* */
    void set_advantage_square(int square){
        if (square > 0) {
            advantage_square = square;
        }
    }
    /* *********************************************************************
    Function Name: set_latest_move
    Purpose: To set the latest move
    Parameters:
                latest_move, a string variable containing a description
                    of a player's latest move
    Return Value: none (void)
    Algorithm:
                1) Assign the parameter to latest_move member variable.
    Assistance Received: none
    ********************************************************************* */
    void set_latest_move(String latest_move) {
        this.latest_move = latest_move;
    }
}
