package com.blank038.neteasestorecore.packet;

import com.blank038.neteasestorecore.NeteaseStoreCore;
import com.blank038.neteasestorecore.api.event.PlayerRechargeEvent;
import com.blank038.neteasestorecore.data.StoreData;
import com.blank038.neteasestorecore.util.CommonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * 玩家领取商品, 通知网易服务器商品已处理
 *
 * @author Blank038
 * @since 2021-04-15
 */
public class ShipOrderPacket implements Runnable {
    private final String playerUniqueId;
    private final List<StoreData> storeList;
    private final long[] orderArray;

    public ShipOrderPacket(String uuidStr, List<StoreData> storeList, long[] orderArray) {
        this.playerUniqueId = uuidStr;
        this.storeList = storeList;
        this.orderArray = orderArray;
        Bukkit.getScheduler().runTaskAsynchronously(NeteaseStoreCore.getInstance(), this);
    }


    @Override
    public void run() {
        UUID uuid = UUID.fromString(this.playerUniqueId);
        Player player = Bukkit.getPlayer(uuid);
        // 确保玩家在线才提交商品
        if (player != null && player.isOnline()) {
            // 初始化数据
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("gameid", NeteaseStoreCore.getInstance().getStoreOption().getGameId());
            jsonObject.addProperty("uuid", this.playerUniqueId);
            JsonArray array = new JsonArray();
            Arrays.stream(this.orderArray).forEach(array::add);
            jsonObject.add("orderid_list", array);
            try {
                // 发送请求
                URL url = new URL(NeteaseStoreCore.getInstance().getStoreOption().getShipOrderUrl());
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Netease-Server-Sign", CommonUtil.signData(jsonObject.toString(), "/ship-mc-item-order"));
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
                // 写入数据
                osw.write(jsonObject.toString());
                osw.flush();
                // 连接
                connection.connect();
                // 开启写入流
                InputStreamReader isr = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                isr.close();
                osw.close();
                // 收到数据, 转换成 JsonObject
                JsonObject object = CommonUtil.fromJson(sb.toString());
                if (object.has("code")) {
                    int code = object.get("code").getAsInt();
                    if (code == 0) {
                        // 开始执行逻辑
                        this.storeList.forEach((s) -> {
                            Bukkit.getScheduler().runTask(NeteaseStoreCore.getInstance(), () -> {
                                // 唤起事件
                                PlayerRechargeEvent event = new PlayerRechargeEvent(player, s);
                                Bukkit.getPluginManager().callEvent(event);
                                // 执行命令
                                for (int i = 0; i < s.getItemCount(); i++) {
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.getCommand().replace("%player%", player.getName()));
                                    // 执行自定义命令逻辑, 如果无则是空集合
                                    for (String cmd : NeteaseStoreCore.getInstance().getConfig().getStringList("store_commands." + s.getCommand())) {
                                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                                    }
                                }
                            });
                            NeteaseStoreCore.getInstance().getStoreRecord().recordStore(player.getName(), s);
                        });
                        // 发送随机字段
                        player.sendMessage(NeteaseStoreCore.getString("message.prefix", false)
                                + CommonUtil.randomString(NeteaseStoreCore.getInstance().getConfig().getStringList("message.received")));
                    } else {
                        // 发送随机字段
                        player.sendMessage(NeteaseStoreCore.getString("message.prefix", false)
                                + CommonUtil.randomString(NeteaseStoreCore.getInstance().getConfig().getStringList("message.no_store")));
                        NeteaseStoreCore.getInstance().getLogger().info("通知收货异常 " + this.playerUniqueId + "(" + code + ") -> " + object);
                    }
                }
            } catch (IOException e) {
                NeteaseStoreCore.getInstance().getLogger().log(Level.WARNING, e, () -> "通知收货异常, 玩家 UUID: " + this.playerUniqueId);
            }
        }
    }
}
