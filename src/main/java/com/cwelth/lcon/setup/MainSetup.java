// 🚀 LCon — 初始化设置
// 📄 在模组加载时注册事件处理器到 Forge 事件总线

package com.cwelth.lcon.setup;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public class MainSetup {
    public static void setup() {
        //Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));

        Registries.setup();
        IEventBus bus = MinecraftForge.EVENT_BUS;
        // 📋 注册 Forge 总线事件处理器（目前为空，预留）
        bus.register(new EventHandlersForge());
    }
}
