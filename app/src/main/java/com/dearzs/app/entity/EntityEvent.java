package com.dearzs.app.entity;

/**
 * Created by luyanlong on 2016/12/3.
 * EventBus 实体类
 */

public class EntityEvent {

    public static class EventOrderRefresh{
        public static final int TYPE_ORDER_DELETE = 10000;      //订单删除
        public static final int TYPE_ORDER_REFRESH = TYPE_ORDER_DELETE + 1; //订单刷新
        private EntityOrderInfo mOrderInfo;
        private int mType = 0;

        public int getType() {
            return mType;
        }

        public EventOrderRefresh(EntityOrderInfo orderInfo, int type) {
            this.mOrderInfo = orderInfo;
            this.mType = type;
        }

        public EntityOrderInfo getEntityOrderInfo() {
            return mOrderInfo;
        }
    }

}
