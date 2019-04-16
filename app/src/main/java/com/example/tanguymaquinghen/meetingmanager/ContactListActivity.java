package com.example.tanguymaquinghen.meetingmanager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ContactListActivity extends AppCompatActivity {

    private ListView mListView;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private double newLatitude;
    private double newLongitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        mListView = findViewById(R.id.listView);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getContactList());
            mListView.setAdapter(adapter);*/
        }

        Intent intent = getIntent();
        this.newLatitude = intent.getDoubleExtra("newLatitude", 0.0);
        this.newLongitude =intent.getDoubleExtra("newLongitude", 0.0);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, getContactList());
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setItemsCanFocus(false);

    }




    private ArrayList<String> getContactList() {

        ArrayList<String> result = new ArrayList<String>();
        Set<String> hs = new HashSet<>();


        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.e("mes contact", "Name: " + name);
                        Log.e("mes contact", "Phone Number: " + phoneNo);

                        result.add(name + " |" + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }

        hs.addAll(result);
        result.clear();
        result.addAll(hs);
        Collections.sort(result);
        return result;

    }



    public void getCheckedContact(View view) {
        ListView lv =  findViewById(R.id.listView);
        SparseBooleanArray checked = lv.getCheckedItemPositions();
        ArrayList<String> contactToSendTo = new ArrayList<String>();

        Intent intent = new Intent(this, SendMeetingRequestActivity.class);

        for (int i = 0; i < lv.getAdapter().getCount(); i++) {
            if (checked.get(i)) {
                contactToSendTo.add(lv.getItemAtPosition(i).toString());
                Log.e("Contact", lv.getItemAtPosition(i).toString());
            }
        }

        intent.putStringArrayListExtra("selectedContact", contactToSendTo);
        intent.putExtra("newLatitude", this.newLatitude);
        intent.putExtra("newLongitude", this.newLongitude);
        startActivity(intent);

    }
}
