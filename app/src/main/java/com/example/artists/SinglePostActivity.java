package com.example.artists;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.artists.model.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class SinglePostActivity extends AppCompatActivity {

    TextView eventHeading, eventDate, eventVenue,
            eventDescription, contactName, contactNumber, remainDays;
    RelativeLayout shareButton;

    DatabaseReference databaseSingleEvent;

    private Event event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        eventHeading = findViewById(R.id.postHeading);
        eventDate = findViewById(R.id.tv_eventPostDateData);
        eventVenue = findViewById(R.id.tv_eventPostDVenueData);
        eventDescription= findViewById(R.id.tv_eventPostDescriptData);
        contactName= findViewById(R.id.tv_eventPostContactNameData);
        contactNumber= findViewById(R.id.tv_eventPostContactNumberData);
        shareButton= findViewById(R.id.shareButton);
        remainDays = findViewById(R.id.tv_remainingDays);


        Intent intent = getIntent();

        databaseSingleEvent = FirebaseDatabase.getInstance().getReference("events").child(intent.getStringExtra(HopeHomeActivty.EVENT_ID));


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                event = dataSnapshot.getValue(Event.class);
                eventHeading.setText(event.getEventName());
                eventDate.setText(event.getEventDate());
                eventVenue.setText(event.getEventVenue());
                eventDescription.setText(event.getEventDescription());
                contactName.setText(event.getEventContactName());
                contactNumber.setText(event.getEventContactNumber());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseSingleEvent.addValueEventListener(postListener);

//        int rDays = (int)getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), event.getEventDate().toString(), "31/05/2017");
//
//        remainDays.setText(rDays);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareEvent(event.getEventName(), event.getEventDate(),
                        event.getEventVenue(), event.getEventDescription(),
                        event.getEventContactName(),event.getEventContactNumber());
            }
        });
    }

//    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
//        try {
//            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//
//
//    }


    private boolean shareEvent(String eventHeading, String eventDate,
                               String eventVenue, String eventDescription,
                               String contactName,String contactNumber) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,"Event heading : "+eventHeading + "\n\n"
                                                    +"Event date : " +eventDate + "\n\n"
                                                    +"Event venue : " +eventVenue + "\n\n"
                                                    +"Event description : " +eventDescription + "\n\n"
                                                    +"Contact name : " +contactName + "\n\n"
                                                    +"Contact number : " +contactNumber + "\n\n"
                                                    +"----Shared from HOPE app----");
        sendIntent.setType("text/plain");
        Intent.createChooser(sendIntent,"Share via");
        startActivity(sendIntent);
        return true;
    }




}