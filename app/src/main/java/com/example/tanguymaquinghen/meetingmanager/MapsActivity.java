package com.example.tanguymaquinghen.meetingmanager;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double longitude;
    private double latitude;
    private double newLatitude;
    private double newLongitude;

    private String phoneNum;
    private String message;

    private ArrayList<String> contact;

    private String previousActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        this.longitude = intent.getDoubleExtra("longitude", 0.0);
        this.latitude = intent.getDoubleExtra("latitude", 0.0);
        this.previousActivity = intent.getStringExtra("ActivityName");
        this.phoneNum = intent.getStringExtra("phone");
        this.message = intent.getStringExtra("message");
        this.contact = intent.getStringArrayListExtra("selectedContact");

        if(this.previousActivity.equals("ManageRequestActivity")){
            ((Button)findViewById(R.id.retourNewPosition)).setVisibility(View.GONE);
            ((Button)findViewById(R.id.retourChoice)).setVisibility(View.VISIBLE);


        }else{
            ((Button)findViewById(R.id.retourChoice)).setVisibility(View.GONE);
            ((Button)findViewById(R.id.retourNewPosition)).setVisibility(View.VISIBLE);

        }



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if(this.longitude == 0.0 && this.latitude == 0.0){
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(point).title("newPlace"));

                }

            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    newLatitude = marker.getPosition().latitude;
                    newLongitude = marker.getPosition().longitude;
                    return false;
                }
            });
        } else {

            LatLng meetingPlace = new LatLng(this.longitude, this.latitude);
            mMap.addMarker(new MarkerOptions().position(meetingPlace).title("meeting place"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meetingPlace, 12));
        }

    }

    public void returnToSendTextActivity(View view) {
        Intent intent = new Intent(this, SendMeetingRequestActivity.class);
        intent.putExtra("newLongitude", newLongitude);
        intent.putExtra("newLatitude", newLatitude);
        intent.putExtra("selectedContact", contact);
        setPreviousActivity("");
        startActivity(intent);
    }

    public void setPreviousActivity(String previousActivity) {
        this.previousActivity = previousActivity;
    }


    public void returnToChoice(View view) {
        Intent intent = new Intent(this, ManageRequestActivity.class);
        setPreviousActivity("");
        intent.putExtra("receivedMessage", message);
        intent.putExtra("senderPhoneNumber", phoneNum);
        startActivity(intent);
    }
}
