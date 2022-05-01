package com.example.canoga;

import java.util.ArrayList;
import java.util.List;

public class GameUtils {
    /* *********************************************************************
    Function Name: GameUtils
    Purpose: To construct a GameUtils object
    Parameters:
    Return Value: a GameUtils object
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    GameUtils(){}

    /* *********************************************************************
    Function Name: check_unique
    Purpose: To check if all the numbers in a combination are unique.
    Parameters:
                one_combination, a vector of integers passed by value. It holds
                a combination of moves (ints)
    Return Value: A boolean value that is true if the combination has all unique
                  numbers and false if the combination does not have all unique
                  numbers.
    Algorithm:
                1) Iterate through the vector of the combination, and check if
                there are any two numbers that are equal to each other.
    Assistance Received: none
    ********************************************************************* */
    boolean check_unique(List<Integer> one_combination) {
        for (int i = 0; i < one_combination.size() - 1; i++) {
            for (int j = i + 1; j < one_combination.size(); j++) {
                if (one_combination.get(i).equals(one_combination.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /* *********************************************************************
    Function Name: findCombinationsUtil
    Purpose: To
    Parameters:
                all_possible_combinations, a Pointer to Vector of Vectors.
                It holds all the possible combinations of moves a user
                can make from a given dice roll value.
                arr, an integer array. It holds one possible combination
                from all possible combinations of moves a user can make.
                index, an integer variable. It holds the index of array
                which holds one possible combination out of all combinations.
                num, an integer variable. It holds the value from which
                all moves need to be calculated from. This value is the sum
                of both dice rolled values of a player.
                reducedNum, an integer variable. It holds the number of levels
                in the recursive tree when the function findCombinationsUtil
                is called recursively.
    Return Value: none (void)
    Algorithm:
                1) If reducedNum is less than 0, then return from the function.
                2) If reducedNum is equal to 0, then form a vector and copy
                the elements from arr into the vector one_possible_combination.
                Check if the combination has all unique elements. If yes, push
                the vector into the all_possible_combinations vector.
                3) If reducedNum is greater than 0, then find the previous
                number stored in arr, if none then use 1.
                4) Loop from the previous number upto passed num value.
                5) Set the value at index position of array to k.
                6) Recursively call findCombinationsUtil function with
                reducedNum.
    Assistance Received: Geeks4Geeks
    ********************************************************************* */
    void findCombinationsUtil(List<List<Integer>> all_possible_combinations, int[] arr, int index, int num, int reducedNum) {
        // Base condition
        if (reducedNum < 0)
            return;

        // If combination is found, push it to vector
        if (reducedNum == 0)
        {
            List<Integer> one_possible_combination = new ArrayList<>();
            for (int i = 0; i < index; i++) {
                one_possible_combination.add(arr[i]);
            }
            if (check_unique(one_possible_combination)) {
                all_possible_combinations.add(one_possible_combination);
            }
            return;
        }

        // Find the previous number stored in arr[]
        // It helps in maintaining increasing order
        int prev = (index == 0) ? 1 : arr[index - 1];

        // note: loop starts from previous number
        // i.e. at array location index - 1
        for (int k = prev; k <= num; k++)
        {
            // next element of array is k
            arr[index] = k;

            // call recursively with reduced number
            findCombinationsUtil(all_possible_combinations, arr, index + 1, num, reducedNum - k);
        }
    }

    /* *********************************************************************
    Function Name: findCombinations
    Purpose: To
    Parameters:
                all_possible_combinations, a Pointer to Vector of Vectors.
                It holds all the possible combinations of moves a user
                can make from a given dice roll value.
                n, an integer variable. It holds the value from which
                all moves need to be calculated from. This value is the sum
                of both dice rolled values of a player.
    Return Value: none (void)
    Algorithm:
                1) call findCombinationsUtil function to initiate the
                recursive sequence.
    Assistance Received: Geeks4Geeks
    ********************************************************************* */
    void findCombinations(List<List<Integer>> all_possible_combinations, int n) {
        // array to store the combinations
        // It can contain max n elements
        int [] arr = new int[n];

        //find all combinations
        findCombinationsUtil(all_possible_combinations, arr, 0, n, n);
    }

    /* *********************************************************************
    Function Name: all_possible_moves
    Purpose: To find all possible combinations from a given rolled dice value.
    Parameters:
                value, an integer passed by value. It holds the sum of dice values
                rolled by the player.
    Return Value: a Vector of Pointer of Vector of integers that contains the
                  memory locations that hold all the combinations of moves that
                  can be made from a given value.
    Algorithm:
                1) Call the recursive function to find all possible combination
                from a given value.
    Assistance Received: none
    ********************************************************************* */
    List<List<Integer>> all_possible_moves(int value) {
        // a vector pointer that will hold all possible moves from a given value
        List<List<Integer>> all_moves = new ArrayList<>();
        findCombinations(all_moves, value);
        return all_moves;
    }

    /* *********************************************************************
    Function Name: all_available_cover_moves
    Purpose: To find all available cover moves by comparing a given list of all
             possible moves with the player's uncovered squares.
    Parameters:
                all_possible_moves, a vector of pointer of vector of integers.
                It holds the memory locations that point to all possible moves
                from a rolled dice value.
                currentPlayer_uncovered_sq, a vector of integers passed by value.
                It holds all the uncovered squares of the current player.
    Return Value: A Vector of Pointer of Vector of integers that holds all the
                  memory locations for all available cover moves.
    Algorithm:
                1) Iterate through the vector of all_possible_moves.
                2) Iterate thorugh each move inside the vector.
                3) Iterate through the currentPlayer_uncovered_sq vector.
                4) Check if you find all the numbers from each vector of all_possible_moves
                in the currentPlayer_uncovered_sq vector.
                5) If there is a vector that has an integer which isn't found in the
                currentPlayer_uncovered_sq vector, then remove that vector from
                all_possible_moves.
                6) Return all_possible_moves which has now been modified into
                all_available_cover_moves vector.
    Assistance Received: none
    ********************************************************************* */
    List<List<Integer>> all_available_cover_moves(List<List<Integer>> all_possible_moves, List<Integer> currentPlayer_uncovered_sq) {
        // check currentPlayer's uncovered squares with all_possible_cover_moves
        List<List<Integer>> all_available_moves = new ArrayList<>();
        for (List<Integer>one_move: all_possible_moves) {
            boolean found = false;
            for (int tile: one_move) {
                found = false;
                for (int uncovered_square: currentPlayer_uncovered_sq) {
                    if (tile == uncovered_square) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    break;
                }
            }
            if (found) {
                all_available_moves.add(one_move);
            }
        }
        return all_available_moves;
    }

    /* *********************************************************************
    Function Name: all_available_uncover_moves
    Purpose: To find all available uncover moves by comparing a given list of all
             possible moves with the opponent's covered squares.
    Parameters:
                all_possible_moves, a vector of pointer of vector of integers.
                It holds the memory locations that point to all possible moves
                from a rolled dice value.
                nextPlayer_covered_sq, a vector of integers passed by value.
                It holds all the covered squares of the opponent player.
                advantage_square, an integer passed by value. It holds the square
                that has been decided to be covered from the start of the round for
                a given advantaged player.
    Return Value: A Vector of Pointer of Vector of integers that holds all the
                  memory locations for all available uncover moves.
    Algorithm:
                1) Iterate through the vector of all_possible_moves.
                2) Iterate thorugh each move inside the vector.
                3) Iterate through the nextPlayer_covered_sq vector.
                4) Check if you find all the numbers from each vector of all_possible_moves
                in the nextPlayer_covered_sq vector.
                5) If there is a vector that has an integer which isn't found in the
                nextPlayer_covered_sq vector, then remove that vector from
                all_possible_moves.
                6) If the size of all_available_moves vector is 1 and that one vector has
                the advantage square as the first element, empty the all_available_moves and
                return empty vector.
                7) Else return all_possible_moves which has now been modified into
                all_available_uncover_moves vector.
    Assistance Received: none
    ********************************************************************* */
    List<List<Integer>> all_available_uncover_moves(List<List<Integer>> all_possible_moves, List<Integer> nextPlayer_covered_sq, int advantage_square) {
        // check nextPlayer's covered squares with all_possible_uncover_moves
        List<List<Integer>> all_available_moves = new ArrayList<>();
        for (List<Integer>one_move: all_possible_moves) {
            boolean found = false;
            for (int tile: one_move) {
                found = false;
                for (int covered_square: nextPlayer_covered_sq) {
                    if (tile == covered_square) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    break;
                }
            }
            if (found) {
                all_available_moves.add(one_move);
            }
        }
        if (all_available_moves.size() == 1 && all_available_moves.get(0).get(0) == advantage_square) {
            all_available_moves.clear();
        }
        return all_available_moves;
    }

    /* *********************************************************************
    Function Name: roll_one_available
    Purpose: To check if a player is eligible to have to option to roll one dice.
    Parameters:
                uncovered_squares, a vector of integers passed by value. It holds
                the player's own uncovered squares.
    Return Value: a boolean value which is true if a player is eligible to have
                  the option to roll one dice and vice versa.
    Algorithm:
                1) Iterate through the vector of uncovered squares.
                2) If a number greater than 6 is found, return false.
                3) If no number is greater than 6, return true.
    Assistance Received: none
    ********************************************************************* */
    boolean roll_one_available(List<Integer> uncovered_squares) {
        for (int square: uncovered_squares) {
            if (square > 6) {
                return false;
            }
        }
        return true;
    }
}
