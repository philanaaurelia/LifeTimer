/* 
 * Written By Philana Benton
 * Spelman College/University of Michigan
 * Luke 1:37
 */

package com.philanaaurelia.counterone;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;
import android.app.Activity;
import android.app.AlarmManager;

public class Table extends Activity{

    //These variables are for keeping track of days of months
    private static final int JANUARY = 31, MARCH = 31, JULY = 31, AUGUST = 31, OCTOBER = 31, DECEMBER = 31;
    private static final int SEPTEMBER = 30, APRIL = 30, MAY = 31, JUNE = 30, NOVEMBER = 30;
    private static int FEBRUARY; //Changes depending on leapyear

    //These definitions are the column numbers of a query
    static final int CAT_NAME = 1;
    //private static final int SUB_NAME = 2;
    static final int TIMER_NAME = 3;
    static final int CAT_COLOR = 9; 
    static final int TIMER_DATE = 4;
    static final int TIMER_TIME = 5;
    //private static final int TIMER_COLOR = 6;


    static final Integer CENTER = 17;
    static final Integer RIGHT = 5;

    //These variables calculate the time left before timer ends
    int days =0;
    int hrs = 0;
    int min = 0;
    int sec = 0;
    Boolean is_preset = false;

    //These variables are used to create the timers.
    Time newtimer;
    Vibrator vibr;
    TableRow tr;
    TextView tr_days, tr_hrs, tr_min, tr_sec;
    LinearLayout mLayout;
    Cursor ptr;
    Count count = null;
    Context ctx;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w("VIBRATOR","NOW");

    }

    public Table(Context c, Vibrator v, AlarmManager am, Cursor cursor){
        vibr = v;
        ptr = cursor;
        ctx = c;

    }

    //This class is in charge of dynamically showing the countdown of the timer
    public class Count extends CountDownTimer {
        Boolean timer_done= false;


        public Count(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

            Log.w("Count","Class Started");
        }

        //Tells timer to restart seconds to 60 if timer is not done, else finish
        @Override
        public void onFinish() {
            if(timer_done==false)
                start();
            else{
                tr_sec.setText("0");
                tr_min.setText("0");
                tr_hrs.setText("0");
                tr_days.setText("0");

                Vibrate();
                //long[] pattern = { 500, 300 };
                //v.vibrate(pattern, 5);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {

            //Tells onFinish function that timer is finished if 00:00:00:01 occurs
            if(Integer.valueOf((String)tr_sec.getText()) <= 20 && tr_min.getText().equals("0") &&
                    tr_hrs.getText().equals("0") && tr_days.getText().equals("0")){
                timer_done = true;
            }

            tr_sec.setText(Integer.toString(Integer.valueOf((String)tr_sec.getText()) - 1 ));
            if(tr_sec.getText().equals("-1")){
                tr_min.setText(Integer.toString(Integer.valueOf((String)tr_min.getText()) - 1 ));
                tr_sec.setText("59");
                if(tr_min.getText().equals("-1")){
                    tr_hrs.setText(Integer.toString(Integer.valueOf((String)tr_hrs.getText()) - 1 ));
                    tr_min.setText("59");
                    if(tr_hrs.getText().equals("-1")){
                        tr_days.setText(Integer.toString(Integer.valueOf((String)tr_days.getText()) - 1 ));
                        tr_hrs.setText("23");
                    }	
                }
            }
        }

    }


    //Creates a timer table and populates it
    public TableLayout ReturnTimer(){
        Log.w("TIMER","Creating");

        //Number conversion
        String day = ptr.getString(TIMER_DATE);
        String time =  ptr.getString(TIMER_TIME);


        Log.w("TIME",time);

        //Parses days from 20122509 to 09 -> 25 >2012 and time from 1124 to 11 -> 24
        //Note that the times are backward as a way to load them in ascending order
        int timer_year = Integer.parseInt(day.substring(0, 4));
        int timer_month = Integer.parseInt(day.substring(4, 6));
        int timer_day = Integer.parseInt(day.substring(6, day.length()));

        int timer_min = Integer.parseInt(time.substring(3, 5));
        int timer_hour = Integer.parseInt(time.substring(1, 3));
        int timer_sec =  Integer.parseInt(time.substring(5, time.length()));

        if(time.charAt(0) == '1')
            is_preset = true;
        else
            is_preset = false;
        /*Log.w("YEAR",Integer.toString(timer_year));
        Log.w("DAY",Integer.toString(timer_day));
        Log.w("MONTH",Integer.toString(timer_month));
        Log.w("MIN", time.substring(time.length() - 2, time.length()));
        Log.w("HOUR",time.substring(0, time.length() - 2));*/

        // These times are needed to determine if loaded timers have
        // timers have already expired
        Time current = new Time();
        current.setToNow();

        newtimer = new Time();
        int stop_sec = 0;
        if(timer_sec != 60)
            stop_sec =  timer_sec;
        newtimer.set(stop_sec, timer_min, timer_hour, timer_day, timer_month - 1, timer_year);

        //same hour
        if(current.monthDay == timer_day && current.year == timer_year && current.hour ==  timer_hour && current.month == timer_month - 1){
            if(newtimer.after(current)){
                min += timer_min - (current.minute + 1);
                if(min < 0)
                    min = 0;

                if(timer_sec == 60)
                    sec = 60 - current.second;
                else if(current.second >= timer_sec){
                    Log.w("TIMER SEC", Integer.toString(timer_sec));
                    Log.w("CURRENT SEC", Integer.toString(current.second));
                    sec = 60 - current.second;
                    sec += timer_sec;
                }else 
                    sec = timer_sec - current.second;

                Log.w("SECOND", Integer.toString(sec));
                count = new Count(sec * 1000, 1000);
            }


        } else {
            if(newtimer.after(current)){

                min += 60 - (current.minute + 1);
                min += timer_min;

                if( min > 59){
                    min -= 60;
                    hrs +=1;
                }


                if(timer_sec == 60)
                    sec = 60 - current.second;
                else if(current.second >= timer_sec){
                    Log.w("TIMER SEC", Integer.toString(timer_sec));
                    Log.w("CURRENT SEC", Integer.toString(current.second));
                    sec = 60 - current.second;
                    sec += timer_sec;
                }else 
                    sec = timer_sec - current.second;

                Log.w("SECOND", Integer.toString(sec));
                count = new Count(sec * 1000, 1000);

                if(current.monthDay == timer_day && current.year == timer_year && current.month == timer_month - 1){//same day
                    for(; timer_hour - 1 > current.hour; timer_hour--)
                        hrs +=1;
                } else {
                    hrs += 24 - (current.hour + 1);
                    hrs += timer_hour;

                    if( hrs > 23){
                        hrs -= 24;
                        days +=1;
                    }

                    if(current.year == timer_year && current.month == timer_month-1){ //same month
                        for(; timer_day -1 > current.monthDay; timer_day--)
                            days += 1;
                    } else{
                        int month = DaysInMonth(current.month, current.month + 1);
                        days += month - (current.monthDay + 1);
                        days += timer_day;

                        //Account for Leap Year
                        if(((current.year - 2000) % 4) ==0)
                            FEBRUARY = 29;
                        else 
                            FEBRUARY = 28;

                        if(current.year == timer_year){
                            days += DaysInMonth(current.month + 1, timer_month - 1);
                        } else {
                            for(; timer_year - 1 > current.year; timer_year--){
                                if((timer_year - 2000) % 4 == 0)
                                    days += 366;
                                else 
                                    days += 365;
                            }
                            days += DaysInMonth(current.month + 1, 12);// 12 - (month -2) for days left
                            days += DaysInMonth(0,timer_month-1); //days left after month
                        }
                    }
                }



                //Daylight Saving Time
                DST dst_check = new DST(current.year, timer_year);
                int curr = dst_check.InDST(current.month, current.hour, current.monthDay);
                int timer = dst_check.InDST(timer_month, timer_hour, timer_day);

                if(curr == -1 && timer == 0)
                    hrs -= 1;
                else if(curr== 0 && timer == 1)
                    hrs += 1;
                else if(curr == 1 && timer == 0)
                    hrs -= 1;
            }
        }

        if(hrs < 0){
            hrs = 0;
            days -= 1;
        }
        if(days < 0){
            days = 0;
        }

        // Log.w("CAT NAME",ptr.getString(CAT_NAME)); DO NOT UNCOMMENT, WILL BREAK IF NULL

        String [][] row = {{ptr.getString(TIMER_NAME),"Days", "Hrs", "Min", "Sec"},
                {ptr.getString(CAT_NAME),Integer.toString(days), Integer.toString(hrs), Integer.toString(min), Integer.toString(sec)}};

        /* SQLHelper db_color = new SQLHelper(this);*/
        String cat_color = "none";
        if(ptr.getString(CAT_COLOR)!=null){


            cat_color = ptr.getString(CAT_COLOR);

        }


        TableLayout table = new TableLayout(ctx);
        table.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        table.setBackgroundResource(color(cat_color)); //color(c.getString(CATEGORY_COLOR))

        Log.w("TABLE","is created");

        tr = new TableRow(ctx);
        tr.setPadding(5, 5, 5, 0); 



        Log.w("R","is created");
        //Add texts to the rows

        for(int m =0; m < 5 ;m++){
            TextView b = new TextView(ctx);
            b.setText(row[0][m]);

            if(m==0){
                b.setWidth(180);
                b.setTypeface(null, 1);
            }else{
                b.setGravity(CENTER);
                b.setWidth(32) ;
            }

            // b.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            /* Add Button to row. */
            tr.addView(b);
        } // add texts in rows



        /* Add row to TableLayout. */
        table.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        tr = new TableRow(ctx);
        tr.setPadding(5,0,5,0);

        TextView b = new TextView(ctx);
        b.setText(row[1][0]);
        b.setWidth(180);
        //b.setTypeface(null, 1);
        tr.addView(b);

        tr_days = new TextView(ctx);
        tr_days.setGravity(CENTER);
        tr_days.setWidth(32) ;
        tr_days.setText(Integer.toString(days));
        tr.addView(tr_days);

        tr_hrs = new TextView(ctx);
        tr_hrs.setGravity(CENTER);
        tr_hrs.setWidth(32) ;
        tr_hrs.setText(Integer.toString(hrs));
        tr.addView(tr_hrs);

        tr_min = new TextView(ctx);
        tr_min.setGravity(CENTER);
        tr_min.setWidth(32) ;
        tr_min.setText(Integer.toString(min));
        tr.addView(tr_min);

        tr_sec = new TextView(ctx);
        tr_sec.setGravity(CENTER);
        tr_sec.setWidth(32) ;
        tr_sec.setText(Integer.toString(sec));
        tr.addView(tr_sec);
        if(count!=null)
            count.start();

        table.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));



        TextView k = new TextView(ctx);
        String exp = "Expires on: " + (newtimer.month + 1) + "/" + newtimer.monthDay + "/" + newtimer.year + " at ";

        String occasion = "am";

        if(newtimer.hour==12)
            occasion = "pm";
        if(newtimer.hour > 12){ 
            newtimer.hour -=12;
            occasion = "pm";
        }
        if(newtimer.hour== 0)
            newtimer.hour = 12;

        String tempmin;
        if(newtimer.minute  < 10)
            tempmin = "0" + newtimer.minute ;
        else
            tempmin = Integer.toString(newtimer.minute );

        exp += newtimer.hour +":" + tempmin + " " + occasion;
        k.setText(exp);
        k.setGravity(5);
        k.setTextSize(12);
        k.setPadding(0, 0, 4, 2);

        table.addView(k,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));


        Log.w("TABLE ROWS","added");
        /*	TableRow tr = new TableRow(ctx);
        tr.setPadding(0,0,0,2);

        //********************* 
        //Edit and Delete Row
        //*********************
        for(int y =0; y < 3; y++)
            tr.addView(new View(ctx));

            TextView l = new TextView(ctx);
            l.setGravity(CENTER);
            l.setText("Edit  |");
            l.setTextSize(9);
            tr.addView(l);

            TextView g = new TextView(ctx);
            g.setText("Delete");
            g.setGravity(CENTER);
            g.setTextSize(9);
            tr.addView(g);

            table.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));*/
            //table.setOrientation(orientation)
            //super.setContentView(table);
        return table;
    }

    private int color(String clr){
        Log.w("CATEGORY COLOR","chosen");
        if(clr.equals("pink"))
            return R.drawable.pink_bar;
        else if(clr.equals("red"))
            return R.drawable.red_bar;
        else if(clr.equals("yellow"))
            return R.drawable.yellow_bar;
        else if(clr.equals("limegreen"))
            return R.drawable.limegreen_bar;
        else if(clr.equals("orange"))
            return R.drawable.orange_bar;
        else if(clr.equals("plum"))
            return R.drawable.plum_bar;
        else if(clr.equals("purple"))
            return R.drawable.purple_bar;
        else if(clr.equals("blue"))
            return R.drawable.blue_bar;
        else if(clr.equals("turquoise"))
            return R.drawable.turquoise_bar;
        else if(clr.equals("slateblue"))
            return R.drawable.slateblue_bar;
        else if(clr.equals("gray"))
            return R.drawable.gray_bar;
        else if(clr.equals("bluegreen"))
            return R.drawable.bluegreen_bar;
        else 
            return R.drawable.black_bar;
    }

    private int DaysInMonth(int current, int timer){
        int days_left=0;


        for(; timer > current; timer--){
            switch(timer - 1){
            case 0: days_left += JANUARY;
            break;
            case 1: days_left +=FEBRUARY;
            break;
            case 2: days_left += MARCH;
            break;
            case 3: days_left += APRIL;
            break;
            case 4: days_left += MAY;
            break;
            case 5: days_left +=JUNE;
            break;
            case 6: days_left +=JULY;
            break;
            case 7: days_left += AUGUST;
            break;
            case 8: days_left +=SEPTEMBER;
            break;
            case 9: days_left += OCTOBER;
            break;
            case 10: days_left += NOVEMBER;
            break;
            case 11: days_left += DECEMBER;
            break;
            default:
                days_left += 0;
            } 
        }
        return days_left;
    }

    public void Vibrate(){

        vibr.vibrate(500);
    }


}
