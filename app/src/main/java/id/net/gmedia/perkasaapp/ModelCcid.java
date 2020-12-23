package id.net.gmedia.perkasaapp;

public class    ModelCcid {
    private String ccid;
    private String nama;
    private String harga;
    private boolean selected;

    public ModelCcid(String ccid, String nama, String harga){
        this.ccid = ccid;
        this.nama = nama;
        this.harga = harga;
    }

    public ModelCcid(String ccid, String nama, String harga, boolean selected){
        this.ccid = ccid;
        this.nama = nama;
        this.harga = harga;
        this.selected = selected;
    }

    public String getCcid() {
        return ccid;
    }

    public String getNama() {
        return nama;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }
}
