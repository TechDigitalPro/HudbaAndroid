package com.uru.hudba.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ligl.android.widget.iosdialog.IOSSheetDialog;
import com.uru.hudba.App;
import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.base.BaseFragment;
import com.uru.hudba.constant.CommonUtils;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.filecompress.IImageCompressTaskListener;
import com.uru.hudba.filecompress.ImageCompressTask;
import com.uru.hudba.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.uru.hudba.constant.GlobalConstants.PICK_FROM_CAMERA;
import static com.uru.hudba.constant.GlobalConstants.PICK_FROM_FILE;
import static com.uru.hudba.constant.GlobalConstants.PRE_MEMBER_KEY;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_EMAIL;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_FIRSTNAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_IMAGE;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_LASTNAME;
import static com.uru.hudba.constant.GlobalConstants.PRE_USER_NAME;

public class ProfileChangeFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private CircleImageView img_profile;
    private ImageView submit;
    private EditText et_pass, et_email, et_first_name, et_last_name;
    private TextView btn_change;
    private String user_email, user_name, user_firstname, user_lastname, user_image = "";
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(1);
    private ImageCompressTask imageCompressTask;
    private String filePath;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).current_page = "profile_change";
        rootView = inflater.inflate(R.layout.fragment_profile_change, container, false);
        user_email = App.readSharedSetting(PRE_USER_EMAIL, "");
        user_name = App.readSharedSetting(PRE_USER_NAME, "");
        user_firstname = App.readSharedSetting(PRE_USER_FIRSTNAME, "");
        user_lastname = App.readSharedSetting(PRE_USER_LASTNAME, "");
        user_image = App.readSharedSetting(PRE_USER_IMAGE, "");

        et_pass = rootView.findViewById(R.id.et_pass);
        img_profile = rootView.findViewById(R.id.profile);

        et_email = rootView.findViewById(R.id.et_email);
        et_first_name = rootView.findViewById(R.id.et_firstname);
        et_last_name = rootView.findViewById(R.id.et_lastname);
        btn_change = rootView.findViewById(R.id.btn_change);
        btn_change.setOnClickListener(this);
        rootView.findViewById(R.id.submit).setOnClickListener(this);

        Glide.with(getContext())
                .load(user_image)
                .placeholder(R.drawable.ic_profile)
                .into(img_profile);

        et_email.setText(user_email);
        et_first_name.setText(user_firstname);
        et_last_name.setText(user_lastname);

        rootView.findViewById(R.id.back).setOnClickListener(this);
        return rootView;
    }
    private void onChange(){

    }
    private void showImageDlg(){
        IOSSheetDialog.SheetItem[] items = new IOSSheetDialog.SheetItem[2];
        items[0] = new IOSSheetDialog.SheetItem("Gallery", IOSSheetDialog.SheetItem.BLUE);
        items[1] = new IOSSheetDialog.SheetItem("Camera", IOSSheetDialog.SheetItem.BLUE);
        new IOSSheetDialog.Builder(getContext())
                .setData(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            openGallery();
                        } else if (which == 1) {
                            openCamera();
                        }
                    }
                }).show();
    }
    private void openCamera(){
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                File file = new File(dir + "/Hudba");
                                if (!file.exists()){
                                    file.mkdir();
                                }
                                filePath = dir + "/Hudba/tmpImage" + timeStamp + ".jpg";
                                File imgFile = new File(filePath);
                                try {
                                    imgFile.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Uri imageUri = FileProvider.getUriForFile(
                                        getActivity(),
                                        getActivity().getPackageName() + ".fileprovider",
                                        imgFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent, PICK_FROM_CAMERA);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Camera & Write permissions are needed to take a photo.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private void openGallery(){
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @SuppressLint("IntentReset")
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_FILE);
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getActivity(), "Camera permission is needed to take a photo.", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override public void onError(DexterError error) {
                        Log.e("Dexter", "There was an error: " + error.toString());
                    }
                }).check();
    }
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_FROM_FILE) {
            try {
                String path = getRealPathFromUri(data.getData());
                imageCompressTask = new ImageCompressTask(getContext(), path, iImageCompressTaskListener);
                mExecutorService.execute(imageCompressTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ( resultCode == RESULT_OK && requestCode == PICK_FROM_CAMERA ) {
            try {
                imageCompressTask = new ImageCompressTask(getContext(), filePath, iImageCompressTaskListener);
                mExecutorService.execute(imageCompressTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //image compress task callback
    private IImageCompressTaskListener iImageCompressTaskListener = new IImageCompressTaskListener() {
        @Override
        public void onComplete(List<File> compressed) {
            File file = compressed.get(0);
            uploadImage(file);
        }

        @Override
        public void onError(Throwable error) {
            //very unlikely, but it might happen on a device with extremely low storage.
            //log it, log.WhatTheFuck?, or show a dialog asking the user to delete some files....etc, etc
            Log.wtf("ImageCompressor", "Error occurred", error);
        }
    };
    private void uploadImage(File image_file){
        String member_key = App.readSharedSetting("member_key","");
        if (App.isNetworkAvailable(getActivity())) {
            CommonUtils.showLoadingDialog(getActivity());
            Ion.with(getActivity())
                    .load(GlobalConstants.UpdateProfileImageUrl + "&memberKey=" + member_key)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setMultipartFile("image", "application/form-data", image_file)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            CommonUtils.hideLoadingDialog();
                            if (e == null) {
                                try {
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        ((MainActivity)getActivity()).showMessages(R.string.msg_upload_success);
                                        Glide.with(getContext())
                                                .load(image_file)
                                                .placeholder(R.drawable.ic_profile)
                                                .into(img_profile);

                                        JSONObject api_result = resultObj.getJSONObject("api_result");
                                        Gson gson = new Gson();
                                        UserModel userModel = gson.fromJson(api_result.getJSONObject("user_info").toString(), UserModel.class);
                                        CommonUtils.currentUser = userModel;
                                        App.saveSharedSetting(PRE_USER_EMAIL, userModel.getEmail());
                                        App.saveSharedSetting(PRE_USER_IMAGE, userModel.getImage());
                                        App.saveSharedSetting(PRE_USER_NAME, userModel.getName());
                                        App.saveSharedSetting(PRE_USER_FIRSTNAME, userModel.getFirst_name());
                                        App.saveSharedSetting(PRE_USER_LASTNAME, userModel.getLast_name());
                                        popFragment();

                                    } else {
                                        ((MainActivity)getActivity()).showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                    ((MainActivity)getActivity()).showErrorMessages(ex.getMessage());
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
    private void changeProfile(){
        String member_key = App.readSharedSetting("member_key","");
        if (App.isNetworkAvailable(getActivity())) {
            CommonUtils.showLoadingDialog(getActivity());
            Ion.with(getActivity())
                    .load(GlobalConstants.UpdateProfileUrl + "&memberKey=" + member_key)
                    .setTimeout(CommonUtils.NETWORK_TIME)
                    .setBodyParameter("password", et_pass.getText().toString().trim())
                    .setBodyParameter("first_name", et_first_name.getText().toString().trim())
                    .setBodyParameter("last_name", et_last_name.getText().toString().trim())
                    .setBodyParameter("email", et_email.getText().toString().trim())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            CommonUtils.hideLoadingDialog();
                            if (e == null) {
                                try {
                                    JSONObject resultObj = new JSONObject(result);
                                    if (resultObj.getString("res").equalsIgnoreCase("success")) {
                                        ((MainActivity)getActivity()).showMessages(R.string.msg_profilechange_success);

                                        JSONObject api_result = resultObj.getJSONObject("api_result");
                                        Gson gson = new Gson();
                                        UserModel userModel = gson.fromJson(api_result.getJSONObject("user_info").toString(), UserModel.class);
                                        CommonUtils.currentUser = userModel;
                                        App.saveSharedSetting(PRE_USER_EMAIL, userModel.getEmail());
                                        App.saveSharedSetting(PRE_USER_IMAGE, userModel.getImage());
                                        App.saveSharedSetting(PRE_USER_NAME, userModel.getName());
                                        App.saveSharedSetting(PRE_USER_FIRSTNAME, userModel.getFirst_name());
                                        App.saveSharedSetting(PRE_USER_LASTNAME, userModel.getLast_name());

                                        popFragment();

                                    } else {
                                        ((MainActivity)getActivity()).showErrorMessages(resultObj.getString("err_msg"));
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                    ((MainActivity)getActivity()).showErrorMessages(ex.getMessage());
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
                popFragment();
                break;
            case R.id.btn_change:
                if(TextUtils.isEmpty(et_pass.getText().toString())){
                    ((MainActivity)getContext()).showErrorMessages("Please input current password");
                }else if(TextUtils.isEmpty(et_email.getText().toString().trim())){
                    ((MainActivity)getContext()).showErrorMessages("Please input email address");
                }else if(!((MainActivity)getContext()).isValidEmail(et_email.getText().toString())){
                    ((MainActivity)getContext()).showErrorMessages("Please input valid email address");
                }else if(TextUtils.isEmpty(et_first_name.getText().toString().trim())){
                    ((MainActivity)getContext()).showErrorMessages("Please input first name");
                }else if(TextUtils.isEmpty(et_last_name.getText().toString().trim())){
                    ((MainActivity)getContext()).showErrorMessages("Please input last name");
                }else{
                    changeProfile();
                }
                break;
            case R.id.submit:
                showImageDlg();
                break;
        }
    }

}