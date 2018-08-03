package com.example.anti2110.firestorepractice;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anti2110.firestorepractice.model.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText edtTitle, edtDescription;
    private TextView textData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.document("Notebook/My First Note");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTitle = findViewById(R.id.edt_title);
        edtDescription = findViewById(R.id.edt_description);
        textData = findViewById(R.id.text_data);

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(v);
            }
        });

        findViewById(R.id.btn_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNote(v);
            }
        });

        findViewById(R.id.btn_update_description).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDescription(v);
            }
        });

        findViewById(R.id.btn_delete_description).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDescription(v);
            }
        });

        findViewById(R.id.btn_delete_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote(v);
            }
        });

    }

    private void deleteNote(View v) {
        noteRef.delete();
    }

    private void deleteDescription(View v) {
        // Map<String, Object> note = new HashMap<>();
        // note.put(KEY_DESCRIPTION, FieldValue.delete());

        // noteRef.update(note);
        noteRef.update(KEY_DESCRIPTION, FieldValue.delete());
    }

    private void updateDescription(View v) {
        String description = edtDescription.getText().toString();

        // Map<String, Object> note = new HashMap<>();
        // note.put(KEY_DESCRIPTION, description);

        // noteRef.set(note);
        // noteRef.set(note, SetOptions.merge());
        // noteRef.update(note);

        noteRef.update(KEY_DESCRIPTION, description);
    }

    @Override
    protected void onStart() {
        super.onStart();

        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(e != null) {
                    Toast.makeText(MainActivity.this, "Error while Loading!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: " + e.getMessage());
                    return;
                }

                if(documentSnapshot.exists()) {
                    // String title = documentSnapshot.getString(KEY_TITLE);
                    // String description = documentSnapshot.getString(KEY_DESCRIPTION);

                    Note note = documentSnapshot.toObject(Note.class);

                    textData.setText("Title: " + note.getTitle() + "\n" + "Description: " + note.getDescription());
                } else {
                    textData.setText("");
                }
            }
        });
    }

    private void loadNote(View v) {
        noteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    // String title = documentSnapshot.getString(KEY_TITLE);
                    // String description = documentSnapshot.getString(KEY_DESCRIPTION);
                    // Map<String, String> note = documentSnapshot.getData();

                    Note note = documentSnapshot.toObject(Note.class);

                    textData.setText("Title: " + note.getTitle() + "\n" + "Description: " + note.getDescription());
                } else {
                    Toast.makeText(MainActivity.this, "Document does not exist!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+e.toString());
            }
        });
    }

    public void saveNote(View v) {
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();

//        Map<String, String> note = new HashMap<>();
//        note.put(KEY_TITLE, title);
//        note.put(KEY_DESCRIPTION, description);

        Note note = new Note(title, description, 0);
        noteRef.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: "+e.toString());
                    }
                });
    }

}
