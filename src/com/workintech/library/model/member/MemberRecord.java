package com.workintech.library.model.member;

import java.time.LocalDate;

/**
 * Bir okuyucunun üyelik bilgileri. Her Reader kendi MemberRecord'unu
 * oluşturur ve onunla birlikte yaşar (composition).
 */
public class MemberRecord {
    public static final int MAX_BOOK_LIMIT = 5;

    private final long memberId;
    private final String type;
    private final LocalDate dateOfMembership;
    private int noBooksIssued;
    private String address;
    private String phoneNo;
    private double totalPaid;

    public MemberRecord(long memberId, String type, String address, String phoneNo) {
        this.memberId = memberId;
        this.type = type;
        this.address = address;
        this.phoneNo = phoneNo;
        this.dateOfMembership = LocalDate.now();
    }

    public long getMemberId() {
        return memberId;
    }

    public String getType() {
        return type;
    }

    public LocalDate getDateOfMembership() {
        return dateOfMembership;
    }

    public int getNoBooksIssued() {
        return noBooksIssued;
    }

    public int getMaxBookLimit() {
        return MAX_BOOK_LIMIT;
    }

    public boolean hasReachedLimit() {
        return noBooksIssued >= MAX_BOOK_LIMIT;
    }

    public void incBookIssued() {
        noBooksIssued++;
    }

    public void decBookIssued() {
        if (noBooksIssued > 0) {
            noBooksIssued--;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    /** Üyenin şu an kütüphaneye ödemiş olduğu toplam tutar (iadeler düşülmüş). */
    public double getTotalPaid() {
        return totalPaid;
    }

    public void payBill(double amount) {
        totalPaid += amount;
    }

    public void receiveRefund(double amount) {
        totalPaid -= amount;
    }
}
