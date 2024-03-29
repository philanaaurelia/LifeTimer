/* 
 * Written By Philana Benton
 * Spelman College/University of Michigan
 * Luke 1:37
 */
package com.philanaaurelia.counterone;

import android.app.Activity;
//import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import android.view.MotionEvent;
import android.widget.ImageView;
//import android.widget.LinearLayout;
import android.content.Intent;
import android.graphics.Canvas;

public class CounterOne extends Activity {
    private static final int PREFERENCES_GROUP_ID = 0;
    private static final int PREFERENCES_ID = 0;
    //private static final int SETTINGS_ID = 0;
    private static final int HELP_ID = 1;
    private static final int FEEDBACK_ID = 2;
    private static final int ABOUT_ID = 4;
    private static final int GONE = 8;
    private static final int VISIBLE = 0;

    private Handler mHandler = new Handler();
    private int imageflip = 0;
    Canvas c = null;


    ImageView mPanic,mProc, mSet, mWait;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPanic = (ImageView) findViewById(R.id.panic_img);
        mProc = (ImageView) findViewById(R.id.proc_img);
        mSet = (ImageView) findViewById(R.id.set_img);
        mWait = (ImageView) findViewById(R.id.wait_img);

        mPanic.setVisibility(GONE);

        //Handler thread to change images
        mHandler.removeCallbacks(mChangeImage);
        mHandler.postDelayed(mChangeImage, 2000);

        //On Touch handler
        /*  LinearLayout mMainLL = (LinearLayout) findViewById(R.id.mainLayout);
       mMainLL.setOnTouchListener(new View.OnTouchListener(){

    	   public boolean onTouch(View v, MotionEvent e) {
    		//openOptionsMenu();
       		return true;
       	}
       });*/ 
    }

    //Makes the thread run and actually hcanges the images
    private Runnable mChangeImage = new Runnable() {
        public void run() {
            if(imageflip==0  || imageflip==4){
                mSet.setVisibility(VISIBLE);
                mWait.setVisibility(GONE);
                mProc.setVisibility(GONE);
                mPanic.setVisibility(GONE);
                mHandler.postDelayed(mChangeImage, 1500);
                imageflip++;
            } else if(imageflip==1 || imageflip==5){
                mSet.setVisibility(GONE);
                mWait.setVisibility(VISIBLE);
                mProc.setVisibility(GONE);
                mPanic.setVisibility(GONE);
                imageflip++;
                mHandler.postDelayed(mChangeImage, 1500);
            } else if(imageflip==2 || imageflip==6){
                mSet.setVisibility(GONE);
                mWait.setVisibility(GONE);
                mProc.setVisibility(VISIBLE);
                mPanic.setVisibility(GONE);
                imageflip++;
                mHandler.postDelayed(mChangeImage, 1500);
            } else if(imageflip==3 || imageflip==7){
                mSet.setVisibility(GONE);
                mWait.setVisibility(GONE);
                mProc.setVisibility(GONE);
                mPanic.setVisibility(VISIBLE);
                imageflip++;
                mHandler.postDelayed(mChangeImage, 1500);
            } else{
                mSet.setVisibility(VISIBLE);
                mWait.setVisibility(GONE);
                mProc.setVisibility(GONE);
                mPanic.setVisibility(GONE);
                mHandler.removeCallbacks(mChangeImage);
            }

        }
    };

    /***************************
     * Handles Button Clicks
     ***************************/
    public void Create_click(View view){
        mHandler.removeCallbacks(mChangeImage);
        Intent myIntent = new Intent(view.getContext(), AddTimer.class);
        startActivityForResult(myIntent, 0);
    }

    public void View_click(View view){
        mHandler.removeCallbacks(mChangeImage);
        Intent myIntent = new Intent(view.getContext(), ViewMyTimer.class);
        myIntent.putExtra("tableType", "ascending");
        startActivityForResult(myIntent, 0);
    }

    public void Preset_click(View view){
        mHandler.removeCallbacks(mChangeImage);
        Intent myIntent = new Intent(view.getContext(), Preset.class);
        startActivityForResult(myIntent, 0);
    }

    /********************
     * OPTIONS MENU
     *******************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);


        /* menu.add(PREFERENCES_GROUP_ID, SETTINGS_ID, 0, R.string.menu_settings)
    .setIcon(android.R.drawable.ic_menu_preferences);*/
        menu.add(PREFERENCES_GROUP_ID, PREFERENCES_ID, 0 ,"Settings")
        .setIcon(android.R.drawable.ic_menu_set_as);
        menu.add(PREFERENCES_GROUP_ID, HELP_ID, 0, "Help/FAQ")
        .setIcon(android.R.drawable.ic_menu_help);
        menu.add(PREFERENCES_GROUP_ID, FEEDBACK_ID, 0,"FeedBack")
        .setIcon(android.R.drawable.ic_menu_send);
        menu.add(PREFERENCES_GROUP_ID, ABOUT_ID, 0, R.string.menu_about)
        .setIcon(android.R.drawable.ic_menu_info_details);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent myIntent;
        // Handle item selection
        switch (item.getItemId()) {
        case FEEDBACK_ID:
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "SC.Taboo@gmail.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"LifeTimer App Feedback");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
            CounterOne.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            return true;
        case ABOUT_ID:
            myIntent = new Intent(this.getBaseContext(), AboutUs.class);
            startActivityForResult(myIntent, 0);
            return true;
        case HELP_ID:
            myIntent = new Intent(this.getBaseContext(), Help.class);
            startActivityForResult(myIntent, 0);
            return true;
        case PREFERENCES_ID:
            myIntent = new Intent(this.getBaseContext(), MyPreference.class);
            startActivityForResult(myIntent, 0);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}