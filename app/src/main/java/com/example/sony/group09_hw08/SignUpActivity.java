package com.example.sony.group09_hw08;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Get the buttons and set on click listeners
        Button signup = (Button) findViewById(R.id.create);
        Button cancel = (Button) findViewById(R.id.cancel);
        signup.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    //Button Click listeners
    @Override
    public void onClick(View v) {
        int id = v.getId();
        {
            switch (id){
                case R.id.create:
                    Log.d("SignUpActivity", "SignUp Clicked");

                    //get all the resources
                    EditText name = (EditText) findViewById(R.id.name);
                    EditText email = (EditText) findViewById(R.id.login_email);
                    EditText password = (EditText) findViewById(R.id.signup_password);
                    EditText confirm_password = (EditText) findViewById(R.id.singup_confirm_password);
                    EditText phonenumber = (EditText) findViewById(R.id.phonenumber);
                    String name_text = name.getText().toString().trim();
                    String email_text = email.getText().toString().trim();
                    String password_text = password.getText().toString();
                    String confirm_password_text = confirm_password.getText().toString();
                    String phonenumber_text = phonenumber.getText().toString().trim();

                    //Some mandatory field checks
                    if(name_text.isEmpty() || email_text.isEmpty() || password_text.isEmpty() || confirm_password_text.isEmpty() || phonenumber_text.isEmpty()){
                        Toast.makeText(SignUpActivity.this,"All fields are mandatory to sign up!",Toast.LENGTH_LONG).show();
                        break;
                    }

                    //password and confirm password check
                    if(!password_text.equals(confirm_password_text)){
                        Toast.makeText(SignUpActivity.this,"Passwords don't match!",Toast.LENGTH_LONG).show();
                        break;
                    }
                    // Create user object to add
                    User u = new User();
                    u.setEmail(email_text);
                    u.setFullName(name_text);
                    u.setId("");
                    u.setPassword(password_text);
                    u.setPhoneNumber(phonenumber_text);
                    u.setPicture("");
                    //Connect to firebase and add an authorized user
                    signupuser(email_text,password_text , u);
                    break;

                case R.id.cancel:
                    Log.d("SignUpActivity", "Cancel Clicked");
                    Intent i = new Intent(SignUpActivity.this,LoginActivity.class);
                    startActivity(i);
                    SignUpActivity.this.finish();
                    break;
            }
        }
    }

    private void signupuser(String email_text, String password_text, final User u) {
        final Firebase app_root = new Firebase(getString(R.string.firebase_root_url));
        app_root.createUser(email_text, password_text, new Firebase.ValueResultHandler<Map<String, Object>>(){
            @Override
            public void onSuccess(Map<String, Object> result) {
                System.out.println("Successfully created user account with uid: " + result.get("uid"));
                User user = u;
                user.setId(result.get("uid").toString());
                app_root.child("users").child(user.getId()).setValue(user, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        Log.d("SignUpActivity", "Created Successfully");
                        Toast.makeText(SignUpActivity.this, "User Created Successfully. Please login now",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        SignUpActivity.this.finish();
                    }
                });
            }
            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(SignUpActivity.this, "User couldn't be created :( Please try again",Toast.LENGTH_LONG).show();
            }
        });
    }
}
