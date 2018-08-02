package com.example.anti2110.firestorepractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.anti2110.firestorepractice.model.Note;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText edtTitle, edtDescription;
    private TextView textData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");
    private DocumentReference noteRef = db.document("Notebook/My First Note");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        edtTitle = findViewById(R.id.edt_title);
        edtDescription = findViewById(R.id.edt_description);
        textData = findViewById(R.id.text_data);

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote(v);
            }
        });

        findViewById(R.id.btn_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNotes(v);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null) {
                    return;
                }

                String data = "";

                for(DocumentSnapshot ds : documentSnapshots) {
                    Note note = ds.toObject(Note.class);
                    note.setDocumentId(ds.getId());

                    String documentId = note.getDocumentId();
                    String title = note.getTitle();
                    String desc = note.getDescription();

                    data += "documentId" + documentId + "\nTitle" + title + "\nDescription: " + desc + "\n\n";
                }
                textData.setText(data);
            }
        });

    }

    private void loadNotes(View v) {
       notebookRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot documentSnapshots) {
               String data = "";
               for(DocumentSnapshot ds : documentSnapshots) {
                    Note note = ds.toObject(Note.class);
                    note.setDocumentId(ds.getId());

                    String documentId = note.getDocumentId();
                    String title = note.getTitle();
                    String desc = note.getDescription();

                    data += "documentId" + documentId + "\nTitle" + title + "\nDescription: " + desc + "\n\n";
                }

                textData.setText(data);
           }
       });
    }

    public void addNote(View v) {
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();

        Note note = new Note(title, description);

        notebookRef.add(note);

    }

}
