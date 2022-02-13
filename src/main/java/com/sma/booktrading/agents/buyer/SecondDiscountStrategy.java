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
public class SecondDiscountStrategy implements Strategy {

    @Override
    public Double discount(Double price, int value) {

        Double finalPrice = 0.0;
        finalPrice = price - (price * 5 / 100);

        return finalPrice;

    }
}
