package com.connorcode.sigmautils.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class Map implements Command {
    private static int save(CommandContext<FabricClientCommandSource> context) {
        Optional<Pair<MapIdComponent, MapState>> rawMap = getMap(context);
        if (rawMap.isEmpty()) return 0;
        var mapId = rawMap.get().getLeft();
        MapState mapState = rawMap.get().getRight();
        assert client.player != null;

        File mapFile = getNewFile(mapId);
        try {
            client.gameRenderer.getMapRenderer().getMapTexture(mapId.id(), mapState).texture.getImage().writeTo(mapFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.player.sendMessage(Text.literal(String.format("Σ] Saved Map #%d", mapId))
                .formatted(Formatting.UNDERLINE)
                .styled(style -> style.withClickEvent(
                        new ClickEvent(ClickEvent.Action.OPEN_FILE, mapFile.getAbsolutePath()))));
        return 0;
    }

    private static int info(CommandContext<FabricClientCommandSource> context) {
        Optional<Pair<MapIdComponent, MapState>> rawMap = getMap(context);
        if (rawMap.isEmpty()) return 0;
        var mapId = rawMap.get().getLeft();
        MapState mapState = rawMap.get().getRight();

        MutableText out = Text.empty()
                // Id
                .append(Text.literal("\nΣ] Id: ")
                        .formatted(Formatting.BOLD))
                .append(Text.literal(String.valueOf(mapId))
                        .formatted(Formatting.RESET))

                // Scale
                .append(Text.literal("\nΣ] Scale: ")
                        .formatted(Formatting.BOLD))
                .append(Text.literal(String.format("1:%d", (int) Math.pow(2, mapState.scale)))
                        .formatted(Formatting.RESET))

                // Locked
                .append(Text.literal("\nΣ] Locked: ")
                        .formatted(Formatting.BOLD))
                .append(Text.literal(String.valueOf(mapState.locked))
                        .formatted(Formatting.RESET));


        context.getSource()
                .getPlayer()
                .sendMessage(out);
        return 0;
    }

    static Optional<Pair<MapIdComponent, MapState>> getMap(CommandContext<FabricClientCommandSource> context) {
        assert client.player != null;

        ItemStack itemStack = client.player.getInventory().getMainHandStack();
        if (!itemStack.isOf(Items.FILLED_MAP) && Objects.requireNonNull(client.crosshairTarget).getType() == HitResult.Type.ENTITY &&
            ((EntityHitResult) client.crosshairTarget).getEntity() instanceof ItemFrameEntity itemFrame) itemStack = itemFrame.getHeldItemStack();


//        if (!itemStack.isOf(Items.FILLED_MAP)) {
        if (!(itemStack.getItem() instanceof FilledMapItem filledMapItem)) {
            context.getSource().sendError(Text.of("You must be holding or looking at a map!"));
            return Optional.empty();
        }

        var mapId = itemStack.get(DataComponentTypes.MAP_ID);
        var mapState = FilledMapItem.getMapState(mapId, client.world);
        if (mapId == null) {
            context.getSource().sendError(Text.of("Map ID is null?"));
            return Optional.empty();
        }

        return Optional.of(new Pair<>(mapId, mapState));
    }

    static File getNewFile(MapIdComponent id) {
        int i = 1;
        while (true) {
            File file = new File(new File(client.runDirectory, "screenshots"), String.format("map_%d%s.png", id.id(), (i++ == 1 ? "" : "_" + i)));
            if (!file.exists()) return file;
        }
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("map")
                        .then(ClientCommandManager.literal("save")
                                .executes(Map::save))
                        .then(ClientCommandManager.literal("info")
                                .executes(Map::info))));
    }
}
