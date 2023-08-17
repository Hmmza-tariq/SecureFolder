//package com.example.myapplication;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.storage.StorageManager;
//import android.os.storage.StorageVolume;
//import android.provider.MediaStore;
//import android.widget.Button;
//import android.widget.ImageView;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.List;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FilenameFilter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.nio.file.*;
//public class MainActivity extends AppCompatActivity {
//    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
//    private Button addImagesButton;
//    private RecyclerView recyclerView;
//
//    private List<Uri> selectedImages;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        addImagesButton = findViewById(R.id.save_image_button);
//        recyclerView = findViewById(R.id.idRVImages);
//        selectedImages = new ArrayList<>();
//        addImagesButton.setText("Add Images");
//        addImagesButton.setOnClickListener(view -> selectImage());
//
//        setupRecyclerView();
//
//        ActivityCompat.requestPermissions(this, new String[]{
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE},
//                PackageManager.PERMISSION_GRANTED);
//    }
//
//    private void selectImage() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
//            Uri imageUri = data.getData();
////            try {
////                encryptAndMove(Uri.parse(imageUri.getPath()));
////            } catch (Exception e) {
////                System.out.println("error while encrypting image");
////                e.printStackTrace();
////                System.out.println("error while encrypting image");
////            }
////            try {
////                showDecryptedImage();
////            } catch (Exception e) {
////                System.out.println("error while decrypting image");
////                e.printStackTrace();
////                System.out.println("error while decrypting image");
////
////            }
//            try {
//                moveData(imageUri);
//            } catch (IOException e) {
//                System.out.println("error while");
//                e.printStackTrace();
//            }
//            selectedImages.add(imageUri);
//            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
//        }
//    }
//
//    private void setupRecyclerView() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(RecyclerView.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//
//        ImageListAdapter adapter = new ImageListAdapter(selectedImages);
//        recyclerView.setAdapter(adapter);
//    }
//
//    private void encryptAndMove(Uri imageUri) throws IOException {
//
//        InputStream inputStream = getContentResolver().openInputStream(imageUri);
//        byte[] inputData = new byte[inputStream.available()];
//        inputStream.read(inputData);
//        inputStream.close();
//        // Perform encryption using any easy encryption method
//        byte[] encryptedData = encryptionMethod(inputData);
//        // Move the encrypted file to app storage
//        File appStorageDir = getApplicationContext().getFilesDir();
//        File encryptedFile = new File(appStorageDir, "encrypted_image.jpg");
//        FileOutputStream outputStream = new FileOutputStream(encryptedFile);
//        outputStream.write(encryptedData);
//        outputStream.close();
//        // Delete the original file from the local path
//        getContentResolver().delete(imageUri, null, null);
//    }
//
//    private void showDecryptedImage() throws IOException {
//        // Get the list of encrypted files in app storage
//        File appStorageDir = getApplicationContext().getFilesDir();
//        File[] encryptedFiles = appStorageDir.listFiles(new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                return name.toLowerCase().endsWith(".jpg") && name.toLowerCase().startsWith("encrypted_");
//            }
//        });
//
//        // Loop through each encrypted file and decrypt it
//        for (File encryptedFile : encryptedFiles) {
//            FileInputStream inputStream = new FileInputStream(encryptedFile);
//            byte[] encryptedData = new byte[(int) encryptedFile.length()];
//            inputStream.read(encryptedData);
//            inputStream.close();
//
//            // Perform decryption using the same easy encryption method
//            byte[] decryptedData = decryptionMethod(encryptedData);
//
//            // Preview the decrypted image
//            Bitmap bitmap = BitmapFactory.decodeByteArray(decryptedData, 0, decryptedData.length);
//            ImageView imageView = new ImageView(this);
//            imageView.setImageBitmap(bitmap);
//            recyclerView.addView(imageView);
//        }
//    }
//
//    private byte[] decryptionMethod(byte[] inputData) {
//        // Perform simple XOR encryption with key 1234
//        byte[] key = "1234".getBytes();
//        byte[] outputData = new byte[inputData.length];
//        for (int i = 0; i < inputData.length; i++) {
//            outputData[i] = (byte) (inputData[i] ^ key[i % key.length]);
//        }
//        return outputData;
//    }
//
//    private byte[] encryptionMethod(byte[] inputData) {
//        // Perform simple XOR encryption with key 1234
//        byte[] key = "1234".getBytes();
//        byte[] outputData = new byte[inputData.length];
//        for (int i = 0; i < inputData.length; i++) {
//            outputData[i] = (byte) (inputData[i] ^ key[i % key.length]);
//        }
//        return outputData;
//    }
//
//    private String getPathFromUri(Uri uri) {
//        String path = null;
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            path = cursor.getString(columnIndex);
//            cursor.close();
//        }
//        return path;
//    }
//
//    private String getFileNameFromUri(Uri uri) {
//        String fileName = null;
//        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
//            fileName = cursor.getString(columnIndex);
//            cursor.close();
//        }
//        return fileName;
//    }
//    public void moveData(Uri uri) throws IOException {
//
//        String fromFile =  getPathFromUri(uri);
//        String toFile = String.valueOf(getExternalFilesDir(null));
//        String appStorageDirPath = getApplicationContext().getFilesDir().getAbsolutePath();
//
////        System.out.println(fromFile);
////        System.out.println(appStorageDirPath);
////        System.out.println(toFile);
////
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            Path source = Paths.get(getPathFromUri(uri));
////            Path target = Paths.get(String.valueOf(getExternalFilesDir(null)));
////            System.out.println("done");
////            Files.move(source, target);
////        }
//        File file = new File(fromFile);
//        File newFile = new File(appStorageDirPath);
//        if(file.renameTo(newFile)) {
//            System.out.println("File moved successfully!");
//        } else {
//            System.out.println("Failed to move file!");
//        }
//    }
//    public void moveData1(Uri uri) {
//        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
//        List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();
//        StorageVolume storageVolume = storageVolumeList.get(0);
//        File fileSource = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//            fileSource = new File(storageVolume.getDirectory().getPath() + getPathFromUri(uri));
//        }
//        File fileDestination = new File(getExternalFilesDir(null), getFileNameFromUri(uri));
//        try {
//            InputStream inputStream = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                inputStream = Files.newInputStream(fileSource.toPath());
//            }
//            OutputStream outputStream = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                outputStream = Files.newOutputStream(fileDestination.toPath());
//            }
//            byte[] bytesArrayBuffer = new byte[1924];
//            int intLength;
//            while ((intLength = inputStream.read(bytesArrayBuffer)) > 0) {
//                outputStream.write(bytesArrayBuffer, 0, intLength);
//            }
//            inputStream.close();
//            outputStream.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}



