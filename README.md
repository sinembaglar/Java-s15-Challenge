# Kütüphane Otomasyonu (Java OOP)

Workintech S15 Challenge: Nesne tabanlı olarak tasarlanmış, konsoldan kullanılan bir kütüphane otomasyonu.
Kütüphane görevlisi kitapları yönetir, üyelere ödünç verir ve fatura keser. Kitap iade edilince ücret üyeye geri ödenir.

<img src="docs/demo.png" alt="Konsolda ödünç verme, hata, iade ve fatura listesi" width="700">

## Çalıştırma

IntelliJ IDEA ile projeyi açıp `src/Main.java` dosyasını çalıştırın (JDK 17 veya üstü). Terminalden:

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main
```

Kütüphaneci şifresi **`1234`**. Uygulama örnek verilerle açılır: 14 yayın ve 3 üye.

## İsterler

**Tasarım**

| İster | Projede karşılığı |
|---|---|
| En az 10 sınıf | 17 sınıf, 1 arayüz, 2 enum |
| Hiyerarşinin çizili gösterimi | Aşağıdaki sınıf diyagramı |
| Anlamlı paketler | `model`, `service`, `ui`, `data`, `exception` |
| Encapsulation | Alanlar `private` (tek istisna `MAX_BOOK_LIMIT` sabiti), değişmemesi gerekenler `final`. Koleksiyonlar dışarıya salt okunur verilir. |
| Composition | Her `Reader` kendi `MemberRecord` nesnesini oluşturur, kayıt okuyucudan ayrı yaşamaz. |
| Inheritance | `Person` → `Author`, `Reader`, `Librarian` · `Reader` → `Student`, `Faculty` · `Book` → `StudyBook`, `Journal`, `Magazine` |
| Abstract class / Interface | Abstract: `Person`, `Reader`, `Book` · Interface: `Displayable` |
| Polymorphism | Ödünç ücreti üye türüne göre farklı indirimle hesaplanır (`getDiscountRate()`). Kitap, üye ve fatura listeleri tek bir metotla yazdırılır. |
| List | Arama sonuçları ve fatura geçmişi |
| Set | Bir üyedeki ve bir yazardaki kitaplar: aynı kitap iki kez yer alamaz. |
| Map | Kitaplar, üyeler, yazarlar ve aktif faturalar `Library` içinde Map'lerde saklanır. |

**İşlevler** (konsol menüsü, `Scanner` ile)

| İster | Menü |
|---|---|
| Yeni kitap ekleme | 2 |
| Id, isim veya yazara göre kitap seçme | 3 |
| Kitap bilgilerini güncelleme | 4 |
| Kitap silme (ödünçteki kitap silinemez) | 5 |
| Bir kategorideki kitapları listeleme | 6 |
| Bir yazarın kitaplarını listeleme | 7 |
| Ödünç verme: kitap rafta değilse verilmez, kimde olduğu tutulur, fatura kesilir | 8, 10 |
| İade alma ve ücreti geri ödeme | 9 |
| 5 kitap limiti | 8 |

Öğrenciler %50, akademisyenler %20 indirimli öder. Hatalı girişler (sayı yerine harf, boş alan, geçersiz seçim) programı çökertmez.

## Sınıf diyagramı

```mermaid
classDiagram
    direction TB

    class Displayable {
        <<interface>>
    }
    class Person {
        <<abstract>>
    }
    class Reader {
        <<abstract>>
    }
    class Book {
        <<abstract>>
    }
    class Category {
        <<enumeration>>
    }
    class BookStatus {
        <<enumeration>>
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
    Book --> Author
    Book --> Reader : ödünç alan
    Book --> Category
    Book --> BookStatus
    Bill --> Reader
    Bill --> Book

    Library o-- Book
    Library o-- Reader
    Library o-- Author
    Library o-- Bill
    Library --> Librarian
    ConsoleMenu --> Library
```

## Paket yapısı

```
src/com/workintech/library
├── model       Book, Person ve alt sınıfları, MemberRecord, Bill, Displayable
├── service     Library: veriler ve bütün iş kuralları
├── ui          ConsoleMenu: konsol menüsü
├── data        SampleData: başlangıç verileri
└── exception   LibraryException
```
