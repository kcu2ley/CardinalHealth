package com.persistent.cardinalhealth;


import com.persistent.cardinalhealth.scan.CaptureActivity;

import android.app.Activity;
import android.os.Bundle;

public class BarcodeScanActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		startActivity(CaptureActivity.newIntent(BarcodeScanActivity.this));
		finish();
		super.onCreate(savedInstanceState);
		
		
	}

	
}
