package com.example.vendr.booklibrary;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public static final String BOOK_DETAIL_KEY = "book";
    private ListView listView;
    private BookAdapter bookAdapter;
    private BookClient bookClient;
    private ProgressBar progressBar;
    private ArrayList<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listViewBooks);
        books = new ArrayList<>();
        bookAdapter = new BookAdapter(this, books);
        listView.setAdapter(bookAdapter);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        bookSelected();
    }
    private void bookSelected(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(BOOK_DETAIL_KEY,bookAdapter.getItem(i));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book,menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView =(SearchView)MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                fetchBooks(s);
                //reset SearchView
                searchView.clearFocus();
                searchView.setQuery("",false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                //Change the title of the activity which is present on the bar
                MainActivity.this.setTitle(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id==R.id.action_search || super.onOptionsItemSelected(item);
    }


    private void fetchBooks(String strings){
        progressBar.setVisibility(View.VISIBLE);
        bookClient = new BookClient();
        bookClient.getBooks(strings, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try{
                    progressBar.setVisibility(View.GONE);
                    JSONArray data = null;
                    if(response!=null){
                        data = response.getJSONArray("docs");
                        //parse the Json objects to Book objects
                        books.addAll(Book.getBooks(data));
                        bookAdapter.notifyDataSetChanged();
                        //System.out.println(Book.titleCheck);
                    }

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressBar.setVisibility(View.GONE);

            }
        });

    }
}
