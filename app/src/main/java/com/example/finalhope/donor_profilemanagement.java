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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalhope.model.Event;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class donor_profilemanagement extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    Button callmyprofile;
    Button callmyevent;
    Button callchangeprof;
    String _DUSERNAME;
    TextView eventCount;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    String type;

    int count = 0;
    String countS;

    List<Event> events;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_donor_profilemanagement);

        callmyprofile = findViewById(R.id.button2);
        callmyevent = findViewById(R.id.button3);
        //callchangeprof = findViewById(R.id.button8);
        eventCount = findViewById(R.id.tx);

        drawerLayout = findViewById(R.id.drawer_DonorProfManage);
        navigationView = findViewById(R.id.nav_view_DonorProfManage);
        toolbar = findViewById(R.id.toolbar_DonorProfManage);

        Intent intent = getIntent();
        _DUSERNAME = intent.getStringExtra("username");
        type = intent.getStringExtra("type");

        /*-------------------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //make menu clickable
        navigationView.setNavigationItemSelectedListener(this);

        //setCount();

        callmyprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(donor_profilemanagement.this, donor_profile.class);
                intent.putExtra("username", _DUSERNAME);
                intent.putExtra("type","Donor");
                startActivity(intent);

            }
        });


        callmyevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(donor_profilemanagement.this, EventListActivity.class);
                intent.putExtra("username", _DUSERNAME);
                intent.putExtra("type","Donor");
                startActivity(intent);
            }
        });

//        callchangeprof.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(donor_profilemanagement.this, profilepic.class);
//                startActivity(intent);
//
//            }
//        });



    }



    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("events");

        //checking the entered username with username in the database
        Query checkUser = reference.orderByChild("eventUser").equalTo(_DUSERNAME);

        checkUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    //clearing the previous artist list
//                    events.clear();

                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        //getting event
//                        Event event = postSnapshot.getValue(Event.class);
//                        //adding event to the list
//                        events.add(event);
                        count++;
                    }

                    countS = String.valueOf(count);
                    eventCount.setText(countS);

                    String TAG = "MyActivity";
                    Log.i(TAG, "Donor Profile countString" + countS);
                    Log.i(TAG, "Donor Profile countInteger" + count);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

//    public void setCount() {
//
//        String TAG = "MyActivity";
//        Log.i(TAG, "Setcount " + countS);
//    }

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case  R.id.nav_raise_fund:
                if (type.equals("Donor")){
                    Toast.makeText(this, "Please register as a Raiser to raise a fund!", Toast.LENGTH_LONG).show();
                    Intent intent4 = new Intent(getApplicationContext(),WelcomeScreenActivity.class);
                    startActivity(intent4);
                    break;
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),CreateFundActivity.class);
                    intent.putExtra("username",_DUSERNAME);
                    startActivity(intent);
                    break;
                }

            case R.id.nav_create_event:
                if(type.equals("Donor") ) {
                    Intent intent4 = new Intent(getApplicationContext(), CreateEventActivity.class);
                    intent4.putExtra("username",_DUSERNAME);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else{
                    Intent intent1 = new Intent(getApplicationContext(),CreateEventActivity.class);
                    intent1.putExtra("username",_DUSERNAME);
                    startActivity(intent1);
                    break;
                }

            case R.id.nav_home:
                if(type.equals("Donor") ) {
                    Intent intent4 = new Intent(getApplicationContext(), HopeHomeActivty.class);
                    intent4.putExtra("username",_DUSERNAME);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else {
                    Intent intent2 = new Intent(getApplicationContext(),HopeHomeActivty.class);
                    intent2.putExtra("username",_DUSERNAME);
                    startActivity(intent2);
                    break;
                }

            case R.id.nav_profile_manage:
                if (type.equals("Donor")){
                    Intent intent3 = new Intent(getApplicationContext(),donor_profilemanagement.class);
                    intent3.putExtra("username",_DUSERNAME);
                    intent3.putExtra("type","Donor");
                    startActivity(intent3);
                    break;
                }
                else{
                    Intent intent3 = new Intent(getApplicationContext(),RaiserProfileManage.class);
                    intent3.putExtra("username",_DUSERNAME);
                    startActivity(intent3);
                    break;
                }

            case R.id.nav_profile:
                if (type.equals("Donor")){
                    Intent intent4 = new Intent(getApplicationContext(),donor_profile.class);
                    intent4.putExtra("username",_DUSERNAME);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else{
                    Intent intent4 = new Intent(getApplicationContext(),RaiserProfile.class);
                    intent4.putExtra("username",_DUSERNAME);
                    startActivity(intent4);
                    break;
                }

            case R.id.nav_event:
                if (type.equals("Donor")) {
                    Intent intent5 = new Intent(getApplicationContext(), PostListActivity.class);
                    intent5.putExtra("username", _DUSERNAME);
                    intent5.putExtra("type", "Donor");
                    startActivity(intent5);
                    break;
                }
                else {
                    Intent intent5 = new Intent(getApplicationContext(),PostListActivity.class);
                    intent5.putExtra("username",_DUSERNAME);
                    startActivity(intent5);
                    break;
                }

            case R.id.nav_fund:
                if (type.equals("Donor")) {
                    Intent intent6 = new Intent(getApplicationContext(), FundPostListActivity.class);
                    intent6.putExtra("username", _DUSERNAME);
                    intent6.putExtra("type", "Donor");
                    startActivity(intent6);
                    break;
                }
                else {
                    Intent intent6 = new Intent(getApplicationContext(),FundPostListActivity.class);
                    intent6.putExtra("username",_DUSERNAME);
                    startActivity(intent6);
                    break;
                }

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}