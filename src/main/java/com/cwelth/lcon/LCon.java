// 🧩 LCon — WebSocket remote control for Minecraft client
// 📄 模组主入口（Main Entry Point）
// 🧠 在客户端（单人/局域网）中启动 WebSocket 服务端，
//    让外部工具可以远程执行指令和控制游戏
// 🏗️ Forge 1.20.1

package com.cwelth.lcon;

import com.cwelth.lcon.server.WSSListener;
import com.cwelth.lcon.setup.MainSetup;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(LCon.MODID)
public class LCon
{
    // 🆔 模组唯一 ID
    public static final String MODID = "lcon";
    // 🔌 全局唯一的 WebSocket 服务端实例，在第一次玩家 tick 时创建
    public static WSSListener wss = null;
 	
    // 🏗️ 构造函数 — Forge 加载模组时自动调用
    public LCon()
    {
        // ⚙️ 注册客户端配置（生成 lcon-ws-server.toml）
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        // 📂 从磁盘加载配置文件
        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-ws-server.toml"));
        // 🚀 初始化事件处理器
        MainSetup.setup();
    }
}
