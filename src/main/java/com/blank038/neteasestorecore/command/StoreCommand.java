package com.blank038.neteasestorecore.command;

import com.blank038.neteasestorecore.NeteaseStoreCore;
import com.blank038.neteasestorecore.packet.GetOrderPacket;
import com.blank038.neteasestorecore.util.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * @author Blank038
 * @since 2021-04-15
 */
public class StoreCommand implements CommandExecutor {
    private final NeteaseStoreCore INSTANCE;
    private final HashMap<String, Long> COOLDOWNS = new HashMap<>();

    public StoreCommand() {
        this.INSTANCE = NeteaseStoreCore.getInstance();
        // 计算延迟
        Bukkit.getScheduler().runTaskTimerAsynchronously(INSTANCE, new Runnable() {
            @Override
            public synchronized void run() {
                COOLDOWNS.entrySet().removeIf((entry) -> System.currentTimeMillis() >= entry.getValue());
            }
        }, 20L, 20L);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                // 尝试收货
                long time = this.COOLDOWNS.getOrDefault(player.getName(), 0L);
                if (System.currentTimeMillis() < time) {
                    long last = time - System.currentTimeMillis();
                    player.sendMessage(NeteaseStoreCore.getString("message.cooldown", true)
                            .replace("%cd%", String.format("%.1f", last / 1000.0)));
                    return true;
                }
                this.COOLDOWNS.put(player.getName(), System.currentTimeMillis() + INSTANCE.getConfig().getInt("store_delay") * 1000L);
                // 发送随机字段
                player.sendMessage(NeteaseStoreCore.getString("message.prefix", false)
                        + CommonUtil.randomString(INSTANCE.getConfig().getStringList("message.query")));
                // 发起收货请求
                new GetOrderPacket(player);
            }
            return true;
        } else if ("reload".equalsIgnoreCase(strings[0])
                && commandSender.hasPermission("nsc.admin")) {
            INSTANCE.loadConfig();
            commandSender.sendMessage(NeteaseStoreCore.getString("message.reload", true));
            return true;
        }
        return false;
    }
}
