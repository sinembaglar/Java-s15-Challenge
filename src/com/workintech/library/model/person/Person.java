package com.workintech.library.model.person;

/**
 * Yazar, okuyucu ve kütüphaneci için ortak ata sınıf.
 */
public abstract class Person {
    private String name;

    protected Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** Her kişi türü kendini farklı tanıtır. */
    public abstract String whoYouAre();

    @Override
    public String toString() {
        return name;
    }
}
