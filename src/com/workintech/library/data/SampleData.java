package com.workintech.library.data;

import com.workintech.library.model.book.Book;
import com.workintech.library.model.book.Category;
import com.workintech.library.model.book.Journal;
import com.workintech.library.model.book.Magazine;
import com.workintech.library.model.book.StudyBook;
import com.workintech.library.model.person.Faculty;
import com.workintech.library.model.person.Student;
import com.workintech.library.service.Library;

/**
 * Uygulama açıldığında boş bir kütüphaneyle başlamamak için örnek veriler.
 */
public class SampleData {

    private SampleData() {
    }

    public static void load(Library library) {
        addStudyBook(library, "Suç ve Ceza", "Fyodor Dostoyevski", 180, 3, Category.NOVEL);
        addStudyBook(library, "Karamazov Kardeşler", "Fyodor Dostoyevski", 240, 2, Category.NOVEL);
        addStudyBook(library, "İnce Memed", "Yaşar Kemal", 150, 5, Category.NOVEL);
        addStudyBook(library, "Masumiyet Müzesi", "Orhan Pamuk", 160, 1, Category.NOVEL);
        addStudyBook(library, "Benim Adım Kırmızı", "Orhan Pamuk", 170, 4, Category.NOVEL);
        addStudyBook(library, "Nutuk", "Mustafa Kemal Atatürk", 200, 10, Category.HISTORY);
        addStudyBook(library, "Sapiens", "Yuval Noah Harari", 220, 6, Category.HISTORY);
        addStudyBook(library, "Kozmos", "Carl Sagan", 190, 3, Category.SCIENCE);
        addStudyBook(library, "Sofie'nin Dünyası", "Jostein Gaarder", 140, 7, Category.PHILOSOPHY);
        addStudyBook(library, "Clean Code", "Robert C. Martin", 450, 1, Category.TECHNOLOGY);
        addStudyBook(library, "Küçük Prens", "Antoine de Saint-Exupéry", 60, 12, Category.CHILDREN);

        long id = library.nextBookId();
        library.newBook(new Journal(id, "Bilim ve Teknik - Ekim", library.findOrCreateAuthor("TÜBİTAK"),
                40, 1, Category.SCIENCE));
        id = library.nextBookId();
        library.newBook(new Journal(id, "Toplumsal Tarih - Eylül", library.findOrCreateAuthor("Tarih Vakfı"),
                55, 1, Category.HISTORY));
        id = library.nextBookId();
        library.newBook(new Magazine(id, "Sanat Dünyamız", library.findOrCreateAuthor("Yapı Kredi Yayınları"),
                35, 1, Category.ART));

        library.addReader(new Student(library.nextMemberId(), "Ayşe Yılmaz", "Kadıköy, İstanbul", "0532 000 00 01"));
        library.addReader(new Student(library.nextMemberId(), "Mehmet Demir", "Çankaya, Ankara", "0532 000 00 02"));
        library.addReader(new Faculty(library.nextMemberId(), "Prof. Dr. Elif Kaya", "Bornova, İzmir", "0532 000 00 03"));
    }

    private static void addStudyBook(Library library, String title, String authorName,
                                     double price, int edition, Category category) {
        Book book = new StudyBook(library.nextBookId(), title, library.findOrCreateAuthor(authorName),
                price, edition, category);
        library.newBook(book);
    }
}
