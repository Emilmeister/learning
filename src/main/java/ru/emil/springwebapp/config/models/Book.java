package ru.emil.springwebapp.config.models;


import java.io.Serializable;

public class Book implements Serializable{
    private int id;
    private String authorName;
    private String bookName;
    private String type;

    public Book() {
    }

    public Book(int id, String authorName, String bookName, String type) {
        this.id = id;
        this.authorName = authorName;
        this.bookName = bookName;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
