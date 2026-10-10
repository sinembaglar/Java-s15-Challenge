package com.workintech.library.model.book;

import com.workintech.library.model.person.Author;

public class Magazine extends Book {
    public Magazine(long id, String title, Author author, double price, int edition, Category category) {
        super(id, title, author, price, edition, category);
    }

    @Override
    public String getType() {
        return "Magazin";
    }
}
