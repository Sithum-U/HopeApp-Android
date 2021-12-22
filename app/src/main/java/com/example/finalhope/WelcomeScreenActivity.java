package com.example.finalhope;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WelcomeScreenActivity extends AppCompatActivity {

    Button btnDonor, btnRaiseAFund, btnLogin;
    ImageView btnHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        btnDonor = findViewById(R.id.btnDonor);
        //btnLogin = findViewById(R.id.btnLogin);
        btnRaiseAFund = findViewById(R.id.btnRaiseFund);
        //fabHope = findViewById(R.id.fabHope);
        btnHelp = findViewById(R.id.btnHelp);


        btnDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeScreenActivity.this,DonorLogin.class);
                startActivity(intent);
            }
        });

        btnRaiseAFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeScreenActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(WelcomeScreenActivity.this,DonorLogin.class);
//                startActivity(intent);
//            }
//        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://www.facebook.com/100003812859148/posts/2300574440079629/?d=n");
            }
        });

    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent (Intent.ACTION_VIEW,uri));
    }
}