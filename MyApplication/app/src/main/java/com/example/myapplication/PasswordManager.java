package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class PasswordManager {

        private final SharedPreferences pref;
        private final SharedPreferences.Editor editor;

        public PasswordManager(Context context) {
            pref = context.getSharedPreferences("SecurePass", Context.MODE_PRIVATE);
            editor = pref.edit();
        }


    public String getPassword() {
            String password = pref.getString("password", null);
            int len = password.length();
            len /= 2;
            StringBuilder b1 = new StringBuilder(password.substring(0, len));
            StringBuilder b2 = new StringBuilder(password.substring(len));
            password = b1.reverse().toString() + b2.reverse();
            return password;
        }

        public void setPassword(String password) {
            int len = password.length();
            len /= 2;
            StringBuilder b1 = new StringBuilder(password.substring(0, len));
            StringBuilder b2 = new StringBuilder(password.substring(len));
            b1.reverse();
            b2.reverse();
            password = b1.toString() + b2;

            editor.putString("password", password);
            editor.apply();
        }
    }
