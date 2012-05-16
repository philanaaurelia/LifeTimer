package com.philanaaurelia.counterone;

import android.app.Activity;
import android.app.AlarmManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Vibrator;
import android.preference.PreferenceManager;
//import android.util.Log;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class ViewMyTimer extends Activity {

    //Options menu static value
    private static final int TIMERS_GROUP_ID = 1;
    private static final int HOME_ID = 0;
    private static final int DELETE_ID = 1;
    private static final int ADD_TIMER_ID = 2;
    private static final int ADD_PRESET_ID = 3;
    private static final int EDIT_ID =4;
    private static final int SORT_ID = 5;
    static final Integer CENTER = 17;
    static final Integer RIGHT = 5;

    Table table[];
    Vibrator vibr;
    AlarmManager alam;
    //SharedPreferences sp;
    int cnt = 0;
    LinearLayout mLayout;
    SQLHelper datab = new SQLHelper(this);
    Cursor ptr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main3);
        mLayout = (LinearLayout) findViewById(R.id.mainLayout);
        vibr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        alam = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //sp = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle extras = getIntent().getExtras();
        String table_type = extras.getString("tableType");
        Log.w("TIMER CHOICE",table_type);
        if(table_type.compareTo("ascending")== 0)
            PopulateTimers(true);
        else if(table_type.compareTo("categories")==0)
            PopulateCatTimers();
        else if(table_type.compareTo("descending")==0)
            PopulateTimers(false);


    }


    //This program opens the database returns the ptr
    //Rerturns the pointer
    public void PopulateTimers(Boolean asc){
        datab.open();

        if(asc == true){
            ptr = datab.Results(null, "timers");
        } else {
            ptr = datab.RawQuery("Select * from Timers LEFT OUTER JOIN Categories ON Timers.CategoryName = Categories.CategoryName" +
            " ORDER BY Timers.Date Asc, Timers.Time Asc");
        }

        if(ptr.getCount() > 0)
            table = new Table[ptr.getCount()];

        if (ptr.moveToFirst()){
            do {
                Table table= new Table(this, vibr, alam, ptr);
                mLayout.addView(table.ReturnTimer());
            }while (ptr.moveToNext()); 
        }

        datab.Close();
    }//End PopulateTimers


    public void PopulateCatTimers(){
        datab.open();


        ptr = datab.RawQuery("Select * from Timers LEFT OUTER JOIN Categories ON Timers.CategoryName = Categories.CategoryName" +
        " ORDER BY CategoryName Asc, Timers.Date Asc, Timers.Time Asc");


        if(ptr.getCount() > 0)
            table = new Table[ptr.getCount()];

        if (ptr.moveToFirst()){
            do {
                Table table= new Table(this, vibr, alam, ptr);
                mLayout.addView(table.ReturnTimer());
            }while (ptr.moveToNext()); 
        }

        datab.Close();
    }//End PopulateTimers

    public void PopulateCatTimers(String q){
        datab.open();

        ptr = datab.Results(null, "timers");

        if(ptr.getCount() > 0)
            table = new Table[ptr.getCount()];

        if (ptr.moveToFirst()){
            do {
                Table table= new Table(this, vibr, alam, ptr);
                mLayout.addView(table.ReturnTimer());
            }while (ptr.moveToNext()); 
        }

        datab.Close();
    }//End PopulateTimers

    /************************
     *  OPTIONS MENU
     ************************/
    @Override	
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        menu.add(TIMERS_GROUP_ID, HOME_ID, 0 , "Home")
        .setIcon(R.drawable.ic_menu_home);
        menu.add(TIMERS_GROUP_ID, DELETE_ID, 0, "Delete")
        .setIcon(android.R.drawable.ic_menu_delete);
        menu.add(TIMERS_GROUP_ID, ADD_TIMER_ID, 0, "New Timer")
        .setIcon(android.R.drawable.ic_menu_add);
        menu.add(TIMERS_GROUP_ID, ADD_PRESET_ID, 0, "New Preset")
        .setIcon(android.R.drawable.ic_menu_add);
        menu.add(TIMERS_GROUP_ID, EDIT_ID, 0 , "Edit")
        .setIcon(android.R.drawable.ic_menu_edit);
        menu.add(TIMERS_GROUP_ID, SORT_ID, 0 , "Sort By")
        .setIcon(android.R.drawable.ic_menu_share);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent;
        // Handle item selection
        switch (item.getItemId()) {
        case HOME_ID:
            myIntent = new Intent(this.getBaseContext(), CounterOne.class);
            startActivityForResult(myIntent, 0);
            return true;
        case DELETE_ID:
            myIntent = new Intent(this.getBaseContext(), DeleteTimer.class);
            startActivityForResult(myIntent, 0);
            return true;
        case ADD_TIMER_ID:
            myIntent = new Intent(this.getBaseContext(), AddTimer.class);
            startActivityForResult(myIntent, 0);
            return true;
        case ADD_PRESET_ID:
            myIntent = new Intent(this.getBaseContext(), Preset.class);
            startActivityForResult(myIntent, 0);
            return true;
        case EDIT_ID:
            myIntent = new Intent(this.getBaseContext(), Edit.class);
            startActivityForResult(myIntent, 0);
            return true;
        case SORT_ID:
            myIntent = new Intent(this.getBaseContext(), Sort.class);
            startActivityForResult(myIntent, 0);
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }
}//End class


