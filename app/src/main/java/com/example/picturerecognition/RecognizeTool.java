package com.example.picturerecognition;

import android.os.FileUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.picturerecognition.FileUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class RecognizeTool {
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String accessTokenUrl;
    public String accessToken;
    public String result;
    public String localImagePath;

    public RecognizeTool(String imgPath){
        if(imgPath != null){
            localImagePath = imgPath;
        }
        clientId = "8EPmQoopy0YSfuoBlmEl6QsM";
        clientSecret = "wGvSGAaTtoUKFcOEkQEFhMsicqMWjATI";
        grantType = "client_credentials";
        accessTokenUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=" + grantType + "&client_id=" + clientId + "&client_secret=" + clientSecret;
        try{
            URL realRrl = new URL(accessTokenUrl);
            HttpURLConnection connection = (HttpURLConnection) realRrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            Map<String, List<String>> map = connection.getHeaderFields();
            for(String key : map.keySet()){
                Log.d("=====>", String.valueOf(map.get(key)));
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while((line = in.readLine() )!= null){
                result += line;
            }
            JSONObject jsonObject = new JSONObject(result);
            accessToken = jsonObject.getString("access_token");
            Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@", accessToken);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String recognizePlant(){
        String urlForPlant = "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant";
        try{
            byte[] imgData = FileUtil.readFileByBytes(localImagePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr,"UTF-8");

            String param = "image=" + imgParam +"&baike_num=1";

            result = HttpUtil.post(urlForPlant,accessToken,param);
            Log.d("=============================================================>>",result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getScore(){
        String scoreRet;
        if(null != result){
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                JSONObject jObj = jsonArray.getJSONObject(0);
                String Score = String.valueOf(jObj.getDouble("score"));
                return Score;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  null;
    }

    public String[] getInfo(){
        String[] infoRet = new String[3];
        if(null != result){
            try{
                JSONObject jsonObject = new JSONObject(result);
                String resultName = jsonObject.getJSONArray("result").getJSONObject(0).getString("name");
                JSONObject jsonObj = jsonObject.getJSONArray("result").getJSONObject(0).getJSONObject("baike_info");
                String baikeUrl = null;
                if(jsonObj.has("baike_url"))
                    baikeUrl =  "】\n\n百度百科： " + jsonObj.getString("baike_url") + "\n\n简介： " + jsonObj.getString("description");
                infoRet[0] = "鉴定结果： 【" + resultName +  baikeUrl;
                if(jsonObj.has("image_url")) {
                    infoRet[1] = jsonObj.getString("image_url");
                }
                else{
                    infoRet[1] = "https://www.hualigs.cn/image/60ee6f4350b36.jpg";
                }
                infoRet[2] = resultName;
                return infoRet;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void addNewReco(String imgUrl, String userName, String info){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String urlToReq = "https://southstem.cloud/addNewRec?user=" + userName + "&imgUrl=" + imgUrl + "&info=" + info;
                    URL reqUrl = new URL(urlToReq);
                    HttpURLConnection connection = (HttpURLConnection)reqUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(8000);
                    if(connection.getResponseCode() == 200) {
                        Log.d("=====================>>", "success");
                    }
                    connection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
