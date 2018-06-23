package com.dearzs.app.entity;

import java.io.Serializable;

/**
 * 订单状态类
 *
     "btn": "立即付款",
     "des": "已下单，待支付",
     "id": 0
 *
 * @author myh
 * @version 1.0
 */
public class EntityOrderState implements Serializable {
    private String btn;
    private String des;
    private Long id;//支付状态，0待支付，1已支付

    public String getBtn() {
        return btn;
    }

    public void setBtn(String btn) {
        this.btn = btn;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
