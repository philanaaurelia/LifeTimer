package com.philanaaurelia.counterone;


import android.preference.PreferenceActivity;
import android.os.Bundle;

public class MyPreference extends PreferenceActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	addPreferencesFromResource(R.layout.preferences);
	}

}
