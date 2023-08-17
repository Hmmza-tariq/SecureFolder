package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Uri> selectedImages;
    private ActivityResultLauncher<Intent> selectImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Button addImagesButton = findViewById(R.id.save_image_button);
        recyclerView = findViewById(R.id.idRVImages);
        selectedImages = new ArrayList<>();
        addImagesButton.setText("Add Images");
        addImagesButton.setOnClickListener(view -> selectImage());

        setupRecyclerView();
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        selectImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    moveData(imageUri);
                    decryptSelectedImages();
                    DecryptAllFiles();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        DecryptAllFiles();


        recyclerView.getAdapter().notifyDataSetChanged();
    }
    @Override
    protected void onStop() {
        super.onStop();
        encryptAllFiles();
    }
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        ImageListAdapter adapter = new ImageListAdapter(selectedImages);
        recyclerView.setAdapter(adapter);
    }
    private String getSourceFilePath(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }
    private String getTargetFilePath(Uri uri) {
        File targetDir = getExternalFilesDir(null);
        String targetStr = targetDir.getAbsolutePath();
        if (uri != null) {
            String fileName = getFileName(uri);
            targetStr = targetStr.substring(0, targetStr.lastIndexOf("/")) + File.separator + fileName;
        } else {
            targetStr = targetStr.substring(0, targetStr.lastIndexOf("/")) + File.separator;
        }

        return targetStr;
    }
    private String getFileName(Uri uri) {
        String fileName = null;
        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            fileName = cursor.getString(columnIndex);
            cursor.close();
        }
        return fileName;
    }
    private byte[] getSecurityKey() throws Exception {
        SharedPreferences settings = getSharedPreferences("keyAccount", 0);

        KeyManager km = new KeyManager(GalleryActivity.this);
        if (settings.getBoolean("one", true)) {
            km.setKey();
            settings.edit().putBoolean("one", false).apply();
            System.out.println("new key: " + Arrays.toString(km.getKey()));
        }
        System.out.println("using key: " + Arrays.toString(km.getKey()));
        return km.getKey();
    }
    private void encryptFile(File inputFile) throws Exception {
        byte[] key = getSecurityKey();
        System.out.println("Encrypting key: " + Arrays.toString(key));
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        inputStream.close();

        FileOutputStream outputStream = new FileOutputStream(inputFile);
        outputStream.write(outputBytes);

        outputStream.close();
    }
    private void decryptFile(File inputFile) throws Exception {
        byte[] key = getSecurityKey();
        System.out.println("Decrypting key: " + Arrays.toString(key));

        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        inputStream.close();

        FileOutputStream outputStream = new FileOutputStream(inputFile);
        outputStream.write(outputBytes);

        outputStream.close();
    }
    public void moveData(Uri uri) throws Exception {
        System.out.println("Moving");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String sourceFilePath = getSourceFilePath(uri);
            String targetFilePath = getTargetFilePath(uri);

            File targetFile = new File(targetFilePath);

            Path source = Paths.get(sourceFilePath);
            Path target = Paths.get(targetFilePath);

            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            selectedImages.add(Uri.fromFile(targetFile));
            recyclerView.getAdapter().notifyDataSetChanged();
            Toast.makeText(GalleryActivity.this, getFileName(uri) + " is Secured", Toast.LENGTH_SHORT).show();

        }
    }
    private void decryptSelectedImages() {
        for (int i = 0; i < selectedImages.size(); i++) {
            System.out.println("decrypting: " + i);
            Uri encryptedUri = selectedImages.get(i);
            String filePath = encryptedUri.getPath();
            File encryptedFile = new File(filePath);

            try {
                decryptFile(encryptedFile);
                selectedImages.set(i, Uri.fromFile(encryptedFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        recyclerView.getAdapter().notifyDataSetChanged();
    }
    public void DecryptAllFiles() {
        System.out.println("DecryptAllFiles called");
        String path = getTargetFilePath(null);
        System.out.println(path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        System.out.println("size: " + Objects.requireNonNull(files).length);
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            System.out.println("decrypting all: " + i + " / " + Objects.requireNonNull(files).length);
            File encryptedFile = files[i];

            try {
                decryptFile(encryptedFile);
                if (i < selectedImages.size()) {
                    selectedImages.set(i, Uri.fromFile(encryptedFile));
                } else {
                    selectedImages.add(Uri.fromFile(encryptedFile));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        recyclerView.getAdapter().notifyDataSetChanged();
    }
    private void encryptAllFiles() {
        String path = getTargetFilePath(null);
        System.out.println(path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        System.out.println("size: " + Objects.requireNonNull(files).length);
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            System.out.println("encrypting all: " + i + " / " + Objects.requireNonNull(files).length);
            File inputFile = files[i];

            try {
                encryptFile(inputFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}