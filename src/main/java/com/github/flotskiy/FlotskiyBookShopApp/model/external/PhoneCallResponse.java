package com.github.flotskiy.FlotskiyBookShopApp.model.external;

public class PhoneCallResponse {

    private String status;
    private String code;
    private String callId;
    private double cost;
    private double balance;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getValance() {
        return balance;
    }

    public void setValance(double valance) {
        this.balance = valance;
    }

    @Override
    public String toString() {
        return "PhoneCallResponse{" +
                "status='" + status + '\'' +
                ", code='" + code + '\'' +
                ", callId='" + callId + '\'' +
                ", cost=" + cost +
                ", balance=" + balance +
                '}';
    }
}
