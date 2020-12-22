package com.uru.hudba.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.auth.LoginActivity;
import com.uru.hudba.activity.intro.IntroActivity;
import com.uru.hudba.base.BaseActivity;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_EMAIL;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_FIRSTNAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_IMAGE;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_LASTNAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_NAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_PASS;

public class SplashActivity extends BaseActivity {
    private ImageView app_logo;
    private TextView app_title;
    private long showStartTime;
    private final static long DELAY_TIME = 2000;
    private boolean isRunning;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        app_logo = findViewById(R.id.logo);
        app_title = findViewById(R.id.name);
        startSplash();
        startAnimation();
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
                    // Delay Time
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - showStartTime < DELAY_TIME) {
                        try {
                            // Delay for DELAY_TIME
                            Thread.sleep(showStartTime + DELAY_TIME
                                    - currentTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }catch (Exception e){
                    return;
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            doFinish();
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
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }
    private void onSignin(){
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
}
