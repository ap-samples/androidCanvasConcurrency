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

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CanvasView extends SurfaceView implements SurfaceHolder.Callback {
	private ThreadPoolExecutor executorService;
	private final int THREAD_COUNT = 2;
	private final int UNITS_TO_WAIT = 180;
	
	private volatile SurfaceHolder sh;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private volatile Canvas canv;
    private int canvWidth;
    private int canvHeight;
    private volatile ArrayList<BlueRect> objectList;
	
	public CanvasView(Context context) {
		super(context);
		sh = getHolder();
		sh.addCallback(this);
		setFocusable(true);
		
		Display disp = ((WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		this.canvWidth = disp.getWidth();
		this.canvHeight = disp.getHeight();
		Log.d("Canvas", "h: "+ String.valueOf(canvHeight)+ " w:" + String.valueOf(canvWidth));
		
		executorService = new ThreadPoolExecutor(THREAD_COUNT,THREAD_COUNT,UNITS_TO_WAIT,
				TimeUnit.SECONDS, 
			    new ArrayBlockingQueue<Runnable>(THREAD_COUNT, true),
			    new ThreadPoolExecutor.DiscardOldestPolicy());
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev){
		final int maskedAction = (ev.getAction() & MotionEvent.ACTION_MASK);

		if(maskedAction  == MotionEvent.ACTION_DOWN){
			objectList = fillRectList();

			long startTime = System.nanoTime();
			drawSequentialRects();
			long timeTaken = System.nanoTime() - startTime;
			Log.d("Sequential ",String.valueOf(timeTaken));
			
			objectList = fillRectList();
			startTime = System.nanoTime();
			drawConcurrentRects();
			timeTaken = System.nanoTime() - startTime;
			Log.d("Concurrent ",String.valueOf(timeTaken));
		}
		else if(maskedAction == MotionEvent.ACTION_MOVE){
			Log.d("Action", "MOVE");
		}
		return true;
	}
	
	private void drawConcurrentRects(){
		Runnable r = new Runnable(){
			@Override
			public void run() {
				try{
					synchronized(sh){
							canv = sh.lockCanvas();
						}
					canv.restore();
					canv.drawColor(Color.WHITE);
						
					for(BlueRect rect : objectList){
						rect.drawOnCanvas(canv, paint);
						
					}
				}
				catch(Exception e){
					Log.e("Canvas drawing exception", e.getMessage());
				}
				finally{
					sh.unlockCanvasAndPost(canv);
				}
			}
		};
		
		executorService.submit(r);
	}
	
	private void drawSequentialRects(){
		try{
				synchronized(sh){
					canv = sh.lockCanvas();
				}
				canv.restore();
				canv.drawColor(Color.WHITE);
				
				for(BlueRect rect : objectList){
					rect.drawOnCanvas(canv, paint);
				}
		}
		catch(Exception e){
			Log.e("Canvas drawing exception", e.getMessage());
		}
		finally{
			sh.unlockCanvasAndPost(canv);
		}
	}
	
	private ArrayList<BlueRect> fillRectList(){
		ArrayList<BlueRect> ret = new ArrayList<BlueRect>();
		
		for(int i=0;i<200;i++){
			ret.add(new BlueRect(i*2,i*2+10));
		}
		
		return ret;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.canvWidth = width;
		this.canvHeight = height;
		this.sh = holder;
		Log.d("Canvas", "!h: "+ String.valueOf(canvHeight)+ " w:" + String.valueOf(canvWidth));
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
			canv = sh.lockCanvas();
			drawInitial(canv);
			sh.unlockCanvasAndPost(canv);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	private void drawInitial(Canvas canv){
		canv.restore();
		canv.drawColor(Color.WHITE);
	}
}
