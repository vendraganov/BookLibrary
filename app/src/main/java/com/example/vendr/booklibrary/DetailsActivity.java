package com.example.vendr.booklibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by vendr on 11/16/2017.
 */

public class DetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView displayTitleTextView;
    private TextView displayAuhtorTextView;
    private TextView publisher;
    private TextView pageCount;
    private Book book;
    private BookClient bookClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        imageView = (ImageView)findViewById(R.id.imageView);
        displayTitleTextView = (TextView)findViewById(R.id.displayTitleTextView);
        displayAuhtorTextView =(TextView)findViewById(R.id.displayAuthorTextView);
        publisher = (TextView)findViewById(R.id.publisher);
        pageCount = (TextView)findViewById(R.id.pageCount);
        //getting the Book object
        book = (Book)getIntent().getSerializableExtra(MainActivity.BOOK_DETAIL_KEY);
        loadBook();
    }

    private void loadBook(){
        //changing the activity title
        this.setTitle(book.getTitle());
        displayTitleTextView.setText(book.getTitle());
        displayAuhtorTextView.setText(book.getAuthor());
        Picasso.with(this).load(Uri.parse(book.getLargeCoverUrl())).error(R.drawable.nocover).into(imageView);
        //getting the extra information about the book
        //-------------------------------------------------------
        //I AM NOT USING HERE THE BOOKCLIENT CLASS. I AM CREATING A HANDLER AND HTTPCLIENT HERE
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try{
                    //if we have publishers
                    if(response.has("publishers")){
                        JSONArray publishers = response.getJSONArray("publishers");
                        int numberOfPublishers = publishers.length();
                        String[] publishersNames = new String[numberOfPublishers];
                        for(int i = 0; i<numberOfPublishers; i++){
                            publishersNames[i] = publishers.getString(i);
                        }
                        publisher.setText(TextUtils.join(", ", publishersNames));

                    }
                    //if we can get the book page count
                    if(response.has("number_of_pages")){
                        String numberOfPages = String.valueOf(response.getInt("number_of_pages"))+ " pages";
                        pageCount.setText(numberOfPages);
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://openlibrary.org/books/" + book.getOpenLibrary() + ".json";
        client.get(url, handler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.action_share){
            shareIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareIntent(){
        Uri imageUri = getImageUri();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TEXT, book.getTitle());
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(intent, "Share Image"));

    }

    private Uri getImageUri(){
        //first we need to get the bitmap and then share it
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap = null;
        if(drawable instanceof BitmapDrawable){
            bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        }
        Uri imageUri = null;
        try{
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"share_image_" + System.currentTimeMillis() + ".png");
            //Creates the directory named by this abstract pathname, including any necessary but nonexistent parent directories.
            file.getParentFile().mkdirs();
            //out - i am sending the file to be written on the external storage
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.close();//file is written
            imageUri = Uri.fromFile(file);

        }
        catch (IOException e){
            e.printStackTrace();
        }

        return imageUri;
    }
}
