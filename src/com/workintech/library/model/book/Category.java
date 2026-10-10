package com.workintech.library.model.book;

public enum Category {
    NOVEL("Roman"),
    HISTORY("Tarih"),
    SCIENCE("Bilim"),
    PHILOSOPHY("Felsefe"),
    TECHNOLOGY("Teknoloji"),
    CHILDREN("Çocuk"),
    ART("Sanat");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
