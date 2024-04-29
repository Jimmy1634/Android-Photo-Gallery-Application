package com.example.photoapplication;

import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TagActivity extends BottomSheetDialogFragment {
	// Instance variables for album, photo, and tags
	private AlbumInList albumInList;
	private Album album;
	private Photo photo;
	private Tag location;
	private Tag person;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.activity_tag, container, false);

		// Initialize UI components
		ImageButton closeButton = view.findViewById(R.id.tags_button_close);
		ImageButton editPersonButton = view.findViewById(R.id.tags_button_edit_person);
		ImageButton editLocationButton = view.findViewById(R.id.tags_button_edit_location);
		TextView tagPersonTextView = view.findViewById(R.id.tags_text_person);
		TextView tagLocationTextView = view.findViewById(R.id.tags_text_location);

		// Get arguments passed to the fragment
		Bundle args = getArguments();
		albumInList = AlbumInList.getInstance();
		assert args != null;
		int albumIndex = args.getInt(MainActivity.INDEX);
		album = albumInList.getAlbums().get(albumIndex);
		photo = album.getPhotos().get(album.getIndex());
		location = photo.getLocationTag();
		person = photo.getPersonTag();

		// Set text for person and location tags
		tagPersonTextView.setText("Person: " + person.toString());
		tagLocationTextView.setText("Location: " + location.toString());

		// Set click listener for editing person tag
		editPersonButton.setOnClickListener(v -> {
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
			builder.setTitle("Edit Person");
			builder.setMessage("Enter person to add or remove from this photo.");

			EditText inputVal = new EditText(getContext());
			inputVal.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(inputVal);

			// Add person tag or remove if already exists
			builder.setPositiveButton("Add", (dialog, which) ->{
				boolean changed = person.addTag(inputVal.getText().toString().trim());
				tagPersonTextView.setText("Person: " + person.toString());
				if(changed)
					albumInList.serialize(getContext());
			});

			builder.setNegativeButton("Remove", (dialog, which)->{
				person.removeTag(inputVal.getText().toString().trim());
				tagPersonTextView.setText("Person: " + person.toString());
				albumInList.serialize(getContext());
			});

			builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

			builder.show();
		});

		// Set click listener for editing location tag
		editLocationButton.setOnClickListener(v-> {
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
			builder.setTitle("Edit Location");
			builder.setMessage("Enter location to add or remove from this photo.");

			EditText inputVal = new EditText(getContext());
			inputVal.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(inputVal);

			// Add location tag or remove if already exists
			builder.setPositiveButton("Add", (dialog, which) ->{
				boolean changed = location.addTag(inputVal.getText().toString().trim());
				tagLocationTextView.setText("Location: " + location.toString());
				if(changed)
					albumInList.serialize(getContext());
			});

			builder.setNegativeButton("Remove", (dialog, which)->{
				location.removeTag(inputVal.getText().toString().trim());
				tagLocationTextView.setText("Location: " + location.toString());
				albumInList.serialize(getContext());
			});

			builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

			builder.show();
		});

		// Set click listener for close button to dismiss the dialog
		closeButton.setOnClickListener(v -> dismiss());

		return view;
	}
}
