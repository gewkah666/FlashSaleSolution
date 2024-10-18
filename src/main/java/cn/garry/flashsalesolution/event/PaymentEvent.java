package cn.garry.flashsalesolution.event;

public class PaymentEvent {
    private String transactionId;
    private String status; // paid, cancelled, timeout

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
