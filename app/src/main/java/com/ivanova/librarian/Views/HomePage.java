package com.ivanova.librarian.Views;

import static android.content.ContentValues.TAG;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import com.ivanova.librarian.Models.BookModel;
import com.ivanova.librarian.R;
import com.ivanova.librarian.ViewModels.Firestore.FirestoreCallback;
import com.ivanova.librarian.ViewModels.Firestore.ImageCallback;
import com.ivanova.librarian.ViewModels.Firestore.ReadBooksData;
import com.ivanova.librarian.ViewModels.RecyclerViews.HomePageRecycleViewAdapter;
import com.ivanova.librarian.ViewModels.RecyclerViews.RecyclerViewInterface;

import org.checkerframework.checker.units.qual.A;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class HomePage extends AppCompatActivity implements RecyclerViewInterface {

    private BottomNavigationView menu;
    private Spinner spinner;

    private ImageButton searchBtn;
    private boolean searchIsEnabled;
    private ImageButton filtersBtn;
    private RelativeLayout searchEditTextLayout;
    private EditText searchEditText;

    private Interpreter tflite;

    private FirebaseAuth fAuth;
    private FirebaseUser currentUser;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        db = FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();

        downloadModel();

        initSpinner();
        initMenu();
        initSearchButton();

        initBookLists();
    }

    private void initBookLists() {
        getRecommendedBooks(db, R.id.firstBooksRecyclerHorizontalView);
        getBestBooks(db, R.id.secondBooksRecyclerHorizontalView);
        getPopularBooks(db, R.id.thirdBooksRecyclerHorizontalView);
        getNewBooks(db, R.id.fourthBooksRecyclerHorizontalView);
    }

    private void getRecommendedBooks(FirebaseFirestore db, int horizontalViewID) {
        String currentUserUid = currentUser.getUid();
        db.collection("users").document(currentUserUid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                float userID = Float.parseFloat(document.get("id").toString());
                                if (userID == -1.0f) {
                                    getBestBooks(db, horizontalViewID);
                                } else {
                                    db.collection("books").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        ArrayList<Float> booksIds = new ArrayList<>();
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            booksIds.add(Float.parseFloat(document.getId()));
                                                        }
                                                        ArrayList<Integer> recommendedBooksIds = recommend(booksIds, userID);
                                                        getBooks(db, horizontalViewID, recommendedBooksIds);
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getBestBooks(FirebaseFirestore db, int horizontalViewID) {
        db.collection("books").orderBy("rating", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Integer> ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ids.add(Integer.parseInt(document.getId()));
                            }
                            getBooks(db, horizontalViewID, ids);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getPopularBooks(FirebaseFirestore db, int horizontalViewID) {
        db.collection("books").orderBy("readersCount", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Integer> ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ids.add(Integer.parseInt(document.getId()));
                            }
                            getBooks(db, horizontalViewID, ids);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getNewBooks(FirebaseFirestore db, int horizontalViewID) {
        db.collection("books").orderBy("year", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Integer> ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ids.add(Integer.parseInt(document.getId()));
                            }
                            getBooks(db, horizontalViewID, ids);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getBooks(FirebaseFirestore db, int horizontalViewID, ArrayList<Integer> bookIDs) {
        ArrayList<BookModel> books = new ArrayList<>();
        for (int id : bookIDs) {
            ReadBooksData.readData(new FirestoreCallback() {
                @Override
                public void onCallback(BookModel book) {
                    books.add(book);
                    createHorizontalRecyclerView(horizontalViewID, books);
                }
            }, db, id);
        }
    }

    private void createHorizontalRecyclerView(int idBooksRecyclerHorizontalView, ArrayList<BookModel> books) {
        RecyclerView recyclerView = findViewById(idBooksRecyclerHorizontalView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter recyclerViewAdapter = new HomePageRecycleViewAdapter(books, this, HomePage.this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onBookItemClick(ArrayList<BookModel> books, int position) {
        Intent intent = new Intent(HomePage.this, Book.class);

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

    private void downloadModel() {
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel("recommendations", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Model was loaded", Toast.LENGTH_SHORT);
                        toast.show();

                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            tflite = new Interpreter(modelFile);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Failed to download model", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    private ArrayList<Integer> recommend(ArrayList<Float> booksIds, float userId) {
        float[] userIdArray = new float[booksIds.size()];
        Arrays.fill(userIdArray, userId);

        float[] booksIdsFloat = new float[booksIds.size()];
        for (int i = 0; i < booksIds.size(); i++) {
            booksIdsFloat[i] = booksIds.get(i);
        }
        Arrays.sort(booksIdsFloat);

        Object[] inputs = {booksIdsFloat, userIdArray};
        Map<Integer, Object> outputs = new LinkedHashMap<>();
        outputs.put(0, new float[booksIds.size()][1]);
        tflite.runForMultipleInputsOutputs(inputs, outputs);

        Object[] outputsObjectsArray = (Object[]) outputs.get(0);
        ArrayList<Float> outputsArrayList = new ArrayList<>();
        for (int i = 0; i < booksIds.size(); i++) {
            float[] outputsFloatArray = (float[]) outputsObjectsArray[i];
            outputsArrayList.add(outputsFloatArray[0]);
        }

        ArrayList<Integer> ids = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int largest = getIndexOfLargest(outputsArrayList);
            ids.add(largest + 1);
            outputsArrayList.set(largest, -1.0f);
        }
        return ids;
    }

    private int getIndexOfLargest(ArrayList<Float> array) {
        int largest = 0;
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i) > array.get(largest)) largest = i;
        }
        return largest;
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_drop_item);
        spinner.setAdapter(adapter);
    }

    private void initMenu() {
        menu = findViewById(R.id.bottomNavigation);
        menu.getMenu().getItem(0).setChecked(true);
        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeItem: {
                        return true;
                    }
                    case R.id.searchItem: {
                        return true;
                    }
                    case R.id.bookItem: {
                        Intent intent = new Intent(HomePage.this, Library.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.heartItem: {
                        Intent intent = new Intent(HomePage.this, FavouriteBooks.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.userItem: {
                        Intent intent = new Intent(HomePage.this, UserAccount.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return true;
            }
        });
    }
}
