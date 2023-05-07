package com.ivanova.librarian.ViewModels.Firestore;

import com.ivanova.librarian.Models.BookModel;

public interface FirestoreCallback {
    void onCallback(BookModel book);
}
