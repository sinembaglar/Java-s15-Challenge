package com.workintech.library.model.person;

public class Faculty extends Reader {
    public Faculty(long memberId, String name, String address, String phoneNo) {
        super(memberId, name, "Akademisyen", address, phoneNo);
    }

    @Override
    public double getDiscountRate() {
        return 0.2;
    }

    @Override
    public String whoYouAre() {
        return "Ben " + getName() + ", akademisyen üyeyim. Ödünç alırken %20 indirim alırım.";
    }
}
