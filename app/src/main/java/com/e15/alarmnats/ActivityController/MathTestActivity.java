package com.e15.alarmnats.ActivityController;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.e15.alarmnats.R;

import java.util.Random;

public class MathTestActivity extends AppCompatActivity {
    EditText math_answer;
    TextView math_question;
    Button button;
    int x = 0, y = 0, z = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_test);

        math_answer = findViewById(R.id.math_answer);
        math_question = findViewById(R.id.math_question);
        button = findViewById(R.id.button);

        Random randomGenerator = new Random();
        x = randomGenerator.nextInt(500) + 100;
        y = randomGenerator.nextInt(500) + 100;
        z = randomGenerator.nextInt(5000) + 100;

        math_question.setText(x + " x " + y + " + " + z + " = ");
    }

    public void submitBtnClick(View view) {
        try {
            int user_answer = Integer.parseInt(math_answer.getText().toString());
            if (user_answer == x * y + z) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Wrong answer!!", Toast.LENGTH_SHORT).show();
        }
    }

}
