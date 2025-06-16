package com.example.jawapp.User;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jawapp.Entity.User;
import com.example.jawapp.R;
import com.example.jawapp.databinding.ActivitySignUpBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding root;

    public boolean criPass;
    private String gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        root =ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checker();

        root.btnBack.setOnClickListener(view -> {
           finish();
        });

        root.btnSignUp.setOnClickListener(v ->{
            signup();
        });

        root.radiogroup.setOnCheckedChangeListener(((radioGroup, i) -> {
            gender = "";

            if(root.radioMale.isChecked())
            {
                gender = root.radioMale.getText().toString();
            } else if (root.radioFemale.isChecked()) {
                gender = root.radioFemale.getText().toString();
            }
            else if (root.radioOthers.isChecked()) {
                gender = root.radioOthers.getText().toString();
            }
        }));
    }

    public void signup()
    {

        if(!validate())
        {
            return;
        }

        String firstName = root.edtFirstName.getText().toString();
        String middleName = root.edtMiddleName.getText().toString();

        if (middleName.isEmpty()) {
            if (root.checkboxMiddleName.isChecked()) {
                middleName = "N/A";
                root.lytMiddleName.setError(null);
            } else {

                root.lytMiddleName.setError("Enter middle name or check the checkbox if none.");
                return;
            }
        } else {

            root.lytMiddleName.setError(null);
        }


        String lastName = root.edtLastName.getText().toString();
        String address = root.edtAddress.getText().toString();
        String phoneNum = "+63"+root.edtPhoneNum.getText().toString();
        int age = Integer.parseInt(root.edtAge.getText().toString());
        String username = root.edtUserName.getText().toString();
        String password = root.edtpassword.getText().toString();







        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference();

        String userId = userRef.push().getKey();


        User user = new User(userId,firstName,middleName,lastName,address,phoneNum,age,username,password,gender);

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmation.").setMessage("Create this account with this information?").setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    userRef.child("users").child(userId).setValue(user).addOnSuccessListener(unused -> {
                        Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(SignUpActivity.this, "Creating this account failed. try again.", Toast.LENGTH_SHORT).show();
                    });
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        catch (Exception ex)
        {
            Toast.makeText(SignUpActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validate()
    {
        if(root.edtFirstName.getText().toString().isEmpty())
        {
            root.lytFirstName.setError("Fill up required Fields");
            return false;
        }
        else if (root.edtLastName.getText().toString().isEmpty()) {
            root.lytLastName.setError("Fill up required Fields");
            return false;
        }
        else if (root.edtAge.getText().toString().isEmpty()) {
            root.lytAge.setError("Fill up required Fields");
            return false;
        }
        else if (root.edtPhoneNum.getText().toString().isEmpty()) {
            root.lytPhoneNum.setError("Fill up required Fields");
            return false;
        }

        else if (root.edtAddress.getText().toString().isEmpty()) {
            root.lytAddress.setError("Fill up required Fields");
            return false;
        }
        else if (root.edtUserName.getText().toString().isEmpty()) {
            root.lytUserName.setError("Fill up required Fields");
            return false;
        }

        else if (root.edtpassword.getText().toString().isEmpty()) {
            root.lytpassword.setError("Fill up required Fields");
            return false;
        }

        else if (root.edtConfirmpassword.getText().toString().isEmpty()) {
            root.lytConfirmpassword.setError("Fill up required Fields");
            return false;
        }
        else if (root.radiogroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Choose your gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(root.edtPhoneNum.getText().toString().length() != 10)
        {
            root.lytPhoneNum.setError("Invalid Phone number");
            return false;
        }
        else if(Integer.parseInt(root.edtAge.getText().toString()) < 18)
        {
            root.lytAge.setError("Age is must be 18 or above");
            return false;
        } else if (!root.edtpassword.getText().toString().equals(root.edtConfirmpassword.getText().toString())) {
            root.lytConfirmpassword.setError("Password Does not match.");
            return false;
        } else if (criPass == false) {
            root.lytpassword.setError("Password criteria does not meet.");
            return false;
        }
        return true;
    }

    public void checker()
    {
        root.edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty())
                {
                    root.lytFirstName.setError("Fill up required");
                    root.lytFirstName.setErrorEnabled(true);
                } else {
                    root.lytFirstName.setError("");
                    root.lytFirstName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        root.edtMiddleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                root.lytMiddleName.setError(null);
                root.lytFirstName.setErrorEnabled(false);
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        root.edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty())
                {
                    root.lytLastName.setError("Fill up required");
                    root.lytLastName.setErrorEnabled(true);
                } else {
                    root.lytLastName.setError("");
                    root.lytLastName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        root.edtAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty())
                {
                    root.lytAge.setError("Fill up required");
                    root.lytAge.setErrorEnabled(true);
                } else {
                    root.lytAge.setError("");
                    root.lytAge.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        root.edtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty())
                {
                    root.lytAddress.setError("Fill up required");
                    root.lytAddress.setErrorEnabled(true);
                } else {
                    root.lytAddress.setError("");
                    root.lytAddress.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        root.edtPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty())
                {
                    root.lytPhoneNum.setError("Fill up required");
                    root.lytPhoneNum.setErrorEnabled(true);
                } else {
                    root.lytPhoneNum.setError("");
                    root.lytPhoneNum.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        root.edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty())
                {
                    root.lytUserName.setError("Fill up required");
                    root.lytUserName.setErrorEnabled(true);
                } else {
                    root.lytUserName.setError("");
                    root.lytUserName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        root.edtpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passRequirements();
                if(charSequence.toString().isEmpty())
                {
                    root.lytpassword.setError("Fill up required");
                    root.lytpassword.setErrorEnabled(true);
                } else {
                    root.lytpassword.setError("");
                    root.lytpassword.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        root.edtConfirmpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty())
                {
                    root.lytConfirmpassword.setError("Fill up required");
                    root.lytConfirmpassword.setErrorEnabled(true);
                } else {
                    root.lytConfirmpassword.setError("");
                    root.lytConfirmpassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    public void passRequirements()
    {

        String password = root.edtpassword.getText().toString();
        boolean hasNoSpace = !password.contains(" ");
        boolean hasEight = password.length() >= 8;
        boolean hasUpper = Pattern.matches(".*[A-Z].*",password);
        boolean hasLower = Pattern.matches(".*[a-z].*",password);
        boolean hasNum = Pattern.matches(".*[0-9].*",password);
        boolean hasSymbol = Pattern.matches(".*[\\W].*",password.replace(" ",""));

        setIndicator(root.txtSpace, hasNoSpace);
        setIndicator(root.txtCharacters, hasEight);
        setIndicator(root.txtUppercase, hasUpper);
        setIndicator(root.txtLowercase, hasLower);
        setIndicator(root.txtNumber, hasNum);
        setIndicator(root.txtSymbol, hasSymbol);

        boolean allValid = hasNoSpace && hasEight && hasUpper && hasLower && hasNum && hasSymbol;

        if (!allValid) {
            criPass = false;
        } else {
            criPass = true;
        }
    }

    public void setIndicator(TextView textView,Boolean isValid)
    {

        if(isValid)
        {
            textView.setTextColor(ContextCompat.getColor(this,R.color.green));
        }
        else
        {
            textView.setTextColor(ContextCompat.getColor(this,R.color.red));
        }
    }
}