package com.example.jewelleryshopadmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jewelleryshopadmin.R;
import com.example.jewelleryshopadmin.adapter.ProductAdapter;
import com.example.jewelleryshopadmin.common.Common;
import com.example.jewelleryshopadmin.databinding.ActivityProductBinding;
import com.example.jewelleryshopadmin.model.Category;
import com.example.jewelleryshopadmin.model.Product;
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

public class ProductActivity extends AppCompatActivity {
    private ActivityProductBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference category;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private EditText productNameET, productPriceET,productDiscount;
    private ImageView productImageCamera, productImageShow;
    private TextView btnUpload;
    private String categoryId, name, price, discount, imageUrl = "";
    private Uri saveUri;
    private List<Product> productList;
    private ProductAdapter adapter;
    private int discountAmount= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_product);
        init();
        controlFAB();
        categoryId = getIntent().getStringExtra("key");
        getProduct();
    }

    private void getProduct() {

        DatabaseReference productRef = category.child(categoryId).child("Product");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    productList.clear();


                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Product product = data.getValue(Product.class);
                        // String pushId = data.getKey();
                        productList.add(product);
                        // String namee = data.child("categoryName").getValue().toString();

                        // Toast.makeText(HomeActivity.this, ""+namee, Toast.LENGTH_SHORT).show();

                        //pushList.add(pushId);
                        //binding.dummyTextTV.setVisibility(View.INVISIBLE);

                        adapter.notifyDataSetChanged();
                    }
                    binding.productRecyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void controlFAB() {
        binding.productRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && binding.addProductFAB.getVisibility() == View.VISIBLE) {
                    binding.addProductFAB.hide();
                } else if (dy < 0 && binding.addProductFAB.getVisibility() != View.VISIBLE) {
                    binding.addProductFAB.show();
                }
            }
        });
    }

    private void init() {

        productList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        adapter = new ProductAdapter(productList,this);
        binding.productRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        binding.productRecyclerView.setAdapter(adapter);


    }

    public void addProduct(View view) {
        showDialog();
    }

    private void showDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Add new Product");
        alertDialog.setMessage("Please fill information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_product_layout, null);

        productNameET = add_menu_layout.findViewById(R.id.productNameET);
        productPriceET = add_menu_layout.findViewById(R.id.productPriceET);
        productDiscount = add_menu_layout.findViewById(R.id.productDiscountET);

        btnUpload = add_menu_layout.findViewById(R.id.uploadProductImage);
        productImageCamera = add_menu_layout.findViewById(R.id.productImageCamera);
        productImageShow = add_menu_layout.findViewById(R.id.productImage);


      /*  yseBtn = add_menu_layout.findViewById(R.id.yesBtn);
        noBtn = add_menu_layout.findViewById(R.id.noBtn);*/


        //Event for Button
        productImageCamera.setOnClickListener(new View.OnClickListener() {
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
                name = productNameET.getText().toString();
                price = productPriceET.getText().toString();
                discount = productDiscount.getText().toString();
                if(discount.isEmpty()){
                    discount = "0";
                }
                discountAmount= Integer.parseInt(discount);

                String key = category.child(categoryId).child("Product").push().getKey();

                Product product = new Product(categoryId,key,name,price,imageUrl,discountAmount);

                DatabaseReference productRef = category.child(categoryId).child("Product");
                productRef.child(key).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ProductActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            productImageShow.setImageURI(null);
                        }

                    }
                });


                //String userId = firebaseAuth.getCurrentUser().getUid();
//                category.orderByChild("categoryName").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()){
//                            for (DataSnapshot data: dataSnapshot.getChildren()) {
//                                // String uid = data.getKey();
//                                Toast.makeText(HomeActivity.this, "Data All ready Exist. Try Different one", Toast.LENGTH_SHORT).show();
//                                showDialog();
//                            }
//                        }else {
//                            String key = category.push().getKey();
//                            Category cat = new Category(key,name,imageUrl);
//
//                            DatabaseReference categoryRef = category;
//                            categoryRef.child(key).setValue(cat).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        Toast.makeText(ProductActivity.this, "Done", Toast.LENGTH_SHORT).show();
//                                        productImageShow.setImageURI(null);
//                                    }
//
//                                }
//                            });
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);


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
                            Toast.makeText(ProductActivity.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            //  binding.showIV.setImageURI(uri);
//            btnSelect.setIm("Image Selected");
            productImageShow.setImageURI(saveUri);
            productImageCamera.setVisibility(View.GONE);
            productImageShow.setVisibility(View.VISIBLE);
        }
    }
}
