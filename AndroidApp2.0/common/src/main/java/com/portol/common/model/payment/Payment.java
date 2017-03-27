package com.portol.common.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment implements Serializable, Rush {

    private static final long serialVersionUID = -3452319005766146516L;
    private Status status;
    private Type type = Type.BITCOIN;
    private String[] txIds; //contains IDs of all transactions used to fund this account
    //@JsonIgnore
    private String aggregationDestAddr; //contains the address of our account where we aggregate all revenues
    private String aggregationtxId; //transaction ID of the transfer of the btc in this address to the


    private String btcSourceAddr; //source of payment, used for refunding
    private String btcPaymentAddr; //address under our control
    private int totRequested; //amt in bits requested
    private int oldBalance;
    private int totReceived; //amt in bits received
    private int priceInCents;

    public Payment(String[] txIds, String aggregationDestAddr,
                   String aggregationtxId, String btcSourceAddr,
                   String btcPaymentAddr, int totRequested, int totReceived) {
        super();
        this.txIds = txIds;
        this.aggregationDestAddr = aggregationDestAddr;
        this.aggregationtxId = aggregationtxId;
        this.btcSourceAddr = btcSourceAddr;
        this.btcPaymentAddr = btcPaymentAddr;
        this.totRequested = totRequested;
        this.totReceived = totReceived;
    }

    public Payment() {
        super();

    }

    @Override
    public void save() {
        RushCore.getInstance().save(this);
    }
    //aggregation address

    @Override
    public void save(RushCallback callback) {
        RushCore.getInstance().save(this, callback);
    }

    @Override
    public void delete() {
        RushCore.getInstance().delete(this);
    }

    @Override
    public void delete(RushCallback callback) {
        RushCore.getInstance().delete(this, callback);
    }

    @Override
    public String getId() {
        return RushCore.getInstance().getId(this);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String[] getTxIds() {
        return txIds;
    }

    public void setTxIds(String[] txIds) {
        this.txIds = txIds;
    }

    public String getAggregationDestAddr() {
        return aggregationDestAddr;
    }

    public void setAggregationDestAddr(String aggregationDestAddr) {
        this.aggregationDestAddr = aggregationDestAddr;
    }

    public String getAggregationtxId() {
        return aggregationtxId;
    }

    public void setAggregationtxId(String aggregationtxId) {
        this.aggregationtxId = aggregationtxId;
    }

    public String getBtcSourceAddr() {
        return btcSourceAddr;
    }

    public void setBtcSourceAddr(String btcSourceAddr) {
        this.btcSourceAddr = btcSourceAddr;
    }

    public String getBtcPaymentAddr() {
        return btcPaymentAddr;
    }

    public void setBtcPaymentAddr(String btcPaymentAddr) {
        this.btcPaymentAddr = btcPaymentAddr;
    }

    public int getTotRequested() {
        return totRequested;
    }

    public void setTotRequested(int totRequested) {
        this.totRequested = totRequested;
    }

    public int getTotReceived() {
        return totReceived;
    }

    public void setTotReceived(int totReceived) {
        this.totReceived = totReceived;
        int difference = this.totReceived - this.totRequested;
        Status decision;

        if (difference == 0) {
            decision = Status.COMPLETE;
        } else if (difference < 0) {
            decision = Status.PARTIAL;
        } else {
            decision = Status.OVERPAY;
        }

        setStatus(decision);
    }

    public int getPriceInCents() {
        return priceInCents;
    }

    public void setPriceInCents(int priceInCents) {
        this.priceInCents = priceInCents;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {

        StringBuffer buf = new StringBuffer();

        buf.append("    Info for payment @ " + this.hashCode() + ":\n");
        //buf.append("    id:" + id +":\n");
        buf.append("    status:" + status + ":\n");
        buf.append("    type:" + type + ":\n");
        buf.append("    btcsourceaddr:" + btcSourceAddr + ":\n");
        buf.append("    btc target payment addr:" + btcPaymentAddr + ":\n");
        buf.append("    total amt requested:" + totRequested + ":\n");
        buf.append("    total amt received:" + totReceived + ":\n");
        buf.append("    price in cents:" + priceInCents + ":\n");

        return buf.toString();
    }

    public int getOldBalance() {
        return oldBalance;
    }

    public void setOldBalance(int oldBalance) {
        this.oldBalance = oldBalance;
    }

    //L credits are liquid credits
    public enum Type {
        BITCOIN, SHARDS
    }

    public enum Status {
        UNUSED, PARTIAL, COMPLETE, OVERPAY, FAILED, REFUNDED, EMPTIED, MASTER;
    }


}
