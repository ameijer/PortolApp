package com.bitpay.common.controller;

public class BitPayException extends Exception {

    private static final long serialVersionUID = 1L;

    public BitPayException(String message) {
        super(message);
    }
}
