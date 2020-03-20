package com.ehsandonation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ehsandonation.R;
import com.ehsandonation.donation.DonationActivity;
import com.ehsandonation.utils.Tools;

public class MakeDonation extends Fragment implements View.OnClickListener {

    private static final String TAG = "MakeDonation";
    private ImageButton clothesBtn, booksBtn, furnitureBtn, medicalBtn;

    public static MakeDonation newInstance() {
        MakeDonation fragment = new MakeDonation();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.make_donation_fragment, container, false);

        init(view);

        return view;
    }


    private void init(View view) {
        clothesBtn = view.findViewById(R.id.clothes_id);
        clothesBtn.setOnClickListener(this);
        booksBtn = view.findViewById(R.id.books_id);
        booksBtn.setOnClickListener(this);
        furnitureBtn = view.findViewById(R.id.furniture_id);
        furnitureBtn.setOnClickListener(this);
        medicalBtn = view.findViewById(R.id.medical_id);
        medicalBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.clothes_id:

                makeDonation(getString(R.string.clothes_tag));

                break;

            case R.id.books_id:

                makeDonation(getString(R.string.books_tag));

                break;

            case R.id.furniture_id:

                makeDonation(getString(R.string.furniture_tag));

                break;

            case R.id.medical_id:

                makeDonation(getString(R.string.medical_tag));

                break;
        }
    }

    private void makeDonation(String donationTag)
    {
        Intent intent = new Intent(getContext() , DonationActivity.class);
        intent.putExtra(getContext().getString(R.string.donate_type) , donationTag);
        startActivity(intent);
    }

}
