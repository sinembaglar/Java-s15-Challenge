# Kütüphane Otomasyonu (Java OOP)

Workintech S15 Challenge: Java ile nesne tabanlı tasarlanmış, konsoldan kullanılan bir kütüphane otomasyonu.
Kütüphane görevlisi kitap ekleyip güncelleyebilir, arayabilir, silebilir; üyelere kitap ödünç verip iade alabilir.
Ödünç verilen her kitap için fatura kesilir, kitap iade edilince ücret üyeye geri ödenir.

## Çalıştırma

1. Projeyi IntelliJ IDEA ile açın (JDK 17 veya üstü).
2. `src/Main.java` dosyasını çalıştırın.
3. Kütüphaneci şifresi: **`1234`**

Terminalden çalıştırmak için:

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main
```

Uygulama örnek verilerle açılır: 14 kitap (kitap, akademik dergi, magazin) ve 3 üye (2 öğrenci, 1 akademisyen).

## Özellikler

| Menü | İşlem |
|---|---|
| 1 | Tüm kitapları listele |
| 2 | Yeni kitap ekle (tür, ad, yazar, fiyat, baskı, kategori) |
| 3 | Kitap seç: id, isim veya yazara göre (büyük/küçük harf duyarsız, kısmi eşleşme) |
| 4 | Kitap bilgilerini güncelle (boş bırakılan alan değişmez) |
| 5 | Kitap sil (ödünçteki kitap silinemez) |
| 6 | Bir kategorideki tüm kitapları listele |
| 7 | Bir yazarın tüm kitaplarını listele |
| 8 | Kitap ödünç ver ve fatura kes |
| 9 | Kitap iade al ve ücreti geri öde |
| 10 | Ödünçteki kitapları ve kimde olduklarını listele |
| 11 / 12 | Üyeleri listele / yeni üye ekle |
| 13 | Fatura geçmişi |

**Kurallar**

- Kitap ancak raftaysa (başka bir üyede değilse) ödünç verilebilir. Kitabın hangi üyede olduğu tutulur.
- Her üyenin **en fazla 5 kitap** limiti vardır. Limit dolunca yeni kitap alamaz.
- Ödünç ücreti kitap fiyatı üzerinden hesaplanır: **öğrenciler %50, akademisyenler %20 indirim** alır.
- İade edilince fatura "iade edildi" olarak işaretlenir ve aynı tutar üyeye geri ödenir.
- Hatalı girişler (harf yerine sayı, boş alan, geçersiz menü seçimi) programı çökertmez, tekrar sorulur.

## Paket yapısı

```
src
├── Main.java                         Uygulamanın giriş noktası
└── com/workintech/library
    ├── model
    │   ├── Displayable.java          Konsola yazdırılabilen nesneler için arayüz
    │   ├── book                      Book (abstract), StudyBook, Journal, Magazine, Category, BookStatus
    │   ├── person                    Person (abstract), Author, Reader (abstract), Student, Faculty, Librarian
    │   ├── member                    MemberRecord (üyelik kaydı ve 5 kitap limiti)
    │   └── billing                   Bill (fatura)
    ├── service                       Library: veri saklama ve tüm iş kuralları
    ├── exception                     LibraryException
    ├── data                          SampleData: başlangıç verileri
    └── ui                            ConsoleMenu: Scanner ile menü
