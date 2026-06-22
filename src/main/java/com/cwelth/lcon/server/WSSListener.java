// 🖧 LCon — WebSocket 服务端
// 📄 基于 Java-WebSocket 库，在客户端启动一个 WS 服务端
// 🧠 外部工具以 WebSocket 客户端连入，发送带前缀的文本指令，
//    服务端解析前缀并执行对应的游戏操作
// 🔌 使用反射调用包私有构造器（见 RconManager）

package com.cwelth.lcon.server;

/*
import com.netiq.websocket.WebSocket;
import com.netiq.websocket.WebSocketServer;

 */
import com.cwelth.lcon.Config;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;

import java.net.InetSocketAddress;

public class WSSListener extends WebSocketServer {
    private static final Logger LOGGER = LogUtils.getLogger();
    // 👤 当前本地玩家引用，由 updatePlayer() 在主线程更新
    private LocalPlayer player;

    // 🏗️ 构造函数 — 绑定端口，保存玩家引用
    public WSSListener(int port, LocalPlayer player)
    {
        super(new InetSocketAddress(port));
        this.player = player;
    }

    // 🔄 更新玩家引用（每次 tick 调用，确保 player 不过期）
    public void updatePlayer(LocalPlayer player)
    {
        this.player = player;
    }

    @Override
    // 🔓 新 WS 客户端连接时调用
    // 1）校验 token（如果有配置）
    // 2）发送欢迎消息 + 前缀帮助列表
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        String configuredToken = Config.TOKEN.get();
        if (!configuredToken.isEmpty()) {
            String token = extractQueryParam(clientHandshake.getResourceDescriptor(), "token");
            if (!configuredToken.equals(token)) {
                webSocket.send(fmt(Config.EMOJI_UNAUTHORIZED, Config.MSG_UNAUTHORIZED));
                webSocket.close();
                return;
            }
        }
        webSocket.send(fmt(Config.EMOJI_WELCOME, Config.MSG_WELCOME));
        webSocket.send(fmt(Config.EMOJI_PREFIXES, Config.MSG_PREFIXES));
        webSocket.send(fmt(Config.EMOJI_CHAT, Config.MSG_CHAT));
        webSocket.send(fmt(Config.EMOJI_MESSAGE, Config.MSG_MESSAGE));
        webSocket.send(fmt(Config.EMOJI_SYSTEM, Config.MSG_SYSTEM));
        webSocket.send(fmt(Config.EMOJI_CLIENT, Config.MSG_CLIENT));
        webSocket.send(fmt(Config.EMOJI_SERVER, Config.MSG_SERVER));
        webSocket.send(fmt(Config.EMOJI_READY, Config.MSG_READY));
    }

    // 🔍 解析 URI 查询参数（如 ?token=xxx → "xxx"）
    private static String extractQueryParam(String uri, String param) {
        if (uri == null || !uri.contains("?")) return "";
        String query = uri.substring(uri.indexOf("?") + 1);
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(param)) return kv[1];
        }
        return "";
    }

    @Override
    // 🔌 客户端断开连接时调用（暂无特殊处理）
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    // 📩 收到 WS 客户端消息时调用
    // 根据前缀分发：chat / message / system / client / server
    public void onMessage(WebSocket webSocket, String s) {
        String clearMessage = "";
        // 💬 [chat] — 以玩家身份发送聊天消息
        if(s.startsWith("[chat]"))
        {
            clearMessage = s.substring(6);
            player.connection.sendChat(clearMessage);
            return;
        }
        // 📩 [message] — 仅向玩家显示消息（客户端侧，其他人看不到）
        if(s.startsWith("[message]"))
        {
            clearMessage = s.substring(9);
            player.displayClientMessage(Component.literal(clearMessage), true);
            return;
        }
        // 🔔 [system] — 在聊天栏显示系统消息
        if(s.startsWith("[system]"))
        {
            clearMessage = s.substring(8);
            player.sendSystemMessage(Component.literal(clearMessage));
            return;
        }
        // 🖥️ [client] — 执行客户端侧命令（如 /fps）
        if(s.startsWith("[client]"))
        {
            clearMessage = s.substring(8);
            if(clearMessage.startsWith("/")) clearMessage = clearMessage.substring(1);
            ClientCommandHandler.runCommand(clearMessage);
            return;
        }
        // 🖧 [server] — 执行服务端命令（绕过玩家权限，使用配置的 OP 等级）
        if(s.startsWith("[server]"))
        {
            clearMessage = s.substring(8);
            if(clearMessage.startsWith("/")) clearMessage = clearMessage.substring(1);
            MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
            if (server != null) {
                ServerPlayer serverPlayer = server.getPlayerList().getPlayer(player.getUUID());
                if (serverPlayer != null) {
                    int level = Config.COMMAND_PERMISSION_LEVEL.get();
                    server.getCommands().performPrefixedCommand(
                        serverPlayer.createCommandSourceStack().withPermission(level),
                        clearMessage
                    );
                }
            }
            return;
        }
        // ❌ 未知前缀
        webSocket.send(fmt(Config.EMOJI_ERROR_PREFIX, Config.MSG_ERROR_PREFIX));
    }

    @Override
    // 💥 发生错误时调用 — 记录日志并通知客户端
    public void onError(WebSocket webSocket, Exception e) {
        LOGGER.error(fmt(Config.EMOJI_LOG_ERROR, Config.MSG_LOG_ERROR) + e.getMessage());
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.send(fmt(Config.EMOJI_ERROR_INTERNAL, Config.MSG_ERROR_INTERNAL) + e.getMessage());
        }
    }

    @Override
    // 🚀 服务端启动成功时调用
    public void onStart() {
        LOGGER.info(fmt(Config.EMOJI_LOG_START, Config.MSG_LOG_START) + this.getPort());
    }

    // 🛠️ 工具方法：根据 emoji 总开关决定是否拼上 emoji
    private static String fmt(ForgeConfigSpec.ConfigValue<String> emojiCfg,
                               ForgeConfigSpec.ConfigValue<String> msgCfg) {
        return Config.ENABLE_MESSAGE_EMOJI.get()
            ? emojiCfg.get() + " " + msgCfg.get()
            : msgCfg.get();
    }
}
