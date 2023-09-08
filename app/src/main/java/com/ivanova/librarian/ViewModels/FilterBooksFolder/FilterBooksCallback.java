package com.ivanova.librarian.ViewModels.FilterBooksFolder;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public interface FilterBooksCallback {
    void onCallback(Task<QuerySnapshot> task);
}
