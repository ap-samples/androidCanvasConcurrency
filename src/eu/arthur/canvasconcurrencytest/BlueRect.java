package eu.arthur.canvasconcurrencytest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class BlueRect {
	public final int width = 60;
	public final int height = 50;
	public final int alpha = 100;
	public final int color = Color.argb(alpha, 131, 111, 255);
	
	private int centerX, centerY;
	
	public BlueRect(int centerX, int centerY){
		this.centerX = centerX;
		this.centerY = centerY;
	}
	
	public void drawOnCanvas(Canvas canv, final Paint paint){
		paint.setColor(this.color);
		canv.drawRect(new Rect(centerX - width/2, centerY - height/2, centerX + width/2, centerY + height/2),
				paint);
	}
}

