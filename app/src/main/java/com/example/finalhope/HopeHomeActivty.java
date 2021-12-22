package com.example.finalhope;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.util.Log;
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
import com.example.finalhope.adapters.PostList;
import com.example.finalhope.model.Event;
import com.example.finalhope.model.Fund;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HopeHomeActivty extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //we will use these constants later to pass the artist name and id to another activity

    //public static final String EVENT_NAME = "com.example.artists.eventName";
    //public static final String EVENT_ID = "com.example.artists.eventId";
    //public static final String USER_ID = intent.getStringExtra("username");



//    public String USER_ID = "com.example.artists.userId";
//    public static final String USER_ID = "com.example.artists.userId";
//    public static final String USER_ID = "com.example.artists.userId";
//    public static final String USER_ID = "com.example.artists.userId";
//    public static final String USER_ID = "com.example.artists.userId";
//
//    Intent intent = getIntent();
//    _NAME = intent.getStringExtra("fullName");
//    _USERNAME = intent.getStringExtra("username");
//    _NIC = intent.getStringExtra("nic_reg");
//    _EMAIL = intent.getStringExtra("email");
//    _PHONENO = intent.getStringExtra("phoneNo");
//    _PASSWORD = intent.getStringExtra("password");

    ConstraintLayout fundsLayout;
    ConstraintLayout eventsLayout;
    ListView listPosts;
    ProgressBar progressBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;


    //a list to store all the artist from firebase database
    List<Event> events;
    List<Fund> funds;

    //our database reference object
    DatabaseReference databaseEvents;
    DatabaseReference databaseFunds;
    String USER_ID;
    String eventId;
    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hope_home_activty);

        Intent intent = getIntent();
        USER_ID = intent.getStringExtra("username");
        type = intent.getStringExtra("type");

        //getting the reference of events node
        databaseEvents = FirebaseDatabase.getInstance().getReference("events");
        databaseFunds = FirebaseDatabase.getInstance().getReference("funds");

        fundsLayout = findViewById(R.id.fundsLayout);
        eventsLayout = findViewById(R.id.eventsLayout);
        listPosts = findViewById(R.id.listViewPosts);
        progressBar = findViewById(R.id.progressBar2);

        drawerLayout = findViewById(R.id.home_drawer_layout);
        navigationView = findViewById(R.id.navHome);
        toolbar = findViewById(R.id.toolbar_Rprofile);


        /*-------------------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //make menu clickable
        navigationView.setNavigationItemSelectedListener(this);

        //When application launches, direct to the home
        //navigationView.setCheckedItem(R.id.nav_home);


        String TAG = "MyActivity";
        Log.i(TAG, "HOME.getView() " + USER_ID);


        fundsLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //creating an intent
                Intent intent = new Intent(getApplicationContext(), FundPostListActivity.class);

                //putting artist name and id to intent
//                intent.putExtra(EVENT_ID, event.getEventId());
//                intent.putExtra(EVENT_NAME, event.getEventName());
                //intent.putExtra("eventId",eventId);
                if(type.equals("Donor")) {
                    intent.putExtra("type","Donor");
                }
                if(type.equals("Raiser")) {
                    intent.putExtra("type","Raiser");
                }
                if(type.equals("not")) {
                    intent.putExtra("type","not");
                    intent.putExtra("username","not");
                }
                if(USER_ID.equals("not")) {
                    intent.putExtra("type","not");
                    intent.putExtra("username","not");
                }

                intent.putExtra("username",USER_ID);

                String TAG = "MyActivity";
                Log.i(TAG, "HOME.eventID " + eventId);

                //starting the activity with intent
                startActivity(intent);
                //startActivity(new Intent(HopeHomeActivty.this , FundPostListActivity.class));
            }
        });

        eventsLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //creating an intent
                Intent intent = new Intent(getApplicationContext(), PostListActivity.class);

                //putting artist name and id to intent
//                intent.putExtra(EVENT_ID, event.getEventId());
//                intent.putExtra(EVENT_NAME, event.getEventName());
                //intent.putExtra("eventId",eventId);
                if(type.equals("Donor")) {
                    intent.putExtra("type","Donor");
                }
                if(type.equals("Raiser")) {
                    intent.putExtra("type","Raiser");
                }
                if(type.equals("not")) {
                    intent.putExtra("type","not");
                    intent.putExtra("username","not");
                }
                if(USER_ID.equals("not")) {
                    intent.putExtra("type","not");
                    intent.putExtra("username","not");
                }

                intent.putExtra("username",USER_ID);


                String TAG = "MyActivity";
                Log.i(TAG, "HOME.eventID " + eventId);

                //starting the activity with intent
                startActivity(intent);
                //startActivity(new Intent(HopeHomeActivty.this , PostListActivity.class));
            }
        });

        //list to store events
        events = new ArrayList<>();
        funds = new ArrayList<>();

        //attaching listener to listview
        listPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                Event event = events.get(i);

                eventId = event.getEventId();

                //creating an intent
                Intent intent = new Intent(getApplicationContext(), SinglePostActivity.class);

                //putting artist name and id to intent
//                intent.putExtra(EVENT_ID, event.getEventId());
//                intent.putExtra(EVENT_NAME, event.getEventName());

                if(type.equals("Donor")) {
                    intent.putExtra("type","Donor");
                }
                if(type.equals("Raiser")) {
                    intent.putExtra("type","Raiser");
                }

                intent.putExtra("eventId",eventId);
                intent.putExtra("username",USER_ID);


                String TAG = "MyActivity";
                Log.i(TAG, "HOME.eventID " + eventId);

                //starting the activity with intent
                startActivity(intent);
            }
        });

//        listPosts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Artist artist = artists.get(i);
//                showUpdateDeleteDialog(artist.getArtistId(), artist.getArtistName(), artist.getArtistGenre());
//                return true;
//            }
//        });

    }

    protected void onStart() {
        super.onStart();
//        attaching value event listener
        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.VISIBLE);

                //clearing the previous artist list
                events.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting event
                    Event event = postSnapshot.getValue(Event.class);
                    //adding artist to the list
                    events.add(event);
                }

                //creating adapter
                PostList posAdapter = new PostList(HopeHomeActivty.this, events);
                //attaching adapter to the listview
                listPosts.setAdapter(posAdapter);

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        databaseFunds.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                progressBar.setVisibility(View.VISIBLE);
//
//                //clearing the previous artist list
//                funds.clear();
//
//                //iterating through all the nodes
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    //getting event
//                    Fund fund = postSnapshot.getValue(Fund.class);
//                    //adding artist to the list
//                    funds.add(fund);
//                }
//
//                //creating adapter
//                FundPostList posAdapter = new FundPostList(HopeHomeActivty.this, funds);
//                //attaching adapter to the listview
//                listPosts.setAdapter(posAdapter);
//
//                progressBar.setVisibility(View.GONE);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

//    private void showUpdateDeleteDialog(final String artistId, String artistName, String artistGenre) {
//
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
//        dialogBuilder.setView(dialogView);
//
//        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
//        final EditText editTextType = (EditText) dialogView.findViewById(R.id.editTextType);
//        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
//        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteArtist);
//        final Button buttonShare = (Button) dialogView.findViewById(R.id.buttonShareArtist);
//
//        dialogBuilder.setTitle(artistName);
//        final AlertDialog b = dialogBuilder.create();
//        b.show();
//        editTextName.setText(artistName);
//        editTextType.setText(artistGenre);
//
//
//
//        buttonUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String name = editTextName.getText().toString().trim();
//                String genre = editTextType.getText().toString().trim();
//                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(genre)) {
//                    updateArtist(artistId, name, genre);
//                    b.dismiss();
//                }
//
//            }
//        });
//
//        buttonDelete.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                //deleteArtist(artistId);
//                //b.dismiss();
//                showDeleteDialog(artistId, artistName, artistGenre);
//                b.dismiss();
//            }
//        });
//
//        buttonShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                shareArtist(artistId,artistName,artistGenre);
//                b.dismiss();
//            }
//        });
//    }
//
//    private void showDeleteDialog(final String artistId, String artistName, String artistGenre){
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.delete_confirm, null);
//        dialogBuilder.setView(dialogView);
//
//        final Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancelDelete);
//        final Button buttonConfirm = (Button) dialogView.findViewById(R.id.buttonConfirmDelete);
//        //final TextView textview = (TextView) dialogView.findViewById(R.id.textView2);
//
//        dialogBuilder.setTitle("Are you Sure to delete?");
//        final AlertDialog b = dialogBuilder.create();
//        b.show();
//
//        buttonCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                b.dismiss();
//                showUpdateDeleteDialog(artistId,artistName,artistGenre);
//            }
//        });
//
//        buttonConfirm.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                deleteArtist(artistId);
//                b.dismiss();
//            }
//        });
//    }
//
//
//
//    private boolean updateArtist(String id, String name, String genre) {
//        //getting the specified artist reference
//        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("events").child(id);
//
//        //updating artist
//        Event event = new Event(id, name, genre);
//        dR.setValue(event);
//        Toast.makeText(getApplicationContext(), "Event Updated", Toast.LENGTH_LONG).show();
//        return true;
//    }
//
//    private boolean deleteArtist(String id) {
//        //getting the specified artist reference
//        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("events").child(id);
//
//        //removing artist
//        dR.removeValue();
//
////        //getting the tracks reference for the specified artist
////        DatabaseReference drTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id);
////
////        //removing all tracks
////        drTracks.removeValue();
//        Toast.makeText(getApplicationContext(), "event Deleted", Toast.LENGTH_LONG).show();
//
//        return true;
//    }
//    private boolean shareArtist(String id, String name, String venue) {
//        //getting the specified artist reference
//        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("events").child(id);
//
//        Event event = new Event(id, name, venue);
//
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT,event.getEventName()+ ", " + event.getEventVenue() );
//        sendIntent.setType("text/plain");
//        Intent.createChooser(sendIntent,"Share via");
//        startActivity(sendIntent);
//        return true;
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

            case R.id.nav_logout:
                Intent intent6 = new Intent(getApplicationContext(), WelcomeScreenActivity.class);
                startActivity(intent6);
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}