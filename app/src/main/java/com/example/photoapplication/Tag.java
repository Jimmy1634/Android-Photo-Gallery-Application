package com.example.photoapplication;

import androidx.annotation.NonNull;
import java.io.*;
import java.util.*;

public class Tag implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name; // Name of the tag
	private ArrayList<String> values; // List of values associated with the tag

	// Constructor to initialize a tag with a name
	public Tag(String name) {
		this.name = name;
		values = new ArrayList<>();
	}

	// Method to add a value to the tag
	public boolean addTag(String value) {
		// Check if the value already exists in the list
		boolean contains = values.stream().anyMatch(str -> str.equalsIgnoreCase(value));

		if (contains) {
			return false; // Value already exists, return false
		}
		// If the tag is a location and already has one value, replace it with the new value
		if (name.equals("location") && values.size() == 1) {
			values.set(0, value);
			return true;
		}

		return values.add(value); // Add the new value to the list
	}

	// Method to remove a value from the tag
	public void removeTag(String value) {
		values.removeIf(str -> str.equalsIgnoreCase(value)); // Remove the value from the list
	}

	// Getter and setter methods for name and values
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getValues() {
		return values;
	}

	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	// Method to check if any value of the tag starts with the given query
	public boolean startsWithTag(String query){
		for(String s: values){
			if(s.toLowerCase().startsWith(query)){
				return true;
			}
		}
		return false;
	}

	// Override equals method to compare tags by name and values
	@Override
	public boolean equals(Object o) {
		if (o instanceof Tag) {
			Tag tag = (Tag) o;
			return name.equals(tag.getName()) && values.equals(tag.getValues());
		}
		return false;
	}

	// Override toString method to return a string representation of the tag
	@NonNull
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder(" ");
		for(int i = 0; i < values.size(); i++){
			res.append(values.get(i)).append(i != values.size() - 1 ? " | " : "");
		}
		return res.toString();
	}
}