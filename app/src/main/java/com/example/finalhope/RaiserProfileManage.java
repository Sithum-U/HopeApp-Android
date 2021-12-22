package com.example.finalhope;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RaiserProfileManage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Button myProfile, myFunds, myEvents;
    ImageView profileImage;
    private ProgressBar progressBar;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("RaiserImage");
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;


    String _USERNAME;
    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raiser_profile_manage);

        drawerLayout = findViewById(R.id.drawer_layout_Rprofile_manage);
        navigationView = findViewById(R.id.nav_view_Rpro_manage);
        toolbar = findViewById(R.id.toolbar_Rpro_manage);
        myProfile = findViewById(R.id.btn_myProfile);
        myFunds = findViewById(R.id.btn_myFunds);
        myEvents = findViewById(R.id.btn_myEvents);

        progressBar = findViewById(R.id.progressBar);
        //changePic = findViewById(R.id.change_pic);
        //profileImage = findViewById(R.id.profile_manage_image);

        progressBar.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        //_NAME = intent.getStringExtra("fullName");
        _USERNAME = intent.getStringExtra("username");
        type = intent.getStringExtra("type");


//        int postCount = 0;
//
//        Button btn_pCount;
//        TextView tvCount;
//
//        //Hooks
//        tvCount = (TextView) findViewById(R.id.view_pCount);
//        btn_pCount = (Button) findViewById(R.id.btn_postCount);
//
//        //
//        btn_pCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                postCount++;
//                //
//                tvCount.setText(Integer.toString(postCount++));
//            }

        /*-----------------------Tool Bar------------------*/
//        setSupportActionBar(toolbar);

        /*-------------------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //make menu clickable
        navigationView.setNavigationItemSelectedListener(this);

        //When application launches, direct to the home
        //navigationView.setCheckedItem(R.id.nav_home);

//        profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent galleryIntent = new Intent();
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                //get all type of images
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent, 2);
//
//            }
//        });
//
//        changePic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (imageUri != null) {
//
//                    UploadToFirebase(imageUri);
//
//                } else {
//                    Toast.makeText(RaiserProfileManage.this, "Please select image", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

        myEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(RaiserProfileManage.this,EventListActivity.class));
                //creating an intent
                Intent intent = new Intent(getApplicationContext(), EventListActivity.class);

                //putting artist name and id to intent
//                intent.putExtra(EVENT_ID, event.getEventId());
//                intent.putExtra(EVENT_NAME, event.getEventName());
                intent.putExtra("username",_USERNAME);
                intent.putExtra("type","Raiser");

                String TAG = "MyActivity";
                Log.i(TAG, "RaiserToEventList.eventID " + _USERNAME);

                //starting the activity with intent
                startActivity(intent);
            }
        });

        myFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(RaiserProfileManage.this,EventListActivity.class));
                //creating an intent
                Intent intent = new Intent(getApplicationContext(), FundListActivity.class);

                //putting artist name and id to intent
//                intent.putExtra(EVENT_ID, event.getEventId());
//                intent.putExtra(EVENT_NAME, event.getEventName());
                intent.putExtra("username",_USERNAME);
                intent.putExtra("type","Raiser");

                String TAG = "MyActivity";
                Log.i(TAG, "RaiserToFundList.eventID " + _USERNAME);

                //starting the activity with intent
                startActivity(intent);
            }
        });
    }

    //If nav menu is open and when we press back button instead of going back to the previous activity, just close the nav menu
//    @Override
//    public void onBackPressed() {
//        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        }
//        else {
//            super.onBackPressed();
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            profileImage.setImageURI(imageUri);

        }
    }

    private void UploadToFirebase(Uri uri) {

        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Model model = new Model(uri.toString());
                        String modelId = root.push().getKey();
                        root.child(modelId).setValue(model);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RaiserProfileManage.this, "Uploaded Successfully", Toast.LENGTH_SHORT);


                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(RaiserProfileManage.this, "Uploading Failed!!!", Toast.LENGTH_SHORT).show();

            }


        });

    }

    private String getFileExtension(Uri mUri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
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
        return false;
    }

    public void directToRaiserProfile(View view) {
        Intent intent = new Intent(getApplicationContext(),RaiserProfile.class);
        intent.putExtra("type","Raiser");
        intent.putExtra("username",_USERNAME);
        startActivity(intent);
    }

}