package com.blank038.neteasestorecore.data.execute.impl;

import com.aystudio.core.bukkit.util.mysql.MySqlStorageHandler;
import com.blank038.neteasestorecore.NeteaseStoreCore;
import com.blank038.neteasestorecore.data.StoreData;
import com.blank038.neteasestorecore.data.execute.IStoreRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.SQLException;
import java.util.logging.Level;

/**
 * @author Blank038
 * @since 2021-04-16
 */
public class MySQLStoreRecord implements IStoreRecord {
    public static boolean SQL_STATUS = false;

    private final MySqlStorageHandler dataSource;
    private final Gson gson = new GsonBuilder().create();

    public MySQLStoreRecord() {
        String[] sqlArray = {
                "CREATE TABLE IF NOT EXISTS netease_store_record (order_id BIGINT, item_num INT, group_id BIGINT, item_id BIGINT, buy_time BIGINT, " +
                        "player VARCHAR(30) NOT NULL, uuid VARCHAR(40) NOT NULL, original_data TEXT, PRIMARY KEY ( order_id ))"
        };
        this.dataSource = new MySqlStorageHandler(NeteaseStoreCore.getInstance(), NeteaseStoreCore.getInstance().getConfig().getString("record_option.url"),
                NeteaseStoreCore.getInstance().getConfig().getString("record_option.user"), NeteaseStoreCore.getInstance().getConfig().getString("record_option.password"), sqlArray);
        this.dataSource.setReconnectionQueryTable("netease_store_record");
        this.dataSource.setCheckConnection(true);
        SQL_STATUS = true;
    }

    @Override
    public void recordStore(String playerName, StoreData storeData) {
        this.dataSource.connect((statement) -> {
            try {
                statement.setLong(1, storeData.getOrderId());
                statement.setInt(2, storeData.getItemCount());
                statement.setLong(3, storeData.getItemId());
                statement.setLong(4, storeData.getGroupId());
                statement.setLong(5, storeData.getBuyTime());
                statement.setString(6, playerName);
                statement.setString(7, storeData.getPlayerUUID());
                statement.setString(8, storeData.getoriginalData());
                statement.executeUpdate();
            } catch (SQLException e) {
                NeteaseStoreCore.getInstance().getLogger().log(Level.WARNING, e, () -> "写入数据出现异常, 订单号: " + storeData.getOrderId());
            }
        }, "INSERT INTO netease_store_record (order_id,item_num,group_id,item_id,buy_time,player,uuid,original_data) VALUES (?,?,?,?,?,?,?,?)");
    }
}