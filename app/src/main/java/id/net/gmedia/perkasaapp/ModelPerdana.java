package id.net.gmedia.perkasaapp;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelPerdana implements Parcelable{
    private String surat_jalan;
    private String nama;
    private int harga;
    private int stok;
    private String tanggal, kodegudang, kodebrg, hargaString, stokString, tipeProgram;

    public ModelPerdana(String nama, String surat_jalan, int harga, int stok){
        this.nama = nama;
        this.surat_jalan = surat_jalan;
        this.stok = stok;
        this.harga = harga;
    }

    public ModelPerdana(String nama, int harga, int stok){
        this.nama = nama;
        this.stok = stok;
        this.harga = harga;
    }

    public ModelPerdana(String nama, int stok){
        this.nama = nama;
        this.stok = stok;
        this.harga = 0;
    }

    public ModelPerdana(String nama, String surat_jalan, String hargaString, String stokString, String tanggal, String kodegudang, String kodebrg, String tipeProgram){

        this.nama = nama;
        this.surat_jalan = surat_jalan;
        this.hargaString = hargaString;
        this.stokString = stokString;
        this.tanggal = tanggal;
        this.kodegudang = kodegudang;
        this.kodebrg = kodebrg;
        this.tipeProgram = tipeProgram;
    }

    public String getSurat_jalan() {
        return surat_jalan;
    }

    public int getHarga(){
        return harga;
    }

    public int getStok() {
        return stok;
    }

    public String getNama() {
        return nama;
    }

    //METHODE PARCELABLE
    //Konstruktor Parcelable
    private ModelPerdana(Parcel in){
        this.nama = in.readString();
        this.surat_jalan = in.readString();
        this.harga = in.readInt();
        this.stok = in.readInt();
    }

    //Kelas CREATOR Parcelable
    public static final Parcelable.Creator<ModelPerdana> CREATOR = new Parcelable.Creator<ModelPerdana>(){

        @Override
        public ModelPerdana createFromParcel(Parcel source) {
            return new ModelPerdana(source);
        }

        @Override
        public ModelPerdana[] newArray(int size) {
            return new ModelPerdana[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nama);
        dest.writeString(this.surat_jalan);
        dest.writeInt(this.harga);
        dest.writeInt(this.stok);
    }

    public String getHargaString() {
        return hargaString;
    }

    public void setHargaString(String hargaString) {
        this.hargaString = hargaString;
    }

    public String getStokString() {
        return stokString;
    }

    public void setStokString(String stokString) {
        this.stokString = stokString;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getKodegudang() {
        return kodegudang;
    }

    public void setKodegudang(String kodegudang) {
        this.kodegudang = kodegudang;
    }

    public String getKodebrg() {
        return kodebrg;
    }

    public void setKodebrg(String kodebrg) {
        this.kodebrg = kodebrg;
    }

    public String getTipeProgram() {
        return tipeProgram;
    }

    public void setTipeProgram(String tipeProgram) {
        this.tipeProgram = tipeProgram;
    }
}