package com.workintech.library.ui;

import com.workintech.library.exception.LibraryException;
import com.workintech.library.model.Displayable;
import com.workintech.library.model.billing.Bill;
import com.workintech.library.model.book.Book;
import com.workintech.library.model.book.Category;
import com.workintech.library.model.book.Journal;
import com.workintech.library.model.book.Magazine;
import com.workintech.library.model.book.StudyBook;
import com.workintech.library.model.person.Author;
import com.workintech.library.model.person.Faculty;
import com.workintech.library.model.person.Reader;
import com.workintech.library.model.person.Student;
import com.workintech.library.service.Library;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Kullanıcıyla konsol üzerinden konuşan sınıf. Burada iş kuralı yok;
 * girdileri okur, Library'yi çağırır ve sonucu yazdırır.
 */
public class ConsoleMenu {
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final Library library;
    private final Scanner scanner;

    public ConsoleMenu(Library library, Scanner scanner) {
        this.library = library;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("=== " + library.getName() + " ===");
        try {
            if (!login()) {
                System.out.println("Çok fazla hatalı deneme. Program kapatılıyor.");
                return;
            }
            runMenu();
        } catch (NoSuchElementException e) {
            // Girdi akışı kapandı (ör. Ctrl+D). Programı sessizce bitir.
            System.out.println();
        }
        System.out.println("Güle güle!");
    }

