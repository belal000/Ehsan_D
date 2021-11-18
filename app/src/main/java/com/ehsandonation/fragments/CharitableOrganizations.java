package com.ehsandonation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ehsandonation.R;
import com.ehsandonation.adapters.CharitiesAdapter;
import com.ehsandonation.model.Charity;
import com.ehsandonation.utils.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CharitableOrganizations extends Fragment {

    private static final String TAG = "CharitableOrganizations";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private List<Charity> charityList;
    private ProgressBar loadCharites;
    private RecyclerView recyclerViewCharites;
    private Context context;
    private CharitiesAdapter charitiesAdapter;



    public static CharitableOrganizations newInstance() {
        CharitableOrganizations fragment = new CharitableOrganizations();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.charitable_organizations_fragment, container, false);

        this.context = container.getContext();
        initWidgets(view);
        getAuth();


        return view;

    }

    private void initWidgets(View view) {
        Tools.initImageLoader(getContext());
        recyclerViewCharites = view.findViewById(R.id.recyclerViewCharites);
        loadCharites = view.findViewById(R.id.load_charities);
    }


    private void getAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
        } else {
            firebaseFirestore = FirebaseFirestore.getInstance();
            loadCharites.setVisibility(View.VISIBLE);
            getCharities();

        }
    }

    private void getCharities() {

        charityList = new ArrayList<>();
        Query query = FirebaseFirestore.getInstance().collection(getString(R.string.firebase_charities)) .whereEqualTo("accountType" ,"Charity");


               query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                         for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){
                             if(documentChange.getType() == DocumentChange.Type.ADDED){


                                 Charity charity = documentChange.getDocument().toObject(Charity.class);

                                 charityList.add(charity);
                                 loadCharites.setVisibility(View.GONE);
                                 setupRecyclerViewShops(charityList);
                                 charitiesAdapter.notifyDataSetChanged();
                             }
                         }
//
                    }
                });
    }

    private void setupRecyclerViewShops(List<Charity> storeList) {

        charitiesAdapter = new CharitiesAdapter(getContext(), storeList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewCharites.setLayoutManager(mLayoutManager);
        recyclerViewCharites.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCharites.setAdapter(charitiesAdapter);

    }

}
