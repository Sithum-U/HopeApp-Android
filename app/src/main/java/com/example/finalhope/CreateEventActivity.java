package com.example.finalhope;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalhope.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {

    //we will use these constants later to pass the artist name and id to another activity
    // public static final String ARTIST_NAME = "com.example.artists.artistname";
    //public static final String ARTIST_ID = "com.example.artists.artistid";

    String USER_ID;
    String eventId;
    String type;

    EditText eventName, eventDate, eventVenue, eventDescription,
                eventContactName, eventContactNumber, eventContactNIC,
                eventContactMail;
    Spinner eventCategory;

    Button btncreateEvent,btnClose;
    Uri imageUri;
    ProgressBar progressBar;
    Event event;


    private String mEventDate,mtoday;
    DatePickerDialog.OnDateSetListener dateSetListener;

    DatabaseReference eventRef;
    private StorageReference eventStorageRef = FirebaseStorage.getInstance().getReference();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        //getting the reference of artists node
        eventRef = FirebaseDatabase.getInstance().getReference("events");


        eventName = findViewById(R.id.et_eventName);
        eventDate = findViewById(R.id.et_eventDate);
        eventVenue = findViewById(R.id.et_eventVenue);
        eventDescription = findViewById(R.id.etml_eventDescription);
        eventContactName = findViewById(R.id.et_contact_name);
        eventContactNumber = findViewById(R.id.et_contact_phone);
        eventContactMail = findViewById(R.id.et_contactEmail);
        eventContactNIC = findViewById(R.id.et_contactNic);
        eventCategory = findViewById(R.id.sp_eventCategory);
        btncreateEvent = findViewById(R.id.btn_submitEvent);
        btnClose = findViewById(R.id.btnClose);
        //eventCoverImage = findViewById(R.id.ib_eventCoverImg);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        USER_ID = intent.getStringExtra("username");
        type = intent.getStringExtra("type");


//        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.eventCategories));
//
//        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        eventCategory.setAdapter(myAdapter);

//        progressBar.setVisibility(View.INVISIBLE);

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.eventCategories)){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventCategory.setAdapter(myAdapter);


        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        mtoday = simpleDateFormat.format(Calendar.getInstance().getTime());


        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),dateSetListener,year,month,day);
               // datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                mEventDate = dayOfMonth + "/" + month + "/" + year;
                //dob_txt.setText("Birthday "+ mbirthday);
                eventDate.setText(mEventDate);
            }
        };

        //adding an onclicklistener to button
        btncreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addArtist()
                //the method is defined below
                //this method is actually performing the write operation
                addEvent();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(CreateEventActivity.this , EventListActivity.class));
                //creating an intent
                Intent intent = new Intent(getApplicationContext(), EventListActivity.class);

                //putting artist name and id to intent
                //intent.putExtra("eventId",eventId);
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

