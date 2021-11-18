package com.ehsandonation.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ehsandonation.utils.MapsActivity;
import com.ehsandonation.R;
import com.ehsandonation.holders.CharitiesViewHolder;
import com.ehsandonation.model.Charity;
import com.ehsandonation.utils.Const;
import com.ehsandonation.utils.Tools;
import com.ehsandonation.utils.UniversalImageLoader;

import java.util.List;

public class CharitiesAdapter extends RecyclerView.Adapter<CharitiesViewHolder> {

    private Context context;
    private List<Charity> charityList;

    public CharitiesAdapter(Context context, List<Charity> charityList) {
        this.context = context;
        this.charityList = charityList;
    }

    @Override
    public CharitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.charity_item, parent, false);

        return new CharitiesViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(CharitiesViewHolder holder, final int position) {
        final Charity charity = charityList.get(position);

        holder.charityName.setText(charity.getCharityName());
        holder.charityAddress.setText(charity.getCharityAddress());

        UniversalImageLoader.setImage(charity.getChairtyImage(), holder.charityImage, holder.charity_image_load, "");

        holder.contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCustomDialog(charity);

            }
        });


    }

    @Override
    public int getItemCount() {
        return charityList.size();
    }


    private void showCustomDialog(final Charity charity) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.charity_dialog_name)).setText(charity.getCharityName());
        ((TextView) dialog.findViewById(R.id.charity_dialog_address)).setText(charity.getCharityAddress());

        ImageView imageView = dialog.findViewById(R.id.charity_dialog_image);

        UniversalImageLoader.setImage(charity.getChairtyImage(), imageView, null, "");

        dialog.findViewById(R.id.dialog_loc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                getLocation(charity);
            }
        });

        dialog.findViewById(R.id.dialog_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                permissionCheckCall(charity);
            }
        });

        dialog.findViewById(R.id.dialog_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                sendEmail(charity);

            }
        });

        dialog.findViewById(R.id.dialog_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                shareCharity(charity);

            }
        });

        dialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void getLocation(Charity charity) {
        Intent intent=new Intent(context, MapsActivity.class);
        intent.putExtra("lat" ,charity.getLat());

        intent.putExtra("lng" ,charity.getLng());

        context.startActivity(intent);
    }


    private void shareCharity(Charity charity) {
        String data = context.getString(R.string.charity_name) + " : " + charity.getCharityName() + ".\n\n" +
                context.getString(R.string.charity_email) + " : " + charity.getCharityEmail() + ".\n\n" +
                context.getString(R.string.charity_phone) + " : " + charity.getCharityPhoneNumber() + ".\n\n" +
                context.getString(R.string.charity_address) + " : " + charity.getCharityAddress() + ".\n\n" +
                context.getString(R.string.charity_image) + " : " + charity.getChairtyImage() + ".\n\n";

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT , data);
        intent.setType("text/plain");
        context.startActivity(intent);
    }

    private void sendEmail(Charity charity) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{charity.getCharityEmail()});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.communicate_with) + " " + charity.getCharityName() + ".");
        intent.setPackage("com.google.android.gm");
        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivity(intent);
        else
            Toast.makeText(context, R.string.no_gmail, Toast.LENGTH_SHORT).show();
    }

    private void permissionCheckCall(Charity charity) {

        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            makeCall(charity);
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, Const.MY_PERMISIONS_REQUEST_MAKE_CALL);
        }

    }

    private void makeCall(Charity charity) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        String phone_number = String.valueOf(charity.getCharityPhoneNumber());

        if (phone_number.trim().isEmpty()) {
            Tools.showMessage(context, context.getString(R.string.cannot_call));
        } else {
            intent.setData(Uri.parse("tel:" + phone_number));
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissionCheckCall(charity);
        } else {
            context.startActivity(intent);
        }
    }
}
