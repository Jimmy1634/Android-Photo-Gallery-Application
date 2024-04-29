package com.example.photoapplication;

import androidx.annotation.NonNull;
import java.io.*;
import java.util.*;

public class Album implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private ArrayList<Photo> photos;
	private int index; // Index of the currently selected photo

	// Constructor to initialize an album with a name
	public Album(String name) {
		this.name = name;
		photos = new ArrayList<>();
		index = 0; // Initialize index to zero
	}

	// Method to add a photo to the album
	public boolean addPhoto(Photo photo) {
		if (photos.contains(photo)) {
			return false; // Photo already exists in the album
		}
		boolean add = photos.add(photo);
		if (add) {
			setIndex(photos.size() - 1); // Update the index to the newly added photo
		}
		return add;
	}

	// Method to remove a photo from the album
	public boolean removePhoto(Photo photo) {
		boolean delete = photos.remove(photo);
		if (delete && index == photos.size()) {
			setIndex(index - 1); // Update the index if the last photo is deleted
		}
		return delete;
	}

	// Method to get all unique tag values from photos in the album
	public ArrayList<String> getTagValues() {
		ArrayList<String> tags = new ArrayList<>();
		for (Photo p : photos) {
			tags.addAll(p.getTagValues());
		}
		// Remove duplicates by converting to a Set and back to a List
		Set<String> set = new LinkedHashSet<>(tags);
		tags.clear();
		tags.addAll(set);
		return tags;
	}

	// Getter and setter methods
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public ArrayList<Photo> getPhotos() { return photos; }
	public int getIndex() { return index; }
	public void setPhotos(ArrayList<Photo> photos) { this.photos = photos; }

	// Method to set the index of the currently selected photo
	public void setIndex(int index) {
		// Ensure index is within valid range
		if (index >= 0 && index < photos.size()) {
			this.index = index;
		}
	}

	// Override equals method to compare albums by name
	@Override
	public boolean equals(Object o) {
		if (o instanceof Album) {
			return name.equals(((Album) o).getName());
		}
		return false;
	}

	// Override toString method to return album name
	@NonNull
	@Override
	public String toString() {
		return name;
	}
}