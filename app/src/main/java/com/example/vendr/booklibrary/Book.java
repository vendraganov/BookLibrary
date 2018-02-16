package com.example.vendr.booklibrary;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ventsislavdraganov on 11/21/17.
 */

public class Book implements Serializable {
    public String openLibrary;
    public String author;
    public String title;

    public String getOpenLibrary(){
        return openLibrary;
    }

    public String getAuthor(){
        return author;
    }
    public String getTitle(){
        return title;
    }
    public String getCoverUrl(){
        return "http://covers.openlibrary.org/b/olid/"+openLibrary+"-M.jpg?default=false";
    }

    public String getLargeCoverUrl(){
        return "http://covers.openlibrary.org/b/olid/"+openLibrary+"-L.jpg?default=false";
    }

    //return a book object after passing a JSON object

    private static Book bookFromJson(JSONObject jsonObject){
        Book book = new Book();
         try{
             if(jsonObject.has("cover_edition_key")){
                 book.openLibrary = jsonObject.getString("cover_edition_key");
             }
             else if(jsonObject.has("edition_key")) {
                 final JSONArray covers = jsonObject.getJSONArray("edition_key");
                 book.openLibrary = covers.getString(0);
             }
             book.title = jsonObject.has("title_suggest")? jsonObject.getString("title_suggest"):"";
             book.author = getAuthor(jsonObject);
             System.out.println("This is the title " + book.title);
             System.out.println("This is the author " +book.author);
             System.out.println("This is the author " +book.openLibrary);
             System.out.println("This is the author " +book.getOpenLibrary());


         }
         catch (JSONException e){
             e.printStackTrace();
         }
         return book;
    }
    // returning a String with all the Authors
    private static String getAuthor(JSONObject jsonObject){
        try{
            final JSONArray authors = jsonObject.getJSONArray("author_name");
            int length = authors.length();
            final String[] authorsNames = new String[length];
            for(int i = 0; i < length; i++){
                authorsNames[i] = authors.getString(i);
            }
            return TextUtils.join(", ", authorsNames);
        }
        catch (JSONException e){
            e.printStackTrace();
            return "";
        }
    }

    public static ArrayList<Book> getBooks(JSONArray jsonArray){
        ArrayList<Book> books = new ArrayList<>(jsonArray.length());
        for(int i =0; i< jsonArray.length();i++){
            JSONObject bookObject = null;
            try{
                bookObject = jsonArray.getJSONObject(i);
            }
            catch (JSONException e){
                e.printStackTrace();
                continue;
            }
            Book book = bookFromJson(bookObject);
            if(book != null) {
                books.add(book);
            }
        }

        return books;
    }
}
