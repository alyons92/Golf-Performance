package lyons.aaron.golfperformance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "golf_db";
    private static final int VERSION = 5;
    private static final String CLUB_TABLE_NAME = "club_table";
    private static final String PER_TABLE_NAME = "performance_table";
    private static final String CLUB_COL1 = "ID";
    private static final String CLUB_COL2 = "club_name";
    private static final String PER_COL1 = "ID";
    private static final String PER_COL2 = "club_ID";
    private static final String PER_COL3 = "date";
    private static final String PER_COL4 = "distance";
    private static final String PER_COL5 = "club_speed";
    private static final String PER_COL6 = "ball_speed";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createClubTable = "CREATE TABLE " + CLUB_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                CLUB_COL2 + " TEXT)";
        db.execSQL(createClubTable);
        String createPerTable = "CREATE TABLE " + PER_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                PER_COL2 + " INTEGER, " + PER_COL3 + " TEXT, " + PER_COL4 + " DOUBLE, " + PER_COL5
                + " DOUBLE," + PER_COL6 + " DOUBLE, FOREIGN KEY (" + PER_COL2 + ") REFERENCES CLUB_TABLE_NAME (" + CLUB_COL1 + "))";
        db.execSQL(createPerTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CLUB_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PER_TABLE_NAME);
        onCreate(db);
    }

    public boolean addClubData(String item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CLUB_COL2, item);

        long result = db.insert(CLUB_TABLE_NAME, null, contentValues);
        if (result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean addPerformanceData(String dateItem, int clubItem, Double distanceItem, Double clubSpeedItem,
                                      Double ballSpeedItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PER_COL2, clubItem);
        contentValues.put(PER_COL3, dateItem);
        contentValues.put(PER_COL4, distanceItem);
        contentValues.put(PER_COL5, clubSpeedItem);
        contentValues.put(PER_COL6, ballSpeedItem);

        long result = db.insert(PER_TABLE_NAME, null, contentValues);
        if (result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getDateData(String startDate, String endDate, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + PER_COL3 + ", " + PER_COL4 + ", " + PER_COL5 + ", " + PER_COL6 +
                " FROM " + PER_TABLE_NAME + " WHERE " + PER_COL2 + " = '" + id + "' AND " + PER_COL3 +
                " BETWEEN '" + startDate + "' AND '" + endDate + "' ORDER BY " + PER_COL3;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getClubData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + CLUB_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getPerformanceData(String date, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + PER_COL4 + ", " + PER_COL5 + ", " + PER_COL6 + " FROM " + PER_TABLE_NAME +
                " WHERE " + PER_COL2 + " = '" + id + "' AND " + PER_COL3 + " = '" + date + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + CLUB_COL1 + " FROM " + CLUB_TABLE_NAME +
                " WHERE " + CLUB_COL2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + CLUB_TABLE_NAME + " WHERE " +
                CLUB_COL1 + " + '" + id + "'" + " AND " + CLUB_COL2 +
                " = '" + name + "'";
        db.execSQL(query);
    }

    public void updatePerformance(String dateItem, int clubItem, Double distanceItem, Double clubSpeedItem, Double ballSpeedItem){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + PER_TABLE_NAME + " SET " + PER_COL4 + " = '" + distanceItem + "', " +
                PER_COL5 + " = '" + clubSpeedItem + "', " + PER_COL6 + " = '" + ballSpeedItem + "' WHERE "
                + PER_COL2 + " = '" + clubItem + "' AND " + PER_COL3 + " = '" + dateItem + "'";
        db.execSQL(query);
    }
}
