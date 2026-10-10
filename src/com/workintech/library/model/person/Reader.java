package com.workintech.library.model.person;

import com.workintech.library.model.Displayable;
import com.workintech.library.model.book.Book;
import com.workintech.library.model.member.MemberRecord;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Kütüphane üyesi. Student ve Faculty bu sınıftan türer
 * ve ödünç alma ücretinde farklı indirim uygular.
 */
public abstract class Reader extends Person implements Displayable {
    // Composition: üyelik kaydı okuyucuyla birlikte oluşturulur, dışarıdan verilmez.
    private final MemberRecord record;
    // Set: okuyucu aynı kitabı iki kez ödünç almış görünemez.
    private final Set<Book> borrowedBooks = new LinkedHashSet<>();

    protected Reader(long memberId, String name, String type, String address, String phoneNo) {
        super(name);
        this.record = new MemberRecord(memberId, type, address, phoneNo);
    }

    /** 0 ile 1 arasında indirim oranı (0.5 = %50). */
    public abstract double getDiscountRate();

    /** Kitabın fiyatına okuyucu türüne göre indirim uygulanır. */
    public double calculateFee(Book book) {
        return book.getPrice() * (1 - getDiscountRate());
    }

    public long getMemberId() {
        return record.getMemberId();
    }

    public MemberRecord getRecord() {
        return record;
    }

    public boolean canBorrow() {
        return !record.hasReachedLimit();
    }

    public boolean hasBook(Book book) {
        return borrowedBooks.contains(book);
    }

    public void borrowBook(Book book) {
        if (borrowedBooks.add(book)) {
            record.incBookIssued();
        }
    }

    public void returnBook(Book book) {
        if (borrowedBooks.remove(book)) {
            record.decBookIssued();
        }
    }

    public Set<Book> getBorrowedBooks() {
        return Collections.unmodifiableSet(borrowedBooks);
    }

    public void showBooks() {
        if (borrowedBooks.isEmpty()) {
            System.out.println(getName() + " üzerinde kitap yok.");
            return;
        }
        System.out.println(getName() + " üzerindeki kitaplar:");
        for (Book book : borrowedBooks) {
            book.display();
        }
    }

    @Override
    public void display() {
        System.out.printf("[Üye #%d] %s | %s | %d/%d kitap | Ödenen: %.2f TL%n",
                getMemberId(), getName(), record.getType(),
                record.getNoBooksIssued(), record.getMaxBookLimit(), record.getTotalPaid());
    }
}
