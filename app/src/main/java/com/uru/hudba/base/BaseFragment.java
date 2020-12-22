package com.uru.hudba.base;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Fade;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionSet;

import com.uru.hudba.R;
import com.uru.hudba.constant.BundleExtra;

public class BaseFragment extends Fragment {

    final String TAG = "BaseFragment";
    public boolean isLoading = false;
    protected Handler mBaseHandler = new Handler();
    protected View mRootView;
    protected View mWrapperView;
    protected boolean mStateRecreated;
    private BaseActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout root = new LinearLayout(getActivity());
        return root;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        //dismissSnackBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //dismissSnackBar();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showProgress(boolean show) {

        if (show)
            isLoading = true;
        else
            isLoading = false;

        try {
            // getCurrentAct().showProgress(show);
        } catch (Exception e) {
        }
    }

    public void onSelectionItemClicked(int positon){

    }
    public BaseActivity getCurrentAct() {
        try {
            return (BaseActivity) getActivity();
        } catch (Exception e) {
            return null;
        }
    }

    public void startActivityWithAnimation(Class className) {

        try {
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.push_left_in, R.anim.push_left_out);
            startActivity(new Intent(getActivity(), className), options.toBundle());
        } catch (Exception e) {
        }
    }

    public void startActivityWithAnimation(Intent intent) {

        try {
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.push_left_in, R.anim.push_left_out);
            startActivity(intent, options.toBundle());
        } catch (Exception e) {
        }
    }

    public void startService(Intent service) {
        getContext().startService(service);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleExtra.EXTRA_STATE_RECREATED, true);
    }

    public void onRestoreInstanceState(Bundle outState) {
        if (outState != null) {
            mStateRecreated = outState.getBoolean(BundleExtra.EXTRA_STATE_RECREATED, false);
        }
    }

    public FragmentActivity getContext() {
        if (getActivity() != null) {
            return getActivity();
        }

        return mActivity;
    }

    public void runOnUiThread(Runnable r) {
        if (getContext() == null) {
            return;
        }

        getContext().runOnUiThread(r);
    }

    protected void doIn(Runnable r, long delay) {
        mBaseHandler.postDelayed(r, delay);
    }

    protected void doNow(Runnable r) {
        mBaseHandler.post(r);
    }

    protected void cancel(Runnable r) {
        mBaseHandler.removeCallbacks(r);
    }

    protected void run(Runnable r) {
        new Thread(r).start();
    }

    /**
     * @param fragment
     * @param addToBackStack
     */

    public void pushFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();

        String name = mActivity.getCurrentFragment() != null ? mActivity.getCurrentFragment().getClass().getName() : null;
        if (addToBackStack) {
            transaction.addToBackStack(name);
        }

        transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commitAllowingStateLoss();
    }

    public void pushFragment(Fragment fragment, boolean addToBackStack, String string) {
        FragmentTransaction transaction = ((BaseActivity)getActivity()).getSupportFragmentManager().beginTransaction();
        String name = ((BaseActivity)getActivity()).getCurrentFragment() != null ? ((BaseActivity)getActivity()).getCurrentFragment().getClass().getName() : null;
        if (addToBackStack) {
            transaction.addToBackStack(name);
        }

//        transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
        transaction.replace(R.id.fragmentContainer, fragment, string);
        transaction.commitAllowingStateLoss();

    }

    public void pushFragment(Fragment fragment, boolean addToBackStack, String string, View view, View view2, View view3) {
        FragmentTransaction transaction = ((BaseActivity)getActivity()).getSupportFragmentManager().beginTransaction();


        final long MOVE_DEFAULT_TIME = 150;
        final long FADE_DEFAULT_TIME = 150;

        String name = ((BaseActivity)getActivity()).getCurrentFragment() != null ? ((BaseActivity)getActivity()).getCurrentFragment().getClass().getName() : null;
        if (addToBackStack) {
            transaction.addToBackStack(name);
        }


        // 1. Exit for Previous Fragment
        Fade exitFade = new Fade();
        exitFade.setDuration(300);
        this.setExitTransition(exitFade);

        // 2. Shared Elements Transition
        TransitionSet enterTransitionSet = new TransitionSet();
        enterTransitionSet.addTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        enterTransitionSet.setDuration(MOVE_DEFAULT_TIME);
        enterTransitionSet.setStartDelay(FADE_DEFAULT_TIME);
        fragment.setSharedElementEnterTransition(enterTransitionSet);

        // 3. Enter Transition for New Fragment
        Fade enterFade = new Fade();
        enterFade.setStartDelay(100);
        enterFade.setDuration(FADE_DEFAULT_TIME);
        //fragment.setEnterTransition(enterFade);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transaction.addSharedElement(view, view.getTransitionName());
            transaction.addSharedElement(view2, view2.getTransitionName());
            transaction.addSharedElement(view3, view3.getTransitionName());
        }


        transaction.replace(R.id.fragmentContainer, fragment, string);
        transaction.commitAllowingStateLoss();

        //mActivity.hideLoadMoreProgress();

    }

    /**
     * Pop current fragment from stack.
     *
     * @return
     */
    public boolean popFragment() {
        try {

            boolean isPop = false;
            if (((BaseActivity)getActivity()).getSupportFragmentManager().getBackStackEntryCount() > 0) {
                isPop = true;
                ((BaseActivity)getActivity()).getSupportFragmentManager().popBackStack();
            }
            return isPop;

        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Pop current fragment from stack.
     *
     * @return
     */
    public boolean popFragment(String tag) {
        //MainActivity.hideAPiCallProgressbar();
        //CommonUtils.hideSoftKeyboard(mActivity);

        Fragment fragment = ((BaseActivity)getActivity()).getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            ((BaseActivity)getActivity()).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            return true;
        }
        return false;
    }

    public boolean popFragment(String tag, String tag2) {
        //MainActivity.hideAPiCallProgressbar();
        //CommonUtils.hideSoftKeyboard(mActivity);

        Fragment fragment = ((BaseActivity)getActivity()).getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            ((BaseActivity)getActivity()).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        fragment = ((BaseActivity)getActivity()).getSupportFragmentManager().findFragmentByTag(tag2);
        if (fragment != null) {
            ((BaseActivity)getActivity()).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            return true;
        }

        return false;
    }

    /**
     * Pop all fragments from stack
     */
    public void popAllFragments() {
        //MainActivity.hideAPiCallProgressbar();
        ((BaseActivity)getActivity()).getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Pop all fragments from stack upto the specified fragment.
     *
     * @param tag fragment name upto that all fragments will remove from stack.
     * @return
     */
    public boolean popFragmentUpto(String tag) {
        //MainActivity.hideAPiCallProgressbar();
        if (((BaseActivity)getActivity()).getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE))
            return true;
        else
            return false;
    }

    /*  *//**
     * Pop all fragments from stack upto the specified fragment.
     *
     * @param tag fragment name upto that all fragments will remove from stack.
     * @return
     *//*
    public boolean popFragmentUptoParent(String tag) {
        if (((BaseActivity)getActivity()).getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE))
            return true;
        else
            return false;
    }
*/

}
