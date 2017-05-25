package com.example.user.eksibiseyler;

/**
 * Created by user on 12.05.2017.
 */

public class Product {
    private String image;
    private String title;
    private String stats;
    private String domain;

    public Product(String image, String name, String price, String domain) {
        this.image = image;
        this.title = name;
        this.stats = price;
        this.domain = domain;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    public String getStats() {
        return stats;
    }

    public void setPrice(String price) {
        this.stats = price;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }
}