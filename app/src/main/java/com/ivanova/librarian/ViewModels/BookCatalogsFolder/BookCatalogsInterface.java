package com.ivanova.librarian.ViewModels.BookCatalogsFolder;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.ivanova.librarian.ViewModels.BookViewModelFolder.BookViewModel;

import java.util.ArrayList;

@FunctionalInterface
public interface BookCatalogsInterface {
    void getCatalog(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter);
}
