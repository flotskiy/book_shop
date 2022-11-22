package com.github.flotskiy.FlotskiyBookShopApp.model.external;

import java.util.Map;

public class TeleIpPhoneCallResponse {

    private Boolean success;
    private String error;
    private Map<String, String> data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
