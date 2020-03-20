package com.ehsandonation.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ehsandonation.R;
import com.ehsandonation.about_us.AboutUsActivity;
import com.ehsandonation.contact_us.ContactActivity;
import com.ehsandonation.firebase.FirebaseServices;
import com.ehsandonation.fragments.CharitableOrganizations;
import com.ehsandonation.fragments.MakeDonation;
import com.ehsandonation.login.LoginActivity;
import com.ehsandonation.model.User;
import com.ehsandonation.utils.Const;
import com.ehsandonation.utils.SectionsPagerAdapter;
import com.ehsandonation.utils.Tools;
import com.ehsandonation.utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // double click to exit from app.
    boolean doubleBackToExitPressedOnce = false;

    private static final String TAG = "MainActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private User user;
    private FirebaseServices firebaseServices;

    // toolbar , show in the top page.
    private Toolbar toolbar;

    // Drawer Layout
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ViewPager view_pager;
    private TabLayout tab_layout;

    private TextView nameInHeader;
    private TextView emailInHeader;
    private ImageView profile_picture_header;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();
        setupToolbar();
        initComponent();
        setupDrawerLayout();
        initFirebase();
        checkCurrentUser();
    }

    private void initWidgets() {
        Tools.setSystemBarColor(this, R.color.blue_grey_900);
        Tools.initImageLoader(MainActivity.this);
        setTitle(getString(R.string.make_donation));
    }

    private void checkCurrentUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            backToLogin();
        } else {
            initUser(firebaseAuth.getCurrentUser().getUid());
        }
    }

    private void initFirebase() {
        firebaseServices = FirebaseServices.getFirebaseServicesInstance();
        firebaseServices.setContext(MainActivity.this);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initComponent() {
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(view_pager);

        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);
        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:

                        view_pager.setCurrentItem(0);
                        toolbar.setTitle(getString(R.string.make_donation));

                        break;

                    case 1:

                        view_pager.setCurrentItem(1);
                        toolbar.setTitle(getString(R.string.charitable_organizations));

                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MakeDonation.newInstance(), getString(R.string.make_donation));
        adapter.addFragment(CharitableOrganizations.newInstance(), getString(R.string.charitable_organizations));
        viewPager.setAdapter(adapter);
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
                    updateUI(user);
                    Log.e(TAG, "Current data: " + documentSnapshot.getData());
                } else {
                    if (user == null) {
                        Tools.showMessage(getApplicationContext(), getString(R.string.you_are_not_donator));
                        signOutMethod();
                    }
                }
            }
        });

    }

    private void updateUI(User user) {

        Log.e(TAG, "updateUI: " + user.toString());

        View headerView = navigationView.getHeaderView(0);
        nameInHeader = headerView.findViewById(R.id.nameInHeader);
        emailInHeader = headerView.findViewById(R.id.emailInHeader);
        nameInHeader.setText(user.getFullName());
        emailInHeader.setText(user.getEmailAddress());
        profile_picture_header = headerView.findViewById(R.id.profile_picture_header);

        UniversalImageLoader.setImage(user.getProfileImageUrl(), profile_picture_header, null, "");
    }

    private void setupDrawerLayout() {

        // DrawerLayout in activity_main.xml
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }
        };
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void backToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.home) {
            this.drawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.set_profile_picture) {
            chooseImage();
        } else if (id == R.id.setting) {
            this.drawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.contact_us) {
            goToContactUs();
            this.drawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.about) {
            goToAboutUs();
            this.drawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.signout) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
            signOutMethod();
        }

        return true;
    }


    private void goToContactUs() {
        Intent intent = new Intent(MainActivity.this, ContactActivity.class);
        startActivity(intent);
    }

    private void goToAboutUs() {
        Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
        startActivity(intent);
    }

    private void signOutMethod() {
        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void exitFromApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Tools.showMessage(MainActivity.this, getString(R.string.confrim_exit));

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Const.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_picture_header.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.uploading));
            progressDialog.show();

            final StorageReference ref = storageReference.child(Const.DONATORS_IMAGES + firebaseAuth.getCurrentUser().getUid());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();

                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Uri downloadUrl = uri;
                            String fileUrl = downloadUrl.toString();

                            Log.e(TAG, "onSuccess: " + fileUrl);
                            final String userID = firebaseAuth.getCurrentUser().getUid();

                            firebaseServices.updateProfile(fileUrl, userID);
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Tools.showMessage(MainActivity.this, getString(R.string.failed) + " " + e.getMessage());
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }
}
