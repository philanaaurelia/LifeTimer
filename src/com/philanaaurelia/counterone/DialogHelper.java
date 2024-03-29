/* 
 * Written By Philana Benton
 * Spelman College/University of Michigan
 * Luke 1:37
 */

package com.philanaaurelia.counterone;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
//import android.util.Log;


public class DialogHelper extends AlertDialog {
    //private AlertDialog dialog=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("OPENED");
        super.onCreate(savedInstanceState);
    }

    DialogHelper(Context context) {
        super(context,0);
    }

    public AlertDialog set(String title, String message, String btn1){
        setTitle(title);
        setMessage(message);
        setButton(btn1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            } });
        return this;
    }

}
