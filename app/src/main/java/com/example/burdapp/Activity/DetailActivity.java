package com.example.burdapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.burdapp.Domain.ItemDomain;
import com.example.burdapp.R;
import com.example.burdapp.databinding.ActivityDetailBinding;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private ItemDomain object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$" + object.getPrice());
        binding.backBtn.setOnClickListener(v -> finish());
        binding.bedTxt.setText("" + object.getBed());
        binding.durationTxt.setText(object.getDuration());
        binding.distanceTxt.setText(object.getDistance());
        binding.descriptionTxt.setText(object.getDescription());
        binding.addressTxt.setText(object.getAddress());
        binding.ratingTxt.setText(object.getScore() + " Rating");
        binding.ratingBar.setRating((float) object.getScore());

        Glide.with(DetailActivity.this)
                .load(object.getPic())
                .into(binding.pic);

        binding.addToCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(DetailActivity.this, );
            }
        });
    }

    private void getIntentExtra() {
        object = (ItemDomain) getIntent().getSerializableExtra("object");
    }
}
