package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteCategory extends AppCompatActivity {
    EditText deleteCategory;
    ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_category);
        deleteCategory=(EditText)findViewById(R.id.delete_category_name);
        loadingbar=new ProgressDialog(this);
    }
    public void Deletecategory(View view)
    {
        loadingbar.setTitle("Deleting Category");
        loadingbar.setMessage("Please Wait");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        final DatabaseReference prodref= FirebaseDatabase.getInstance().getReference().child("Categories");

        prodref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(deleteCategory.getText().toString()).exists())
                {
                    prodref.child(deleteCategory.getText().toString()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(DeleteCategory.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(DeleteCategory.this, "Network Error", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(DeleteCategory.this, "Category is not Present", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
