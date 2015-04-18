package com.amazon.rvspeedtest.dto;

import java.io.Serializable;

/**
 * Created by joshroha on 4/16/2015.
 */
public class RegistrationPOJO implements Serializable {
    public String mobileRegistrationId;
    public String invitationCode;

    public RegistrationPOJO(String mobileRegistrationId,String invitationCode)
    {
        this.invitationCode = invitationCode;
        this.mobileRegistrationId = mobileRegistrationId;
    }
}
