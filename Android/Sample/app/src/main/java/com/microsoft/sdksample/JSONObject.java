/**
package com.microsoft.sdksample;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class JSONObject{

    public JSONObject postFile(String url,String filePath,int id){
        String result="";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        File file = new File(filePath);
        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody cbFile = new FileBody(file, "image/jpeg");
        StringBody stringBody= null;
        JSONObject responseObject=null;
        try {
            stringBody = new StringBody(id+"", ContentType);
            mpEntity.addPart("file", cbFile);
            mpEntity.addPart("id",stringBody);
            httpPost.setEntity(mpEntity);
            System.out.println("executing request " + httpPost.getRequestLine());
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            result=resEntity.toString();
            responseObject=new JSONObject(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseObject;
    }
}
**/