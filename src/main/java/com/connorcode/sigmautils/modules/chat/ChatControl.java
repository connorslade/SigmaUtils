package com.connorcode.sigmautils.modules.chat;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Allows other players to control your client over game chat.", inDevelopment = true)
public class ChatControl extends Module {
    BoolSetting requireSignature = new BoolSetting(ChatControl.class, "Require Signature").description("Whether to require the message to be signed to execute it.").build();
    BoolSetting allowSelf = new BoolSetting(ChatControl.class, "Allow Self").description("Allow receiving commands from yourself.").build();
    BoolSetting allowGameMessage = new BoolSetting(ChatControl.class, "Allow Game Message").description("Allow receiving commands from game messages. (from server, not player)").build();
    BoolSetting debug = new BoolSetting(ChatControl.class, "Debug").description("Whether to print debug messages to the console.").build();


    BoolSetting kickCommand = new BoolSetting(ChatControl.class, "Kick Command").category("Commands").description("Allows other players to kick you from the server.").build();
    BoolSetting pingCommand = new BoolSetting(ChatControl.class, "Ping Command").category("Commands").description("Replies 'Pong'.").build();
    BoolSetting sayCommand = new BoolSetting(ChatControl.class, "Say Command").category("Commands").description("Sends a message or command to the server.").build();

    @EventHandler
    void onPacket_ChatMessageS2CPacket(PacketReceiveEvent event) {
        if (!(event.get() instanceof ChatMessageS2CPacket packet) || client.player == null) return;
        debug("[PLAYER] Received message: %s", packet.body().content());
        if (packet.sender() == client.player.getUuid() && !allowSelf.value()) return;
        handleMessage(packet.sender(), packet.body().content());
    }

    @EventHandler
    void onPacket_GameMessageS2CPacket(PacketReceiveEvent event) {
        if (!allowGameMessage.value() || !(event.get() instanceof GameMessageS2CPacket packet) || client.player == null)
            return;
        debug("[SERVER] Received message: %s", packet.content().getString());
        handleMessage(null, packet.content().getString());
    }

    void handleMessage(UUID sender, String message) {
        var rawCommand = getCommand(message);
        if (rawCommand.isEmpty()) return;
        var command = rawCommand.get();
        var rawArgs = command.split(">", 2)[1].trim();
        var args = new CommandArgs(sender, rawArgs);
        debug("Received command: %s", command);

        if (command.equals("kick") && kickCommand.value()) command(this::kickCommand, "kick", args);
        else if (command.equals("ping") && pingCommand.value()) command(this::pingCommand, "ping", args);
        else if (command.equals("say") && sayCommand.value()) command(this::sayCommand, "say", args);
        else if (client.player != null) {
            debug("Unknown command: %s", command);
            client.player.networkHandler.sendChatMessage(String.format("Unknown command: %s", command));
        }
    }

    Optional<String> getCommand(String message) {
        if (client.player == null) return Optional.empty();
        var profile = client.player.getGameProfile();
        var name = profile.getName().toLowerCase();
        var uuid = profile.getId();

        var parts = message.split(">", 2);
        var ref = parts[0].trim();
        if (parts.length < 2) return Optional.empty();

        if (!(ref.toLowerCase().equals(name) || ref.equals(uuid.toString()) || ref.equals(uuid.toString().replaceAll("-", ""))))
            return Optional.empty();
        return Optional.of(parts[1]);
    }

    void command(Command command, String name, CommandArgs args) {
        if (args.args.isEmpty()) info("Executing command: %s", name);
        else info("Executing command: %s {%s}", name, args);
        command.run(args);
    }

    void debug(String format, Object... args) {
        if (!debug.value()) return;
        info(format, args);
    }

    void pingCommand(CommandArgs args) {
        if (client.player == null) return;
        var net = client.player.networkHandler;
        if (args.args.isEmpty()) net.sendChatMessage("Pong!");
        else net.sendChatMessage(String.format("Pong! {%s}", args.args));
    }

    void kickCommand(CommandArgs args) {
        if (client.player == null) return;
        var net = client.player.networkHandler;
        if (args.args.isEmpty())
            net.onDisconnected(Text.of(String.format("Kicked by %s through SigmaUtils::ChatControl.", args.senderName())));
        else
            net.onDisconnected(Text.of(String.format("Kicked by %s through SigmaUtils::ChatControl.\n%s", args.senderName(), args.args)));
    }

    // ==  COMMANDS ==

    void sayCommand(CommandArgs args) {
        if (client.player == null) return;
        var net = client.player.networkHandler;
        if (args.args.startsWith("/")) net.sendCommand(args.args);
        else net.sendChatMessage(args.args);
    }

    interface Command {
        void run(CommandArgs args);
    }

    record CommandArgs(@Nullable UUID sender, String args) {
        String senderName() {
            if (sender == null) return "SERVER";
            if (client.player == null) return sender.toString();
            var player = client.player.networkHandler.getPlayerListEntry(sender);
            if (player == null) return sender.toString();
            return player.getProfile().getName();
        }
    }
}
