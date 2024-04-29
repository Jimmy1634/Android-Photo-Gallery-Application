package com.example.photoapplication;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import java.util.*;

public class SearchActivity extends AppCompatActivity implements PhotoAdapter.PhotoAction {
    // Instance variables
    private AlbumInList albumInList;
    private PhotoAdapter adapter;
    private TextView text;
    private List<Photo> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Get the search query from intent
        //String query = getIntent().getExtras().getString(SearchManager.QUERY);

        // Initialize variables
        albumInList = AlbumInList.getInstance();
        results = albumInList.getPhotoSearch();

        // Initialize UI components
        Toolbar searchToolbar = findViewById(R.id.search_toolbar);
        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        text = findViewById(R.id.search_text_empty);

        // Set toolbar title and back button
        searchToolbar.setTitle("Photo");
        setSupportActionBar(searchToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        // Set up adapter for RecyclerView
        adapter = new PhotoAdapter();
        adapter.setAction(this);
        recyclerView.setAdapter(adapter);

        // Show text view if search results are empty
        if(results.isEmpty()) {
            text.setVisibility(View.VISIBLE);
        }
        adapter.setPhotoList(results);
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

    // PhotoAction interface methods (not implemented in this SearchActivity)
    @Override
    public void removePhoto(int position, PhotoAdapter.PhotoView view, Context context) { return; }

    @Override
    public void openPhoto(int position) { return; }

    @Override
    public void showActions(PhotoAdapter.PhotoView view, Context context) { return; }

    @Override
    public void hideActions(PhotoAdapter.PhotoView view, Context context) { return; }
}
