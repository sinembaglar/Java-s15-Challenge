package com.workintech.library.model.billing;

import com.workintech.library.model.Displayable;
import com.workintech.library.model.book.Book;
import com.workintech.library.model.person.Reader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Kitap ödünç verilirken kesilen fatura. Kitap iade edilince
 * fatura "iade edildi" olarak işaretlenir ve ücret geri ödenir.
 */
public class Bill implements Displayable {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final long id;
    private final Reader reader;
    private final Book book;
    private final double amount;
    private final LocalDateTime issuedAt;
    private LocalDateTime refundedAt;

    public Bill(long id, Reader reader, Book book, double amount) {
        this.id = id;
        this.reader = reader;
        this.book = book;
        this.amount = amount;
        this.issuedAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public Reader getReader() {
        return reader;
    }

    public Book getBook() {
        return book;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public boolean isRefunded() {
        return refundedAt != null;
    }

    public void markRefunded() {
        this.refundedAt = LocalDateTime.now();
    }

    @Override
    public void display() {
        String state = isRefunded()
                ? "İade edildi (" + refundedAt.format(FORMAT) + ")"
                : "Ödendi";
        System.out.printf("[Fatura #%d] %s | %s | %.2f TL | %s | %s%n",
                id, reader.getName(), book.getTitle(), amount, issuedAt.format(FORMAT), state);
    }
}
