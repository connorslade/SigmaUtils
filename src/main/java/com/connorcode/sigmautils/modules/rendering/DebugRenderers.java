package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;

@ModuleInfo(description = "Allows enabling debug renderers that are normally inaccessible")
public class DebugRenderers extends Module {
    public static BoolSetting pathfinding = new BoolSetting(DebugRenderers.class, "Pathfinding").build();
    public static BoolSetting water = new BoolSetting(DebugRenderers.class, "Water").build();
    public static BoolSetting heightMap = new BoolSetting(DebugRenderers.class, "Height Map").build();
    public static BoolSetting collision = new BoolSetting(DebugRenderers.class, "Collision").build();
    public static BoolSetting supportingBlock = new BoolSetting(DebugRenderers.class, "Supporting Block").build();
    public static BoolSetting neighborUpdate = new BoolSetting(DebugRenderers.class, "Neighbor Update").build();
    public static BoolSetting structure = new BoolSetting(DebugRenderers.class, "Structure").build();
    public static BoolSetting skyLight = new BoolSetting(DebugRenderers.class, "Sky Light").build();
    public static BoolSetting worldGenAttempt = new BoolSetting(DebugRenderers.class, "World Gen Attempt").build();
    public static BoolSetting blockOutline = new BoolSetting(DebugRenderers.class, "Block Outline").build();
    public static BoolSetting village = new BoolSetting(DebugRenderers.class, "Village").build();
    public static BoolSetting villageSections = new BoolSetting(DebugRenderers.class, "Village Sections").build();
    public static BoolSetting bee = new BoolSetting(DebugRenderers.class, "Bee").build();
    public static BoolSetting raidCenter = new BoolSetting(DebugRenderers.class, "Raid Center").build();
    public static BoolSetting goalSelector = new BoolSetting(DebugRenderers.class, "Goal Selector").build();
    public static BoolSetting gameTest = new BoolSetting(DebugRenderers.class, "Game Test").build();
    public static BoolSetting gameEvent = new BoolSetting(DebugRenderers.class, "Game Event").build();
    public static BoolSetting light = new BoolSetting(DebugRenderers.class, "Light").build();
}
