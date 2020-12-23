package id.net.gmedia.perkasaapp;

public class ModelPulsa {
    private int pulsa;
    private int harga;
    private String pulsaString, hargaString, jumlah, totalHarga;

    public ModelPulsa(int pulsa, int harga){
        this.pulsa = pulsa;
        this.harga = harga;
    }

    public ModelPulsa(String pulsaString, String hargaString, String jumlah, String totalHarga){

        this.pulsaString = pulsaString;
        this.hargaString = hargaString;
        this.jumlah = jumlah;
        this.totalHarga = totalHarga;
    }

    public int getHarga() {
        return harga;
    }

    public int getPulsa() {
        return pulsa;
    }

    public String getPulsaString() {
        return pulsaString;
    }

    public void setPulsaString(String pulsaString) {
        this.pulsaString = pulsaString;
    }

    public String getHargaString() {
        return hargaString;
    }

    public void setHargaString(String hargaString) {
        this.hargaString = hargaString;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(String totalHarga) {
        this.totalHarga = totalHarga;
    }
}
