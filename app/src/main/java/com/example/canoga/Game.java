package com.example.canoga;
import java.util.ArrayList;

public class Game {
    boolean single_player;
    String player1_name, player2_name;
    int round_num;
    Round current_round;

    /* *********************************************************************
    Function Name: Game
    Purpose: To construct a default Game object
    Parameters: single_p, a boolean variable to denote whether the game is
                    single player or not.
                p1_name, a String variable that holds player1's name.
                p2_name, a String variable that holds player2's name.
    Return Value: a Game object
    Algorithm:
                1) Set the passed values and default values for all members
                    of game object
    Assistance Received: none
    ********************************************************************* */
    Game(Boolean single_p, String p1_name, String p2_name){
        this.single_player = single_p;
        this.player1_name = p1_name;
        this.player2_name = p2_name;
        round_num = 0;
    }

    /* *********************************************************************
    Function Name: initialize_round
    Purpose: To create a new round and pass all the relevant variables.
    Parameters: tiles, an int variable that holds the number of tiles the
                    player chose to play with.
    Return Value: none (void)
    Algorithm:
                1) Check if there is already any data in the current round
                    data member. If yes, save the required states in temporary
                    variables.
                2) Initialize a new round and pass all game values in to the
                    Round constructor.
    Assistance Received: none
    ********************************************************************* */
    public void initialize_round(int tiles){
        boolean first_round = true;
        int prev_round_sq = 0;
        String first_mover="", winner="";
        round_num++;
        if(this.current_round != null){
            this.player1_name = this.current_round.player1.get_name();
            this.player2_name = this.current_round.player2.get_name();
            first_round = false;
            first_mover = this.current_round.player1.had_first_turn()?
                    this.player1_name:
                    this.player2_name;
            winner = this.current_round.winner;
            prev_round_sq = this.current_round.player1.get_previous_round_score() != 0?
                    this.current_round.player1.get_previous_round_score():
                    this.current_round.player2.get_previous_round_score();
        }
        this.current_round = new Round(this.player1_name, this.player2_name, this.single_player, tiles, new ArrayList<>());
        if(!first_round){
            if(this.current_round.player1.get_name().equals(first_mover)){
                this.current_round.player1.set_had_first_turn(true);
            }
            else if(this.current_round.player2.get_name().equals(first_mover)){
                this.current_round.player2.set_had_first_turn(true);
            }
            if(this.current_round.player1.get_name().equals(winner)){
                this.current_round.player1.set_previous_round_score(prev_round_sq);
            }
            else{
                this.current_round.player2.set_previous_round_score(prev_round_sq);
            }
        }
    }
}