    private boolean login() {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            String password = readLine("Kütüphaneci şifresi: ");
            if (library.getLibrarian().checkPassword(password)) {
                System.out.println("Hoş geldiniz! " + library.getLibrarian().whoYouAre());
                return true;
            }
            System.out.println("Hatalı şifre. Kalan hak: " + (MAX_LOGIN_ATTEMPTS - attempt));
        }
        return false;
    }

    private void runMenu() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Seçiminiz: ");
            System.out.println();
            try {
                switch (choice) {
                    case 1 -> printAll(library.getBooks(), "Kütüphanede kitap yok.");
                    case 2 -> addBook();
                    case 3 -> findBook();
                    case 4 -> updateBook();
                    case 5 -> removeBook();
                    case 6 -> listByCategory();
                    case 7 -> listByAuthor();
                    case 8 -> lendBook();
                    case 9 -> takeBackBook();
                    case 10 -> printAll(library.getBorrowedBooks(), "Şu an ödünçte kitap yok.");
                    case 11 -> printAll(library.getReaders(), "Henüz üye yok.");
                    case 12 -> addReader();
                    case 13 -> printAll(library.getBills(), "Henüz fatura kesilmedi.");
                    case 0 -> running = false;
                    default -> System.out.println("Geçersiz seçim, lütfen menüden bir numara girin.");
                }
            } catch (LibraryException e) {
                System.out.println("HATA: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("----------- MENÜ -----------");
        System.out.println(" 1. Tüm kitapları listele");
        System.out.println(" 2. Yeni kitap ekle");
        System.out.println(" 3. Kitap seç (id / isim / yazar)");
        System.out.println(" 4. Kitap bilgilerini güncelle");
        System.out.println(" 5. Kitap sil");
        System.out.println(" 6. Kategorideki kitapları listele");
        System.out.println(" 7. Yazarın kitaplarını listele");
        System.out.println(" 8. Kitap ödünç ver");
        System.out.println(" 9. Kitap iade al");
        System.out.println("10. Ödünçteki kitaplar");
        System.out.println("11. Üyeleri listele");
        System.out.println("12. Yeni üye ekle");
        System.out.println("13. Faturaları listele");
        System.out.println(" 0. Çıkış");
    }

    // ---------------- Kitap işlemleri ----------------

    private void addBook() {
        System.out.println("Tür: 1) Kitap  2) Akademik Dergi  3) Magazin");
        int type = readInt("Tür seçin: ", 1, 3);
        String title = readText("Kitap adı: ");
        String authorName = readText("Yazar: ");
        double price = readPositiveDouble("Fiyat (TL): ");
        int edition = readInt("Baskı: ", 1, 1000);
        Category category = chooseCategory(null);

        long id = library.nextBookId();
        Author author = library.findOrCreateAuthor(authorName);
        Book book = switch (type) {
            case 1 -> new StudyBook(id, title, author, price, edition, category);
            case 2 -> new Journal(id, title, author, price, edition, category);
            default -> new Magazine(id, title, author, price, edition, category);
        };
        library.newBook(book);
        System.out.println("Kitap eklendi:");
        book.display();
    }

    private void findBook() {
        System.out.println("Arama: 1) id  2) İsim  3) Yazar");
        int type = readInt("Seçiminiz: ", 1, 3);
        if (type == 1) {
            Book book = library.getBook(readLong("Kitap id: "));
            book.display();
            if (book.getOwner() != null) {
                System.out.println("Kitap şu an " + book.getOwner().getName()
                        + " (Üye #" + book.getOwner().getMemberId() + ") adlı üyede.");
            }
            return;
        }
        String text = readText(type == 2 ? "Kitap adı (tamamı ya da bir kısmı): " : "Yazar adı (tamamı ya da bir kısmı): ");
        List<Book> found = type == 2 ? library.searchByTitle(text) : library.searchByAuthor(text);
        printAll(found, "Aramaya uyan kitap bulunamadı.");
    }

    private void updateBook() {
        Book book = library.getBook(readLong("Güncellenecek kitap id: "));
        System.out.println("Mevcut bilgiler:");
        book.display();
        System.out.println("Değiştirmek istemediğiniz alanı boş bırakıp Enter'a basın.");

        String title = readTextOrDefault("Kitap adı", book.getTitle());
        String authorName = readTextOrDefault("Yazar", book.getAuthor().getName());
        double price = readPositiveDoubleOrDefault("Fiyat (TL)", book.getPrice());
        int edition = readIntOrDefault("Baskı", book.getEdition());
        Category category = chooseCategory(book.getCategory());

        library.updateBook(book.getId(), title, authorName, price, edition, category);
        System.out.println("Kitap güncellendi:");
        book.display();
    }

    private void removeBook() {
        Book book = library.getBook(readLong("Silinecek kitap id: "));
        book.display();
        String answer = readLine("Bu kitabı silmek istediğinize emin misiniz? (e/h): ");
        if (answer.trim().equalsIgnoreCase("e")) {
            library.removeBook(book.getId());
            System.out.println("Kitap silindi.");
        } else {
            System.out.println("Silme iptal edildi.");
        }
    }

    private void listByCategory() {
        Category category = chooseCategory(null);
        printAll(library.getBooksByCategory(category), category.getDisplayName() + " kategorisinde kitap yok.");
    }

    private void listByAuthor() {
        Collection<Author> authors = library.getAuthors();
        System.out.println("Yazarlar:");
        for (Author author : authors) {
            System.out.println(" - " + author.whoYouAre());
        }
        Author author = library.getAuthor(readText("Yazar adı: "));
        author.showBooks();
    }

    // ---------------- Ödünç / iade ----------------

    private void lendBook() {
        Reader reader = library.getReader(readLong("Üye no: "));
        reader.display();
        long bookId = readLong("Ödünç verilecek kitap id: ");

        Bill bill = library.lendBook(bookId, reader.getMemberId());
        System.out.println("Kitap ödünç verildi. Kesilen fatura:");
        bill.display();
    }

    private void takeBackBook() {
        Reader reader = library.getReader(readLong("Üye no: "));
        reader.showBooks();
        if (reader.getBorrowedBooks().isEmpty()) {
            return;
        }
        long bookId = readLong("İade edilen kitap id: ");

        Bill bill = library.takeBackBook(bookId, reader.getMemberId());
        System.out.printf("Kitap iade alındı. %s adlı üyeye %.2f TL geri ödendi.%n", reader.getName(), bill.getAmount());
        bill.display();
    }

    // ---------------- Üye işlemleri ----------------

    private void addReader() {
        System.out.println("Üye türü: 1) Öğrenci (%50 indirim)  2) Akademisyen (%20 indirim)");
        int type = readInt("Tür seçin: ", 1, 2);
        String name = readText("Ad soyad: ");
        String address = readText("Adres: ");
        String phone = readText("Telefon: ");

        long id = library.nextMemberId();
        Reader reader = type == 1
                ? new Student(id, name, address, phone)
                : new Faculty(id, name, address, phone);
        library.addReader(reader);
        System.out.println("Üye eklendi. " + reader.whoYouAre());
        reader.display();
    }

    // ---------------- Yardımcı metodlar ----------------

    /** Book, Reader ve Bill listelerinin hepsini aynı metodla yazdırır (polymorphism). */
    private void printAll(Collection<? extends Displayable> items, String emptyMessage) {
        if (items.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (Displayable item : items) {
            item.display();
        }
        System.out.println("Toplam: " + items.size());
    }

    /** current null değilse Enter'a basınca mevcut kategori korunur. */
    private Category chooseCategory(Category current) {
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ") " + categories[i].getDisplayName());
        }
        if (current == null) {
            return categories[readInt("Kategori seçin: ", 1, categories.length) - 1];
        }
        while (true) {
            String input = readLine("Kategori [" + current.getDisplayName() + "]: ").trim();
            if (input.isEmpty()) {
                return current;
            }
            Integer number = parseInt(input);
            if (number != null && number >= 1 && number <= categories.length) {
                return categories[number - 1];
            }
            System.out.println("Lütfen 1 ile " + categories.length + " arasında bir sayı girin.");
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private String readText(String prompt) {
        while (true) {
            String input = readLine(prompt).trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Bu alan boş bırakılamaz.");
        }
    }

    private String readTextOrDefault(String label, String current) {
        String input = readLine(label + " [" + current + "]: ").trim();
        return input.isEmpty() ? current : input;
    }

    private int readInt(String prompt) {
        while (true) {
            Integer number = parseInt(readLine(prompt));
            if (number != null) {
                return number;
            }
            System.out.println("Lütfen geçerli bir sayı girin.");
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            int number = readInt(prompt);
            if (number >= min && number <= max) {
                return number;
            }
            System.out.println("Lütfen " + min + " ile " + max + " arasında bir sayı girin.");
        }
    }

    private int readIntOrDefault(String label, int current) {
        while (true) {
            String input = readLine(label + " [" + current + "]: ").trim();
            if (input.isEmpty()) {
                return current;
            }
            Integer number = parseInt(input);
            if (number != null && number > 0) {
                return number;
            }
            System.out.println("Lütfen pozitif bir sayı girin.");
        }
    }

    private long readLong(String prompt) {
        while (true) {
            String input = readLine(prompt).trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Lütfen geçerli bir numara girin.");
            }
        }
    }

    private double readPositiveDouble(String prompt) {
        while (true) {
            Double number = parsePositiveDouble(readLine(prompt));
            if (number != null) {
                return number;
            }
            System.out.println("Lütfen sıfırdan büyük bir tutar girin (ör. 120 veya 99,90).");
        }
    }

    private double readPositiveDoubleOrDefault(String label, double current) {
        while (true) {
            String input = readLine(String.format("%s [%.2f]: ", label, current)).trim();
            if (input.isEmpty()) {
                return current;
            }
            Double number = parsePositiveDouble(input);
            if (number != null) {
                return number;
            }
            System.out.println("Lütfen sıfırdan büyük bir tutar girin (ör. 120 veya 99,90).");
        }
    }

    private Integer parseInt(String input) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** "99,90" ve "99.90" ikisi de kabul edilir. */
    private Double parsePositiveDouble(String input) {
        try {
            double number = Double.parseDouble(input.trim().replace(',', '.'));
            return number > 0 ? number : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
