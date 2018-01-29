package com.demo.ipcdemo.remote;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.demo.ipcdemo.AppConstants;

/**
 * Date    19/09/2017
 * Author  WestWang
 */

public class MessengerService extends Service {

    private static final String TAG = MessengerService.class.getSimpleName();

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConstants.MSG_FROM_CLIENT:
                    Log.e(TAG, "receive msg from Client: " + msg.getData().getString("msg"));
                    // reply
                    Messenger client = msg.replyTo;
                    Message replyMsg = Message.obtain(null, AppConstants.MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply", "Yes, i have got your message, i'll reply you later.");
                    replyMsg.setData(bundle);
                    try {
                        client.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}