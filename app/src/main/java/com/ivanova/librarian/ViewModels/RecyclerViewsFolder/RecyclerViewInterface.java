package com.ivanova.librarian.ViewModels.RecyclerViewsFolder;

import com.ivanova.librarian.ViewModels.BookViewModelFolder.BookViewModel;

import java.util.ArrayList;

public interface RecyclerViewInterface {
    void onBookItemClick(ArrayList<BookViewModel> bookVMs, int position);
}
