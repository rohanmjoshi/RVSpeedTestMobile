package com.amazon.rvspeedtest;

/**
 * Created by joshroha on 4/21/2015.
 */
public class SpeedTestConstants {
    public static final String EC2_BASE_URL = "https://speedtestvoiceapp-1878794768.us-west-2.elb.amazonaws.com/SpeedTestVoiceApp";
    public static final String BASE_URL = "http://10.0.2.2:8080/SpeedTestVoiceApp";

    public static final String REGISTRATION_URL = BASE_URL+"/registerDevice";
    public static final String EC2_REGISTRATION_URL = EC2_BASE_URL+"/registerDevice";

    public static final String SEND_SPEED_URL = BASE_URL+"/reportNetworkSpeed";
    public static final String EC2_SEND_SPEED_URL = EC2_BASE_URL+"/reportNetworkSpeed";
}
