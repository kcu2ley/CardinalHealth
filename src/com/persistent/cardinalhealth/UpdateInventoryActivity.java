package com.persistent.cardinalhealth;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class UpdateInventoryActivity extends Activity {
	private String msgToDisplay;
	TextView updateDetails;
	TextView updateDetailsFootNote;
	String pillCountToUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.update_details_layout);
		pillCountToUpdate = getIntent().getExtras().getString(
				"pillCountToUpdate");
		msgToDisplay = getMessageToDisplay(pillCountToUpdate);
		showUpdateInventoryDetails(msgToDisplay);
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			finish();

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	private void showUpdateInventoryDetails(String msg) {

		updateDetails = (TextView) findViewById(R.id.update_inventory_text);
		updateDetailsFootNote = (TextView) findViewById(R.id.update_details_foot_note);
		updateDetails.setText(msg);
		updateDetailsFootNote.setText(R.string.update_details_footnote);
	}

	private String getMessageToDisplay(String pillCount) {
		String message = null;
		message = "Inventory updated with " + pillCount + " pills.";

		return message;
	}
}
