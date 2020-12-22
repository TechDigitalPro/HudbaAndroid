package com.uru.hudba.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.farshidabz.poizitoast.PoiziToast;
import com.uru.hudba.R;
import com.uru.hudba.sqlite.DatabaseQueryClass;

public class BaseActivity extends AppCompatActivity {
    protected ProgressDialog mProgress;
    Context mContext;
    public DatabaseQueryClass databaseQueryClass = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mContext = this;
//        databaseQueryClass = new DatabaseQueryClass(mContext);
    }
    public static void setFullScreen(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = activity.getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
    public void setTransparentStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
    public void setStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    public void showProgressDialog(Activity activity) {
        try {
            if (mProgress == null){
                mProgress = new ProgressDialog(activity, R.style.DialogTheme);
                mProgress.setMessage(getString(R.string.loading));
                mProgress.setCancelable(false);
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                mProgress.setContentView(R.layout.dialog_loading);
            }else if(mProgress.isShowing()){
                return;
            }else{
                mProgress.show();
                mProgress.setContentView(R.layout.dialog_loading);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void hideProgressDialog() {
        if (mProgress != null && mProgress.isShowing())
            mProgress.dismiss();
    }

    public void animateVisibilityShown(View view) {

        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(300).start();
        view.animate().alpha(1f).setDuration(300).start();
    }
    public void animateVisibilityHidden(final View view) {
        view.animate().alpha(0f).setDuration(300).start();
        view.animate().alpha(0f).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.GONE);
            }
        }).start();
    }
    public void pushFragment(Fragment fragment, boolean addToBackStack, String string) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String name = getCurrentFragment() != null ? getCurrentFragment().getClass().getName() : null;
        if (addToBackStack) {
            transaction.addToBackStack(name);
        }

//        transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
        transaction.replace(R.id.fragmentContainer, fragment, string);
        transaction.commitAllowingStateLoss();

    }
    public boolean popFragment() {
        try {

            boolean isPop = false;
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                isPop = true;
                getSupportFragmentManager().popBackStack();
            }
            return isPop;

        } catch (Exception e) {
            return false;
        }
    }
    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    @SuppressLint("ResourceType")
    public void showMessages(int resID){
        PoiziToast.with(this)
                .setBackgroundColor(getResources().getColor(R.color.green_dark))
                .setTextColor(getResources().getColor(R.color.white_color))
                .setTextSize(14)
                .makeToast(getResources().getString(resID), Toast.LENGTH_SHORT)
                .show();
    }
    @SuppressLint("ResourceType")
    public void showMessages(String str){
        PoiziToast.with(this)
                .setBackgroundColor(getResources().getColor(R.color.green_dark))
                .setTextColor(getResources().getColor(R.color.white_color))
                .setTextSize(14)
                .makeToast(str, Toast.LENGTH_SHORT)
                .show();
    }
    @SuppressLint("ResourceType")
    public void showErrorMessages(int resID){
        PoiziToast.with(this)
                .setBackgroundColor(getResources().getColor(R.color.red_light))
                .setTextColor(getResources().getColor(R.color.white_color))
                .setTextSize(14)
                .makeToast(getResources().getString(resID), Toast.LENGTH_SHORT)
                .show();
    }
    @SuppressLint("ResourceType")
    public void showErrorMessages(String str){
        PoiziToast.with(this)
                .setBackgroundColor(getResources().getColor(R.color.red_light))
                .setTextColor(getResources().getColor(R.color.white_color))
                .setTextSize(14)
                .makeToast(str, Toast.LENGTH_SHORT)
                .show();
    }
    public Fragment getCurrentFragment()
    {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
        return currentFragment;
    }
}
