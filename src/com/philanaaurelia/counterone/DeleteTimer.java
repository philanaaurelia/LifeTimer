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
    private static final int GONE = 8;
    private static final int VISIBLE = 0;
    private static final int INVISIBLE = 4;
    private static final int TIMER_DATE = 4;
    private static final int TIMER_TIME = 5;

    String timerToDelete=null,categoryToDelete=null;
    CheckBox deleteTimerOptionChkBox;
    LinearLayout mLLChkBox;
    RadioButton deleteCategoryRadioBtn, deleteTimerRadioBtn;
    Spinner categorySpinner, timerSpinner;
    Stack<Integer> expiredTimers = new Stack<Integer>();
    ArrayAdapter<String> spinAdapterCat, spinAdapterTim;

    SQLHelper datab;
    DialogHelper dialog;
    Cursor ptr;
    Boolean deleteAllTimers=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete);

        //Define general variables
        dialog = new DialogHelper(this);
        datab = new SQLHelper(this);
        datab.open();

        //Define general views
        mLLChkBox = (LinearLayout) findViewById(R.id.LL_CatChkBox);
        deleteTimerOptionChkBox = (CheckBox) findViewById(R.id.Check1);
        deleteCategoryRadioBtn=(RadioButton) findViewById(R.id.del1);
        deleteTimerRadioBtn=(RadioButton) findViewById(R.id.del2);
        categorySpinner = (Spinner) findViewById(R.id.delcatSpinner);
        timerSpinner = (Spinner) findViewById(R.id.deltimSpinner);
        
        deleteCategoryRadioBtn.setOnClickListener(radioListener);
        deleteTimerRadioBtn.setOnClickListener(radioListener);

        Button cancelBtn = (Button) findViewById(R.id.ButtonCancel1);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        deleteTimerOptionChkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(deleteTimerRadioBtn.isChecked()){
                    if(deleteTimerOptionChkBox.isChecked()){
                        timerSpinner.setVisibility(INVISIBLE);
                    }
                    else {
                        timerSpinner.setVisibility(VISIBLE);
                    }
                }
            }
        });

        //Call is made when Delete Button is clicked
        Button deleteBtn = (Button) findViewById(R.id.ButtonDelete1);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View view) {
                if (deleteCategoryRadioBtn.isChecked() == true){

                    if (categoryToDelete==null){
                        dialog.set("Hmm...", "You need to Choose a Category!", "Ok");
                        dialog.show();
                        return;
                    } else {
                        if(deleteTimerOptionChkBox.isChecked()){
                            datab.Execute("DELETE from Timers WHERE CategoryName='" + categoryToDelete + "'");
                        } else {
                            datab.Execute("UPDATE Timers SET CategoryName= null WHERE CategoryName='" + categoryToDelete + "'");
                        }
                        datab.Execute("DELETE from Categories WHERE CategoryName='" + categoryToDelete + "'");
                    }

                } else if (deleteTimerRadioBtn.isChecked() == true){

                    if (deleteAllTimers==true){
                        datab.Execute("DELETE FROM Timers");
                    }else if (timerToDelete==null && !deleteTimerOptionChkBox.isChecked()){
                        dialog.set("Hmm...", "You need to Choose a Timer!", "Ok");
                        dialog.show();
                        return;
                    } else {

                        if (!deleteTimerOptionChkBox.isChecked() && categoryToDelete.equals("null")){
                            Log.w("categoryToDelete","isnull");
                            datab.Execute("DELETE from Timers where TimerName='" + timerToDelete + "' and CategoryName IS NULL");
                        }else{
                            
                            if(deleteTimerOptionChkBox.isChecked()){
                                while(!expiredTimers.empty()){
                                    datab.Execute("DELETE from Timers where TimerID=" + expiredTimers.lastElement() );
                                    expiredTimers.pop();
                                }
                            } else {
                                datab.Execute("DELETE from Timers where TimerName='" + timerToDelete + "' and CategoryName='"	
                                        + categoryToDelete +"'");
                            }

                        }
                    }
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

    public void IncludeTimer_Click(View view){
        dialog = new DialogHelper(this);

        if (deleteTimerRadioBtn.isChecked()){
            dialog.set("Info", "If 'Expired Only' is checked, then all expired timers are deleted" +
                    " and the other timers will remain.", "Ok");
        } else {
            dialog.set("Info", "If 'Include Timers' is checked, then the category AND all of its timers are deleted." 
                    + " If it is unchecked, then the category will be deleted but NONE of its timers.", "Ok");
        }
        dialog.show();
    }

    private OnClickListener radioListener = new OnClickListener() {
        
        public void onClick(View view) {
            // Perform action on clicks
            if(view == deleteCategoryRadioBtn){
                mLLChkBox.setVisibility(VISIBLE);
                deleteTimerOptionChkBox.setText("Include Timer");
                ViewCategorySpinner();
            } else{
                mLLChkBox.setVisibility(VISIBLE);
                deleteTimerOptionChkBox.setText("Expired Only");
                ViewTimerSpinner();
            }
        }
    };

    public void ViewCategorySpinner(){
        categorySpinner.setVisibility(VISIBLE);
        timerSpinner.setVisibility(GONE);

        spinAdapterCat = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        spinAdapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAdapterCat.add("Choose Category");

        categorySpinner.setAdapter(spinAdapterCat);
        categorySpinner.setOnItemSelectedListener(new SpinnerListener());
        
        PopulateSpinners(spinAdapterCat, new String[]{"Categories","CategoryName"},"spinner");
    }

    public void ViewTimerSpinner(){
        timerSpinner.setVisibility(VISIBLE);
        categorySpinner.setVisibility(GONE);

        spinAdapterTim = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        spinAdapterTim.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAdapterTim.add("Choose Timer");
        spinAdapterTim.add("All timers");

        timerSpinner.setAdapter(spinAdapterTim);
        timerSpinner.setOnItemSelectedListener(new SpinnerListener());
        
        PopulateSpinners(spinAdapterTim, new String[]{"Timers"}, null);
    }

    //Populates Spinners with database
    public void PopulateSpinners(ArrayAdapter<String> s, String[] values, String type){
        
        if(deleteTimerRadioBtn.isChecked()==true)
            ptr = datab.Results(new String[]{values[0]}, "none");
        else
            ptr = datab.Results(new String[]{values[0],values[1]}, type);

        Log.w("QUERY", "finished");
        String spintext;
        
        if (ptr.moveToFirst()) {
            
            do {
                if(deleteTimerRadioBtn.isChecked() == true){
                    spintext = ptr.getString(ptr.getColumnIndex("TimerName")) + 
                    " (" + ptr.getString(ptr.getColumnIndex("CategoryName")) + ")";
                    CheckAndAddExpiredTimer();
                } else 
                    spintext = ptr.getString(ptr.getColumnIndex(values[1]));

                s.add(spintext);
            }while (ptr.moveToNext()); 
        }
    }

    public class SpinnerListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                View view, int pos, long id) {

            switch(pos){

            case 0:
                deleteAllTimers = false;
                Log.w("TIMER SPIN","Nothing chosen");
                break;
            case 1:
                deleteAllTimers=true;
                if(deleteCategoryRadioBtn.isChecked() == true)
                    categoryToDelete = parent.getItemAtPosition(pos).toString();
                break;
            default:
                deleteAllTimers=false;
                if(deleteCategoryRadioBtn.isChecked() == true) {
                    categoryToDelete = parent.getItemAtPosition(pos).toString();
                } else if(deleteTimerRadioBtn.isChecked() == true){
                    //Parses Selected item in to Name and Category
                    timerToDelete = parent.getItemAtPosition(pos).toString();
                    String Delim="[ ()]+";
                    String Parses[] = timerToDelete.split(Delim);
                    timerToDelete = Parses[0];
                    categoryToDelete = Parses[1];
                }
                break;
            }
        }

        public void onNothingSelected(@SuppressWarnings("rawtypes") AdapterView parent) {
            //TODO: Have an error box that says choose a category or maybe no category

        }
    }

    public void CheckAndAddExpiredTimer(){
        String day = ptr.getString(TIMER_DATE);
        String time =  ptr.getString(TIMER_TIME);
        int timerYear = Integer.parseInt(day.substring(0, 4));
        int timerMonth = Integer.parseInt(day.substring(4, 6));
        int timerDay = Integer.parseInt(day.substring(6, day.length()));
        int timerMinute = Integer.parseInt(time.substring(3, 5));
        int timerHour = Integer.parseInt(time.substring(1, 3));
        Time newTime = new Time();
        Time currentTime = new Time();

        currentTime.setToNow();
        newTime.set(0, timerMinute, timerHour, timerDay, timerMonth - 1, timerYear);

        //Formats times from  4 -> 0:04 or 52 -> 0:52
        if (time.length() == 1)
            time = "00" + time;
        else if (time.length() ==2)
            time = "0" + time;

        if(currentTime.after(newTime))
            expiredTimers.push(Integer.valueOf(ptr.getString(0)));
    }

}
