package com.example.tanguymaquinghen.meetingmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class ManageRequestActivity extends AppCompatActivity {
    private GoogleMap mMap;

    private String message;
    private String phoneNum;
    private double longitute;
    private double latitude;

    private String ActivityName = "ManageRequestActivity";

    SmsManager smsManager = SmsManager.getDefault();
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 696;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_request);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            this.message = extras.getString("receivedMessage");
            this.phoneNum = extras.getString("senderPhoneNumber");

        }


        ((TextView)findViewById(R.id.senderTV)).setText(phoneNum);

        getLongLat(message);

    }

    public void sendAcceptAnswer(View view) {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ManageRequestActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        smsManager.sendTextMessage(phoneNum,
                null,
                "Yeah sure ! See you there !\n",
                null,
                null);

        this.longitute = 0.0;
        this.latitude = 0.0;
        Toast.makeText(this, "The answer has been send !", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, SendMeetingRequestActivity.class);
        startActivity(intent);

    }

    public void sendRefuseAnswer(View view) {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ManageRequestActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        smsManager.sendTextMessage(phoneNum,
                null,
                "No sorry I can't ! :( \n",
                null,
                null);
        this.longitute = 0.0;
        this.latitude = 0.0;

        Toast.makeText(this, "The answer has been send !", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, SendMeetingRequestActivity.class);
        startActivity(intent);

    }

    public void getLongLat(String textMessage){
        String[] split = textMessage.split(" : ");

        for(int i = 0 ; i < split.length ; i++){
            Log.i("position", split[i]);
        }

       this.latitude = Double.valueOf(split[4]);
       this.longitute = Double.valueOf(split[2]);


    }

    public void goToMaps(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("longitude", this.longitute);
        intent.putExtra("latitude", this.latitude);
        intent.putExtra("ActivityName", ActivityName);
        intent.putExtra("message", message);
        intent.putExtra("phone",phoneNum);
        startActivity(intent);
    }
}
