package com.example.canoga;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class Computer extends Player{
    boolean helper;
    Player opponent;

    /* *********************************************************************
    Function Name: Computer
    Purpose: To construct a helper computer object
    Parameters:
                is_helper, a boolean variable passed by value. It holds true
                    if the object to be constructed should be a helper and vice
                    versa.
                opp_player, a Player variable that holds the Human player
                    who is playing as the opponent.
    Return Value: a helper Computer object
    Algorithm:
                1) Set the member variables from the passed parameters.
                2) Set previous round scores as 0 and latest_move as empty string.
    Assistance Received: none
    ********************************************************************* */
    Computer(Player opp_player, boolean is_helper) {
        this.helper = is_helper;
        this.opponent = opp_player;
        set_previous_round_score(0);
        this.latest_move = "";
    }

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
        return "Computer";
    }

    /* *********************************************************************
    Function Name: choose_a_move
    Purpose: To find and return the best move from all available moves.
    Parameters:
                cover_options, uncover_options, List of List of Integers that
                    hold all available cover and uncover options for a player
    Return Value: A List of integers that holds the combination of integers
                  that the computer wants to cover or uncover.
    Algorithm:
                1) If the player can both cover or uncover, check if it is better to
                cover or uncover, and find then the best move.
                2) If the player can only do one of both, find the best move.
                3) Return the best move as a player's moves.
    Assistance Received: none
    ********************************************************************* */
    List<Integer> choose_a_move(List<List<Integer>> cover_options, List<List<Integer>> uncover_options){
        List<Integer> player_moves = new ArrayList<>();
        if(!cover_options.isEmpty() && !uncover_options.isEmpty()) {
            // find best possible move from available cover and available uncover moves
            if (cover_or_uncover()){
                player_moves = find_best_move(cover_options, true);
            }
            else{
                player_moves = find_best_move(uncover_options, false);
            }
        }
        else if(!cover_options.isEmpty() && uncover_options.isEmpty()){
            // find best possible move from available cover moves
            this.latest_move += "Because there are no available moves to uncover, ";
            player_moves = find_best_move(cover_options, true);

        }
        else if(cover_options.isEmpty() && !uncover_options.isEmpty()){
            // find best possible move from available uncover moves
            this.latest_move += "Because there are no available moves to cover, ";
            player_moves = find_best_move(uncover_options, false);
        }
        Log.d("TAG_MOVE", player_moves.toString());
        return player_moves;
    }

    /* *********************************************************************
    Function Name: find_best_move
    Purpose: To find and return the best move from all available moves.
    Parameters:
                moves, a list of list of integers. It holds all moves out of
                    which one best move must be selected
                to_cover, a boolean value. It holds true if the player has decided to cover,
                    and false if the player has decided to uncover
    Return Value: A list of integer that contains the combination of best possible moves
                    for a player.
    Algorithm:
                1) Iterate through the moves vector to find the vector with the largest
                number in it.
                2) If two vectors in the move vector has the same largest number, then
                pick the one with the smaller size, since the vector with the smaller
                size will have the larger numbers than the vector with bigger size.
                3) If this instance of the Computer class is a helper, then use the
                appropriate words a helper would use. And likewise if this instance is a
                Computer player.
    Assistance Received: none
    ********************************************************************* */
    List<Integer> find_best_move(List<List<Integer>> moves, boolean to_cover){
        List<Integer> best_move;
        // an int variable to hold the maximum numbered square from all combinations
        int max_square = 1;
        best_move = moves.get(0);
        for( List<Integer> move : moves){
            for(int tile: move){
                if(tile > max_square){
                    max_square = tile;
                    best_move = move;
                }
                else if(tile == max_square){
                    if (move.size() < best_move.size()){
                        best_move = move;
                    }
                }
            }
        }
        StringBuilder move_description = new StringBuilder();

        move_description.append(this.helper ? "you should probably " : this.get_name() + " decided to ");
        move_description.append(to_cover ? "cover " : "uncover ");

        move_description.append("( ");
        for(int tile: best_move){
            move_description.append(String.valueOf(tile)).append(" ");
        }
        move_description.append(") ");


        if(to_cover){
            best_move.add(0, new Integer(0));
        }
        else{
            best_move.add(0, new Integer(-1));
        }

        if (moves.size() == 1){
            move_description.append("because there is no other options for ");
            if (to_cover){
                move_description.append("covering.");
            }
            else {
                move_description.append("uncovering.");
            }
        }
        else{
            if(to_cover){
                move_description.append("because covering ");
            }
            else{
                move_description.append("because uncovering ");
            }
            move_description.append("the largest possible values maximizes winning score. ");
        }

        this.latest_move += move_description;
        return best_move;
    }

    /* *********************************************************************
    Function Name: cover_or_uncover
    Purpose: To figure out whether to cover your own square of uncover an
             opponent's squares in order to win and maximize points.
    Parameters: none
    Return Value: A boolean value which is true if the player decides to cover
                 and is false if the player decides to uncover.
    Algorithm:
                1) Get player's covered squares and opponent's uncovered
                    squares.
                2) Add the covered tiles and uncovered tiles.
                3) If the sum of covered tiles is greater, then go for
                    covering, else go for uncovering.
    Assistance Received: none
    ********************************************************************* */
    boolean cover_or_uncover() {
        List<Integer> self_covered_sq = this.get_covered_squares();
        List<Integer> opp_uncovered_sq = this.opponent.get_uncovered_squares();

        int cov_sum = 0, uncov_sum = 0;
        for(int sq: self_covered_sq){ cov_sum += sq; }
        for(int sq: opp_uncovered_sq){ uncov_sum += sq; }
        this.latest_move += "Since it is more likely to win the game by ";
        if(cov_sum > uncov_sum){
            this.latest_move += "covering, ";
            return true;
        }
        else{
            this.latest_move += "uncovering, ";
            return false;
        }
    }

    /* *********************************************************************
    Function Name: roll_one
    Purpose: To decide whether it is good to roll one die or two dice
    Parameters: none
    Return Value: A boolean value that is true if the player decides to roll one
                  dice and false if the player decides to roll two.
    Algorithm:
                1) Iterate through a vector of the opponent's covered squares.
                2) roll one if opponent's covered squares are all less than 6, else
                roll two.
    Assistance Received: none
    ********************************************************************* */
    boolean roll_one() {
        //a boolean variable that indicated whether the player will roll one (true)
        // or roll two(false)
        boolean rollOne = true;
        String move_desc = "";
        move_desc += this.get_name() + " decided to ";
        for(int tile : this.opponent.get_covered_squares()){
            if (tile > 6) {
                rollOne = false;
                break;
            }
        }
        if(rollOne){
            move_desc += "roll one die because opponent has all squares greater than 6 uncovered.";
        }
        else{
            move_desc += "roll two dice because opponent's covered squares can be uncovered as well.";
        }
        this.latest_move += move_desc;
        return rollOne;
    }
}
