package com.persistent.cardinalhealth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

public class StartWorkflowActivity extends Activity {

	String barCodeText;
	TextView medicationNo;
	TextView pillCount;
	TextView barcodeDetailsFootNote;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		barCodeText = getIntent().getExtras().getString("barCodeText");
		setContentView(R.layout.barcode_details_layout);

		showBarcodeDetails(barCodeText);

		super.onCreate(savedInstanceState);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			Intent startWorkflow = new Intent(StartWorkflowActivity.this,
					ImageCaptureActivity.class);
			startActivity(startWorkflow);
			finish();

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	/**
	 * shows barcode details on layout.
	 * 
	 * @param barcode details obtained from barcode.
	 */
	private void showBarcodeDetails(String barcodeDetails) {
//		String[] barcodeDataArray = barcodeDetails.split("-");
//		medicationNo = (TextView) findViewById(R.id.medication_no_text);
//		pillCount = (TextView) findViewById(R.id.pill_count_text);
//		barcodeDetailsFootNote = (TextView) findViewById(R.id.barcode_details_foot_note);
//
//		medicationNo.setText("Medication No.: " + barcodeDataArray[0]);
//		pillCount.setText("Pill count: " + barcodeDataArray[1]);
//		barcodeDetailsFootNote.setText(R.string.barcode_details_footnote);

		storeBarcodeDetails(barcodeDetails);

		SharedPreferences pref = getSharedPreferences(CommonUtilities.PREF_NAME, 0);
		
		((TextView)findViewById(R.id.textViewNdc)).setText(pref.getString("medication_ndc", null));
		((TextView)findViewById(R.id.textViewCin)).setText(pref.getString("medication_cin", null));
		((TextView)findViewById(R.id.textViewDesc)).setText(pref.getString("medication_desc", null));
		((TextView)findViewById(R.id.textViewOnlineCount)).setText(Integer.toString(pref.getInt("online_pill_count", 22)));
		((ImageView)findViewById(R.id.imageViewPill)).setImageResource(pref.getInt("pill_image",0));
	}

	/**
	 * stores barcode details in shared preferences.
	 * 
	 * @param string array containing barcode medication number and pill count.
	 */
	private void storeBarcodeDetails(String barcodeDetails) {
		SharedPreferences pref = getApplicationContext().getSharedPreferences(
				CommonUtilities.PREF_NAME, 0); // 0 - for private mode
		Editor editor = pref.edit();
		editor.putString("medication_ndc", barcodeDetails); // Storing medication number
		if (barcodeDetails.equals(getString(R.string.correct_barcode))) {
			editor.putString("medication_cin", "123456"); // Storing medication number
			editor.putString("medication_desc", "Divalproex Sodium 500mg");
			editor.putInt("online_pill_count", 22);
			editor.putInt("current_pill_count", 22);
			editor.putInt("pill_image", R.drawable.green_mandm);
		}
		else {
			editor.putString("medication_cin", "654321"); // Storing medication number
			editor.putString("medication_desc", "Gabapentin 600mg");
			editor.putInt("online_pill_count", 17);
			editor.putInt("current_pill_count", 12);
			editor.putInt("pill_image", R.drawable.red_mandm);
		}
		editor.commit();
	}

}
