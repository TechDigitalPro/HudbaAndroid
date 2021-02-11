package com.uru.hudba.activity.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

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
            case R.id.forgot_btn:
                onForgot();
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

    private void onForgot(){
        View dialogView = getLayoutInflater().inflate(R.layout.dlg_forgot_pass, null);


        AlertDialog emailDlg = new android.app.AlertDialog.Builder(LoginActivity.this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        final TextView ok = dialogView.findViewById(R.id.ok);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final EditText new_pass = dialogView.findViewById(R.id.pass_et);
        final EditText confirm_pass = dialogView.findViewById(R.id.confirm_pass_et);
        final EditText email_et = dialogView.findViewById(R.id.email_et);
        email_et.requestFocus();
        final ToggleButton toggoleBtnf = dialogView.findViewById(R.id.toggoleBtn);
        toggoleBtnf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    new_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else{
                    new_pass.setInputType(129);

                }
            }
        });
        final ToggleButton toggoleBtnf1 = dialogView.findViewById(R.id.toggoleBtn1);
        toggoleBtnf1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    confirm_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else{
                    confirm_pass.setInputType(129);

                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailDlg.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email_et.getText().toString().equals("") || new_pass.getText().toString().equals("") || confirm_pass.getText().toString().equals("")){
                    showErrorMessages("Please confirm field");
                }else if(!isValidEmail(email_et.getText().toString().trim())){
                    showErrorMessages("Please confirm your mail");
                }
                else if(!new_pass.getText().toString().equalsIgnoreCase(confirm_pass.getText().toString())){
                    showErrorMessages("Please confirm new password");
                }else{
                    emailDlg.dismiss();
                    if (App.isNetworkAvailable(getApplicationContext())) {
                        CommonUtils.showLoadingDialog(LoginActivity.this);
                        CommonUtils.hideKeyboard(LoginActivity.this);
                        Ion.with(getApplicationContext())
                                .load(GlobalConstants.ForgotUrl)
                                .setTimeout(CommonUtils.NETWORK_TIME)
                                .setBodyParameter("email", email_et.getText().toString().trim())
                                .setBodyParameter("new_password", new_pass.getText().toString().trim())
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        CommonUtils.hideLoadingDialog();
                                        if (e == null) {
                                            try {
                                                JSONObject resultObj = new JSONObject(result);
                                                if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                                    showMessages(resultObj.getString("api_result"));
                                                } else {
                                                    showErrorMessages(resultObj.getString("err_msg"));
                                                }
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }
                                        } else {
                                            showErrorMessages(R.string.txt_network_error);
                                        }
                                    }
                                });
                    } else {
                        showErrorMessages(R.string.txt_network_error);
                    }
                }

            }
        });
        emailDlg.setCanceledOnTouchOutside(true);
        emailDlg.setCancelable(true);
        emailDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        emailDlg.show();
    }
}