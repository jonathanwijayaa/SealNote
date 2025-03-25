package com.example.sealnote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import kotlinx.android.synthetic.main.activity_registration.*;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Set click listeners
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        logInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    /**
     * Validates user inputs and attempts to create a new user with email and password
     */
    private void registerUser() {
        // Get input values
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(fullName, email, password, confirmPassword)) {
            return;
        }

        // Show loading state
        showLoading(true);

        // Create user with email and password
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful) {
                        // Sign in success
                        FirebaseUser user = auth.getCurrentUser();
                        updateUI(user);
                        Toast.makeText(
                                getBaseContext(), "Registration successful!",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        // If sign in fails
                        Toast.makeText(
                                getBaseContext(), "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                        updateUI(null);
                    }
                });
    }

    /**
     * Validates all input fields
     */
    private boolean validateInputs(
            String fullName,
            String email,
            String password,
            String confirmPassword
    ) {
        boolean isValid = true;

        if (fullName.isEmpty()) {
            fullNameInput.setError("Full name is required");
            isValid = false;
        }

        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            isValid = false;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
            confirmPasswordInput.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Updates UI based on authentication status
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Navigate to the main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Close registration activity
        }
    }

    /**
     * Navigates to login activity
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close registration activity
    }

    /**
     * Shows or hides loading indicator
     */
    private void showLoading(boolean isLoading) {
        signUpButton.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}