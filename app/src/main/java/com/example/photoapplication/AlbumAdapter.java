package com.example.photoapplication;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.*;
import android.widget.*;
import java.util.*;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumView> {
	private Context context;
	private AlbumAction listener;
	private List<Album> albumsList;

	// Interface for actions related to albums
	interface AlbumAction {
		// Method to open an album at a specific position
		void openAlbum(int position);

		// Method to delete an album at a specific position
		void deleteAlbum(int position, AlbumView holder, Context context);

		// Method to rename an album at a specific position
		void renameAlbum(int position, AlbumView holder, Context context);

		// Method to show additional actions for an album
		void showActions(AlbumView holder, Context context);

		// Method to hide additional actions for an album
		void hideActions(AlbumView holder, Context context);
	}

	@NonNull
	@Override
	public AlbumView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		context = parent.getContext();
		// Inflate the layout for each item in the RecyclerView
		View view = LayoutInflater.from(context).inflate(R.layout.album_view, parent, false);
		return new AlbumView(view);
	}

	@Override
	public void onBindViewHolder(@NonNull AlbumView holder, int position) {
		Album album = albumsList.get(position);
		// Set album title to the corresponding view
		holder.title.setText(album.getName());
		// Set click listeners for various actions
		holder.mainLayout.setOnClickListener(v -> listener.openAlbum(position));
		holder.delete.setOnClickListener(v -> listener.deleteAlbum(position, holder, context));
		holder.rename.setOnClickListener(v -> listener.renameAlbum(position, holder, context));
		holder.overflow.setOnClickListener(v -> listener.showActions(holder, context));
		holder.cancel.setOnClickListener(v -> listener.hideActions(holder, context));
	}

	@Override
	public int getItemCount() {
		// Return the size of the album list
		return albumsList.size();
	}

	// Setter for AlbumAction interface
	void setItemActionListener(AlbumAction listener) {
		this.listener = listener;
	}

	// Setter for album list
	void setAlbumsList(List<Album> albumsList) {
		this.albumsList = albumsList;
	}

	// ViewHolder class for each item in the RecyclerView
	static class AlbumView extends RecyclerView.ViewHolder {
		CardView card;
		TextView title;
		ImageButton overflow, delete, rename, cancel;
		ConstraintLayout mainLayout;
		LinearLayout overlayLayout;

		public AlbumView(@NonNull View itemView) {
			super(itemView);
			// Initialize views inside the ViewHolder
			card = itemView.findViewById(R.id.album_card_view);
			mainLayout = itemView.findViewById(R.id.album_card_layout_main);
			overlayLayout = itemView.findViewById(R.id.album_card_layout_overlay);
			title = itemView.findViewById(R.id.album_card_text_title);
			overflow = itemView.findViewById(R.id.album_card_button_overflow);
			delete = itemView.findViewById(R.id.album_card_button_delete);
			rename = itemView.findViewById(R.id.album_card_button_rename);
			cancel = itemView.findViewById(R.id.album_card_button_cancel);
		}
	}
}