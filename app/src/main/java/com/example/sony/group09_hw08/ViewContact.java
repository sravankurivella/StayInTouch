package com.example.sony.group09_hw08;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewContact extends AppCompatActivity {
    TextView textview;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);
        user = (User) getIntent().getExtras().getSerializable(ConversationsActivity.USER_KEY);
        ImageView user_image = (ImageView) findViewById(R.id.userImage);
        if(user.getPicture() != null && !user.getPicture().equals("")) {
            byte[] decodedString = Base64.decode(user.getPicture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            user_image.setImageBitmap(decodedByte);
        } else {
            user_image.setImageResource(R.drawable.default_pic);
        }
        textview = (TextView) findViewById(R.id.contact_name);
        textview.setText(user.getFullName());
        textview = (TextView) findViewById(R.id.user_name);
        textview.setText(user.getFullName());
        textview = (TextView) findViewById(R.id.phone_number);
        textview.setText(user.getPhoneNumber());
        textview = (TextView) findViewById(R.id.email);
        textview.setText(user.getEmail());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ViewContact.this.finish();
    }
}
