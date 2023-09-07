package com.ivanova.librarian.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ivanova.librarian.R;

public class BookModel {

    private int id;
    private String ISBN;
    private String author;
    private String bookName;
    private String year;
    private String genre;
    private String annotation;
    private Bitmap image;
    private double rating;

    public BookModel(Context context) {
        this.id = -1;
        this.ISBN = "";
        this.author = "";
        this.bookName = "";
        this.year = "";
        this.genre = "";
        this.annotation = "";
        this.rating = 0.0;
        this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.book_cover);
    }

    public BookModel(int id, String ISBN, String author, String bookName, String year, String genre, String annotation, Bitmap image, double rating) {
        this.id = id;
        this.ISBN = ISBN;
        this.author = author;
        this.bookName = bookName;
        this.year = year;
        this.genre = genre;
        this.annotation = annotation;
        this.image = image;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
