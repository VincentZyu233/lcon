// 📦 LCon — 注册表初始化
// 📄 获取 Mod 事件总线（目前未注册任何内容，为空预留）

package com.cwelth.lcon.setup;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.cwelth.lcon.LCon.MODID;

public class Registries {

    public static void setup() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    }
}
