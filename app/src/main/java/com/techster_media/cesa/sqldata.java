package com.techster_media.cesa;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class sqldata {
    public SQLiteDatabase db;

    public void mysqladd(String str_name, String str_number, Context context) {
        db = context.openOrCreateDatabase("NumberDB", MODE_PRIVATE, null);
        //Toast.makeText(getApplicationContext(), "db created",Toast.LENGTH_LONG).show();
        //db2 = db;
        db.execSQL("CREATE TABLE IF NOT EXISTS details(Pname VARCHAR,number VARCHAR);");
        //Toast.makeText(getApplicationContext(), "table created",Toast.LENGTH_LONG).show();

        Cursor c = db.rawQuery("SELECT * FROM details", null);
        if (c.getCount() <= 5) {
            db.execSQL("INSERT INTO details VALUES('" + str_name + "','" + str_number + "');");


            Toast.makeText(context, "Successfully Saved", Toast.LENGTH_SHORT).show();
        } else {

            //db.execSQL("INSERT INTO details VALUES('" + str_name + "','" + str_number + "');");
            Toast.makeText(context, "Maximun Numbers limited reached.", Toast.LENGTH_SHORT).show();
        }


        db.close();
    }


    public void mysqldel(String str_number, Context context) {
        db = context.openOrCreateDatabase("NumberDB", MODE_PRIVATE, null);
        //Toast.makeText(getApplicationContext(), "db created",Toast.LENGTH_LONG).show();
        //db2 = db;
        db.execSQL("CREATE TABLE IF NOT EXISTS details(Pname VARCHAR,number VARCHAR);");
        //Toast.makeText(getApplicationContext(), "table created",Toast.LENGTH_LONG).show();

        Cursor c = db.rawQuery("SELECT * FROM details", null);
        db.execSQL("DELETE FROM details WHERE number="+str_number+"");
        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();

        db.close();
        c.close();
    }
    public void mysqlmod(String str_number,String str_name,String oldname,String oldnum, Context context) {
        db = context.openOrCreateDatabase("NumberDB", MODE_PRIVATE, null);
        //Toast.makeText(getApplicationContext(), "db created",Toast.LENGTH_LONG).show();
        //db2 = db;
        db.execSQL("CREATE TABLE IF NOT EXISTS details(Pname VARCHAR,number VARCHAR);");
        //Toast.makeText(getApplicationContext(), "table created",Toast.LENGTH_LONG).show();

        Cursor c = db.rawQuery("SELECT * FROM details", null);
        db.execSQL("UPDATE details SET number="+str_number+" WHERE number="+oldnum+"");
        db.execSQL("UPDATE details SET Pname="+str_name+" WHERE Pname="+oldname+"");
        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();

        db.close();
        c.close();
    }


    public static String getNumber(SQLiteDatabase db2) {


        Cursor c = null;
        String phone_num = "";
        //db2 = openOrCreateDatabase("NumDB", MODE_PRIVATE, null);

        c = db2.rawQuery("SELECT * FROM details LIMIT 1", null);
        if (c.getCount() > 0) {
            //c.moveToFirst();

            while (c.moveToNext()) {

                phone_num += c.getString(1);
            }

        }

        return phone_num;


    }
}
