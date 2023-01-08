package com.blank038.neteasestorecore.common;

import com.blank038.neteasestorecore.NeteaseStoreCore;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * 存放收货设定
 *
 * @author Blank038
 * @since 2021-04-15
 */
public class StoreOption {
    private String appKey, orderUrl, shipOrderUrl;
    private long gameId;

    public void init() {
        FileConfiguration data = NeteaseStoreCore.getInstance().getConfig();
        this.gameId = data.getLong("store_option.game_id");
        this.appKey = data.getString("store_option.key");
        this.orderUrl = data.getString("store_option.url.get_order");
        this.shipOrderUrl = data.getString("store_option.url.ship_order");
    }

    public long getGameId() {
        return this.gameId;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public String getOrderUrl() {
        return this.orderUrl;
    }

    public String getShipOrderUrl() {
        return this.shipOrderUrl;
    }
}
