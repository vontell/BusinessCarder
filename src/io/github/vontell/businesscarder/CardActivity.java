package io.github.vontell.businesscarder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.github.vontell.businesscarder.layouts.TestLayoutLand;
import io.github.vontell.businesscarder.util.SystemUiHider;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class CardActivity extends Activity {
	
	/**
	 * The whole view of the card, which we will use to obtain a 'screenshot'
	 */
	private FrameLayout cardViewBackground;
	private FrameLayout cardView;
	//private ImageView starter;
	private TextView starter;
	private RelativeLayout contentView;
	
	//Some more of my variables
	private int orientation;
	private boolean blank;
	
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_card);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		contentView = (RelativeLayout) findViewById(R.id.fullscreen_content);
		cardViewBackground = (FrameLayout) findViewById(R.id.the_card);
		//starter = (ImageView) findViewById(R.id.start_image);
		starter = (TextView) findViewById(R.id.start_text);
		
		prefs = this.getSharedPreferences(getResources().getString(R.string.PREFS_KEY), MODE_PRIVATE);
		editor = prefs.edit();
		
		orientation = prefs.getInt("orientation", 0);
		blank = prefs.getBoolean("blank", true);
		
		if(blank == true){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		else if( orientation == R.id.radio_land){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		else if( orientation == 0){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		//tells if the card is blank or not created
		if(blank){
			showGetStarted();
		}
		else{
			try {
				displayCard();
			} catch (IOException e) {
				Log.v("Error", "Error");
			}
		}

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.edit_button).setOnTouchListener(
				mDelayHideTouchListener);
	}

	/**
	 * Starts the card building process
	 * @throws IOException 
	 */
	private void displayCard() throws IOException {
		
		//First make the foundation of the card
		cardView = new FrameLayout(this);
		
		//Then load the desired theme and background
		cardView.setBackgroundColor(android.graphics.Color.WHITE);
		
		//Then compute the size of the card
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		int containerW = size.x;
		int containerH = size.y;
		Log.v("Displaying Card", "Your background: Width(" + containerW + ")  Height(" + containerH +")");
		
		if(orientation == R.id.radio_land){
			int cardHeight = containerH - 265;
			int cardWidth = (int) (cardHeight * 1.75);
			cardView.setLayoutParams(new RelativeLayout.LayoutParams(cardWidth, cardHeight));
			Log.v("Displaying Card", "Your card is: Width(" + cardWidth + ")  Height(" + cardHeight +")");
		}
		else{
			int cardHeight = containerH - 265;
			int cardWidth = (int) (cardHeight / 1.75);
			cardView.setLayoutParams(new RelativeLayout.LayoutParams(cardWidth, cardHeight));
			Log.v("Displaying Card", "Your card is: Width(" + cardWidth + ")  Height(" + cardHeight +")");
		}
		
		//then compute the location of the card
		cardView.setY(cardView.getY() + 40);
		
		//Now create the views that will be going into the 
		int layoutId = prefs.getInt("layout_id", 0);
		
		//Choose and create the layout
		switch(layoutId){
		case 0 : TestLayoutLand.create(cardView, contentView, prefs, this);
		}
		
		starter.setVisibility(View.GONE);
		cardView.setVisibility(View.VISIBLE);		
		
	}

	/**
	 * Tells the user to get started
	 */
	private void showGetStarted() {
		
		cardViewBackground.setBackgroundColor(getResources().getColor(R.color.blank_color));
		starter.setVisibility(View.VISIBLE);
		//cardView.setVisibility(View.GONE);
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	/**
	 * Starts a sharing intent to share the image
	 * @param view The view that invoked this method
	 * @throws FileNotFoundException 
	 */
	public void shareCard(View view) throws FileNotFoundException{
		
		View controlsView = findViewById(R.id.fullscreen_content_controls);
		controlsView.setVisibility(View.GONE);
		
		Log.v("Business Carder", "About to create new image file");
		//First we create a file output stream to save the card
		//TODO: Put in the right directory. Hover to view more information
		File sdCard = Environment.getExternalStorageDirectory();
		File file = new File(sdCard, "Card.jpg");
		FileOutputStream fos = new FileOutputStream(file);
		Log.v("Business Carder", "New File Created");
		
		//Second, we take a picture of the card
		cardView.setDrawingCacheEnabled(true);
		Bitmap b = cardView.getDrawingCache();
		b.compress(CompressFormat.JPEG, 95, fos);
		Log.v("Business Carder", "Snapshot created and saved");
		
		Log.v("Business Carder", "Creating the URI");
		Uri uri = Uri.fromFile(file);
		
		controlsView.setVisibility(View.VISIBLE);
		
		Log.v("Business Carder", "Creating the intent");
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		shareIntent.setType("image/jpeg");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Business Card from " + prefs.getString("name", "Interested Party"));
		shareIntent.putExtra(Intent.EXTRA_TEXT, "Attached is my business card.\n\nThank-you for your time.");
		startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_chooser)));
		
		Log.v("Business Carder", "Finished and succeeded");
		
	}
	
	/**
	 * Opens the edit info page
	 * @param view The view that invoked this method
	 */
	public void editInfo(View view){
		
		Intent intent = new Intent(this, EditActivity.class);
		startActivity(intent);
		this.finish();
		
	}
	
}
