package com.connorcode.sigmautils.config.settings.list;


import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.misc.util.NetworkUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

;

public class PacketList implements DynamicListSetting.ResourceManager<Class<? extends Packet<?>>> {
    DynamicListSetting<Class<? extends Packet<?>>> setting;
    Object2IntOpenHashMap<Class<? extends Packet<?>>> packets = null;
    NetworkSide side;

    public PacketList(DynamicListSetting<Class<? extends Packet<?>>> setting, NetworkSide side) {
        this.setting = setting;
        this.side = side;
    }

    @Override
    public int width() {
        return 320;
    }

    @Override
    public List<Class<? extends Packet<?>>> getAllResources() {
        return getPackets().keySet().stream().toList();
    }

    @Override
    public String getDisplay(Class<? extends Packet<?>> resource) {
        return Optional.ofNullable(NetworkUtils.getPacketName(resource)).orElse(resource.getName());
    }

    @Override
    public void render(Class<? extends Packet<?>> resource, Screen data, int x, int y) {
        SimpleList.render(setting, resource, getDisplay(resource), data, x, y, 0);
    }

    @Override
    public boolean renderSelector(Class<? extends Packet<?>> resource, Screen data, int x, int y) {
        if (setting.value().contains(resource))
            return false;
        SimpleList.selector(setting, resource, getDisplay(resource), data, x, y, 0);
        return true;
    }

    @Override
    public NbtElement serialize(List<Class<? extends Packet<?>>> resources) {
        var thisPackets = getPackets();
        var list = resources.stream().mapToInt(thisPackets::getInt).toArray();
        return new NbtIntArray(list);
    }

    @Override
    public List<Class<? extends Packet<?>>> deserialize(NbtElement nbt) {
        if (!(nbt instanceof NbtIntArray resourceList))
            return null;
        var list = resourceList.stream().map(e -> getPacket(e.intValue())).toList();
        return new ArrayList<>(list);
    }

    Class<? extends Packet<?>> getPacket(int id) {
        var thisPackets = getPackets();
        return thisPackets.keySet().stream().filter(e -> thisPackets.getInt(e) == id).findFirst().orElse(null);
    }

    Object2IntOpenHashMap<Class<? extends Packet<?>>> getPackets() {
        if (packets == null)
            this.packets = NetworkUtils.getPackets(side);
        return packets;
    }

    @Override
    public String type() {
        return "Packet";
    }
}
