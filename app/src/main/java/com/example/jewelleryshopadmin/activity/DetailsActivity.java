package com.example.jewelleryshopadmin.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.jewelleryshopadmin.R;
import com.example.jewelleryshopadmin.databinding.ActivityDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
    private ActivityDetailsBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference category;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String id, userId, catId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_details);
        init();
       // controlBackBtn();

        id = getIntent().getStringExtra("id");
        catId = getIntent().getStringExtra("catId");

       // userId = getIntent().getStringExtra("userId");

        getProductDetails();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        binding.beforeFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.beforeFav.setVisibility(View.GONE);
                binding.afterFav.setVisibility(View.VISIBLE);
            }
        });
        binding.afterFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.afterFav.setVisibility(View.GONE);
                binding.beforeFav.setVisibility(View.VISIBLE);
            }
        });

    }



    private void getProductDetails() {
       // String userId = firebaseAuth.getCurrentUser().getUid();

         category.child(catId).child("Product").orderByChild("productId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        String image = data.child("productImage").getValue().toString();
                        String name = data.child("productName").getValue().toString();
                        String currentPrice = data.child("productDiscountPrice").getValue().toString();
                        String regularPrice = data.child("productRegularPrice").getValue().toString();
                        String discount = data.child("productDiscount").getValue().toString();
                        binding.discount.setText(discount+"%");
                        binding.regularPrice.setText(regularPrice +" Tk");

                        binding.currentPrice.setText(currentPrice +" Tk");


                        binding.productName.setText(name);

                        if(image!=null && !image.isEmpty()){
                            Picasso.get()
                                    .load(image)
                                    .placeholder(R.drawable.jewellery)
                                    .fit()
                                    .centerCrop()
                                    .into(binding.productImageDetails);


                        }else {
                            binding.productImageDetails.setImageDrawable(ContextCompat.getDrawable(DetailsActivity.this,R.drawable.jewellery));
                        }
                        if(image!=null && !image.isEmpty()){
                            Picasso.get()
                                    .load(image)
                                    .placeholder(R.drawable.jewellery)
                                    .fit()
                                    .centerCrop()
                                    .into(binding.productPicture);


                        }else {
                            binding.productImageDetails.setImageDrawable(ContextCompat.getDrawable(DetailsActivity.this,R.drawable.jewellery));
                        }


                    }



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void init() {

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }


}
