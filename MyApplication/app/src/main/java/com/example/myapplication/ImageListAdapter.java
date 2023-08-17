package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private final List<Uri> imageUris;
    private Context context;

    public ImageListAdapter(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        holder.bind(imageUri);
        Picasso.get().load(imageUri).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        boolean firstTime = true;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.idIVImage);
        }

        public void bind(Uri imageUri) {
            imageView.setImageURI(imageUri);
            imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    if(firstTime){
                        params.width = imageView.getWidth() * 3;
                        params.height = imageView.getHeight() * 3;
                        firstTime = false;
                    }else{

                        params.width = imageView.getWidth();
                        params.height =imageView.getHeight();
                    }

                    imageView.setLayoutParams(params);
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

    }
}
