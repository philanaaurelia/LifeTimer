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
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.DatePicker;
import java.util.Calendar;
import android.database.Cursor;


public class AddTimer extends Activity {
	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;

	//Declare all the view variables
	TextView pDBdisplay, pNameText, pTimeDisplay, pDateDisplay,pRevString;
	Button goback, pAddTime, pAddDate, pSubmitDate, pReview;
	EditText pAddCatName, pAddSubCatName, pAddTimerName;
	Spinner pCatSpin; // pSubCatSpin;
	ArrayAdapter<String> spinAdapterCat;// spinAdapterSubCat;
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
  
	
  @Override
	public void onCreate(Bundle savedInstanceState) {
	  datab.open();
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.addtimer);
    	
    	// get the current time and set variables for Time Display
        final Calendar c = Calendar.getInstance();
        pHour = c.get(Calendar.HOUR_OF_DAY);
        pMinute = c.get(Calendar.MINUTE);
        pYear = c.get(Calendar.YEAR);
        pMonth = c.get(Calendar.MONTH);
        pDay = c.get(Calendar.DAY_OF_MONTH);

       // capture our View elements
       pTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
       pAddTime = (Button) findViewById(R.id.addTime);
       pDateDisplay = (TextView) findViewById(R.id.dateDisplay);
       pAddDate = (Button) findViewById(R.id.addDate);
       pCatSpin = (Spinner) findViewById(R.id.catSpinner);
       //pSubCatSpin = (Spinner) findViewById(R.id.subcatSpinner);
       pNameText = (TextView) findViewById(R.id.NameText);
       pAddTimerName = (EditText) findViewById(R.id.AddTimerName);
       pAddCatName = (EditText) findViewById(R.id.AddCatName);
       //pAddSubCatName = (EditText) findViewById(R.id.AddSubCatName);
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
       datab.Close();
	}
	
  	//This function submits time, dates, timer name, category names
  	//etc to the database
	public void Submit_Click(View view){
		DialogHelper dialog = new DialogHelper(this);
		datab.open();
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
		String timer_name =pAddTimerName.getText().toString();
		String cat_name = pAddCatName.getText().toString(); //If cat was chosen from spinner, pAddCatNAme text was set to cat name
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
			
			/*Log.w("TIMERNAME", timer_name);
			Log.w("CAT NAME", cat_name);
			Log.w("DATE", String.valueOf(inputDate));
			Log.w("TIME", String.valueOf(inputTime));*/
			
			
			//It sets the color of timer if no category added
			//Also sets the cat_name to null
			String timer_color= null;
			if(cat_name.length() == 0){
				cat_name = null;
				timer_color= Color();
			}
			
			//Insert into timers
			//Log.w("CATEGORY NAME", cat_name);
			datab.Insert(new String[]{"Timers",cat_name, null, timer_name, 
					String.valueOf(inputDate), String.valueOf(inputTime), timer_color});
			Log.w("TIMER","Inserted to database");	
			datab.Close();
		
			//Load ViewTimers page after timer is created
			Intent myIntent = new Intent(view.getContext(), ViewMyTimer.class);

			myIntent.putExtra("tableType", "ascending");
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

	 
    /*************************************
	//
	//			SPINNER FUNCTIONS
	//
     **************************************/

	public void SetUpSpinners(){
		 spinAdapterCat = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item); 
	      // spinAdapterSubCat = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item); 
	       
	       spinAdapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	       //spinAdapterSubCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	       
	       //pSubCatSpin.setAdapter(spinAdapterSubCat);
	       pCatSpin.setAdapter(spinAdapterCat); 
	       
	       spinAdapterCat.add("Choose Category (opt)");
	       spinAdapterCat.add("Create New...");
	       
	       //spinAdapterSubCat.add("Choose Sub-Category (opt)");
	       //spinAdapterSubCat.add("Create New...");
	      
	       pCatSpin.setOnItemSelectedListener(new SpinnerListener(pAddCatName));
	      // pSubCatSpin.setOnItemSelectedListener(new SpinnerListener(pAddSubCatName));
	       
	       PopulateSpinners(spinAdapterCat, new String[]{"Categories","CategoryName"});
	       //PopulateSpinners(spinAdapterSubCat,  new String[]{"Categories","CategoryName"});
	}
	
	//Populates Spinners with database
	public void PopulateSpinners(ArrayAdapter<String> s, String[] values){
		ptr = datab.Results(new String[]{values[0],values[1]},"spinner");
		
		
		if (ptr.moveToFirst())
			do {
				s.add(ptr.getString(ptr.getColumnIndex(values[1])));
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
		    		pAddCatName.setText("");
		    		mRadCol.setVisibility(GONE);
		    		s.setVisibility(GONE);
		    		break;
		    	//If user chooses create new, EditBox pops up
		    	case 1:
		    		no_cat = false;
		    		pAddCatName.setText("");
		    		mRadCol.setVisibility(VISIBLE);
		    		s.setVisibility(VISIBLE);
		    		s.requestFocus();
		    		break;
		    	//If user choose a category from the database,	
		    	default:
		    		no_cat = false;
		    		pAddCatName.setText(spinAdapterCat.getItem(pos));
		    		mRadCol.setVisibility(GONE);
		    		s.setVisibility(GONE);
		    		break;
		    	}
		    }

		    public void onNothingSelected(@SuppressWarnings("rawtypes") AdapterView parent) {
		    	//Have an error box that says choose a category or maybe no category
		  
		    }
		}
	    

    /*************************************
	//
	//			TIMER FUNCTIONS
	//
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
            		.append("Time: ")
                    .append(pad(displayHour)).append(":")
                    .append(pad(pMinute))
                    .append(occasion));
	}

	private void updateDate(){
		pDateDisplay.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
            		.append("Date: ")
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