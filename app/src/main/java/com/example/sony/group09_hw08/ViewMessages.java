package com.example.sony.group09_hw08;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ViewMessages extends AppCompatActivity {
    Firebase app_root;
    ArrayList<Message> messages = new ArrayList<>();
    TextView input_messsage;
    User sender;
    User receiver;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);
        Firebase.setAndroidContext(this);
        app_root = new Firebase(getResources().getString(R.string.firebase_root_url));
        final Firebase messages_root = app_root.child("messages");
        list = (ListView) findViewById(R.id.messages_list);
        final AuthData authData= app_root.getAuth();
        if (authData == null){
            Log.d("ViewMessages", "User Not Authenticaed. Please Sign in Again");
            Intent i = new Intent(ViewMessages.this, LoginActivity.class);
            startActivity(i);
            ViewMessages.this.finish();
        }
        else{
            app_root.child("users").orderByChild("id").equalTo(authData.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("ViewMessages", "snapshot " + snapshot);
                        sender = snapshot.getValue(User.class);
                        Log.d("ViewMessages", "Sender" + sender.toString());
                    }
                    receiver = (User) getIntent().getExtras().getSerializable(ConversationsActivity.USER_KEY);
                    final MessagesAdapter adapter = new MessagesAdapter(ViewMessages.this, R.layout.message_layout, messages, sender, receiver, messages_root);
                    list.setAdapter(adapter);
                    messages_root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            adapter.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Message message = snapshot.getValue(Message.class);

                                if ((message.getSender().equals(sender.getId()) && message.getReceiver().equals(receiver.getId()) ||
                                        (message.getSender().equals(receiver.getId()) && message.getReceiver().equals(sender.getId())))) {
                                    Log.d("demo", "out cndn " + message.getMessageText() + "-" + message.isMessageRead());
                                    if (message.getReceiver().equals(sender.getId())) {
                                        Log.d("demo", "in -" + message.getReceiver() + " -----" + sender.getFullName());
                                        message.setMessageRead(true);
                                        messages_root.child(message.getUid()).setValue(message);
                                    }
                                    adapter.add(message);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }
        Button send = (Button) findViewById(R.id.button_send);
        assert send != null;
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_messsage = (TextView) findViewById(R.id.message_text);
                String text = input_messsage.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(ViewMessages.this, "Please Write a Message", Toast.LENGTH_SHORT).show();
                } else if (text.length() > 140) {
                    Toast.makeText(ViewMessages.this, "Message cannot be longer than 140 characters", Toast.LENGTH_SHORT).show();
                } else {
                    Message message = new Message();
                    message.setMessageText(input_messsage.getText().toString());
                    message.setSender(authData.getUid());
                    message.setReceiver(receiver.getId());
                    message.setMessageRead(false);
                    String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date());
                    message.setTimeStamp(timeStamp);
                    Log.d("ViewMessages", message.toString());
                    Firebase newMessage = app_root.child("messages").push();
                    message.setUid(newMessage.getKey());
                    newMessage.setValue(message, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            input_messsage.setText(null);
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            //Start View Contact Activity
            case R.id.view_contact:
                intent = new Intent(ViewMessages.this, ViewContact.class);
                intent.putExtra(ConversationsActivity.USER_KEY,receiver);
                startActivity(intent);
                break;

            //Call the receiver
            case R.id.call_contact:
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+receiver.getPhoneNumber()));
                startActivity(intent);
                break;
        }
        return true;
    }
}