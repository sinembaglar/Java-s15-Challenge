package com.workintech.library.service;

import com.workintech.library.exception.LibraryException;
import com.workintech.library.model.billing.Bill;
import com.workintech.library.model.book.Book;
import com.workintech.library.model.book.BookStatus;
import com.workintech.library.model.book.Category;
import com.workintech.library.model.person.Author;
import com.workintech.library.model.person.Librarian;
import com.workintech.library.model.person.Reader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Kütüphanenin tüm verisini tutar ve kitap / üye / ödünç işlemlerini yönetir.
 */
public class Library {
    private static final Locale TR = Locale.forLanguageTag("tr");

    private final String name;
    private final Librarian librarian;

    // Veriler Map'lerde saklanır: id ile arama O(1) / sıralı listeleme kolay olsun diye.
    private final Map<Long, Book> books = new TreeMap<>();
    private final Map<Long, Reader> readers = new TreeMap<>();
    // Anahtar: yazar adının küçük harfli hali. "orhan pamuk" ve "Orhan Pamuk" aynı yazardır.
    private final Map<String, Author> authors = new TreeMap<>();
    // Ödünçteki kitabın id'si -> o ödünç için kesilen fatura.
    private final Map<Long, Bill> activeBills = new HashMap<>();
    // Tüm faturaların geçmişi (kesildiği sırayla).
    private final List<Bill> bills = new ArrayList<>();

    private long nextBookId = 1;
    private long nextMemberId = 1;
    private long nextBillId = 1;

    public Library(String name, Librarian librarian) {
        this.name = name;
        this.librarian = librarian;
    }

    public String getName() {
        return name;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    // ---------------- Kitap işlemleri ----------------

    public long nextBookId() {
        return nextBookId++;
    }

    public Author findOrCreateAuthor(String authorName) {
        String key = normalize(authorName);
        Author author = authors.get(key);
        if (author == null) {
            author = new Author(authorName.trim());
            authors.put(key, author);
        }
        return author;
    }

    public void newBook(Book book) {
        if (books.containsKey(book.getId())) {
            throw new LibraryException("Bu id ile zaten bir kitap var: " + book.getId());
        }
        books.put(book.getId(), book);
        book.getAuthor().newBook(book);
    }

    public Book getBook(long id) {
        Book book = books.get(id);
        if (book == null) {
            throw new LibraryException("Kitap bulunamadı. id: " + id);
        }
        return book;
    }

    public List<Book> searchByTitle(String text) {
        String search = normalize(text);
        List<Book> result = new ArrayList<>();
        for (Book book : books.values()) {
            if (normalize(book.getTitle()).contains(search)) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> searchByAuthor(String text) {
        String search = normalize(text);
        List<Book> result = new ArrayList<>();
        for (Book book : books.values()) {
            if (normalize(book.getAuthor().getName()).contains(search)) {
                result.add(book);
            }
        }
        return result;
    }

    public void updateBook(long id, String title, String authorName, double price, int edition, Category category) {
        Book book = getBook(id);
        book.setTitle(title);
        book.setPrice(price);
        book.setEdition(edition);
        book.setCategory(category);

        Author newAuthor = findOrCreateAuthor(authorName);
        Author oldAuthor = book.getAuthor();
        if (newAuthor != oldAuthor) {
            oldAuthor.removeBook(book);
            removeAuthorIfEmpty(oldAuthor);
            book.setAuthor(newAuthor);
            newAuthor.newBook(book);
        }
    }

    public Book removeBook(long id) {
        Book book = getBook(id);
        if (!book.isAvailable()) {
            throw new LibraryException("Ödünçteki bir kitap silinemez. Kitap şu an " + book.getOwner().getName() + " adlı üyede.");
        }
        books.remove(id);
        book.getAuthor().removeBook(book);
        removeAuthorIfEmpty(book.getAuthor());
        return book;
    }

    public List<Book> getBooksByCategory(Category category) {
        List<Book> result = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getCategory() == category) {
                result.add(book);
            }
        }
        return result;
    }

    public Collection<Book> getBooks() {
        return Collections.unmodifiableCollection(books.values());
    }

    public List<Book> getBorrowedBooks() {
        List<Book> result = new ArrayList<>();
        for (Book book : books.values()) {
            if (!book.isAvailable()) {
                result.add(book);
            }
        }
        return result;
    }

    public Author getAuthor(String authorName) {
        Author author = authors.get(normalize(authorName));
        if (author == null) {
            throw new LibraryException("Yazar bulunamadı: " + authorName);
        }
        return author;
    }

    public Collection<Author> getAuthors() {
        return Collections.unmodifiableCollection(authors.values());
    }

    private void removeAuthorIfEmpty(Author author) {
        if (author.getBooks().isEmpty()) {
            authors.remove(normalize(author.getName()));
        }
    }

    // ---------------- Üye işlemleri ----------------

    public long nextMemberId() {
        return nextMemberId++;
    }

    public void addReader(Reader reader) {
        if (readers.containsKey(reader.getMemberId())) {
            throw new LibraryException("Bu numarayla zaten bir üye var: " + reader.getMemberId());
        }
        readers.put(reader.getMemberId(), reader);
    }

    /** Üye numarasını doğrular ve üyeyi döner. */
    public Reader getReader(long memberId) {
        Reader reader = readers.get(memberId);
        if (reader == null) {
            throw new LibraryException("Üye bulunamadı. Üye no: " + memberId);
        }
        return reader;
    }

    public Collection<Reader> getReaders() {
        return Collections.unmodifiableCollection(readers.values());
    }

    // ---------------- Ödünç / iade ----------------

    /**
     * Kitabı üyeye ödünç verir ve fatura keser.
     * Kitap rafta değilse veya üye 5 kitap limitine ulaştıysa hata fırlatır.
     */
    public Bill lendBook(long bookId, long memberId) {
        Book book = getBook(bookId);
        Reader reader = getReader(memberId);

        if (!book.isAvailable()) {
            throw new LibraryException("\"" + book.getTitle() + "\" şu an başka bir üyede: " + book.getOwner().getName());
        }
        if (!reader.canBorrow()) {
            throw new LibraryException(reader.getName() + " " + reader.getRecord().getMaxBookLimit()
                    + " kitap limitine ulaştı. Önce bir kitap iade etmeli.");
        }

        book.updateStatus(BookStatus.BORROWED);
        book.changeOwner(reader);
        reader.borrowBook(book);

        Bill bill = createBill(reader, book);
        reader.getRecord().payBill(bill.getAmount());
        return bill;
    }

    /**
     * Üyenin elindeki kitabı geri alır, faturayı iade edildi olarak işaretler
     * ve ücreti üyeye geri öder.
     */
    public Bill takeBackBook(long bookId, long memberId) {
        Book book = getBook(bookId);
        Reader reader = getReader(memberId);

        if (!reader.hasBook(book)) {
            throw new LibraryException("\"" + book.getTitle() + "\" bu üyede değil.");
        }

        reader.returnBook(book);
        book.changeOwner(null);
        book.updateStatus(BookStatus.AVAILABLE);

        Bill bill = activeBills.remove(bookId);
        bill.markRefunded();
        reader.getRecord().receiveRefund(bill.getAmount());
        return bill;
    }

    private Bill createBill(Reader reader, Book book) {
        Bill bill = new Bill(nextBillId++, reader, book, reader.calculateFee(book));
        bills.add(bill);
        activeBills.put(book.getId(), bill);
        return bill;
    }

    public List<Bill> getBills() {
        return Collections.unmodifiableList(bills);
    }

    private static String normalize(String text) {
        return text.trim().toLowerCase(TR);
    }
}
