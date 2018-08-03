package com.example.anti2110.firestorepractice;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.anti2110.firestorepractice.model.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText edtTitle, edtDescription, edtPriority;
    private TextView textData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");
    private DocumentReference noteRef = db.document("Notebook/My First Note");
    private DocumentSnapshot lastResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        edtTitle = findViewById(R.id.edt_title);
        edtDescription = findViewById(R.id.edt_description);
        edtPriority = findViewById(R.id.edt_priority);
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

                for(DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    String id = documentSnapshot.getId();
                    int oldIndex = dc.getOldIndex();
                    int newIndex = dc.getNewIndex();

                    switch (dc.getType()) {
                        case ADDED:
                            textData.append("\nAdded: " + id + "\noldIndex: " + oldIndex + "newIndex: " + newIndex);
                            break;
                        case MODIFIED:
                            textData.append("\nModified: " + id + "\noldIndex: " + oldIndex + "newIndex: " + newIndex);
                            break;
                        case REMOVED:
                            textData.append("\nRemoved: " + id + "\noldIndex: " + oldIndex + "newIndex: " + newIndex);
                            break;
                    }
                }

            }
        });

//        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                if(e != null) {
//                    return;
//                }
//
//                String data = "";
//
//                for(DocumentSnapshot ds : documentSnapshots) {
//                    Note note = ds.toObject(Note.class);
//                    note.setDocumentId(ds.getId());
//
//                    String documentId = note.getDocumentId();
//                    String title = note.getTitle();
//                    String desc = note.getDescription();
//                    int priority = note.getPriority();
//
//                    data += "documentId" + documentId + "\nTitle" + title + "\nDescription: " + desc + "\nPriority: " + priority + "\n\n";
//                }
//                textData.setText(data);
//            }
//        });

    }

    private void loadNotes(View v) {
        Query query;
        if(lastResult == null) {
            query = notebookRef.orderBy("priority").limit(3);
        } else {
            query = notebookRef.orderBy("priority").startAfter(lastResult).limit(3);
        }

        query.get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    String data = "";

                    for(DocumentSnapshot ds : documentSnapshots) {
                        Note note = ds.toObject(Note.class);
                        note.setDocumentId(ds.getId());

                        String documentId = note.getDocumentId();
                        String title = note.getTitle();
                        String desc = note.getDescription();
                        int priority = note.getPriority();

                        data += "documentId" + documentId + "\nTitle" + title + "\nDescription: " + desc + "\nPriority: " + priority + "\n\n";
                    }
                    if(documentSnapshots.size() > 0) {
                        data += "-----------------------------";
                        textData.append(data);
                        lastResult = documentSnapshots.getDocuments().get(documentSnapshots.size()-1);
                    }
                }
            });

//        Task task1 =  notebookRef.whereLessThan("priority", 2)
//                .orderBy("priority")
//                .get();
//
//        Task task2 = notebookRef.whereGreaterThan("priority", 2)
//                .orderBy("priority")
//                .get();
//
//        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
//        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
//            @Override
//            public void onSuccess(List<QuerySnapshot> querySnapshots) {
//                String data = "";
//
//                for(QuerySnapshot documentSnapshots : querySnapshots) {
//                    for (DocumentSnapshot ds : documentSnapshots) {
//                        Note note = ds.toObject(Note.class);
//                        note.setDocumentId(ds.getId());
//
//                        String documentId = note.getDocumentId();
//                        String title = note.getTitle();
//                        String desc = note.getDescription();
//                        int priority = note.getPriority();
//
//                        data += "documentId" + documentId + "\nTitle" + title + "\nDescription: " + desc + "\nPriority: " + priority + "\n\n";
//                    }
//                }
//                textData.setText(data);
//            }
//        });

       /*notebookRef.whereGreaterThanOrEqualTo("priority", 2)
               .whereEqualTo("title", "test1")
               .orderBy("priority", Query.Direction.DESCENDING)
               .get()
               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot documentSnapshots) {
               String data = "";
               for(DocumentSnapshot ds : documentSnapshots) {
                    Note note = ds.toObject(Note.class);
                    note.setDocumentId(ds.getId());

                    String documentId = note.getDocumentId();
                    String title = note.getTitle();
                    String desc = note.getDescription();
                    int priority = note.getPriority();

                    data += "documentId" + documentId + "\nTitle" + title + "\nDescription: " + desc + "\nPriority: " + priority + "\n\n";
                }

                textData.setText(data);
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {

           }
       });*/
    }

    public void addNote(View v) {
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();

        if(edtPriority.length() == 0) {
            edtPriority.setText("0");
        }

        int priority = Integer.parseInt(edtPriority.getText().toString());

        Note note = new Note(title, description, priority);

        notebookRef.add(note);

    }

}
