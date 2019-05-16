package com.e15.alarmnats.ActivityController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.e15.alarmnats.R;

public class ChooseTaskActivity extends AppCompatActivity {
    Intent returnIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_task);
        setTitle("Choose Task");
        returnIntent= new Intent();
    }

    public void chooseDefault(View view) {
        returnIntent.putExtra("task", getString(R.string.default_question));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void chooseRecaptcha(View view) {
        returnIntent.putExtra("task", getString(R.string.recaptcha_question));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void chooseQR(View view) {
        returnIntent.putExtra("task", getString(R.string.qr_question));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void chooseMath(View view) {
        returnIntent.putExtra("task", getString(R.string.math_question));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
