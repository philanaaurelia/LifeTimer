/* 
 * Written By Philana Benton
 * Spelman College/University of Michigan
 * Luke 1:37
 */

package com.philanaaurelia.counterone;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import java.util.Stack;

public class DeleteTimer extends Activity {
	String del_timer=null,del_cat=null;
	CheckBox mChkBox;
	LinearLayout mLLChkBox;
	RadioButton mDelCat;
	RadioButton mDelTim;
	Spinner pCatSpin, pTimSpin;
	Stack<Integer> expired_timers = new Stack<Integer>();
	ArrayAdapter<String> spinAdapterCat, spinAdapterTim;
	
	SQLHelper datab = new SQLHelper(this);
	DialogHelper dialog;
	Cursor ptr;
	Boolean delete_all_timers=false;
	Boolean delete_null_timers=false;

	 
	private static final int GONE = 8;
	private static final int VISIBLE = 0;
	private static final int INVISIBLE = 4;
	private static final int TIMER_DATE = 4;
	private static final int TIMER_TIME = 5;
	 
	
	 
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.delete);
    	
    	//Define general variables
    	dialog = new DialogHelper(this);
    	datab.open();
    	
    	//Define general views
    	mLLChkBox = (LinearLayout) findViewById(R.id.LL_CatChkBox);
    	mChkBox = (CheckBox)findViewById(R.id.Check1);
    	mDelCat=(RadioButton)findViewById(R.id.del1);
    	mDelTim=(RadioButton)findViewById(R.id.del2);
    	mDelCat.setOnClickListener(radio_listener);
    	mDelTim.setOnClickListener(radio_listener);
    	pCatSpin = (Spinner) findViewById(R.id.delcatSpinner);
        pTimSpin = (Spinner) findViewById(R.id.deltimSpinner);
        
    	
        //Call is made when Button is clicked
        //If Done Button is clicked, close the activity
        Button mDone = (Button) findViewById(R.id.ButtonDone1);
        mDone.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent();
        		setResult(RESULT_OK, intent);
        		finish();
        	}
        });
        
        //Function is called when Box is Checked
        //If Timer Radio Button , Checked box hides spinner
        mChkBox.setOnClickListener(new View.OnClickListener() {
        	 public void onClick(View view) {
                 if(mDelTim.isChecked()){
                	 if(mChkBox.isChecked())
                		 pTimSpin.setVisibility(INVISIBLE);
                 else 
                	 pTimSpin.setVisibility(VISIBLE);
                 }
        	 }

        });
    
        //Call is made when Delete Button is clicked
        Button del = (Button) findViewById(R.id.ButtonDelete1);
        del.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        	
        		//if Category Radio Button is Checked
        		if(mDelCat.isChecked() == true){
        		
        			//If no category is Chosen
        			if(del_cat==null){
        				dialog.set("Hmm...", "You need to Choose a Category!", "Ok");
        				dialog.show();
					return;
        			} else{
        			
        				//If all timers AND category need to be deleted
        				if(mChkBox.isChecked()) 
        					datab.Execute("DELETE from Timers WHERE CategoryName='" + del_cat + "'");
        				//if category only needs to be deleted
        				else 
        					datab.Execute("UPDATE Timers SET CategoryName= null WHERE CategoryName='" + del_cat + "'");

        				//Delete the Category regardless of decision
        				datab.Execute("DELETE from Categories WHERE CategoryName='" + del_cat + "'");
        			}
        		
        		//If delete timer is checked	
        		} else if(mDelTim.isChecked() == true){
        			
        			if(delete_all_timers==true){
        				datab.Execute("DELETE FROM Timers");
        			//If no timer was selected from Spinner
        			}else if(del_timer==null && !mChkBox.isChecked()){
        				dialog.set("Hmm...", "You need to Choose a Timer!", "Ok");
        				dialog.show();
        				return;
        			} else{
        				
        				//If timer has no category
        				if(!mChkBox.isChecked() && del_cat.equals("null")){
        					Log.w("DEL_CAT","isnull");
        					datab.Execute("DELETE from Timers where TimerName='" + del_timer + "' and CategoryName IS NULL");
        				
        				//if category does have a timer
        				}else{
        					
        					//If delete all timers is sleceted
        					if(mChkBox.isChecked()){
        						while(!expired_timers.empty()){
        							datab.Execute("DELETE from Timers where TimerID=" + expired_timers.lastElement() );
        							expired_timers.pop();
        						}
        					
        						//If specific timer is selected
        					}else{
        						datab.Execute("DELETE from Timers where TimerName='" + del_timer + "' and CategoryName='"	
        							+ del_cat +"'");
        					}
        				}
        			}
        			
        		//If Category and Timer button are not selected, show dialog	
       		 	} else {
       		 		Log.w("RADIO CHECKED", "Nothing");
       		 		dialog.set("Hmm...", "Click 'Done' to exit or choose a category or timer to delete.", "Ok");
					dialog.show();
					return;
       		 }
        	
        	Intent myIntent = new Intent(view.getContext(), ViewMyTimer.class);
        	myIntent.putExtra("tableType", "ascending");
        	startActivityForResult(myIntent, 0);
       			 
        }

    });
    
	}
	/****************************
	 *  BUTTON CLICK FUNCTIONS
	 ****************************/
	public void IncludeTimer_Click(View view){
		dialog = new DialogHelper(this);
		
		if(mDelTim.isChecked())
			dialog.set("Info", "If 'Expired Only' is checked, then all expired timers are deleted" +
					" and the other timers will remain.", "Ok");
		else
			dialog.set("Info", "If 'Include Timers' is checked, then the category AND all of its timers are deleted." 
				+ " If it is unchecked, then the category will be deleted but NONE of its timers.", "Ok");
		dialog.show();
	}
	
	
	/********************
	 * RADIO BUTTON
	 *******************/
	private OnClickListener radio_listener = new OnClickListener() {
	    public void onClick(View v) {
	        // Perform action on clicks
	       if(v == mDelCat){
	    	   mLLChkBox.setVisibility(VISIBLE);
	    	   mChkBox.setText("Include Timer");
	    	   ViewCategorySpinner();
	       } else{
	    	   mLLChkBox.setVisibility(VISIBLE);
	    	   mChkBox.setText("Expired Only");
	    	   ViewTimerSpinner();
	       }
	    }
	    	   
	};

	/*******************
	 * SPINNERS
	 *******************/
	public void ViewCategorySpinner(){

		//hides the Timer spinner and makes category spinner visible
		pCatSpin.setVisibility(VISIBLE);
		pTimSpin.setVisibility(GONE);
		 spinAdapterCat = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
		 spinAdapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 
		 ///
		 pCatSpin.setAdapter(spinAdapterCat);
		 spinAdapterCat.add("Choose Category");
		 pCatSpin.setOnItemSelectedListener(new SpinnerListener(mDelCat));
		 PopulateSpinners(spinAdapterCat, new String[]{"Categories","CategoryName"},"spinner");
	}
	
	public void ViewTimerSpinner(){
		pTimSpin.setVisibility(VISIBLE);
		pCatSpin.setVisibility(GONE);
		spinAdapterTim = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
		spinAdapterTim.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 
		pTimSpin.setAdapter(spinAdapterTim);
		spinAdapterTim.add("Choose Timer");
		spinAdapterTim.add("All timers");
		pTimSpin.setOnItemSelectedListener(new SpinnerListener(mDelTim));
		PopulateSpinners(spinAdapterTim, new String[]{"Timers"}, null);
	}
	
	
	//Populates Spinners with database
	public void PopulateSpinners(ArrayAdapter<String> s, String[] values, String type){
		
		//Pull info form the database depending on which radiobutton checked
		if(mDelTim.isChecked()==true)
			ptr = datab.Results(new String[]{values[0]}, "none");
		else
			ptr = datab.Results(new String[]{values[0],values[1]}, type);
		
		Log.w("QUERY", "finihed");
		String spintext;
		if (ptr.moveToFirst())
			do {
			
				if(mDelTim.isChecked() == true){
					spintext = ptr.getString(ptr.getColumnIndex("TimerName")) + 
					" (" + ptr.getString(ptr.getColumnIndex("CategoryName")) + ")";
					CheckTimerExpiration();
				} else 
					spintext = ptr.getString(ptr.getColumnIndex(values[1]));
				
				s.add(spintext);
			}while (ptr.moveToNext()); 
	}
	
	 public class SpinnerListener implements OnItemSelectedListener {
		
		  RadioButton s;
		  SpinnerListener(RadioButton rb){
			  s = rb;
		  }
		  
		    public void onItemSelected(AdapterView<?> parent,
		        View view, int pos, long id) {
		    	 
		    	switch(pos){
		    	//If user chooses nothing, it does nothing
		    	case 0:
		    		delete_all_timers = false;
		    		Log.w("TIMER SPIN","Nothing chosen");
		    		break;
		    		
		    	case 1:
		    		delete_all_timers=true;
		    		if(mDelCat.isChecked() == true)
		    			del_cat = parent.getItemAtPosition(pos).toString();
		    		break;
		    	default:
		    		delete_all_timers=false;
		    		if(mDelCat.isChecked() == true)
		    			del_cat = parent.getItemAtPosition(pos).toString();
		    		else if(mDelTim.isChecked() == true){
		    			//Parses Selected item in to Name and Category
		    			del_timer = parent.getItemAtPosition(pos).toString();
		    			String Delim="[ ()]+";
		    			String Parses[] = del_timer.split(Delim);
		    			del_timer = Parses[0];
		    			del_cat = Parses[1];
		    		}
		    		break;
		    	}
		    }

		    public void onNothingSelected(@SuppressWarnings("rawtypes") AdapterView parent) {
		    	//Have an error box that says choose a category or maybe no category
		  
		    }
		}
	 
	 public void CheckTimerExpiration(){
		//Number conversion
		 String day = ptr.getString(TIMER_DATE);
		 String time =  ptr.getString(TIMER_TIME);
		 
		 //Formats times from  4 -> 0:04 or 52 -> 0:52 so 
		 //time can be parsed correctly
		 if(time.length() == 1)
			 time = "00" + time;
		 else  if(time.length() ==2)
			 time = "0" + time;
		 
		 int timer_year = Integer.parseInt(day.substring(0, 4));
		 int timer_month = Integer.parseInt(day.substring(4, 6));
		 int timer_day = Integer.parseInt(day.substring(6, day.length()));
		 
		 int timer_min = Integer.parseInt(time.substring(3, 5));
		 int timer_hour = Integer.parseInt(time.substring(1, 3));
		 
		 // These times are needed to determine if loaded timers have
		 // timers have already expired
		 Time current = new Time();
		 current.setToNow();
		 
		 Time newtimer = new Time();
		 newtimer.set(0, timer_min, timer_hour, timer_day, timer_month - 1, timer_year);
		 
		 //If timer is deleted add to stack of expired timers for "DELETE ALL EXPIRED TIMERS" option
		 if(current.after(newtimer))
			 expired_timers.push(Integer.valueOf(ptr.getString(0)));
	 }

}
