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
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by joshroha on 4/16/2015.
 */
public class ServerUtil {
    public static <T extends ServerResponse, S extends Serializable> void SendToServer(S dataToSend, final T dataToReceive, String stringURL, final Context context) {
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                performNetworkActivity((S) params[0], (String) params[1], (T) params[2]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                handleResponse(dataToReceive, context);
            }

            private void handleResponse(T response, Context context) {
                if (response == null) {
                    Toast.makeText(context, "Got empty response. Could not contact server.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Got empty response. Could not contact server.");
                }
                if (response.getError() != null) {
                    Toast.makeText(context, response.getError(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, response.getError());
                } else {
                    Toast.makeText(context, "You are registered!", Toast.LENGTH_LONG).show();
                }
            }

        }.execute(dataToSend, stringURL, dataToReceive);

    }

    private static <T extends ServerResponse, S extends Serializable> void performNetworkActivity(S dataToSend, String stringURL, T dataToReceive) {
        URL url;
        HttpURLConnection urlCon;
        ObjectOutputStream out;
        Gson gson = new GsonBuilder().create();
        try {
//            HttpParams httpParams = new BasicHttpParams();
//            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
//            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);

            HttpClient httpClient = getNewHttpClient();
            HttpPost request = new HttpPost(stringURL);
            request.addHeader("content-type", "application/json");
            String JSONData = gson.toJson(dataToSend);
            StringEntity params = new StringEntity(JSONData);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            String responseJSON = EntityUtils.toString(response.getEntity(), "UTF-8");
            HashMap<String,String> respnoseMap = (HashMap)gson.fromJson(responseJSON.trim(), HashMap.class);
            dataToReceive.setError(respnoseMap.get("error"));
            //Not doing anything with the received data.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            //HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            //HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    private static final String TAG = "Server";
}
