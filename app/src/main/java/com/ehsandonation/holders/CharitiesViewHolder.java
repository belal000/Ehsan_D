package com.ehsandonation.holders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ehsandonation.R;

public class CharitiesViewHolder extends RecyclerView.ViewHolder {

    public ImageView charityImage;
    public ProgressBar charity_image_load;
    public TextView charityName;
    public TextView charityAddress;
    public Button contactBtn;


    public CharitiesViewHolder(@NonNull View itemView) {
        super(itemView);

        charityImage = itemView.findViewById(R.id.charity_image);
        charity_image_load = itemView.findViewById(R.id.charity_image_load);
        charityName = itemView.findViewById(R.id.charity_name);
        charityAddress = itemView.findViewById(R.id.charity_address);
        contactBtn = itemView.findViewById(R.id.contact_charity);
    }
}
