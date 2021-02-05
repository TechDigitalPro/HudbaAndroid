package com.uru.hudba.activity.auth;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uru.hudba.application.App;
import com.uru.hudba.R;
import com.uru.hudba.base.BaseActivity;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import static com.uru.hudba.constant.GlobalConstants.PRE_FAVDATA;
import static com.uru.hudba.constant.GlobalConstants.PRE_HOMEDATA;
import static com.uru.hudba.constant.GlobalConstants.PRE_IMAGEPATH;
import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_RECENTLIST;
import static com.uru.hudba.constant.GlobalConstants.PRE_SAVEDATA;
import static com.uru.hudba.constant.GlobalConstants.PRE_SONGCNT;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_EMAIL;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_FIRSTNAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_IMAGE;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_LASTNAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_NAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_PASS;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backBtn;
    private TextInputLayout editEmail, editFirst, editLast, editPassword, editRepeat;
    private Button signUpBtn, termsBtn, privacyBtn;
    private String email, first_name, last_name, password, repass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
    }

    private void initView() {
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);
        editEmail = findViewById(R.id.edit_email);
        editFirst = findViewById(R.id.first_name);
        editLast = findViewById(R.id.last_name);
        editPassword = findViewById(R.id.edit_password);
        editRepeat = findViewById(R.id.edit_repeat_password);
        signUpBtn = findViewById(R.id.signup_btn);
        signUpBtn.setOnClickListener(this);
        termsBtn = findViewById(R.id.signup_terms);
        termsBtn.setOnClickListener(this);
        privacyBtn = findViewById(R.id.signup_privacy);
        privacyBtn.setOnClickListener(this);
        termsBtn.setPaintFlags(termsBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        privacyBtn.setPaintFlags(privacyBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                Intent intent = new Intent(SignUpActivity.this, AuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.signup_btn:
                validCheck();
                break;
            case R.id.signup_terms:
                gotoTerms();
                break;
            case R.id.signup_privacy:
                gotoPrivacy();
                break;
        }
    }

    private void validCheck(){
        first_name = editFirst.getEditText().getText().toString();
        last_name = editLast.getEditText().getText().toString();
        email = editEmail.getEditText().getText().toString().trim();
        password = editPassword.getEditText().getText().toString().trim();
        repass = editRepeat.getEditText().getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            editEmail.setError(getResources().getString(R.string.msg_input_email));
            return;
        }else if(!isValidEmail(email)){
            editEmail.setError(getResources().getString(R.string.msg_invalid_mail));
            return;
        }else if(TextUtils.isEmpty(first_name)){
            editFirst.setError(getResources().getString(R.string.msg_input_name));
            return;
        }else if(TextUtils.isEmpty(last_name)){
            editLast.setError(getResources().getString(R.string.msg_input_name));
            return;
        }else if(TextUtils.isEmpty(password)){
            editPassword.setError(getResources().getString(R.string.msg_input_password));
            return;
        }else if(TextUtils.isEmpty(repass)){
            editRepeat.setError(getResources().getString(R.string.msg_input_confirmpassword));
            return;
        }else if(!password.equalsIgnoreCase(repass)){
            editRepeat.setError(getResources().getString(R.string.msg_valid_confirmpassword));
            return;
        }else{
            onSignup();
        }
    }
    private void onSignup(){
        String device_key = "";
        try{
            device_key = CommonUtils.getDeviceToken(getApplicationContext());
        }catch (Exception e){
            device_key = "";
        }
        if (App.isNetworkAvailable(getApplicationContext())) {
            showProgressDialog(SignUpActivity.this);
            Ion.with(getApplicationContext())
                    .load(GlobalConstants.SignupemailUrl)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("first_name", first_name)
                    .setBodyParameter("last_name", last_name)
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

                                        App.saveSharedSetting(PRE_HOMEDATA, "");
                                        App.saveSharedSetting(PRE_RECENTLIST, "");
                                        App.saveSharedSetting(PRE_SAVEDATA, "");
                                        App.saveSharedSetting(PRE_FAVDATA, "");
                                        App.saveSharedSetting(PRE_SONGCNT, "0");
                                        App.saveSharedSetting(PRE_IMAGEPATH, "");


                                        gotoMainActivity();
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
    private void gotoPrivacy() {
    }

    private void gotoTerms() {
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }
}