package io.github.vontell.businesscarder.layouts;

import java.io.IOException;

import io.github.vontell.businesscarder.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TestLayoutLand {

	public static void create(FrameLayout cardView, RelativeLayout contentView, SharedPreferences prefs, Context con) throws IOException {
		
		String name = prefs.getString("name", "");
		String phone = prefs.getString("phone", "");
		String email = prefs.getString("email", "");
		String title = prefs.getString("title", "");
		String website = prefs.getString("website", "");
		//String font = prefs.getString("font", "");
		int font = prefs.getInt("fontID", 0);
		
		TextView text = new TextView(con);
		text.setText(name + "\n" +
					 title + "\n\n" +
					 phone + "\t\t\t" + email + "\n" +
					 website);
		
		//TODO:
		if(font == 0){
			//Do nothing, keep default font
		}
		else{
			String[] fonts = con.getAssets().list("fonts");
			Typeface face = Typeface.createFromAsset(con.getAssets(), "fonts/" + fonts[font]);
			text.setTypeface(face);
		}
		
		text.setTextColor(android.graphics.Color.WHITE);
		text.setTextSize(15);
		text.setGravity(Gravity.CENTER);
		
		cardView.setBackgroundResource(R.drawable.test_layout_land);
		
		cardView.setPadding(200, 0, 0, 0);
		cardView.addView(text);
		contentView.addView(cardView);
		
	}

}
