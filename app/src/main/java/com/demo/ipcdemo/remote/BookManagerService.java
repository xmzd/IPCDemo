package com.demo.ipcdemo.remote;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.demo.ipcdemo.aidl.Book;
import com.demo.ipcdemo.aidl.IBookManager;
import com.demo.ipcdemo.aidl.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Date    19/09/2017
 * Author  WestWang
 */

public class BookManagerService extends Service {

    private static final String TAG = BookManagerService.class.getSimpleName();

    private AtomicBoolean mIsDestroyed = new AtomicBoolean(false);
    // 支持并发读写
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<IOnNewBookArrivedListener>();
    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerNewBookArrivedListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
            int size = mListenerList.beginBroadcast();
            Log.e(TAG, "registerNewBookArrivedListener, size: " + size);
            mListenerList.finishBroadcast();
        }

        @Override
        public void unregisterNewBookArrivedListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
            int size = mListenerList.beginBroadcast();
            Log.e(TAG, "unregisterNewBookArrivedListener, size: " + size);
            mListenerList.finishBroadcast();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "iOS"));
        new Thread(new ServiceWorker()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i ++) {
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            if (listener != null) {
                listener.onNewBookArrived(book);
            }
        }
        mListenerList.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while (!mIsDestroyed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book book = new Book(bookId, "new book#" + bookId);
                try {
                    onNewBookArrived(book);
                } catch (RemoteException e) {
                    Log.e(TAG, "error: add new book on Service failed!");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        mIsDestroyed.set(true);
        super.onDestroy();
    }
}
