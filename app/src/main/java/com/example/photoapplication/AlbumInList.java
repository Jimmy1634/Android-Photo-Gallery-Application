package com.example.photoapplication;

import android.content.Context;
import java.io.*;
import java.util.*;

public class AlbumInList{
	private static AlbumInList instance = null;
	private ArrayList<Album> albums; // List of albums
	private ArrayList<Photo> photoSearch; // List of photos for search results

	// Private constructor
	private AlbumInList() {
		albums = new ArrayList<>();
		photoSearch = null;
	}

	// Getter and setter for photoSearch
	public ArrayList<Photo> getPhotoSearch() { return photoSearch; }
	public void setPhotoSearch(ArrayList<Photo> results) { photoSearch = results; }

	// Method to get an album by its name
	public Album getAlbum(Album album) {
		for(Album a: albums){
			if(a.equals(album)){
				return a;
			}
		}
		return null;
	}

	// Method to get the list of albums
	public ArrayList<Album> getAlbums() { return albums; }

	// Singleton instance getter
	public static AlbumInList getInstance() {
		if (instance == null) {
			instance = new AlbumInList();
		}
		return instance;
	}

	// Method to add a new album
	public boolean addAlbum(String name) {
		for (Album a : albums) {
			if (a.getName().equals(name)) {
				return false; // Album with the same name already exists
			}
		}
		return albums.add(new Album(name));
	}

	// Method to remove an album
	public boolean removeAlbum(Album album) { return albums.remove(album); }

	// Method to get unique tag values from all albums
	public ArrayList<String> getAlbumTagValues(){
		ArrayList<String> results = new ArrayList<>();
		for(Album a: albums){
			results.addAll(a.getTagValues());
		}
		Set<String> set = new LinkedHashSet<>(results);
		results.clear();
		results.addAll(set);
		return results;
	}

	// Method to get photos with a specific tag value
	public ArrayList<Photo> getPhotosTag(String tagValue){
		ArrayList<Photo> photoArrayList = new ArrayList<>();
		Set<Photo> photoSet = new LinkedHashSet<>();
		for(Album a: albums){
			for(Photo p: a.getPhotos()){
				if(p.getPersonTag().startsWithTag(tagValue) || p.getLocationTag().startsWithTag(tagValue)){
					photoSet.add(p);
				}
			}
		}
		photoArrayList.addAll(photoSet);
		return photoArrayList;
	}

	public ArrayList<Photo> getBothPhotosTag(String person, String location){
		ArrayList<Photo> photoArrayList = new ArrayList<>();
		Set<Photo> photoSet = new LinkedHashSet<>();
		for(Album a: albums){
			for(Photo p: a.getPhotos()){
				if(p.getPersonTag().startsWithTag(person) && p.getLocationTag().startsWithTag(location)){
					photoSet.add(p);
				}
			}
		}
		photoArrayList.addAll(photoSet);
		return photoArrayList;
	}

	public ArrayList<Photo> getEitherPhotosTag(String person, String location){
		ArrayList<Photo> photoArrayList = new ArrayList<>();
		Set<Photo> photoSet = new LinkedHashSet<>();
		for(Album a: albums){
			for(Photo p: a.getPhotos()){
				if(p.getPersonTag().startsWithTag(person) || p.getLocationTag().startsWithTag(location)){
					photoSet.add(p);
				}
			}
		}
		photoArrayList.addAll(photoSet);
		return photoArrayList;
	}



	// Method to serialize albums to file
	public void serialize(Context context) {
		try (ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput("albums.dat", Context.MODE_PRIVATE))) {
			oos.writeObject(albums);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to deserialize albums from file
	public void deserialize(Context context) {
		albums = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput("albums.dat"))) {
			albums = (ArrayList<Album>) ois.readObject();
		} catch (FileNotFoundException e) {
			// File not created yet, ignore
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}