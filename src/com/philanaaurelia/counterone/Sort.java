package com.philanaaurelia.counterone;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class Sort extends Activity{
    DialogHelper dialog;
    RadioButton mSrtAsc;
    RadioButton mSrtDes;
    RadioButton mSrtCat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sort);

        //Define general variables
        mSrtAsc=(RadioButton)findViewById(R.id.ascSort);
        mSrtDes=(RadioButton)findViewById(R.id.descSort);
        mSrtCat=(RadioButton)findViewById(R.id.catSort);
        dialog = new DialogHelper(this);

        //Call is made when Button is clicked
        //If Done Button is clicked, close the activity
        Button mDone = (Button) findViewById(R.id.BtnSrtCnl);
        mDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button mUpdate = (Button) findViewById(R.id.BtnSrtUpd);
        mUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent myIntent = new Intent(view.getContext(), ViewMyTimer.class);
                if(!mSrtAsc.isChecked() && !mSrtDes.isChecked() && !mSrtCat.isChecked()){
                    dialog.set("Hmm...", "Please choose an option to sort by, or click cancel.", "Ok");
                    dialog.show();
                    return;
                }

                if(mSrtAsc.isChecked())
                    myIntent.putExtra("tableType", "ascending");
                else if(mSrtDes.isChecked())
                    myIntent.putExtra("tableType", "descending");
                else if(mSrtCat.isChecked())
                    myIntent.putExtra("tableType", "categories");

                startActivityForResult(myIntent, 0);
                finish();
            }
        });
    }
}
