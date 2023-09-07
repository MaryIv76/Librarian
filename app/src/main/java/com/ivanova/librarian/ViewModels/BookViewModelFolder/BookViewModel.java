package com.ivanova.librarian.ViewModels.BookViewModelFolder;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ivanova.librarian.Models.BookModel;

import java.io.File;
import java.io.IOException;

public class BookViewModel {

    private BookModel book;

    private FirebaseFirestore db;

    public BookViewModel(Context context) {
        this.book = new BookModel(context);
        this.db = FirebaseFirestore.getInstance();
    }

    public BookModel getBook() {
        return this.book;
    }

    public void readAllBookData(int bookID, Integer position, RecyclerView.Adapter recyclerViewAdapter) {
        readData(new FirestoreCallback() {
            @Override
            public void onCallback() {
                recyclerViewAdapter.notifyItemChanged(position);
            }
        }, bookID);

        readImage(new FirestoreCallback() {
            @Override
            public void onCallback() {
                recyclerViewAdapter.notifyItemChanged(position);
            }
        }, bookID);
    }

    private void readImage(FirestoreCallback firestoreCallback, int bookID) {
        DocumentReference docRef = db.collection("books").document(String.valueOf(bookID));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(document.get("image").toString());
                        try {
                            File localFile = File.createTempFile("tempfile", ".jpg");
                            storageReference.getFile(localFile)
                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                            book.setImage(bitmap);
                                            firestoreCallback.onCallback();
                                        }
                                    });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "Get failed with ", task.getException());
                }
            }
        });
    }

    private void readData(FirestoreCallback firestoreCallback, int bookID) {
        DocumentReference docRef = db.collection("books").document(String.valueOf(bookID));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        book.setId(bookID);
                        book.setISBN(document.get("ISBN").toString());
                        book.setAuthor(document.get("author").toString());
                        book.setBookName(document.get("name").toString());
                        book.setYear(document.get("year").toString());
                        book.setGenre(document.get("genre").toString());
                        book.setAnnotation(document.get("annotation").toString());
                        book.setRating(Double.parseDouble(document.get("rating").toString()));

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
