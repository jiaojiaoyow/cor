package com.exa.order.entity;

public enum OrderStatus {
    //手动取消，管理员取消，未支付，已支付，入库，发货，完成交易
    CANCELBYUSER("-2"),
    CANCELBYAMDIN("-1"),
    UNPAID("0"),
    PAID("1"),
    WAREHOU("2"),
    DELIVER("3"),
    COMPLETE("4");

    private String code;

    OrderStatus(String code) {
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
