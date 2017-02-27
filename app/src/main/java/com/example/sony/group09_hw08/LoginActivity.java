package com.example.sony.group09_hw08;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(this);
        Firebase app_root = new Firebase(getString(R.string.firebase_root_url));
        AuthData authData = app_root.getAuth();
        if(authData != null){
            Log.d("LoginActivity", "Auth Data found.. Routing to Conversations Activity");
            Intent i = new Intent(LoginActivity.this,ConversationsActivity.class);
            startActivity(i);
        }
        else{
            Log.d("LoginActivity", "Auth data not found... user must login");
        }



        //get the button ids and add OnClickListeners
        Button button_login = (Button) findViewById(R.id.login);
        Button button_signup = (Button) findViewById(R.id.signup);
        button_login.setOnClickListener(this);
        button_signup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.login){
            Log.d("LoginActivity", "Login Clicked");

            //get the email and password of the user
            EditText email = (EditText) findViewById(R.id.login_email);
            EditText password = (EditText) findViewById(R.id.login_password);
            String email_text = email.getText().toString().trim();
            String password_text = password.getText().toString().trim();

            //check if any field is empty
            if(email_text.equals("")){
                email.setError("field cannot be empty");
                return;
            }
            if (password_text.equals("")){
                password.setError("field cannot be empty");
                return;
            }

            //calling authwithpassword method to check if the user is valid
            Firebase app_root = new Firebase(getString(R.string.firebase_root_url));
            app_root.authWithPassword(email_text,password_text,authResultHandler);

        }
        else if(id == R.id.signup){
            Log.d("LoginActivity", "Signup Clicked");
            Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(i);
            LoginActivity.this.finish();
        }
    }
    Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
        // if auth is valid, get the details
        @Override
        public void onAuthenticated(AuthData authData) {
            Log.d("LoginActivity", "Logged in with "+authData.getUid());
            Intent i = new Intent(LoginActivity.this,ConversationsActivity.class);
            startActivity(i);
            LoginActivity.this.finish();
        }
        // handle invalid auth reasons
        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            switch (firebaseError.getCode()){
                case FirebaseError.USER_DOES_NOT_EXIST:
                    Toast.makeText(LoginActivity.this, "User doesn't exist!!",Toast.LENGTH_LONG ).show();
                    break;
                case FirebaseError.INVALID_PASSWORD:
                    Toast.makeText(LoginActivity.this, "Wrong password!!",Toast.LENGTH_LONG ).show();
                    break;
                default:
                    Toast.makeText(LoginActivity.this, "Auth Failed due to other reasons :(",Toast.LENGTH_LONG ).show();
                    break;
            }
        }
    };
}
