package com.demo.ipcdemo.aidl;

import com.demo.ipcdemo.aidl.Book;
import com.demo.ipcdemo.aidl.IOnNewBookArrivedListener;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerNewBookArrivedListener(IOnNewBookArrivedListener listener);
    void unregisterNewBookArrivedListener(IOnNewBookArrivedListener listener);
}