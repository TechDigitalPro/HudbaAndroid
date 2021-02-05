package com.uru.hudba.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.uru.hudba.application.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.intro.IntroActivity;

import static com.google.android.gms.ads.appopen.AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT;
import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;

public class SplashActivity extends Activity {
    private ImageView app_logo;
    private TextView app_title;
    private long showStartTime;
    private final static long DELAY_TIME = 1000;
    private boolean isRunning;

    private String AD_UNIT_ID;
    private AppOpenAd appOpenAd = null;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private FullScreenContentCallback fullScreenContentCallback;
    private static boolean isShowingAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);
            app_logo = findViewById(R.id.logo);
            app_title = findViewById(R.id.name);
            startSplash();
            startAnimation();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startAds() {
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });
        AD_UNIT_ID = getString(R.string.admob_appopen);
        showAdIfAvailable();
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public boolean isAdAvailable() {
        return appOpenAd != null;
    }

    public void showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            fullScreenContentCallback = new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            appOpenAd = null;
                            isShowingAd = false;
                            doFinish();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {}

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };

            appOpenAd.show(this, fullScreenContentCallback);

        } else {
            fetchAd();
        }
    }

    public void fetchAd() {
        if (isAdAvailable()) {
            return;
        }
        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAppOpenAdLoaded(AppOpenAd ad) {
                        appOpenAd = ad;
                        showAdIfAvailable();
                    }

                    @Override
                    public void onAppOpenAdFailedToLoad(LoadAdError loadAdError) {
                        doFinish();
                    }
                };
        AdRequest request = getAdRequest();
        AppOpenAd.load(this, AD_UNIT_ID, request, APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    private void startAnimation() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                app_logo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(ObjectAnimator.ofFloat(app_logo, "alpha", 0, 1, 1, 1),
                                ObjectAnimator.ofFloat(app_logo, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                                ObjectAnimator.ofFloat(app_logo, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                        mAnimatorSet.setDuration(500);
                        mAnimatorSet.start();
                    }
                });
                app_title.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(ObjectAnimator.ofFloat(app_title, "alpha", 0, 1, 1, 1),
                                ObjectAnimator.ofFloat(app_title, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                                ObjectAnimator.ofFloat(app_title, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                        mAnimatorSet.setDuration(500);
                        mAnimatorSet.start();
                    }
                });

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startSplash(){
        showStartTime = System.currentTimeMillis();
        isRunning = true;

        Thread background = new Thread() {
            public void run(){
                try{
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - showStartTime < DELAY_TIME) {
                        try {
                            Thread.sleep(showStartTime + DELAY_TIME - currentTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (App.isNetworkAvailable(SplashActivity.this)){
                                startAds();
                            }else {
                                doFinish();
                            }
                        }
                    });
                }
            }
        };
        background.start();
    }

    private void doFinish(){
        if(this.isRunning){
            try {
                String member_key = App.readSharedSetting(PRE_MEMBER_KEY, "");
                if(TextUtils.isEmpty(member_key)){
                    startActivity(new Intent(SplashActivity.this,IntroActivity.class));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(this.isRunning)
            this.isRunning = false;
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    /*private void onSignin(){
        String device_key = "";
        try{
            device_key = CommonUtils.getDeviceToken(getApplicationContext());
        }catch (Exception e){
            device_key = "";
        }
        String email = App.readSharedSetting(PRE_USER_EMAIL, "");
        String password = App.readSharedSetting(PRE_USER_PASS, "");
        if (App.isNetworkAvailable(SplashActivity.this)) {
            showProgressDialog(SplashActivity.this);
            Ion.with(SplashActivity.this)
                    .load(GlobalConstants.LoginUrl)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("email", email)
                    .setBodyParameter("password", password)
                    .setBodyParameter("device_key", device_key)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            hideProgressDialog();
                            if (e == null) {
                                try {
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        JSONObject api_result = resultObj.getJSONObject("api_result");
                                        App.saveSharedSetting(PRE_MEMBER_KEY, api_result.getString("memberKey") );
                                        Gson gson = new Gson();
                                        UserModel userModel = gson.fromJson(api_result.getJSONObject("user_info").toString(), UserModel.class);
                                        CommonUtils.currentUser = userModel;
                                        App.saveSharedSetting(PRE_USER_EMAIL, userModel.getEmail());
                                        App.saveSharedSetting(PRE_USER_PASS, password);
                                        App.saveSharedSetting(PRE_USER_ID, userModel.getId());
                                        App.saveSharedSetting(PRE_USER_IMAGE, userModel.getImage());
                                        App.saveSharedSetting(PRE_USER_NAME, userModel.getName());
                                        App.saveSharedSetting(PRE_USER_FIRSTNAME, userModel.getFirst_name());
                                        App.saveSharedSetting(PRE_USER_LASTNAME, userModel.getLast_name());
                                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        finish();
                                    } else {
                                        showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                showErrorMessages(R.string.txt_server_error);
                            }
                        }
                    });
        } else {
            showErrorMessages(R.string.txt_network_error);
        }
    }*/
}
