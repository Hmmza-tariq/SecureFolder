package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static boolean newAccount = false;
    EditText passwordEditText;
    Button loginButton;
    Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        newAccount = false;
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        createButton = findViewById(R.id.create_button);
        SharedPreferences settings = getSharedPreferences("passwordAccount", 0);
        if (settings.getBoolean("one", true)) {
            Toast.makeText(LoginActivity.this, "Welcome to Secure Folder", Toast.LENGTH_SHORT).show();
            loginButton.setText("Create");
            createButton.setVisibility(View.GONE);
            newAccount = true;
            settings.edit().putBoolean("one", false).apply();
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordEditText.getText().toString();
                PasswordManager pm = new PasswordManager(LoginActivity.this);
                if (!newAccount) {
                    if (Objects.equals(pm.getPassword(), password)) {
                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, GalleryActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    pm.setPassword(password);
                    Toast.makeText(LoginActivity.this, "Account created: " + password, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, GalleryActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Warning")
                        .setMessage("By creating a new account all the existing files will be deleted!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                passwordEditText.setText("");
                                Toast.makeText(LoginActivity.this, "Please enter new account credentials", Toast.LENGTH_SHORT).show();
                                loginButton.setText("Create");
                                createButton.setVisibility(View.GONE);
                                newAccount = true;
                                deleteAllFilesFromAppStorage();
                            }})
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
    }
    public void deleteAllFilesFromAppStorage() {
        String targetStr = getExternalFilesDir(null).getAbsolutePath();
        targetStr = targetStr.substring(0, targetStr.lastIndexOf("/")) + File.separator;

        File directory = new File(targetStr);
        File[] files = directory.listFiles();
        System.out.println(targetStr);
        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
                file.delete();
            }
        }
    }

}
