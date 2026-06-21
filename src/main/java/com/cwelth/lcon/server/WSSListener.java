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
    private LocalPlayer player;

    public WSSListener(int port, LocalPlayer player)
    {
        super(new InetSocketAddress(port));
        this.player = player;
    }

    public void updatePlayer(LocalPlayer player)
    {
        this.player = player;
    }

    @Override
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
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        String clearMessage = "";
        if(s.startsWith("[chat]"))
        {
            // Send chat
            clearMessage = s.substring(6);
            player.connection.sendChat(clearMessage);
            return;
        }
        if(s.startsWith("[message]"))
        {
            // Send client message
            clearMessage = s.substring(9);
            player.displayClientMessage(Component.literal(clearMessage), true);
            return;
        }
        if(s.startsWith("[system]"))
        {
            // Send player-only chat message
            clearMessage = s.substring(8);
            player.sendSystemMessage(Component.literal(clearMessage));
            return;
        }
        if(s.startsWith("[client]"))
        {
            // Client Command
            clearMessage = s.substring(8);
            if(clearMessage.startsWith("/")) clearMessage = clearMessage.substring(1);
            ClientCommandHandler.runCommand(clearMessage);
            return;
        }
        if(s.startsWith("[server]"))
        {
            // Server Command (bypass player permission, use configured OP level)
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
        webSocket.send(fmt(Config.EMOJI_ERROR_PREFIX, Config.MSG_ERROR_PREFIX));
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        LOGGER.error(fmt(Config.EMOJI_LOG_ERROR, Config.MSG_LOG_ERROR) + e.getMessage());
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.send(fmt(Config.EMOJI_ERROR_INTERNAL, Config.MSG_ERROR_INTERNAL) + e.getMessage());
        }
    }

    @Override
    public void onStart() {
        LOGGER.info(fmt(Config.EMOJI_LOG_START, Config.MSG_LOG_START) + this.getPort());
    }

    private static String fmt(ForgeConfigSpec.ConfigValue<String> emojiCfg,
                               ForgeConfigSpec.ConfigValue<String> msgCfg) {
        return Config.ENABLE_MESSAGE_EMOJI.get()
            ? emojiCfg.get() + " " + msgCfg.get()
            : msgCfg.get();
    }
}
