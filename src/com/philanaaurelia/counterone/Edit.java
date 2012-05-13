/* 
 * Written By Philana Benton
 * Spelman College/University of Michigan
 * Luke 1:37
 */

package com.philanaaurelia.counterone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
//import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;

//import java.util.Stack;

public class Edit extends Activity {
	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;

	//Declare all the view variables
	TextView pDBdisplay, pNameText, pTimeDisplay, pDateDisplay,pRevString;
	Button goback, pAddTime, pAddDate, pSubmitDate, pReview;
	EditText pEditCatName, pEditTimerName;
	Spinner pCatSpin, pTimerSpin; // pSubCatSpin;
	ArrayAdapter<String> spinAdapterCat, spinAdapterEdit;// spinAdapterSubCat;
	RadioGroup mRadCol;
	RadioButton mRadBlg, mRadBlu, mRadGra, mRadLig,mRadOra, mRadPlu,mRadPur, mRadRed, mRadTur, mRadYel, mRadPin, mRadSlb;
	
	//Specifies view visibility types
	private static final int GONE = 8;
	private static final int VISIBLE = 0;
	
	//variables for time and date settings
	private int pHour;
    private int pMinute;
    private int pMonth;
    private int pDay;
    private int pYear;
    
    private Boolean no_cat = false; // no category is chosen as spinner option
    Cursor ptr;
    SQLHelper datab = new SQLHelper(this);
    String o_timer_name;
    String o_cat_name;
  
	
  @Override
	public void onCreate(Bundle savedInstanceState) {
	  datab.open();
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.edit);
    	
    	// get the current time and set variables for Time Display
        pHour = 0;//c.get(Calendar.HOUR_OF_DAY);
        pMinute = 0;//c.get(Calendar.MINUTE);
        pYear = 0;//c.get(Calendar.YEAR);
        pMonth = 0;//c.get(Calendar.MONTH);
        pDay = 0;//c.get(Calendar.DAY_OF_MONTH);

       // capture our View elements
       pTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
       pAddTime = (Button) findViewById(R.id.addTime);
       pDateDisplay = (TextView) findViewById(R.id.dateDisplay);
       pAddDate = (Button) findViewById(R.id.addDate);
       pCatSpin = (Spinner) findViewById(R.id.catSpinner);
       pTimerSpin = (Spinner) findViewById(R.id.timerSpinner);
       pNameText = (TextView) findViewById(R.id.NameText);
       pEditTimerName = (EditText) findViewById(R.id.AddTimerName);
       pEditCatName = (EditText) findViewById(R.id.AddCatName);
       mRadCol = (RadioGroup) findViewById(R.id.RadioColors);
       mRadBlg = (RadioButton) findViewById(R.id.radioBlg);
       mRadBlu = (RadioButton) findViewById(R.id.radioBlu);
       mRadGra = (RadioButton) findViewById(R.id.radioGra);
       mRadLig = (RadioButton) findViewById(R.id.radioLiG);
       mRadOra = (RadioButton) findViewById(R.id.radioOra);
       mRadPlu = (RadioButton) findViewById(R.id.radioPlu);
       mRadPur = (RadioButton) findViewById(R.id.radioPur);
       mRadRed = (RadioButton) findViewById(R.id.radioRed);
       mRadTur = (RadioButton) findViewById(R.id.radioTur);
       mRadYel = (RadioButton) findViewById(R.id.radioYel);
       mRadPin = (RadioButton) findViewById(R.id.radioPin);
       mRadSlb = (RadioButton) findViewById(R.id.radioSlB);
       
