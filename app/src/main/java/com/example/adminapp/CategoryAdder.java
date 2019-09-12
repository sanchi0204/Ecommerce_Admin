package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class CategoryAdder extends AppCompatActivity {



    // private String CategoryName;
//    private Button AddNewProductButton;
    private ImageView InputCategoryImage;
    private EditText InputCategoryName, InputCategoryDescription, InputCategoryPrice,InputCompany;
    private StorageReference categoryImageRef;
    private FirebaseDatabase categoryInfodata=FirebaseDatabase.getInstance();
    private DatabaseReference categoryInfoRef=categoryInfodata.getReference().child("Categories");
    private int value=1;
    private Uri imageuri;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String key;
    private String downloadImageUri;
    private String name;
    private String description ;
    private String price ;
    private String company;
    private String imageUri;
    private ProgressDialog loadingBar;
    private int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_adder);

        //CategoryName = getIntent().getExtras().get("Category").toString();

        ///AddNewProductButton = findViewById(R.id.add_product_button);
        InputCategoryImage = findViewById(R.id.add_category_image);
        InputCategoryName = findViewById(R.id.add_category_name);
        InputCategoryDescription = findViewById(R.id.add_category_description);
        InputCategoryPrice = findViewById(R.id.add_category_price);
        InputCompany=findViewById(R.id.add_company_details);
        categoryImageRef= FirebaseStorage.getInstance().getReference().child("Category Image");
        loadingBar=new ProgressDialog(CategoryAdder.this);
        InputCategoryImage.setOnClickListener(new View.OnClickListener() {
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
            InputCategoryImage.setImageURI(imageuri);



        }

    }
    public void SaveCategory (View view)
    {
        checkIfAnyDetailIsEmpty();
    }

    private void checkIfAnyDetailIsEmpty() {
        name =InputCategoryName.getText().toString();
        description =InputCategoryDescription.getText().toString();
        price =InputCategoryPrice.getText().toString();
        company= InputCompany.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Enter Category Name", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Enter Category Description", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(price))
        {
            Toast.makeText(this, "Enter category Price", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(company))
        {
            Toast.makeText(this, "Enter Company", Toast.LENGTH_SHORT).show();
        }

        else if(imageuri==null)
        {
            Toast.makeText(this, "Please Add Category Image", Toast.LENGTH_SHORT).show();
        }
        else
        {   imageUri=imageuri.toString();
            ExistingCategoryChecker();
            loadingBar.setTitle("Adding Category");
            loadingBar.setMessage("Please Wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
        }
    }
    private void ExistingCategoryChecker() {
        if (flag != 1) {
            categoryInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(InputCategoryName.getText().toString()).exists()) {
                        Toast.makeText(CategoryAdder.this, "category Already Exists", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
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

        final StorageReference filePath= categoryImageRef.child(imageuri.getLastPathSegment()+key+".jpg");
        final UploadTask uploadTask= filePath.putFile(imageuri);
        filePath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadImageUri=uri.toString().trim();
                        //Toast.makeText(AddProducts.this,downloadImageUri, Toast.LENGTH_SHORT).show();
                        AddCategoryToDatabase();
                    }
                });

            }
        });
    }

    private void AddCategoryToDatabase() {


        final HashMap<String,Object> categoryInfo=new HashMap<>();
        categoryInfo.put("Name",name);
        categoryInfo.put("Description",description);
        categoryInfo.put("Price",price);
        categoryInfo.put("Company_Name",company);
        categoryInfo.put("image_uri",downloadImageUri);

        categoryInfoRef.child(name.toString()).updateChildren(categoryInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(CategoryAdder.this, "Category Added", Toast.LENGTH_SHORT).show();
                            categoryInfo.clear();
                            loadingBar.dismiss();
                            flag=1;
                        }
                        else
                        {
                            Toast.makeText(CategoryAdder.this, "Network Error", Toast.LENGTH_SHORT).show();
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
