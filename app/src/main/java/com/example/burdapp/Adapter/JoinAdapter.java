package com.example.burdapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.burdapp.Activity.SavedTicketActivity;
import com.example.burdapp.Domain.ItemDomain;
import com.example.burdapp.databinding.ViewholderAttendedBinding;

import java.util.ArrayList;

public class JoinAdapter extends RecyclerView.Adapter<JoinAdapter.Viewholder> {
    ArrayList<ItemDomain> items;
    Context context;
    ViewholderAttendedBinding binding;

    public JoinAdapter(ArrayList<ItemDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public JoinAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=ViewholderAttendedBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        context = parent.getContext();
        return new Viewholder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull JoinAdapter.Viewholder holder, int position) {
        binding.titleTxt.setText(items.get(position).getTitle());
        binding.priceTxt.setText("$" +items.get(position).getPrice());
        binding.addressTxt.setText(items.get(position).getAddress());
        binding.scoreTxt.setText(""+items.get(position).getScore());

        Glide.with(context)
                .load(items.get(position).getPic())
                .into(binding.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SavedTicketActivity.class);
                intent.putExtra("object", items.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        public Viewholder(ViewholderAttendedBinding binding) {
            super(binding.getRoot());
        }
    }
}
