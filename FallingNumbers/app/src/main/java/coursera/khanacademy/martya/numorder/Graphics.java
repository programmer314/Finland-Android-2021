package coursera.khanacademy.martya.numorder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;

@SuppressLint("ClickableViewAccessibility")
public class Graphics extends View implements OnGlobalLayoutListener, OnTouchListener {

//	private long startTime, elapsedTime;
//	private final int FRAME_RATE = 100;
	
	private final GestureDetector gestureDetector;
	
	private Board board;
	
	private Bundle savedInstanceState;
	
	private AlertDialog restart;
	
	private static final String storeRestart = "Graphics.displayRestart";
	
	private static final Paint greenBackground = new Paint();
	
	private static final int lineWidth = 20;
	
	static {
		greenBackground.setColor(0xff006400);
		greenBackground.setStrokeWidth(lineWidth * 3 / 4f);
		greenBackground.setStyle(Style.STROKE);
	}
	
	public Graphics(Context context) {
		this(context, null);
	}

	public Graphics(Context context, Bundle savedInstanceState) {
		super(context);
		
		this.savedInstanceState = savedInstanceState;
		
		this.getViewTreeObserver().addOnGlobalLayoutListener(this);
		this.gestureDetector = new GestureDetector(context, new GestureListener());
		this.setOnTouchListener(this);
		
	}
	
	// initializer method called when graphical information (like View width and height) is finished loading
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		if (getWidth() > 0 && getHeight() > 0) {
			if (Build.VERSION.SDK_INT < 16)
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			else
				getViewTreeObserver().removeOnGlobalLayoutListener(this);
		}
		
		// graphical initialization
		float size = Math.min(getWidth(), getHeight());
		
		this.board = new Board(
				this, 
				savedInstanceState,
				new RectF(
						(getWidth() - size) / 2f, 
						(getHeight() - size) / 2f, 
						(getWidth() + size) / 2f, 
						(getHeight() + size) / 2f));
		
