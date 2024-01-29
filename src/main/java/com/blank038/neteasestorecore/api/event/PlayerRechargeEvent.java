package com.blank038.neteasestorecore.api.event;

import com.blank038.neteasestorecore.data.StoreData;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author Blank038
 */
public class PlayerRechargeEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final StoreData storeData;

    public PlayerRechargeEvent(Player who, StoreData storeData) {
        super(who);
        this.storeData = storeData;
    }

    public StoreData getStoreData() {
        return this.storeData;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
