package id.net.gmedia.perkasaapp;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelOutlet implements Parcelable {
    private String nama;
    private String alamat;
    private String nomorTelepon;
    private String nomorHp;
    private double latitude;
    private double longitude;
    private int piutang;

    public ModelOutlet(String nama, String alamat, String nomor){
        this.nama = nama;
        this.alamat = alamat;
        this.nomorTelepon = nomor;

        latitude = 0;
        longitude = 0;
    }

    public ModelOutlet(String nama, String alamat, String nomor, double latitude, double longitude){
        this.nama = nama;
        this.alamat = alamat;
        this.nomorTelepon = nomor;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ModelOutlet(String nama, String alamat, String nomorTelepon, String nomorHp, double latitude, double longitude){
        this.nama = nama;
        this.alamat = alamat;
        this.nomorTelepon = nomorTelepon;
        this.nomorHp = nomorHp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNama(){
        return nama;
    }

    public String getAlamat(){
        return alamat;
    }

    public String getNomor() {
        return nomorTelepon;
    }

    public String getNomorHp() {
        return nomorHp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setPiutang(int piutang){
        this.piutang = piutang;
    }

    public int getPiutang(){
        return piutang;
    }

    //PARCELABLE

    //konstruktor Parceable
    private ModelOutlet(Parcel in){
        this.nama = in.readString();
        this.alamat = in.readString();
        this.nomorTelepon = in.readString();
        this.nomorHp = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    //kelas Creator Parcelable
    public static final Parcelable.Creator<ModelOutlet> CREATOR = new Parcelable.Creator<ModelOutlet>(){
        @Override
        public ModelOutlet createFromParcel(Parcel source) {
            return new ModelOutlet(source);
        }

        @Override
        public ModelOutlet[] newArray(int size) {
            return new ModelOutlet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nama);
        dest.writeString(alamat);
        dest.writeString(nomorTelepon);
        dest.writeString(nomorHp);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
