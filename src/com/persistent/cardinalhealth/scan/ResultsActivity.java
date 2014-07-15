package com.persistent.cardinalhealth.scan;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.persistent.cardinalhealth.StartWorkflowActivity;
import com.persistent.cardinalhealth.image.ImageManager;
import com.persistent.cardinalhealth.scan.api.CardPresenter;

public class ResultsActivity extends Activity {

    private static final String TAG = ResultsActivity.class.getSimpleName();
    private static final String EXTRA_CARDS = "EXTRA_CARDS";
    private static final String EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI";

    private final List<CardPresenter> mCardPresenters = new ArrayList<CardPresenter>();
    private CardScrollView mCardScrollView;
    private Bitmap mImage;
    private CardPresenter firstCard;

    public static Intent newIntent(Context context,
            List<CardPresenter> cardResults, Uri imageUri) {

        Intent intent = new Intent(context, ResultsActivity.class);
        if (cardResults != null) {
            intent.putExtra(EXTRA_CARDS,
                    cardResults.toArray(new CardPresenter[cardResults.size()]));
        }
        intent.putExtra(EXTRA_IMAGE_URI, imageUri);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (savedInstanceState != null) {
            readExtras(intent.getExtras());
            Log.i("TAG","In saved instance state");
           
        } else if (intent != null && intent.getExtras() != null) {
            readExtras(intent.getExtras());
            Log.i("TAG","In get extras");
            
        } else {
            Log.e(TAG, "No extras were present");
            Intent codeIntent = new Intent(this, CaptureActivity.class);                       
            startActivity(codeIntent);
            finish();            
            return;
        }

        if (mCardPresenters.size() == 0) {
            Log.w(TAG, "There were no cards to display");
            Intent codeIntent = new Intent(this, CaptureActivity.class);                       
            startActivity(codeIntent);
            finish();   
            return;
        }
        Intent codeIntent = new Intent(this, StartWorkflowActivity.class);
        codeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        codeIntent.putExtra("barCodeText",firstCard.getFooter());
        startActivity(codeIntent);
        finish();
        
      /*  mCardScrollView = new CardScrollView(this);
        mCardScrollView.setAdapter(new CardScrollViewAdapter(this, mCardPresenters));
        mCardScrollView.activate();
        mCardScrollView.setOnItemClickListener(mOnItemClickListener);

        setContentView(mCardScrollView);*/
    }

    private void readExtras(Bundle extras) {
        Parcelable[] parcelCardsArray = extras.getParcelableArray(EXTRA_CARDS);
        for (int i = 0; i < parcelCardsArray.length; i++) {
            mCardPresenters.add((CardPresenter) parcelCardsArray[i]);
            firstCard=(CardPresenter) parcelCardsArray[0];
        }
        Log.i("cardData",mCardPresenters.toString());
        Uri imageUri = (Uri) extras.getParcelable(EXTRA_IMAGE_URI);
        if (imageUri != null) {
            mImage = new ImageManager(this).getImage(imageUri);
        }
        Log.i("ImageURI",imageUri.toString());
    }

    public static class CardScrollViewAdapter extends CardScrollAdapter {

        private final Context mContext;
        private final List<CardPresenter> mCardPresenters;

        public CardScrollViewAdapter(Context context,
                List<CardPresenter> cardPresenter) {
            mContext = context;
            mCardPresenters = cardPresenter;
        }

        /*@Override
        public int findIdPosition(Object id) {
            return -1;
        }*/

        @Override
        public int getCount() {
            return mCardPresenters.size();
        }

        @Override
        public Object getItem(int position) {
            return mCardPresenters.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CardPresenter cardPresenter = mCardPresenters.get(position);
            return cardPresenter.getCardView(mContext);
        }

		@Override
		public int getPosition(Object item) {
			return mCardPresenters.indexOf(item);
		}
    }

    private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            CardPresenter cardPresenter = mCardPresenters.get(position);
            PendingIntent pendingIntent = cardPresenter.getPendingIntent();
            if (pendingIntent != null) {
                try {
                    pendingIntent.send();
                } catch (CanceledException e) {
                    Log.w(TAG, e.getMessage());
                }
            } else {
                Log.w(TAG, "No PendingIntent attached to card!");
            }
        }
    };
}
