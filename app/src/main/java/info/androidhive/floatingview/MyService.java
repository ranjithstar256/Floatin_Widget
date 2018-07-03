package info.androidhive.floatingview;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class MyService extends Service {

    final Handler handler = new Handler();
    private WindowManager mWindowManager;
    private View mFloatingView;
    WindowManager.LayoutParams params;
    ImageView imageView;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
       imageView=(ImageView) mFloatingView.findViewById(R.id.collapsed_iv);
        imageView.setAlpha(50);
         params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;
        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);  //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout

        //Set the close button
        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
            }
        });

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkConnection();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        //if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        //if (mFloatingView1 != null) mWindowManager.removeView(mFloatingView1);
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void checkConnection() {
        if (mFloatingView != null) {
            if (isOnline()) {
                imageView.setImageResource(R.drawable.green);
            } else {
                imageView.setImageResource(R.drawable.red);
            }
        }
        else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        // if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
//            mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widgettt, null);
        //initializeView(2);
        // Toast.makeText(MyService.this, "You are not connected to Internet", Toast.LENGTH_SHORT).show();
        //mWindowManager.removeView(mFloatingView);
        //mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        //Add the view to the window.
        //stopSelf();
        //mWindowManager.removeView(mFloatingView);
        // startService(new Intent(vv, FloatingViewService.class));
        //           // initializeView(1);
        //            // Toast.makeText(MyService.this, "You are connected to Internet", Toast.LENGTH_SHORT).show();
        //            //Inflate the floating view layout we created
        //            //stopSelf();
        //            //mWindowManager.removeView(mFloatingView);
    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

//    private void initializeView(int y) {
//        if (y==1){
//                startService(new Intent(getApplicationContext(), FloatingViewService.class).putExtra("STATE",y));
//        }else if(y==2) {
//            startService(new Intent(getApplicationContext(), FloatingViewService.class).putExtra("STATE",y));
//
//        }
//    }
}