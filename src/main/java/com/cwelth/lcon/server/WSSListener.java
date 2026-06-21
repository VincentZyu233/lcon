package com.cwelth.lcon.server;

/*
import com.netiq.websocket.WebSocket;
import com.netiq.websocket.WebSocketServer;

 */
import com.cwelth.lcon.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WSSListener extends WebSocketServer {
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
                webSocket.send("401:Unauthorized - invalid token.");
                webSocket.close();
                return;
            }
        }
        webSocket.send("200:Welcome to LCon! Have fun! Don't forget to use prefixes with every message you send to me.");
        webSocket.send("200:Valid prefixes:");
        webSocket.send("200:[chat] - send message to Minecraft chat.");
        webSocket.send("200:[message] - display message for player only.");
        webSocket.send("200:[system] - display system message in chat (for player only).");
        webSocket.send("200:[client] - execute client-side command.");
        webSocket.send("200:[server] - execute server-side command.");
        webSocket.send("201:ready.");
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
        webSocket.send("400:Error! Send message prefix first! [chat], [message], [system], [client], [server] are valid prefixes.");
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
