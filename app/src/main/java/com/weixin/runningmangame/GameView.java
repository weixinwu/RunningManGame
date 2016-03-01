package com.weixin.runningmangame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
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
    private MyThread myThread;
    private Bitmap background,leftBtn,rightBtn,cloud;
    private SurfaceHolder surfaceHolder;
    private boolean isRunning = false;
    private SurfaceHolder holder;
    private int shootFramCount;
    private int locationX = 0;
    private int locationY = 0;
    private int countFrame = 0;
    private int objectX = 2000;
    private boolean drawObject = false;
    private boolean pressed = false;
    private boolean drawOrigPosition = true;
    private boolean incrementCount = true;
    private boolean run = true;
    private boolean initializeVar = false;
    private boolean drawPlus2 = false;
    private boolean drawminus2 = false;
    private boolean cloudCountIncrease = false;
    private boolean drawEnergyObjXbyCLoud = false;
    private boolean drawBombObjXbyCLoud = false;
    private Paint paint,paint_black,paint_transparent;
    private int countRun=0,objectMoveBackCount = 0,objectMoveBackCountTotal;
    private boolean isTrueCountRun=true,objectMoveBackTrue = false;
    private boolean isShoot = false;
    private Bitmap run1,run2,plus2,minus2;
    private int screenX,screenY,scaledX,scaledY;
    private boolean isRightPressed,isLeftPressed;
    private int HPCount;
    //count down while the hp is 0, restart the game after gameOverCount is 0;
    private int gameOverCount;
    private float energyObjX,energyObjY;
    private boolean drawEnergyObj;
    private float bombX,bombY;
    private boolean drawBomb;
    //if left or right is pressed, increase or decrease this value;
    private int countMovement;
    final int weaponLength = 120;
    private Bitmap title,bike,weapon,energyObj,fingerprint,upBtn;
    private int plus2X,plus2Y,minus2X,minus2Y,cloudX,cloudY,cloudCount;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        HPCount=6;
        holder.addCallback(this);
        myContext = context;
        myThread = new MyThread();
        cloudCount = 0;


        Log.d("J","int the GameView Constructop");
    }
    public void setIsRunning(boolean r){isRunning = r;}
    class MyThread extends Thread{

        public MyThread(){

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

        }
        @Override
        public void run() {

            int ranNum = new Random().nextInt(72386);
            while(isRunning){
                Canvas c = null;
                c = holder.lockCanvas(null);
                plus2Y-=20;
                minus2Y-=20;
                if (plus2Y<0) drawPlus2 = false;
                if (minus2Y<0) drawminus2 = false;
                if (!initializeVar){
                    initializeVar = true;
                }
                if ( !cloudCountIncrease){
                    cloudX-= 5;
                    if (cloudX<0) cloudCountIncrease = true;
                }else if (cloudCountIncrease){
                    cloudX+=5;
                    if (cloudX > screenX-cloud.getWidth()) cloudCountIncrease = false;
                }
                int ran = new Random().nextInt(65000);
                if (ran%80==0)
                    drawObject = true;
                if (ran %200 ==0)
                    drawEnergyObj = true;
                if (drawEnergyObj){
                    if (!drawEnergyObjXbyCLoud){
                        energyObjX = cloudX;
                        energyObjY = cloudY+20;
                        drawEnergyObjXbyCLoud = true;
                    }
                    if (ran%3==2)
                        energyObjX -= (ran % 3);
                    else energyObjX+= ran%2;
                    energyObjY+=(ran%5);
                }
                if (ran%82==0){
                    drawBomb = true;
                }
                if (drawBomb){
                    if (!drawBombObjXbyCLoud){
                        bombX = cloudX;
                        bombY = cloudY+20;
                        drawBombObjXbyCLoud = true;
                    }
                    if (ran%3==2)
                        bombX -= (ran % 3);
                    else bombX+= ran%2;
                    bombY+=(ran%5);
                }
                if (isRightPressed){
                    countMovement++;
                }else if (isLeftPressed){
                    countMovement--;
                }if (objectX>(200+countMovement*12)&&objectX < (200+countMovement*12+5)&&((float) (screenY * 0.6 - countFrame * 16)-run1.getHeight()<((float) (screenY * 0.6 - 140))&&(((float) (screenY * 0.6 - countFrame * 16))>(float) (screenY * 0.6 - 140)))){
                    //object hits the man
                    HPCount--;
                    Log.d("J",HPCount+"hp count is ");
                    drawminus2 = true;
                    minus2X = 200+countMovement*12;
                    minus2Y = screenY;
                }
                //synchronized (holder) {
                    if (drawObject) {
                        //randomly move object back
                        if (objectMoveBackCount == 0){
                            objectMoveBackTrue =(new Random().nextInt(651000)%100==0);
                            objectMoveBackCountTotal = ranNum%15+30;
                        }
                        if (objectMoveBackTrue&&objectMoveBackCount<objectMoveBackCountTotal){
                            objectMoveBackCount++;
                            objectX+=3;
                            //Log.d("J","in the move back");
                            if (objectX>screenX-60) {
                                objectX= screenX-60;
                                objectMoveBackCount = 0;
                                objectMoveBackTrue = false;
                            }
                        }else {
                            //
                            objectX -= 3;
                            if (objectX < 0)
                                objectX = screenX + 20;
                            objectMoveBackCount = 0;
                            objectMoveBackTrue = false;
                            objectMoveBackCountTotal=0;
                        }
                    }

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
                    if (objectX>=locationX &&objectX <=locationX+22 ){
                        //hit the object
                        //locationX =200;
                        drawObject = false;
                        isShoot = false;
                        objectX = screenX+200;

                    }
                //}
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
                if (energyObjX>(200+countMovement*12)&&energyObjX<(200+countMovement*12+run1.getWidth())&&
                        energyObjY+energyObj.getHeight()>((float) (screenY * 0.6 - countFrame * 16)-run1.getHeight())&&energyObjY<((float) (screenY * 0.6 - countFrame * 16))){
                    drawEnergyObj = false;
                    drawEnergyObjXbyCLoud = false;
                    double tempX = new Random().nextDouble()+0.05;
                    energyObjX= (int) (screenX*tempX);
                    energyObjY=-100;
                    HPCount+=2;
                    drawPlus2 = true;
                    plus2X = 200+countMovement*12;
                    plus2Y = screenY;

                }
                //bomb hits the man
                if (bombX>(200+countMovement*12)&&bombX<(200+countMovement*12+run1.getWidth())&&
                        (bombY+weapon.getHeight())>((float) (screenY * 0.6 - countFrame * 16-run1.getHeight()))&&bombY<((float) (screenY * 0.6 - countFrame * 16))){
                    drawBomb = false;
                    drawBombObjXbyCLoud = false;
                    double tempX = new Random().nextDouble()+0.05;
                    bombX= (int) (screenX*tempX);
                    bombY=-100;
                    HPCount-=2;
                    drawminus2 = true;
                    minus2X = 200+countMovement*12;
                    minus2Y = screenY;
                }
                holder.unlockCanvasAndPost(c);

            }
        }
        public void draw(Canvas canvas){

            //base line
            canvas.drawBitmap(background, 0, 0, null);
            canvas.drawRect(0, (float) (screenY * 0.6), screenX, (float) (screenY * 0.6 + 15), paint);
            //hp line text
            canvas.drawRect((float) (screenX*0.3),(float)(screenY*0.1),(float)(screenX*0.3+HPCount*20),(float)(screenY*0.1+15),paint);
            canvas.drawText("HP:", (float) (screenX * 0.3 - screenX * 0.05), (float) (screenY * 0.1), paint_black);
            //buttons
            //canvas.drawRect(screenX - 160, (float) (screenY * 0.8 - screenY * 0.1), screenX, (float) (screenY * 0.8), paint);
            canvas.drawBitmap(upBtn, screenX - upBtn.getWidth(), (float) (screenY * 0.8 - screenY * 0.1) , null);
            canvas.drawBitmap(fingerprint, screenX - 480, (float) (screenY * 0.8 - screenY * 0.1), null);
            //canvas.drawText("Jump", screenX - 126, (float) (screenY*0.8-screenY*0.04), paint_black);

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

           // canvas.drawRect(objectX, (float) (screenY * 0.6 - 140), objectX+20, (float) (screenY * 0.6), paint);
            canvas.drawBitmap(bike,objectX, (float) (screenY * 0.6)-bike.getHeight(),null);

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
            if (drawEnergyObj){
                canvas.drawBitmap(energyObj, energyObjX, energyObjY, null);
                if (energyObjX>screenX||energyObjY>screenY){
                    drawEnergyObj = false;
                    drawEnergyObjXbyCLoud = false;
                    int temp = new Random().nextInt(666666);
                    double tempx = ((temp%6)+3)*0.1;
                    energyObjX= (int) (screenX*tempx);
                    energyObjY=0;
                }
            }
            if (drawBomb){
                //canvas.drawCircle(bombX,bombY,30,paint_black);
                canvas.drawBitmap(weapon,bombX,bombY,null);
                if (bombX>screenX||bombY>screenY){
                    drawBomb = false;
                    drawBombObjXbyCLoud = false;
                    int temp = new Random().nextInt(666666);
                    double tempx = ((temp%6)+3)*0.1;
                    bombX= (int) (screenX*tempx);
                    bombY=0;
                }
            }
            if (drawPlus2){
                canvas.drawBitmap(plus2,plus2X,plus2Y,null);
            }
            if (drawminus2){
                canvas.drawBitmap(minus2,minus2X,minus2Y,null);
            }
            canvas.drawBitmap(cloud, cloudX, cloudY,null);

        }
        public void setRunning(boolean r){isRunning = r;}


    }

    public boolean onTouchEvent(MotionEvent event){
        int action = MotionEventCompat.getActionMasked(event);
        int index = MotionEventCompat.getActionIndex(event);
        float x,y;
        if (event.getPointerCount()>1) {
             x = MotionEventCompat.getX(event,index);
             y =  MotionEventCompat.getY(event, index);
        }else {
             x = MotionEventCompat.getX(event,index);
             y =  MotionEventCompat.getY(event, index);
        }
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
                    isRightPressed= false;
                    isLeftPressed = true;
                }else if (x>(leftBtn.getWidth()+20)&&x<(leftBtn.getWidth()+rightBtn.getWidth()+20) && (y>(screenY*0.1))&&y<(screenY+leftBtn.getHeight())){
                    //pressed the right Button
                    isLeftPressed=false;
                    isRightPressed = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("J","action up");
                isLeftPressed = false;
                isRightPressed = false;

                break;

            case MotionEvent.ACTION_MOVE:
                if (x>0&&x<leftBtn.getWidth() && (y>(screenY*0.1))&&y<(screenY+leftBtn.getHeight())){
                    //pressed the leftButton
                    isRightPressed = false;
                    isLeftPressed = true;

                }else if (x>(leftBtn.getWidth()+20)&&x<(leftBtn.getWidth()+rightBtn.getWidth()+20) && (y>(screenY*0.1))&&y<(screenY+leftBtn.getHeight())){
                    //pressed the right Button
                    isLeftPressed= false;
                    isRightPressed = true;

                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:

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
                    isRightPressed= false;
                    isLeftPressed = true;
                }else if (x>(leftBtn.getWidth()+20)&&x<(leftBtn.getWidth()+rightBtn.getWidth()+20) && (y>(screenY*0.1))&&y<(screenY+leftBtn.getHeight())){
                    //pressed the right Button
                    isLeftPressed =false;
                    isRightPressed = true;
                }

                break;
            case MotionEvent.ACTION_POINTER_UP:

                isLeftPressed = false;
                isRightPressed = false;

                break;
            case MotionEvent.ACTION_POINTER_INDEX_SHIFT:
                break;
        }


        return true;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("J", "begin the surfaceCreated");

        //if (myThread.getState() ==Thread.State.NEW)

        background = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.background);
        run1=BitmapFactory.decodeResource(myContext.getResources(), R.drawable.run1);
        run2 = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.run2);
        run1 = Bitmap.createScaledBitmap(run1, 150, 150, true);
        run2 = Bitmap.createScaledBitmap(run2, 150, 150, true);
        leftBtn = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.left);
        rightBtn = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.right);
        plus2 = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.plus2);
        plus2= Bitmap.createScaledBitmap(plus2, 120, 120, true);
        minus2= BitmapFactory.decodeResource(myContext.getResources(), R.drawable.minus2);
        minus2 = Bitmap.createScaledBitmap(minus2, 120, 120, true);
        cloud= BitmapFactory.decodeResource(myContext.getResources(), R.drawable.cloud);
        cloud = Bitmap.createScaledBitmap(cloud, 150, 150, true);
        bike= BitmapFactory.decodeResource(myContext.getResources(), R.drawable.bike);
        bike= Bitmap.createScaledBitmap(bike, 150, 150, true);
        weapon= BitmapFactory.decodeResource(myContext.getResources(), R.drawable.weapon);
        weapon= Bitmap.createScaledBitmap(weapon, 80, 80, true);
        energyObj=BitmapFactory.decodeResource(myContext.getResources(), R.drawable.energyobj);
        energyObj= Bitmap.createScaledBitmap(energyObj, 80, 80, true);
        fingerprint=BitmapFactory.decodeResource(myContext.getResources(), R.drawable.fingerprint);
        fingerprint= Bitmap.createScaledBitmap(fingerprint, 180, 180, true);
        upBtn=BitmapFactory.decodeResource(myContext.getResources(), R.drawable.up);
        upBtn= Bitmap.createScaledBitmap(upBtn, 180,180, true);
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

        paint.setTextSize((float)(screenX*0.03));
        paint_black.setTextSize((float)(screenX*0.02));
        objectX = screenX+200;
        energyObjX = (int) (screenX*(new Random().nextDouble()+0.1));
        energyObjY = 0;
        cloudX = (int) (screenX*0.2);
        cloudY = (int) (screenY*0.12);
        background =Bitmap.createScaledBitmap(background,(int)(background.getWidth()*sx),(int)(background.getHeight()*sy),true);
        leftBtn = Bitmap.createScaledBitmap(leftBtn, (int) (screenX * 0.12), (int) (screenX * 0.12), true);
        rightBtn = Bitmap.createScaledBitmap(rightBtn, (int) (screenX * 0.12), (int) (screenX * 0.12), true);

        myThread.setRunning(true);
        myThread.start();
        Log.d("J","Finished the surfaceChange");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myThread.setRunning(false);

        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
