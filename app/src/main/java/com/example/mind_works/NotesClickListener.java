package com.example.mind_works;

import androidx.cardview.widget.CardView;

import com.example.mind_works.modelsNotes.Notes;

public interface NotesClickListener {
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
