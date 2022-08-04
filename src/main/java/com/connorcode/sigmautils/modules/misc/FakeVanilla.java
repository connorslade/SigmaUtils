package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.event.PacketSendCallback;
import com.connorcode.sigmautils.misc.Datatypes;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.mixin.CustomPayloadC2SPacketAccessor;
import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class FakeVanilla extends BasicModule {
    String brand = "vanilla";

    public FakeVanilla() {
        super("fake_vanilla", "Fake Vanilla", "Reports to be a vanilla client when joining servers", Category.Misc);
    }

    @Override
    public void init() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> Util.moduleConfigCommand(dispatcher, this, "brand", Datatypes.String,
                        (context) -> {
                            brand = getString(context, "setting");
                            return 0;
                        }));

        PacketSendCallback.EVENT.register(packet -> {
            if (!(packet instanceof CustomPayloadC2SPacket)) return false;
            CustomPayloadC2SPacketAccessor payloadPacket = (CustomPayloadC2SPacketAccessor) packet;
            payloadPacket.setData(new PacketByteBuf(Unpooled.buffer()).writeString(brand));
            return false;
        });
    }

    @Override
    public void loadConfig(NbtCompound config) {
        super.loadConfig(config);
        brand = config.getString("brand");
    }

    @Override
    public NbtCompound saveConfig() {
        NbtCompound nbt = super.saveConfig();
        nbt.putString("brand", brand);
        return nbt;
    }
}
