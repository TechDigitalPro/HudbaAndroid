package com.uru.hudba.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mancj.slideup.SlideUp;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.activity.SplashActivity;
import com.uru.hudba.base.BaseActivity;
import com.uru.hudba.base.BaseFragment;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.musicservice.NotiService;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_PLAYING_MODE;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_EMAIL;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_FIRSTNAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_IMAGE;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_LASTNAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_NAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_VOLUME_MODE;

public class SettingFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;

    private SlideUp settingSlideView;
    private View setting_panel;
    private ImageView setting_back;
    private CircleImageView setting_profile_img;
    private TextView setting_theme, tv_user_name, tv_first_name, tv_last_name, tv_setting_title, tv_version;
    private Button setting_logout;
    private LinearLayout setting_mainview, setting_playview, setting_find, setting_info, profile_item, player_item, library_item, find_item, contact_item, information_item, review_item,
                            find_face, find_instagram, find_twitter, info_term, info_privacy, info_office ;

    private SwitchCompat play_modeSwitch, volume_modeSwitch;

    private String cur_playing_mode = "photo", cur_volume_mode = "invisible";
    private String user_name, user_firstname, user_lastname, user_image;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).current_page = "setting";
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        setting_mainview = rootView.findViewById(R.id.main_setting);
        setting_playview = rootView.findViewById(R.id.play_setting);
        setting_find = rootView.findViewById(R.id.find_setting);
        setting_info = rootView.findViewById(R.id.info_setting);

        tv_setting_title = rootView.findViewById(R.id.setting_title);
        setting_back = rootView.findViewById(R.id.back);
        setting_back.setOnClickListener(this);

        setting_profile_img = rootView.findViewById(R.id.user_image);
        tv_user_name = rootView.findViewById(R.id.name);

        profile_item = rootView.findViewById(R.id.li_profile);
        profile_item.setOnClickListener(this);

        player_item = rootView.findViewById(R.id.li_play);
        player_item.setOnClickListener(this);
        play_modeSwitch = rootView.findViewById(R.id.sw_play);
        volume_modeSwitch = rootView.findViewById(R.id.sw_volume);
        play_modeSwitch.setOnClickListener(this);
        volume_modeSwitch.setOnClickListener(this);

        library_item = rootView.findViewById(R.id.li_library);
        library_item.setOnClickListener(this);
        review_item = rootView.findViewById(R.id.li_review);
        review_item.setOnClickListener(this);
        find_item = rootView.findViewById(R.id.li_find);
        find_item.setOnClickListener(this);
        contact_item = rootView.findViewById(R.id.li_contact);
        contact_item.setOnClickListener(this);
        information_item = rootView.findViewById(R.id.li_information);
        information_item.setOnClickListener(this);


        setting_logout = rootView.findViewById(R.id.btn_logout);
        setting_logout.setOnClickListener(this);

        find_face = rootView.findViewById(R.id.li_find_face);
        find_face.setOnClickListener(this);
        find_instagram = rootView.findViewById(R.id.li_find_instagram);
        find_instagram.setOnClickListener(this);
        find_twitter = rootView.findViewById(R.id.li_find_twitter);
        find_twitter.setOnClickListener(this);

        info_term = rootView.findViewById(R.id.li_info_term);
        info_term.setOnClickListener(this);
        info_privacy = rootView.findViewById(R.id.li_info_privacy);
        info_privacy.setOnClickListener(this);
        info_office = rootView.findViewById(R.id.li_info_office);
        info_office.setOnClickListener(this);
        tv_version = rootView.findViewById(R.id.version);

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            tv_version.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        user_name = App.readSharedSetting(PRE_USER_NAME, "");
        user_image = App.readSharedSetting(PRE_USER_IMAGE, "");
        if(!TextUtils.isEmpty(user_name)){
            tv_user_name.setText(user_name);
        }
        Glide.with(getContext())
                .load(user_image)
                .placeholder(R.drawable.ic_profile)
                .into(setting_profile_img);
        cur_playing_mode = App.readSharedSetting(PRE_PLAYING_MODE, "photo");
        if(cur_playing_mode.equalsIgnoreCase("photo")){
            play_modeSwitch.setChecked(false);
        }else{
            play_modeSwitch.setChecked(true);
        }
        cur_volume_mode = App.readSharedSetting(PRE_VOLUME_MODE, "invisible");
        if(cur_volume_mode.equalsIgnoreCase("invisible")){
            volume_modeSwitch.setChecked(false);
        }else{
            volume_modeSwitch.setChecked(true);
        }

        play_modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(play_modeSwitch.isChecked()){
                    cur_playing_mode = "video";
                }else{
                    cur_playing_mode = "photo";
                }
                App.saveSharedSetting(PRE_PLAYING_MODE, cur_playing_mode);
            }
        });
        volume_modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(volume_modeSwitch.isChecked()){
                    cur_volume_mode = "visible";
                }else{
                    cur_volume_mode = "invisible";
                }
                App.saveSharedSetting(PRE_VOLUME_MODE, cur_volume_mode);
            }
        });
        return rootView;
    }
    private void logout(){
        String member_key = App.readSharedSetting(PRE_MEMBER_KEY,"");
        if (App.isNetworkAvailable(getActivity())) {
            ((MainActivity)getActivity()).showProgressDialog(getActivity());
            Ion.with(getActivity())
                    .load(GlobalConstants.LogoutUrl + "&memberKey=" + member_key)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
//                            CommonUtils.hideLoadingDialog();
                            ((MainActivity)getActivity()).hideProgressDialog();
                            if (e == null) {
                                try {
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        App.saveSharedSetting(PRE_MEMBER_KEY, "");
                                        App.saveSharedSetting(PRE_USER_ID, "");
                                        App.saveSharedSetting(PRE_USER_EMAIL, "");
                                        App.saveSharedSetting(PRE_USER_FIRSTNAME, "");
                                        App.saveSharedSetting(PRE_USER_LASTNAME, "");
                                        App.saveSharedSetting(PRE_USER_IMAGE, "");
                                    } else {
                                        ((MainActivity)getActivity()).showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                    Intent intent = new Intent(getActivity(), NotiService.class);
                                    intent.setAction(NotiService.ACTION_STOP);
                                    startService(intent);

                                    Intent i = new Intent(getActivity(), SplashActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
                            }
                        }
                    });
        } else {
            ((MainActivity)getActivity()).showErrorMessages(R.string.txt_network_error);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                if(setting_mainview.getVisibility() == View.GONE && setting_playview.getVisibility() == View.VISIBLE){
                    ((BaseActivity)getActivity()).animateVisibilityHidden(setting_playview);
                    ((BaseActivity)getActivity()).animateVisibilityShown(setting_mainview);
                    tv_setting_title.setText(getResources().getString(R.string.setting));
                }else if(setting_mainview.getVisibility() == View.GONE && setting_find.getVisibility() == View.VISIBLE){
                    ((BaseActivity)getActivity()).animateVisibilityHidden(setting_find);
                    ((BaseActivity)getActivity()).animateVisibilityShown(setting_mainview);
                    tv_setting_title.setText(getResources().getString(R.string.setting));
                }else if(setting_mainview.getVisibility() == View.GONE && setting_info.getVisibility() == View.VISIBLE){
                    ((BaseActivity)getActivity()).animateVisibilityHidden(setting_info);
                    ((BaseActivity)getActivity()).animateVisibilityShown(setting_mainview);
                    tv_setting_title.setText(getResources().getString(R.string.setting));
                }else{
                    popFragment();
                }

                break;
            case R.id.li_profile:
                ProfileChangeFragment frag = new ProfileChangeFragment();
                pushFragment(frag, true, ProfileChangeFragment.class.getName());
                break;
            case R.id.li_library:
                ((MainActivity)getActivity()).setLibrary();
                break;
            case R.id.li_review:
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                }
                catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case R.id.li_information:
                ((BaseActivity)getActivity()).animateVisibilityHidden(setting_mainview);
                ((BaseActivity)getActivity()).animateVisibilityShown(setting_info);
                tv_setting_title.setText(getResources().getString(R.string.information));
                break;
            case R.id.li_contact:
                ContactusFragment contactusFragment = new ContactusFragment();
                pushFragment(contactusFragment, true, ContactusFragment.class.getName());
                break;

            case R.id.li_play:
                ((BaseActivity)getActivity()).animateVisibilityHidden(setting_mainview);
                ((BaseActivity)getActivity()).animateVisibilityShown(setting_playview);
                tv_setting_title.setText(getResources().getString(R.string.player));
                break;
            case R.id.li_find:
                ((BaseActivity)getActivity()).animateVisibilityHidden(setting_mainview);
                ((BaseActivity)getActivity()).animateVisibilityShown(setting_find);
                tv_setting_title.setText(getResources().getString(R.string.findus));
                break;
            case R.id.li_find_face:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConstants.find_face_link));
                startActivity(browserIntent);
                break;
            case R.id.li_find_instagram:
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConstants.find_instagram_link));
                startActivity(browserIntent2);
                break;
            case R.id.li_find_twitter:
                Intent browserIntent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConstants.find_twitter_link));
                startActivity(browserIntent3);
                break;
            case R.id.li_info_term:
                Intent termintent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConstants.term_link));
                startActivity(termintent);
                break;
            case R.id.li_info_privacy:
                Intent privacyintent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConstants.privacy_link));
                startActivity(privacyintent);
                break;
            case R.id.li_info_office:
                Intent officeintent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConstants.office_link));
                startActivity(officeintent);
                break;
            case R.id.btn_logout:
                View dialogView = getLayoutInflater().inflate(R.layout.dlg_exit, null);

                final android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(getContext())
                        .setView(dialogView)
                        .setCancelable(true)
                        .create();

                dlg.setCanceledOnTouchOutside(true);
                final TextView ok = dialogView.findViewById(R.id.ok);
                final TextView cancel = dialogView.findViewById(R.id.cancel);
                final TextView confirm_txt = dialogView.findViewById(R.id.confirm_txt);
                confirm_txt.setText(R.string.msg_logout);
                cancel.setText("Cancel");
                cancel.setVisibility(View.VISIBLE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.dismiss();
                        logout();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.dismiss();
                    }
                });

                dlg.setCanceledOnTouchOutside(false);
                dlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dlg.show();
                break;
        }
    }
}