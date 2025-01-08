package com.example.burdapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.burdapp.Adapter.ExplorerAdapter;
import com.example.burdapp.Domain.ItemDomain;
import com.example.burdapp.Domain.Location;
import com.example.burdapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class ExplorerActivity extends BaseActivity {

    private RecyclerView recyclerViewExplorer;
    private ArrayList<ItemDomain> itemList;
    private ExplorerAdapter explorerAdapter;
    private DatabaseReference itemReference, popularReference;
    private ProgressBar progressBarExplorer;
    private Spinner locationSpinner;
    private ChipNavigationBar chipNavigationBar;

    // Nereden geldiği bilgisini tutmak için bir değişken
    private String previousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        // Intent'ten önceki aktivite bilgisini al
        previousActivity = getIntent().getStringExtra("previousActivity");

        // Bileşenleri tanımla
        initViews();

        // RecyclerView için ayarlar
        setupRecyclerView();

        // Spinner için lokasyonları yükle
        initLocation();

        // Firebase verilerini yükle
        loadCombinedData();

        // Navigation Bar ayarları
        setupNavigationBar();

        // Geri butonu için işlem
        findViewById(R.id.backBtn).setOnClickListener(v -> handleBackButton());
    }

    private void initViews() {
        // Görünüm bileşenlerini tanımla
        locationSpinner = findViewById(R.id.locationSp);
        recyclerViewExplorer = findViewById(R.id.recyclerViewExplorer);
        progressBarExplorer = findViewById(R.id.progressBarExplorer);
        chipNavigationBar = findViewById(R.id.chipNavigationBar);
    }

    private void setupRecyclerView() {
        // RecyclerView ve adapter ayarları
        itemList = new ArrayList<>();
        explorerAdapter = new ExplorerAdapter(itemList);
        recyclerViewExplorer.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExplorer.setAdapter(explorerAdapter);
    }

    private void handleBackButton() {
        // Hangi sayfadan gelindiğine göre geri dön
        if ("MainActivity".equals(previousActivity)) {
            Intent intent = new Intent(ExplorerActivity.this, MainActivity.class);
            startActivity(intent);
        } else if ("FavoritesActivity".equals(previousActivity)) {
            Intent intent = new Intent(ExplorerActivity.this, FavoritesActivity.class);
            startActivity(intent);
        } else if ("UserActivity".equals(previousActivity)) {
            Intent intent = new Intent(ExplorerActivity.this, UserActivity.class);
            startActivity(intent);
        } else {
            // Varsayılan olarak ana ekrana dön
            Intent intent = new Intent(ExplorerActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish(); // Bu aktiviteyi kapat
    }

    private void setupNavigationBar() {
        chipNavigationBar.setItemSelected(R.id.explorer, true);

        chipNavigationBar.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                Intent homeIntent = new Intent(ExplorerActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            } else if (id == R.id.explorer) {
                // Zaten Explorer sayfasındayız
            } else if (id == R.id.bookmark) {
                Intent favoritesIntent = new Intent(ExplorerActivity.this, FavoritesActivity.class);
                favoritesIntent.putExtra("previousActivity", "ExplorerActivity");
                startActivity(favoritesIntent);
                finish();
            } else if (id == R.id.profile) {
                Intent profileIntent = new Intent(ExplorerActivity.this, UserActivity.class);
                profileIntent.putExtra("previousActivity", "ExplorerActivity");
                startActivity(profileIntent);
                finish();
            }
        });
    }

    private void initLocation() {
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("Location");
        ArrayList<Location> locationList = new ArrayList<>();

        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                        Location location = locationSnapshot.getValue(Location.class);
                        if (location != null) {
                            locationList.add(location);
                        }
                    }

                    ArrayAdapter<Location> adapter = new ArrayAdapter<>(
                            ExplorerActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            locationList
                    );
                    locationSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Failed to load locations");
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });
    }

    private void loadCombinedData() {
        progressBarExplorer.setVisibility(View.VISIBLE);

        itemReference = FirebaseDatabase.getInstance().getReference("Item");
        popularReference = FirebaseDatabase.getInstance().getReference("Popular");

        itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ItemDomain item = dataSnapshot.getValue(ItemDomain.class);
                        if (item != null) {
                            itemList.add(item);
                        }
                    }
                }

                popularReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ItemDomain item = dataSnapshot.getValue(ItemDomain.class);
                                if (item != null) {
                                    itemList.add(item);
                                }
                            }
                        }

                        explorerAdapter.notifyDataSetChanged();
                        progressBarExplorer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showToast("Failed to load popular items");
                        progressBarExplorer.setVisibility(View.GONE);
                        Log.e("FirebaseError", "Error: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Failed to load items");
                progressBarExplorer.setVisibility(View.GONE);
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
