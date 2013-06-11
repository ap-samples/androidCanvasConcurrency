/*  Copyright 2013 Arthur Peka
 * 
 *  This file is part of CanvasConcurrencyTest.
    CanvasConcurrencyTest is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License.   
    CanvasConcurrencyTest is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CanvasConcurrencyTest.  If not, see <http://www.gnu.org/licenses/>.
*/
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

