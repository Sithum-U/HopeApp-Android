package com.example.finalhope;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalhope.model.Event;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

import org.joda.time.Period;
import org.joda.time.PeriodType;

public class SinglePostActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView eventHeading, eventDate, eventVenue,
            eventDescription, contactName, contactNumber, remainDays;
    RelativeLayout shareButton;

    DatabaseReference databaseSingleEvent;

    private Event event;

    String _EVENTID;
    String _USERID;
    String type;

     DrawerLayout drawerLayout;
     NavigationView navigationView;
     Toolbar toolbar;

    String eventDateVal,mtoday,dueRemain;


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

        drawerLayout = findViewById(R.id.singleEventPost_drawer);
        navigationView = findViewById(R.id.nav_singleEventPost);
        toolbar = findViewById(R.id.toolbar_singleEventPost);

        /*-------------------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //make menu clickable
        navigationView.setNavigationItemSelectedListener(this);


        Intent intent = getIntent();
        _EVENTID = intent.getStringExtra("eventId");
        _USERID = intent.getStringExtra("username");
        type = intent.getStringExtra("type");

        String TAG = "MyActivity";
        Log.i(TAG, "SinglePost.getView() " + _EVENTID);
        Log.i(TAG, "SinglePost.getView() " + _USERID);

        databaseSingleEvent = FirebaseDatabase.getInstance().getReference("events").child(_EVENTID);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                event = dataSnapshot.getValue(Event.class);
                try{
                    eventHeading.setText(event.getEventName());
                    eventDate.setText(event.getEventDate());
                    eventDateVal = event.getEventDate();
                    eventVenue.setText(event.getEventVenue());
                    eventDescription.setText(event.getEventDescription());
                    contactName.setText(event.getEventContactName());
                    contactNumber.setText(event.getEventContactNumber());
                    calculateDue(event.getEventDate());
                }catch(Exception e){
                    //closeActivity();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseSingleEvent.addValueEventListener(postListener);


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareEvent(event.getEventName(), event.getEventDate(),
                        event.getEventVenue(), event.getEventDescription(),
                        event.getEventContactName(),event.getEventContactNumber());
            }
        });



    }

    private void calculateDue(String eventDateVal) {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        mtoday = simpleDateFormat.format(Calendar.getInstance().getTime());

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");

        try {

            //Date date1 = simpleDateFormat1.parse(fundDateVal);
            Date date1 = simpleDateFormat1.parse(eventDateVal);

            Date date2 = simpleDateFormat.parse(mtoday);

            long startDate = date1.getTime();
            long endDate = date2.getTime();

            Period period = new Period(endDate, startDate, PeriodType.yearMonthDay());
            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();

            remainDays.setText(days + " days remaining");

        } catch (Exception e) {
            e.printStackTrace();
        }

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

    //If nav menu is open and when we press back button instead of going back to the previous activity, just close the nav menu
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
            this.finish();
        }
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case  R.id.nav_raise_fund:
                if(_USERID.equals("not") ){
                    Toast.makeText(this, "You are not registered.Please register!", Toast.LENGTH_LONG).show();
                    Intent intent4 = new Intent(getApplicationContext(),WelcomeScreenActivity.class);
                    startActivity(intent4);
                    break;
                }else if (type.equals("Donor")){
                    Toast.makeText(this, "Please register as a Raiser to raise a fund!", Toast.LENGTH_LONG).show();
                    Intent intent4 = new Intent(getApplicationContext(),WelcomeScreenActivity.class);
                    startActivity(intent4);
                    break;
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),CreateFundActivity.class);
                    intent.putExtra("username",_USERID);
                    intent.putExtra("type","Raiser");
                    startActivity(intent);
                    break;
                }

            case R.id.nav_create_event:
                if(_USERID.equals("not")){
                    Toast.makeText(this, "You are not registered.Please register!", Toast.LENGTH_LONG).show();
                    Intent intent4 = new Intent(getApplicationContext(),WelcomeScreenActivity.class);
                    startActivity(intent4);
                    break;
                } else if(type.equals("Donor") ) {
                    Intent intent4 = new Intent(getApplicationContext(), CreateEventActivity.class);
                    intent4.putExtra("username",_USERID);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else{
                    Intent intent1 = new Intent(getApplicationContext(),CreateEventActivity.class);
                    intent1.putExtra("username",_USERID);
                    intent1.putExtra("type","Raiser");
                    startActivity(intent1);
                    break;
                }

            case R.id.nav_home:
                if(type.equals("Donor") ) {
                    Intent intent4 = new Intent(getApplicationContext(), HopeHomeActivty.class);
                    intent4.putExtra("username",_USERID);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else if(_USERID.equals("not")) {
                    Intent intent4 = new Intent(getApplicationContext(),HopeHomeActivty.class);
                    startActivity(intent4);
                    break;
                }
                else {
                    Intent intent2 = new Intent(getApplicationContext(),HopeHomeActivty.class);
                    intent2.putExtra("username",_USERID);
                    intent2.putExtra("type","Raiser");
                    startActivity(intent2);
                    break;
                }

            case R.id.nav_profile_manage:
                if(_USERID.equals("not")){
                    Toast.makeText(this, "You are not registered.Please register!", Toast.LENGTH_LONG).show();
                    Intent intent4 = new Intent(getApplicationContext(),WelcomeScreenActivity.class);
                    startActivity(intent4);
                    break;
                }else if (type.equals("Donor")){
                    Intent intent3 = new Intent(getApplicationContext(),donor_profilemanagement.class);
                    intent3.putExtra("username",_USERID);
                    intent3.putExtra("type","Donor");
                    startActivity(intent3);
                    break;
                }
                else{
                    Intent intent3 = new Intent(getApplicationContext(),RaiserProfileManage.class);
                    intent3.putExtra("username",_USERID);
                    intent3.putExtra("type","Raiser");
                    startActivity(intent3);
                    break;
                }

            case R.id.nav_profile:
                if(_USERID.equals("not")){
                    Toast.makeText(this, "You are not registered.Please register!", Toast.LENGTH_LONG).show();
                    Intent intent4 = new Intent(getApplicationContext(),WelcomeScreenActivity.class);
                    startActivity(intent4);
                    break;
                }else if (type.equals("Donor")){
                    Intent intent4 = new Intent(getApplicationContext(),donor_profile.class);
                    intent4.putExtra("username",_USERID);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else{
                    Intent intent4 = new Intent(getApplicationContext(),RaiserProfile.class);
                    intent4.putExtra("username",_USERID);
                    intent4.putExtra("type","Raiser");
                    startActivity(intent4);
                    break;
                }

            case R.id.nav_event:
                if (type.equals("Donor")) {
                    Intent intent5 = new Intent(getApplicationContext(), PostListActivity.class);
                    intent5.putExtra("username", _USERID);
                    intent5.putExtra("type", "Donor");
                    startActivity(intent5);
                    break;
                }
                else {
                    Intent intent5 = new Intent(getApplicationContext(),PostListActivity.class);
                    intent5.putExtra("username",_USERID);
                    intent5.putExtra("type","Raiser");
                    startActivity(intent5);
                    break;
                }

            case R.id.nav_fund:
                if (type.equals("Donor")) {
                    Intent intent6 = new Intent(getApplicationContext(), FundPostListActivity.class);
                    intent6.putExtra("username", _USERID);
                    intent6.putExtra("type", "Donor");
                    startActivity(intent6);
                    break;
                }
                else {
                    Intent intent6 = new Intent(getApplicationContext(),FundPostListActivity.class);
                    intent6.putExtra("username",_USERID);
                    intent6.putExtra("type","Raiser");
                    startActivity(intent6);
                    break;
                }

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void closeActivity(){
        this.finish();
    }

}