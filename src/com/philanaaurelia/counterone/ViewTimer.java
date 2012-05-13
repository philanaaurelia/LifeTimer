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
//	private static final String TIMER_NAME = null;
	//private static final String TIMER_DATE = null;
	//private static final String TIMER_TIME = null;
	//private int pHour;
    //private int pMinute;
    //private int pMonth;
    //private int pDay;
    //private int pYear;
    
	
    Cursor c ;
    SQLHelper VTdb = new SQLHelper(this);

	
	  @Override
		public void onCreate(Bundle savedInstanceState) {
			System.out.println("OPENED");
	    	super.onCreate(savedInstanceState);
	    	setContentView(R.layout.viewtimermain);
	
	    	try {
	    	VTdb.open();
	    	System.out.println("Just Opened VTdb");
	    	
	    	}catch(SQLException sqle){
	    		throw sqle;
	    	}
	    	
	    	PopulateScrollView();
	    	System.out.println("made it past populatescrollview");
	  }
	  
	public void PopulateScrollView(){
		 
		  	//Will hold Category Query Results to be displayed as CheckBox Text
	    	ArrayList<String> catNameResults = new ArrayList<String>(); 
	    	ArrayList<String> catColorResults = new ArrayList<String>();
	    	
	    	// Queries Database to retrieve Category names	
	    	
	    	String sql = "SELECT CategoryName, Color FROM Categories";
	    	c = VTdb.Query(sql,null);

		  
		//Get index of CategoryName Column
	    	int CatNameColumn = c.getColumnIndex("CategoryName");
	    	int CatColor = c.getColumnIndex("Color");
	    	//System.out.println("before the color. CatColor = "+ CatColor +" CatNameColumn = "+ CatNameColumn);
	    	//c.moveToFirst();
	    	
	    	//System.out.println("after the color");
	  
	    		//Check if at least one Result was returned.
	    		if (c.moveToFirst()){
	    			System.out.println("Inside the If");
	    			
	    			//Loop through all Results
	    			do{
	    				
	    				//Retrieve the value of the Entry the Cursor is pointing to
	    				String CategoryName = c.getString(CatNameColumn);
	    				String Color = c.getString(CatColor);
	    				
	    				//add current entry to results
	    				catNameResults.add(CategoryName);
	    				catColorResults.add(Color);
	    				System.out.println("Inside the do");
	    			    System.out.println(CategoryName);
	    			}
	    			while(c.moveToNext());
	    			
	    		}
	    		System.out.println("Outside the Do While");
	    		
	    //Find TableLayout as defined in viewtimermain.xml
	  	TableLayout t1 = (TableLayout)findViewById(R.id.ScrollTable);
	  	
	  	
	  	//Loop to create CheckBoxes in the ScrollView for each item in the results list (all the category names)
	  	CheckBox cb = null;
	  	TableRow tr = null;
	  	int jLimit;
	  	int catsLeft = catNameResults.size() - 1;
	  	int idCount = 0;
	  	
	  	for (int i=0; i < catNameResults.size(); i++){
	  		
	  		if (catsLeft % 2  == 0){
	  			jLimit = 2;
	  		}
	  			else if (catsLeft > 1)
	  				jLimit = 2;
	  			else
	  				jLimit =1;
	  		
	  	int CBCount = 2; //Two CheckBoxes per Table Row

	  		for (int j = 0; j < jLimit; j++){

	  			if (CBCount == 2){
	  				
	  				//Create a new row to be added to the TableView		
	  				tr = new TableRow(this);
	  				float weight = (float) 0.5;//weight variable
	  				
	  				//Set TableRow layout Params. childWeight set to 50%
	  				tr.setLayoutParams(new TableRow.LayoutParams(-2,-2,weight));//width,height,weight
	  				tr.setPadding(2, 2, 2, 2);
	  				
	  				//Add Table Row to TableView
	  				t1.addView(tr);
	  				
	  				//Reset CheckBox Count
	  				CBCount = 0;
	  				catsLeft--;
	  				
	  			}
	  			if (idCount < catNameResults.size()){
	  				
	  				
	  				//Create a CheckBox to add to row-content
	  				cb = new CheckBox(this);
	  				
	  				//To allow access to drawables
	  				Resources res = getResources();
	  				Drawable d = null;

	  				//sets the checkboxes color from database
	  				//System.out.println("Color = " + catColorResults.get(idCount));

	  			//	cb.setButtonDrawable(setCheckboxColor(catColorResults.get(idCount),cb,res));
	  			
	
	  				//Set CheckBox id
	  				cb.setId(idCount);
	  				
	  				//Set Text Size
	  				cb.setTextSize(18);
	  				
	  				//Set CheckBox text to Query result from Database
	  				cb.setText("   " + catNameResults.get(idCount));
	  				
	  				//Add CheckBox to Table Row
	  				tr.addView(cb);
	  				CBCount++;
	  				idCount++;
	  			}
	  		} i++;
	  	}
		  
	    /*	final CheckBox checkbox = (CheckBox) findViewById(R.id.CB1Row1);
	    	checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    	    public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					
				}
	    	});*/
}
	/*public Drawable setCheckboxColor(String Color, CheckBox cb, Resources res){
		//Can't switch on strings and enumerations are costly so nested ifs instead
		Drawable d ;
		System.out.println("The COLOR is : "+ Color);
			
			//Set Color of checkbox from database
			if (Color.equals("blue"))
				//set button = to blue checkbox
				d = res.getDrawable(drawable.blue_checkbox);
			
			else if (Color.equals("bluegreen"))
				d = res.getDrawable(drawable.bluegreen_checkbox);

			else if (Color.equals("gray"))
				d = res.getDrawable(drawable.gray_checkbox);			

			else if (Color.equals("limegreen"))
				d = res.getDrawable(drawable.limegreen_checkbox);

			else if (Color.equals("orange"))
				d = res.getDrawable(drawable.orange_checkbox);

			else if (Color.equals("pink"))
				d = res.getDrawable(drawable.pink_checkbox);
			
			else if (Color.equals("plum"))
				d = res.getDrawable(drawable.plum_checkbox);

			else if (Color.equals("purple"))
				d = res.getDrawable(drawable.purple_checkbox);

			else if (Color.equals("red"))
				d = res.getDrawable(drawable.red_checkbox);

			else if (Color.equals("slateblue"))
				d = res.getDrawable(drawable.slateblue_checkbox);

			else if (Color.equals("turquoise"))
				d = res.getDrawable(drawable.turquoise_checkbox);

			else if (Color.equals("yellow"))
				d = res.getDrawable(drawable.bluegreen_checkbox); 
			else
				d = res.getDrawable(drawable.gray_checkbox);
			
			return d;
			
	}*/
} 



	  
