package com.example.vendr.booklibrary;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ventsislavdraganov on 11/21/17.
 */

public class BookClient {
    private static final String API_BASE_URL = "http://openlibrary.org/search.json?q=";
    private AsyncHttpClient client;

    public BookClient(){
        this.client = new AsyncHttpClient();
    }


    public void getBooks(final String query, JsonHttpResponseHandler handler) {
        try {
            client.get(API_BASE_URL + URLEncoder.encode(query, "utf-8"), handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
// here we are using the book id = openLibrary
    public void extraBookDetails( String openLibrary, JsonHttpResponseHandler handler){
        String url = "http://openlibrary.org/books/" + openLibrary + ".json";
        client.get(url, handler);
    }


}
