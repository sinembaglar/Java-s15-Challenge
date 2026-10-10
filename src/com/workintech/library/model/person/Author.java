package com.workintech.library.model.person;

import com.workintech.library.model.book.Book;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Author extends Person {
    // Set: aynı kitap bir yazara iki kez eklenemez.
    private final Set<Book> books = new LinkedHashSet<>();

    public Author(String name) {
        super(name);
    }

    public void newBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    /** Dışarıya sadece okunabilir bir kopya verilir, liste dışarıdan değiştirilemez. */
    public Set<Book> getBooks() {
        return Collections.unmodifiableSet(books);
    }

    public void showBooks() {
        System.out.println(getName() + " adlı yazarın kitapları:");
        for (Book book : books) {
            book.display();
        }
    }

    @Override
    public String whoYouAre() {
        return "Yazar: " + getName() + " (" + books.size() + " kitap)";
    }
}
