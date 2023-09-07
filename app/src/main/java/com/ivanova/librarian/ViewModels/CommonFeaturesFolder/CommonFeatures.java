package com.ivanova.librarian.ViewModels.CommonFeaturesFolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ivanova.librarian.R;
import com.ivanova.librarian.ViewModels.BookViewModelFolder.BookViewModel;
import com.ivanova.librarian.Views.Book;
import com.ivanova.librarian.Views.FavouriteBooks;
import com.ivanova.librarian.Views.HomePage;
import com.ivanova.librarian.Views.Library;
import com.ivanova.librarian.Views.UserAccount;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class CommonFeatures {
    public static void onBookItemClick(Context context, ArrayList<BookViewModel> bookVMs, int position) {
        Intent intent = new Intent(context, Book.class);

        intent.putExtra("ID", String.valueOf(bookVMs.get(position).getBook().getId()));
        intent.putExtra("ISBN", bookVMs.get(position).getBook().getISBN());
        intent.putExtra("AUTHOR", bookVMs.get(position).getBook().getAuthor());
        intent.putExtra("BOOK_NAME", bookVMs.get(position).getBook().getBookName());
        intent.putExtra("YEAR", bookVMs.get(position).getBook().getYear());
        intent.putExtra("GENRE", bookVMs.get(position).getBook().getGenre());
        intent.putExtra("ANNOTATION", bookVMs.get(position).getBook().getAnnotation());
        intent.putExtra("RATING", String.valueOf(bookVMs.get(position).getBook().getRating()));

        try {
            Bitmap bitmap = bookVMs.get(position).getBook().getImage().copy(bookVMs.get(position).getBook().getImage().getConfig(), true);
            String filename = "bitmap.png";
            FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            stream.close();
            bitmap.recycle();

            intent.putExtra("IMAGE", filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        context.startActivity(intent);
    }

    public static void initSpinner(Context context, Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.spinner_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_drop_item);
        spinner.setAdapter(adapter);
    }

    public static void initMenu(Context context, BottomNavigationView menu, boolean[] pages, boolean isBookPage) {
        for (int i = 0; i < pages.length; i++) {
            if (pages[i]) {
                menu.getMenu().getItem(i).setChecked(true);
            }
        }
        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeItem: {
                        if (!pages[0]) {
                            Intent intent = new Intent(context, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            context.startActivity(intent);
                        }
                        return true;
                    }
                    case R.id.searchItem: {
                        return true;
                    }
                    case R.id.bookItem: {
                        if (!pages[2] || isBookPage) {
                            Intent intent = new Intent(context, Library.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            context.startActivity(intent);
                        }
                        return true;
                    }
                    case R.id.heartItem: {
                        if (!pages[3]) {
                            Intent intent = new Intent(context, FavouriteBooks.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            context.startActivity(intent);
                        }
                        return true;
                    }
                    case R.id.userItem: {
                        if (!pages[4]) {
                            Intent intent = new Intent(context, UserAccount.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            context.startActivity(intent);
                        }
                        return true;
                    }
                }
                return true;
            }
        });
    }

    public static void initSearch(Context context, boolean[] searchIsEnabled, Spinner spinner, ImageButton searchBtn, ImageButton filtersBtn, RelativeLayout searchEditTextLayout, EditText searchEditText) {
        searchEditTextLayout.setEnabled(false);
        searchIsEnabled[0] = false;

        initSearchEditText(searchIsEnabled, spinner, filtersBtn, searchEditTextLayout, searchEditText);
        initSearchButton(context, searchIsEnabled, spinner, searchBtn, filtersBtn, searchEditTextLayout, searchEditText);
    }

    private static void initSearchEditText(boolean[] searchIsEnabled, Spinner spinner, ImageButton filtersBtn, RelativeLayout searchEditTextLayout, EditText searchEditText) {
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchIsEnabled[0] = CommonFeatures.endSearching(spinner, filtersBtn, searchEditTextLayout, searchEditText);
                    return true;
                }
                return false;
            }
        });
    }

    private static void initSearchButton(Context context, boolean[] searchIsEnabled, Spinner spinner, ImageButton searchBtn, ImageButton filtersBtn, RelativeLayout searchEditTextLayout, EditText searchEditText) {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchIsEnabled[0]) {
                    searchIsEnabled[0] = CommonFeatures.startSearching(context, spinner, filtersBtn, searchEditTextLayout, searchEditText);

                } else {
                    searchIsEnabled[0] = CommonFeatures.endSearching(spinner, filtersBtn, searchEditTextLayout, searchEditText);
                }
            }
        });
    }

    private static boolean startSearching(Context context, Spinner spinner, ImageButton filtersBtn, RelativeLayout searchEditTextLayout, EditText searchEditText) {
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

    private static boolean endSearching(Spinner spinner, ImageButton filtersBtn, RelativeLayout searchEditTextLayout, EditText searchEditText) {
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
