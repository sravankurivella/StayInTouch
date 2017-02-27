package com.example.sony.group09_hw08;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Firebase app_root;
    ImageView profile_pic;
    EditText username,email, phonenumber, password;
    TextView displayname;
    Button update, cancel;
    User u;
    String to64, oldEmail, oldPass;
    AuthData authData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Firebase.setAndroidContext(this);
        //Firebase root url
        app_root = new Firebase(getString(R.string.firebase_root_url));


        //get all the UI Elements and the corresponding data.
        displayname = (TextView) findViewById(R.id.editprofile_displayname);
        profile_pic = (ImageView) findViewById(R.id.editprofile_image);
        username = (EditText) findViewById(R.id.editprofile_name);
        email = (EditText) findViewById(R.id.editprofile_email);
        phonenumber = (EditText) findViewById(R.id.editprofile_phonenumber);
        password = (EditText) findViewById(R.id.editprofile_password);
        update = (Button) findViewById(R.id.button_updateprofile);
        cancel = (Button) findViewById(R.id.button_cancel);
        update.setOnClickListener(this);
        cancel.setOnClickListener(this);


        //get user details from auth
        authData = app_root.getAuth();
        app_root.child("users").orderByChild("id").equalTo(authData.getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d("d", "snapshot " + snapshot);
                            u = snapshot.getValue(User.class);
                            Log.d("d", "user " + u.toString());
                        }

                        //Set the Values..
                        displayname.setText(u.getFullName());
                        username.setText(u.getFullName());
                        email.setText(u.getEmail());
                        phonenumber.setText(u.getPhoneNumber());
                        password.setText(u.getPassword());
                        if (u.getPicture() == null || u.getPicture().equals("")){
                            profile_pic.setImageResource(R.drawable.default_pic);
                        }
                        else{
                            byte[] decodedString = Base64.decode(u.getPicture(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            profile_pic.setImageBitmap(decodedByte);
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

        // On Click Listener to Change the profile picture
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        } );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_cancel) {
            Intent i = new Intent(EditProfileActivity.this, ConversationsActivity.class);
            startActivity(i);
            EditProfileActivity.this.finish();
        } else if (id == R.id.button_updateprofile) {

            if(username.getText().toString().equals("") ||username.getText().toString().equals(null) ||
                    password.getText().toString().equals("") ||password.getText().toString().equals(null) ||
                    phonenumber.getText().toString().equals("") ||phonenumber.getText().toString().equals(null) ||
                    email.getText().toString().equals("") ||email.getText().toString().equals(null))
            {
                Toast.makeText(EditProfileActivity.this, "All fields are mandatory! I am not going to update!!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("EditProfile", "Update Profile Clicked");
            u.setPicture(to64);
            u.setFullName(username.getText().toString());
            u.setPhoneNumber(phonenumber.getText().toString());

            String newemail = email.getText().toString();
            final String pass = password.getText().toString();
            oldEmail = u.getEmail();
            oldPass = u.getPassword();
            u.setEmail(newemail);
            u.setPassword(pass);

            if (!oldEmail.equals(newemail)) {
                app_root.changeEmail(oldEmail, oldPass, newemail, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        changePassword(pass);
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Log.d("d", "Error " + firebaseError.getMessage());
                        Toast.makeText(EditProfileActivity.this, "Error: " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
            } else if (!oldPass.equals(pass)) {
                changePassword(pass);
            } else {
                updateUser();
            }
        }
    }

    //Get The Image in base64 after choosing an image from the gallery.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView image = (ImageView) findViewById(R.id.editprofile_image);
        try {
            if (requestCode == requestCode && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                Log.d("d", "Image URI: " + selectedImage);

                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                image.setImageBitmap(bm);

                //Encoding image to base64
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                to64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.d("d", "Encoded String: " + to64);

            }
        }catch (IOException e){
            Toast.makeText(this, "An error occurred.", Toast.LENGTH_LONG).show();
        }

    }
    private void changePassword(String newPass){
        if(!oldPass.equals(newPass)) {
            app_root.changePassword(u.getEmail(), oldPass, newPass, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    updateUser();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Log.d("d", "Error " + firebaseError.getMessage());
                    Toast.makeText(EditProfileActivity.this, "Error: " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateUser();
        }
    }
    private void updateUser(){
        app_root.child("users").child(u.getId()).setValue(u, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Intent intent = new Intent(EditProfileActivity.this, ConversationsActivity.class);
                startActivity(intent);
                Toast.makeText(EditProfileActivity.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
