/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sma.booktrading.agents.seller;

import java.util.ArrayList;

/**
 *
 * @author aksil
 */
public class BookStore {

    private ArrayList<Book> books;

    public BookStore() {
        books = new ArrayList<>();
        
        Book xml = new Book("XML", 1000.00, 10, 850.00, 5);
        Book java = new Book("JAVA", 1000.00, 5, 950.00, 10);
        
        books.add(xml);
        books.add(java);
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }

    public Book lookup(String name) {
        Book aBook = null;

        for (Book book : books) {
            if (book.getName().equals(name)) {
                aBook = book;
            }
        }
        return aBook;
    }

    public void add(Book book) {
        Book aBook = null;

        for (Book listBook : books) {
            if (book.getName().equals(listBook.getName())) {
                aBook = listBook;
            }
        }
        if (aBook == null) {
            books.add(book);
        } else {
            aBook.setPrice(book.getPrice());
            aBook.setDiscount(book.getDiscount());
            aBook.setQuantity(book.getQuantity());
        }
    }

}
