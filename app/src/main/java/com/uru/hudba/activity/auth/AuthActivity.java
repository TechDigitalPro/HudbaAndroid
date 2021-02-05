package com.uru.hudba.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uru.hudba.application.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.base.BaseActivity;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.uru.hudba.constant.GlobalConstants.PRE_USER_EMAIL;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_FIRSTNAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_ID;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_IMAGE;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_LASTNAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_NAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_PASS;

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    private Button facebookButton, signupButton, loginButton;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setFullScreen(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("facebook", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("facebook", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TAGggg", "facebook:onError", error);
                showErrorMessages("Facebook login failed");
            }
        });

        initView();
    }

    private void initView() {
        facebookButton = findViewById(R.id.auth_facebook_button);
        facebookButton.setOnClickListener(this);
        signupButton = findViewById(R.id.auth_signup_button);
        signupButton.setOnClickListener(this);
        loginButton = findViewById(R.id.auth_login_button);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.auth_facebook_button:
                facebookLogin();
                break;
            case R.id.auth_signup_button:
                gotoSignUp();
                break;
            case R.id.auth_login_button:
                gotoLogin();
                break;
        }
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void facebookLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null){
            handleFacebookAccessToken(accessToken);
        }
        else{
            LoginManager.getInstance().logInWithReadPermissions(AuthActivity.this, Arrays.asList("email", "public_profile"));
        }
    }
    private void handleFacebookAccessToken(AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(token, (object, response) -> {
            try {
                if(object!=null){
                    String id = object.getString("id");
                    String email = object.getString("email");
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    requestFacebookAvatar(token, id, first_name,last_name, email);
                }
            } catch (Exception e) {
                showErrorMessages("Facebook login failed");
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,link");
        request.setParameters(parameters);
        request.executeAsync();
    }
    private void requestFacebookAvatar(AccessToken token, String userId, String first_name,String last_name, String email) {
        GraphRequest request = new GraphRequest(token, "/" + userId + "/picture", null, HttpMethod.GET, response -> {
            try {
                String url = response.getJSONObject().getJSONObject("data").getString("url");
                fbLogin(userId, email, first_name, last_name, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putBoolean("redirect", false);
        parameters.putString("type", "normal");
        request.setParameters(parameters);
        request.executeAsync();
    }
    private void fbLogin(String id, String emails, String first_name, String last_name , String url){
        if (App.isNetworkAvailable(getApplicationContext())) {
            Ion.with(getApplicationContext())
                .load(GlobalConstants.FaceBookLoginUrl)
                .setTimeout(CommonUtils.NETWORK_TIME)
                .setBodyParameter("facebook_id", id)
                .setBodyParameter("email", emails)
                .setBodyParameter("first_name", first_name)
                .setBodyParameter("last_name", last_name)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e == null) {
                            try {
                                JSONObject resultObj = new JSONObject(result);
                                if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                    JSONObject api_result = resultObj.getJSONObject("api_result");
                                    App.saveSharedSetting("member_key", api_result.getString("memberKey") );

                                    Gson gson = new Gson();
                                    UserModel userModel = gson.fromJson(api_result.getJSONObject("user_info").toString(), UserModel.class);
                                    CommonUtils.currentUser = userModel;

                                    App.saveSharedSetting(PRE_USER_EMAIL, userModel.getEmail());
                                    App.saveSharedSetting(PRE_USER_PASS, "");
                                    App.saveSharedSetting(PRE_USER_ID, userModel.getId());
                                    App.saveSharedSetting(PRE_USER_IMAGE, userModel.getImage());
                                    App.saveSharedSetting(PRE_USER_NAME, userModel.getName());
                                    App.saveSharedSetting(PRE_USER_FIRSTNAME, userModel.getFirst_name());
                                    App.saveSharedSetting(PRE_USER_LASTNAME, userModel.getLast_name());
                                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                                    finish();
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(mCallbackManager!=null){
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}