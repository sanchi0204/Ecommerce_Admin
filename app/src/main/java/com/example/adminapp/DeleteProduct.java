package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteProduct extends AppCompatActivity {
    EditText deleteProduct;
    EditText CategoryName;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);
        deleteProduct=findViewById(R.id.delete_product_name);
        CategoryName=findViewById(R.id.delete_product_categoryname);
        loadingBar=new ProgressDialog(this);
    }
    public void DeleteProduct(View view)
    {
        loadingBar.setTitle("Deleting Product");
        loadingBar.setMessage("Please Wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        final DatabaseReference prodref= FirebaseDatabase.getInstance().getReference().child("Categories");
        prodref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child(CategoryName.getText().toString()).exists()))
                {
                    Toast.makeText(DeleteProduct.this, "Category Dosen't Exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else if(!(dataSnapshot.child(CategoryName.getText().toString()).child("Products").child("Name").child(deleteProduct.getText().toString()).exists()))
                {
                    Toast.makeText(DeleteProduct.this, "Product Dosen't Exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else
                {
                    prodref.child(CategoryName.getText().toString()).child("Products").child("Name").child(deleteProduct.getText().toString()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(DeleteProduct.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(DeleteProduct.this, "Network Error", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