       updateTime();
       updateDate();
       SetUpSpinners();
	}
	
  	//This function submits time, dates, timer name, category names
  	//etc to the database
	public void Submit_Click(View view){
		DialogHelper dialog = new DialogHelper(this);
		//Sets time instances for comparisons
		//needed to determine if timers have already
		//finished countdown, or need a countdown instance
		Time curtime = new Time();
		curtime.setToNow();
		
		Time newtime = new Time();
		newtime.set(0, pMinute, pHour, pDay, pMonth, pYear);
		
		//format as YYYYMMDD so timers can be loaded in ascending order
		Integer inputDate = 1000000*pYear + pDay + 10000*(pMonth+1);
		Integer inputTime = 1000000 + 10000*pHour + 100*pMinute+ 60;
		Integer radio_btn = mRadCol.getCheckedRadioButtonId();
		String timer_name =pEditTimerName.getText().toString();
		
		String cat_name = pEditCatName.getText().toString(); //If cat was chosen from spinner, pEditCatName text was set to cat name
		Log.w("RADIO BUTTON", String.valueOf(radio_btn));

		//If there is no name for the timer
		if(timer_name.length()==0){
			dialog.set("Uh oh!", "Your timer needs a name", "Ok");
			dialog.show();
			return;
			
		//If the date set is in the past
		} else if(newtime.before(curtime)==true){
			dialog.set("Oops!", "Wouldn't it be nice to turn back time? Please pick a future date.", "Ok");
			dialog.show();
			return;
		}else{
			
			//If it's a new category (Note both new category and no category return -1)
			if(spinAdapterCat.getPosition(cat_name) < 0){
				
				//If no color is picked, black is assigned via Color() function
				//and category is inserted as long as no category is not assigned
				if(no_cat==false)
					datab.Insert(new String[]{"Categories", cat_name, Color()});
				} 
			
			//It sets the color of timer if no category added
			//Also sets the cat_name to null
			String timer_color= null;
			if(cat_name.length() == 0){
				cat_name = null;
				timer_color= Color();
			}
			
			//Insert into timers
			//Log.w("CATEGORY NAME", cat_name);
			datab.Execute("UPDATE Timers SET Date = " + String.valueOf(inputDate) + ", Time = " + String.valueOf(inputTime)
					+ ", Color = '" + timer_color + "', TimerName = '" + timer_name + "' WHERE TimerName = '"+ o_timer_name
					+ "' and CategoryName = '" + o_cat_name + "'");

			Log.w("TIMER","Inserted to database");	
			datab.Close();
			//ptr.close();
		
			//Load ViewTimers page after timer is created
			Intent myIntent = new Intent(view.getContext(), ViewMyTimer.class);
			startActivityForResult(myIntent, 0);
		}
	}
	
	//Returns the color of category to be saved depending
	//on which radio button was pushed
	public String Color(){
		if(mRadBlg.isChecked())
			return "bluegreen";
		else if(mRadBlu.isChecked())
			return "blue";
		else if(mRadGra.isChecked())
			return "gray";
		else if(mRadLig.isChecked())
			return "limegreen";
		else if(mRadOra.isChecked())
			return "orange";
		else if(mRadPlu.isChecked())
			return "plum";
		else if(mRadPur.isChecked())
			return "purple";
		else if(mRadRed.isChecked())
			return "red";
		else if(mRadTur.isChecked())
			return "turquoise";
		else if(mRadPin.isChecked())
			return "pink";
		else if(mRadYel.isChecked())
			return "yellow";
		else if(mRadSlb.isChecked())
			return "slateblue";
		else
			return "black";
	}

    /**************************************
	//			SPINNER FUNCTIONS
     **************************************/
	public void SetUpSpinners(){
		spinAdapterCat = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
		spinAdapterEdit = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
	       
		spinAdapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinAdapterEdit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	       
	    //pSubCatSpin.setAdapter(spinAdapterSubCat);
	    pCatSpin.setAdapter(spinAdapterCat);
	    pTimerSpin.setAdapter(spinAdapterEdit); 
	       
	       spinAdapterCat.add("Edit Category");
	       spinAdapterCat.add("Create New...");
	       
	       spinAdapterEdit.add("Edit Timer...");
	      
	       pCatSpin.setOnItemSelectedListener(new SpinnerListener(pEditCatName));
	       //pTimerSpin.setOnItemSelectedListener(new TimerSpinner(pEditTimerName));
	       pTimerSpin.setOnItemSelectedListener(new OnItemSelectedListener(){public void onItemSelected(AdapterView parent, View
	    		   v,  int pos, long id) {
	    	   String[] temp;
	    	   Log.w("TIMER NAME", spinAdapterEdit.getItem(pos));
	    	 
  	    		
	   			switch(pos){
	   	    	//If user chooses nothing, it does nothing
	   	    	case 0:
	   	    		break;
	   	    	default:
	   	    		temp = spinAdapterEdit.getItem(pos).split("\\(");
	   	    		Log.w("QUERY", temp[0].trim());
	   	    		Log.w("CategoryName", temp[1]);

	   	    		ptr = datab.RawQuery("SELECT * FROM Timers WHERE TimerName = '" + temp[0].trim() + "'");
	   	    		if(ptr.moveToFirst()){ // DO NOT FORGET THIS!!!
	   	    		String time =  ptr.getString(Table.TIMER_TIME);
	   	    		String day = ptr.getString(Table.TIMER_DATE);
	   	    		o_timer_name = ptr.getString(Table.TIMER_NAME);
	   	    		o_cat_name = ptr.getString(Table.CAT_NAME);
	   	    		
	   	    		pMinute = Integer.parseInt(time.substring(3, 5));
	   	    		pHour = Integer.parseInt(time.substring(1, 3));
	   	    		
	   	    		pYear = Integer.parseInt(day.substring(0, 4));
	   				pMonth = Integer.parseInt(day.substring(4, 6)) - 1;
	   				pDay= Integer.parseInt(day.substring(6, day.length()));
	   				
	   	    		}
	   	    		
	   	    		pEditTimerName.setText(temp[0]); 
	   			}
	   		updateTime();
	   		updateDate();
	   		ptr.close();
	       }
	       
	       public void onNothingSelected(@SuppressWarnings("rawtypes") AdapterView parent) {
		    	//xHave an error box that says choose a category or maybe no category
		  
		    }
	}
	   		);
	       
	       PopulateSpinners(spinAdapterCat, new String[]{"Categories","CategoryName"});
	       populateTimers(spinAdapterEdit, new String[]{"Timers"}, null);
	       }
	
	//Populates Spinners with database
	public void PopulateSpinners(ArrayAdapter<String> s, String[] values){
		ptr = datab.Results(new String[]{values[0],values[1]},"spinner");
		
		if (ptr.moveToFirst())
			do {
				s.add(ptr.getString(ptr.getColumnIndex(values[1])));
			}while (ptr.moveToNext()); 
	}
	
