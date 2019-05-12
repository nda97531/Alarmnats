package com.e15.alarmnats.ViewSupport;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.e15.alarmnats.MainActivity;
import com.e15.alarmnats.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final int RINGTONE_REQUEST_CODE = 1;

    AlarmManager alarmManager;

    // lists to keep alarm details
    private ArrayList<String> mAlarmTimes = new ArrayList<>();
    private ArrayList<Long> mAlarmTimesInMillis = new ArrayList<>();
    private ArrayList<Boolean> mAlarmStatuses = new ArrayList<>();
    private ArrayList<String> mRingtoneNames = new ArrayList<>();
    private ArrayList<String> mRingtoneUris = new ArrayList<>();
    private ArrayList<String> mLabels = new ArrayList<>();
    private ArrayList<String> mQuestions = new ArrayList<>();
    private ArrayList<Integer> mFlags = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> mAlarmTimes,
                               ArrayList<Long> mAlarmTimesInMillis,
                               ArrayList<Boolean> mAlarmStatuses,
                               ArrayList<String> mRingtoneNames,
                               ArrayList<String> mRingtoneUris,
                               ArrayList<String> mLabels,
                               ArrayList<String> mQuestions,
                               ArrayList<Integer> mFlags,
                               Context mContext) {
        this.mAlarmTimes = mAlarmTimes;
        this.mAlarmTimesInMillis = mAlarmTimesInMillis;
        this.mAlarmStatuses = mAlarmStatuses;
        this.mRingtoneNames = mRingtoneNames;
        this.mRingtoneUris = mRingtoneUris;
        this.mLabels = mLabels;
        this.mQuestions = mQuestions;
        this.mFlags = mFlags;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_alarm_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final int poisition = i;

        viewHolder.alarmTime.setText(mAlarmTimes.get(i));
        viewHolder.alarmSwitch.setChecked(mAlarmStatuses.get(i));
        viewHolder.alarmLabel.setText(mLabels.get(i));
        viewHolder.dismisQuestion.setText(mQuestions.get(i));
//        viewHolder.editAlarmButton.setText(mRingtoneNames.get(i));

        final long timeInMillis = mAlarmTimesInMillis.get(i);
        Uri ringtoneUri = Uri.parse(mRingtoneUris.get(i));
        int flag = mFlags.get(i);

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof MainActivity) {
                    Log.d("delete", "delete button clicked");
                    ((MainActivity) mContext).deleteAlarm(mFlags.get(poisition), poisition);
                }
            }
        });

        viewHolder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((MainActivity) mContext).enableExistingAlarm(mFlags.get(poisition));
                } else {
                    ((MainActivity) mContext).cancelAlarm(mFlags.get(poisition), true);// change status when switching on/off
                }
            }
        });

        viewHolder.editAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof MainActivity) {
                    Log.d("edit", "edit button clicked");
                    ((MainActivity) mContext).editAlarm(mFlags.get(poisition));
                }
            }
        });

        /*TODO: get time form timepicker
        * TODO: get ringtone
        * TODO: do all the things from main activity*/
    }

    @Override
    public int getItemCount() {
        return mAlarmTimes.size();
    }

    public void onRecAdapterActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onRecAdapterActivityResult");
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView alarmTime;
        Button editAlarmButton;
        Button deleteButton;
        TextView alarmLabel;
        TextView dismisQuestion;
        Switch alarmSwitch;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            alarmTime = itemView.findViewById(R.id.text_view_alarm_time);
            editAlarmButton = itemView.findViewById(R.id.button_edit_alarm);
            deleteButton = itemView.findViewById(R.id.button_delete);
            alarmLabel = itemView.findViewById(R.id.text_view_label);
            dismisQuestion = itemView.findViewById(R.id.dismiss_method);
            alarmSwitch = itemView.findViewById(R.id.switch_alarm);
            parentLayout = itemView.findViewById(R.id.layout_parent);

        }
    }
}
