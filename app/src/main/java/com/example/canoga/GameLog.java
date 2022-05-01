package com.example.canoga;

import java.util.ArrayList;
import java.util.List;

public class GameLog {
    List<String> log_record;

    /* *********************************************************************
   Function Name: GameLog
   Purpose: To construct a GameLog object
   Parameters:
   Return Value: a GameLog object
   Algorithm:
               1) Initialize an empty log_record.
   Assistance Received: none
   ********************************************************************* */
    GameLog(){
        this.log_record = new ArrayList<>();
    }

    /* *********************************************************************
    Function Name: add_log
    Purpose: To add a new log message to the entire log record.
    Parameters: log, a String variable that holds the log msg to be recorded.
    Return Value:
    Algorithm:
              1) Add the passed parameter to the log_record arraylist.
    Assistance Received: none
    ********************************************************************* */
    void add_log(String log){
        log_record.add(log);
    }

    /* *********************************************************************
    Function Name: get_all_log
    Purpose: To return the entire log of a round.
    Parameters:
    Return Value: String variable that holds the entire log of a round.
    Algorithm:
              1) Append all the String values in the log_record arraylist to
                a StringBuilder variable all_log and return it.
    Assistance Received: none
    ********************************************************************* */
    String get_all_log(){
        StringBuilder all_log = new StringBuilder();
        for(int i = log_record.size()-1; i >= 0; i--){
            all_log.append(log_record.get(i)).append('\n');
            all_log.append("----------------------------" + '\n');
        }
        return all_log.toString();
    }

    /* *********************************************************************
    Function Name: get_latest_log
    Purpose: To return the latest log message recorded.
    Parameters:
    Return Value: String variable that holds the latest log record.
    Algorithm:
              1) Return the last element of the log_record arraylist.
    Assistance Received: none
    ********************************************************************* */
    String get_latest_log(){
        return log_record.get(log_record.size()-1);
    }
}
