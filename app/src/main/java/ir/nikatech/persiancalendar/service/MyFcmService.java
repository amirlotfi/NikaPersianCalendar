package ir.nikatech.persiancalendar.service;

import com.google.firebase.messaging.RemoteMessage;

import co.ronash.pushe.Pushe;
import me.cheshmak.android.sdk.core.push.CheshmakFirebaseMessagingService;

public class MyFcmService extends CheshmakFirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(isCheshmakMessage(remoteMessage)){
            super.onMessageReceived(remoteMessage);
        }else {
            Pushe.getFcmHandler(this).onMessageReceived(remoteMessage);
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Pushe.getFcmHandler(this).onNewToken(s);
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        Pushe.getFcmHandler(this).onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s,e);
        Pushe.getFcmHandler(this).onSendError(s, e);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Pushe.getFcmHandler(this).onDeletedMessages();
    }
}
