package fr.glog.aourir_infos.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

import fr.glog.aourir_infos.MainActivity;
import fr.glog.aourir_infos.MessageActivity;
import fr.glog.aourir_infos.RdvActivity;


public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("================================================================================ onMessageReceived onMessageReceived onMessageReceived    ================================================================================");


        String sented = remoteMessage.getData().get("sented");
        System.out.println("000000000000000000000000000000000000000000000000000000001");


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && sented.equals(firebaseUser.getUid())){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                sendAndOboveNotification(remoteMessage);
                System.out.println("00000000000000000000000000000000000000000000000000000001111");

            }else {
                System.out.println("000000000000000000000000000000000000000000000000000000011111111");
                sendNotification(remoteMessage);

            }
        }

    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String icon = remoteMessage.getData().get("icon");
        String body = remoteMessage.getData().get("body");
        String title = remoteMessage.getData().get("title");

        if (title.equals(" New message")) {
            String user = remoteMessage.getData().get("user");
            RemoteMessage.Notification notification = remoteMessage.getNotification();

            //int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            int j = (int)System.currentTimeMillis();

            Intent intent = new Intent(this, MessageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userid", user);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(Integer.parseInt(icon))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);

            NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int i = 0;
            if (j > 0) {
                i = j;
            }
            noti.notify(i, builder.build());
        }else {

            String allString = remoteMessage.getData().get("user");
            String user = allString.substring(allString.indexOf("=")+1,allString.indexOf("+"));
            String idrvd = allString.substring(allString.indexOf("+")+1,allString.indexOf("#"));
            String Type = allString.substring(allString.indexOf("#")+1,allString.indexOf("/"));
            RemoteMessage.Notification notification = remoteMessage.getNotification();

            //int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            int j = (int)System.currentTimeMillis();


            Intent intent = new Intent(this, RdvActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userid", user);
            bundle.putString("rvdtype", Type);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(Integer.parseInt(icon))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);

            NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void  sendAndOboveNotification(RemoteMessage remoteMessage){
        String icon = remoteMessage.getData().get("icon");
        String body = remoteMessage.getData().get("body");
        String title = remoteMessage.getData().get("title");
        if (title.equals(" New message")){

            String user = remoteMessage.getData().get("user");

            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]",""));
            Intent intent = new Intent(this, MessageActivity.class);
            Bundle bundle =new Bundle();
            bundle.putString("userid",user );
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, j ,intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSound  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            OreoAndAbiveNotifications notifications = new OreoAndAbiveNotifications(this);
            Notification.Builder builder = notifications.getNotification(title,body,pendingIntent,defaultSound, icon );
            NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            int i=0;
            if (j>0){
                i=j;
            }
            notifications.getManager().notify(i, builder.build());

        }else {
            String allString = remoteMessage.getData().get("user");
            String user = allString.substring(allString.indexOf("=")+1,allString.indexOf("+"));
            String idrvd = allString.substring(allString.indexOf("+")+1,allString.indexOf("#"));
            String Type = allString.substring(allString.indexOf("#")+1,allString.indexOf("/"));

            //int j = Integer.parseInt(user.replaceAll("[\\D]",""));
            int j = (int)System.currentTimeMillis();

            Intent intent = new Intent(this, RdvActivity.class);
            Bundle bundle =new Bundle();
            bundle.putString("rdvid", idrvd);
            bundle.putString("rvdtype", Type);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSound  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            OreoAndAbiveNotifications notifications = new OreoAndAbiveNotifications(this);
            Notification.Builder builder = notifications.getNotification(title,body,pendingIntent,defaultSound, icon );
            NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            int i=0;
            if (j>0){
                i=j;
            }
            notifications.getManager().notify(i, builder.build());

        }


    }
}
