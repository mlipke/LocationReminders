package com.admuc.locationreminders.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.admuc.locationreminders.R;
import com.admuc.locationreminders.models.GooglePlace;
import com.admuc.locationreminders.utils.MapHelper;

import java.util.List;

/**
 * Created by 4gray on 22.01.16.
 */
public class GooglePlacesListViewAdapter extends ArrayAdapter<GooglePlace> {

    Context context;

    public GooglePlacesListViewAdapter(Context context, int resourceId,
                                       List<GooglePlace> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        GooglePlace googlePlace = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.googleplace_list_item_view, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtDesc.setText(MapHelper.convertKmToMeter(googlePlace.getDistance()) + " m | " + googlePlace.getOpenNow());
        holder.txtTitle.setText(googlePlace.getName());
        holder.imageView.setImageResource(R.drawable.ic_location_on_24dp);  // TODO: location type icon from URL

        return convertView;
    }
}