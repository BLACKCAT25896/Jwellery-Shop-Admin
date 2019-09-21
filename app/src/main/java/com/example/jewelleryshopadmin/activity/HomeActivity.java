package com.example.jewelleryshopadmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jewelleryshopadmin.R;
import com.example.jewelleryshopadmin.adapter.CategoryAdapter;
import com.example.jewelleryshopadmin.common.Common;
import com.example.jewelleryshopadmin.databinding.ActivityHomeBinding;
import com.example.jewelleryshopadmin.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private EditText edtName;
    private ImageView btnSelect,showImage;
    private TextView btnUpload;
    private Category newCategory;
    private Button yseBtn, noBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference category;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String name;
    private String imageUrl = "";
    private Uri uri;
    private Uri saveUri;
    private List<Category> categoryList;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        init();
        controlFAB();
        getCategory();

    }

    private void getCategory() {
       String userId = firebaseAuth.getCurrentUser().getUid();
        category.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    categoryList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    Category category = data.getValue(Category.class);
                                    categoryList.add(category);
                                    adapter.notifyDataSetChanged();
                                }
                                binding.homeRecyclerViewHorizontal.setAdapter(adapter);


//                    DatabaseReference catRef = category;
//                    catRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if(dataSnapshot.exists()){
//                                categoryList.clear();
//
//
//                                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                    Category category = data.getValue(Category.class);
//                                    categoryList.add(category);
//                                    adapter.notifyDataSetChanged();
//                                }
//                                binding.homeRecyclerViewHorizontal.setAdapter(adapter);
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /////////////Test//////////



    }

    private void controlFAB() {
        binding.homeRecyclerViewHorizontal.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && binding.addCategoryFAB.getVisibility() == View.VISIBLE) {
                    binding.addCategoryFAB.hide();
                } else if (dy < 0 && binding.addCategoryFAB.getVisibility() != View.VISIBLE) {
                    binding.addCategoryFAB.show();
                }
            }
        });
    }

    private void init() {
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList,this);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        binding.homeRecyclerViewHorizontal.setLayoutManager(new GridLayoutManager(this,2));
        binding.homeRecyclerViewHorizontal.setAdapter(adapter);



    }

    public void addCategory(View view) {
        showDialog();
    }

    private void showDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Add new Category");
        alertDialog.setMessage("Please fill information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_category_layout, null);

        edtName = add_menu_layout.findViewById(R.id.categoryNameET);
        btnSelect = add_menu_layout.findViewById(R.id.categoryImageCamera);
        btnUpload = add_menu_layout.findViewById(R.id.uploadCategoryImage);
        showImage = add_menu_layout.findViewById(R.id.categoryImage);

      /*  yseBtn = add_menu_layout.findViewById(R.id.yesBtn);
        noBtn = add_menu_layout.findViewById(R.id.noBtn);*/


        //Event for Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(); //Let user select from Gallery and save Uri of this image
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);


        //Set button

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                name = edtName.getText().toString();
                final String userId = firebaseAuth.getCurrentUser().getUid();
                category.orderByChild("categoryName").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot data: dataSnapshot.getChildren()) {
                               // String uid = data.getKey();
                                Toast.makeText(HomeActivity.this, "Data All ready Exist. Try Different one", Toast.LENGTH_SHORT).show();
                                showDialog();
                            }
                        }else {
                            String key = category.push().getKey();
                            Category cat = new Category(userId,key,name,imageUrl);

                            DatabaseReference categoryRef = category;
                            categoryRef.child(key).setValue(cat).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(HomeActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                        showImage.setImageURI(null);
                                    }

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //Here, just create new category
//                if (newCategory != null) {
//                    category.push().setValue(newCategory);
//                   /* Snackbar.make(drawer, "New Category " + newCategory.getName() + "was added ", Snackbar.LENGTH_SHORT)
//                            .show();*/
//                    Toast.makeText(HomeActivity.this, "done", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(HomeActivity.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageUrl = uri.toString();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            int value = (int)Math.round(progress);
                            mDialog.setMessage("Uploaded " + value + " %");
                        }
                    });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
          //  binding.showIV.setImageURI(uri);
//            btnSelect.setIm("Image Selected");
            showImage.setImageURI(saveUri);
            btnSelect.setVisibility(View.GONE);
            showImage.setVisibility(View.VISIBLE);
        }
    }
}
