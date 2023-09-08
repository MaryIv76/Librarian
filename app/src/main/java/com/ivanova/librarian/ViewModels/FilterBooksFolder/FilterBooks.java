package com.ivanova.librarian.ViewModels.FilterBooksFolder;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ivanova.librarian.ViewModels.BookCatalogsFolder.BookCatalogs;
import com.ivanova.librarian.ViewModels.BookViewModelFolder.BookViewModel;

import java.util.ArrayList;
import java.util.HashSet;

public class FilterBooks {

    // int bookOrder
    // 1 - popular books
    // 2 - best books
    // 3 - new books
    // 0 - no order

    public static void getFilteredBooks(int bookOrder, String keyWord, HashSet<String> desiredGenres, ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
        switch (bookOrder) {
            case 1: {
                BookCatalogs.getPopularBooks(new FilterBooksCallback() {
                    @Override
                    public void onCallback(Task<QuerySnapshot> task) {
                        checkNameAuthorGenres(task, keyWord, desiredGenres, books, context, recyclerView, recyclerViewAdapter);
                    }
                });
                break;
            }
            case 2: {
                BookCatalogs.getBestBooks(new FilterBooksCallback() {
                    @Override
                    public void onCallback(Task<QuerySnapshot> task) {
                        checkNameAuthorGenres(task, keyWord, desiredGenres, books, context, recyclerView, recyclerViewAdapter);
                    }
                });
                break;
            }
            case 3: {
                BookCatalogs.getNewBooks(new FilterBooksCallback() {
                    @Override
                    public void onCallback(Task<QuerySnapshot> task) {
                        checkNameAuthorGenres(task, keyWord, desiredGenres, books, context, recyclerView, recyclerViewAdapter);
                    }
                });
                break;
            }
            default: {
                BookCatalogs.getAllBooksForFilter(new FilterBooksCallback() {
                    @Override
                    public void onCallback(Task<QuerySnapshot> task) {
                        checkNameAuthorGenres(task, keyWord, desiredGenres, books, context, recyclerView, recyclerViewAdapter);
                    }
                });
                break;
            }
        }
    }

    private static void checkNameAuthorGenres(Task<QuerySnapshot> task, String keyWord, HashSet<String> desiredGenres, ArrayList<BookViewModel> books, Context context, RecyclerView recyclerView, RecyclerView.Adapter recyclerViewAdapter) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (QueryDocumentSnapshot document : task.getResult()) {
            String name = document.get("name").toString();
            String author = document.get("author").toString();
            String genre = document.get("genre").toString();

            if (matchesNameOrAuthor(keyWord, name, author) && matchesGenre(desiredGenres, genre)) {
                ids.add(Integer.parseInt(document.getId()));
            }
        }
        BookCatalogs.getBooks(ids, books, context, recyclerView, recyclerViewAdapter);
    }

    private static String prepareKeyWord(String keyWord) {
        return keyWord.replace(" ", ".*");
    }

    private static boolean matchesNameOrAuthor(String keyWord, String name, String author) {
        if (keyWord == null || keyWord.equals("")) {
            return true;
        }

        keyWord = prepareKeyWord(keyWord);
        keyWord = keyWord.toLowerCase();
        name = name.toLowerCase();
        author = author.toLowerCase();

        return name.matches(".*" + keyWord + ".*") || author.matches(".*" + keyWord + ".*");
    }

    private static boolean matchesGenre(HashSet<String> desiredGenres, String genre) {
        if (desiredGenres == null || desiredGenres.size() == 0) {
            return true;
        }
        return desiredGenres.contains(genre);
    }
}
