package com.example.photoapplication;

import static com.example.photoapplication.MainActivity.INDEX;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PhotoActivity extends AppCompatActivity {
	// Instance variables
	private AlbumInList albumInList;
	private Album album;
	private Toolbar tool;
	private ImageView image;
	private int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);

		// Get album index from intent
		index = getIntent().getIntExtra(INDEX, 0);

		// Initialize variables
		albumInList = AlbumInList.getInstance();
		album = albumInList.getAlbums().get(index);

		// Initialize UI components
		tool = findViewById(R.id.photo_toolbar);
		image = findViewById(R.id.photo_image);
		FloatingActionButton leftButton = findViewById(R.id.photo_button_left);
		FloatingActionButton rightButton = findViewById(R.id.photo_button_right);

		// Load initial photo
		loadPhoto();

		setSupportActionBar(tool);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Set click listeners for left and right navigation buttons
		leftButton.setOnClickListener(v -> {
			album.setIndex(album.getIndex() - 1);
			loadPhoto();
		});
		rightButton.setOnClickListener(v -> {
			album.setIndex(album.getIndex() + 1);
			loadPhoto();
		});
	}

	// Method to load the current photo
	public void loadPhoto() {
		if (!album.getPhotos().isEmpty()) {
			Photo photo = album.getPhotos().get(album.getIndex());
			image.setImageBitmap(photo.getBitmap().getBitmap());
			tool.setTitle(photo.getFileName());
		} else {
			// If no photos in album, go back
			onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu for the activity
		getMenuInflater().inflate(R.menu.photo_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle menu item clicks
		if (item.getItemId() == R.id.photo_menu_item_move) {
			// Dialog to move photo to another album
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
			builder.setTitle("Move Photo");
			builder.setMessage("Choose album to move photo to.");

			// Create spinner to select album
			String[] albumNames = new String[albumInList.getAlbums().size()];
			for (int i = 0; i < albumInList.getAlbums().size(); i++) {
				albumNames[i] = albumInList.getAlbums().get(i).getName();
			}
			Spinner albumsSpinner = new Spinner(this);
			albumsSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, albumNames));
			builder.setView(albumsSpinner);

			// Move photo to selected album
			builder.setPositiveButton("Move", (dialog, which) -> {
				Photo photo = album.getPhotos().get(album.getIndex());
				album.removePhoto(photo);
				albumInList.getAlbums().get(albumsSpinner.getSelectedItemPosition()).addPhoto(photo);
				albumInList.serialize(this);
				loadPhoto();
			});
			builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

			builder.show();
			return true;
		}
		if (item.getItemId() == R.id.photo_menu_item_edit_tags) {
			// Open tag editing activity
			TagActivity tags = new TagActivity();
			Bundle args = new Bundle();
			args.putInt(INDEX, index);
			tags.setArguments(args);
			tags.show(getSupportFragmentManager(), "Tags");
			return true;
		}
		if (item.getItemId() == R.id.photo_menu_item_remove) {
			// Dialog to confirm photo deletion
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
			builder.setTitle("Delete Photo");
			builder.setMessage("Do you want to delete this photo?");
			builder.setPositiveButton("Yes", (dialog, which) -> {
				album.removePhoto(album.getPhotos().get(album.getIndex()));
				albumInList.serialize(this);
				loadPhoto();
			});
			builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
			builder.show();
			return true;
		}
		if (item.getItemId() == android.R.id.home) {
			// Handle toolbar back button click
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
