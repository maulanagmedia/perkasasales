package id.net.gmedia.perkasaapp.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import id.net.gmedia.perkasaapp.ActDirectSelling.DirectSellingPulsa;

/**
 * Created by Shinmaul on 3/9/2018.
 */

public class SmsReceiverService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus=(Object[])intent.getExtras().get("pdus");
        SmsMessage shortMessage=SmsMessage.createFromPdu((byte[]) pdus[0]);

        Log.d("SMSReceiver","SMS message sender: "+
                shortMessage.getOriginatingAddress());
        Log.d("SMSReceiver","SMS message text: "+
                shortMessage.getDisplayMessageBody());

        DirectSellingPulsa.addTambahBalasan(shortMessage.getOriginatingAddress(), shortMessage.getDisplayMessageBody());
    }
}
