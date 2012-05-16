package com.philanaaurelia.counterone;

import java.util.ArrayList;
import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
//import android.view.View;

public class ViewTimer extends Activity {	
    Cursor databaseCursor ;
    SQLHelper database = new SQLHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("OPENED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewtimermain);

        try {
            database.open();
            System.out.println("Just Opened VTdb");
        } catch(SQLException sqle){
            throw sqle;
        }

        PopulateScrollView();
        System.out.println("made it past populatescrollview");
    }

    public void PopulateScrollView(){
        ArrayList<String> categoryNameQueryResults = new ArrayList<String>(); 
        ArrayList<String> categoryColorQueryResults = new ArrayList<String>();
        String sql = "SELECT CategoryName, Color FROM Categories";

        databaseCursor = database.Query(sql,null);  
        int CatNameColumn = databaseCursor.getColumnIndex("CategoryName");
        int CatColor = databaseCursor.getColumnIndex("Color");
        //System.out.println("before the color. CatColor = "+ CatColor +" CatNameColumn = "+ CatNameColumn);
        //c.moveToFirst();

        //Check if at least one Result was returned.
        if (databaseCursor.moveToFirst()){
            System.out.println("Inside the If");

            //Loop through all Results
            do{
                //Retrieve the value of the Entry the Cursor is pointing to
                String CategoryName = databaseCursor.getString(CatNameColumn);
                String Color = databaseCursor.getString(CatColor);

                //add current entry to results
                categoryNameQueryResults.add(CategoryName);
                categoryColorQueryResults.add(Color);
                System.out.println("Inside the do");
                System.out.println(CategoryName);
            }
            while(databaseCursor.moveToNext());

        }
        System.out.println("Outside the Do While");

        //Find TableLayout as defined in viewtimermain.xml
        TableLayout t1 = (TableLayout)findViewById(R.id.ScrollTable);


        //Loop to create CheckBoxes in the ScrollView for each item in the results list (all the category names)
        CheckBox cb = null;
        TableRow tr = null;
        int jLimit;
        int catsLeft = categoryNameQueryResults.size() - 1;
        int idCount = 0;

        for (int i=0; i < categoryNameQueryResults.size(); i++){
            if (catsLeft % 2  == 0){
                jLimit = 2;
            } else if (catsLeft > 1){
                jLimit = 2;
            }else {
                jLimit =1;
            }

            int CBCount = 2; //Two CheckBoxes per Table Row

            for (int j = 0; j < jLimit; j++){
                if (CBCount == 2){
                    //Create a new row to be added to the TableView		
                    tr = new TableRow(this);
                    float weight = (float) 0.5;//weight variable

                    //Set TableRow layout Params. childWeight set to 50%
                    tr.setLayoutParams(new TableRow.LayoutParams(-2,-2,weight));//width,height,weight
                    tr.setPadding(2, 2, 2, 2);

                    //Add Table Row to TableView and reset checkbox count
                    t1.addView(tr);
                    CBCount = 0;
                    catsLeft--;
                }

                if (idCount < categoryNameQueryResults.size()){
                    //Create a CheckBox to add to row-content
                    cb = new CheckBox(this);

                    //To allow access to drawables
                    Resources res = getResources();
                    Drawable d = null;

                    //sets the checkboxes color from database
                    //System.out.println("Color = " + categoryColorQueryResults.get(idCount));

                    cb.setId(idCount);
                    cb.setTextSize(18);

                    //Set CheckBox text to Query result from Database
                    cb.setText("   " + categoryNameQueryResults.get(idCount));

                    //Add CheckBox to Table Row
                    tr.addView(cb);
                    CBCount++;
                    idCount++;
                }
            }
            i++;
        }
    }
}
