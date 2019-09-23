package com.example.jewelleryshopadmin.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jewelleryshopadmin.R;
import com.example.jewelleryshopadmin.activity.DetailsActivity;
import com.example.jewelleryshopadmin.activity.ProductActivity;
import com.example.jewelleryshopadmin.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {



    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference category = database.getReference("Category");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private String key, productName,productRegularPrice,productCurrentPrice, productDiscount ,productdescription ,name, imageUrl = "", categoryKey;
    private EditText edtName;
    private ImageView btnSelect, showImage;
    private TextView btnUpload,uploaded;
    private Uri saveUri;

    private EditText productNameET, productPriceET,productDiscountET,productDescription;
    private ImageView productImageCamera, productImageShow;
    private String categoryId, price, discount,description,  categoryN;

    List<Product> productList;
    Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        final Product currentProduct = productList.get(position);
        holder.name.setText(currentProduct.getProductName());
        holder.regularPrice.setText(String.valueOf(currentProduct.getProductRegularPrice()));

        if(currentProduct.getProductDiscountPrice() == currentProduct.getProductRegularPrice()){
            holder.discountPrice.setVisibility(View.GONE);
            holder.regularPrice.setTextColor(Color.parseColor("#B31F5F"));

        }else {

            holder.discountPrice.setText(String.valueOf(currentProduct.getProductDiscountPrice()));

        }
        if(currentProduct.getProductDiscount()==0){
            holder.discount.setVisibility(View.GONE);
        }else {
            holder.discount.setText(String.valueOf(currentProduct.getProductDiscount())+ " % Off");

        }



        //&& !currentProduct.getProductImage().isEmpty()

        if(currentProduct.getProductImage()!=null && !currentProduct.getProductImage().isEmpty()){
            Picasso.get()
                    .load(currentProduct.getProductImage())
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .fit()
                    .centerCrop()
                    .into(holder.productImage);

        }else {
            holder.productImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_image_black_24dp));
        }

//        holder.beforeFav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.beforeFav.setVisibility(View.GONE);
//                holder.afterFav.setVisibility(View.VISIBLE);
//            }
//        });
//        holder.afterFav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.afterFav.setVisibility(View.GONE);
//                holder.beforeFav.setVisibility(View.VISIBLE);
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("id",currentProduct.getProductId());
                intent.putExtra("userId",currentProduct.getUserId());
                intent.putExtra("catId",currentProduct.getCategoryId());

                context.startActivity(intent );



            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        key = currentProduct.getProductId();
                        categoryKey = currentProduct.getCategoryId();
                        productName = currentProduct.getProductName();
                        productRegularPrice = String.valueOf(currentProduct.getProductRegularPrice());
                        productCurrentPrice = String.valueOf(currentProduct.getProductDiscountPrice());
                        productDiscount = String.valueOf(currentProduct.getProductDiscount());
                        imageUrl = currentProduct.getProductImage();
                        categoryId = categoryKey;

                        switch (menuItem.getItemId()) {
                            case R.id.update:
                                showDialog();


                                //Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.delete:


                                deleteItem();
                                // Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();

                                return true;


                            default:
                        }

                        return false;
                    }
                });
                popupMenu.inflate(R.menu.update_delete);
                popupMenu.show();

                return false;
            }
        });



    }

    private void deleteItem() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure that delete this item?");
        alertDialog.setIcon(R.drawable.ic_delete_black_24dp);


        //Set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();


                //final String userId = firebaseAuth.getCurrentUser().getUid();
                final DatabaseReference productRef = category.child(categoryId).child("Product").child(key);

                productRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

                Toast.makeText(context, "Item deleted !!!", Toast.LENGTH_SHORT);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);

        alertDialog.show();

    }


    private void showDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Add new Product");
        alertDialog.setMessage("Please fill information");

        LayoutInflater inflater = LayoutInflater.from(context);
        View add_menu_layout = inflater.inflate(R.layout.add_product_layout, null);

        productNameET = add_menu_layout.findViewById(R.id.productNameET);
        productPriceET = add_menu_layout.findViewById(R.id.productPriceET);
        productDiscountET = add_menu_layout.findViewById(R.id.productDiscountET);

        btnUpload = add_menu_layout.findViewById(R.id.uploadProductImage);
        productImageCamera = add_menu_layout.findViewById(R.id.productImageCamera);
        productImageShow = add_menu_layout.findViewById(R.id.productImage);
        uploaded = add_menu_layout.findViewById(R.id.uploaded);
        productDescription = add_menu_layout.findViewById(R.id.productDescriptionET);


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
                double regularPrice = Double.parseDouble(price);

                discount = productDiscountET.getText().toString();
                if(discount.isEmpty()){
                    discount = "0";
                }
                int discountAmount= Integer.parseInt(discount);
                description = productDescription.getText().toString();

                double currentPrice = regularPrice - (regularPrice * Double.valueOf(discountAmount))/100;

                String key = category.child(categoryId).child("Product").push().getKey();
                String userId = firebaseAuth.getCurrentUser().getUid();

                Product product = new Product(userId,categoryId,key,name,regularPrice,currentPrice,imageUrl,discountAmount,description);

                DatabaseReference productRef = category.child(categoryId).child("Product");
                productRef.child(key).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                            productImageShow.setImageURI(null);
                        }

                    }
                });



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
    }

    private void chooseImage() {

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, regularPrice, discountPrice, discount;
        ImageView productImage,beforeFav,afterFav;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.productNameTV);
            regularPrice = itemView.findViewById(R.id.regularPriceTV);
            discountPrice = itemView.findViewById(R.id.currentPriceTV);
            discount = itemView.findViewById(R.id.discountTV);
            productImage = itemView.findViewById(R.id.productImageIV);
//            beforeFav = itemView.findViewById(R.id.beforeFavIV);
//            afterFav = itemView.findViewById(R.id.afterFavIV);

        }
    }
}
