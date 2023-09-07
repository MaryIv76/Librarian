package com.ivanova.librarian.ViewModels.CommentViewModelFolder;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ivanova.librarian.Models.CommentModel;
import com.ivanova.librarian.ViewModels.BookViewModelFolder.FirestoreCallback;

public class CommentViewModel {

    private CommentModel comment;

    private FirebaseFirestore db;

    public CommentViewModel() {
        this.comment = new CommentModel();
        this.db = FirebaseFirestore.getInstance();
    }

    public CommentViewModel(CommentModel comment) {
        this.comment = comment;
        this.db = FirebaseFirestore.getInstance();
    }

    public CommentModel getComment() {
        return this.comment;
    }

    public void readCommentData(int bookID, String commentID, Integer position, RecyclerView.Adapter recyclerViewAdapter) {
        readData(new FirestoreCallback() {
            @Override
            public void onCallback() {
                recyclerViewAdapter.notifyItemChanged(position);
            }
        }, bookID, commentID);
    }

    private void readData(FirestoreCallback firestoreCallback, int bookID, String commentID) {
        DocumentReference docRef = db.collection("books").document(String.valueOf(bookID)).collection("comments").document(commentID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        comment.setUserName(document.get("userName").toString());
                        comment.setDate((Timestamp) document.get("date"));
                        comment.setText(document.get("text").toString());

                        firestoreCallback.onCallback();

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "Get failed with ", task.getException());
                }
            }
        });
    }
}
