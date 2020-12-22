package com.uru.hudba.filecompress;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.uru.hudba.activity.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by root on 9/14/17.
 */

public class ImageCompressTask implements Runnable {

    private Context mContext;
    private List<String> originalPaths = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<File> result = new ArrayList<>();
    private IImageCompressTaskListener mIImageCompressTaskListener;


    public ImageCompressTask(Context context, String path, IImageCompressTaskListener compressTaskListener) {

        originalPaths.add(path);
        mContext = context;
        mIImageCompressTaskListener = compressTaskListener;
    }
    public ImageCompressTask(Context context, List<String> paths, IImageCompressTaskListener compressTaskListener) {
        originalPaths = paths;
        mContext = context;
        mIImageCompressTaskListener = compressTaskListener;
    }
    @Override
    public void run() {

            //Loop through all the given paths and collect the compressed file from Util.getCompressed(Context, String)
            for(int i = 0; i < originalPaths.size(); i++){
                try {
                    File file = Util.getCompressed(mContext, originalPaths.get(i));
                    if(file == null){
                        int finalI = i;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //originalPaths.get(finalI) +
                                ((MainActivity)mContext).showErrorMessages("Invalid File type");
                            }
                        });
                        continue;
                    }else{
                        result.add(file);
                    }

                }catch (final IOException ex) {
                    //There was an error, report the error back through the callback
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mIImageCompressTaskListener != null)
                                mIImageCompressTaskListener.onError(ex);
                        }
                    });
                }

            }

            //use Handler to post the result back to the main Thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(mIImageCompressTaskListener != null)
                            mIImageCompressTaskListener.onComplete(result);
                    }catch (Exception e){
                        ((MainActivity)mContext).showErrorMessages("Invalid File type");
                        e.printStackTrace();
                    }

                }
            });

    }
}
