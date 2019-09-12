package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddProducts extends AppCompatActivity {

   // private String CategoryName;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDescription, InputproductPrice,InputProductCategory;
    private StorageReference productImageRef;
    private FirebaseDatabase productInfodata=FirebaseDatabase.getInstance();
    private DatabaseReference productInfoRef=productInfodata.getReference().child("Categories");
    private int value=1;
    private Uri imageuri;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String key;
    private String downloadImageUri;
    private String name;
    private String description ;
    private String price ;
    private String category;
    private String imageUri;
    private ProgressDialog loadingBar;
    private int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        //CategoryName = getIntent().getExtras().get("Category").toString();

        AddNewProductButton = findViewById(R.id.add_product_button);
        InputProductImage = findViewById(R.id.add_product_image);
        InputProductName = findViewById(R.id.add_product_name);
        InputProductDescription = findViewById(R.id.add_product_description);
        InputproductPrice = findViewById(R.id.add_product_price);
        InputProductCategory=findViewById(R.id.category_name);
        productImageRef= FirebaseStorage.getInstance().getReference().child("Product Image");
        loadingBar=new ProgressDialog(AddProducts.this);
        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryIntent();
        flag=0;


            }
        });

    }



    private void GalleryIntent() {

        Intent galleryIntent =new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,value);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==value&&resultCode==RESULT_OK&&data!=null)
        {
            imageuri=data.getData();
            InputProductImage.setImageURI(imageuri);



        }

    }
    public void SaveProduct (View view)
    {
        checkIfAnyDetailIsEmpty();
    }

    private void checkIfAnyDetailIsEmpty() {
        name =InputProductName.getText().toString();
        description =InputProductDescription.getText().toString();
        price =InputproductPrice.getText().toString();
        category= InputProductCategory.getText().toString();


        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Enter Product Name", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Enter Product Description", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(price))
        {
            Toast.makeText(this, "Enter Product Price", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(category))
        {
            Toast.makeText(this, "Enter Product Company", Toast.LENGTH_SHORT).show();
        }

        else if(imageuri==null)
        {
            Toast.makeText(this, "Please Add Product Image", Toast.LENGTH_SHORT).show();
        }
        else
        { imageUri=imageuri.toString();
        ExistingProductChecker();
        loadingBar.setTitle("Adding Products");
        loadingBar.setMessage("Please Wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        }
    }
    private void ExistingProductChecker() {
        if (flag != 1) {
            productInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!(dataSnapshot.child(category).exists()))
                    {
                        Toast.makeText(AddProducts.this, "Category Not Valid", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                    else if (dataSnapshot.child(category).child("Products").child(name).exists()) {
                        Toast.makeText(AddProducts.this, "Product Already Exists", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else {
                        createImageKey();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void createImageKey() {

        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,YYYY");
        saveCurrentDate =currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        key=saveCurrentDate + saveCurrentTime;

        final StorageReference filePath= productImageRef.child(imageuri.getLastPathSegment()+key+".jpg");
        final UploadTask uploadTask= filePath.putFile(imageuri);
        filePath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadImageUri=uri.toString().trim();
                        //Toast.makeText(AddProducts.this,downloadImageUri, Toast.LENGTH_SHORT).show();
                        AddProductToDatabase();
                    }
                });

            }
        });
    }

    private void AddProductToDatabase() {


                    final HashMap<String,Object> productInfo=new HashMap<>();
                    productInfo.put("Name",name);
                    productInfo.put("Description",description);
                    productInfo.put("Price",price);
                    productInfo.put("Category",category);
                    productInfo.put("image_uri",downloadImageUri);

                    productInfoRef.child(category).child("Products").child("Name").child(name).updateChildren(productInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(AddProducts.this, "Product Added", Toast.LENGTH_SHORT).show();
                                        productInfo.clear();
                                        loadingBar.dismiss();
                                        flag=1;
                                    }
                                    else
                                    {
                                        Toast.makeText(AddProducts.this, "Network Error", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });


//                else
//                {
//                    Toast.makeText(AddProducts.this, "Product Already Exists", Toast.LENGTH_SHORT).show();
//                    loadingBar.dismiss();
//                }
    }


}

