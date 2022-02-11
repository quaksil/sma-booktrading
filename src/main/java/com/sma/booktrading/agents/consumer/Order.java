/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sma.booktrading.agents.consumer;

import java.io.Serializable;

/**
 *
 * @author aksil
 */
public class Order implements Serializable {

    String book;

    public Order(String book) {
        this.book = book;

    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

}
