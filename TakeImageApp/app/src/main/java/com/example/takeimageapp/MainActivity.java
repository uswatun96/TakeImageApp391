package com.example.takeimageapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btn_ambil_gambar;
    private ImageView img_view;
    private static final String IMAGE_DIR = "/Gambar";
    private int GALLERY = 1, CAMERA = 2;
    private Button btn_lihat_gambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            requestMultiplePermissions();

            btn_ambil_gambar = (Button) findViewById(R.id.btn_ambil_gambar);
            img_view = (ImageView) findViewById(R.id.img_view);
            btn_lihat_gambar = (Button) findViewById(R.id.btn_lihat_gambar);
            btn_ambil_gambar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImageDialog();

                }
            });
            btn_lihat_gambar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, gallery.class);
                    startActivity(intent);
                }
            });
        }
    private void requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override

                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        //check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }
                        //check for permanent denial of any permissions
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            //show alert dialog navigating to settings
                            //openSettingDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                      token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener(){
                    @Override
                    public void onError(DexterError error){
                        Toast.makeText(getApplicationContext(),"Some Error!", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    private void showImageDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"
        };
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        chooseGallery();
                        break;
                    case 1:
                        chooseCamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private String saveImage(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIR);

        //have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG_IMAGE", "File Saved: :---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException el) {
            el.printStackTrace();
        }
        return "";
    }
    private void chooseCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void chooseGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED){
            return;
        }
        if (requestCode == GALLERY){
            if (data!=null){
                Uri contenURI = data.getData();
                try {
                    Bitmap bitmap =
                            MediaStore.Images.Media.getBitmap(this.getContentResolver(), contenURI);
                    String path = saveImage(bitmap);
                    Log.d("TAG_IMAGE", "File Saved: :---&gt;" + path);
                    Toast.makeText(MainActivity.this, "Image saved!", Toast.LENGTH_SHORT).show();
                    img_view.setImageBitmap(bitmap);

                } catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
            }
        }else if (requestCode == CAMERA){
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            img_view.setImageBitmap(thumbnail);
            String path = saveImage(thumbnail);
            Log.d("TAG_IMAGE", "File Saved::---&gt;" + path);
            Toast.makeText(MainActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
        }
    }
}
