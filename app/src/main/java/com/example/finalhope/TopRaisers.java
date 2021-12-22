package com.example.finalhope;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TopRaisers extends AppCompatActivity {

    private Button myPosts;

    DatabaseReference fundReference;
    private FirebaseAuth mAuth;

    private String currentUserId;
    private int countPosts = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_raisers);

        mAuth = FirebaseAuth.getInstance();

        myPosts = findViewById(R.id.btn_postCount);
        fundReference = FirebaseDatabase.getInstance().getReference().child("Raise");
        currentUserId = mAuth.getCurrentUser().getUid();

        fundReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    countPosts = (int) snapshot.getChildrenCount();
                    myPosts.setText(Integer.toString(countPosts )+ " Posts");
                }
                else {
                    myPosts.setText("0 Posts");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}