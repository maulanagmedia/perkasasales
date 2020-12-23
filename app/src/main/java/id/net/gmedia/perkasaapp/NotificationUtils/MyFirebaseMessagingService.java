package id.net.gmedia.perkasaapp.NotificationUtils;

/**
 * Created by Shin on 2/17/2017.
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.maulana.custommodul.ItemValidation;

import java.util.HashMap;
import java.util.Map;

import id.net.gmedia.perkasaapp.ActCustomerService.DetailChatActivity;
import id.net.gmedia.perkasaapp.ActVerifikasiReseller.ActivityVerifikasiOutlet1;
import id.net.gmedia.perkasaapp.ActivityHome;
import id.net.gmedia.perkasaapp.R;

/**
 * Created by Shin on 2/13/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static String TAG = "MyFirebaseMessaging";
    private ItemValidation iv = new ItemValidation();
    private String ANDROID_CHANNEL_ID = "id.net.gmedia.perkasaapp";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getFrom());

        Map<String, String> extra = new HashMap<>();
        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
            extra = remoteMessage.getData();
        }

        if(remoteMessage.getNotification() != null){
            Log.d(TAG, "onMessageReceived: " + remoteMessage.getNotification());
            sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(), new HashMap<String, String>(extra));
        }

    }

    private void sendNotification(String title, String body , HashMap<String, String> extra) {

        // need no change
        Intent intent = new Intent(this, ActivityHome.class);
        int typeContent = 0;
        for(String key: extra.keySet()){
            if(key.trim().toUpperCase().equals("JENIS")){
                if(extra.get(key).trim().toUpperCase().equals("MAIN")){
                    typeContent = 1;
                }else if(extra.get(key).trim().toUpperCase().equals("CHAT")){
                    typeContent = 2;
                }else if(extra.get(key).trim().toUpperCase().equals("DEPOSIT")){
                    typeContent = 3;
                }else if(extra.get(key).trim().toUpperCase().equals(ActivityVerifikasiOutlet1.flag)){
                    typeContent = 4;
                }else{
                    typeContent = 9;
                }
            }
        }

        if(typeContent != 9){
            switch (typeContent){
                case 1:
                    intent = new Intent(this, ActivityHome.class);
                    break;
                case 2:
                    intent = new Intent(this, DetailChatActivity.class);
                    intent.putExtra("notif", "1");
                    break;
                default:
                    intent = new Intent(this, ActivityHome.class);
                    break;
            }

            intent.putExtra("backto", true);
            for(String key: extra.keySet()){
                intent.putExtra(key, extra.get(key));
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /*request code*/, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if(typeContent == 2 && DetailChatActivity.isChatActive){

                if(extra.get("nomor").trim().equals(DetailChatActivity.nomor)){

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(intent);
                }
            }

            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            int IconColor = getResources().getColor(R.color.color_notif);

            // Set Notification

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel();
                Notification.Builder builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setColor(IconColor)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(notificationSound)
                        .setContentIntent(pendingIntent);
                Notification notification = builder.build();
                startForeground(0, notification);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0/*Id of Notification*/, builder.build());

            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setColor(IconColor)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(notificationSound)
                        .setContentIntent(pendingIntent);
                Notification notification = builder.build();
                startForeground(0, notification);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0/*Id of Notification*/, builder.build());

            }

            /*NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setColor(IconColor)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(notificationSound)
                    .setContentIntent(pendingIntent);*/

        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    ANDROID_CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
