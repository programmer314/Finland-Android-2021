package coursera.khanacademy.martya.numorder;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ViewFlipper;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {
	Graphics graphics;
	
	private Page page = null;
	
	private ViewFlipper viewFlipper;
	private AdView bannerAdViewBottom;
	
//	private boolean isInstructionsDisplayed = false;
//	private boolean isGraphicsDisplayed = false;

	// create activity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_main);
		
		this.page = savedInstanceState == null ? Page.MAIN : Page.valueOf(savedInstanceState.getString(Page.location));
		this.graphics = new Graphics(this, page == Page.GRAPHICS ? savedInstanceState : null);
		
		graphics.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		this.viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		viewFlipper.addView(View.inflate(this, R.layout.activity_main, null));
		viewFlipper.addView(graphics);
		viewFlipper.addView(View.inflate(this, R.layout.instructions, null));
		
		setSwitchContentView(page);
		
		this.bannerAdViewBottom = (AdView) findViewById(R.id.bannerAdViewBottom);
		
		bannerAdViewBottom.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        ((MainApplication) getApplication()).startTracking();
		
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
	
	// save game
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString(Page.location, page.name());
		
		if (page == Page.GRAPHICS)
			graphics.saveInstanceState(outState);
		
	}
	
	public void setSwitchContentView(Page page) {
		if (page == null) return;
		
		this.page = page;
		
		viewFlipper.setDisplayedChild(page.id);

        ((MainApplication) getApplication()).sendScreenView(page.pageName);

	}
	
	@Override
	public void onBackPressed() {
		
		if (page == null) {
			super.onBackPressed();
			
			return;
		}
		
		switch (page) {
		
			case GRAPHICS:
				graphics.popupRestart();

				break;

			case INSTRUCTIONS:
				setSwitchContentView(Page.MAIN);

				break;

			case MAIN:
				super.onBackPressed();
		
		}
		
	}

    // handle menu buttons being pressed (including back button at beginning)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // handle buttons and layouts
	public void goToMainMenu() {
		setSwitchContentView(Page.MAIN);
	}
	
	public void playGame(View view) {
		graphics.initBoard();
		
		setSwitchContentView(Page.GRAPHICS);
		
	}
	
	public void getInstructions(View view) {
		setSwitchContentView(Page.INSTRUCTIONS);
	}
	
	@Override
	protected void onPause() {
		if (bannerAdViewBottom != null)
			bannerAdViewBottom.pause();
		
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (bannerAdViewBottom != null)
			bannerAdViewBottom.resume();
		
	}
	
	@Override
	protected void onDestroy() {
		if (bannerAdViewBottom != null)
			bannerAdViewBottom.destroy();

        ((MainApplication) getApplication()).stopScreenView();
		
		super.onDestroy();
	}
	
	private enum Page {
		MAIN(0, "Home Page"), GRAPHICS(1, "Game Page"), INSTRUCTIONS(2, "Instructions Page");
		
		public static final String location = "MainActivity.page";
		
		int id;
		String pageName;
		
		Page(int id, String pageName) {
			this.id = id;
			this.pageName = pageName;
		}
		
//		public static Page getPageFromId(int id) {
//			
//			for (Page page : Page.values())
//				if (page.id == id)
//					return page;
//			
//			return null;
//			
//		}
		
	}

}
