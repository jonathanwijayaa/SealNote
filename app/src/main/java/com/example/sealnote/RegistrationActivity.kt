import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set click listeners
        signUpButton.setOnClickListener { registerUser() }
        logInText.setOnClickListener { navigateToLogin() }
    }

    /**
     * Validates user inputs and attempts to create a new user with email and password
     */
    private fun registerUser() {
        // Get input values
        val fullName = fullNameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        // Validate inputs
        if (!validateInputs(fullName, email, password, confirmPassword)) {
            return
        }

        // Show loading state
        showLoading(true)

        // Create user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    // Sign in success
                    val user = auth.currentUser
                    updateUI(user)
                    Toast.makeText(
                        baseContext, "Registration successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // If sign in fails
                    Toast.makeText(
                        baseContext, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    /**
     * Validates all input fields
     */
    private fun validateInputs(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        if (fullName.isEmpty()) {
            fullNameInput.error = "Full name is required"
            isValid = false
        }

        if (email.isEmpty()) {
            emailInput.error = "Email is required"
            isValid = false
        }

        if (password.isEmpty() || password.length < 6) {
            passwordInput.error = "Password must be at least 6 characters"
            isValid = false
        }

        if (confirmPassword.isEmpty() || confirmPassword != password) {
            confirmPasswordInput.error = "Passwords do not match"
            isValid = false
        }

        return isValid
    }

    /**
     * Updates UI based on authentication status
     */
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Navigate to the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close registration activity
        }
    }

    /**
     * Navigates to login activity
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close registration activity
    }

    /**
     * Shows or hides loading indicator
     */
    private fun showLoading(isLoading: Boolean) {
        signUpButton.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}