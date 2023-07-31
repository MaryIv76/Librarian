package com.ivanova.librarian.ViewModels.Firestore;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

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

public class ReadBooksData {

    public static void readImage(ImageCallback imageCallback, DocumentSnapshot document) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(document.get("image").toString());
        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            imageCallback.onCallback(bitmap);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readData(FirestoreCallback firestoreCallback, FirebaseFirestore db, int bookID) {
        DocumentReference docRef = db.collection("books").document(String.valueOf(bookID));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        BookModel book = new BookModel();
                        book.setId(bookID);
                        book.setISBN(document.get("ISBN").toString());
                        book.setAuthor(document.get("author").toString());
                        book.setBookName(document.get("name").toString());
                        book.setYear(document.get("year").toString());
                        book.setGenre(document.get("genre").toString());
                        book.setAnnotation(document.get("annotation").toString());
                        book.setRating(Double.parseDouble(document.get("rating").toString()));

                        readImage(new ImageCallback() {
                            @Override
                            public void onCallback(Bitmap image) {
                                book.setImage(image);
                                firestoreCallback.onCallback(book);
                            }
                        }, document);

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
