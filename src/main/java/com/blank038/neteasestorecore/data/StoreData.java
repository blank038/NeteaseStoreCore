package com.blank038.neteasestorecore.data;

import com.google.gson.JsonObject;

/**
 * 商品数据实体类
 *
 * @author Blank038
 * @since 2021-04-15
 */
public class StoreData {
    private final long ITEM_ID, ORDER_ID, BUY_TIME, GROUP_ID;
    private final int ITEM_COUNT;
    private final String PLAYER_UUID, CMD, ORIGINAL_DATA;

    public StoreData(JsonObject object) {
        this.ORIGINAL_DATA = object.toString();
        this.ITEM_COUNT = object.get("item_num").getAsInt();
        this.GROUP_ID = object.get("group").getAsLong();
        this.ITEM_ID = object.get("item_id").getAsLong();
        this.ORDER_ID = object.get("orderid").getAsLong();
        this.BUY_TIME = object.get("buy_time").getAsLong();
        this.PLAYER_UUID = object.get("uuid").getAsString();
        this.CMD = object.get("cmd").getAsString();
    }

    public long getItemId() {
        return this.ITEM_ID;
    }

    public long getOrderId() {
        return this.ORDER_ID;
    }

    public long getBuyTime() {
        return this.BUY_TIME;
    }

    public long getGroupId() {
        return this.GROUP_ID;
    }

    public int getItemCount() {
        return this.ITEM_COUNT;
    }

    public String getPlayerUUID() {
        return this.PLAYER_UUID;
    }

    public String getCommand() {
        return this.CMD;
    }

    public String getoriginalData() {
        return this.ORIGINAL_DATA;
    }
}
