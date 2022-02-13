package com.sma.booktrading.agents.seller;

public class Book {

    private String name;
    private Double price;
    private Double minPrice;
    private int discount;
    private int quantity;

    public Book(String name, Double price, int discount, Double minPrice, int quantity) {
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.minPrice = minPrice;
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

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
