package com.amazon.rvspeedtest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.amazon.rvspeedtest.dto.RegistrationResponse;
import com.amazon.rvspeedtest.dto.ServerResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by joshroha on 4/16/2015.
 */
public class ServerUtil {
    public static <T extends ServerResponse,S extends Serializable> void SendToServer(S dataToSend, final T dataToReceive,String stringURL, final Context context){
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                performNetworkActivity((S)params[0], (String)params[1],(T)params[2]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                handleResponse(dataToReceive,context);
            }

            private void handleResponse(T response,Context context) {
                if(response == null)
                {
                    Toast.makeText(context, "Got empty response. Could not contact server.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Got empty response. Could not contact server.");
                }
                if(response.getError() != null)
                {
                    Toast.makeText(context,response.getError(),Toast.LENGTH_LONG).show();
                    Log.e(TAG,response.getError());
                }
                else
                {
                    Toast.makeText(context,"Everything looks good.",Toast.LENGTH_LONG).show();
                }
            }

        }.execute(dataToSend,stringURL,dataToReceive);

    }

    private static <T extends ServerResponse,S extends Serializable> void performNetworkActivity(S dataToSend, String stringURL, T dataToReceive) {
        URL url;
        HttpURLConnection urlCon;
        ObjectOutputStream out;
        Gson gson = new GsonBuilder().create();
        try {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpClient httpClient = new DefaultHttpClient(httpParams);

            HttpPost request = new HttpPost(stringURL);
            request.addHeader("content-type", "application/json");
            String JSONData = gson.toJson(dataToSend);
            StringEntity params = new StringEntity(JSONData);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            String responseJSON = EntityUtils.toString(response.getEntity(),"UTF-8");
           // dataToReceive = (T)gson.fromJson(responseJSON.trim(),dataToReceive.getClass());
            dataToReceive.setError(responseJSON);
            int i = 0;
            //Not doing anything with the received data.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int TIMEOUT_MILLISEC = 10000;
    private static final String TAG = "Server";
}