//        eventCoverImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent galleryIntent = new Intent();
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                //get all type of images
//                galleryIntent.setType("image/*");
//
//            }
//        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==2 && resultCode == RESULT_OK && data != null) {
//
//            imageUri = data.getData();
//            eventCoverImage.setImageURI(imageUri);
//
//        }
//    }

    /*
     * This method is saving a new artist to the
     * Firebase Realtime Database
     * */
    private void addEvent() {
        //getting the values to save
        String event_name = eventName.getText().toString().trim();
        //String event_date = eventDate.getText().toString().trim();
        String event_date = mEventDate;
        String event_venue = eventVenue.getText().toString().trim();
        String event_description = eventDescription.getText().toString().trim();
        String event_contact_name = eventContactName.getText().toString().trim();
        String event_contact_number = eventContactNumber.getText().toString().trim();
        String event_contact_mail = eventContactMail.getText().toString().trim();
        String event_contact_nic= eventContactNIC.getText().toString().trim();
        String event_category = eventCategory.getSelectedItem().toString();

        /*
         * this line is important
         * this time we are not getting the reference of a direct node
         * but inside the node track we are creating a new child with the artist id
         * and inside that node we will store all the tracks with unique ids
         * */

        DatabaseReference eventRef;
        eventRef = FirebaseDatabase.getInstance().getReference("events");
        //checking if the value is provided
        if (!TextUtils.isEmpty(event_name) && !TextUtils.isEmpty(event_date) &&
                !TextUtils.isEmpty(event_venue) && !TextUtils.isEmpty(event_description) &&
                !TextUtils.isEmpty(event_contact_name) && !TextUtils.isEmpty(event_contact_number) &&
                !TextUtils.isEmpty(event_contact_mail) && !TextUtils.isEmpty(event_contact_nic) &&
                !(eventCategory.getSelectedItemPosition() == 0) && !(mEventDate == null) && validateEmail() && validatePhoneNo() ) {


             eventId = eventRef.push().getKey();
//            StorageReference fileRef = eventStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
//            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//
////                            Model model =new Model(uri.toString());
////                            String modelId = root.push().getKey();
////                            root.child(eventId).setValue(model);
//
//                             event = new Event( eventId,  event_name,  event_date,
//                                    event_venue,  event_description, event_category, event_contact_name,
//                                    event_contact_number,  event_contact_mail,  event_contact_nic, uri.toString());
//
//                            progressBar.setVisibility(View.INVISIBLE);
//                            Toast.makeText(CreateEventActivity.this,"Success",Toast.LENGTH_SHORT).show();
//                            //imageView1.setImageResource(R.drawable.ic_launcher_background);
//
//                        }
//                    });
//
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//
//                    progressBar.setVisibility(View.VISIBLE);
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//
//                    progressBar.setVisibility(View.INVISIBLE);
//                    Toast.makeText(CreateEventActivity.this,"Uploading Failed!!!",Toast.LENGTH_SHORT).show();
//
//                }
//            });

            //creating an Artist Object
            Event event = new Event( eventId,  event_name,  event_date,
                     event_venue,  event_description, event_category, event_contact_name,
                     event_contact_number,  event_contact_mail,  event_contact_nic,USER_ID);

            //Saving the Artist
            eventRef.child(eventId).setValue(event);

            //setting edittext to blank again
            //editTextName.setText("");
            //editTextType.setText("");

            //displaying a success toast
            Toast.makeText(this, "Event added", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,EventListActivity.class );
            if(type.equals("Donor")) {
                intent.putExtra("type","Donor");
            }
            if(type.equals("Raiser")) {
                intent.putExtra("type","Raiser");
            }
            intent.putExtra("eventId",eventId);
            intent.putExtra("username",USER_ID);
            startActivity(intent);
            //startActivity(new Intent(CreateEventActivity.this , SinglePostActivity.class));
        } else {
            //if the value is not given displaying a toast
            if(eventCategory.getSelectedItemPosition() == 0){
                Toast.makeText(this, "Please select an event category", Toast.LENGTH_LONG).show();
            }
            if(TextUtils.isEmpty(event_name))
            Toast.makeText(this, "Please fill the event name", Toast.LENGTH_LONG).show();
//            if(TextUtils.isEmpty(event_date))
//                Toast.makeText(this, "Please fill the event date", Toast.LENGTH_LONG).show();
            if(TextUtils.isEmpty(event_venue))
                Toast.makeText(this, "Please fill the event venue", Toast.LENGTH_LONG).show();
            if(TextUtils.isEmpty(event_description))
                Toast.makeText(this, "Please fill the event description", Toast.LENGTH_LONG).show();
            if (mEventDate == null)
                Toast.makeText(getApplicationContext(), "Please select event date", Toast.LENGTH_SHORT).show();
            if(!validateEmail()){
                Toast.makeText(getApplicationContext(), "Invalid email!", Toast.LENGTH_SHORT).show();
            }
            if(!validatePhoneNo()){
                Toast.makeText(getApplicationContext(), "Invalid phone number!", Toast.LENGTH_SHORT).show();
            }
        }

    }



//    private String getFileExtension(Uri mUri) {
//
//        ContentResolver cr = getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(cr.getType(mUri));
//    }

    private Boolean validateEmail(){
        String val = eventContactMail.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            return false;
        }
        else if(!val.matches(emailPattern)) {

            return false;
        }
        else {

            return true;
        }
    }

    private Boolean validatePhoneNo(){
        String val = eventContactNumber.getText().toString();

        String phoneNo =  "^"+ "(?=.*[0-9])" +"(?=\\S+$)"+ ".{10,10}" +"$";

        if (val.isEmpty()) {
            return false;
        }
        else if(!val.matches(phoneNo)) {
            return false;
        }
        else {

            return true;
        }
    }

//    private Boolean validateNic(){
//        String val = eventContactNumber.getText().toString();
//
//        String phoneNo =  "^"+ "(?=.*[0-9])" +"(?=\\S+$)"+ ".{10,10}" +"$";
//
//        if (val.isEmpty()) {
//            return false;
//        }
//        else if(!val.matches(phoneNo)) {
//            return false;
//        }
//        else {
//
//            return true;
//        }
//    }

}