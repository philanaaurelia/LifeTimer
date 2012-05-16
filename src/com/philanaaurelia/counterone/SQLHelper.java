package com.philanaaurelia.counterone;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
//import android.database.SQLException;
//import android.content.ContentValues;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class SQLHelper{
    private static final String TAG = "Timers";
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "timers.db";
    private static String DATABASE_PATH = "/data/data/com.philanaaurelia.counterone/databases/";

    //Static Vars for the Timers table
    private static final String TIMER_TABLE_NAME = "Timers";
    private static final String TIMER_ID = "TimerID";
    private static final String TIMER_NAME = "TimerName";
    private static final String TIMER_DATE = "Date";
    private static final String TIMER_TIME = "Time";
    private static final String TIMER_COLOR = "Color";


    //Static vars for the Categories table
    public static final String CATEGORY_TABLE_NAME = "Categories";
    private static final String CATEGORY_ID = "CategoryID";
    private static final String CATEGORY_NAME = "CategoryName";
    private static final String CATEGORY_COLOR = "Color";


    //Static vars for the ExtendCategories tables
    private static final String SUBCAT_TABLE_NAME = "Subcategories";
    private static final String SUBCAT_ID = "SubID";
    private static final String SUBCAT_NAME = "SubName";

    //Create timer Table
    private static final String TIMER_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS " + TIMER_TABLE_NAME + " (" +
        TIMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
        CATEGORY_NAME + " TEXT, " +
        SUBCAT_NAME + " TEXT, " +
        TIMER_NAME + " TEXT, "  +
        TIMER_DATE + " INTEGER, " +
        TIMER_TIME + " INTEGER, " +
        TIMER_COLOR + " COLOR);" +
        "FOREIGN KEY(" + CATEGORY_ID + ") REFERENCES "+ CATEGORY_TABLE_NAME + "(" + CATEGORY_ID + "), " +
        "FOREIGN KEY(" + SUBCAT_ID + ") REFERENCES "+ SUBCAT_ID + "(" + SUBCAT_ID + "));";
    //Create Category Table
    private static final String CATEGORY_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS " + CATEGORY_TABLE_NAME + " (" +
        CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
        CATEGORY_NAME + " TEXT, "  +
        CATEGORY_COLOR + " TEXT);" ;// If yes, look in extendable table to populate all categories
    //Create Subcategory Table 
    private static final String SUBCAT_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS " + SUBCAT_TABLE_NAME + " (" +
        SUBCAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"  +
        CATEGORY_ID + " INTEGER," + 
        SUBCAT_NAME + " TEXT, " +
        "FOREIGN KEY(" + CATEGORY_ID + ") REFERENCES "+ CATEGORY_TABLE_NAME + "(" + CATEGORY_ID + "));";

    private final Context myContext;

    private MyHelper DBHelper;
    private SQLiteDatabase db=null;

    public SQLHelper(Context ctx) 
    {
        this.myContext = ctx;
        DBHelper = new MyHelper(myContext);
        Log.w("DATABASE", "initialized");
        // db = DBHelper.getWritableDatabase();
        Log.w("DATABASE", "OPEN!");
    }

    //---opens the database---
    public SQLHelper open() 
    {
        Log.w("DATABASE", "Attempt!");
        //db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        db = DBHelper.getWritableDatabase();
        return this;
    }

    private static class MyHelper extends SQLiteOpenHelper{
        //Constructor and creates database
        MyHelper(Context context) {
            super(context, DATABASE_NAME,null, DATABASE_VERSION);
            Log.w("DATABASE","Set up");
            //this.myContext = context;

            //	boolean dbExist = checkDataBase();

            // 	if(dbExist){
            //onCreate(db);
            //}else{
            //Log.w("DATABASE","Create it");
            //Log.w("DATABASE PATH", db.getPath());
            //this.getWritableDatabase();
            //Log.w("DATABASE","Created");


            //	}
        }

        // Log.w("SQL CONSTRUCTOR", "done");
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }


        //DONT PUT ANYTHING HEAR FOR CREATING DATABASE??
        @Override
        public void onCreate(SQLiteDatabase db) {
            //
            Log.w("DATABASE","Created");
            db.execSQL(TIMER_TABLE_CREATE);
            db.execSQL(CATEGORY_TABLE_CREATE);
            db.execSQL(SUBCAT_TABLE_CREATE);
        }

    }


    private boolean checkDataBase(){		    	 
        try{
            String myPath = DATABASE_PATH + DATABASE_NAME;
            db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            //createTables();

            Log.w("CREATED DATABASE", "Done");
            return true;

        }catch(SQLiteException e){
            return false;
            //database does't exist yet.
        }
    }


    //Attempt to create all the tables if not already


    //Delete all tables
    public void deleteTables(){
        db.execSQL("DROP TABLE IF EXISTS Test");
        db.execSQL("DROP TABLE IF EXISTS Timers");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Subcategories");			 
    }

    //Insert values for databases
    //Value[0] is name of database
    //Value[i] is the values that u want to insert
    public void Insert(String [] values){
        ContentValues args = new ContentValues(); 
        if(TIMER_TABLE_NAME.equals(values[0])){
            args.put(CATEGORY_NAME, values[1]);
            // args.put(SUBCAT_ID, values[2]); 
            args.put(TIMER_NAME,values[3]);
            args.put(TIMER_DATE,Integer.parseInt(values[4]));
            args.put(TIMER_TIME,Integer.parseInt(values[5]));

        }else if(CATEGORY_TABLE_NAME.equals(values[0])){
            args.put(CATEGORY_NAME, values[1]);
            args.put(CATEGORY_COLOR, values[2]);

        }else if(SUBCAT_TABLE_NAME.equals(values[0])){
            args.put(CATEGORY_ID, values[1]);
            args.put(SUBCAT_NAME, values[2]);
        } else {
            //THROW SOME ERROR
        }
        Log.w("BEFORE INSERT", "done");
        db.insert(values[0], null, args);		 
    }


    //Returns the results needed of the query
    public Cursor Results(String[] values, String type){

        //final int num = 0;
        if (type.equals("spinner"))
            return db.query(values[0], new String[]{values[1]},null,null,null,null,null,null);
        else  if (type.equals("deltime"))
            return db.query(values[0], new String[]{values[1], values[2], values[3]},null,null,null,null,null,null);
        else if (type.equals("timers"))
            //SELECT * FROM Artists LEFT OUTER JOIN CDs ON Artists.ArtistID = CDs.ArtistID;  
            return db.rawQuery("Select * from Timers LEFT OUTER JOIN Categories ON Timers.CategoryName = Categories.CategoryName" +
                    " ORDER BY Timers.Date Asc, Timers.Time Asc",null);
        //db.query(values[0], null,null,null,null,null, TIMER_DATE +", " + TIMER_TIME + " ASC",null);
        else
            return RawQuery("Select * From " + values[0]);
    }

    public Cursor RawQuery(String query){
        return db.rawQuery(query, null);
    }

    public void Execute(String string){
        db.execSQL(string);
    }	

    //Determines if results exist or not
    public Integer NumResults(String table){
        Cursor c;

        c = db.query(table, null,null,null,null,null, null,null);
        return c.getCount();

    }

    public void Close(){
        db.close();
    }


    public Cursor Query(String sql, String args[]){
        return db.rawQuery(sql,args);  
    }
}
