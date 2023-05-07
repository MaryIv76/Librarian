package com.ivanova.librarian.Views;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ivanova.librarian.Models.BookModel;
import com.ivanova.librarian.R;
import com.ivanova.librarian.ViewModels.Firestore.FirestoreCallback;
import com.ivanova.librarian.ViewModels.Firestore.ReadBooksData;
import com.ivanova.librarian.ViewModels.RecyclerViews.RecycleViewAdapter;
import com.ivanova.librarian.ViewModels.RecyclerViews.RecyclerViewInterface;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class Library extends AppCompatActivity implements RecyclerViewInterface {

    private BottomNavigationView menu;
    private Spinner spinner;

    private ImageButton searchBtn;
    private boolean searchIsEnabled;
    private ImageButton filtersBtn;
    private RelativeLayout searchEditTextLayout;
    private EditText searchEditText;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        db = FirebaseFirestore.getInstance();

        initMenu();
        initSpinner();
        initSearchButton();

        initBookList();
    }

    private void initBookList() {
        recyclerView = findViewById(R.id.booksRecyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getAllBooks(db);
    }

    private void getAllBooks(FirebaseFirestore db) {
        db.collection("books").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Integer> ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ids.add(Integer.parseInt(document.getId()));
                            }
                            getBooks(db, ids);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getBooks(FirebaseFirestore db, ArrayList<Integer> bookIDs) {
        ArrayList<BookModel> books = new ArrayList<>();
        for (int id : bookIDs) {
            ReadBooksData.readData(new FirestoreCallback() {
                @Override
                public void onCallback(BookModel book) {
                    books.add(book);
                    recyclerViewAdapter = new RecycleViewAdapter(books, Library.this, Library.this);
                    recyclerView.setAdapter(recyclerViewAdapter);
                }
            }, db, id);
        }
    }

    @Override
    public void onBookItemClick(ArrayList<BookModel> books, int position) {
        Intent intent = new Intent(Library.this, Book.class);

        intent.putExtra("ID", String.valueOf(books.get(position).getId()));
        intent.putExtra("ISBN", books.get(position).getISBN());
        intent.putExtra("AUTHOR", books.get(position).getAuthor());
        intent.putExtra("BOOK_NAME", books.get(position).getBookName());
        intent.putExtra("YEAR", books.get(position).getYear());
        intent.putExtra("GENRE", books.get(position).getGenre());
        intent.putExtra("ANNOTATION", books.get(position).getAnnotation());
        intent.putExtra("RATING", String.valueOf(books.get(position).getRating()));

        try {
            Bitmap bitmap = books.get(position).getImage().copy(books.get(position).getImage().getConfig(), true);
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            stream.close();
            bitmap.recycle();

            intent.putExtra("IMAGE", filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startActivity(intent);
    }

    private void startSearching() {
        searchIsEnabled = true;
        filtersBtn.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        filtersBtn.setEnabled(false);
        spinner.setEnabled(false);
        searchEditTextLayout.setVisibility(View.VISIBLE);
        searchEditTextLayout.setEnabled(true);

        searchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
    }

    private void endSearching() {
        searchIsEnabled = false;
        filtersBtn.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        filtersBtn.setEnabled(true);
        spinner.setEnabled(true);
        searchEditTextLayout.setVisibility(View.INVISIBLE);
        searchEditTextLayout.setEnabled(false);
        searchEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    private void initSearchButton() {
        searchEditTextLayout = findViewById(R.id.et_layout_search);
        searchEditTextLayout.setEnabled(false);
        filtersBtn = findViewById(R.id.settingsButton);
        searchIsEnabled = false;

        searchBtn = findViewById(R.id.searchButton);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchIsEnabled) {
                    startSearching();

                } else {
                    endSearching();
                }
            }
        });

        searchEditText = findViewById(R.id.et_search);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    endSearching();
                    return true;
                }
                return false;
            }
        });
    }

    private void initSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_drop_item);
        spinner.setAdapter(spinnerAdapter);
    }

    private void initMenu() {
        menu = findViewById(R.id.bottomNavigation);
        menu.getMenu().getItem(2).setChecked(true);
        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeItem: {
                        Intent intent = new Intent(Library.this, HomePage.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.searchItem: {
                        return true;
                    }
                    case R.id.bookItem: {
                        return true;
                    }
                    case R.id.heartItem: {
                        Intent intent = new Intent(Library.this, FavouriteBooks.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.userItem: {
                        Intent intent = new Intent(Library.this, UserAccount.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return true;
            }
        });
    }
}
