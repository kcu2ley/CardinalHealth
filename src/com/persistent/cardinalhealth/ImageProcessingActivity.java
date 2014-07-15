package com.persistent.cardinalhealth;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ImageProcessingActivity extends Activity{
	
	String pillCountToUpdate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.image_details_layout);
		
		showImageProcessingDetails();
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				Intent updateInventory = new Intent(ImageProcessingActivity.this, UpdateInventoryActivity.class);
				updateInventory.putExtra("pillCountToUpdate", pillCountToUpdate);
				startActivity(updateInventory);
				finish();

				return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}
	
	private void showImageProcessingDetails() {
		SharedPreferences pref = getSharedPreferences(CommonUtilities.PREF_NAME, 0);

		int onlinePillCount = pref.getInt("online_pill_count", 22);
		int currentPillCount = pref.getInt("current_pill_count", 22);
		
		((TextView)findViewById(R.id.textViewNdc)).setText(pref.getString("medication_ndc", null));
		((TextView)findViewById(R.id.textViewCin)).setText(pref.getString("medication_cin", null));
		((TextView)findViewById(R.id.textViewDesc)).setText(pref.getString("medication_desc", null));
		((TextView)findViewById(R.id.textViewOnlineCount)).setText(Integer.toString(onlinePillCount));
		((TextView)findViewById(R.id.textViewCurrentCount)).setText(Integer.toString(currentPillCount));
		((ImageView)findViewById(R.id.imageViewPill)).setImageResource(pref.getInt("pill_image",0));

		RelativeLayout relLayoutDetails = (RelativeLayout)findViewById(R.id.relativeLayoutDetails);
		relLayoutDetails.setBackgroundResource(onlinePillCount > currentPillCount ? R.drawable.not_ok_mark : R.drawable.ok_mark);

		pillCountToUpdate = Integer.toString(currentPillCount);
	}
}

