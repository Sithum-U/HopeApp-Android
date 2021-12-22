package com.example.finalhope;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class DonorLogin extends AppCompatActivity {
    Button callSignUp, loginbtn;
    TextInputLayout Dusername, Dpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_donor_login);

        //Hooks

        Dusername = findViewById(R.id.username4);
        Dpassword = findViewById(R.id.password4);
        loginbtn = findViewById(R.id.loginbtn);
        callSignUp = findViewById(R.id.login_signup_trans);

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonorLogin.this, donor_signup.class);
                startActivity(intent);

//                Pair[] pairs = new Pair[5];
//                pairs[0] = new Pair<View,String>(logoText,"logo_text");
//                pairs[1] = new Pair<View,String>(username,"username_tran");
//                pairs[2] = new Pair<View,String>(password,"password_tran");
//                pairs[3] = new Pair<View,String>(login_btn,"btn_login_tran");
//                pairs[4] = new Pair<View,String>(callSignUp,"login_signup_tran");
//
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DonorLogin.this,pairs);
//
//                }
            }
        });
    }

    private Boolean validateDonorUsername(){
        String val = Dusername.getEditText().getText().toString();

        if(val.isEmpty()) {
            Dusername.setError("Field cannot be empty");
            return false;
        }
        else {
            Dusername.setError(null);
            Dusername.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateDonorPassword(){
        String val = Dpassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            Dpassword.setError("Field cannot be empty");
            return false;
        }
        else {
            Dpassword.setError(null);
            Dpassword.setErrorEnabled(false);
            return true;
        }
    }

    public void loginDonor(View view) {
        //validate login info
        if (!validateDonorUsername() | !validateDonorPassword()) {
            return;
        }
        else{
            isDonor();
        }
    }

    //method to check whether the user is in the system
    private void isDonor() {
        //getting user entered values
        String donorEnteredUsername = Dusername.getEditText().getText().toString().trim();
        String donorEnteredPassword = Dpassword.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Donors");

        //checking the entered username with username in the database
        Query checkUser = reference.orderByChild("username").equalTo(donorEnteredUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    //if 2nd time entered correctly
                    Dusername.setError(null);
                    Dusername.setErrorEnabled(false);

                    //if username exists, get the relevant password
                    String passwordFromDB = snapshot.child(donorEnteredUsername).child("password").getValue(String.class);

                    //checking the entered password with password in the database
                    if(passwordFromDB.equals(donorEnteredPassword)) {
                        //if 2nd time entered correctly
                        Dusername.setError(null);
                        Dusername.setErrorEnabled(false);

                        String nameFromDB = snapshot.child(donorEnteredUsername).child("name").getValue(String.class);
                        String usernameFromDB = snapshot.child(donorEnteredUsername).child("username").getValue(String.class);
                        String emailFromDB = snapshot.child(donorEnteredUsername).child("email").getValue(String.class);
                        String phoneFromDB = snapshot.child(donorEnteredUsername).child("phoneno").getValue(String.class);

                        Intent intent = new Intent(DonorLogin.this, HopeHomeActivty.class);

                        intent.putExtra("name",nameFromDB);
                        intent.putExtra("username",usernameFromDB);
                        intent.putExtra("type","Donor");
                        intent.putExtra("email",emailFromDB);
                        intent.putExtra("phoneno",phoneFromDB);
                        intent.putExtra("password",passwordFromDB);

                        startActivity(intent);

                    }
                    else {
                        Dpassword.setError("Wrong Password");
                        Dpassword.requestFocus(); //automatically request the focus and start pointing on specific field
                    }
                }
                else {
                    Dusername.setError("No such User exist");
                    Dusername.requestFocus();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }
}