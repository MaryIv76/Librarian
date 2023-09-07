package com.ivanova.librarian.ViewModels.CommentViewModelFolder;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ivanova.librarian.Models.CommentModel;
import com.ivanova.librarian.R;
import com.ivanova.librarian.ViewModels.BookViewModelFolder.BookViewModel;
import com.ivanova.librarian.ViewModels.BookViewModelFolder.FirestoreCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CommentsList {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static void getComments(int bookID, ArrayList<String> commentIDs, ArrayList<CommentViewModel> comments, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
        recyclerView.setAdapter(recyclerViewAdapter);

        for (String id : commentIDs) {
            CommentViewModel commentViewModel = new CommentViewModel();
            comments.add(commentViewModel);
            commentViewModel.readCommentData(bookID, id, comments.size() - 1, recyclerViewAdapter);
            recyclerViewAdapter.notifyItemInserted(comments.size() - 1);
        }
    }

    public static void listenToCommentsUpdates(int bookID, ArrayList<CommentViewModel> comments, HashSet<String> commentsIDs, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter, TextView tv_commentsNumber, TextView tv_commentsLabel) {
        db.collection("books").document(String.valueOf(bookID))
                .collection("comments").orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        ArrayList<String> ids = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (!commentsIDs.contains(doc.getId())) {
                                ids.add(doc.getId());
                                commentsIDs.add(doc.getId());
                            }
                        }

                        getComments(bookID, ids, comments, recyclerView, recyclerViewAdapter);
                        setCommentsNumber(commentsIDs.size(), tv_commentsNumber, tv_commentsLabel);
                    }
                });
    }

    private static void setCommentsNumber(int commentsNumber, TextView tv_commentsNumber, TextView tv_commentsLabel) {
        tv_commentsNumber.setText(String.valueOf(commentsNumber));
        setCommentsLabelAccordingToNumber(commentsNumber, tv_commentsLabel);
    }

    private static void setCommentsLabelAccordingToNumber(int commentsNumber, TextView tv_commentsLabel) {
        HashSet<Integer> hashSet = new HashSet<>();
        hashSet.add(11);
        hashSet.add(12);
        hashSet.add(13);
        hashSet.add(14);
        hashSet.add(15);
        hashSet.add(16);
        hashSet.add(17);
        hashSet.add(18);
        hashSet.add(19);

        HashSet<Integer> hashSet1 = new HashSet<>();
        hashSet1.add(0);
        hashSet1.add(5);
        hashSet1.add(6);
        hashSet1.add(7);
        hashSet1.add(8);
        hashSet1.add(9);

        HashSet<Integer> hashSet2 = new HashSet<>();
        hashSet2.add(2);
        hashSet2.add(3);
        hashSet2.add(4);

        if (hashSet.contains(commentsNumber % 100) || hashSet1.contains(commentsNumber % 10)) {
            tv_commentsLabel.setText(R.string.comments_0);

        } else if (commentsNumber % 10 == 1) {
            tv_commentsLabel.setText(R.string.comments_1);

        } else if (hashSet2.contains(commentsNumber % 10)) {
            tv_commentsLabel.setText(R.string.comments_2);
        }
    }
}
