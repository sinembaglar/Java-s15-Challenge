package com.workintech.library.model.person;

/**
 * Sistemi kullanan görevli. Uygulama açılırken şifresiyle giriş yapar.
 */
public class Librarian extends Person {
    private final String password;

    public Librarian(String name, String password) {
        super(name);
        this.password = password;
    }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    @Override
    public String whoYouAre() {
        return "Ben " + getName() + ", bu kütüphanenin görevlisiyim.";
    }
}
