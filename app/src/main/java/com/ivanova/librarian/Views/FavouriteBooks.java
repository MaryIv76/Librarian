package com.ivanova.librarian.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Arrays;

public class FavouriteBooks extends AppCompatActivity implements RecyclerViewInterface {

    private BottomNavigationView menu;
    private TextView tv_noFavouriteBooks;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_books);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initNoFavBooksTextView();
        initMenu();
        initFavouriteBookList();
    }

    private void initFavouriteBookList() {
        recyclerView = findViewById(R.id.booksRecyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getFavouriteBooks(db);
    }

    private void getFavouriteBooks(FirebaseFirestore db) {
        db.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("interactedBooks").whereEqualTo("isFavourite", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Integer> booksIDs = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                booksIDs.add(Integer.parseInt(document.getId()));
                            }
                        }
                        if (booksIDs.size() == 0) {
                            tv_noFavouriteBooks.setVisibility(View.VISIBLE);
                        } else {
                            tv_noFavouriteBooks.setVisibility(View.GONE);
                            getBooks(db, booksIDs);
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
                    recyclerViewAdapter = new RecycleViewAdapter(books, FavouriteBooks.this, FavouriteBooks.this);
                    recyclerView.setAdapter(recyclerViewAdapter);
                }
            }, db, id);
        }
    }

    @Override
    public void onBookItemClick(ArrayList<BookModel> books, int position) {
        Intent intent = new Intent(FavouriteBooks.this, Book.class);

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

    private void initNoFavBooksTextView() {
        tv_noFavouriteBooks = findViewById(R.id.tv_noFavouriteBooks);
        tv_noFavouriteBooks.setVisibility(View.GONE);
    }

    private void initMenu() {
        menu = findViewById(R.id.bottomNavigation);
        menu.getMenu().getItem(3).setChecked(true);
        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeItem: {
                        Intent intent = new Intent(FavouriteBooks.this, HomePage.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.searchItem: {
                        return true;
                    }
                    case R.id.bookItem: {
                        Intent intent = new Intent(FavouriteBooks.this, Library.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.heartItem: {
                        return true;
                    }
                    case R.id.userItem: {
                        Intent intent = new Intent(FavouriteBooks.this, UserAccount.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return true;
            }
        });
    }
}
