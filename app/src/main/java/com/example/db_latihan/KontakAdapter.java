package com.example.db_latihan;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class KontakAdapter extends ArrayAdapter<Kontak> {

    private static class ViewHolder {
        ImageView img;
        TextView nama;
        TextView nohp;
    }
    private SQLiteDatabase db;
    private LayoutInflater li;
    private AlertDialog.Builder dialognya;
    public KontakAdapter(Context context, int resource, List<Kontak> objects) {
        super(context, resource, objects);
    }
    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public void setLi(LayoutInflater li) {
        this.li = li;
    }

    public void setDialognya(AlertDialog.Builder dialognya) {
        this.dialognya = dialognya;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Kontak dtKontak = getItem(position);
        ViewHolder v;
        if (convertView == null) {
            v = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            v.nama= (TextView) convertView.findViewById(R.id.tNama);
            v.nohp= (TextView) convertView.findViewById(R.id.tnoHp);
            v.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(v);

            View.OnClickListener op = (view) -> {
                if (view.getId() == R.id.edit) {
                    edit_input(position);
                } else if (view.getId() == R.id.delete) {
                    delete(position);
                }
            };

            ImageButton edit = (ImageButton)convertView.findViewById(R.id.edit);
            ImageButton delete = (ImageButton)convertView.findViewById(R.id.delete);
            edit.setOnClickListener(op);
            delete.setOnClickListener(op);
        } else {
            v = (ViewHolder)convertView.getTag();
        }
        v.nama.setText(dtKontak.getNama());
        v.nohp.setText(dtKontak.getNrp());
        v.img.setImageResource(dtKontak.getImg());
        return convertView;
    }

    private void edit_input(int position) {
        View input = li.inflate(R.layout.input_dialog, null);

        Kontak k = this.getItem(position);
        EditText nrp = input.findViewById(R.id.nrp);
        EditText nama = input.findViewById(R.id.nama);
        nrp.setText(k.getNrp());
        nama.setText(k.getNama());
        nrp.setEnabled(false);

        dialognya.setView(input);
        dialognya.setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {
                    edit(input);
                })
                .setNegativeButton("Batal", (dialog, which) -> {
                    nrp.setEnabled(true);
                    dialog.cancel();
                });
        dialognya.show();
    }

    private void delete(int position) {
        Kontak k = this.getItem(position);
        if (db.isOpen()) {
            db.delete("mhs", "nrp='" + k.getNrp() + "'", null);
        }
        refresh();
    }

    private void edit(View input) {
        ContentValues dataku = new ContentValues();

        EditText nrp = input.findViewById(R.id.nrp);
        EditText nama = input.findViewById(R.id.nama);

        dataku.put("nrp", nrp.getText().toString());
        dataku.put("nama", nama.getText().toString());
        if (db.isOpen()) {
            db.update("mhs", dataku, "nrp='" + nrp.getText().toString() + "'", null);
        }
        refresh();
    }

    private void refresh() {
        Cursor cur = db.rawQuery("SELECT * FROM mhs", null);

        this.clear();
        if (cur.getCount() > 0) {
            while(cur.moveToNext()) {
                int nama_i = cur.getColumnIndex("nama");
                int nrp_i = cur.getColumnIndex("nrp");
                this.add(new Kontak(cur.getString(nama_i), cur.getString(nrp_i)));
            }
        }
        cur.close();
    }
}
