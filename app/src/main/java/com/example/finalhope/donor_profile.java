package com.example.finalhope;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class donor_profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //variables
    TextInputLayout fullName, username, email, phoneNo, password;
    TextView txt_fullName, txt_username;
    Button btnDelete;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    //Global variables to hold user data inside this activity
    String _DNAME, _DUSERNAME, _DEMAIL, _DPHONENO, _DPASSWORD;

    DatabaseReference reference;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);


        reference = FirebaseDatabase.getInstance().getReference("Donors");

        //Hooks
        fullName = findViewById(R.id.Dmat_fullName);
        username = findViewById(R.id.Dmat_username);
        email = findViewById(R.id.Dmat_email);
        phoneNo = findViewById(R.id.Dmat_phone);
        password = findViewById(R.id.Dmat_password);
        txt_fullName = findViewById(R.id.Dtxt_name);
        txt_username = findViewById(R.id.Dtxt_uname);
        btnDelete = findViewById(R.id.Dbtn_delete);

        drawerLayout = findViewById(R.id.drawer_donorProfile);
        navigationView = findViewById(R.id.nav_view_donorProfile);
        toolbar = findViewById(R.id.toolbar_donorProfile);

        /*-----------------------Tool Bar------------------*/
        //setSupportActionBar(toolbar);

        /*-------------------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //make menu clickable
        navigationView.setNavigationItemSelectedListener(donor_profile.this);

        //Show all data
        ShowAllDonorData();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog("eve",_DUSERNAME);
                //deleteDonor(_DUSERNAME);
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

                deleteDonor(_DUSERNAME);

                Intent intent = new Intent(getApplicationContext(),donor_signup.class);
                if(type.equals("Donor")) {
                    intent.putExtra("type","Donor");
                }
                if(type.equals("Raiser")) {
                    intent.putExtra("type","Raiser");
                }
                intent.putExtra("username",_DUSERNAME);
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

    private void deleteDonor(String dusername) {
        DatabaseReference donor = FirebaseDatabase.getInstance().getReference("Donors").child(dusername);

        donor.removeValue();

        Toast.makeText(this, "Profile Deleted", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(donor_profile.this,donor_signup.class);
        startActivity(intent);
    }


    private void ShowAllDonorData() {

        Intent intent = getIntent();
//        _DNAME = intent.getStringExtra("name");
        _DUSERNAME = intent.getStringExtra("username");
        type = intent.getStringExtra("type");
//        _DEMAIL = intent.getStringExtra("email");
//        _DPHONENO = intent.getStringExtra("phoneno");
//        _DPASSWORD = intent.getStringExtra("password");

//        txt_fullName.setText(_DNAME);
//        txt_username.setText(_DUSERNAME);
//        fullName.getEditText().setText(_DNAME);
//        username.getEditText().setText(_DUSERNAME);
//        email.getEditText().setText(_DEMAIL);
//        phoneNo.getEditText().setText(_DPHONENO);
//        password.getEditText().setText(_DPASSWORD);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Donors");

        //checking the entered username with username in the database
        Query checkUser = reference.orderByChild("username").equalTo(_DUSERNAME);

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

                    _DNAME =  snapshot.child(_DUSERNAME).child("name").getValue(String.class);
                    _DEMAIL = snapshot.child(_DUSERNAME).child("email").getValue(String.class);
                    _DPHONENO = snapshot.child(_DUSERNAME).child("phoneno").getValue(String.class);
                    _DPASSWORD = snapshot.child(_DUSERNAME).child("password").getValue(String.class);

                    txt_fullName.setText(_DNAME);
                    txt_username.setText(_DUSERNAME);
                    fullName.getEditText().setText(_DNAME);
                    username.getEditText().setText(_DUSERNAME);
                    email.getEditText().setText(_DEMAIL);
                    phoneNo.getEditText().setText(_DPHONENO);
                    password.getEditText().setText(_DPASSWORD);

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
    }


    public void updateRaiser(View view) {
        //call Database only if any there is any data update
        if (isDNameChanged() || isDPasswordChanged() || isDEmailChanged() || isDPhoneChanged()) {
            Toast.makeText(this, "Data has been Updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Data is same and cannot be Updated", Toast.LENGTH_LONG).show();
        }
    }




    private boolean isDPasswordChanged() {
        if (!_DPASSWORD.equals(password.getEditText().getText().toString())) {
            reference.child(_DUSERNAME).child("password").setValue(password.getEditText().getText().toString());
            _DPASSWORD = password.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isDNameChanged() {
        if (!_DNAME.equals(fullName.getEditText().getText().toString())) {
            reference.child(_DUSERNAME).child("name").setValue(fullName.getEditText().getText().toString());
            _DNAME = fullName.getEditText().getText().toString();
            txt_fullName.setText(_DNAME);
            return true;
        } else {
            return false;
        }
    }

    private boolean isDEmailChanged() {
        if (!_DEMAIL.equals(email.getEditText().getText().toString())) {
            reference.child(_DUSERNAME).child("email").setValue(email.getEditText().getText().toString());
            _DEMAIL = email.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isDPhoneChanged() {
        if (!_DPHONENO.equals(phoneNo.getEditText().getText().toString())) {
            reference.child(_DUSERNAME).child("phoneno").setValue(phoneNo.getEditText().getText().toString());
            _DPHONENO = phoneNo.getEditText().getText().toString();
            return true;
        } else {
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
        return true;
    }


}