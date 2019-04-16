package com.example.tanguymaquinghen.meetingmanager;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;


public class SendMeetingRequestActivity extends AppCompatActivity {

    private double newLatitude = 0.0;
    private double newLongitude = 0.0;

    private String ActivityName = "SendMeetingRequestActivity";


    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 765;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 696;
    private String message = "";
    SmsManager smsManager = SmsManager.getDefault();
    ArrayList<String> contact = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_meeting_request);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        contact = intent.getStringArrayListExtra("selectedContact");
        this.newLatitude = intent.getDoubleExtra("newLatitude", 0.0);
        this.newLongitude = intent.getDoubleExtra("newLongitude", 0.0);

        if(extras!=null){
            this.message = extras.getString("receivedMessage");

        }

        if(isContactNull() == false){
            Drawable img = this.getResources().getDrawable( R.drawable.checkcontact );
            ((Button)findViewById(R.id.selectContactButton)).setCompoundDrawablesWithIntrinsicBounds(null,null, img,null);
            CharSequence s = ((Button)findViewById(R.id.selectContactButton)).getText();
            ((Button)findViewById(R.id.selectContactButton)).setText(s+" (" + Integer.toString(contact.size()) + ")");
        }

        if(isSpecificLocationSet() == true){
            Drawable img = this.getResources().getDrawable( R.drawable.checklocation );
            ((Button)findViewById(R.id.chooseOtherPlaceBtn)).setCompoundDrawablesWithIntrinsicBounds(null,null, img,null);
        }

        ((TextView)findViewById(R.id.messageTV)).setText(message);
        //((TextView)findViewById(R.id.newLat)).setText(Double.toString(newLatitude));
        //((TextView)findViewById(R.id.newLong)).setText(Double.toString(newLongitude));
        requestSmsPermission();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SendMeetingRequestActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            startLocationService();
        }

        if (contact != null){
            for (int i = 0; i < contact.size(); i++)

            Log.e("TestContact", contact.get(i));
        }

    }

    public boolean isContactNull(){
        if(this.contact == null && (isEmpty((EditText)findViewById(R.id.phoneNumEditText))) == true){
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startLocationService();
                }
            }
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    Toast.makeText(this,"permission not granted", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void startLocationService() {
        startService(new Intent(SendMeetingRequestActivity.this, LocationService.class));
    }


    public void sendText(View view) {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            ActivityCompat.requestPermissions(SendMeetingRequestActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        if(isContactNull()){
            Toast.makeText(this, "Please select contact(s) or enter a valid phone number", Toast.LENGTH_LONG).show();
            return;
        }

        if(isSpecificLocationSet() == false){
            GPSInformation();
        }else{

            MapsInformation();
            setNewLatitude(0.0);
            setNewLongitude(0.0);
        }

        this.contact.clear();
        setNewLatitude(0.0);
        setNewLongitude(0.0);

        Intent refresh = new Intent(this, SendMeetingRequestActivity.class);
        startActivity(refresh);
        finish();

    }

    public boolean isSpecificLocationSet(){
        if(this.newLongitude == 0.0 || this.newLatitude == 0.0){
            return false;
        }return true;
    }


    public void setNewLatitude(double newLatitude) {
        this.newLatitude = newLatitude;
    }


    public void setNewLongitude(double newLongitude) {
        this.newLongitude = newLongitude;
    }

    public void GPSInformation(){
        if (isEmpty((EditText) findViewById(R.id.phoneNumEditText))){
            int i = 0;
            for(String number : phoneNumberToSendTo(contact)){
                number = number.replaceAll(" ","");
                if (PhoneNumberUtils.isGlobalPhoneNumber(number)){
                    smsManager.sendTextMessage(number,
                            null,
                            "Hi ! Would you meet me at : \n" + "Latitude : " + String.valueOf(LocationService.getLatitude())
                                    + " : Longitude : " + String.valueOf(LocationService.getLongitude()),
                            null,
                            null);
                    i = i+1;
                }
            }
            Toast.makeText(this,i + " request has been send", Toast.LENGTH_SHORT).show();


        }else {
            if (PhoneNumberUtils.isGlobalPhoneNumber(((EditText) findViewById(R.id.phoneNumEditText)).getText().toString())) {
                smsManager.sendTextMessage(((EditText) findViewById(R.id.phoneNumEditText)).getText().toString(),
                        null,
                        "Hi ! Would you meet me at : \n" + "Latitude : " + String.valueOf(LocationService.getLatitude())
                                + " : Longitude : " + String.valueOf(LocationService.getLongitude()),
                        null,
                        null);

                Toast.makeText(this,"Request send to : " + ((EditText) findViewById(R.id.phoneNumEditText)).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void MapsInformation(){
        if (isEmpty((EditText) findViewById(R.id.phoneNumEditText))){
            int i = 0;
            for(String number : phoneNumberToSendTo(contact)){
                number = number.replaceAll(" ","");
                if (PhoneNumberUtils.isGlobalPhoneNumber(number)){
                    smsManager.sendTextMessage(number,
                            null,
                            "Hi ! Would you meet me at : \n" + "Latitude : " + this.newLatitude
                                    + " : Longitude : " + this.newLongitude,
                            null,
                            null);
                    i = i+1;
                }
            }
            Toast.makeText(this,i + " request has been send", Toast.LENGTH_SHORT).show();


        }else {
            if (PhoneNumberUtils.isGlobalPhoneNumber(((EditText) findViewById(R.id.phoneNumEditText)).getText().toString())) {
                smsManager.sendTextMessage(((EditText) findViewById(R.id.phoneNumEditText)).getText().toString(),
                        null,
                        "Hi ! Would you meet me at : \n" + "Latitude : " + this.newLatitude
                                + " : Longitude : " + this.newLongitude,
                        null,
                        null);

                Toast.makeText(this,"Request send to : " + ((EditText) findViewById(R.id.phoneNumEditText)).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void selectContact(View view) {
        Intent intent = new Intent(this, ContactListActivity.class);
        intent.putExtra("newLongitude", newLongitude);
        intent.putExtra("newLatitude",  newLatitude);
        startActivity(intent);
    }

    public ArrayList<String> phoneNumberToSendTo(ArrayList<String> s){
        ArrayList<String> result = new ArrayList<>();

        for (int i = 0; i < s.size(); i++){
            /*Log.e("split", s.get(i));
            String[] separated = s.get(i).split("|");
            //separated[1].trim();
            result.add(separated[1]);*/

            StringTokenizer tokens = new StringTokenizer(s.get(i), "|");
            String first = tokens.nextToken();
            String second = tokens.nextToken();
            result.add(second);
        }

        return result;

    }

    private Object[] appendValue(Object[] obj, Object newObj) {

        ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
        temp.add(newObj);
        return temp.toArray();

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    public void setPlaceMeeting(View view) {
        Intent intent = new Intent(this,MapsActivity.class);
        intent.putExtra("ActivityName", ActivityName);
        intent.putExtra("selectedContact", contact);
        startActivity(intent);
    }

}


