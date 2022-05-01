package com.example.canoga;

public class Human extends Player{
    /* *********************************************************************
    Function Name: Human
    Purpose: To construct a Human object
    Parameters:
                player_name, a string variable passed by value. It holds a
                player's name passed by the user.
    Return Value: a Human object
    Algorithm:
                1) Set the passed parameter as the name of the object.
                2) Set the previous round score as 0.
    Assistance Received: none
    ********************************************************************* */
    public Human(String player_name) {
        this.name = player_name;
        this.set_previous_round_score(0);
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
    public String get_name(){
        return this.name;
    }
}
