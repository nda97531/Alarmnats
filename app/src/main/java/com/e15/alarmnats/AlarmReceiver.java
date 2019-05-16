package com.e15.alarmnats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.e15.alarmnats.ActivityController.AlarmFiredActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")
                    || intent.getAction().equals("com.mine.alarm")) {
                ring(context, intent);
            }
        } catch (NullPointerException e) {
            ring(context, intent);
        }
    }

    private void ring(Context context, Intent intent) {
        String ringtone = intent.getStringExtra("ringtoneUri");
        String alarmTime = intent.getStringExtra("alarmTime");
        String question = intent.getStringExtra("question");
        String answer = intent.getStringExtra("answer");
        String label = intent.getStringExtra("label");
//        System.out.println("my receiver label : " + label);

        Log.d("ringtone", ringtone);
        System.out.println("ringing here: "+ alarmTime);

        Toast.makeText(context, "Alarm fired", Toast.LENGTH_LONG).show();
        Log.i("broadcast receiver", "Alarm Fired");

        Intent alarmFiredIntent = new Intent(context, AlarmFiredActivity.class);

//        alarmFiredIntent.putExtra("lock", true);
        alarmFiredIntent.putExtra("ringtone", ringtone);
        alarmFiredIntent.putExtra("alarmTime", alarmTime);
        alarmFiredIntent.putExtra("question", question);
        alarmFiredIntent.putExtra("answer", answer);
        alarmFiredIntent.putExtra("label", label);

        alarmFiredIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(alarmFiredIntent);
    }
}