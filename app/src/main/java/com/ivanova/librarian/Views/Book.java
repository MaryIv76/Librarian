package com.ivanova.librarian.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.ivanova.librarian.Models.BookModel;
import com.ivanova.librarian.R;
import com.ivanova.librarian.ViewModels.CommentViewModelFolder.CommentViewModel;
import com.ivanova.librarian.ViewModels.CommentViewModelFolder.CommentsList;
import com.ivanova.librarian.ViewModels.CommonFeaturesFolder.CommonFeatures;
import com.ivanova.librarian.ViewModels.RecyclerViewsFolder.CommentsRecyclerViewAdapter;
import com.ivanova.librarian.ViewModels.RecyclerViewsFolder.RecyclerViewAdapter;
import com.ivanova.librarian.ViewModels.SendCommentFolder.SendComment;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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

    private ArrayList<CommentViewModel> comments;
    private HashSet<String> commentsIDs;

    private TextView tv_commentsNumber;
    private TextView tv_commentsLabel;
    private EditText et_comment;
    private FrameLayout sendCommentBtn;

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        book = getBookFromPrevActivity();

        isFavourite = false;
        isRead = false;
        getFavouriteBookStatus();
        getReadBookStatus();

        initAllViewItems();
        setAllViewItems();

        initFavouriteButton();
        initReadButton();

        initRatingBar();

        initMenu();

        initCommentsSection();
    }

    private void initCommentsSection() {
        tv_commentsNumber = findViewById(R.id.tv_commentsNumber);
        tv_commentsLabel = findViewById(R.id.tv_commentsLabel);

        et_comment = findViewById(R.id.et_leaveComment);
        initCommentsList();
    }

    private void initCommentsList() {
        comments = new ArrayList<>();
        commentsIDs = new HashSet<>();

        RecyclerView recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter recyclerViewAdapter = new CommentsRecyclerViewAdapter(comments, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        CommentsList.listenToCommentsUpdates(book.getId(), comments, commentsIDs, recyclerView, recyclerViewAdapter, tv_commentsNumber, tv_commentsLabel);

        initSendCommentBtn(recyclerViewAdapter);
    }

    private void initSendCommentBtn(RecyclerView.Adapter recyclerViewAdapter) {
        sendCommentBtn = findViewById(R.id.btn_sendCommentLayout);
        sendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_comment.getText().toString().trim().equals("")) {
                    SendComment.sendComment(book.getId(), et_comment, comments, recyclerViewAdapter);
                    refreshCommentEditText();
                }
            }
        });
    }

    private void refreshCommentEditText() {
        et_comment.setText("");
        et_comment.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (et_comment.isFocused()) {
                Rect outRect = new Rect();
                et_comment.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    et_comment.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private BookModel getBookFromPrevActivity() {
        Bitmap bitmap = null;
        String filename = getIntent().getStringExtra("IMAGE");
        try {
            FileInputStream is = this.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File dir = getFilesDir();
        File file = new File(dir, filename);
        file.delete();

        BookModel book = new BookModel(
                Integer.parseInt(getIntent().getStringExtra("ID")),
                getIntent().getStringExtra("ISBN"),
                getIntent().getStringExtra("AUTHOR"),
                getIntent().getStringExtra("BOOK_NAME"),
                getIntent().getStringExtra("YEAR"),
                getIntent().getStringExtra("GENRE"),
                getIntent().getStringExtra("ANNOTATION"),
                bitmap,
                Double.parseDouble(getIntent().getStringExtra("RATING")));

        return book;
    }

    private void getFavouriteBookStatus() {
        db.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("interactedBooks").document(String.valueOf(book.getId()))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Object isFavouriteObj = document.get("isFavourite");
                                isFavourite = (isFavouriteObj != null) ? Boolean.parseBoolean(String.valueOf(isFavouriteObj)) : false;
                            } else {
                                isFavourite = false;
                            }
                            changeFavouriteBookIcon();
                        }
                    }
                });
    }

    private void changeFavouriteBookIcon() {
        ImageView iv_favButton = findViewById(R.id.imageFavouriteButton);
        if (isFavourite) {
            iv_favButton.setImageResource(R.drawable.heart_red);
        } else {
            iv_favButton.setImageResource(R.drawable.heart_shape);
        }
    }

    private void changeFavouriteBookStatus() {
        Map<String, Object> data = new HashMap<>();
        data.put("isFavourite", isFavourite);

        db.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("interactedBooks").document(String.valueOf(book.getId()))
                .set(data, SetOptions.merge());
    }

    private void initFavouriteButton() {
        favouriteButton = findViewById(R.id.favourite_button);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv_favButton = findViewById(R.id.imageFavouriteButton);
                if (!isFavourite) {
                    iv_favButton.setImageResource(R.drawable.heart_red);
                    isFavourite = true;

                    Bundle params = new Bundle();
                    params.putString("book_id", String.valueOf(book.getId()));
                    params.putString("book_isbn", book.getISBN());
                    mFirebaseAnalytics.logEvent("liked_book", params);
                } else {
                    iv_favButton.setImageResource(R.drawable.heart_shape);
                    isFavourite = false;
                }
                changeFavouriteBookStatus();
            }
        });
    }

    private void getReadBookStatus() {
        db.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("interactedBooks").document(String.valueOf(book.getId()))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Object haveReadObj = document.get("haveRead");
                                isRead = (haveReadObj != null) ? Boolean.parseBoolean(String.valueOf(haveReadObj)) : false;
                            } else {
                                isRead = false;
                            }
                            changeReadBookIcon();
                        }
                    }
                });
    }

    private void changeReadBookIcon() {
        ImageView iv_readButton = findViewById(R.id.imageReadButton);
        if (isRead) {
            iv_readButton.setImageResource(R.drawable.check_mark);
        } else {
            iv_readButton.setImageResource(R.drawable.check_mark_shape);
        }
    }

    private void changeReadBookStatus() {
        Map<String, Object> data = new HashMap<>();
        data.put("haveRead", isRead);

        db.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("interactedBooks").document(String.valueOf(book.getId()))
                .set(data, SetOptions.merge());

        changeBookReadersCount(isRead);
    }

    private void changeBookReadersCount(boolean readersIncreased) {
        db.collection("books").document(String.valueOf(book.getId())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Integer readersCount = Integer.parseInt(document.get("readersCount").toString());
                                if (readersIncreased) {
                                    readersCount++;
                                } else {
                                    readersCount--;
                                }

                                Map<String, Object> data = new HashMap<>();
                                data.put("readersCount", readersCount);

                                db.collection("books").document(String.valueOf(book.getId()))
                                        .set(data, SetOptions.merge());
                            }
                        }
                    }
                });
    }

    private void initReadButton() {
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
                changeReadBookStatus();
            }
        });
    }

    private void initAllViewItems() {
        iv_book = findViewById(R.id.bookImage);
        tv_bookName = findViewById(R.id.tv_bookName);
        tv_author = findViewById(R.id.tv_author);
        ratingBar = findViewById(R.id.ratingBar);
        tv_year = findViewById(R.id.tv_yearInfo);
        tv_genre = findViewById(R.id.tv_genreInfo);
        tv_annotation = findViewById(R.id.tv_annotationInfo);
    }

    private void setAllViewItems() {
        iv_book.setImageBitmap(book.getImage());
        tv_bookName.setText(book.getBookName());
        tv_author.setText(book.getAuthor());
        ratingBar.setRating((float) book.getRating());
        tv_year.setText(book.getYear());
        tv_genre.setText(book.getGenre());
        tv_annotation.setText(book.getAnnotation());
    }

    private void initMenu() {
        menu = findViewById(R.id.bottomNavigation);
        CommonFeatures.initMenu(this, menu, new boolean[]{false, false, true, false, false}, true);
    }

    private void initRatingBar() {
        db.collection("books").document(String.valueOf(book.getId())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                float rating = Float.parseFloat(String.valueOf(document.get("rating")));
                                ratingBar.setRating(rating);
                            }
                        }
                    }
                });

        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    showRatingAlertDialog();
                }
                return true;
            }
        });
    }

    private void prepareAlertData(View customLayout) {
        TextView tv_bigRating = customLayout.findViewById(R.id.tv_bigRating);
        tv_bigRating.setText(String.format("%.1f", book.getRating()));

        RatingBar smallRatingBar = customLayout.findViewById(R.id.smallRatingBar);
        smallRatingBar.setRating((float) book.getRating());

        getBookRating(tv_bigRating, smallRatingBar);

        RatingBar userRatingBar = customLayout.findViewById(R.id.userRatingBar);
        userRatingBar.setRating(0);
        getUserRatingToRatingBar(userRatingBar);

        TextView tv_numRatings = customLayout.findViewById(R.id.tv_numRatings);
        tv_numRatings.setText("");

        ProgressBar progressBar5 = customLayout.findViewById(R.id.progressBar5);
        progressBar5.setMax(0);
        progressBar5.setProgress(0);

        ProgressBar progressBar4 = customLayout.findViewById(R.id.progressBar4);
        progressBar4.setMax(0);
        progressBar4.setProgress(0);

        ProgressBar progressBar3 = customLayout.findViewById(R.id.progressBar3);
        progressBar3.setMax(0);
        progressBar3.setProgress(0);

        ProgressBar progressBar2 = customLayout.findViewById(R.id.progressBar2);
        progressBar2.setMax(0);
        progressBar2.setProgress(0);

        ProgressBar progressBar1 = customLayout.findViewById(R.id.progressBar1);
        progressBar1.setMax(0);
        progressBar1.setProgress(0);

        getRatings(tv_numRatings, progressBar1, progressBar2, progressBar3, progressBar4, progressBar5);

        Button btn_saveRating = customLayout.findViewById(R.id.btn_saveRating);
        btn_saveRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float userRating = userRatingBar.getRating();
                if (userRating == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.rating_bigger_zero, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    saveUserRating(userRating);
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.rating_was_saved, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void saveUserRating(float rating) {
        deletePrevUserRating(rating);
    }

    private void deletePrevUserRating(float rating) {
        db.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("interactedBooks").document(String.valueOf(book.getId())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Object ratingObj = document.get("rating");
                                if (ratingObj != null) {
                                    Integer userRating = (int) Float.parseFloat(String.valueOf(ratingObj));
                                    if (userRating > 0) {
                                        db.collection("books").document(String.valueOf(book.getId())).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot docBook = task.getResult();
                                                            if (docBook.exists()) {
                                                                String numStarsToDelete = getNumStarsStr((int) userRating);
                                                                String numStarsToAdd = getNumStarsStr((int) rating);
                                                                Integer starsCountToDelete = Integer.parseInt(docBook.get(numStarsToDelete).toString());
                                                                Integer starsCountToAdd = Integer.parseInt(docBook.get(numStarsToAdd).toString());
                                                                Integer ratingsCount = Integer.parseInt(docBook.get("ratingsCount").toString());
                                                                Float bookRating = Float.parseFloat(docBook.get("rating").toString());

                                                                float newRating = ((float) bookRating * ratingsCount - userRating) / ((float) ratingsCount - 1);
                                                                newRating = ((float) newRating * (ratingsCount - 1) + rating) / ((float) ratingsCount);

                                                                Map<String, Object> data = new HashMap<>();
                                                                data.put(numStarsToDelete, starsCountToDelete - 1);
                                                                data.put(numStarsToAdd, starsCountToAdd + 1);
                                                                data.put("rating", newRating);
                                                                db.collection("books").document(String.valueOf(book.getId()))
                                                                        .set(data, SetOptions.merge());

                                                                Map<String, Object> dataUser = new HashMap<>();
                                                                dataUser.put("rating", rating);
                                                                db.collection("users").document(fAuth.getCurrentUser().getUid())
                                                                        .collection("interactedBooks").document(String.valueOf(book.getId()))
                                                                        .set(dataUser, SetOptions.merge());
                                                            }
                                                        }
                                                    }
                                                });
                                    } else {
                                        changeAllRatingFirstTime(rating);
                                    }
                                } else {
                                    changeAllRatingFirstTime(rating);
                                }
                            } else {
                                changeAllRatingFirstTime(rating);
                            }
                        }
                    }
                });

        db.collection("users").document(fAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Integer userID = Integer.parseInt(document.get("id").toString());

                                Bundle params = new Bundle();
                                params.putInt("user_id_rating", userID);
                                params.putInt("rated_book_id", book.getId());
                                params.putDouble("rating", rating);
                                params.putString("user_book_rating", userID + "|" + book.getId() + "|" + rating);
                                mFirebaseAnalytics.logEvent("rate_book", params);
                            }
                        }
                    }
                });
    }

    private void changeAllRatingFirstTime(float rating) {
        db.collection("books").document(String.valueOf(book.getId())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot docBook = task.getResult();
                            if (docBook.exists()) {
                                String numStarsToAdd = getNumStarsStr((int) rating);
                                Integer starsCountToAdd = Integer.parseInt(docBook.get(numStarsToAdd).toString());
                                Integer ratingsCount = Integer.parseInt(docBook.get("ratingsCount").toString());
                                Float bookRating = Float.parseFloat(docBook.get("rating").toString());

                                float newRating = ((float) bookRating * ratingsCount + rating) / ((float) ratingsCount + 1);

                                Map<String, Object> data = new HashMap<>();
                                data.put(numStarsToAdd, starsCountToAdd + 1);
                                data.put("rating", newRating);
                                data.put("ratingsCount", ratingsCount + 1);
                                db.collection("books").document(String.valueOf(book.getId()))
                                        .set(data, SetOptions.merge());

                                Map<String, Object> dataUser = new HashMap<>();
                                dataUser.put("rating", rating);
                                db.collection("users").document(fAuth.getCurrentUser().getUid())
                                        .collection("interactedBooks").document(String.valueOf(book.getId()))
                                        .set(dataUser, SetOptions.merge());
                            }
                        }
                    }
                });
    }

    private void showRatingAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Book.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.rating_alert, null);
        alertDialog.setView(customLayout);
        AlertDialog alert = alertDialog.create();

        prepareAlertData(customLayout);
        alert.show();
    }

    private void getBookRating(TextView textView, RatingBar ratingBar) {
        db.collection("books").document(String.valueOf(book.getId())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                float rating = Float.parseFloat(String.valueOf(document.get("rating")));
                                textView.setText(String.format("%.1f", rating));
                                ratingBar.setRating(rating);
                            }
                        }
                    }
                });
    }

    private void getUserRatingToRatingBar(RatingBar ratingBar) {
        db.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("interactedBooks").document(String.valueOf(book.getId())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Object ratingObj = document.get("rating");
                                if (ratingObj != null) {
                                    ratingBar.setRating(Float.parseFloat(String.valueOf(ratingObj)));
                                }
                            }
                        }
                    }
                });
    }

    private void getRatings(TextView textView, ProgressBar prBar1, ProgressBar prBar2, ProgressBar prBar3, ProgressBar prBar4, ProgressBar prBar5) {
        db.collection("books").document(String.valueOf(book.getId())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Integer ratingsCount = Integer.parseInt(document.get("ratingsCount").toString());
                                textView.setText(String.valueOf(ratingsCount));

                                getStarsCount(1, prBar1, ratingsCount);
                                getStarsCount(2, prBar2, ratingsCount);
                                getStarsCount(3, prBar3, ratingsCount);
                                getStarsCount(4, prBar4, ratingsCount);
                                getStarsCount(5, prBar5, ratingsCount);
                            }
                        }
                    }
                });
    }

    private String getNumStarsStr(int numStars) {
        switch (numStars) {
            case 1:
                return "1starCount";
            case 2:
                return "2starsCount";
            case 3:
                return "3starsCount";
            case 4:
                return "4starsCount";
            case 5:
                return "5starsCount";
            default:
                return "";
        }
    }

    private void getStarsCount(int numStars, ProgressBar progressBar, int numRatings) {
        db.collection("books").document(String.valueOf(book.getId())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String numStarsStr = getNumStarsStr(numStars);
                                Integer starsCount = Integer.parseInt(document.get(numStarsStr).toString());
                                progressBar.setMax(numRatings);
                                progressBar.setProgress(starsCount);
                            }
                        }
                    }
                });
    }
}
