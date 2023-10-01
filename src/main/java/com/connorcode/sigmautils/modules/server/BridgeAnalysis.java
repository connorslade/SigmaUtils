package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.PalettedContainer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static com.connorcode.sigmautils.SigmaUtils.client;


@ModuleInfo(description = "Downloads the wool bridges from the world after a game of hypixel bedwars.", inDevelopment = true)
public class BridgeAnalysis extends Module {
    AtomicBoolean downloading = new AtomicBoolean(false);
    final StringSetting serverRegex =
            new StringSetting(BridgeAnalysis.class, "Server Regex").description(
                            "Only run on servers whoes address matches the regex.")
                    .value("hypixel.net")
                    .build();
    final StringSetting endRegex =
            new StringSetting(BridgeAnalysis.class, "End Regex").description(
                            "Regex to match end messages. Triggers wdl.")
                    .value(".*(VICTORY|GAME OVER)!")
                    .build();
    final StringSetting saveLocation =
            new StringSetting(BridgeAnalysis.class, "Save Location").description(
                            "The folder to save the world downloads into.")
                    .value("bridge_analysis")
                    .build();
    final Block[] wool = new Block[]{
            Blocks.WHITE_WOOL,
            Blocks.BLACK_WOOL,
            Blocks.BLUE_WOOL,
            Blocks.BROWN_WOOL,
            Blocks.CYAN_WOOL,
            Blocks.GRAY_WOOL,
            Blocks.GREEN_WOOL,
            Blocks.LIGHT_BLUE_WOOL,
            Blocks.LIGHT_GRAY_WOOL,
            Blocks.LIME_WOOL,
            Blocks.MAGENTA_WOOL,
            Blocks.ORANGE_WOOL,
            Blocks.PINK_WOOL,
            Blocks.PURPLE_WOOL,
            Blocks.RED_WOOL,
            Blocks.YELLOW_WOOL
    };
    boolean validServer;

    @Override
    public void init() {
        super.init();

        ClientCommandRegistrationCallback.EVENT.register(
                ((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("wdl").executes(ctx -> {
                    if (downloading.get()) {
                        info("Already Downloading");
                        return 0;
                    }

                    info("Starting Download");
                    downloading.set(true);
                    new Thread(this::download).start();
                    return 0;
                }))));
    }

    @EventHandler
    void onPacketReceive_TitleS2CPacket(PacketReceiveEvent packet) {
        if (!this.enabled || !(packet.get() instanceof TitleS2CPacket title)) return;
        var regex = Pattern.compile(endRegex.value());
        if (!regex.matcher(title.getTitle().getString()).matches()) return;
        info("Downloading Wool");
        download();
    }

    @EventHandler
    void onPacketReceive_GameJoinS2CPacket(PacketReceiveEvent packet) {
        if (!enabled || !(packet.get() instanceof GameJoinS2CPacket)) return;
        var serverInfo = client.getCurrentServerEntry();
        if (serverInfo == null) return;
        var regex = Pattern.compile(serverRegex.value());
        validServer = regex.matcher(serverInfo.address).matches();
        info("Valid server: %s", validServer);
    }

    void download() {
        assert client.world != null;

        var blocks = new HashSet<BlockData>();
        var chunkManager = client.world.getChunkManager();
        var chunks = chunkManager.chunks.chunks;

        var radius = chunkManager.chunks.radius + 1;
        var centerChunkX = chunkManager.chunks.centerChunkX;
        var centerChunkZ = chunkManager.chunks.centerChunkZ;

        var complete = 0;
        for (int x = centerChunkX - radius; x < centerChunkX + radius; x++) {
            for (int z = centerChunkZ - radius; z < centerChunkZ + radius; z++) {
                if (!chunkManager.chunks.isInRadius(x, z)) continue;

                var index = chunkManager.chunks.getIndex(x, z);
                var chunk = chunks.get(index);
                if (chunk == null) continue;

                var sections = chunk.getSectionArray();
                for (int y = 0; y < sections.length; y++) {
                    var realY = chunk.sectionIndexToCoord(y);
                    var section = sections[y];
                    if (section == null) continue;
                    var blockStates = section.getBlockStateContainer();
                    dumpHorizontalChunkSlice(blockStates, blocks, x, realY, z);
                }

                info("Chunk %d/%d (%.2f%%)", ++complete, chunks.length(), (float) complete / chunks.length() * 100);
            }
        }

        info("Downloaded %d blocks", blocks.size());

        StringBuilder out = new StringBuilder();
        for (var i : blocks) out.append(String.format("[%d,%d,%d] %s\n", i.x, i.y, i.z, wool[i.id].getName().getString()));
        info("Saving...");

        var file = new File("block-download.txt");
        info("File: %s", file.getAbsolutePath());
        try {
            if (file.exists()) file.delete();
            file.createNewFile();
            var writer = new PrintWriter(file);
            writer.write(out.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        info("Saved!");
        downloading.set(false);
    }

    // Inverse of (y << this.edgeBits | z) << this.edgeBits | x;
    Vec3i computePosition(int index, int edgeBits) {
        int mask = (1 << edgeBits) - 1;
        int x = index & mask;
        int z = index >>> edgeBits & mask;
        int y = index >>> edgeBits * 2 & mask;
        return new Vec3i(x, y, z);
    }

    void dumpHorizontalChunkSlice(PalettedContainer<BlockState> blockStates, HashSet<BlockData> blocks, int x, int y, int z) {
        var edgeBits = blockStates.paletteProvider.edgeBits;

        blockStates.data.storage.forEach((value -> {
            var block = blockStates.data.palette().get(value).getBlock();
            if (block == Blocks.AIR) return;
            var rawWool = Arrays.stream(wool).filter(e -> e == block).findFirst();
            if (rawWool.isEmpty()) return;

            var chunkPos = computePosition(value, edgeBits);
            var pos = new Vec3i(x, y, z).multiply(16).add(0, 0, 8);
            blocks.add(new BlockData(pos.add(chunkPos), block));
        }));
    }

    int getWoolId(Block block) {
        for (int i = 0; i < wool.length; i++)
            if (wool[i] == block) return i;
        return -1;
    }

    class BlockData {
        int x;
        int y;
        int z;
        int id;

        public BlockData(Vec3i pos, Block block) {
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            this.id = getWoolId(block);
        }
    }
}
