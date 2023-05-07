package com.ivanova.librarian.Models;

import android.graphics.Bitmap;

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

    public BookModel() {
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
