package com.example.db_latihan;

public class Kontak {
    private int img;
    private String nama;
    private String nrp;

    public Kontak(int img, String nama, String noHp) {
        this.img = img;
        this.nama = nama;
        this.nrp = noHp;
    }

    public Kontak(String nama, String noHp) {
        this.img = R.mipmap.ic_launcher_round;
        this.nama = nama;
        this.nrp = noHp;
    }

    public int getImg() {
        return img;
    }

    public String getNama() {
        return nama;
    }

    public String getNrp() {
        return nrp;
    }
}