```

## Sınıf diyagramı

```mermaid
classDiagram
    direction TB

    class Displayable {
        <<interface>>
        +display()
    }

    class Person {
        <<abstract>>
        -String name
        +whoYouAre()* String
    }
    class Author {
        -Set~Book~ books
        +newBook(Book)
        +removeBook(Book)
        +showBooks()
    }
    class Reader {
        <<abstract>>
        -MemberRecord record
        -Set~Book~ borrowedBooks
        +getDiscountRate()* double
        +calculateFee(Book) double
        +canBorrow() boolean
        +borrowBook(Book)
        +returnBook(Book)
        +showBooks()
    }
    class Student {
        +getDiscountRate() double
    }
    class Faculty {
        +getDiscountRate() double
    }
    class Librarian {
        -String password
        +checkPassword(String) boolean
    }
    class MemberRecord {
        +int MAX_BOOK_LIMIT$
        -long memberId
        -String type
        -LocalDate dateOfMembership
        -int noBooksIssued
        -String address
        -String phoneNo
        -double totalPaid
        +incBookIssued()
        +decBookIssued()
        +payBill(double)
        +receiveRefund(double)
    }

    class Book {
        <<abstract>>
        -long id
        -String title
        -Author author
        -double price
        -BookStatus status
        -int edition
        -LocalDate dateOfPurchase
        -Category category
        -Reader owner
        +getType()* String
        +changeOwner(Reader)
        +getOwner() Reader
        +updateStatus(BookStatus)
    }
    class StudyBook
    class Journal
    class Magazine
    class Category {
        <<enumeration>>
        NOVEL
        HISTORY
        SCIENCE
        PHILOSOPHY
        TECHNOLOGY
        CHILDREN
        ART
    }
    class BookStatus {
        <<enumeration>>
        AVAILABLE
        BORROWED
    }

    class Bill {
        -long id
        -Reader reader
        -Book book
        -double amount
        -LocalDateTime issuedAt
        -LocalDateTime refundedAt
        +markRefunded()
    }

    class Library {
        -Map~Long, Book~ books
        -Map~Long, Reader~ readers
        -Map~String, Author~ authors
        -Map~Long, Bill~ activeBills
        -List~Bill~ bills
        +newBook(Book)
        +getBook(long) Book
        +searchByTitle(String) List~Book~
        +searchByAuthor(String) List~Book~
        +updateBook(...)
        +removeBook(long)
        +getBooksByCategory(Category) List~Book~
        +lendBook(long, long) Bill
        +takeBackBook(long, long) Bill
    }
    class ConsoleMenu {
        -Library library
        -Scanner scanner
        +start()
    }

    Person <|-- Author
    Person <|-- Reader
    Person <|-- Librarian
    Reader <|-- Student
    Reader <|-- Faculty
    Book <|-- StudyBook
    Book <|-- Journal
    Book <|-- Magazine

    Displayable <|.. Book
    Displayable <|.. Reader
    Displayable <|.. Bill

    Reader *-- MemberRecord : composition
    Library o-- Book
    Library o-- Reader
    Library o-- Author
    Library o-- Bill
    Library --> Librarian
    Book --> Author
    Book --> Category
    Book --> BookStatus
    Book --> Reader : owner
    Bill --> Reader
    Bill --> Book
    ConsoleMenu --> Library
```

## OOP gereksinimleri nerede karşılandı?

| Gereksinim | Projede karşılığı |
|---|---|
| En az 10 sınıf | 17 sınıf, 1 arayüz ve 2 enum (yukarıdaki diyagram) |
| Anlamlı paketler | `model`, `service`, `ui`, `data`, `exception` |
| Encapsulation | Tüm alanlar `private`. Değişmemesi gerekenler (`id`, `dateOfPurchase`, `memberId`) `final` ve setter'ı yok. Koleksiyonlar dışarıya `Collections.unmodifiable...` ile verilir. |
| Composition | Her `Reader`, kendi `MemberRecord` nesnesini constructor içinde oluşturur. Kayıt okuyucudan ayrı yaşamaz. |
| Inheritance | `Person` → `Author`, `Reader`, `Librarian`; `Reader` → `Student`, `Faculty`; `Book` → `StudyBook`, `Journal`, `Magazine` |
| Abstract class / Interface | Abstract: `Person`, `Reader`, `Book`. Interface: `Displayable` (`Book`, `Reader`, `Bill` uygular). |
| Polymorphism | `calculateFee()` üye türüne göre farklı indirim uygular (`getDiscountRate()`). `whoYouAre()` ve `getType()` her alt sınıfta farklıdır. `ConsoleMenu.printAll(Collection<? extends Displayable>)` kitap, üye ve fatura listelerini tek metodla yazdırır. |
| List | Arama ve listeleme sonuçları, fatura geçmişi (`List<Bill>`) |
| Set | `Reader.borrowedBooks` ve `Author.books`: aynı kitap bir üyede veya yazarda iki kez görünemez (`Book.equals/hashCode` id üzerinden). |
| Map | Kitaplar, üyeler, yazarlar ve aktif faturalar `Library` içinde Map'lerde saklanır. |
| Konsol uygulaması | `Scanner` ile menü (`ConsoleMenu`) |