public void populateTimers(ArrayAdapter<String> s, String[] values, String type){
		
		//Pull info form the database depending on which radiobutton checked
			ptr = datab.Results(new String[]{values[0]}, "none");

		
		Log.w("QUERY", "finihed");
		String spintext;
		if (ptr.moveToFirst())
			do {
					spintext = ptr.getString(ptr.getColumnIndex("TimerName")) + 
					" (" + ptr.getString(ptr.getColumnIndex("CategoryName")) + ")";
					//CheckTimerExpiration();				
				s.add(spintext);
			}while (ptr.moveToNext()); 
	}
	
	//This class handles the listener on a selected item from spinner
	public class SpinnerListener implements OnItemSelectedListener {
		  EditText s;
		  
		  SpinnerListener(EditText text){
			  s = text;
		  }
		  
		    public void onItemSelected(AdapterView<?> parent,
		        View view, int pos, long id) {
		    	switch(pos){
		    	//If user chooses nothing, it does nothing
		    	case 0:
		    		no_cat = true;
		    		pEditCatName.setText("");
		    		mRadCol.setVisibility(GONE);
		    		s.setVisibility(GONE);
		    		break;
		    	//If user chooses create new, EditBox pops up
		    	case 1:
		    		no_cat = false;
		    		pEditCatName.setText("");
		    		mRadCol.setVisibility(VISIBLE);
		    		s.setVisibility(VISIBLE);
		    		s.requestFocus();
		    		break;
		    	//If user choose a category from the database,	
		    	default:
		    		no_cat = false;
		    		pEditCatName.setText(spinAdapterCat.getItem(pos));
		    		mRadCol.setVisibility(GONE);
		    		s.setVisibility(GONE);
		    		break;
		    	}
		    }

		    public void onNothingSelected(@SuppressWarnings("rawtypes") AdapterView parent) {
		    	//Have an error box that says choose a category or maybe no category
		  
		    }
		}
	    

    /**************************************
	//			TIMER FUNCTIONS
     **************************************/

	public void AddTime_click(View view){
		showDialog(TIME_DIALOG_ID);
	}

	public void AddDate_click(View vew){
		showDialog(DATE_DIALOG_ID);
	}
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case TIME_DIALOG_ID:
            return new TimePickerDialog(this, mTimeSetListener, pHour, pMinute, false);
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this, mDateSetListener, pYear, pMonth, pDay);
        }
        return null;
    }
	
	//Sets private member when Set on time dialog is set
	private TimePickerDialog.OnTimeSetListener mTimeSetListener =
	    new TimePickerDialog.OnTimeSetListener() {
	        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	            pHour = hourOfDay;
	            pMinute = minute;
	            updateTime();
	        }
	    };
	    
	private DatePickerDialog.OnDateSetListener mDateSetListener =
         new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    pYear = year;
                    pMonth = monthOfYear;
                    pDay = dayOfMonth;
                    updateDate();
                }
            };

	//Formats the text to be displayed
	private void updateTime() {
		int displayHour = pHour;
		String occasion = "am";
		if(pHour==12)
			occasion="pm";
				
		if(pHour > 12){ 
			displayHour -=12;
			occasion = "pm";
		}
		if(pHour== 0)
			displayHour = 12;
		
		pTimeDisplay.setText(
            new StringBuilder()
            		.append("Timer Time: ")
                    .append(pad(displayHour)).append(":")
                    .append(pad(pMinute))
                    .append(occasion));
	}

	private void updateDate(){
		pDateDisplay.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
            		.append("Timer Date: ")
                    .append(pMonth + 1).append("-")
                    .append(pDay).append("-")
                    .append(pYear).append(" "));
	}
	
	private static String pad(int c) {
	    if (c >= 10)
	        return String.valueOf(c);
	    else
	        return "0" + String.valueOf(c);
	}
}
