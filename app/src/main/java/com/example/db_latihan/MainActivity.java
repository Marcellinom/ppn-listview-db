package com.example.db_latihan;

import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SQLiteOpenHelper db_helper;
    private SQLiteDatabase db;
    private ListView lv;
    private KontakAdapter kontak_adapter;
    private LayoutInflater li;
    private AlertDialog.Builder dialognya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        li = LayoutInflater.from(this);
        dialognya = new AlertDialog.Builder(this);
        this.db_helper = new SQLiteOpenHelper(this, "db.sql", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        this.db = this.db_helper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS mhs");
        db.execSQL("create table if not exists mhs(nrp TEXT, nama TEXT);");


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View.OnClickListener operasi = new View.OnClickListener() {
//            EditText nrp = findViewById(R.id.nrp);
//            EditText nama = findViewById(R.id.nama);
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.Simpan) {
                    tampil_input("simpan");
                } else if (v.getId() == R.id.ambildata) {
                    tampil_input("ambil");
                } else if (v.getId() == R.id.ambilsemua) {
                    refresh();
                }
            }
        };
        ImageButton simpan = findViewById(R.id.Simpan);
        ImageButton ambildata = findViewById(R.id.ambildata);
        ImageButton ambilsemua = findViewById(R.id.ambilsemua);
        simpan.setOnClickListener(operasi);
        ambildata.setOnClickListener(operasi);
        ambilsemua.setOnClickListener(operasi);

        lv = (ListView) findViewById(R.id.listView);

        ArrayList<Kontak> listKontak = new ArrayList<>();
        kontak_adapter = new KontakAdapter(this, 0, listKontak);
        kontak_adapter.setDb(db);
        kontak_adapter.setLi(li);
        kontak_adapter.setDialognya(dialognya);
        lv.setAdapter(kontak_adapter);
    }

    protected void onStop() {
        db.close();
        db_helper.close();
        super.onStop();
    }

    private void simpan(EditText et_nrp, EditText et_nama) {
        ContentValues dataku = new ContentValues();

        String nrp = et_nrp.getText().toString();
        String nama = et_nama.getText().toString();

        dataku.put("nrp", nrp);
        dataku.put("nama", nama);
        if (db.isOpen()) {
            db.insert("mhs", null, dataku);
            kontak_adapter.add(new Kontak(nama, nrp));
        }
    }

    private void ambil(EditText nrp) {
        Cursor cur = db.rawQuery("SELECT * FROM mhs WHERE nrp='" + nrp.getText().toString() + "'", null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            int nama_i = cur.getColumnIndex("nama");
            int nrp_i = cur.getColumnIndex("nrp");
            kontak_adapter.clear();
            kontak_adapter.add(new Kontak(cur.getString(nama_i), cur.getString(nrp_i)));
        } else {
            Toast.makeText(this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
        }
        cur.close();
    }

    private void refresh() {
        Cursor cur = db.rawQuery("SELECT * FROM mhs", null);

        if (cur.getCount() > 0) {
            kontak_adapter.clear();
            while(cur.moveToNext()) {
                int nama_i = cur.getColumnIndex("nama");
                int nrp_i = cur.getColumnIndex("nrp");
                kontak_adapter.add(new Kontak(cur.getString(nama_i), cur.getString(nrp_i)));
            }
        }
        cur.close();
    }

    private void update(EditText nrp, EditText nama) {
        Cursor cur = db.rawQuery("SELECT * FROM mhs WHERE nrp='" + nrp.getText().toString() + "'", null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            int nama_i = cur.getColumnIndex("nama");
            int nrp_i = cur.getColumnIndex("nrp");
            nrp.setText(cur.getString(nrp_i));
            nama.setText(cur.getString(nama_i));
            nrp.setEnabled(false);
        } else {
            Toast.makeText(this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
        }

        ContentValues dataku = new ContentValues();

        dataku.put("nrp", nrp.getText().toString());
        dataku.put("nama", nama.getText().toString());
        if (db.isOpen()) {
            db.update("mhs", dataku, "nrp='" + nrp.getText().toString() + "'", null);
            refresh();
        }
    }

    private void delete(EditText nrp) {
        db.delete("mhs", "nrp='" + nrp.getText().toString() + "'", null);
    }

    public void tampil_input(String action) {
        View inputnya;
        switch (action) {
            case "ambil":
                inputnya = li.inflate(R.layout.input_search, null);
                break;
            default:
            case "simpan":
                inputnya = li.inflate(R.layout.input_dialog, null);
                break;
        }
        dialognya.setView(inputnya);
        EditText nrp = inputnya.findViewById(R.id.nrp);
        EditText nama = inputnya.findViewById(R.id.nama);

        dialognya
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {
                    switch (action) {
                        case "simpan":
                            simpan(nrp, nama);
                            break;
                        case "ambil":
                            ambil(nrp);
                            break;
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        dialognya.show();
    }
}