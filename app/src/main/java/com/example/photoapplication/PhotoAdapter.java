package com.example.photoapplication;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoView> {
    private Context context;
    private List<Photo> photoList;
    private PhotoAction action;

    // Interface for actions related to photos
    interface PhotoAction {
        // Method to remove a photo at a specific position
        void removePhoto(int position, PhotoView view, Context context);

        // Method to open a photo at a specific position
        void openPhoto(int position);

        // Method to show additional actions for a photo
        void showActions(PhotoView view, Context context);

        // Method to hide additional actions for a photo
        void hideActions(PhotoView view, Context context);
    }

    @NonNull
    @Override
    public PhotoView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get the context from the parent ViewGroup
        context = parent.getContext();

        // Inflate the layout for each item in the RecyclerView
        View itemView = LayoutInflater.from(context).inflate(R.layout.thumbnail_view, parent, false);
        return new PhotoView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoView holder, int position) {
        // Get the photo at the given position
        Photo photo = photoList.get(position);

        // Set data to views inside the ViewHolder
        holder.text.setText(photo.getFileName());
        holder.image.setImageBitmap(photo.getBitmap().getBitmap());

        // Set click listeners for various actions
        holder.layout.setOnClickListener(v -> action.openPhoto(position));
        holder.cancel.setOnClickListener(v -> action.hideActions(holder, context));
        holder.overflow.setOnClickListener(v-> action.showActions(holder, context));
        holder.delete.setOnClickListener(v-> action.removePhoto(position, holder, context));
    }

    // Setter for PhotoAction interface
    void setAction(PhotoAction action){
        this.action = action;
    }

    @Override
    public int getItemCount() {
        // Return the size of the photo list
        return photoList.size();
    }

    // Setter for photo list
    void setPhotoList(List<Photo> photoList){ this.photoList = photoList; }

    // ViewHolder class for each item in the RecyclerView
    static class PhotoView extends RecyclerView.ViewHolder{
        CardView card;
        TextView text;
        ImageView image;
        ConstraintLayout layout;
        LinearLayout overlayLayout;
        ImageButton overflow, delete, cancel;

        public PhotoView(@NonNull View itemView) {
            super(itemView);
            // Initialize views inside the ViewHolder
            card = itemView.findViewById(R.id.photo_card_view);
            layout = itemView.findViewById(R.id.photo_card_layout);
            text = itemView.findViewById(R.id.photo_card_text_details);
            image = itemView.findViewById(R.id.photo_card_image_view);
            overlayLayout = itemView.findViewById(R.id.photo_card_overlay);
            overflow = itemView.findViewById(R.id.photo_card_overflow_button);
            delete = itemView.findViewById(R.id.photo_card_button_remove);
            cancel = itemView.findViewById(R.id.photo_card_button_cancel);
        }
    }
}