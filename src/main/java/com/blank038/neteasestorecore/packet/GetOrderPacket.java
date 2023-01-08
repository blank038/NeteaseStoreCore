package com.blank038.neteasestorecore.packet;

import com.blank038.neteasestorecore.NeteaseStoreCore;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Blank038
 * @since 2021-04-15
 */
public class GetOrderPacket implements Runnable {
    private final String PLAYER_UUID;

    public GetOrderPacket(Player player) {
        this.PLAYER_UUID = player.getUniqueId().toString();
        Bukkit.getScheduler().runTaskAsynchronously(NeteaseStoreCore.getInstance(), this);
    }

    @Override
    public void run() {
        String api = "/get-mc-item-order-list";
        // 初始化数据
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gameid", NeteaseStoreCore.getInstance().getStoreOption().getGameId());
        jsonObject.addProperty("uuid", this.PLAYER_UUID);
        try {
            // 发送请求
            URL url = new URL(NeteaseStoreCore.getInstance().getStoreOption().getOrderUrl());
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Netease-Server-Sign", CommonUtil.signData(jsonObject.toString(), api));
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
            if (object.has("code") && object.get("code").getAsInt() == 0
                    && object.has("entities")) {
                // 成功返回, 开始执行业务逻辑
                JsonArray array = object.getAsJsonArray("entities");
                if (array.size() == 0) {
                    Player player = Bukkit.getPlayer(UUID.fromString(this.PLAYER_UUID));
                    if (player != null && player.isOnline()) {
                        // 发送随机字段
                        player.sendMessage(NeteaseStoreCore.getString("message.prefix", false)
                                + CommonUtil.randomString(NeteaseStoreCore.getInstance().getConfig().getStringList("message.no_store")));
                    }
                    return;
                }
                List<StoreData> storeList = new ArrayList<>();
                long[] orderArray = new long[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    JsonObject entity = array.get(i).getAsJsonObject();
                    if (entity.has("is_received") && entity.get("is_received").getAsBoolean()) {
                        continue;
                    }
                    StoreData storeData = new StoreData(entity);
                    storeList.add(storeData);
                    orderArray[i] = storeData.getOrderId();
                }
                // 发送数据
                new ShipOrderPacket(this.PLAYER_UUID, storeList, orderArray);
            } else {
                Player player = Bukkit.getPlayer(UUID.fromString(this.PLAYER_UUID));
                if (player != null && player.isOnline()) {
                    // 发送随机字段
                    player.sendMessage(NeteaseStoreCore.getString("message.prefix", false)
                            + CommonUtil.randomString(NeteaseStoreCore.getInstance().getConfig().getStringList("message.no_store")));
                }
                NeteaseStoreCore.getInstance().getLogger().info("查询收货异常 " + this.PLAYER_UUID + " -> " + object);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
