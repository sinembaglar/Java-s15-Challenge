package com.workintech.library.model.book;

public enum BookStatus {
    AVAILABLE("Rafta"),
    BORROWED("Ödünçte");

    private final String displayName;

    BookStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
