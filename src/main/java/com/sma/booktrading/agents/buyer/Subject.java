/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sma.booktrading.agents.buyer;

import com.sma.booktrading.agents.seller.Observer;

/**
 *
 * @author aksil
 */
public interface Subject {

        public void attach(Observer o);

        public void detach(Observer o);

        public void notifyUpdate(int strategy);
  
}
