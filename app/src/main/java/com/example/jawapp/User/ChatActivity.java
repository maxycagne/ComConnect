package com.example.jawapp.User;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.jawapp.Adapter.ChatAdapter;
import com.example.jawapp.Entity.ChatMessage;
import com.example.jawapp.Entity.User;
import com.example.jawapp.R;
import com.example.jawapp.databinding.ActivityChatBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        root = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        User messageUser = (User) getIntent().getSerializableExtra("user");

        DashboardActivity dashboardActivity = new DashboardActivity();
        User currenUser = dashboardActivity.currentUser;


        root.txtName.setText(messageUser.getFirstName());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference chatRef = database.getReference("chats");

        List<ChatMessage> messageList = new ArrayList<>();

        ChatAdapter adapter = new ChatAdapter(messageList, messageUser.getUserId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        root.chatRecyclerView.setLayoutManager(layoutManager);

        root.chatRecyclerView.setAdapter(adapter);

        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage message = snapshot.getValue(ChatMessage.class);
                messageList.add(message);
                adapter.notifyItemInserted(messageList.size() - 1);
                root.chatRecyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        root.sendButton.setOnClickListener(view -> {
            String messageText = root.messageEditText.getText().toString().trim();
            if (!messageText.isEmpty()) {
                ChatMessage chatMessage = new ChatMessage(messageUser.getUserId(), messageText);
                chatRef.push().setValue(chatMessage);
                root.messageEditText.setText("");
            }
        });

        root.btnBack.setOnClickListener(view -> {
            finish();
        });

    }
}