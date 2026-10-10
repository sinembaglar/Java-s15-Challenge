package com.workintech.library.model.book;

import com.workintech.library.model.Displayable;
import com.workintech.library.model.person.Author;
import com.workintech.library.model.person.Reader;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Tüm kitap türlerinin ortak atası. Doğrudan "Book" oluşturulamaz;
 * StudyBook, Journal veya Magazine oluşturulur.
 */
public abstract class Book implements Displayable {
    private final long id;
    private String title;
    private Author author;
    private double price;
    private BookStatus status;
    private int edition;
    private final LocalDate dateOfPurchase;
    private Category category;
    private Reader owner;

    protected Book(long id, String title, Author author, double price, int edition, Category category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.edition = edition;
        this.category = category;
        this.status = BookStatus.AVAILABLE;
        this.dateOfPurchase = LocalDate.now();
    }

    /** Her alt sınıf kendi türünün adını döner. */
    public abstract String getType();

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void updateStatus(BookStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public LocalDate getDateOfPurchase() {
        return dateOfPurchase;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    /** Kitap şu an kimdeyse onu döner, raftaysa null. */
    public Reader getOwner() {
        return owner;
    }

    public void changeOwner(Reader owner) {
        this.owner = owner;
    }

    @Override
    public void display() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        String text = String.format("[#%d] %s - %s | %s | %s | %d. baskı | %.2f TL | %s",
                id, title, author.getName(), category.getDisplayName(), getType(),
                edition, price, status.getDisplayName());
        if (owner != null) {
            text += " (" + owner.getName() + ")";
        }
        return text;
    }

    // İki kitap aynı id'ye sahipse aynı kitaptır. Set içinde tekrar etmemeleri için gerekli.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
