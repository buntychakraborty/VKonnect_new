package com.example.vkonnect;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private EditText setUserName, setStatus;
    private Button updateSettings;
    private CircleImageView userProfileImage;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private String currentUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initializeItems();
        setUserName.setVisibility(View.INVISIBLE);

        updateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSetting();
            }
        });


        retrieveUserInformation();

    }


    private void initializeItems() {
        setUserName = (EditText) findViewById(R.id.set_user_name);
        setStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        updateSettings = (Button) findViewById(R.id.update_settings);
        mAuth = FirebaseAuth.getInstance();

        currentUid = mAuth.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();


    }

    private void updateSetting() {
        String setUserNameFor = setUserName.getText().toString();
        String setUserStatus = setStatus.getText().toString();

        if (TextUtils.isEmpty(setUserNameFor)) {
            Toast.makeText(this, "Please enter your username...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(setUserStatus)) {
            Toast.makeText(this, "Please enter your satus....", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUid);
            profileMap.put("name", setUserNameFor);
            profileMap.put("status", setUserStatus);
            rootRef.child("Users").child(currentUid).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                        Toast.makeText(getApplicationContext(), "Profile Updated Sucessfully....", Toast.LENGTH_SHORT).show();

                    } else {
                        String errorMessage = task.getException().toString();
                        Toast.makeText(getApplicationContext(), "Error : " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }


    }

    private void retrieveUserInformation() {


        rootRef.child("Users").child(currentUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))) {

                    String userName=dataSnapshot.child("name").getValue().toString();
                    String status=dataSnapshot.child("status").getValue().toString();
                    String image=dataSnapshot.child("image").getValue().toString();

                    setUserName.setText(userName);
                    setStatus.setText(status);
                   // userProfileImage.setB


                } else if (dataSnapshot.exists() && (dataSnapshot.hasChild("name"))) {
                    String userName=dataSnapshot.child("name").getValue().toString();
                    String status=dataSnapshot.child("status").getValue().toString();


                    setUserName.setText(userName);
                    setStatus.setText(status);


                } else {
                    setUserName.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(), "Please update your profile information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //cannot press back button to go back
        startActivity(intent);
        finish();
    }

}
