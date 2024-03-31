package com.example.db_latihan;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SQLiteOpenHelper db_helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.db_helper = new SQLiteOpenHelper(this, "db.sql", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        this.db = this.db_helper.getWritableDatabase();
        db.execSQL("create table if not exists mhs(nrp TEXT, nama TEXT);");


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View.OnClickListener operasi = new View.OnClickListener() {
            EditText nrp = findViewById(R.id.nrp);
            EditText nama = findViewById(R.id.nama);
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.Simpan) {
                    simpan(nrp, nama);
                } else if (v.getId() == R.id.ambildata) {
                    ambil(nrp, nama);
                } else if (v.getId() == R.id.update) {
                    update(nrp, nama);
                } else if (v.getId() == R.id.hapus) {
                    delete(nrp);
                }
            }
        };
        Button simpan = findViewById(R.id.Simpan);
        Button ambildata = findViewById(R.id.ambildata);
        Button update = findViewById(R.id.update);
        Button hapus = findViewById(R.id.hapus);
        simpan.setOnClickListener(operasi);
        ambildata.setOnClickListener(operasi);
        update.setOnClickListener(operasi);
        hapus.setOnClickListener(operasi);
    }

    protected void onStop() {
        db.close();
        db_helper.close();
        super.onStop();
    }

    private void simpan(EditText nrp, EditText nama) {
        ContentValues dataku = new ContentValues();

        dataku.put("nrp", nrp.getText().toString());
        dataku.put("nama", nama.getText().toString());

        db.insert("mhs", null, dataku);

        Toast.makeText(this, "Data Tersimpan", Toast.LENGTH_LONG).show();
    }

    private void ambil(EditText nrp, EditText nama) {
        Cursor cur = db.rawQuery("SELECT * FROM mhs WHERE nrp='" + nrp.getText().toString() + "'", null);

        if (cur.getCount() > 0) {
            Toast.makeText(this, "Data Ditemukan Sejumlah " + cur.getCount(), Toast.LENGTH_LONG).show();
            cur.moveToFirst();
            int index = cur.getColumnIndex("nama");
            nama.setText(index >= 0 ? cur.getString(index) : "");
        } else {
            Toast.makeText(this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
        }
        cur.close();
    }

    private void update(EditText nrp, EditText nama) {
        ContentValues dataku = new ContentValues();

        dataku.put("nrp", nrp.getText().toString());
        dataku.put("nama", nama.getText().toString());

        db.update("mhs", dataku, "nrp='" + nrp.getText().toString() + "'", null);

        Toast.makeText(this, "Data Terupdate", Toast.LENGTH_LONG).show();
    }

    private void delete(EditText nrp) {
        db.delete("mhs", "nrp='" + nrp.getText().toString() + "'", null);
        Toast.makeText(this, "Data Terhapus", Toast.LENGTH_LONG).show();
    }
}