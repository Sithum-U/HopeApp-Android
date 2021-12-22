package com.example.finalhope;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.finalhope.model.Event;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RaiserProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //variables
    TextInputLayout fullName, username, nic, email, phoneNo, password;
    TextView txt_fullName, txt_username, fundCount;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    Button buttonDelete, btnUpdate;

    int count = 0;
    String countS;
    String type;

    List<Event> events;

    //Global variables to hold user data inside this activity
    String _NAME, _USERNAME, _NIC, _EMAIL, _PHONENO, _PASSWORD,_FUNDCOUNT;


    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raiser_profile);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        reference = FirebaseDatabase.getInstance().getReference("users");

//        String TAG = "MyActivity";
//        Log.i(TAG, "MyProfile.onCreate() " + _USERNAME);

        //Hooks
        fundCount = findViewById(R.id.txt_hearts);
        fullName = findViewById(R.id.mat_fullName);
        username = findViewById(R.id.mat_username);
        nic = findViewById(R.id.mat_nic);
        email = findViewById(R.id.mat_email);
        phoneNo = findViewById(R.id.mat_phone);
        password = findViewById(R.id.mat_password);
        txt_fullName = findViewById(R.id.txt_name);
        txt_username = findViewById(R.id.txt_uname);
        buttonDelete = findViewById(R.id.btn_delete);
        btnUpdate = findViewById(R.id.btn_update_raiser);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_Rprofile);

        /*-----------------------Tool Bar------------------*/
        //setSupportActionBar(toolbar);

        /*-------------------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //make menu clickable
        navigationView.setNavigationItemSelectedListener(RaiserProfile.this);

        //When application launches, direct to the home
        //navigationView.setCheckedItem(R.id.nav_home);

        //Show all data
        ShowAllRaiserData();

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog("ev",_USERNAME);
                //deleteRaiser(_USERNAME);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRaiser(_USERNAME);
            }
        });
    }

    private void showDeleteDialog(final String eventId, String USER_ID){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
        //final View dialogView = inflater.inflate(R.layout.delete_confirm, null);
        //dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Are you Sure to delete?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteRaiser(_USERNAME);

                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                if(type.equals("Donor")) {
                    intent.putExtra("type","Donor");
                }
                if(type.equals("Raiser")) {
                    intent.putExtra("type","Raiser");
                }
                intent.putExtra("username",_USERNAME);
                startActivity(intent);

            }

        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        final AlertDialog b = dialogBuilder.create();
        b.show();

    }

    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("funds");

        //checking the entered username with username in the database
        Query checkUser = reference.orderByChild("fundUser").equalTo(_USERNAME);

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
                    fundCount.setText(countS);

                    String TAG = "MyActivity";
                    Log.i(TAG, "Raiser Profile countString" + countS);
                    Log.i(TAG, "Raiser Profile countInteger" + count);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteRaiser(String rUsername) {
        DatabaseReference dRaiser = FirebaseDatabase.getInstance().getReference("users").child(rUsername);

        dRaiser.removeValue();

        Toast.makeText(this, "Profile Deleted", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RaiserProfile.this,SignUp.class);
        startActivity(intent);
    }

//    //If nav menu is open and when we press back button instead of going back to the previous activity, just close the nav menu
//    @Override
//    public void onBackPressed() {
//        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        }
//        else {
//            super.onBackPressed();
//        }
//    }



    private void ShowAllRaiserData() {

        Intent intent = getIntent();
        //_NAME = intent.getStringExtra("fullName");
        _USERNAME = intent.getStringExtra("username");
        _FUNDCOUNT = intent.getStringExtra("fundCount");
        type = intent.getStringExtra("type");
        //_NIC = intent.getStringExtra("nic_reg");
        //_EMAIL = intent.getStringExtra("email");
        //_PHONENO = intent.getStringExtra("phoneNo");
        //_PASSWORD = intent.getStringExtra("password");

        String TAG = "MyActivity";
        Log.i(TAG, "MyProfile.getView() " + _USERNAME);
        Log.i(TAG, "RaiserProfile.fundCount() " + _FUNDCOUNT);

//        private void isUser() {
            //getting user entered values
            String raiserEnteredUsername = _USERNAME;
//            String raiserEnteredPassword = password.getEditText().getText().toString().trim();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

            //checking the entered username with username in the database
            Query checkUser = reference.orderByChild("username").equalTo(raiserEnteredUsername);

            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
//                        //if 2nd time entered correctly,remove error
//                        username.setError(null);
//                        username.setErrorEnabled(false);//remove space error
//
//                        //if username exists, get the relevant password
//                        String passwordFromDB = snapshot.child(raiserEnteredUsername).child("password").getValue(String.class);
//
//                        //checking the entered password with password in the database
//                        if(passwordFromDB.equals(raiserEnteredPassword)) {
                            //if 2nd time entered correctly,remove error
//                            username.setError(null);
//                            username.setErrorEnabled(false);//remove space error

//                            String nameFromDB = snapshot.child(raiserEnteredUsername).child("fullName").getValue(String.class);
//                            String usernameFromDB = snapshot.child(raiserEnteredUsername).child("username").getValue(String.class);
//                            String emailFromDB = snapshot.child(raiserEnteredUsername).child("email").getValue(String.class);
//                            String phoneFromDB = snapshot.child(raiserEnteredUsername).child("phoneNo").getValue(String.class);
//                            String nicFromDB = snapshot.child(raiserEnteredUsername).child("nic_reg").getValue(String.class);
//                            String passwordFromDB = snapshot.child(raiserEnteredUsername).child("password").getValue(String.class);

                        _NAME =  snapshot.child(raiserEnteredUsername).child("fullName").getValue(String.class);
                        _EMAIL = snapshot.child(raiserEnteredUsername).child("email").getValue(String.class);
                        _PHONENO = snapshot.child(raiserEnteredUsername).child("phoneNo").getValue(String.class);
                        _NIC = snapshot.child(raiserEnteredUsername).child("nic_reg").getValue(String.class);
                        _PASSWORD = snapshot.child(raiserEnteredUsername).child("password").getValue(String.class);

                        txt_fullName.setText(_NAME);
                        txt_username.setText(_USERNAME);
                        fullName.getEditText().setText(_NAME);
                        username.getEditText().setText(_USERNAME);
                        nic.getEditText().setText(_NIC);
                        email.getEditText().setText(_EMAIL);
                        phoneNo.getEditText().setText(_PHONENO);
                        password.getEditText().setText(_PASSWORD);

                        String TAG = "MyActivity";
                        Log.i(TAG, "Donor Profile countString" + countS);

//                            Intent intent = new Intent(getApplicationContext(),HopeHomeActivty.class);
//
//                            intent.putExtra("fullName",nameFromDB);
//                            intent.putExtra("username",usernameFromDB);
//                            intent.putExtra("email",emailFromDB);
//                            intent.putExtra("phoneNo",phoneFromDB);
//                            intent.putExtra("nic_reg",nicFromDB);
//                            intent.putExtra("password",passwordFromDB);
//
//                            startActivity(intent);
//                        }
//                        else {
//                            password.setError("Wrong Password");
//                            password.requestFocus(); //automatically request the focus and start pointing on specific field
//                        }
                    }
                    else {
                        username.setError("No such User exist");
                        username.requestFocus();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

      //  }


    }

    private void updateRaiser(String view) {
        //call Database only if any there is any data update
        if(isNameChanged() || isPasswordChanged() || isEmailChanged() || isNICChanged() || isPhoneChanged() || isUsernameChanged()) {
            Toast.makeText(this, "Data has been Updated", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Data is same and cannot be Updated", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isPasswordChanged() {
        if(!_PASSWORD.equals(password.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("password").setValue(password.getEditText().getText().toString());
            _PASSWORD = password.getEditText().getText().toString();
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isNameChanged() {
        if(!_NAME.equals(fullName.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("fullName").setValue(fullName.getEditText().getText().toString());
            _NAME = fullName.getEditText().getText().toString();
            txt_fullName.setText(_NAME);
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isUsernameChanged() {
        if(!_USERNAME.equals(username.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("username").setValue(username.getEditText().getText().toString());
            _USERNAME = username.getEditText().getText().toString();
            txt_username.setText(_USERNAME);
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isNICChanged() {
        if(!_NIC.equals(nic.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("nic_reg").setValue(nic.getEditText().getText().toString());
            _NIC = nic.getEditText().getText().toString();
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isEmailChanged() {
        if(!_EMAIL.equals(email.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("email").setValue(email.getEditText().getText().toString());
            _EMAIL = email.getEditText().getText().toString();
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isPhoneChanged() {
        if(!_PHONENO.equals(phoneNo.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("phoneNo").setValue(phoneNo.getEditText().getText().toString());
            _PHONENO = phoneNo.getEditText().getText().toString();
            return true;
        }
        else{
            return false;
        }
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
                    intent.putExtra("username",_USERNAME);
                    intent.putExtra("type","Raiser");
                    startActivity(intent);
                    break;
                }

            case R.id.nav_create_event:
                if(type.equals("Donor") ) {
                    Intent intent4 = new Intent(getApplicationContext(), CreateEventActivity.class);
                    intent4.putExtra("username",_USERNAME);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else{
                    Intent intent1 = new Intent(getApplicationContext(),CreateEventActivity.class);
                    intent1.putExtra("username",_USERNAME);
                    intent1.putExtra("type","Raiser");
                    startActivity(intent1);
                    break;
                }

            case R.id.nav_home:
                if(type.equals("Donor") ) {
                    Intent intent4 = new Intent(getApplicationContext(), HopeHomeActivty.class);
                    intent4.putExtra("username",_USERNAME);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else {
                    Intent intent2 = new Intent(getApplicationContext(),HopeHomeActivty.class);
                    intent2.putExtra("username",_USERNAME);
                    intent2.putExtra("type","Raiser");
                    startActivity(intent2);
                    break;
                }

            case R.id.nav_profile_manage:
                if (type.equals("Donor")){
                    Intent intent3 = new Intent(getApplicationContext(),donor_profilemanagement.class);
                    intent3.putExtra("username",_USERNAME);
                    intent3.putExtra("type","Donor");
                    startActivity(intent3);
                    break;
                }
                else{
                    Intent intent3 = new Intent(getApplicationContext(),RaiserProfileManage.class);
                    intent3.putExtra("username",_USERNAME);
                    intent3.putExtra("type","Raiser");
                    startActivity(intent3);
                    break;
                }

            case R.id.nav_profile:
                if (type.equals("Donor")){
                    Intent intent4 = new Intent(getApplicationContext(),donor_profile.class);
                    intent4.putExtra("username",_USERNAME);
                    intent4.putExtra("type","Donor");
                    startActivity(intent4);
                    break;
                }
                else{
                    Intent intent4 = new Intent(getApplicationContext(),RaiserProfile.class);
                    intent4.putExtra("username",_USERNAME);
                    intent4.putExtra("type","Raiser");
                    startActivity(intent4);
                    break;
                }

            case R.id.nav_event:
                if (type.equals("Donor")) {
                    Intent intent5 = new Intent(getApplicationContext(), PostListActivity.class);
                    intent5.putExtra("username", _USERNAME);
                    intent5.putExtra("type", "Donor");
                    startActivity(intent5);
                    break;
                }
                else {
                    Intent intent5 = new Intent(getApplicationContext(),PostListActivity.class);
                    intent5.putExtra("username",_USERNAME);
                    intent5.putExtra("type","Raiser");
                    startActivity(intent5);
                    break;
                }

            case R.id.nav_fund:
                if (type.equals("Donor")) {
                    Intent intent6 = new Intent(getApplicationContext(), FundPostListActivity.class);
                    intent6.putExtra("username", _USERNAME);
                    intent6.putExtra("type", "Donor");
                    startActivity(intent6);
                    break;
                }
                else {
                    Intent intent6 = new Intent(getApplicationContext(),FundPostListActivity.class);
                    intent6.putExtra("username",_USERNAME);
                    intent6.putExtra("type","Raiser");
                    startActivity(intent6);
                    break;
                }

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}