package com.ivanova.librarian.Models;

public class BookModel {

    private String author;
    private String bookName;
    private String year;
    private String genre;
    private String annotation;
    private int image;
    private double rating;

    public BookModel(String author, String bookName, String year, String genre, String annotation, int image, double rating) {
        this.author = author;
        this.bookName = bookName;
        this.year = year;
        this.genre = genre;
        this.annotation = annotation;
        this.image = image;
        this.rating = rating;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
