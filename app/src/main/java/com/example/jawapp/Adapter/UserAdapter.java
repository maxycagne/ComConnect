package com.example.jawapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jawapp.Entity.User;
import com.example.jawapp.databinding.CardUserBinding;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;

    private List<User> userList;

    public UserClick userClick;

    public UserAdapter(Context context, List<User> userList, UserClick userClick) {
        this.context = context;
        this.userList = userList;
        this.userClick = userClick;
    }

    public interface UserClick
    {
        void onClick(User user);
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(CardUserBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);

        String fullName = user.getLastName() +", " + user.getFirstName() +" " + user.getMiddleName();

        holder.root.txtName.setText(fullName);
        holder.root.txtAddress.setText(user.getAddress());
        holder.root.txtGender.setText(user.getGender());

        holder.itemView.setOnClickListener(view -> {
            userClick.onClick(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private CardUserBinding root;
        public ViewHolder(@NonNull CardUserBinding root) {
            super(root.getRoot());
            this.root = root;
        }
    }
}
