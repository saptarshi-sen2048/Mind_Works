package com.example.mind_works;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mind_works.modelsNotes.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTakerActivity extends AppCompatActivity {

    EditText editTextTitle, editTextNotes;
    ImageView imageViewSave;
    Notes notes;
    boolean isOld = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taker_new);

        imageViewSave = findViewById(R.id.imageViewSave);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextNotes = findViewById(R.id.editTextNotes);
        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("oldNote");
            editTextTitle.setText(notes.getTitle());
            editTextNotes.setText(notes.getNotes());
            isOld = true;
        }catch(Exception e){
            e.printStackTrace();
        }



        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                String description = editTextNotes.getText().toString();

                if(description.isEmpty()){
                    Toast.makeText(NotesTakerActivity.this, "Add some of your thoughts...", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat formatter =  new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date =  new Date();

                if(!isOld)
                {
                    notes = new Notes();
                }

                notes.setTitle(title);
                notes.setNotes(description);
                notes.setDate(formatter.format(date));

                Intent intent = new Intent();
                intent.putExtra("note",notes);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}