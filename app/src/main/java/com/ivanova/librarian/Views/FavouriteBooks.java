package com.ivanova.librarian.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ivanova.librarian.R;
import com.ivanova.librarian.ViewModels.BookCatalogsFolder.BookCatalogs;
import com.ivanova.librarian.ViewModels.BookViewModelFolder.BookViewModel;
import com.ivanova.librarian.ViewModels.CommonFeaturesFolder.CommonFeatures;
import com.ivanova.librarian.ViewModels.RecyclerViewsFolder.RecyclerViewAdapter;
import com.ivanova.librarian.ViewModels.RecyclerViewsFolder.RecyclerViewInterface;

import java.util.ArrayList;

public class FavouriteBooks extends AppCompatActivity implements RecyclerViewInterface {

    private BottomNavigationView menu;
    private TextView tv_noFavouriteBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_books);

        initNoFavBooksTextView();
        initMenu();
        initFavouriteBookList();
    }

    private void initFavouriteBookList() {
        ArrayList<BookViewModel> books = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.booksRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter recyclerViewAdapter = new RecyclerViewAdapter(books, FavouriteBooks.this, FavouriteBooks.this);
        recyclerView.setAdapter(recyclerViewAdapter);

        BookCatalogs.getFavouriteBooks(books, this, recyclerView, recyclerViewAdapter, tv_noFavouriteBooks);
    }

    @Override
    public void onBookItemClick(ArrayList<BookViewModel> bookVMs, int position) {
        CommonFeatures.onBookItemClick(this, bookVMs, position);
    }

    private void initNoFavBooksTextView() {
        tv_noFavouriteBooks = findViewById(R.id.tv_noFavouriteBooks);
        tv_noFavouriteBooks.setVisibility(View.GONE);
    }

    private void initMenu() {
        menu = findViewById(R.id.bottomNavigation);
        CommonFeatures.initMenu(this, menu, new boolean[]{false, false, false, true, false}, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        menu.getMenu().getItem(3).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
