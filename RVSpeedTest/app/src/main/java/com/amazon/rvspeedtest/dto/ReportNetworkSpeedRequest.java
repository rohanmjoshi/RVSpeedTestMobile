package com.amazon.rvspeedtest.dto;

import java.io.Serializable;

/**
 * Created by joshroha on 4/21/2015.
 */
public class ReportNetworkSpeedRequest implements Serializable {
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getNetworkSpeedInKb() {
        return networkSpeedInKb;
    }

    public void setNetworkSpeedInKb(String networkSpeedInKb) {
        this.networkSpeedInKb = networkSpeedInKb;
    }

    public String messageId;
    public String networkSpeedInKb;

    public ReportNetworkSpeedRequest(String messageId,String networkSpeedInKb)
    {
        this.messageId = messageId;
        this.networkSpeedInKb = networkSpeedInKb;
    }
}

