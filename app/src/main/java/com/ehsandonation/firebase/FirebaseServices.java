package com.ehsandonation.firebase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ehsandonation.home.MainActivity;
import com.ehsandonation.R;
import com.ehsandonation.model.Donation;
import com.ehsandonation.model.User;
import com.ehsandonation.utils.PreferencesManager;
import com.ehsandonation.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirebaseServices {

    private static final String TAG = "FirebaseServices";
    private FirebaseAuth firebaseAuth;
    private String userID;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private PreferencesManager preferencesManager;
    private FirebaseStorage storage;

    private static FirebaseServices firebaseServicesInstance = null;

    private FirebaseServices() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void setContext(Context context) {
        this.context = context;
        preferencesManager = new PreferencesManager(context);
        if (firebaseAuth.getCurrentUser() != null) {
            userID = firebaseAuth.getCurrentUser().getUid();
        }

    }

    public static FirebaseServices getFirebaseServicesInstance() {

        if (firebaseServicesInstance == null) {
            firebaseServicesInstance = new FirebaseServices();
        }
        return firebaseServicesInstance;
    }


    public void createUser(final User user, String password, final ProgressBar signUpLoading) {

        firebaseAuth.createUserWithEmailAndPassword(user.getEmailAddress(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            userID = firebaseAuth.getCurrentUser().getUid();
                            user.setUserID(userID);
                            addNewUser(user, signUpLoading);
                        } else {
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signUpLoading.setVisibility(View.GONE);
                        Tools.showMessage(context, context.getString(R.string.error_sign_up));
                    }
                });

    }

    public void addDonationToCharity(final Donation donation) {
        Map<String, Object> newDonation = new HashMap<>();
        newDonation.put("userID", donation.getUserID());
        newDonation.put("itemsDonations", donation.getItemsDonations());
        newDonation.put("descriptions", donation.getDescriptions());
        newDonation.put("donationName", donation.getDonationName());
        newDonation.put("charityID", donation.getCharityID());
        newDonation.put("donationImage", donation.getDonationImage());
        newDonation.put("donationPhone", donation.getDonationPhone());
        newDonation.put("donationEmail", donation.getDonationEmail());
        newDonation.put("lat",donation.getLat());
        newDonation.put("lng",donation.getLng());

        DocumentReference documentReference = firebaseFirestore.collection(context.getString(R.string.firebase_charities))
                .document(donation.getCharityID())
                .collection(context.getString(R.string.firebase_donations))
                .document();

        newDonation.put("donationID", documentReference.getId());

        documentReference.set(newDonation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //((AppCompatActivity) context).finish();
                        showRating(donation.getDonationName(),donation.getDonationEmail(),donation.getDonationImage());
                        Tools.showMessage(context, context.getString(R.string.thanks_donations));

                    }
                });
    }

    private void showRating(String name,String email,String imgUrl) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rating , null);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);

        Button btnDone = view.findViewById(R.id.btnDone);

        TextView txtFullName = view.findViewById(R.id.txtFullName);

        TextView txtDepartment = view.findViewById(R.id.txtDepartment);

        CircleImageView img_rating=view.findViewById(R.id.img_rating);

        txtFullName.setText(name);
        txtDepartment.setText(email);
        if (!imgUrl.isEmpty())
            Picasso.get().load(imgUrl).into(img_rating);

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(view);

        alertDialog.show();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) context).finish();
                Tools.showMessage(context,"Thank you for your rate");
            }
        });
    }

    public void addNewUser(User user, final ProgressBar signUpLoading) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("userID", user.getUserID());
        newUser.put("fullName", user.getFullName().trim());
        newUser.put("emailAddress", user.getEmailAddress().trim());
        newUser.put("address", user.getAddress().trim());
        newUser.put("phoneNumber", user.getPhoneNumber().trim());
        newUser.put("profileImageUrl", "empty");
        newUser.put("accountType", "Donation");


        firebaseFirestore.collection(context.getString(R.string.firebase_users)).document(this.userID)
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Tools.showMessage(context, context.getString(R.string.successfully_sign_up));
                        signUpLoading.setVisibility(View.GONE);
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        ((AppCompatActivity) context).finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        signUpLoading.setVisibility(View.GONE);
                        Tools.showMessage(context, context.getString(R.string.error_sign_up));

                    }
                });
    }


    public void login(String email, String password, final ProgressBar loginLoading) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                            ((AppCompatActivity) context).finish();
                            loginLoading.setVisibility(View.GONE);

                        } else {
                            Tools.showMessage(context, context.getString(R.string.authentication_failed));
                            loginLoading.setVisibility(View.GONE);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loginLoading.setVisibility(View.GONE);
                    }
                });
    }

        public void updateProfile(String url, String currentUserID) {

            documentReference = firebaseFirestore.collection(context.getString(R.string.firebase_users))
                    .document(currentUserID);


            documentReference.update("profileImageUrl", url)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Tools.showMessage(context, context.getString(R.string.upload_image));
                            } else {
                                Tools.showMessage(context, context.getString(R.string.try_again));
                            }

                        }
                    });
        }

}
