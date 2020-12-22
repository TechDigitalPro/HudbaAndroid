package com.uru.hudba.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.SplashActivity;
import com.uru.hudba.activity.intro.IntroActivity;
import com.uru.hudba.base.BaseActivity;
import com.uru.hudba.activity.MainActivity;
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

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backBtn;
    private Button loginBtn;
    private TextView forgotBtn;
    private TextInputLayout editEmail, editPassword;

    private String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);
        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        forgotBtn = findViewById(R.id.forgot_btn);
        forgotBtn.setOnClickListener(this);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                Intent intent = new Intent(LoginActivity.this, AuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.login_btn:
                validCheck();
                break;
        }
    }
    private void validCheck(){
        email = editEmail.getEditText().getText().toString().trim();
        password = editPassword.getEditText().getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            editEmail.setError(getResources().getString(R.string.msg_input_email));
            return;
        }else if(!isValidEmail(email)){
            editEmail.setError(getResources().getString(R.string.msg_invalid_mail));
            return;
        }else if(TextUtils.isEmpty(password)){
            editPassword.setError(getResources().getString(R.string.msg_input_password));
            return;
        }else{
            onSignin();
        }
    }

    private void onSignin(){
        String device_key = "";
        try{
            device_key = CommonUtils.getDeviceToken(getApplicationContext());
        }catch (Exception e){
            device_key = "";
        }
        if (App.isNetworkAvailable(LoginActivity.this)) {
            showProgressDialog(LoginActivity.this);
            Ion.with(LoginActivity.this)
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


    private void gotoMainActivity() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }
}