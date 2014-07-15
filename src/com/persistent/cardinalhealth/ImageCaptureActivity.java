package com.persistent.cardinalhealth;

import java.io.File;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.google.android.glass.media.CameraManager;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.persistent.cardinalhealth.scan.BaseGlassActivity;

public class ImageCaptureActivity extends BaseGlassActivity implements
		SurfaceHolder.Callback {

	private static final int TAKE_PICTURE_REQUEST = 1;
	private static final String TAG = ImageCaptureActivity.class
			.getSimpleName();

	private com.google.zxing.client.android.camera.CameraManager mCameraManager;

	private GestureDetector mGestureDetector = null;
	private String imagePath = "";
	private String imageThumbPath = "";

	private boolean mInPreview = false;

	private ImageView imageView = null;
	private SurfaceView surfaceView = null;
	private SurfaceHolder surfaceHolder = null;
	private boolean mHasSurface = false;
	private String barcodeNo;

	public static Intent newIntent(Context context) {
		Intent intent = new Intent(context, ImageCaptureActivity.class);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHasSurface = false;

		// Display the card we just created
		setContentView(R.layout.camera_layout);

		try {

			imageView = (ImageView) findViewById(R.id.pill_count_image);
			imageView.setVisibility(android.view.View.GONE);

			surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
			surfaceView.setVisibility(android.view.View.VISIBLE);

			mCameraManager = new com.google.zxing.client.android.camera.CameraManager(
					getApplication());

			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.addCallback(this);

			// Turn on Gestures
			mGestureDetector = createGestureDetector(this);

			// StartPreview();

		} catch (Exception ex) {
			Log.d("CheckHome", "onCreate - " + ex.toString());
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			if (!mHasSurface) {
				// Install the callback and wait for surfaceCreated() to init
				// the camera.
				surfaceHolder.addCallback(this);
			}
		} catch (Exception ex) {
			Log.d(TAG, "onResume - " + ex.toString());
		}
	}

	@Override
	protected void onPause() {
		try {
			mCameraManager.closeDriver();
			if (!mHasSurface) {
				SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
				surfaceHolder = surfaceView.getHolder();
				surfaceHolder.removeCallback(this);
			}
		} catch (Exception ex) {
			Log.d(TAG, "onPause - " + ex.toString());
		}

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, " WARNING- surfaceCreated() gave us a null surface!");
		}
		if (!mHasSurface) {
			mHasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException(
					"initCamera -  No SurfaceHolder provided");
		}
		if (mCameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open, late SurfaceView callback?");
			return;
		}
		try {
			mCameraManager.openDriver(surfaceHolder);
			mCameraManager.startPreview();
			Log.w(TAG, "initCamera() Success");
			mInPreview = true;

		} catch (IOException e) {
			Log.w(TAG, e);
		} catch (InterruptedException e) {
			Log.w(TAG, e);
		}
	}

	/**
	 * Gesture detection for fingers on the Glass
	 * 
	 * @param context
	 * @return
	 */
	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);

		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
			@Override
			public boolean onGesture(Gesture gesture) {
				// Make sure view is initiated

				// Tap with a single finger for photo
				if (gesture == Gesture.TAP) {
					if (mInPreview) {
						mCameraManager.stopPreview();
						mInPreview = false;

						Intent processImage = new Intent(ImageCaptureActivity.this,
								ImageProcessingActivity.class);
						processImage.putExtra("barcodeNo", barcodeNo);
						processImage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(processImage);
						finish();
//						Intent intent = new Intent(
//								MediaStore.ACTION_IMAGE_CAPTURE);
//						if (intent != null) {
//							startActivityForResult(intent, TAKE_PICTURE_REQUEST);
//						}
					} else {
						StartPreview();

					}
					return true;
				}
				return false;
			}
		});

		return gestureDetector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		mInPreview = false;
		Log.d(TAG,"onActivityResult()");
		// Handle photos
		if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
			String pictureThumbPath = data
					.getStringExtra(CameraManager.EXTRA_THUMBNAIL_FILE_PATH);
			String picturePath = data
					.getStringExtra(CameraManager.EXTRA_PICTURE_FILE_PATH);

			// processPictureWhenReady(picturePath);

			imagePath = picturePath;
			imageThumbPath = pictureThumbPath;

			surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
			surfaceView.setVisibility(android.view.View.INVISIBLE);

			imageView = (ImageView) findViewById(R.id.pill_count_image);
			imageView.setVisibility(android.view.View.VISIBLE);
			// imageView.setMaxWidth(500);

			// Refresh display
			refreshCheckImage();

		} else if (requestCode == TAKE_PICTURE_REQUEST
				&& resultCode == RESULT_CANCELED) {
			surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
			surfaceView.setVisibility(android.view.View.INVISIBLE);

			imageView = (ImageView) findViewById(R.id.pill_count_image);
			imageView.setVisibility(android.view.View.VISIBLE);

			// Refresh display
			refreshCheckImage();

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * Send generic motion events to the gesture detector
	 */
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			return mGestureDetector.onMotionEvent(event);
		}

		return false;
	}

	
	/*
	 * To set image that has been captured on the layout.
	 */
	private void refreshCheckImage() {
		String imagePath = "";

		imagePath = imageThumbPath;

		// setContentView(R.layout.camera_layout);
		Log.d(TAG,"refreshCheckImage()");
		if (imagePath != null && !imagePath.isEmpty()) {
			File destination = new File(imagePath);
			Bitmap cameraBitmap = BitmapFactory.decodeFile(destination
					.getAbsolutePath());
			ImageView imageView = (ImageView) findViewById(R.id.pill_count_image);
			imageView.setImageBitmap(cameraBitmap);
			ProgressDialog progress = new ProgressDialog(this);
			progress.setTitle("Processing");
			progress.setMessage("Please wait...");
			progress.show();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			progress.dismiss();
			Intent processImage = new Intent(ImageCaptureActivity.this,
					ImageProcessingActivity.class);
			processImage.putExtra("barcodeNo", barcodeNo);
			processImage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(processImage);
			finish();
		}
	}

	
	private void StartPreview() {
		try {
			surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
			surfaceView.setVisibility(android.view.View.VISIBLE);

			imageView = (ImageView) findViewById(R.id.pill_count_image);
			imageView.setVisibility(android.view.View.GONE);

			surfaceHolder = surfaceView.getHolder();
			initCamera(surfaceHolder);
		} catch (Exception ex) {
			Log.d(TAG, "StartPreview - " + ex);
		}
	}

}
