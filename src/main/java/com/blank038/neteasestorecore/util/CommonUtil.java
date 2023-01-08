package com.blank038.neteasestorecore.util;

import com.blank038.neteasestorecore.NeteaseStoreCore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

/**
 * @author Blank038
 * @since 2021-04-15
 */
public class CommonUtil {
    private static final Gson GSON = new GsonBuilder().create();

    public static String signData(String source, String packetType) {
        // 请求头
        String str = "POST" + packetType + source, key = NeteaseStoreCore.getInstance().getStoreOption().getAppKey();
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            StringBuilder sb = new StringBuilder();
            for (byte b : mac.doFinal(str.getBytes(StandardCharsets.UTF_8))) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString().toLowerCase(Locale.ROOT);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return null;
        }
    }

    public static String randomString(List<String> strings) {
        if (strings.isEmpty()) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&',
                strings.get((int) (Math.random() * strings.size())));
    }

    public static JsonObject fromJson(String json) {
        return GSON.fromJson(json, JsonObject.class);
    }
}
