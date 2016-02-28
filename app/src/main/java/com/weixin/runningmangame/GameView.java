package com.weixin.runningmangame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;



import java.util.Random;

/**
 * Created by Weixin on 2016-02-16.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Context myContext;
    MyThread myThread;
    private Bitmap background,leftBtn,rightBtn;
    private SurfaceHolder surfaceHolder;
    private boolean isRunning = false;
    private SurfaceHolder holder;
    int shootFramCount;
    int locationX = 0;
    int locationY = 0;
    int objectX = 2000;
    boolean drawObject = false;
    boolean pressed = false;
    boolean drawOrigPosition = true;
    boolean incrementCount = true;
    boolean run = true;
    int countFrame = 0;
    private Paint paint,paint_black,paint_transparent;
    private int countRun=0,objectMoveBackCount = 0,objectMoveBackCountTotal;
    private boolean isTrueCountRun=true,objectMoveBackTrue = false;
    boolean isShoot = false;
    private Bitmap run1,run2;
    private int screenX,screenY,scaledX,scaledY;
    private boolean isRightPressed,isLeftPressed;
    private int HPCount;
    //count down while the hp is 0, restart the game after gameOverCount is 0;
    private int gameOverCount;

    //if left or right is pressed, increase or decrease this value;
    private int countMovement;
    final int weaponLength = 120;
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        HPCount=6;
        holder.addCallback(this);
        myContext = context;
        myThread = new MyThread();
        myThread.setRunning(true);
        background = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.background);
        run1=BitmapFactory.decodeResource(myContext.getResources(), R.drawable.run1);
        run2 = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.run2);
        run1 = Bitmap.createScaledBitmap(run1, 150, 150, true);
        run2 = Bitmap.createScaledBitmap(run2, 150, 150, true);
        leftBtn = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.left);
        rightBtn = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.right);
        paint = new Paint();
        paint_black = new Paint();
        paint_transparent = new Paint();
        paint_transparent.setAntiAlias(true);
        paint_transparent.setColor(Color.TRANSPARENT);
        paint_black.setAntiAlias(true);

        paint_black.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setTextSize(60);
        isRightPressed = false;
        isLeftPressed = false;
        countMovement = 0;
        shootFramCount = 0;
        gameOverCount=300;
        Log.d("J","int the GameView Constructop");
    }

    class MyThread extends Thread{

        public MyThread(){

        }
        @Override
        public void run() {

            while(isRunning){
                Canvas c = null;
                c = holder.lockCanvas(null);
                if (new Random().nextInt(65000)%80==0)
                    drawObject = true;
                if (isRightPressed){
                    countMovement++;
                }else if (isLeftPressed){
                    countMovement--;
                }if (objectX>(200+countMovement*12)&&objectX < (200+countMovement*12+5)){
                    //object hits the man
                    HPCount--;
                    Log.d("J",HPCount+"hp count is ");
                }
                synchronized (holder) {
                    if (drawObject) {
                        //randomly move object back
                        if (objectMoveBackCount == 0){
                            objectMoveBackTrue = (new Random().nextInt(65000)%60==0);
                            objectMoveBackCountTotal = new Random().nextInt(15)+16;
                        }
                        if (objectMoveBackTrue&&objectMoveBackCount<objectMoveBackCountTotal){
                            objectMoveBackCount++;
                            objectX+=5;
                            if (objectX>screenX-60) {
                                objectX= screenX-60;
                                objectMoveBackCount = 0;
                                objectMoveBackTrue = false;
                            }
                        }else {
                            objectX -= 5;
                            if (objectX < 0)
                                objectX = screenX + 20;
                            objectMoveBackCount = 0;
                            objectMoveBackTrue = false;
                            objectMoveBackCountTotal=0;
                        }
                    }
                    c.drawBitmap(background, 0, 0, null);
                    draw(c);
                    if (pressed) {
                        drawOrigPosition = false;
                        //draw the drump
                        c.drawBitmap(run1,200+countMovement*12,(float) (screenY * 0.6 - countFrame * 16)-run1.getHeight()-60, null);
                        //c.drawRect(200, (float) (screenY * 0.8 - 80 - countFrame * 32), 220, (float) (screenY * 0.8 - countFrame * 32), paint);
                        //control the height of the jump
                        if (countFrame <18 && incrementCount) {
                            countFrame++;
                        }
                        else {
                            incrementCount = false;
                            countFrame--;
                            if (countFrame<0) {
                                countFrame = 0;
                                drawOrigPosition = true;
                                pressed = false;
                            }
                        }
                    }
                    if (objectX>=locationX &&objectX <=locationX+20 ){
                        //hit the object
                        Log.d("J", "hithit");
                        locationX =200;
                        drawObject = false;
                        isShoot = false;
                        objectX = screenX+200;
                    }
                }
                if (HPCount <=0){
                    if (gameOverCount<0) {
                        HPCount = 10;
                        gameOverCount = 300;
                    }else {
                        gameOverCount--;
                        c.drawBitmap(background, 0, 0, null);
                        c.drawText("Game Over, restart in : " + ((int)(gameOverCount/100)+1), (float) (screenX * 0.36), (float) (screenY * 0.3), paint);

                    }
                }
                holder.unlockCanvasAndPost(c);

            }
        }
        public void draw(Canvas canvas){
            //base line
            canvas.drawRect(0,(float)(screenY*0.6),screenX,(float)(screenY*0.6+15),paint);
            //hp line
            canvas.drawRect((float) (screenX*0.3),(float)(screenY*0.1),(float)(screenX*0.3+HPCount*20),(float)(screenY*0.1+15),paint);
            canvas.drawRect(screenX-160, (float) (screenY*0.8-screenY*0.1),screenX, (float) (screenY*0.8),paint);
            canvas.drawRect(screenX-480,  (float) (screenY*0.8-screenY*0.1), screenX-320,  (float) (screenY*0.8), paint);
            canvas.drawText("Jump", screenX - 126, (float) (screenY*0.8-screenY*0.04), paint_black);
            canvas.drawText("Hit", screenX - 426, (float) (screenY*0.8-screenY*0.04), paint_black);
            //draw the left and right move buttons{
            canvas.drawBitmap(leftBtn, 0, (int) (screenY * 0.69), null);
            canvas.drawBitmap(rightBtn,leftBtn.getWidth()+20,(int)(screenY*0.69),null);
            //end of drawing the left right button
            if (drawOrigPosition) {
                //canvas.drawRect(200, (float) (screenY * 0.8 - 80), 220, (float) (screenY * 0.8), paint);
                if (run) {
                    //canvas.drawRect(200, (float) (screenY * 0.8 - 80 - countFrame * 32), 220, (float) (screenY * 0.8 - countFrame * 32), paint);
                    if (countRun <8&&isTrueCountRun) {
                        countRun++;
                        canvas.drawBitmap(run1, 200+countMovement*12, (float) (screenY * 0.6 - countFrame * 16)-run1.getHeight(), null);
                    }else{
                        isTrueCountRun = false;
                        run = false;
                    }
                }else {
                    if (countRun>0&&!isTrueCountRun) {
                        countRun--;
                        canvas.drawBitmap(run2,200+countMovement*12, (float) (screenY * 0.6 - countFrame * 16)-run1.getHeight(), null);
                    }else{
                        run = true;
                        isTrueCountRun = true;
                    }
                    //canvas.drawRect(210, (float) (screenY * 0.8 - 80 - countFrame * 32), 230, (float) (screenY * 0.8 - countFrame * 32), paint);

                }
            }

            canvas.drawRect(objectX, (float) (screenY * 0.6 - 140), objectX+20, (float) (screenY * 0.6), paint);

            //draw bullet
            if(isShoot) {
//                canvas.drawCircle(locationX+countMovement*50, locationY, 10, paint);
//                locationX+=20;
                //control the speed that the weapon disapear
                shootFramCount++;
                if (shootFramCount >16) {
                    isShoot = false;
                    shootFramCount=0;
                }
                int x = locationX+countMovement*12+run1.getWidth();
                paint.setColor(Color.DKGRAY);
                canvas.drawRect(x, (float) (screenY * 0.6 - countFrame * 16 - run1.getHeight()) + 10, x + weaponLength, (float) (screenY * 0.6 - countFrame * 16 - run1.getHeight()) + 40, paint);
                paint.setColor(Color.RED);
            }else {
                int x = locationX+countMovement*12+run1.getWidth();
                canvas.drawRect(x,(float) (screenY * 0.6 - countFrame * 16-run1.getHeight())+10,x+weaponLength,(float) (screenY * 0.6 - countFrame * 16-run1.getHeight())+40,paint_transparent);
            }
        }
        public void setRunning(boolean r){isRunning = r;}

        public void onTouchEvent(MotionEvent event){

        }
    }

    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        float x= event.getX();
        float y= event.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                if (!pressed&& x >screenX-160&&x < screenX &&y >(float) (screenY*0.8-screenY*0.1) &&y <(float) (screenY*0.8)) {
                    pressed = true;
                    incrementCount = true;
                    countFrame = 0;
                }
                else if (x>screenX-480&&x<screenX-320 &&y >(float) (screenY*0.8-screenY*0.1) &&y <(float) (screenY*0.8)){
                    isShoot = true;
                    int tempX = locationX+countMovement*12+run1.getWidth();;
                    if (objectX > tempX && objectX< tempX+weaponLength){
                        //weapon touches the object, the object disapear
                        drawObject=false;
                        objectX = screenX+200;
                    }
                }else if (x>0&&x<leftBtn.getWidth() && (y>(screenY*0.1))&&y<(screenY+leftBtn.getHeight())){
                    //pressed the leftButton
                    isLeftPressed = true;
                }else if (x>(leftBtn.getWidth()+20)&&x<(leftBtn.getWidth()+rightBtn.getWidth()+20) && (y>(screenY*0.1))&&y<(screenY+leftBtn.getHeight())){
                    //pressed the right Button
                    isRightPressed = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isLeftPressed = false;
                isRightPressed = false;

                break;

            case MotionEvent.ACTION_MOVE:

                break;
        }


        return true;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("J","int the GameView surfaceCreated");

        //if (myThread.getState() ==Thread.State.NEW)
        myThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenX = width;
        screenY = height;
        double tempx = background.getWidth();
        double tempy = background.getHeight();
        double sx = width/tempx;
        double sy = height/tempy;
        locationX = 200;
        locationY = (int) ((screenY * 0.8 - countFrame * 20)-run1.getHeight() + 20);
        //Log.d("J","screen:"+tmx + "   "+height + " image"+tempx+"   "+background.getHeight());
        background =Bitmap.createScaledBitmap(background,(int)(background.getWidth()*sx),(int)(background.getHeight()*sy),true);
        leftBtn = Bitmap.createScaledBitmap(leftBtn,(int)(screenX*0.12),(int)(screenX*0.12),true);
        rightBtn = Bitmap.createScaledBitmap(rightBtn,(int)(screenX*0.12),(int)(screenX*0.12),true);
        paint.setTextSize((float)(screenX*0.03));
        paint_black.setTextSize((float)(screenX*0.02));
        objectX = screenX+200;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myThread.setRunning(false);
        myThread.destroy();
        myThread.stop();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
