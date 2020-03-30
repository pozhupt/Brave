package com.rest.brave.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.rest.brave.R;


public class Splash extends Activity {

    boolean allowint;

    public void showintsingle()
    {
        try {
            final InterstitialAd mInterstitialAd1;
            mInterstitialAd1 = new InterstitialAd(this);
            mInterstitialAd1.setAdUnitId("ca-app-pub-2038038715841690/7042674787");
            mInterstitialAd1.loadAd(new AdRequest.Builder().build());

            ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(myProcess);
            boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            boolean con=true;
            while(con) {
                if(mInterstitialAd1.isLoaded()) {
                    con=false;
                    if (allowint && !isInBackground) {
                        mInterstitialAd1.show();
                        Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                        Splash.this.startActivity(mainIntent);
                        Splash.this.finish();
                    }
                }


            }
        }
        catch(Exception E)
        {
        }
    }
    public void showint()
    {
        final InterstitialAd mInterstitialAd1;
        mInterstitialAd1 = new InterstitialAd(this);
        mInterstitialAd1.setAdUnitId("ca-app-pub-2038038715841690/7042674787");
        mInterstitialAd1.loadAd(new AdRequest.Builder().build());

        mInterstitialAd1.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
                ActivityManager.getMyMemoryState(myProcess);
                boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                if (!isInBackground) {
                    Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                    mInterstitialAd1.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });
    }


    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 4000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
        allowint=true;
        MobileAds.initialize(this, "ca-app-pub-2038038715841690~4712310327");
        showint();

/*
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
*/


    }

    @Override
    protected void onPause() {
        allowint=false;
        super.onPause();

    }
    @Override
    protected void onDestroy() {
        allowint=false;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        allowint = false;
        super.onPause();

    }
}