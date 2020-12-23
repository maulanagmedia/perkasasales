package id.net.gmedia.perkasaapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PinManager {

	SharedPreferences pref;
	Editor editor;
	Context context;

	int PRIVATE_MODE = 0;

	private static final String PREF_NAME = "GmediaPerkasaAppPin";

	public static final String TAG_PIN = "pin";

	public PinManager(Context context){
		this.context = context;
		pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void savePin(String pin){

		editor.putString(TAG_PIN, pin);
		editor.commit();
	}

	public String getPin(){

		return pref.getString(TAG_PIN, "");
	}

	public void clearPin(){

		try {
			editor.clear();
			editor.commit();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public boolean isPinSaved(){
		if(getPin().isEmpty()){
			return false;
		}else{
			return true;
		}
	}
}
