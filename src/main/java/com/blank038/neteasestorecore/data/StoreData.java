package com.blank038.neteasestorecore.data;

import com.google.gson.JsonObject;

/**
 * 商品数据实体类
 *
 * @author Blank038
 * @since 2021-04-15
 */
public class StoreData {
    private final long itemId, orderId, buyTime, groupId;
    private final int itemCount;
    private final String playerUniqueId, cmd, originalData;

    public StoreData(JsonObject object) {
        this.originalData = object.toString();
        this.itemCount = object.get("item_num").getAsInt();
        this.groupId = object.get("group").getAsLong();
        this.itemId = object.get("item_id").getAsLong();
        this.orderId = object.get("orderid").getAsLong();
        this.buyTime = object.get("buy_time").getAsLong();
        this.playerUniqueId = object.get("uuid").getAsString();
        this.cmd = object.get("cmd").getAsString();
    }

    public long getItemId() {
        return this.itemId;
    }

    public long getOrderId() {
        return this.orderId;
    }

    public long getBuyTime() {
        return this.buyTime;
    }

    public long getGroupId() {
        return this.groupId;
    }

    public int getItemCount() {
        return this.itemCount;
    }

    public String getPlayerUUID() {
        return this.playerUniqueId;
    }

    public String getCommand() {
        return this.cmd;
    }

    public String getoriginalData() {
        return this.originalData;
    }
}
