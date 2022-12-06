package com.ivanova.librarian.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ivanova.librarian.Models.BookModel;
import com.ivanova.librarian.R;

public class Book extends AppCompatActivity {

    private BottomNavigationView menu;
    private RatingBar ratingBar;

    private BookModel book;

    private ImageView iv_book;
    private TextView tv_bookName;
    private TextView tv_author;
    private TextView tv_year;
    private TextView tv_genre;
    private TextView tv_annotation;

    private boolean isFavourite;
    private RelativeLayout favouriteButton;
    private boolean isRead;
    private RelativeLayout readButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book);

        isFavourite = false;
        isRead = false;

        book = getBookFromPrevActivity();

        iv_book = findViewById(R.id.bookImage);
        tv_bookName = findViewById(R.id.tv_bookName);
        tv_author = findViewById(R.id.tv_author);
        tv_year = findViewById(R.id.tv_yearInfo);
        tv_genre = findViewById(R.id.tv_genreInfo);
        tv_annotation = findViewById(R.id.tv_annotationInfo);

        // ---------------------- Favourite Button -----------------------------
        favouriteButton = findViewById(R.id.favourite_button);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv_favButton = findViewById(R.id.imageFavouriteButton);
                if (!isFavourite) {
                    iv_favButton.setImageResource(R.drawable.heart_red);
                    isFavourite = true;
                } else {
                    iv_favButton.setImageResource(R.drawable.heart_shape);
                    isFavourite = false;
                }
            }
        });

        // ---------------------- Read Button -----------------------------
        readButton = findViewById(R.id.read_button);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv_readButton = findViewById(R.id.imageReadButton);
                if (!isRead) {
                    iv_readButton.setImageResource(R.drawable.check_mark);
                    isRead = true;
                } else {
                    iv_readButton.setImageResource(R.drawable.check_mark_shape);
                    isRead = false;
                }
            }
        });

        // ---------------------- Rating bar -----------------------------
        ratingBar = findViewById(R.id.ratingBar);

        setAllViewItems();

        // ---------------------- Menu -----------------------------
        menu = findViewById(R.id.bottomNavigation);
        menu.getMenu().getItem(2).setChecked(true);
        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeItem: {
                        Intent intent = new Intent(Book.this, HomePage.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.searchItem: {
                        return true;
                    }
                    case R.id.bookItem: {
                        Intent intent = new Intent(Book.this, Library.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.heartItem: {
                        Intent intent = new Intent(Book.this, FavouriteBooks.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.userItem: {
                        Intent intent = new Intent(Book.this, UserAccount.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return true;
            }
        });
    }

    private BookModel getBookFromPrevActivity() {
        BookModel book = new BookModel(getIntent().getStringExtra("AUTHOR"),
                getIntent().getStringExtra("BOOK_NAME"),
                getIntent().getStringExtra("YEAR"),
                getIntent().getStringExtra("GENRE"),
                getIntent().getStringExtra("ANNOTATION"),
                Integer.parseInt(getIntent().getStringExtra("IMAGE")),
                Double.parseDouble(getIntent().getStringExtra("RATING")));

        return book;
    }

    private void setAllViewItems() {
        iv_book.setImageResource(book.getImage());
        tv_bookName.setText(book.getBookName());
        tv_author.setText(book.getAuthor());
        ratingBar.setRating((float) book.getRating());
        tv_year.setText(book.getYear());
        tv_genre.setText(book.getGenre());
        tv_annotation.setText(book.getAnnotation());
    }
}
