package com.example.photoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.*;
import android.app.SearchManager;
import android.content.*;
import android.database.*;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.*;

public class MainActivity extends AppCompatActivity implements AlbumAdapter.AlbumAction {
	// Constants
	static final String INDEX = "index";

	// Instance variables
	private AlbumInList albumInList;
	private AlbumAdapter adapter;
	private TextView text;
	private FloatingActionButton fullsearchbutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize UI components
		text = findViewById(R.id.main_text_empty);
		Toolbar toolbar = findViewById(R.id.main_toolbar);
		RecyclerView recyclerView = findViewById(R.id.main_recycler_view);
		FloatingActionButton addButton = findViewById(R.id.main_button_add);

		setSupportActionBar(toolbar);

		// Set up RecyclerView
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(layoutManager);

		// Initialize AlbumAdapter and set action listener
		adapter = new AlbumAdapter();
		adapter.setItemActionListener(this);
		recyclerView.setAdapter(adapter);

		// Set click listener for add button
		addButton.setOnClickListener(v -> addAlbum());

		// Deserialize albums from storage
		albumInList = AlbumInList.getInstance();
		albumInList.deserialize(this);
		if (albumInList.getAlbums().size() == 0) {
			text.setVisibility(View.VISIBLE);
		}
		adapter.setAlbumsList(albumInList.getAlbums());

		fullsearchbutton = findViewById(R.id.full_search_button);
		fullsearchbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<String> allTagValues = albumInList.getAlbumTagValues();
				Intent intent = new Intent(MainActivity.this, SearchTags.class);
				intent.putExtra("allTags", allTagValues);
				startActivity(intent);
			}
		});

	}


	// Method to add a new album
	public void addAlbum() {
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
		builder.setTitle("Add Album");
		builder.setMessage("Enter new album name.");

		EditText nameInput = new EditText(this);
		nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(nameInput);

		builder.setPositiveButton("Add", (dialog, which) -> {
			albumInList.getAlbums().add(new Album(nameInput.getText().toString().trim()));
			adapter.notifyItemInserted(albumInList.getAlbums().size() - 1);
			text.setVisibility(View.INVISIBLE);
			albumInList.serialize(this);
		});
		builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

		builder.show();
	}

	// AlbumAction interface methods
	@Override
	public void openAlbum(int position) {
		Intent intent = new Intent(this, AlbumActivity.class);
		intent.putExtra(INDEX, position);
		startActivity(intent);
	}

	@Override
	public void deleteAlbum(int position, AlbumAdapter.AlbumView holder, Context context) {
		// Dialog to confirm album deletion
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
		builder.setTitle("Delete Album");
		builder.setMessage("Do you want to delete this album?");
		builder.setPositiveButton("Yes", (dialog, which) -> {
			albumInList.getAlbums().remove(position);
			adapter.notifyItemRemoved(position);
			adapter.notifyDataSetChanged();
			if (albumInList.getAlbums().size() == 0) {
				text.setVisibility(View.VISIBLE);
			}
			hideActions(holder, context);
			albumInList.serialize(this);
		});
		builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
		builder.show();
	}

	@Override
	public void renameAlbum(int position, AlbumAdapter.AlbumView holder, Context context) {
		// Dialog to rename album
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
		builder.setTitle("Rename Album");
		builder.setMessage("Enter new name for the album.");

		EditText nameInput = new EditText(this);
		nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(nameInput);

		builder.setPositiveButton("Rename", (dialog, which) -> {
			albumInList.getAlbums().get(position).setName(nameInput.getText().toString().trim());
			adapter.notifyItemChanged(position);
			hideActions(holder, context);
			albumInList.serialize(this); // save data
		});
		builder.setNegativeButton("Cancel", (dialog, which) -> {
			hideActions(holder, context); // hide overlay
			dialog.cancel();
		});

		builder.show();
	}

	@Override
	public void showActions(AlbumAdapter.AlbumView holder, Context context) {
		// Show overlay with options for the album
		holder.overlayLayout.setVisibility(View.VISIBLE);
		float elevationPx = 24 * context.getResources().getDisplayMetrics().density;
		holder.card.setCardElevation(elevationPx);
		holder.mainLayout.setClickable(false);
	}

	@Override
	public void hideActions(AlbumAdapter.AlbumView holder, Context context) {
		// Hide overlay
		holder.overlayLayout.setVisibility(View.INVISIBLE);
		float elevationPx = 4 * context.getResources().getDisplayMetrics().density;
		holder.card.setCardElevation(elevationPx);
		holder.mainLayout.setClickable(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu for the activity
		getMenuInflater().inflate(R.menu.menu_main, menu);

		// Set up search functionality
		MenuItem searchItem = menu.findItem(R.id.main_menu_item_search);
		SearchView searchView = (SearchView) searchItem.getActionView();

		// Set up search suggestions adapter
		searchView.setSuggestionsAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
				null, new String[] {"text"}, new int[] {android.R.id.text1}));

		ArrayList<String> allTagValues = albumInList.getAlbumTagValues();

		// Set up query hint and text change listener
		searchView.setQueryHint("Search");
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (albumInList.getPhotoSearch() == null) {
					albumInList.setPhotoSearch(albumInList.getPhotosTag(query));
					Intent intent = new Intent(MainActivity.this, SearchActivity.class);
					intent.setAction(Intent.ACTION_SEARCH);
					intent.putExtra(SearchManager.QUERY, query);
					startActivity(intent);
				}
				return true;
			}
			@Override
			public boolean onQueryTextChange(String newText) {
				albumInList.setPhotoSearch(null);
				MatrixCursor cursor = new MatrixCursor(new String[] {"_id", "text"});
				String[] array = allTagValues.stream().filter(str -> str.toLowerCase().startsWith(newText.toLowerCase())).toArray(String[]::new);
				for (int i = 0; i < array.length; i++) {
					cursor.addRow(new String[] {i + "" , array[i]});
				}
				searchView.getSuggestionsAdapter().changeCursor(cursor);
				return true;
			}
		});

		// Set up suggestion click listener
		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionSelect(int i) {
				Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(i);
				int colIndex = cursor.getColumnIndex("text");
				if (colIndex != -1) {
					String query = cursor.getString(colIndex);
					cursor.close();

					if (albumInList.getPhotoSearch() == null) {
						albumInList.setPhotoSearch(albumInList.getPhotosTag(query));
						Intent intent = new Intent(MainActivity.this, SearchActivity.class);
						intent.setAction(Intent.ACTION_SEARCH);
						intent.putExtra(SearchManager.QUERY, query);
						startActivity(intent);
					}
				}
				return true;
			}

			@Override
			public boolean onSuggestionClick(int i) {
				return onSuggestionSelect(i);
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.main_menu_item_search) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}