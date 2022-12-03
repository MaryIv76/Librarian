package com.ivanova.librarian.Models;

import android.net.Uri;

public class Book {

    private String author;
    private String bookName;
    private String year;
    private String genre;
    private String annotation;
    private int image;

    public Book(String author, String bookName, String year, String genre, String annotation, int image) {
        this.author = author;
        this.bookName = bookName;
        this.year = year;
        this.genre = genre;
        this.annotation = annotation;
        this.image = image;
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
