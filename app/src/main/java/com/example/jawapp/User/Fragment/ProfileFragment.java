package com.example.jawapp.User.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jawapp.Entity.User;
import com.example.jawapp.R;
import com.example.jawapp.User.DashboardActivity;
import com.example.jawapp.User.LoginActivity;
import com.example.jawapp.databinding.DialogChangepassBinding;
import com.example.jawapp.databinding.FragmentProfileBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;


public class ProfileFragment extends Fragment {


    private FragmentProfileBinding root;

    private User currentUser;

    public Boolean criPass;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = FragmentProfileBinding.inflate(inflater, container, false);


        currentUser = ((DashboardActivity) getActivity()).currentUser;

        String fullName = currentUser.getLastName() + ", " + currentUser.getFirstName() + " " + currentUser.getMiddleName();
        root.txtName.setText(fullName);
        root.txtAddress.setText(currentUser.getAddress());
        root.txtContactNum.setText(currentUser.getPhoneNum());
        root.txtGender.setText(currentUser.getGender());
        root.txtAge.setText(String.valueOf(currentUser.getAge()));

        root.txtUsername.setText(currentUser.getUsername());
        root.txtPassword.setText(currentUser.getPassword());

        root.btnChangePassword.setOnClickListener(view -> {
            changePassword();
        });

        root.btnEditDetails.setOnClickListener(view -> {

        });


        root.btnLogout.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Logout").setMessage("Are you sure you want to logout?").setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });

        return root.getRoot();
    }
    public void changePassword() {
        DialogChangepassBinding dialogBinding = DialogChangepassBinding.inflate(getLayoutInflater());

        criPass = false;


        dialogBinding.edtNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validatePasswordRequirements(dialogBinding, s.toString());
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(dialogBinding.getRoot())
                .setTitle("Change Password")
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                String oldPass = dialogBinding.edtOldPass.getText().toString();
                String newPass = dialogBinding.edtNewPass.getText().toString();
                String confirmPass = dialogBinding.edtConfirmPass.getText().toString();

                if (validatePasswordChange(dialogBinding, oldPass, newPass, confirmPass)) {
                    updatePasswordInFirebase(newPass);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void validatePasswordRequirements(DialogChangepassBinding binding, String password) {
        boolean hasNoSpace = !password.contains(" ");
        boolean hasEight = password.length() >= 8;
        boolean hasUpper = Pattern.matches(".*[A-Z].*", password);
        boolean hasLower = Pattern.matches(".*[a-z].*", password);
        boolean hasNum = Pattern.matches(".*[0-9].*", password);
        boolean hasSymbol = Pattern.matches(".*[\\W].*", password.replace(" ", ""));

        setIndicator(binding.txtSpace, hasNoSpace);
        setIndicator(binding.txtCharacters, hasEight);
        setIndicator(binding.txtUppercase, hasUpper);
        setIndicator(binding.txtLowercase, hasLower);
        setIndicator(binding.txtNumber, hasNum);
        setIndicator(binding.txtSymbol, hasSymbol);

        criPass = hasNoSpace && hasEight && hasUpper && hasLower && hasNum && hasSymbol;
    }

    private boolean validatePasswordChange(DialogChangepassBinding binding,
                                           String oldPass,
                                           String newPass,
                                           String confirmPass) {
        boolean isValid = true;

        // Clear previous errors
        binding.edtOldPass.setError(null);
        binding.edtNewPass.setError(null);
        binding.edtConfirmPass.setError(null);

        if (oldPass.isEmpty()) {
            binding.edtOldPass.setError("Please enter your current password");
            isValid = false;
        } else if (!oldPass.equals(currentUser.getPassword())) {
            binding.edtOldPass.setError("Incorrect current password");
            isValid = false;
        }

        if (newPass.isEmpty()) {
            binding.edtNewPass.setError("Please enter a new password");
            isValid = false;
        } else if (!criPass) {
            binding.edtNewPass.setError("Password doesn't meet all requirements");
            isValid = false;
        }

        if (confirmPass.isEmpty()) {
            binding.edtConfirmPass.setError("Please confirm your password");
            isValid = false;
        } else if (!newPass.equals(confirmPass)) {
            binding.edtConfirmPass.setError("Passwords don't match");
            isValid = false;
        }

        return isValid;
    }

    private void updatePasswordInFirebase(String newPassword) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference refChangePass = firebaseDatabase.getReference("users");

        Map<String, Object> updates = new HashMap<>();
        updates.put("password", newPassword);

        refChangePass.child(currentUser.getUserId()).updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    currentUser.setPassword(newPassword);
                    root.txtPassword.setText(newPassword);
                    Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void setIndicator(TextView textView,Boolean isValid)
    {

        if(isValid)
        {
            textView.setTextColor(ContextCompat.getColor(requireContext(),R.color.green));
        }
        else
        {
            textView.setTextColor(ContextCompat.getColor(requireContext(),R.color.red));
        }
    }
}