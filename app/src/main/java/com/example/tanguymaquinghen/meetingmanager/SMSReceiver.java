package com.example.tanguymaquinghen.meetingmanager;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;


public class SMSReceiver extends BroadcastReceiver {

    private Bundle bundle;
    private SmsMessage currentSMS;
    private String message;
    private String phoneNumber;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {

                    for (Object aObject : pdu_Objects) {

                        currentSMS = getIncomingMessage(aObject, bundle);

                        phoneNumber = currentSMS.getDisplayOriginatingAddress();

                        message = currentSMS.getDisplayMessageBody();

                        if (checkReleventSMS(message)) {
                            Log.i("sms", "senderNum: " + phoneNumber + " :\n message: " + message);
                            Toast.makeText(context, "senderNum: " + phoneNumber + " :\n message: " + message, Toast.LENGTH_LONG).show();
                            generateNotification(context, "New meeting request from : " + phoneNumber);
                        }
                    }
                    this.abortBroadcast();

                }
            }
        }
    }


    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

    private boolean checkReleventSMS(String s){
        boolean result = false;
        StringTokenizer tokens = new StringTokenizer(s, ":");
        String first = tokens.nextToken();
        Log.i("tokenizer", first);

        String delimiter = "Hi ! Would you meet me at ";


        if(first.equals(delimiter)){
            result = true;
        }
        return result;
    }

    private void generateNotification(Context context, String content){

        Intent intent = new Intent(context, ManageRequestActivity.class);
        intent.putExtra("receivedMessage", message);
        intent.putExtra("senderPhoneNumber", phoneNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.i("content", message);

        CharSequence name = "ChannelId";
        String description = "This channel is used for meetingManager app";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel;
       // Log.d("notifChanel", "avant le if");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            channel = new NotificationChannel("meetingManager", name, importance);
            channel.setDescription(description);
            NotificationManager mNotificationManager = context.getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(channel);
           // Log.d("notifChanel", "dans le if");
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "meetingManager");
        mBuilder.setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("MeetingManager")
                .setContentText(content)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);




        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }


}
