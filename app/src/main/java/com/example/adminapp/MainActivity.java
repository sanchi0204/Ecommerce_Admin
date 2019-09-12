package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    //Button login;
    FirebaseDatabase admindata=FirebaseDatabase.getInstance();
    DatabaseReference adminInfo=admindata.getReference().child("Admin Data");
    ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username =(EditText)findViewById(R.id.user_name);
        password=(EditText)findViewById(R.id.password);
        loadingBar=new ProgressDialog(MainActivity.this);

    }

    public void ifAnyDetailIsEmpty(View view ) {
        if(TextUtils.isEmpty(username.toString().trim()))
        {
            Toast.makeText(this, "Please Enter userame", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password.toString().trim()))
        {
            Toast.makeText(this, "Enter the password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            CheckUsernameAndPassword();
            loadingBar.setTitle("Checking in our data");
            loadingBar.setMessage("Please Wait");
            loadingBar.show();
        }
    }

    private void CheckUsernameAndPassword() {

        adminInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(username.getText().toString().trim()).exists())
                {
                    AdminValues admindata=dataSnapshot.child(username.getText().toString().trim()).getValue(AdminValues.class);
                        String a=password.getText().toString();
                    if(admindata.getPassword().equals(password.getText().toString()))
                    {
                        startActivity(new Intent(MainActivity.this,AdminCategory.class));
                        Toast.makeText(MainActivity.this, "Admin Logged In", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Not an Admin", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
