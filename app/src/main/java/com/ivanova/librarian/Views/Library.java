package com.ivanova.librarian.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

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

public class Library extends AppCompatActivity implements RecyclerViewInterface {

    private BottomNavigationView menu;
    private Spinner spinner;

    private ImageButton searchBtn;
    private boolean[] searchIsEnabled = new boolean[1];
    private ImageButton filtersBtn;
    private RelativeLayout searchEditTextLayout;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        initSpinner();
        initMenu();
        initSearch();

        initBookList();
    }

    private void initBookList() {
        ArrayList<BookViewModel> books = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.booksRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter recyclerViewAdapter = new RecyclerViewAdapter(books, Library.this, Library.this);
        recyclerView.setAdapter(recyclerViewAdapter);

        BookCatalogs.getAllBooks(books, this, recyclerView, recyclerViewAdapter);
    }

    @Override
    public void onBookItemClick(ArrayList<BookViewModel> bookVMs, int position) {
        CommonFeatures.onBookItemClick(this, bookVMs, position);
    }

    private void initSearch() {
        searchEditTextLayout = findViewById(R.id.et_layout_search);
        filtersBtn = findViewById(R.id.settingsButton);
        searchEditText = findViewById(R.id.et_search);
        searchBtn = findViewById(R.id.searchButton);
        CommonFeatures.initSearch(this, searchIsEnabled, spinner, searchBtn, filtersBtn, searchEditTextLayout, searchEditText);
    }

    private void initSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner);
        CommonFeatures.initSpinner(this, spinner);
    }

    private void initMenu() {
        menu = findViewById(R.id.bottomNavigation);
        CommonFeatures.initMenu(this, menu, new boolean[]{false, false, true, false, false}, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        menu.getMenu().getItem(2).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
