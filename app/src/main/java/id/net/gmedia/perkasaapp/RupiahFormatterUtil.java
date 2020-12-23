package id.net.gmedia.perkasaapp;

import java.text.NumberFormat;
import java.util.Locale;

public class RupiahFormatterUtil {
    
    private RupiahFormatterUtil(){
        
    }
    
    public static String getRupiah(int value){
        NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiah = rupiahFormat.format(Double.parseDouble(String.valueOf(value)));

        return "Rp " + rupiah + " ,00";
    }
}
