package com.ivanova.librarian.ViewModels;

import com.ivanova.librarian.Models.BookModel;

import java.util.ArrayList;

public interface HomePageRecyclerViewInterface {
    void onBookItemClick(ArrayList<BookModel> books, int position);
}
