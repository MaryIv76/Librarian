package com.ivanova.librarian.ViewModels.BookCatalogsFolder;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ivanova.librarian.ViewModels.BookViewModelFolder.BookViewModel;
import com.ivanova.librarian.ViewModels.FilterBooksFolder.FilterBooksCallback;
import com.ivanova.librarian.ViewModels.RecommendFolder.Recommendations;
import com.ivanova.librarian.ViewModels.RecommendFolder.RecommendationsCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class BookCatalogs {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void getBooks(ArrayList<Integer> bookIDs, ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
        recyclerView.setAdapter(recyclerViewAdapter);

        for (int id : bookIDs) {
            BookViewModel bookViewModel = new BookViewModel(context);
            books.add(bookViewModel);
            bookViewModel.readAllBookData(id, books.size() - 1, recyclerViewAdapter);
            recyclerViewAdapter.notifyItemInserted(books.size() - 1);
        }
    }

    public static void getRecommendedBooks(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();

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
                                    getFiveBestBooks(books, context, recyclerView, recyclerViewAdapter);  //  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
                                                        Recommendations recommendations = new Recommendations();
                                                        recommendations.getRecommendations(new RecommendationsCallback() {
                                                            @Override
                                                            public void onCallback(ArrayList<Integer> ids) {
                                                                getBooks(ids, books, context, recyclerView, recyclerViewAdapter);
                                                            }
                                                        }, booksIds, userID);
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

    public static void getFiveBestBooks(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
        db.collection("books").orderBy("rating", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Integer> ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ids.add(Integer.parseInt(document.getId()));
                            }
                            getBooks(ids, books, context, recyclerView, recyclerViewAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void getBestBooks(FilterBooksCallback filterCallback) {
        db.collection("books").orderBy("rating", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            filterCallback.onCallback(task);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void getFivePopularBooks(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
        db.collection("books").orderBy("readersCount", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Integer> ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ids.add(Integer.parseInt(document.getId()));
                            }
                            getBooks(ids, books, context, recyclerView, recyclerViewAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void getPopularBooks(FilterBooksCallback filterCallback) {
        db.collection("books").orderBy("readersCount", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            filterCallback.onCallback(task);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void getFiveNewBooks(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
        db.collection("books").orderBy("year", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Integer> ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ids.add(Integer.parseInt(document.getId()));
                            }
                            getBooks(ids, books, context, recyclerView, recyclerViewAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void getNewBooks(FilterBooksCallback filterCallback) {
        db.collection("books").orderBy("year", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            filterCallback.onCallback(task);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void getAllBooks(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
        db.collection("books").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Integer> ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ids.add(Integer.parseInt(document.getId()));
                            }
                            getBooks(ids, books, context, recyclerView, recyclerViewAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void getAllBooksForFilter(FilterBooksCallback filterCallback) {
        db.collection("books").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            filterCallback.onCallback(task);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void getFavouriteBooks(ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter, TextView tv_noFavouriteBooks) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        db.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("interactedBooks").whereEqualTo("isFavourite", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Integer> ids = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ids.add(Integer.parseInt(document.getId()));
                            }
                        }
                        if (ids.size() == 0) {
                            tv_noFavouriteBooks.setVisibility(View.VISIBLE);
                        } else {
                            tv_noFavouriteBooks.setVisibility(View.GONE);
                            getBooks(ids, books, context, recyclerView, recyclerViewAdapter);
                        }
                    }
                });
    }
}
