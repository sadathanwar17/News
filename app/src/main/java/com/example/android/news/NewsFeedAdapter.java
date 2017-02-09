package com.example.android.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsFeedAdapter extends ArrayAdapter<NewsFeed> {

    public NewsFeedAdapter(Context context, ArrayList<NewsFeed> newsFeed) {
        super(context, 0, newsFeed);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.newsfeed, parent, false);
        }
        NewsFeed newsFeed = getItem(position);
        TextView title = (TextView) convertView.findViewById(R.id.titleTextView);
        title.setText(newsFeed.title);

        TextView contributor = (TextView) convertView.findViewById(R.id.contributorTextView);
        contributor.setText(newsFeed.contributor);

        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnailImageView);
        thumbnail.setImageBitmap(newsFeed.image);

        return convertView;
    }
}
