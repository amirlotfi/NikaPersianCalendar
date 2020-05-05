package ir.nikatech.persiancalendar.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import co.ronash.pushe.Pushe;
import ir.nikatech.persiancalendar.view.activity.Push;

public class CheshmakListener extends Service {

    private Intent i;
    private JSONObject message;

    @Override
    public int onStartCommand(Intent intent , int flags , int startId) {

        super.onStartCommand(intent , flags ,startId);

        try {
            message = new JSONObject(intent.getStringExtra("me.cheshmak.data"));
            String type = message.getString("type");

            if (type.equals("link")) {
                String link = message.getString("link_address");
                i = new Intent(getApplicationContext(), Push.class);
                i.putExtra("mode", "1");
                i.putExtra("link", link);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }else if(type.equals("delete")){
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.cancelAll();
                }
            }else if(type.equals("pushe_disable")){
                Pushe.setNotificationOff(getApplicationContext());
            }

        } catch (JSONException e) {
            android.util.Log.e("", "Exception in parsing json", e);
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
