package com.ivanova.librarian.ViewModels.RecyclerViews;

import com.ivanova.librarian.Models.BookModel;

import java.util.ArrayList;

public interface RecyclerViewInterface {
    void onBookItemClick(ArrayList<BookModel> books, int position);
}
