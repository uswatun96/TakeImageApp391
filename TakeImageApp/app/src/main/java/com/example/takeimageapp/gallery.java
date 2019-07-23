package com.example.takeimageapp;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;

public class gallery extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    ImageAdapter myImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        GridView gridView = (GridView)findViewById(R.id.gridview);
        myImageAdapter = new ImageAdapter(this);
        gridView.setAdapter(myImageAdapter);
        gridView.setOnItemClickListener(this);

        String ExternalStorageDirectoryPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath();
        String targetPath = ExternalStorageDirectoryPath+ "/DCIM/Camera";
        Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_SHORT).show();
        File targerDirector = new File(targetPath);
        File[] files = targerDirector.listFiles();
        for (File file:files){
            myImageAdapter.add(file.getAbsolutePath());
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(gallery.this, "Gambar no"+position+"Dipilih", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(),OneFoto.class );
        i.putExtra("path", myImageAdapter.itemList.get(position));
        startActivity(i);
    }
}
