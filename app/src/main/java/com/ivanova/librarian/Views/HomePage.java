package com.ivanova.librarian.Views;

import android.content.Context;
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
import com.ivanova.librarian.ViewModels.BookCatalogsFolder.BookCatalogsInterface;
import com.ivanova.librarian.ViewModels.BookViewModelFolder.BookViewModel;
import com.ivanova.librarian.ViewModels.CommonFeaturesFolder.CommonFeatures;
import com.ivanova.librarian.ViewModels.RecyclerViewsFolder.HomePageRecyclerViewAdapter;
import com.ivanova.librarian.ViewModels.RecyclerViewsFolder.RecyclerViewInterface;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements RecyclerViewInterface {

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
        setContentView(R.layout.home_page);

        initSpinner();
        initMenu();
        initSearch();

        initBookLists();
    }

    private RecyclerView.Adapter initRecyclerView(RecyclerView recyclerView, ArrayList<BookViewModel> books) {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter recyclerViewAdapter = new HomePageRecyclerViewAdapter(books, this, HomePage.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        return recyclerViewAdapter;
    }

    private void initBookLists() {
        initBookList(R.id.firstBooksRecyclerHorizontalView, new BookCatalogsInterface() {
            @Override
            public void getCatalog(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
                BookCatalogs.getRecommendedBooks(books, context, recyclerView, recyclerViewAdapter);
            }
        });
        initBookList(R.id.secondBooksRecyclerHorizontalView, new BookCatalogsInterface() {
            @Override
            public void getCatalog(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
                BookCatalogs.getBestBooks(books, context, recyclerView, recyclerViewAdapter);
            }
        });
        initBookList(R.id.thirdBooksRecyclerHorizontalView, new BookCatalogsInterface() {
            @Override
            public void getCatalog(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
                BookCatalogs.getPopularBooks(books, context, recyclerView, recyclerViewAdapter);
            }
        });
        initBookList(R.id.fourthBooksRecyclerHorizontalView, new BookCatalogsInterface() {
            @Override
            public void getCatalog(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
                BookCatalogs.getNewBooks(books, context, recyclerView, recyclerViewAdapter);
            }
        });
    }

    private void initBookList(int horizontalViewID, BookCatalogsInterface bookCatalogsInterface) {
        ArrayList<BookViewModel> books = new ArrayList<>();
        RecyclerView recyclerView = findViewById(horizontalViewID);
        RecyclerView.Adapter recyclerViewAdapter = initRecyclerView(recyclerView, books);
        bookCatalogsInterface.getCatalog(books, this, recyclerView, recyclerViewAdapter);
    }

    @Override
    public void onBookItemClick(ArrayList<BookViewModel> bookVMs, int position) {
        CommonFeatures.onBookItemClick(this, bookVMs, position);
    }

    private void initSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner);
        CommonFeatures.initSpinner(this, spinner);
    }

    private void initSearch() {
        searchEditTextLayout = findViewById(R.id.et_layout_search);
        filtersBtn = findViewById(R.id.settingsButton);
        searchEditText = findViewById(R.id.et_search);
        searchBtn = findViewById(R.id.searchButton);
        CommonFeatures.initSearch(this, searchIsEnabled, spinner, searchBtn, filtersBtn, searchEditTextLayout, searchEditText);
    }

    private void initMenu() {
        menu = findViewById(R.id.bottomNavigation);
        CommonFeatures.initMenu(this, menu, new boolean[]{true, false, false, false, false}, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        menu.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
