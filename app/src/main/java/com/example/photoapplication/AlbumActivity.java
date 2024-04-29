package com.example.photoapplication;

import static com.example.photoapplication.MainActivity.INDEX;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.*;
import android.widget.TextView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.*;
import java.util.*;

public class AlbumActivity extends AppCompatActivity implements PhotoAdapter.PhotoAction {
	// Variables for the activity
	private AlbumInList albumInList;
	private Album album;
	private PhotoAdapter adapter;
	private List<Photo> photoList;
	private TextView text;
	private int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);

		// Retrieve album index from intent
		index = getIntent().getIntExtra(INDEX, 0);

		// Get album and photo list
		albumInList = AlbumInList.getInstance();
		album = albumInList.getAlbums().get(index);
		photoList = album.getPhotos();

		// Initialize UI components
		Toolbar toolbar = findViewById(R.id.album_toolbar);
		FloatingActionButton addButton = findViewById(R.id.album_button_add);
		RecyclerView recyclerView = findViewById(R.id.album_recycler_view);
		text = findViewById(R.id.album_text_empty);

		// Set toolbar title
		toolbar.setTitle(album.getName());
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Set up RecyclerView
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(layoutManager);

		// Set up adapter for RecyclerView
		adapter = new PhotoAdapter();
		adapter.setAction(this);
		recyclerView.setAdapter(adapter);

		// Show text view if photo list is empty
		if(photoList.isEmpty()){
			text.setVisibility(View.VISIBLE);
		}
		adapter.setPhotoList(photoList);

		// Register activity result launcher for file chooser
		ActivityResultLauncher<Intent> startFileChooserForResult = registerForActivityResult(
				new ActivityResultContracts.StartActivityForResult(), result -> {
					if (result.getResultCode() == Activity.RESULT_OK) {
						Intent data = result.getData();
						if (data == null) {
							return;
						}
						Uri uri = data.getData();
						try {
							// Decode bitmap from selected image
							String fileName = getFileName(uri);
							Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
							// Add photo to album
							if (album.addPhoto(new Photo(fileName, new Serializer(bitmap)))) {
								albumInList.serialize(this); // save data
								adapter.notifyItemInserted(photoList.size() - 1);
								text.setVisibility(View.INVISIBLE);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						// Open PhotoActivity
						Intent intent = new Intent(this, PhotoActivity.class);
						intent.putExtra(INDEX, index);
						startActivity(intent);
					}
				});

		// Set click listener for add button to open file chooser
		addButton.setOnClickListener(v -> {
			Intent data = new Intent(Intent.ACTION_GET_CONTENT);
			data.addCategory(Intent.CATEGORY_OPENABLE);
			data.setType("*/*");
			String[] mimeTypes = {"image/png", "image/jpeg"};
			data.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
			Intent fileChooser = Intent.createChooser(data, "Choose Image");
			startFileChooserForResult.launch(fileChooser);
		});
	}

	// Method to get file name from URI
	public String getFileName(Uri uri) {
		String fileName = null;
		if (uri.getScheme().equals("content")) {
			try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
				if (cursor != null && cursor.moveToFirst()) {
					int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
					if (columnIndex >= 0) {
						fileName = cursor.getString(columnIndex);
					}
				}
			}
		}
		if (fileName == null) {
			fileName = uri.getPath();
			assert fileName != null;
			int i = fileName.lastIndexOf('/');
			if (i != -1) {
				fileName = fileName.substring(i + 1);
			}
		}
		return fileName;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle toolbar back button click
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NotifyDataSetChanged")
	@Override
	public void removePhoto(int position, PhotoAdapter.PhotoView view, Context context) {
		// Create dialog to confirm photo deletion
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
		builder.setTitle("Delete Photo");
		builder.setMessage("Do you want to delete this photo?");
		builder.setPositiveButton("Yes", (dialog, which) -> {
			// Remove photo and update adapter
			album.removePhoto(album.getPhotos().get(album.getIndex()));
			albumInList.serialize(this);
			adapter.notifyItemRemoved(position);
			adapter.notifyDataSetChanged();
			photoList = albumInList.getAlbum(album).getPhotos();
			if (photoList.isEmpty()) {
				text.setVisibility(View.VISIBLE);
			}
			hideActions(view, context);
		});
		builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
		builder.show();
	}

	@Override
	public void openPhoto(int position) {
		// Open PhotoActivity with selected photo
		album.setIndex(position);
		Intent intent = new Intent(this, PhotoActivity.class);
		intent.putExtra(INDEX, index);
		startActivity(intent);
	}

	@Override
	public void showActions(PhotoAdapter.PhotoView view, Context context) {
		// Show overlay actions for a photo
		view.overlayLayout.setVisibility(View.VISIBLE);
		float elevationPx = 24 * context.getResources().getDisplayMetrics().density;
		view.card.setCardElevation(elevationPx);
		view.layout.setClickable(false);
	}

	@Override
	public void hideActions(PhotoAdapter.PhotoView view, Context context) {
		// Hide overlay actions for a photo
		view.overlayLayout.setVisibility(View.INVISIBLE);
		float elevationPx = 4 * context.getResources().getDisplayMetrics().density;
		view.card.setCardElevation(elevationPx);
		view.layout.setClickable(true);
	}
}
