package id.net.gmedia.perkasaapp;

public class ModelSales {
    private String nama;
    private int jumlah;

    public ModelSales(String nama, int jumlah){
        this.nama = nama;
        this.jumlah = jumlah;
    }

    public String getNama() {
        return nama;
    }

    public int getJumlah() {
        return jumlah;
    }
}
