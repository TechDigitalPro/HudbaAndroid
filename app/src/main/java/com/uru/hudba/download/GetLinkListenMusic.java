package com.uru.hudba.download;

import android.os.AsyncTask;

import com.uru.hudba.interfaces.JsonGetLinkListenMusic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class GetLinkListenMusic extends AsyncTask<String, Void, String> {
    private JsonGetLinkListenMusic callBack;

    public void setCallBack(JsonGetLinkListenMusic callBack) {
        this.callBack = callBack;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            Document doc = Jsoup.connect("https://api.savemedia.website/convert/")
                    .ignoreContentType(true)
                    .data("url", strings[0])
                    .data("convert", "gogogo")
                    .post();
            if (doc != null) {
                return getLinkVideo(doc.toString());
            }
        } catch (Exception e) {

        }

        return "";
    }

    private String getLinkVideo(String toString) {
        if (toString.contains("jobid")) {
            int index = toString.indexOf("jobid\":\"");
            String jobId = toString.substring(index + 8);
            int indexLast = jobId.indexOf("\"");
            jobId = jobId.substring(0, indexLast);
            return jobId;
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (callBack != null) {
            if (s.equals("") || s == null) {
                callBack.getLinkFail();
            } else {
                callBack.getLinkSuccess("https://api.savemedia.website/convert/?jobid=" + s);
            }
        }
    }
}
