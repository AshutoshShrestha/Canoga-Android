package com.example.canoga;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {
    TextView player1_score_label, player2_score_label, game_winner_label;
    Button play_again_btn;
    String player1_name, player2_name;
    int player1_score, player2_score;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent myIntent = getIntent(); // gets the previously created intent

        player1_name = myIntent.getStringExtra("player1Name");
        player2_name= myIntent.getStringExtra("player2Name");
        player1_score= Integer.parseInt(myIntent.getStringExtra("player1Score"));
        player2_score= Integer.parseInt(myIntent.getStringExtra("player2Score"));

        player1_score_label = findViewById(R.id.player1_score_label);
        player2_score_label = findViewById(R.id.player2_score_label);
        game_winner_label = findViewById(R.id.game_winner_label);
        play_again_btn = findViewById(R.id.play_again_btn);

        player1_score_label.setText(player1_name + ": " + player1_score);
        player2_score_label.setText(player2_name + ": " + player2_score);

        if(player1_score > player2_score) {
            game_winner_label.setText(player1_name + " won. Congrats!");
        }
        else if(player1_score < player2_score){
            game_winner_label.setText(player2_name + " won. Congrats!");
        }
        else{
            game_winner_label.setText("It was a draw. Well done.");
        }
        play_again_btn.setOnClickListener(view -> finish());
    }
}