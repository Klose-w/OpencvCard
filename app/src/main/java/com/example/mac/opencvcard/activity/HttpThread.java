package com.example.mac.opencvcard.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;


public class HttpThread extends Thread {

    private String url="http://115.159.188.113/CaoCao/";
    private Map map;
    private Handler handler;

    public HttpThread(Handler handler, Map map) {
        this.map = map;
        this.handler = handler;
        this.url+=map.get("method");
    }



    public JSONObject createJSON(){
        JSONObject result = new JSONObject();
        try {
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                result.put(""+entry.getKey() , ""+entry.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void run() {
        super.run();
        try {
            //http请求
            URL httpUrl=new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");

            //创建json数据
            JSONObject jsonData =createJSON();
            String result= String.valueOf(jsonData);
            System.out.println("___________________________");
            System.out.println(result);
            //设置连接
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            //输出流
            OutputStream outputStream=connection.getOutputStream();
            outputStream.write(result.getBytes());
            outputStream.close();

            //判断返回值 200 为正常返回
            if(connection.getResponseCode()==200){
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader
                        (connection.getInputStream())) ;
                String line=null;
                final StringBuffer stringBuffer=new StringBuffer();
                while((line=bufferedReader.readLine())!=null){
                    stringBuffer.append(line);
                }
                JSONObject jsonObject= new JSONObject(stringBuffer.toString());
                bufferedReader.close();
                System.out.println("___________________________");
                System.out.println(jsonObject);
                Message msg = new Message();
                Bundle bundler = new Bundle();// 存放数据
                bundler.putString("state", "" + jsonObject.getString("message"));
                msg.setData(bundler);
                handler.sendMessage(msg);
            }
            else{
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putString("state","error");
                msg.setData(b);
                handler.sendMessage(msg);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