//
//public static void encryptFile(Context context, Uri uri) throws Exception {
//        Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
//        + KeyProperties.BLOCK_MODE_CBC + "/"
//        + KeyProperties.ENCRYPTION_PADDING_PKCS7);
//
//        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
//        keyStore.load(null);
//
//        if (!keyStore.containsAlias(ALIAS)) {
//        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
//        keyGenerator.init(new KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
//        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
//        .setUserAuthenticationRequired(false)
//        .build());
//        keyGenerator.generateKey();
//        }
//
//        SecretKey key = (SecretKey) keyStore.getKey(ALIAS, null);
//
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//
//        InputStream inputStream = context.getContentResolver().openInputStream(uri);
//        byte[] inputBytes = new byte[inputStream.available()];
//        inputStream.read(inputBytes);
//        byte[] outputBytes = cipher.doFinal(inputBytes);
//
//        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
//        outputStream.write(outputBytes);
//        outputStream.flush();
//        outputStream.close();
//        }
//
//public static void decryptFile(Context context, Uri uri) throws Exception {
//        Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
//        + KeyProperties.BLOCK_MODE_CBC + "/"
//        + KeyProperties.ENCRYPTION_PADDING_PKCS7);
//
//        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
//        keyStore.load(null);
//
//        if (!keyStore.containsAlias(ALIAS)) {
//        throw new Exception("Key not found");
//        }
//
//        SecretKey key = (SecretKey) keyStore.getKey(ALIAS, null);
//
//        cipher.init(Cipher.DECRYPT_MODE, key);
//
//        InputStream inputStream = context.getContentResolver().openInputStream(uri);
//        byte[] inputBytes = new byte[inputStream.available()];
//        inputStream.read(inputBytes);
//        byte[] outputBytes = cipher.doFinal(inputBytes);
//
//        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
//        outputStream.write(outputBytes);
//        outputStream.flush();
//        outputStream.close();
//        }
//
