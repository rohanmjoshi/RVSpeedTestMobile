package com.amazon.rvspeedtest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by joshroha on 4/16/2015.
 */
public class ServerUtil {
    public static <T extends Serializable> void SendToServer(T dataToSend,T dataToReceive,String stringURL){
        URL url;
        HttpURLConnection urlCon;
        ObjectOutputStream out;
        try {
            url = new URL(stringURL);
            urlCon = (HttpURLConnection) url.openConnection();

            urlCon.setDoOutput(true); // to be able to write.
            urlCon.setDoInput(true); // to be able to read.

            out = new ObjectOutputStream(urlCon.getOutputStream());
            out.writeObject(dataToSend);
            out.close();

           ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
            dataToReceive = (T)ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
