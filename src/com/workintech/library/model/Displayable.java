package com.workintech.library.model;

/**
 * Konsola kendini yazdırabilen her nesne bu arayüzü uygular.
 * Book, Reader ve Bill farklı şekillerde yazdırılır (polymorphism).
 */
public interface Displayable {
    void display();
}
