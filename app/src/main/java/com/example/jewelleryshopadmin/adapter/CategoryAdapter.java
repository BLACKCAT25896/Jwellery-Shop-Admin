package com.example.jewelleryshopadmin.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.jewelleryshopadmin.activity.HomeActivity;
import com.example.jewelleryshopadmin.activity.ProductActivity;
import com.example.jewelleryshopadmin.common.Common;
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
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {


    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference category = database.getReference("Category");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private String key, categoryName,name, imageUrl = "";
    private EditText edtName;
    private ImageView btnSelect, showImage;
    private TextView btnUpload,uploaded;
    private Uri saveUri;


    List<Category> categoryList;
    Context context;

    public CategoryAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position) {
        final Category currentCategory = categoryList.get(position);

        holder.categoryName.setText(currentCategory.getCategoryName());

        if(currentCategory.getCategoryImage()!=null && !currentCategory.getCategoryImage().isEmpty()){
            Picasso.get()
                    .load(currentCategory.getCategoryImage())
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .fit()
                    .centerCrop()
                    .into(holder.showImage);

        }else {
            holder.showImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_image_black_24dp));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, ""+currentCategory.getCategoryName() +"   "+ currentCategory.getCategoryId(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra("key",currentCategory.getCategoryId());
                intent.putExtra("name",currentCategory.getCategoryName());
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
                        key = currentCategory.getCategoryId();
                        categoryName = currentCategory.getCategoryName();
                        imageUrl = currentCategory.getCategoryImage();
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

    private void showDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Add new Category");
        alertDialog.setMessage("Please fill information");

       // LayoutInflater inflater = context.getLayoutInflater();
        //View add_menu_layout = inflater.inflate(R.layout.add_category_layout, null);
        View add_menu_layout = LayoutInflater.from(context).inflate(R.layout.add_category_layout,null);

        edtName = add_menu_layout.findViewById(R.id.categoryNameET);
        btnSelect = add_menu_layout.findViewById(R.id.categoryImageCamera);
        btnUpload = add_menu_layout.findViewById(R.id.uploadCategoryImage);
        showImage = add_menu_layout.findViewById(R.id.categoryImage);
        uploaded = add_menu_layout.findViewById(R.id.uploaded);

        edtName.setText(categoryName);


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

        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                name = edtName.getText().toString();
                final String userId = firebaseAuth.getCurrentUser().getUid();
                category.orderByChild("categoryName").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                // String uid = data.getKey();
                                Toast.makeText(context, "Data All ready Exist. Try Different one", Toast.LENGTH_SHORT).show();
                                showDialog();
                            }
                        } else {

                            Category cat = new Category(userId, key, name, imageUrl);

                            DatabaseReference categoryRef = category;
                            categoryRef.child(key).setValue(cat).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
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
            final ProgressDialog mDialog = new ProgressDialog(context);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(context, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageUrl = uri.toString();
                                    btnUpload.setVisibility(View.GONE);
                                    uploaded.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            int value = (int) Math.round(progress);
                            mDialog.setMessage("Uploaded " + value + " %");
                        }
                    });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Activity origin = (Activity)context;
        origin.startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);

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
                final DatabaseReference catRef = category.child(key);

                catRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            catRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView showImage;
        TextView categoryName;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            showImage = itemView.findViewById(R.id.categoryImageIV);
            categoryName = itemView.findViewById(R.id.categoryNameTV);
        }
    }
}
