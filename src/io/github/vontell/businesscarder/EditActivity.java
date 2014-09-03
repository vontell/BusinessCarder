package io.github.vontell.businesscarder;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class EditActivity extends Activity {
	
	private EditText name;
	private EditText phone;
	private EditText email;
	private EditText jobTitle;
	private EditText website;
	private Button fontButton;
	private RadioGroup orGroup;
	private RadioButton landscape;
	private RadioButton portrait;
	private int orientation;
	private RadioGroup fontSelection;
	
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	AssetManager aManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		
		name = (EditText) findViewById(R.id.name_view);
		phone = (EditText) findViewById(R.id.phone_view);
		email = (EditText) findViewById(R.id.email_view);
		jobTitle = (EditText) findViewById(R.id.title_view);
		website = (EditText) findViewById(R.id.website_view);
		fontButton = (Button) findViewById(R.id.font_button);
		landscape = (RadioButton) findViewById(R.id.radio_land);
		portrait = (RadioButton) findViewById(R.id.radio_port);
		orGroup = (RadioGroup) findViewById(R.id.orientation_group);
		
		prefs = this.getSharedPreferences(getResources().getString(R.string.PREFS_KEY), MODE_PRIVATE);
		editor = prefs.edit();
		
		aManager = this.getAssets();
		
		//load existing info
		name.setText(prefs.getString("name", ""));
		phone.setText(prefs.getString("phone", ""));
		email.setText(prefs.getString("email", ""));
		jobTitle.setText(prefs.getString("title", ""));
		website.setText(prefs.getString("website", ""));
		orientation = prefs.getInt("orientation", R.id.radio_land);
		orGroup.check(orientation);
		
		updateFont();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_clear) {
			clearChanges();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void updateFont(){
		
		try {
			Log.v("Edit Page:", "Starting to update the font");
			String[] fonts = this.aManager.list("fonts");
			
			String name = fonts[prefs.getInt("fontID", 0)].replace('_', ' ');
			name = name.replace("fonts/", "");
			name = name.substring(0, name.length() - 4);
			fontButton.setTypeface(Typeface.createFromAsset(aManager, "fonts/" + fonts[prefs.getInt("fontID", 0)]));
			fontButton.setText(name);
			Log.v("Edit Page:", "Updated the font");
			fontButton.invalidate();
		} catch (IOException e) {
			Log.v("Error setting button: ", e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public void clearChanges(){
		
		name.setText("");
		phone.setText("");
		email.setText("");
		jobTitle.setText("");
		website.setText("");
		orGroup.check(R.id.radio_land);
		
	}
	
	public void openFonts(View view) throws IOException{
		
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		fontSelection = new RadioGroup(this);
		String[] fonts = this.aManager.list("fonts");
		
		for(String path : fonts){
			Log.v("Array of fonts:", path);
			RadioButton button = new RadioButton(this);
			String name = path.replace('_', ' ');
			name = name.replace("fonts/", "");
			name = name.substring(0, name.length() - 4);
			button.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/" + path));
			button.setText(name);
			button.setTextSize(25);
			Log.v("Font path:", path);
			Log.v("Font name:", name);
			fontSelection.addView(button);
			
		}
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   //TODO: cannot save at this point
	        	   int radioButtonID = fontSelection.getCheckedRadioButtonId();
	        	   View radioButton = fontSelection.findViewById(radioButtonID);
	        	   int idx = fontSelection.indexOfChild(radioButton);
	        	   
	               editor.putInt("fontID", idx).commit();
	               updateFont();
	           }
	       });
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User cancelled the dialog
	           }
	       });

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setTitle(R.string.dialog_font);

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		
		dialog.setView(fontSelection);
		
		dialog.show();
		
		
	}
	
	public void openPhotoDialog(View view){
		
		
		
	}
	
	public void applyChanges(View view){
		
		editor.putString("name", name.getText().toString()).commit();
		editor.putString("phone", phone.getText().toString()).commit();
		editor.putString("email", email.getText().toString()).commit();
		editor.putString("title", jobTitle.getText().toString()).commit();
		editor.putString("website", website.getText().toString()).commit();
		editor.putInt("orientation", orGroup.getCheckedRadioButtonId()).commit();
		
		if(name.getText().toString().equals("") &&
		   phone.getText().toString().equals("") &&
		   email.getText().toString().equals("") &&
		   website.getText().toString().equals("") &&
		   jobTitle.getText().toString().equals("")){
			
			editor.putBoolean("blank", true).commit();
			
		}
		else editor.putBoolean("blank", false).commit();
		
		Intent intent = new Intent(this, CardActivity.class);
		startActivity(intent);
		this.finish();
		
	}
	
}
