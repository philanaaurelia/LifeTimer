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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.DatePicker;
import android.database.Cursor;

public class AddTimer extends Activity {
    static final int TIME_DIALOG_ID = 0;
    static final int DATE_DIALOG_ID = 1;
    static final int GONE = 8;
    static final int VISIBLE = 0;

    EditText createCategoryEditText, createTimerNameEditText;
    TextView timerNameTextView, desiredTimeTextView, desiredDateTextView;
    Spinner timerCategorySpinner;
    RadioGroup categoryColorRadioGroup;
    ArrayAdapter<String> categoryAdapterArray;

    //variables for time and date settings
    Time desiredTime = new Time();
    Boolean isCategoryChosen = false; // no category is chosen as spinner option
    SQLHelper datab = new SQLHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        datab.open();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtimer);

        // capture our View elements
        createTimerNameEditText = (EditText) findViewById(R.id.AddTimerName);
        createCategoryEditText = (EditText) findViewById(R.id.AddCatName);
        desiredTimeTextView = (TextView) findViewById(R.id.timeDisplay);
        desiredDateTextView = (TextView) findViewById(R.id.dateDisplay);
        timerNameTextView = (TextView) findViewById(R.id.NameText);
        timerCategorySpinner = (Spinner) findViewById(R.id.catSpinner);
        categoryColorRadioGroup = (RadioGroup) findViewById(R.id.RadioColors);

        desiredTime.setToNow();
        updateTime();
        updateDate();
        SetUpSpinners();
        datab.Close();
    }

    public void SubmitTimer_Click(View view){
        DialogHelper dialog = new DialogHelper(this);
        Time currentTime = new Time();
        Integer inputDate = (1000000 * desiredTime.year) + desiredTime.monthDay + 10000 * (desiredTime.month + 1);
        Integer inputTime = 1000000 + (10000 * desiredTime.hour) + (100 * desiredTime.minute) + 60;
        Integer radioBtnColorID = categoryColorRadioGroup.getCheckedRadioButtonId();
        String timerName = createTimerNameEditText.getText().toString();
        String categoryName = createCategoryEditText.getText().toString(); //If cat was chosen from spinner, pAddCatNAme text was set to cat name
        //Log.w("RADIO BUTTON", String.valueOf(radioBtnColorID));

        datab.open();
        currentTime.setToNow();

        if (timerName.length() == 0){
            dialog.set("Uh oh!", "Your timer needs a name", "Ok");
            dialog.show();
            return;
        } else if (desiredTime.before(currentTime) == true){
            dialog.set("Oops!", "Wouldn't it be nice to turn back time? Please pick a future date.", "Ok");
            dialog.show();
            return;
        } else {
            //If it's a new category (Note both new category and no category return -1)
            if(categoryAdapterArray.getPosition(categoryName) < 0){
                if(isCategoryChosen == false)
                    datab.Insert(new String[]{"Categories", categoryName, returnColorString(radioBtnColorID)});
            }

            /*Log.w("TIMERNAME", timerName);
            Log.w("CAT NAME", categoryName);
            Log.w("DATE", String.valueOf(inputDate));
            Log.w("TIME", String.valueOf(inputTime));*/

            //Handles no color selection for category by user
            String categoryColorName = null;
            if(categoryName.length() == 0){
                categoryName = null;
                categoryColorName = returnColorString(radioBtnColorID);
            }

            //Log.w("CATEGORY NAME", categoryName);
            datab.Insert(new String[]{"Timers", categoryName, null, timerName, 
                  String.valueOf(inputDate), String.valueOf(inputTime), categoryColorName});
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
    public String returnColorString(int radioBtnID){
        RadioButton colorBtn;
        
        colorBtn = (RadioButton) categoryColorRadioGroup.findViewById(radioBtnID);
        
        return (String) colorBtn.getTag();
    }

    public void SetUpSpinners(){
        categoryAdapterArray = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item); 
        categoryAdapterArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryAdapterArray.add("Choose Category (opt)");
        categoryAdapterArray.add("Create New...");
        timerCategorySpinner.setAdapter(categoryAdapterArray); 
        timerCategorySpinner.setOnItemSelectedListener(new SpinnerListener());

        PopulateSpinner(categoryAdapterArray, new String[]{"Categories","CategoryName"});
    }

    //Populates Spinners with database values
    public void PopulateSpinner(ArrayAdapter<String> categoryNames, String[] values){
        Cursor ptr;
        ptr = datab.Results(new String[]{values[0],values[1]},"spinner");

        if (ptr.moveToFirst())
            do {
                categoryNames.add(ptr.getString(ptr.getColumnIndex(values[1])));
            } while (ptr.moveToNext()); 
    }

    //This class handles the listener on a selected item from spinner
    public class SpinnerListener implements OnItemSelectedListener {


        public void onItemSelected(AdapterView<?> parent,
                View view, int pos, long id) {
            switch(pos){
                //If user chooses nothing, it does nothing
                case 0:
                    isCategoryChosen = true;
                    createCategoryEditText.setText("");
                    categoryColorRadioGroup.setVisibility(GONE);
                    createCategoryEditText.setVisibility(GONE);
                    break;
                    //If user chooses create new, EditBox pops up
                case 1:
                    isCategoryChosen = false;
                    createCategoryEditText.setText("");
                    categoryColorRadioGroup.setVisibility(VISIBLE);
                    createCategoryEditText.setVisibility(VISIBLE);
                    createCategoryEditText.requestFocus();
                    break;
                    //If user choose a category from the database,	
                default:
                    isCategoryChosen = false;
                    createCategoryEditText.setText(categoryAdapterArray.getItem(pos));
                    categoryColorRadioGroup.setVisibility(GONE);
                    createCategoryEditText.setVisibility(GONE);
                    break;
            }
        }

        public void onNothingSelected(@SuppressWarnings("rawtypes") AdapterView parent) {
            //TODO: Have an error box that says choose a category or maybe no category

        }
    }


    /*************************************
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
                return new TimePickerDialog(this, mTimeSetListener, desiredTime.hour, desiredTime.minute, false);
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, desiredTime.year, desiredTime.month, desiredTime.monthDay);
        }
        return null;
    }

    //Sets private member when Set on time dialog is set
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            desiredTime.hour = hourOfDay;
            desiredTime.minute = minute;
            updateTime();
        }
    };

    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            desiredTime.year = year;
            desiredTime.month = monthOfYear;
            desiredTime.monthDay = dayOfMonth;
            updateDate();
        }
    };

    //Formats the text to be displayed
    private void updateTime() {
        int displayHour = desiredTime.hour;
        String occasion = "am";
        if(desiredTime.hour == 12)
            occasion = "pm";

        if(desiredTime.hour > 12){ 
            displayHour -= 12;
            occasion = "pm";
        }
        if(desiredTime.hour == 0)
            displayHour = 12;

        desiredTimeTextView.setText(
            new StringBuilder()
            .append("Time: ")
            .append(formatTime(displayHour)).append(":")
            .append(formatTime(desiredTime.minute))
            .append(occasion));
    }

    private void updateDate(){
        desiredDateTextView.setText(
            new StringBuilder()
            // Month is 0 based so add 1
            .append("Date: ")
            .append(desiredTime.month + 1).append("-")
            .append(desiredTime.monthDay).append("-")
            .append(desiredTime.year).append(" "));
    }

    private static String  formatTime(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}
