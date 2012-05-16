package com.philanaaurelia.counterone;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class TimerAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("alarm_message");
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Timer Display Error: Timer Complete", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
