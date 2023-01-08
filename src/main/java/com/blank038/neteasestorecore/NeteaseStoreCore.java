package com.blank038.neteasestorecore;

import com.aystudio.core.bukkit.util.custom.LoggerUtil;
import com.blank038.neteasestorecore.command.StoreCommand;
import com.blank038.neteasestorecore.common.StoreOption;
import com.blank038.neteasestorecore.data.execute.IStoreRecord;
import com.blank038.neteasestorecore.data.execute.impl.MySQLStoreRecord;
import com.blank038.neteasestorecore.data.execute.impl.YamlStoreRecord;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Blank038
 * @since 2021-04-15
 */
public class NeteaseStoreCore extends JavaPlugin {
    private static NeteaseStoreCore instance;

    private final StoreOption STORE_OPTION = new StoreOption();
    private IStoreRecord storeRecord;

    public static NeteaseStoreCore getInstance() {
        return instance;
    }

    public StoreOption getStoreOption() {
        return this.STORE_OPTION;
    }

    public IStoreRecord getStoreRecord() {
        return this.storeRecord;
    }

    @Override
    public void onEnable() {
        instance = this;
        LoggerUtil loggerUtil = LoggerUtil.getOrRegister(NeteaseStoreCore.class, "&f[&eNeteaseStoreCore&f] - ");
        super.onEnable();
        // 开始加载配置文件
        this.loadConfig();
        // 注册命令
        super.getCommand("nsc").setExecutor(new StoreCommand());
        // 初始化存储格式
        if ("mysql".equalsIgnoreCase(this.getConfig().getString("record_option.type"))) {
            this.storeRecord = new MySQLStoreRecord();
        } else {
            this.storeRecord = new YamlStoreRecord();
        }
        loggerUtil.log(false, " ");
        loggerUtil.log(false, "   &3NeteaseStoreCore &bv" + getDescription().getVersion());
        loggerUtil.log(false, " ");
        loggerUtil.log(false, "&f[&eNeteaseStoreCore&f] &6Author: &fBlank038 &6- &a收货小精灵 //>");
        if (!MySQLStoreRecord.SQL_STATUS) {
            loggerUtil.log(ChatColor.WHITE + "数据库加载失败");
        } else {
            loggerUtil.log(ChatColor.WHITE + "数据库加载成功");
        }
        loggerUtil.log(false, " ");
    }

    public void loadConfig() {
        super.saveDefaultConfig();
        super.reloadConfig();
        // 初始化数据
        this.STORE_OPTION.init();
    }

    public static String getString(String key, boolean prefix) {
        return ChatColor.translateAlternateColorCodes('&',
                (prefix ? instance.getConfig().getString("message.prefix") : "")
                        + instance.getConfig().getString(key, ""));
    }
}
