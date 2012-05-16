package com.philanaaurelia.counterone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class Preset extends Activity {

    EditText mPreHrs,mPreMin,mPreMth,mPreDay,mPreYr, mPreName;

    int min = 0, hr = 0, day = 0, mth = 0, yr = 0;
    int[] month = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int FEBRUARY = 1;

    DialogHelper dialog;
    SQLHelper datab = new SQLHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preset);


        mPreHrs = (EditText) findViewById(R.id.presetHrs);
        mPreMin = (EditText) findViewById(R.id.presetMin);
        mPreDay = (EditText) findViewById(R.id.presetDay);
        mPreMth = (EditText) findViewById(R.id.presetMonth);
        mPreYr = (EditText) findViewById(R.id.presetYear);
        mPreName = (EditText) findViewById(R.id.presetName);
    }

    public void Submit_click(View view){
        dialog = new DialogHelper(this);
        datab.open();

        //Using try to determine if input are actually integers
        try{ 

            //All of these are checks to make sure timer has correct name/correct timer format	
            if(mPreName.getText().length()==0){
                dialog.set("Uh oh!", "Your preset timer needs a name", "Ok");
                dialog.show();
                return;
            }
            if(mPreMin.getText().length()!=0){
                min = Integer.parseInt(mPreMin.getText().toString());
                if(min <= 0 || min > 59){
                    dialog.set("Oops","Please set minute interval between 0 and 60.","Ok");
                    dialog.show();
                    return;
                }
            }

            if(mPreHrs.getText().length()!=0){
                hr = Integer.parseInt(mPreHrs.getText().toString());
                if(hr <= 0 || hr > 23){
                    dialog.set("Oops","Please set hour interval between 0 and 24.","Ok");
                    dialog.show();
                    return;
                }
            }

            if(mPreDay.getText().length()!=0){
                day = Integer.parseInt(mPreDay.getText().toString());
                if(day <= 0 || day > 31){
                    dialog.set("Oops","Please set month interval between 0 and 32.","Ok");
                    dialog.show();
                    return;
                }
            }

            if(mPreMth.getText().length()!=0){
                mth = Integer.parseInt(mPreMth.getText().toString());
                if(mth <= 0 || mth > 11){
                    dialog.set("Oops","Please set month interval between 0 and 11.","Ok");
                    dialog.show();
                    return;
                }
            }

            if(mPreYr.getText().length()!=0){
                yr = Integer.parseInt(mPreYr.getText().toString());
                if(yr <= 0 || yr > 5){
                    dialog.set("Hmm...","Now why on earth would you need a timer for more than 5 years?","Ok");
                    dialog.show();
                    return;
                }
            }

            //If error is thrown, format is incorrect
        }catch(NumberFormatException e){
            Log.w("SUBmIT","exception");
            dialog.set("Oops","Please enter a numeric value.","Ok");
            dialog.show();
            return;
        }

        //If user inputs nothing, alert him
        if(min==0 && hr==0 && day==0 && mth==0 && yr==0){
            dialog.set("Oops","You preset has no values!","Ok");
            dialog.show();
            return;
        }

        //Needed to set timer to the correct format
        Time current = new Time();
        int extra_day = 0;
        current.setToNow();

        //min += current.minute;

        if(min + current.minute > 59){
            current.minute = current.minute + min - 60;
            current.hour += 1;
        } else
            current.minute += min;

        if(hr + current.hour > 23){
            current.hour = current.hour + hr - 24;
            current.monthDay += 1;		
        } else
            current.hour += hr;

        // For leap years
        if(((current.year - 2000) % 4) ==0 && current.month == FEBRUARY)
            extra_day = 1;

        if(day > (month[current.month] + extra_day)){
            day -= month[current.month] + extra_day;
            current.monthDay = day;
            current.month += 1;
        } else
            current.monthDay += day;

        if(mth + current.month > 11){
            current.month = current.month + mth - 12;
            current.year +=1;
        } else
            current.month += mth;

        current.year += yr;
        Log.w("MONTH", Integer.toString(current.month));

        Integer inputDate = 1000000*current.year + current.monthDay + 10000*(current.month+1);
        Integer inputTime = 1000000 + 10000*current.hour + 100*current.minute + current.second;//One specifies that this is a preset

        datab.Insert(new String[]{"Timers",null, null, mPreName.getText().toString(), 
                String.valueOf(inputDate), String.valueOf(inputTime), null});
        Log.w("PRESET TIMER","Inserted to database");	
        datab.Close();


        //Load ViewTimers page after timer is created
        Intent myIntent = new Intent(view.getContext(), ViewMyTimer.class);
        myIntent.putExtra("tableType", "ascending");
        startActivityForResult(myIntent, 0);

    }

}
