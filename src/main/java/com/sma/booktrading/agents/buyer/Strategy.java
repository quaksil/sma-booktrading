/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sma.booktrading.agents.buyer;

import com.sma.booktrading.agents.seller.Book;

/**
 *
 * @author aksil
 */
public interface Strategy {

    public Double discount(Double price, int value);

}
