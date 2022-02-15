package coursera.khanacademy.martya.numorder;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
	
	private Graphics graphics;
	private Graphics.G_info backgroundInfo = new Graphics.G_info();
	
	private Graphics.G_info gridInfo = new Graphics.G_info();
	
	private List<Integer> grid = new ArrayList<>();
	private List<Graphics.G_info> gridInfos = new ArrayList<>();
	
	private static int[] colorList = new int[15];
//	private static final int[] winOrder = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
	
	public boolean hasWon = false;
//	private static final String HAS_WON_KEY = "hasWon";
	
	private final Runnable autoPush = new Runnable() { // reset when flipped or gravity used
		
		public void run() {
			gravity();
			
			if (!hasWon)
				reset(false);
			
		}
		
	};
	
	static {
		for (int i = 1; i <= 15; i++) {
			colorList[i - 1] = 0xff000000 + 0x1100 * i;
		}
	}
	
	private static final Random rand = new Random();
	
	// initialize board
	public Board(Graphics graphics, Bundle savedInstanceState, RectF info) {
		this.graphics = graphics;
		
		this.backgroundInfo.setRect(
									info.left + 0.05f * info.width(),
									info.top + 0.05f * info.height(),
									info.left + 0.95f * info.width(),
									info.top + 0.95f * info.height())
									
						   .setFillColor(0xffff5100)
						   .setStyle(Style.FILL)
						   .setRectRadius(9);
				
		this.gridInfo.setRect(
							  backgroundInfo.getX() + 0.05f * backgroundInfo.getWidth(), 
							  backgroundInfo.getY() + 0.05f * backgroundInfo.getHeight(), 
							  backgroundInfo.getX() + 0.95f * backgroundInfo.getWidth(), 
							  backgroundInfo.getY() + 0.95f * backgroundInfo.getHeight())
							  
					 .setStyle(Style.FILL_AND_STROKE)
					 .setColor(Color.WHITE, Color.BLACK)
					 .setStrokeWidth(5);
		
		initGrid(savedInstanceState);
		
		for (int i = 0; i < 16; i++)
			gridInfos.add(
					new Graphics.G_info()
						.setRect(
								gridInfo.getX() + gridInfo.getWidth() * (i % 4) / 4f,
								gridInfo.getY() + gridInfo.getHeight() * (i / 4) / 4f,
								gridInfo.getX() + gridInfo.getWidth() * (i % 4 + 1) / 4f,
								gridInfo.getY() + gridInfo.getHeight() * (i / 4 + 1) / 4f)
								
						.setStyle(Style.STROKE)
						.setStrokeWidth(5)
						.setStrokeColor(Color.BLACK)
						.setTextAlign(Align.CENTER)
						.setTextSizeToRectWithPadding(0.3f));
		
	}
	
	// draw
	public void draw(Canvas canvas) {
		backgroundInfo.drawRect(canvas);
		gridInfo.drawRect(canvas);
		
		for (int i = 0; i < grid.size(); i++) {
			gridInfos.get(i).drawRect(canvas);
			
			if (grid.get(i) != 0) {
				new Graphics.G_info()
								.set(gridInfos.get(i))
								
								.setText(String.valueOf(grid.get(i)))
								
								.setStrokeColor(colorList[grid.get(i) - 1])
								.setStrokeWidth(1)
								
								.setPoint(gridInfos.get(i).getCenter())
								
								.drawText(canvas);
			}
		}
		
		updateWin();
		
	}
	
	// initialize grid
	public void initGrid(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			List<Integer> nums = new ArrayList<>();
			
			for (int num = 0; num < 16; num++)
				nums.add(num);
			
			grid.clear();
			
			for (int i = 0; i < 16; i++) {
				int index = rand.nextInt(nums.size());
				
				grid.add(nums.get(index));
				
				nums.remove(index);
				
			}
		}
		
		else 
			grid = savedInstanceState.getIntegerArrayList("grid");
		
		this.hasWon = false;
//		getApplication().putInDataLayer(HAS_WON_KEY, "false");
		
		graphics.postInvalidate();
		
		
	}
	
	// reset the gravity timer
	private static final long DELAY_GRAVITY = 4000;
	private static final Handler handler = new Handler();
	
	public void reset(boolean remove) {
		if (remove)
			handler.removeCallbacks(autoPush);
		
		handler.postDelayed(autoPush, DELAY_GRAVITY);
	}
	
	// save the state
	public void saveBoard(Bundle outState) {
		if (outState != null)
			outState.putIntegerArrayList("grid", (ArrayList<Integer>) grid);
	}
	
	// check for win
	public void updateWin() {
		this.hasWon = true;
		
		for (int i = 0; i < grid.size() - 1; i++)
			if (grid.get(i) != i)
				this.hasWon = false;
		
		if (this.hasWon) {
			Toast.makeText(
                    graphics.getContext().getApplicationContext(),
					R.string.tellToStart, 
					Toast.LENGTH_LONG).show();

            getApplication().sendEvent("Main", "Won", "null");
//			getApplication().putInDataLayer(HAS_WON_KEY, "true");
			
		}
		
	}
	
	// control board changes
	public void gravity() {
		if (!hasWon) {
			int index = grid.indexOf(0);
			
			if (index >= 4) {
				grid.set(index, grid.get(index - 4));
				grid.set(index - 4, 0);
			}
			
			graphics.postInvalidate();
			reset(true);
			
		}
	}
	
	public void flipLeft() {
		if (!hasWon) {
			List<Integer> newGrid = new ArrayList<>(16);
			
			for (int index = 0; index < grid.size(); index++)
	//			newGrid.set(((grid.size() - index - 1) % 4) * 4 + index / 4, grid.get(index));
				newGrid.add(grid.get((index % 4) * 4 + (grid.size() - index - 1) / 4));
			
			this.grid = newGrid;
			
			graphics.postInvalidate();
			reset(true);
			
		}
		
	}
	
	public void flipRight() {
		if (!hasWon) {
			List<Integer> newGrid = new ArrayList<>(16);
			
			for (int index = 0; index < grid.size(); index++)
	//			newGrid.set((index % 4) * 4 + (grid.size() - index - 1) / 4, grid.get(index));
				newGrid.add(grid.get(((grid.size() - index - 1) % 4) * 4 + index / 4));
			
			this.grid = newGrid;
			
			graphics.postInvalidate();
			reset(true);
			
		}
		
	}

    private MainApplication getApplication() {
        return (MainApplication) ((Activity) graphics.getContext()).getApplication();
    }
	
}
