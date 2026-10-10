package com.workintech.library.exception;

/**
 * Kütüphane kurallarına aykırı bir işlem yapıldığında fırlatılır
 * (kitap bulunamadı, kitap zaten ödünçte, limit doldu vb.).
 */
public class LibraryException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LibraryException(String message) {
        super(message);
    }
}
