package com.sma.booktrading.agents.seller;

public class Book {

    private String name;
    private Double price;
    private int discount;
    private int quantity;

    public Book(String name, Double price, int discount, int quantity) {
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
