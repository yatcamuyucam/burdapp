package com.example.burdapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.burdapp.Domain.ItemDomain;
import com.example.burdapp.databinding.ActivityTicket2Binding;

public class SavedTicketActivity extends BaseActivity {
    ActivityTicket2Binding binding;
    private ItemDomain object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicket2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        Glide.with(SavedTicketActivity.this)
                .load(object.getPic())
                .into(binding.pic);

        Glide.with(SavedTicketActivity.this)
                .load(object.getTourGuidePic())
                .into(binding.profile);

        binding.backBtn.setOnClickListener(v -> finish());
        binding.titleTxt.setText(object.getTitle());
        binding.durationTxt.setText(object.getDuration());
        binding.tourGuideTxt.setText(object.getDateTour());
        binding.timeTxt.setText(object.getTimeTour());
        binding.tourGuideNameTxt.setText(object.getTourGuideName());

        binding.messageBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + object.getTourGuidePhone()));
            sendIntent.putExtra("sms_body", "Type your message");
            startActivity(sendIntent);
        });

        binding.callBtn.setOnClickListener(v -> {
            String phone = object.getTourGuidePhone();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        });

    }

    private void getIntentExtra() {
        object = (ItemDomain) getIntent().getSerializableExtra("object");
    }
}
