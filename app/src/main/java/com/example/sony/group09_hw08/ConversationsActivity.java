package com.example.sony.group09_hw08;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class ConversationsActivity extends AppCompatActivity {

    Firebase app_root = new Firebase("https://group09hw8.firebaseIO.com");
    Firebase users_root = app_root.child("users");
    Firebase messages_root = app_root.child("messages");
    ListView lv;
    ArrayList<User> contacts = new ArrayList<User>();
    ContactsAdapter adapter;
    AuthData authData;
    User user;
    static String USER_KEY = "user";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.editProfile:
                Intent i = new Intent(ConversationsActivity.this, EditProfileActivity.class);
                startActivity(i);
                ConversationsActivity.this.finish();
                break;
            case R.id.logOut:
                app_root.unauth();
                i = new Intent(ConversationsActivity.this, LoginActivity.class);
                startActivity(i);
                ConversationsActivity.this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        Firebase.setAndroidContext(this);
        lv = (ListView) findViewById(R.id.listView);
        authData = app_root.getAuth();
        if (authData != null) {
            Log.d("d", "user is authenticated");
            adapter = new ContactsAdapter(ConversationsActivity.this, R.layout.contact_layout, contacts, messages_root, authData);
            adapter.setNotifyOnChange(true);
            Log.d("ConversationsActivity", "On Create number of users " + contacts.size());
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    User user = contacts.get(position);
                    Log.d("d", "sending user to messages activity " + user.toString());
                    Intent intent = new Intent(ConversationsActivity.this, ViewMessages.class);
                    intent.putExtra(USER_KEY, user);
                    startActivity(intent);
                }
            });

            displayContacts();
        } else {
            Log.d("ConversationsActivity", "User is not authenticated!");
            Toast.makeText(ConversationsActivity.this, "User Not Authenticated :p Please sign in again",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ConversationsActivity.this,LoginActivity.class);
            startActivity(i);
            ConversationsActivity.this.finish();
        }
    }
    private void displayContacts() {
        users_root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("ConversationsActivity", "on data changed displaying contacts");
                adapter.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    if (!user.getId().equals(authData.getUid())) {
                        adapter.add(user);
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}