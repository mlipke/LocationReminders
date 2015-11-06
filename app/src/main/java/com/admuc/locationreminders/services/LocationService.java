package com.admuc.locationreminders.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.design.widget.Snackbar;
import android.util.Log;

public class LocationService extends Service {

    static final int MSG_GET_POIS = 1;

    final Messenger messenger = new Messenger(new MessageHandler());

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_GET_POIS:
                    Log.d("Location Service", "Getting data!");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


}
