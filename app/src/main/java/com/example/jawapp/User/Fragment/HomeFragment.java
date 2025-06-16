package com.example.jawapp.User.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jawapp.Adapter.UserAdapter;
import com.example.jawapp.Entity.User;
import com.example.jawapp.R;
import com.example.jawapp.User.ChatActivity;
import com.example.jawapp.User.DashboardActivity;
import com.example.jawapp.databinding.FragmentHomeBinding;
import com.example.jawapp.databinding.FragmentProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding root;
    public HomeFragment() {
        // Required empty public constructor
    }

    public User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = FragmentHomeBinding.inflate(inflater,container,false);
        currentUser = ((DashboardActivity) getActivity()).currentUser;
        retrieve(root.edtSearch.getText().toString());

        root.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                retrieve(root.edtSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return root.getRoot();
    }

    public void retrieve(String lastName)
    {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference retrieveRef = firebaseDatabase.getReference("users");
            Query query;

            if(lastName.isEmpty())
            {
                query = retrieveRef;
            }
            else
            {
                query = retrieveRef.orderByChild("lastName").equalTo(lastName);
            }

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<User> userList = new ArrayList<>();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null && !user.getUserId().equals(currentUser.getUserId())) {
                            userList.add(user);
                        }
                    }

                    userList.sort((user, t1) -> user.getLastName().compareToIgnoreCase(t1.getLastName()));

                    UserAdapter adapter = new UserAdapter(getContext(), userList, new UserAdapter.UserClick() {
                        @Override
                        public void onClick(User user) {
                             startActivity(new Intent(getContext(), ChatActivity.class)
                                     .putExtra("user",user));
                        }
                    });
                    root.rvUsers.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        retrieve(root.edtSearch.getText().toString());
    }
}