
package com.example.finalhope;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.finalhope.adapters.FundPostList;
import com.example.finalhope.model.Event;
import com.example.finalhope.model.Fund;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FundPostListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //we will use these constants later to pass the artist name and id to another activity
    //public static final String EVENT_NAME = "com.example.artists.eventName";
    //public static final String EVENT_ID = "com.example.artists.eventId";

    //ConstraintLayout fundsLayout;
    //ConstraintLayout eventsLayout;
    ListView listPosts;
    ProgressBar progressBar;

    //a list to store all the artist from firebase database
    List<Fund> funds;

    //our database reference object
    DatabaseReference databaseEvents;

    String USER_ID;
    String fundId;
    String type;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_post_list);

        Intent intent = getIntent();
        //eventId = intent.getStringExtra("eventId");
        USER_ID = intent.getStringExtra("username");
        type = intent.getStringExtra("type");
        //fundId = intent.getStringExtra("fundId");

        //getting the reference of artists node
        //databaseEvents = FirebaseDatabase.getInstance().getReference("funds");
        databaseEvents = FirebaseDatabase.getInstance().getReference("funds");

//        fundsLayout = findViewById(R.id.fundsLayout);
//        eventsLayout = findViewById(R.id.eventsLayout);
        listPosts = findViewById(R.id.listViewPosts);
        progressBar = findViewById(R.id.progressBar2);

        drawerLayout = findViewById(R.id.drawer_layout_fundPostList);
        navigationView = findViewById(R.id.nav_view_fundPostList);
        toolbar = findViewById(R.id.toolbar_fundPostList);

        /*-------------------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //make menu clickable
        navigationView.setNavigationItemSelectedListener(this);


//        fundsLayout.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(PostListActivity.this , CreateEventActivity.class));
//            }
//        });
//
//        eventsLayout.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(PostListActivity.this , CreateEventActivity.class));
//            }
//        });

        //list to store artists
        funds = new ArrayList<>();

        //attaching listener to listview
        listPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                Fund fund = funds.get(i);

                fundId = fund.getFundId();
                //creating an intent
                Intent intent = new Intent(getApplicationContext(), FundSinglePostActivity.class);

                intent.putExtra("fundId",fundId);
                if(type.equals("Donor")) {
                    intent.putExtra("type","Donor");
                }
                if(type.equals("Raiser")) {
                    intent.putExtra("type","Raiser");
                }
                intent.putExtra("username",USER_ID);

                //starting the activity with intent
                startActivity(intent);
            }
        });
    }

    protected void onStart() {
        super.onStart();
        //attaching value event listener
        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.VISIBLE);

                //clearing the previous artist list
                funds.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting event
                    Fund fund = postSnapshot.getValue(Fund.class);
                    //adding artist to the list
                    funds.add(fund);
                }

                //creating adapter
                FundPostList posAdapter = new FundPostList(FundPostListActivity.this, funds);
                //attaching adapter to the listview
                listPosts.setAdapter(posAdapter);

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                if(USER_ID.equals("not") ){
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
                    intent.putExtra("username",USER_ID);
                    intent.putExtra("type","Raiser");
                    startActivity(intent);
                    break;
                }

            case R.id.nav_create_event:
                if(USER_ID.equals("not")){
                    Toast.makeText(this, "You are not registered.Please register!", Toast.LENGTH_LONG).show();
                    Intent intent4 = new Intent(getApplicationContext(),WelcomeScreenActivity.class);
                    startActivity(intent4);
                    break;
                } else if(type.equals("Donor") ) {
                    Intent intent4 = new Intent(getApplicationContext(), CreateEventActivity.class);
                    intent4.putExtra("username",USER_ID);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else{
                    Intent intent1 = new Intent(getApplicationContext(),CreateEventActivity.class);
                    intent1.putExtra("username",USER_ID);
                    intent1.putExtra("type","Raiser");
                    startActivity(intent1);
                    break;
                }

            case R.id.nav_home:
                if(type.equals("Donor") ) {
                    Intent intent4 = new Intent(getApplicationContext(), HopeHomeActivty.class);
                    intent4.putExtra("username",USER_ID);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else if(USER_ID.equals("not")) {
                    Intent intent4 = new Intent(getApplicationContext(),HopeHomeActivty.class);
                    startActivity(intent4);
                    break;
                }
                else {
                    Intent intent2 = new Intent(getApplicationContext(),HopeHomeActivty.class);
                    intent2.putExtra("username",USER_ID);
                    intent2.putExtra("type","Raiser");
                    startActivity(intent2);
                    break;
                }

            case R.id.nav_profile_manage:
                if(USER_ID.equals("not")){
                    Toast.makeText(this, "You are not registered.Please register!", Toast.LENGTH_LONG).show();
                    Intent intent4 = new Intent(getApplicationContext(),WelcomeScreenActivity.class);
                    startActivity(intent4);
                    break;
                }else if (type.equals("Donor")){
                    Intent intent3 = new Intent(getApplicationContext(),donor_profilemanagement.class);
                    intent3.putExtra("username",USER_ID);
                    intent3.putExtra("type","Donor");
                    startActivity(intent3);
                    break;
                }
                else{
                    Intent intent3 = new Intent(getApplicationContext(),RaiserProfileManage.class);
                    intent3.putExtra("username",USER_ID);
                    intent3.putExtra("type","Raiser");
                    startActivity(intent3);
                    break;
                }

            case R.id.nav_profile:
                if(USER_ID.equals("not")){
                    Toast.makeText(this, "You are not registered.Please register!", Toast.LENGTH_LONG).show();
                    Intent intent4 = new Intent(getApplicationContext(),WelcomeScreenActivity.class);
                    startActivity(intent4);
                    break;
                }else if (type.equals("Donor")){
                    Intent intent4 = new Intent(getApplicationContext(),donor_profile.class);
                    intent4.putExtra("username",USER_ID);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else{
                    Intent intent4 = new Intent(getApplicationContext(),RaiserProfile.class);
                    intent4.putExtra("username",USER_ID);
                    intent4.putExtra("type","Raiser");
                    startActivity(intent4);
                    break;
                }

            case R.id.nav_event:
                if (type.equals("Donor")) {
                    Intent intent5 = new Intent(getApplicationContext(), PostListActivity.class);
                    intent5.putExtra("username", USER_ID);
                    intent5.putExtra("type", "Donor");
                    startActivity(intent5);
                    break;
                }
                else {
                    Intent intent5 = new Intent(getApplicationContext(),PostListActivity.class);
                    intent5.putExtra("username",USER_ID);
                    intent5.putExtra("type","Raiser");
                    startActivity(intent5);
                    break;
                }

            case R.id.nav_fund:
                if (type.equals("Donor")) {
                    Intent intent6 = new Intent(getApplicationContext(), FundPostListActivity.class);
                    intent6.putExtra("username", USER_ID);
                    intent6.putExtra("type", "Donor");
                    startActivity(intent6);
                    break;
                }
                else {
                    Intent intent6 = new Intent(getApplicationContext(),FundPostListActivity.class);
                    intent6.putExtra("username",USER_ID);
                    intent6.putExtra("type","Raiser");
                    startActivity(intent6);
                    break;
                }

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

