package com.example.sony.group09_hw08;

/**
 * Created by Sony on 4/19/2016.
 */
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessagesAdapter extends ArrayAdapter {
    List<Message> mData;
    Context mContext;
    int mResource;
    User sender;
    ImageView imageView;
    Firebase mRef;
    User receiver;

    public MessagesAdapter(Context context, int resource, List objects, User sender, User receiver, Firebase ref) {
        super(context, resource, objects);
        this.mData = objects;
        this.mContext = context;
        this.mResource = resource;
        this.sender = sender;
        this.receiver= receiver;
        Firebase.setAndroidContext(mContext);
        mRef = ref;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.mResource, parent, false);
        }
        final Message msg = mData.get(position);
        TextView textView;
        textView = (TextView) convertView.findViewById(R.id.messages_title);
        if(msg.getSender().equals(sender.getId())) {
            textView.setText(sender.getFullName());
        } else {
            textView.setText(receiver.getFullName());
        }
        textView = (TextView) convertView.findViewById(R.id.message_content);
        textView.setText(msg.getMessageText());
        textView = (TextView) convertView.findViewById(R.id.message_time);
        textView.setText(msg.getTimeStamp());
        imageView = (ImageView) convertView.findViewById(R.id.delete_image);
        imageView.setVisibility(View.INVISIBLE);
        if(msg.getSender().equals(sender.getId())){
            convertView.setBackgroundColor(Color.parseColor("#FFD6D1D1"));
            imageView.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(R.drawable.ic_action_name).resize(40,40).into(imageView);
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MessagesAdapter","DeleteClicked");
                final Query qref =  mRef.orderByChild("uid").equalTo(msg.getUid());
                qref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //Message match_msg = dataSnapshot.getValue(Message.class);
                        dataSnapshot.getRef().removeValue();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });
        return convertView;
    }
}
