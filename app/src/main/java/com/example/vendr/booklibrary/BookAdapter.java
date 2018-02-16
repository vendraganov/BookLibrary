package com.example.vendr.booklibrary;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ventsislavdraganov on 11/21/17.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private class ViewHolder{
        public ImageView bookCoverImageView;
        public TextView bookTitleTextView;
        public TextView bookAuthorTextView;
    }

    public BookAdapter(Context context, ArrayList<Book> book){
        super(context,0, book);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final Book book = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_book, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.bookCoverImageView = (ImageView)convertView.findViewById(R.id.bookCoverImageView);
            viewHolder.bookTitleTextView = (TextView)convertView.findViewById(R.id.bookTitle);
            viewHolder.bookAuthorTextView = (TextView)convertView.findViewById(R.id.bookAuthor);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.bookAuthorTextView.setText(book.getAuthor());
        viewHolder.bookTitleTextView.setText(book.getTitle());
        //viewHolder.bookCoverImageView.setImageResource(R.drawable.nocover);
        Picasso.with(getContext()).load(Uri.parse(book.getCoverUrl())).error(R.drawable.nocover).into(viewHolder.bookCoverImageView);
        return convertView;
    }
}
