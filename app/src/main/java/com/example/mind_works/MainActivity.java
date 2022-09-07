package com.example.mind_works;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mind_works.adapters.Notes_list_adapter;
import com.example.mind_works.dataBase.RoomDB;
import com.example.mind_works.modelsNotes.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    RecyclerView recyclerView;
    Notes_list_adapter notesListAdapter;
    List<Notes>  notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton floatingActionButton;
    SearchView searchViewHome;
    Notes selectedNote;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_home);
        floatingActionButton = findViewById(R.id.FloatingActionButton_add);
        searchViewHome = findViewById(R.id.searchViewHome);

        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAll();

        updateRecycler(notes);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
                startActivityForResult(intent, 101);
            }
        });
        searchViewHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

;    }

    private void filter(String newText) {
        List<Notes> filteredList = new ArrayList<>();
        for(Notes singleNote : notes){
            if(singleNote.getTitle().toLowerCase().contains(newText.toLowerCase()) || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(singleNote);
            }
        }
        notesListAdapter.filterList(filteredList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==101){
            if(resultCode == Activity.RESULT_OK){
                Notes newNotes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(newNotes);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
        else if(requestCode==102)
        {
            if(resultCode==Activity.RESULT_OK){
                Notes newNotes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(newNotes.getID(), newNotes.getTitle(), newNotes.getNotes());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new Notes_list_adapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }
    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            intent.putExtra("oldNote",notes);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = notes;
            showPopup(cardView);


        }
    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.pin:
                if(selectedNote.isPinned()){
                    database.mainDAO().pin(selectedNote.getID(), false);
                    Toast.makeText(MainActivity.this, "Unpinned :D", Toast.LENGTH_SHORT).show();
                }
                else{
                    database.mainDAO().pin(selectedNote.getID(), true);
                    Toast.makeText(MainActivity.this, "Pinned :D", Toast.LENGTH_SHORT).show();
                }
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                return true;

            case R.id.delete:
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Deleted :(", Toast.LENGTH_SHORT).show();
                return true;
            default:return false;
        }

    }
}