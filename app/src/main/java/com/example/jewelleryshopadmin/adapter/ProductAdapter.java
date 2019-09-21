package com.example.jewelleryshopadmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jewelleryshopadmin.R;
import com.example.jewelleryshopadmin.activity.ProductActivity;
import com.example.jewelleryshopadmin.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

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
        Product currentProduct = productList.get(position);
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

        holder.beforeFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.beforeFav.setVisibility(View.GONE);
                holder.afterFav.setVisibility(View.VISIBLE);
            }
        });
        holder.afterFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.afterFav.setVisibility(View.GONE);
                holder.beforeFav.setVisibility(View.VISIBLE);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, ""+currentCategory.getCategoryName() +"   "+ currentCategory.getCategoryId(), Toast.LENGTH_LONG).show();

//                Intent intent = new Intent(context, ProductActivity.class);
//
//                context.startActivity(intent );



            }
        });


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
            beforeFav = itemView.findViewById(R.id.beforeFavIV);
            afterFav = itemView.findViewById(R.id.afterFavIV);

        }
    }
}
