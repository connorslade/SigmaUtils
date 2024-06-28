package com.connorcode.sigmautils.config.settings.list;


import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.misc.util.NetworkUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PacketList implements DynamicListSetting.ResourceManager<PacketType<? extends Packet<?>>> {
    DynamicListSetting<PacketType<? extends Packet<?>>> setting;
    NetworkSide side;

    public PacketList(DynamicListSetting<PacketType<? extends Packet<?>>> setting, NetworkSide side) {
        this.setting = setting;
        this.side = side;
    }

    @Override
    public int width() {
        return 320;
    }

    @Override
    public List<PacketType<? extends Packet<?>>> getAllResources() {
        var out = new ArrayList<PacketType<? extends Packet<?>>>();
        (side == NetworkSide.SERVERBOUND ? PlayStateFactories.C2S : PlayStateFactories.S2C).forEachPacketType(((type, protocolId) -> out.add(type)));
        return out;
    }

    @Override
    public String getDisplay(PacketType<? extends Packet<?>> resource) {
        return resource.id().toString();
    }

    @Override
    public void render(PacketType<? extends Packet<?>> resource, Screen data, int x, int y) {
        SimpleList.render(setting, resource, getDisplay(resource), data, x, y, 0);
    }

    @Override
    public boolean renderSelector(PacketType<? extends Packet<?>> resource, Screen data, int x, int y) {
        if (setting.value().contains(resource))
            return false;
        SimpleList.selector(setting, resource, getDisplay(resource), data, x, y, 0);
        return true;
    }

    @Override
    public NbtElement serialize(List<PacketType<? extends Packet<?>>> resources) {
        var out = new NbtList();
        for (var resource : resources)
            out.add(NbtString.of(resource.id().getPath()));
        return out;
    }

    @Override
    public List<PacketType<? extends Packet<?>>> deserialize(NbtElement nbt) {
        if (!(nbt instanceof NbtList resourceList))
            return null;
        var out = new ArrayList<PacketType<? extends Packet<?>>>();
        for (var resource : resourceList)
            out.add(NetworkUtils.getPacket(Identifier.of(resource.asString())));
        return new ArrayList<>(out);
    }

    @Override
    public String type() {
        return "Packet";
    }
}
