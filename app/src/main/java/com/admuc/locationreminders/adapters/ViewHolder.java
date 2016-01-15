package com.admuc.locationreminders.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.admuc.locationreminders.R;

/**
 * Created by matt on 15/01/16.
 */
public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public long id;
    public String typeName;

    public TextView title;
    public TextView locationString;

    public ImageView type;
    public ImageView locationIcon;
    public ViewHolderClickListener clickListener;
    public ImageView circleIcon;

    public ViewHolder(View itemView, ViewHolderClickListener clickListener) {
        super(itemView);
        this.clickListener = clickListener;
        this.title = (TextView) itemView.findViewById(R.id.title);
        this.locationString = (TextView) itemView.findViewById(R.id.locationString);
        this.type = (ImageView) itemView.findViewById(R.id.type);
        this.locationIcon = (ImageView) itemView.findViewById(R.id.locationIcon);
        this.circleIcon = (ImageView) itemView.findViewById(R.id.circle);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        clickListener.onReminder(this.id, this.typeName);
    }
}
