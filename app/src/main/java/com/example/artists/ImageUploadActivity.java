package com.example.artists;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.artists.adapters.ImageViewAdapter;
import com.example.artists.model.Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class ImageUploadActivity extends AppCompatActivity {

    //widgets
    private Button UploadBtn;
    private ImageView imageView1;
    private ProgressBar progressBarID;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;
    private RecyclerView recyclerView;
    private ArrayList<Model> list;

    private ImageViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        //all the ids
        UploadBtn = findViewById(R.id.UploadBtn);
        //Showbtn = findViewById(R.id.Showbtn);
        progressBarID = findViewById(R.id.progressBarID);
        imageView1 = findViewById(R.id.imageView1);

        progressBarID.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.imgRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new ImageViewAdapter(this , list);
        recyclerView.setAdapter(adapter);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                //get all type of images
                galleryIntent.setType("image/*");

            }
        });

        UploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageUri != null) {

                    UploadToFirebase(imageUri);

                }else {
                    Toast.makeText(ImageUploadActivity.this,"Please select image",Toast.LENGTH_SHORT).show();
                }

            }
        });


//        root.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Model model = dataSnapshot.getValue(Model.class);
//                    list.add(model);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    protected void onStart() {
        super.onStart();
        //attaching value event listener
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressBarID.setVisibility(View.VISIBLE);


                //clearing the previous artist list
                list.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Model model = dataSnapshot.getValue(Model.class);
                    list.add(model);
                }

                //creating adapter
                adapter = new ImageViewAdapter(ImageUploadActivity.this, list);
                //attaching adapter to the listview
                adapter.notifyDataSetChanged();

                progressBarID.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==2 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            imageView1.setImageURI(imageUri);

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

                        Model model =new Model(uri.toString());
                        String modelId = root.push().getKey();
                        root.child(modelId).setValue(model);

                        progressBarID.setVisibility(View.INVISIBLE);
                        Toast.makeText(ImageUploadActivity.this,"Success",Toast.LENGTH_SHORT).show();
                        //imageView1.setImageResource(R.drawable.common_google_signin_btn_text_dark_normal_background);

                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                progressBarID.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBarID.setVisibility(View.GONE);
                Toast.makeText(ImageUploadActivity.this,"Uploading Failed!!!",Toast.LENGTH_SHORT).show();

            }
        });

    }

    private String getFileExtension(Uri mUri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}