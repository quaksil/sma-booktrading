/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sma.booktrading.agents.buyer;

import com.sma.booktrading.agents.seller.Book;

/**
 *
 * @author aksil
 */
public class Context {

    private final Strategy strategy;

    public Context(Strategy strategy) {

        this.strategy = strategy;
    }

    public Double executeStrategy(Double price, int value) {

        return strategy.discount(price, value);
    }
}
