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

import com.example.finalhope.model.Fund;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpCookie;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

import org.joda.time.Period;
import org.joda.time.PeriodType;

public class FundSinglePostActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TextView fundHeading, fundDate, fundAmount,
            fundDescription, contactName, contactNumber,remainDayView;
    RelativeLayout shareButton;

    DatabaseReference databaseSingleEvent;

    String _FUNDID;
    String _USERID;
    String type;

    String fundDateVal,mtoday,dueRemain;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private Fund fund;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_single_post);
        fundHeading = findViewById(R.id.postHeading);
        fundDate = findViewById(R.id.tv_eventPostDateData);
        fundAmount = findViewById(R.id.tv_eventPostDVenueData);
        fundDescription= findViewById(R.id.tv_eventPostDescriptData);
        contactName= findViewById(R.id.tv_eventPostContactNameData);
        contactNumber= findViewById(R.id.tv_eventPostContactNumberData);
        shareButton= findViewById(R.id.shareButton);
        remainDayView = findViewById(R.id.tv_remainDays);


        drawerLayout = findViewById(R.id.singleFundPost_drawer_layout);
        navigationView = findViewById(R.id.nav_singleFundPost);
        toolbar = findViewById(R.id.toolbar_singleFundPost);

        /*-------------------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //make menu clickable
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();

        _FUNDID = intent.getStringExtra("fundId");
        _USERID = intent.getStringExtra("username");
        type = intent.getStringExtra("type");


        databaseSingleEvent = FirebaseDatabase.getInstance().getReference("funds").child(_FUNDID);

        //databaseSingleEvent = FirebaseDatabase.getInstance().getReference("funds").child(intent.getStringExtra(com.example.finalhope.FundListActivity.FUND_ID));


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                fund = dataSnapshot.getValue(Fund.class);
                try{
                    fundHeading.setText(fund.getFundName());
                    fundDate.setText(fund.getFundDate());
                    //fundDateVal = fund.getFundDate();
                    fundAmount.setText(fund.getFundAmount());
                    fundDescription.setText(fund.getFundDescription());
                    contactName.setText(fund.getFundContactName());
                    contactNumber.setText(fund.getFundContactNumber());
                    calculateDue(fund.getFundDate());
                }catch(Exception e){
                    //closeActivity();
                    String TAG = "MyActivity";
                    Log.i(TAG, "SinglePost.getView() " + _FUNDID);
                    Log.i(TAG, "SinglePost.getView() " + _USERID);
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
                shareFund(fund.getFundName(), fund.getFundDate(),
                        fund.getFundAmount(), fund.getFundDescription(),
                        fund.getFundContactName(),fund.getFundContactNumber());
            }
        });


    }

    private void calculateDue(String fundDateVal) {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        mtoday = simpleDateFormat.format(Calendar.getInstance().getTime());

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");

        try {
            String TAG = "MyActivity";
            Log.i(TAG, "Fund date today " + mtoday);
            Log.i(TAG, "Fund date fund date  " + fundDateVal);

            //Date date1 = simpleDateFormat1.parse(fundDateVal);
            Date date1 = simpleDateFormat1.parse(fundDateVal);

            Date date2 = simpleDateFormat.parse(mtoday);

            long startDate = date1.getTime();
            long endDate = date2.getTime();
//            long startDate = date2.getTime();
//            long endDate = date1.getTime();

            Period period = new Period(endDate, startDate, PeriodType.yearMonthDay());
            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();

            remainDayView.setText(days + " days remaining");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void closeActivity(){
        this.finish();
    }

    //    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
//        try {
//            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//
//    int rDays = (int)getDateDiff(new SimpleDateFormat("dd/MM/yyyy"),)

    private boolean shareFund(String fundHeading, String fundDate,
                              String fundAmount, String fundDescription,
                              String contactName,String contactNumber) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,"Fund heading : "+fundHeading + "\n\n"
                +"Fund date : " +fundDate + "\n\n"
                +"Fund amount : " +fundAmount + "\n\n"
                +"Fund description : " +fundDescription + "\n\n"
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
}