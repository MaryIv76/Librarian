package com.ivanova.librarian.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ivanova.librarian.R;
import com.ivanova.librarian.ViewModels.BookCatalogsFolder.BookCatalogs;
import com.ivanova.librarian.ViewModels.BookViewModelFolder.BookViewModel;
import com.ivanova.librarian.ViewModels.CommonFeaturesFolder.CommonFeatures;
import com.ivanova.librarian.ViewModels.FilterBooksFolder.FilterBooks;
import com.ivanova.librarian.ViewModels.RecyclerViewsFolder.RecyclerViewAdapter;
import com.ivanova.librarian.ViewModels.RecyclerViewsFolder.RecyclerViewInterface;

import java.util.ArrayList;
import java.util.HashSet;

public class Library extends AppCompatActivity implements RecyclerViewInterface {

    private BottomNavigationView menu;
    private Spinner spinner;

    private ImageButton searchBtn;
    private boolean[] searchIsEnabled = new boolean[1];
    private ImageButton filtersBtn;
    private RelativeLayout searchEditTextLayout;
    private EditText searchEditText;

    private HashSet<String> desiredGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        desiredGenres = new HashSet<>();

        initSpinner();
        initMenu();
        initSearch();
        initFiltersBtn();

        boolean needsFiltering = getIntent().getBooleanExtra("NEEDS_FILTERING", false);
        initBookList(needsFiltering);
    }

    private void initBookList(boolean needsFiltering) {
        ArrayList<BookViewModel> books = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.booksRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter recyclerViewAdapter = new RecyclerViewAdapter(books, Library.this, Library.this);
        recyclerView.setAdapter(recyclerViewAdapter);

        if (!needsFiltering) {
            BookCatalogs.getAllBooks(books, this, recyclerView, recyclerViewAdapter);
        } else {
            prepareForFiltering(books, this, recyclerView, recyclerViewAdapter);
        }
    }

    private void prepareForFiltering(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
        int bookOrder = spinner.getSelectedItemPosition();
        String keyWord = searchEditText.getText().toString().trim();

        FilterBooks.getFilteredBooks(bookOrder, keyWord, desiredGenres, books, context, recyclerView, recyclerViewAdapter);
    }

    @Override
    public void onBookItemClick(ArrayList<BookViewModel> bookVMs, int position) {
        CommonFeatures.onBookItemClick(this, bookVMs, position);
    }

    private void initFiltersBtn() {
        filtersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGenresFilterAlertDialog();
            }
        });
    }

    private void showGenresFilterAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Library.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.genres_filter_alert, null);
        alertDialog.setView(customLayout);
        AlertDialog alert = alertDialog.create();

        prepareAlertData(customLayout, alert);
        alert.show();
    }

    private void prepareAlertData(View customLayout, AlertDialog alert) {
        CheckBox chBox_fantasy = customLayout.findViewById(R.id.chBox_fantasy);
        CheckBox chBox_detectives = customLayout.findViewById(R.id.chBox_detectives);
        CheckBox chBox_scienceFiction = customLayout.findViewById(R.id.chBox_scienceFiction);
        CheckBox chBox_loveRomances = customLayout.findViewById(R.id.chBox_loveRomances);
        CheckBox chBox_fairyTales = customLayout.findViewById(R.id.chBox_fairyTales);
        CheckBox chBox_horrors = customLayout.findViewById(R.id.chBox_horrors);

        if (desiredGenres.contains("фэнтези")) {
            chBox_fantasy.setChecked(true);
        }
        if (desiredGenres.contains("детектив")) {
            chBox_detectives.setChecked(true);
        }
        if (desiredGenres.contains("научная фантастика")) {
            chBox_scienceFiction.setChecked(true);
        }
        if (desiredGenres.contains("любовный роман")) {
            chBox_loveRomances.setChecked(true);
        }
        if (desiredGenres.contains("сказка")) {
            chBox_fairyTales.setChecked(true);
        }
        if (desiredGenres.contains("ужасы")) {
            chBox_horrors.setChecked(true);
        }

        Button btn_applyGenresFilter = customLayout.findViewById(R.id.btn_applyGenresFilter);
        btn_applyGenresFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desiredGenres = new HashSet<>();
                if (chBox_fantasy.isChecked()) {
                    desiredGenres.add("фэнтези");
                }
                if (chBox_detectives.isChecked()) {
                    desiredGenres.add("детектив");
                }
                if (chBox_scienceFiction.isChecked()) {
                    desiredGenres.add("научная фантастика");
                }
                if (chBox_loveRomances.isChecked()) {
                    desiredGenres.add("любовный роман");
                }
                if (chBox_fairyTales.isChecked()) {
                    desiredGenres.add("сказка");
                }
                if (chBox_horrors.isChecked()) {
                    desiredGenres.add("ужасы");
                }

                initBookList(true);
                alert.cancel();
            }
        });
    }

    private void initSearch() {
        searchEditTextLayout = findViewById(R.id.et_layout_search);
        filtersBtn = findViewById(R.id.settingsButton);
        searchEditText = findViewById(R.id.et_search);
        searchBtn = findViewById(R.id.searchButton);
        initSearch(this, searchIsEnabled, spinner, searchBtn, filtersBtn, searchEditTextLayout, searchEditText);
    }

    private void initSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner);
        initSpinner(this, spinner);
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

    private void initSpinner(Context context, Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.spinner_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_drop_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initBookList(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initSearch(Context context, boolean[] searchIsEnabled, Spinner spinner, ImageButton searchBtn, ImageButton filtersBtn, RelativeLayout searchEditTextLayout, EditText searchEditText) {
        searchEditTextLayout.setEnabled(false);
        searchIsEnabled[0] = false;

        initSearchEditText(searchIsEnabled, spinner, filtersBtn, searchEditTextLayout, searchEditText);
        initSearchButton(context, searchIsEnabled, spinner, searchBtn, filtersBtn, searchEditTextLayout, searchEditText);
    }

    private void initSearchEditText(boolean[] searchIsEnabled, Spinner spinner, ImageButton filtersBtn, RelativeLayout searchEditTextLayout, EditText searchEditText) {
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchIsEnabled[0] = endSearching(spinner, filtersBtn, searchEditTextLayout, searchEditText);
                    return true;
                }
                return false;
            }
        });
    }

    private void initSearchButton(Context context, boolean[] searchIsEnabled, Spinner spinner, ImageButton searchBtn, ImageButton filtersBtn, RelativeLayout searchEditTextLayout, EditText searchEditText) {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchIsEnabled[0]) {
                    searchIsEnabled[0] = startSearching(context, spinner, filtersBtn, searchEditTextLayout, searchEditText);

                } else {
                    searchIsEnabled[0] = endSearching(spinner, filtersBtn, searchEditTextLayout, searchEditText);
                    initBookList(true);
                }
            }
        });
    }

    private boolean startSearching(Context context, Spinner spinner, ImageButton filtersBtn, RelativeLayout searchEditTextLayout, EditText searchEditText) {
        filtersBtn.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        filtersBtn.setEnabled(false);
        spinner.setEnabled(false);
        searchEditTextLayout.setVisibility(View.VISIBLE);
        searchEditTextLayout.setEnabled(true);

        searchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
        return true;
    }

    private boolean endSearching(Spinner spinner, ImageButton filtersBtn, RelativeLayout searchEditTextLayout, EditText searchEditText) {
        filtersBtn.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        filtersBtn.setEnabled(true);
        spinner.setEnabled(true);
        searchEditTextLayout.setVisibility(View.INVISIBLE);
        searchEditTextLayout.setEnabled(false);
        searchEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
        return false;
    }
}
