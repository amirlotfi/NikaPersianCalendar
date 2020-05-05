package ir.nikatech.persiancalendar.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import co.ronash.pushe.Pushe;
import co.ronash.pushe.PusheListenerService;
import ir.nikatech.persiancalendar.view.activity.Push;

public class PusheListener extends PusheListenerService {

    private Intent i;

    @Override
    public void onMessageReceived(JSONObject message, JSONObject content) {

        android.util.Log.i("Pushe", "Custom json Message: " + message.toString());

        try {
            String type = message.getString("type");
            android.util.Log.i("Pushe", "Json Message\n type : " + type);

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
    }
}






