package com.ivanova.librarian.ViewModels.SendCommentFolder;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.ivanova.librarian.Models.CommentModel;
import com.ivanova.librarian.ViewModels.BookViewModelFolder.FirestoreCallback;
import com.ivanova.librarian.ViewModels.CommentViewModelFolder.CommentViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SendComment {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void sendComment(int bookID, EditText et_comment, ArrayList<CommentViewModel> comments, RecyclerView.Adapter recyclerViewAdapter) {
        getCommentData(bookID, et_comment, comments, recyclerViewAdapter);
    }

    private static void getCommentData(int bookID, EditText et_comment, ArrayList<CommentViewModel> comments, RecyclerView.Adapter recyclerViewAdapter) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        String userID = currentUser.getUid();

        CommentModel comment = new CommentModel();
        comment.setUserID(userID);
        comment.setText(et_comment.getText().toString());
        comment.setDate(Timestamp.now());

        getUserName(userID, comment, new FirestoreCallback() {
            @Override
            public void onCallback() {
                sendCommentData(bookID, comment);

//                comments.add(new CommentViewModel(comment));
//                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private static void sendCommentData(int bookID, CommentModel comment) {
        Map<String, Object> data = new HashMap<>();
        data.put("userID", comment.getUserID());
        data.put("userName", comment.getUserName());
        data.put("date", comment.getDateTimestamp());
        data.put("text", comment.getText());

        db.collection("books").document(String.valueOf(bookID))
                .collection("comments").add(data);
    }

    private static void getUserName(String userID, CommentModel comment, FirestoreCallback firestoreCallback) {
        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        comment.setUserName(document.get("name").toString());
                        firestoreCallback.onCallback();

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
