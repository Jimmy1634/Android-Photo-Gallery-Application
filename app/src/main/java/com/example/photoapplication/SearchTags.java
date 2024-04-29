package com.example.photoapplication;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchTags extends AppCompatActivity {
    private AlbumInList albumInList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_tags);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        albumInList = AlbumInList.getInstance();

        Button back = findViewById(R.id.backbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchTags.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ArrayList<String> allTags = (ArrayList<String>) getIntent().getExtras().getSerializable("allTags");
        String[] tags = new String[allTags.size()];
        for (int i = 0; i < tags.length; i++){
            tags[i] = allTags.get(i);
        }

        AutoCompleteTextView searchbar1 = findViewById(R.id.searchbar1);
        AutoCompleteTextView searchbar2 = findViewById(R.id.searchbar2);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, tags);
        searchbar1.setAdapter(adapter);
        searchbar2.setAdapter(adapter);

        Button search = findViewById(R.id.searchbutton);


        //IMPLEMENTATION OF CONJUNCTION AND DISJUNCTION TO PASS THROUGH INTENT
        RadioGroup radioGroup = findViewById(R.id.radiogroup);
        boolean[] searchBoth = {false};
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.eitheror) {
                    searchBoth[0] = false;
                    Log.d("searchBoth", "false");
                } else if (checkedId == R.id.both) {
                    searchBoth[0] = true;
                    Log.d("searchBoth", "true");
                }
            }


        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personTag = searchbar1.getText().toString();
                String locationTag = searchbar2.getText().toString();

                if (personTag.equals("") && locationTag.equals("")){
                    albumInList.setPhotoSearch(albumInList.getPhotosTag("USERDIDNOTENTERANYVALUESSODONOTRETURNANYPHOTOS"));
                }
                else if (personTag.equals("")){
                    albumInList.setPhotoSearch(albumInList.getPhotosTag(locationTag));
                }
                else if (locationTag.equals("")){
                    albumInList.setPhotoSearch(albumInList.getPhotosTag(personTag));
                }
                else if (searchBoth[0] == true){
                    albumInList.setPhotoSearch(albumInList.getBothPhotosTag(personTag, locationTag));
                }
                else if (searchBoth[0] == false){
                    albumInList.setPhotoSearch(albumInList.getEitherPhotosTag(personTag, locationTag));
                }

                Intent intent = new Intent(SearchTags.this, SearchActivity.class);
                startActivity(intent);
            }
        });

    }


}