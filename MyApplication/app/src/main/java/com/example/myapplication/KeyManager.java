package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyManager {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public KeyManager(Context context) {
        pref = context.getSharedPreferences("SecureKey", Context.MODE_PRIVATE);
        editor = pref.edit();
    }


    public byte[] getKey() {
        String encodedKey = pref.getString("key", null);
        return Base64.decode(encodedKey, Base64.DEFAULT);
    }

    public void setKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        byte[] key = secretKey.getEncoded();
        String encodedKey = Base64.encodeToString(key, Base64.DEFAULT);
        editor.putString("key", encodedKey).apply();
    }
}
