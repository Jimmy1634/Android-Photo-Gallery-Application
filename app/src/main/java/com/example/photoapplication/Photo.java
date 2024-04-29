package com.example.photoapplication;

import java.io.*;
import java.util.*;

public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String fileName; // File name of the photo
	private Serializer bitmap; // Serialized bitmap data
	private Tag personTag, locationTag; // Tags associated with the photo

	// Constructor to initialize a photo with a file name and serialized bitmap
	public Photo(String fileName, Serializer bitmapSerialized) {
		this.fileName = fileName;
		this.bitmap = bitmapSerialized;
		personTag = new Tag("person"); // Initialize person tag
		locationTag = new Tag("location"); // Initialize location tag
	}

	// Getter and setter methods for file name, bitmap, and tags
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Serializer getBitmap() {
		return bitmap;
	}
	public void setBitmap(Serializer bitmapSerialized) {
		this.bitmap = bitmapSerialized;
	}
	public Tag getPersonTag() {
		return personTag;
	}
	public void setPersonTag(Tag personTag) {
		this.personTag = personTag;
	}
	public Tag getLocationTag() {
		return locationTag;
	}
	public void setLocationTag(Tag locationTag) {
		this.locationTag = locationTag;
	}

	// Method to get all tag values associated with the photo
	public ArrayList<String> getTagValues(){
		ArrayList<String> results = new ArrayList<>();
		results.addAll(personTag.getValues()); // Add person tag values
		results.addAll(locationTag.getValues()); // Add location tag values
		return results;
	}

	// Override equals method to compare photos by file name
	@Override
	public boolean equals(Object o) {
		if (o instanceof Photo) {
			return fileName.equals(((Photo) o).getFileName());
		}
		return false;
	}
}