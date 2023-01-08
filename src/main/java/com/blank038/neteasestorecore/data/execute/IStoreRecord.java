package com.blank038.neteasestorecore.data.execute;

import com.blank038.neteasestorecore.data.StoreData;

/**
 * 商品记录接口
 *
 * @author Blank038
 * @since 2021-04-16
 */
public interface IStoreRecord {

    /**
     * 记录商品订单
     *
     * @param playerName 触发玩家名
     * @param storeData  商品数据
     */
    void recordStore(String playerName, StoreData storeData);
}
