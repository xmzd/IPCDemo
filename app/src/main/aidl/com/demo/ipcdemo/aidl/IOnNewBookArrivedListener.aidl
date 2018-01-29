// IOnNewBookArrivedListener.aidl
package com.demo.ipcdemo.aidl;

import com.demo.ipcdemo.aidl.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book book);
}
