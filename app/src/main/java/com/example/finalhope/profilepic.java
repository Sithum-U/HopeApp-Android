package com.example.finalhope;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class profilepic extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //widgets
    private Button saveBtn, closeBtn;
    private ImageView imageView2;
    private ProgressBar progressBar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    String type;
    String _DUSERNAME;
    //counters
    private int mCounter = 0;
    Button btn;
    TextView tx;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepic);

        //all the ids
        saveBtn = findViewById(R.id.button4);
        closeBtn = findViewById(R.id.button6);
        imageView2 = findViewById(R.id.imageView2);
        progressBar = findViewById(R.id.progressBar);

        drawerLayout = findViewById(R.id.drawer_proPic);
        navigationView = findViewById(R.id.nav_proPic);
        toolbar = findViewById(R.id.toolbar_proPic);

        /*-------------------Navigation Drawer Menu-----------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //make menu clickable
        navigationView.setNavigationItemSelectedListener(this);
//counters
//        tx = (TextView) findViewById(R.id.tx);
//        btn = (Button) findViewById(R.id.bt);

        progressBar.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        _DUSERNAME = intent.getStringExtra("username");
        type = intent.getStringExtra("type");

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profilepic.this, donor_profilemanagement.class);
                intent.putExtra("username",_DUSERNAME);
                intent.putExtra("type","Donor");
                startActivity(intent);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                //get all type of images
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageUri != null) {

                    UploadToFirebase(imageUri);
                    Intent intent = new Intent(profilepic.this, donor_profilemanagement.class);
                    intent.putExtra("username",_DUSERNAME);
                    intent.putExtra("type","Donor");

                } else {
                    Toast.makeText(profilepic.this, "Please select image", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //counters
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mCounter++;
//                tx.setText(Integer.toString(mCounter));
//            }
//        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            imageView2.setImageURI(imageUri);

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
                        Toast.makeText(profilepic.this, "Uploaded Successful", Toast.LENGTH_SHORT);


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
                Toast.makeText(profilepic.this, "Uploading Failed!!!", Toast.LENGTH_SHORT).show();

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