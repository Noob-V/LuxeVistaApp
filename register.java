package com.example.resort;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        EditText etFirstName = findViewById(R.id.firstNameField);
        EditText etLastName = findViewById(R.id.lastNameField);
        EditText etEmail = findViewById(R.id.emailField);
        EditText etPassword = findViewById(R.id.passwordField);
        Button btnRegister = findViewById(R.id.registerButton);


        DatabaseHelper dbHelper = new DatabaseHelper(this);

        btnRegister.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();


            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }


            String hashedPassword = Utils.hashPassword(password);


            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("email", email);
            values.put("password_hash", hashedPassword);
            values.put("first_name", firstName);
            values.put("last_name", lastName);

            long result = db.insert("Users", null, values);

            if (result != -1) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(register.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration failed! Email might already be registered.", Toast.LENGTH_SHORT).show();
            }

            db.close();
        });
    }
}
