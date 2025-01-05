package com.example.burdapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.burdapp.Adapter.JoinAdapter;
import com.example.burdapp.Adapter.PopularAdapter;
import com.example.burdapp.Domain.ItemDomain;
import com.example.burdapp.Domain.User;
import com.example.burdapp.R;
import com.example.burdapp.databinding.ActivityUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class UserActivity extends BaseActivity {
    ActivityUserBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            loadUserInfo(currentUser);
            loadUserProfileData(currentUser);
            setupNavigationBar();
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }

    }

    public void loadUserInfo(FirebaseUser user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    User userName = snapshot.getValue(User.class);
                    assert userName != null;
                    String fullName = userName.getName();
                    binding.emailTxt.setText(user.getEmail());
                    binding.nameTxt.setText(fullName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error: " + error.getMessage());
                }
            });
        } else {
            Log.e("FirebaseError", "Current user is null");
        }
    }

    private void loadUserProfileData(FirebaseUser user) {
        DatabaseReference userTicketsReference = FirebaseDatabase.getInstance().getReference("userTickets").child(user.getUid());
        ArrayList<ItemDomain> list = new ArrayList<>();

        userTicketsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(ItemDomain.class));
                    }
                    if (!list.isEmpty()) {
                        binding.recyclerViewRecommended.setLayoutManager(new LinearLayoutManager(UserActivity.this, LinearLayoutManager.VERTICAL, false));
                        RecyclerView.Adapter<JoinAdapter.Viewholder> adapter = new JoinAdapter(list);
                        binding.recyclerViewRecommended.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setupNavigationBar() {
        ChipNavigationBar chipNavigationBar = findViewById(R.id.chipNavigationBar);

        chipNavigationBar.setItemSelected(R.id.profile, true);

        chipNavigationBar.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                Intent homeIntent = new Intent(UserActivity.this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish();
            } else if (id == R.id.explorer) {
                Intent intent = new Intent(UserActivity.this, ExplorerActivity.class);
                startActivity(intent);
                finish();
            }
            else if (id == R.id.bookmark) {
                Intent intent = new Intent(UserActivity.this, FavoritesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onImageClick(View view) {
        ImageView photoImageView = findViewById(R.id.photoImageView);
        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
