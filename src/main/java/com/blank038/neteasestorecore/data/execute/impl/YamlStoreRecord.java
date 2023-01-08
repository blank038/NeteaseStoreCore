package com.blank038.neteasestorecore.data.execute.impl;

import com.blank038.neteasestorecore.NeteaseStoreCore;
import com.blank038.neteasestorecore.data.StoreData;
import com.blank038.neteasestorecore.data.execute.IStoreRecord;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author Blank038
 * @since 2021-04-16
 */
public class YamlStoreRecord implements IStoreRecord {

    @Override
    public void recordStore(String playerName, StoreData storeData) {
        File path = new File(NeteaseStoreCore.getInstance().getDataFolder(), "record");
        path.mkdir();
        // 目标文件
        File target = new File(path, storeData.getOrderId() + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(target);
        data.set("order_id", storeData.getOrderId());
        data.set("group", storeData.getGroupId());
        data.set("item_id", storeData.getItemId());
        data.set("buy_time", storeData.getBuyTime());
        data.set("item_num", storeData.getItemCount());
        data.set("player", playerName);
        data.set("uuid", storeData.getPlayerUUID());
        data.set("original", storeData.getoriginalData());
        try {
            data.save(target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
