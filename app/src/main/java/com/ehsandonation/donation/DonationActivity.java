package com.ehsandonation.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ehsandonation.R;
import com.ehsandonation.firebase.FirebaseServices;
import com.ehsandonation.model.Charity;
import com.ehsandonation.model.Donation;
import com.ehsandonation.model.User;
import com.ehsandonation.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class DonationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DonationActivity";
    private Toolbar toolbar;
    private TextView itemsSelected, charitySelected;
    private EditText shortDescription;
    private ImageButton selectItems, selectCharity;
    private String[] items;
    private ArrayList<String> selectedItems = new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private ArrayList<String> charitiesName;
    private Charity charity;
    private ProgressBar loadCharity;
    private FirebaseServices firebaseServices;
    private User user;
    private Button makeDonation;
    List<Charity> charityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        initWidgets();
        selectList();
        setupToolbar();
        getAuth();
    }

    private void getAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
        } else {
            firebaseFirestore = FirebaseFirestore.getInstance();
            initUser(firebaseUser.getUid());
        }
    }

    private void initUser(String userID) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection(getString(R.string.firebase_users)).document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {

                    user = documentSnapshot.toObject(User.class);
                    Log.e(TAG, "Current data: " + documentSnapshot.getData());
                } else {
                }
            }
        });

    }

    private void getAllCharities() {

        loadCharity.setVisibility(View.VISIBLE);

        charityList = new ArrayList<>();

        firebaseFirestore.collection(getString(R.string.firebase_charities))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                charityList.add(document.toObject(Charity.class));
                            }
                            selectCharites(charityList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadCharity.setVisibility(View.GONE);

            }
        });
    }

    private void selectCharites(final List<Charity> charities) {

        charitiesName = new ArrayList<>();

        for (int i = 0; i < charities.size(); i++) {
            charitiesName.add(charities.get(i).getCharityName());
        }

        String[] stockArr = new String[charitiesName.size()];
        stockArr = charitiesName.toArray(stockArr);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] finalStockArr = stockArr;

        builder.setTitle(getString(R.string.select_charity))
                .setSingleChoiceItems(stockArr, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        updateCategories(finalStockArr[i]);
                        charity = charities.get(i);

                    }
                }).setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                charitiesName.clear();
            }
        });

        try {
            loadCharity.setVisibility(View.GONE);
            builder.create().show();
        } catch (Exception x) {

        }
    }

    private void updateCategories(String charitySelected) {
        this.charitySelected.setText(charitySelected);
    }

    private void setupToolbar() {
        setTitle(getDonateType());
        Tools.setSystemBarColor(this, R.color.blue_grey_900);
        toolbar = findViewById(R.id.donation_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initWidgets() {

        firebaseServices = FirebaseServices.getFirebaseServicesInstance();
        firebaseServices.setContext(DonationActivity.this);

        itemsSelected = findViewById(R.id.items_selected);
        shortDescription = findViewById(R.id.short_description);
        selectItems = findViewById(R.id.select_button);
        selectItems.setOnClickListener(this);
        selectCharity = findViewById(R.id.select_charity);
        selectCharity.setOnClickListener(this);
        charitySelected = findViewById(R.id.charity_selected);

        if (charitiesName == null || charitiesName.size() == 0) {
            charitySelected.setText(getString(R.string.no_charity_selected));
        }

        makeDonation = findViewById(R.id.make_donation);
        makeDonation.setOnClickListener(this);
        loadCharity = findViewById(R.id.load_charity);
        updateTitles();
    }

    private void selectList() {
        String category = getDonateType();

        if (category.equals(getString(R.string.clothes_tag))) {

            items = getResources().getStringArray(R.array.clothes_types);

        } else if (category.equals(getString(R.string.books_tag))) {

            items = getResources().getStringArray(R.array.books_types);

        } else if (category.equals(getString(R.string.furniture_tag))) {

            items = getResources().getStringArray(R.array.furniture_types);

        } else if (category.equals(getString(R.string.medical_tag))) {

            items = getResources().getStringArray(R.array.medical_types);

        }
    }

    private void updateTitles() {

        String total = "";

        if (selectedItems.size() != 0) {
            for (int i = 0; i < selectedItems.size(); i++) {
                total += "-" + selectedItems.get(i) + ".\n";
            }

            itemsSelected.setText(total);
        } else {

            itemsSelected.setText(getString(R.string.no_item_selected));

        }
    }

    private String getDonateType() {
        Intent intent = getIntent();
        return intent.getStringExtra(getString(R.string.donate_type));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.select_button:

                selectItems();

                break;

            case R.id.select_charity:

                getAllCharities();

                break;

            case R.id.make_donation:

                makeDonate();

                break;

        }
    }

    private boolean validateItem() {
        boolean valid = true;

        String item = itemsSelected.getText().toString();
        if (item.isEmpty() || item.equals(getString(R.string.no_item_selected))) {
            Tools.showMessage(getApplicationContext(), getString(R.string.no_item_selected));
            valid = false;
        } else {

        } // end else

        return valid;
    }


    private boolean validateDescription() {
        boolean valid = true;

        String description = shortDescription.getText().toString();
        if (description.isEmpty() || description.length() < 6) {
            Tools.showMessage(getApplicationContext(), getString(R.string.must_fill_description));
            valid = false;
        } else {

        } // end else

        return valid;
    }

    private boolean validateCharity() {
        boolean valid = true;

        String charity = charitySelected.getText().toString();
        if (charity.isEmpty() || charity.equals(getString(R.string.no_charity_selected))) {
            Tools.showMessage(getApplicationContext(), getString(R.string.no_charity_selected));
            valid = false;
        } else {

        } // end else

        return valid;
    }

    private void makeDonate() {
        if (!validateItem() || !validateDescription() || !validateCharity()) {

        } else {

            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.dialog_title_donations) + " " + charity.getCharityName() + " " + getString(R.string.charity));
            dialog.setMessage(itemsSelected.getText().toString() + "\n\n" +
                    charity.getCharityName() + " " + getString(R.string.confirm_donate));
            dialog.setPositiveButton(getString(R.string.donate), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    firebaseServices.addDonationToCharity(generateDonations(charity));

                }
            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();

                }
            });

            dialog.create().show();

        }
    }

    private Donation generateDonations(Charity charity) {
        Donation donation = new Donation();
        donation.setUserID(firebaseAuth.getCurrentUser().getUid());
        donation.setDonationName(user.getFullName());
        donation.setDescriptions(shortDescription.getText().toString());
        donation.setCharityID(charity.getCharityID());
        donation.setItemsDonations(selectedItems);
        donation.setDonationImage(user.getProfileImageUrl());
        donation.setDonationPhone(user.getPhoneNumber());
        donation.setDonationEmail(user.getEmailAddress());

        return donation;
    }


    private void selectItems() {

        selectedItems = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_items))
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                        if (b) {
                            selectedItems.add(items[i]);
                        } else if (selectedItems.contains(items[i])) {
                            selectedItems.remove(items[i]);
                        }

                    }
                })
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateTitles();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        selectedItems.clear();
                        updateTitles();

                    }
                });

        builder.create().show();
    }
}
