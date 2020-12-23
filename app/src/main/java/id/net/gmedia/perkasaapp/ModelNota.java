package id.net.gmedia.perkasaapp;

import java.util.ArrayList;
import java.util.List;

public class ModelNota {
    private String id;
    private List<ModelTransaksi> listTransaksi = new ArrayList<>();

    public ModelNota(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addTransaksi(ModelTransaksi transaksi){
        listTransaksi.add(transaksi);
    }

    public List<ModelTransaksi> getListTransaksi() {
        return listTransaksi;
    }
}
