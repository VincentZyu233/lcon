// 🧩 LCon — 客户端事件处理器
// 📄 监听 Minecraft 客户端事件，管理 WS 服务端的生命周期：
//   - PlayerTickEvent  → 启动 / 更新 WebSocket 服务端
//   - LoggingOut      → 关闭 WebSocket 服务端
//   - ChatReceived    → 将聊天消息广播给所有 WS 客户端

package com.cwelth.lcon.setup;


import com.cwelth.lcon.Config;
import com.cwelth.lcon.LCon;
import com.cwelth.lcon.server.WSSListener;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = LCon.MODID)
public class EventHandlersModClient {
    @SubscribeEvent
    // 🎨 创造模式标签页（暂无自定义物品，保留为空）
    public static void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS)
        {

        }
    }

    @SubscribeEvent
    // 🕐 玩家每 tick 触发一次 — 用于管理 WS 服务端生命周期
    // ⚠️ 局域网联机时，其他玩家的 RemotePlayer 也会触发此事件
    //    必须用 instanceof 跳过，否则强转 LocalPlayer 会 ClassCastException
    public static void clientTick(TickEvent.PlayerTickEvent event) throws IOException {
        if(!event.player.level().isClientSide) return;
        // 👤 跳过远程玩家（局域网加入的朋友），只处理本地玩家
        if (!(event.player instanceof LocalPlayer player)) return;
        if(player != null)
        {
            if(Config.ENABLE_MOD.get())
            {
                // 🚀 首次进入世界 → 创建 WS 服务端并启动
                if(LCon.wss == null)
                {
                    LCon.wss = new WSSListener(Config.PORT.get(), player);
                    LCon.wss.start();
                } else
                    // 🔄 已存在 → 更新玩家引用（防止过期）
                    LCon.wss.updatePlayer(player);

            }
        }
    }

    @SubscribeEvent
    // 🚪 玩家登出时调用 — 关闭 WS 服务端
    public void logOut(ClientPlayerNetworkEvent.LoggingOut event){
        if(event.getConnection() == null) return;
        if(LCon.wss != null) {
            try {
                LCon.wss.stop(3, "201:closed.");
            } catch (InterruptedException e) {
            } finally {
                LCon.wss = null;
            }
        }
    }

    @SubscribeEvent
    // 💬 收到聊天消息时调用 — 广播给所有连接的 WS 客户端
    public static void getChatMessage(ClientChatReceivedEvent event) throws IOException {
        if(LCon.wss != null)
        {
            Gson gson = new Gson();
            LCon.wss.broadcast("200:" + gson.toJson(event.getMessage().getContents().toString()));
        }
    }
}
