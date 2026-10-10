package com.workintech.library.model.person;

public class Student extends Reader {
    public Student(long memberId, String name, String address, String phoneNo) {
        super(memberId, name, "Öğrenci", address, phoneNo);
    }

    @Override
    public double getDiscountRate() {
        return 0.5;
    }

    @Override
    public String whoYouAre() {
        return "Ben " + getName() + ", öğrenci üyeyim. Ödünç alırken %50 indirim alırım.";
    }
}
