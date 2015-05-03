package com.amazon.rvspeedtest.dto;

import java.io.Serializable;

/**
 * Created by joshroha on 4/16/2015.
 */
public class RegistrationResponse implements Serializable,ServerResponse {
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String error;
}
