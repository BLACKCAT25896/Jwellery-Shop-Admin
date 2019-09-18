package com.example.jewelleryshopadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jewelleryshopadmin.R;
import com.example.jewelleryshopadmin.model.Category;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {


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
                Toast.makeText(context, ""+currentCategory.getCategoryName() +"   "+ currentCategory.getCategoryId(), Toast.LENGTH_LONG).show();
            }
        });


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