		this.restart = new AlertDialog.Builder(getContext())
							.setMessage(R.string.ask_replay)
							.setPositiveButton(R.string.replay, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									initBoard();
									
								}
							})
							.setNegativeButton(R.string.go_back, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									((MainActivity) getContext()).goToMainMenu();
									
								}
							})
							.setNeutralButton(R.string.back_to_game, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {}
								
							}).create();
		
		if (savedInstanceState != null && savedInstanceState.getBoolean(storeRestart))
			popupRestart();
		
		this.savedInstanceState = null;
		
	}
	
	// method called when drawing the board
	@Override
	protected void onDraw(Canvas canvas) {
//		this.startTime = SystemClock.uptimeMillis();
		
		drawBackground(canvas, getWidth(), getHeight());
		
		board.draw(canvas);
		
//		this.elapsedTime = SystemClock.uptimeMillis() - startTime;
//		postInvalidateDelayed(elapsedTime < FRAME_RATE ? FRAME_RATE - elapsedTime : 0);
		
	}
	
	private static void drawBackground(Canvas canvas, int width, int height) {
		canvas.drawColor(0xff008800);
		
		for (int p = 0; p < 2 * Math.max(width, height); p += 2 * lineWidth)
			canvas.drawLine(p, -lineWidth / 2f, -lineWidth / 2f, p, greenBackground);
		
	}
	
	// save the graphics state
	public void saveInstanceState(Bundle outState) {
		if (board != null)
			board.saveBoard(outState);
		
		outState.putBoolean(storeRestart, restart.isShowing());
		
	}
	
	// gesture code
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		if (board.hasWon && event.getAction() == MotionEvent.ACTION_UP && view instanceof Graphics && !restart.isShowing())
			popupRestart();
		
		if (!board.hasWon && view instanceof Graphics && !restart.isShowing())
			return gestureDetector.onTouchEvent(event);
		
		return true;
		
	}
	
	public void popupRestart() {
		if(restart.isShowing())
			restart.dismiss();
		else
			restart.show();
	}
	
	public void initBoard() {
		board.initGrid(null);
	}
	
	public void onSwipeRight() {
		board.flipRight();
	}
	
	public void onSwipeLeft() {
		board.flipLeft();
	}
	
	public void onSwipeDown() {
		board.gravity();
	}
	
	// a class to detect swipes
	private final class GestureListener extends SimpleOnGestureListener {
		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;
		
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				float diffX = e2.getX() - e1.getX();
				float diffY = e2.getY() - e1.getY();
				
				if (Math.abs(diffX) > Math.abs(diffY)) {
					if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffX > 0)
							onSwipeRight();
						else
							onSwipeLeft();
					}
					
					return true;
				}
				
				else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
					if (diffY > 0)
						onSwipeDown();
					
					return true;
					
				}
				
				
			} catch(Exception exception) {
				exception.printStackTrace();
			}
			
			return false;
		}
		
	}
	
	// A view drawing background on xml
	public static class BackgroundViewGroup extends RelativeLayout {

		public BackgroundViewGroup(Context context) {
			super(context);
		}
		
		public BackgroundViewGroup(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		public BackgroundViewGroup(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			drawBackground(canvas, getWidth(), getHeight());
			
			super.onDraw(canvas);
		}
		
	}
	
	// A class with graphical information like position and color
	public static class G_info {
		private RectF rect = new RectF();
		
		private float radius = 0;
		
		private Paint paint = new Paint(), strokePaint = new Paint();
		private Style style = Style.FILL;
		
		private String text = "";
		
		public G_info() {
			paint.setStyle(Style.FILL);
			strokePaint.setStyle(Style.STROKE);
		}
		
		public G_info set(G_info info) {
			setRect(info.getRect());
			
			setColor(info.getFillColor(), info.getStrokeColor());
			setStyle(style);
			setStrokeWidth(info.getStrokeWidth());
			
			setText(info.getText());
			setTextAlign(info.getTextAlign());
			setTextSize(info.getTextSize());
			
			return this;
			
		}
		
		// setter functions
		public G_info setRect(float x, float y, float w, float h) {
			this.rect.set(x, y, w, h);
			
			return this;
		}
		
		public G_info setRect(RectF rect) {
			this.rect.set(rect);
			
			return this;
		}
		
		public G_info setPoint(PointF point) {
			this.rect.offsetTo(point.x, point.y);
			
			return this;
		}
		
		public G_info setRectRadius(float radius) {
			this.radius = radius;
			
			return this;
		}
		
		public G_info setStyle(Style style) {
			this.style = style;
			
			return this;
		}
		
		public G_info setFillColor(int color) {
			paint.setColor(color);
			
			return this;
		}
		
		public G_info setStrokeColor(int color) {
			strokePaint.setColor(color);
			
			return this;
		}
		
		public G_info setColor(int fillColor, int strokeColor) {
			setFillColor(fillColor);
			setStrokeColor(strokeColor);
			
			return this;
		}
		
		public G_info setStrokeWidth(float width) {
			strokePaint.setStrokeWidth(width);
			
			return this;
		}
		
		public G_info setText(String text) {
			this.text = text;
			
			return this;
		}
		
		public G_info setTextAlign(Align align) {
			strokePaint.setTextAlign(align);
			
			return this;
		}
		
		public G_info setTextSize(float size) {
			strokePaint.setTextSize(size);
			
			return this;
		}
		
		public G_info setTextSizeToRectWithPadding(float padding) {
			setTextSize((rect.bottom - rect.top) * (1 - padding));
			
			return this;
		}
		
		// drawing functions
		public void drawRect(Canvas canvas) {
			if (style != Style.STROKE)
				canvas.drawRoundRect(rect, radius, radius, paint);
			if (style != Style.FILL)
				canvas.drawRoundRect(rect, radius, radius, strokePaint);
			
		}
		
		public void drawText(Canvas canvas) {
			strokePaint.setStyle(Style.FILL);
			canvas.drawText(text, rect.left, rect.top - strokePaint.ascent() - strokePaint.getTextSize() / 2, strokePaint);
			strokePaint.setStyle(Style.STROKE);
		}
		
		// getter functions
		public RectF getRect() {
			return new RectF(rect);
		}
		
		public float getX() {
			return rect.left;
		}
		
		public float getY() {
			return rect.top;
		}
		
		public float getWidth() {
			return rect.width();
		}
		
		public float getHeight() {
			return rect.height();
		}
		
		public PointF getCenter() {
			return new PointF((rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2);
		}
		
		public float getRectRadius() {
			return radius;
		}
		
		public int getFillColor() {
			return paint.getColor();
		}
		
		public int getStrokeColor() {
			return strokePaint.getColor();
		}
		
		public Style getStyle() {
			return style;
		}
		
		public float getStrokeWidth() {
			return strokePaint.getStrokeWidth();
		}
		
		public String getText() {
			return new String(text);
		}
		
		public Align getTextAlign() {
			return strokePaint.getTextAlign();
		}
		
		public float getTextSize() {
			return strokePaint.getTextSize();
		}
		
	}

}
