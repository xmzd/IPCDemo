package com.demo.ipcdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.demo.ipcdemo.aidl.Book;
import com.demo.ipcdemo.aidl.IBookManager;
import com.demo.ipcdemo.aidl.IOnNewBookArrivedListener;
import com.demo.ipcdemo.remote.BookManagerService;
import com.demo.ipcdemo.remote.MessengerService;

import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConstants.MSG_FROM_SERVICE:
                    Log.e(TAG, "receive msg from Service: " + msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());

    private Messenger mService;

    private ServiceConnection mMessengerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = new Messenger(iBinder);
            Message msg = Message.obtain(null, AppConstants.MSG_FROM_CLIENT);
            Bundle bundle = new Bundle();
            bundle.putString("msg", "Hello, this is client!");
            msg.setData(bundle);
            msg.replyTo = mGetReplyMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "mMessengerConnection send " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private IBookManager mRemoteBookManager;
    private ServiceConnection mAidlConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mRemoteBookManager = IBookManager.Stub.asInterface(iBinder);
            try {
                List<Book> list = mRemoteBookManager.getBookList();
                Log.e(TAG, "query book list, list type is " + list.getClass().getCanonicalName());
                Log.e(TAG, "query book list: " + list.toString());
                Book newBook = new Book(3, "Android开发艺术探讨");
                mRemoteBookManager.addBook(newBook);
                Log.e(TAG, "add book: " + newBook);
                List<Book> newList = mRemoteBookManager.getBookList();
                Log.e(TAG, "query book list: " + newList.toString());
                mRemoteBookManager.registerNewBookArrivedListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                Log.e(TAG, "mAidlConnection " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mRemoteBookManager = null;
            Log.e(TAG, "binder died!");
        }
    };
    private MyHandler mHandler;
    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConstants.MSG_NEW_BOOK_ARRIVED:
                    Log.e(TAG, "receive new book: " + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            mHandler.obtainMessage(AppConstants.MSG_NEW_BOOK_ARRIVED, book).sendToTarget();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarColor(R.color.colorPrimary);
        setSwipeBackEnable(false);
        mHandler = new MyHandler();
    }

    // Bundle
    public void onBundleClick(View view) {

    }

    // 共享文件
    public void onShareFileClick(View view) {

    }

    // Messenger
    public void onMessengerClick(View view) {
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, mMessengerConnection, Context.BIND_AUTO_CREATE);
    }

    // aidl
    public void onAIDLClick(View view) {
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mAidlConnection, Context.BIND_AUTO_CREATE);
    }

    public void onUnregisterNewBookArrivedListenerClick(View view) {
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                mRemoteBookManager.unregisterNewBookArrivedListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSecondActivityClick(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

    @Override
    protected void onDestroy() {
        unbindService(mMessengerConnection);
        unbindService(mAidlConnection);
        super.onDestroy();
    }
}
