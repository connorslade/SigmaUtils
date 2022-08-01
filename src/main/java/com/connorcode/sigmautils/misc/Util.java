package com.connorcode.sigmautils.misc;

import com.connorcode.sigmautils.SigmaUtilsClient;
import com.connorcode.sigmautils.commands.Command;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class Util {
    public static Object loadNewClass(String classPath) {
        try {
            return SigmaUtilsClient.class.getClassLoader()
                    .loadClass(classPath)
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String loadResourceString(String name) {
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(SigmaUtilsClient.class.getClassLoader()
                .getResourceAsStream(name)))).lines()
                .collect(Collectors.joining("\n"));
    }

    public static boolean loadEnabled(NbtCompound config) {
        return config.getBoolean("enabled");
    }

    public static NbtCompound saveEnabled(boolean enabled) {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("enabled", enabled);
        return nbt;
    }

    public static void addDrawable(Screen screen, Drawable drawable) {
        ScreenAccessor sa = ((ScreenAccessor) screen);
        sa.getDrawables()
                .add(drawable);
        sa.getChildren()
                .add(drawable);
        sa.getSelectables()
                .add(drawable);
    }

    public static void moduleConfigCommand(CommandDispatcher<FabricClientCommandSource> commandSource, Module module, String configName, Datatypes datatype, ModuleSettingSetter set) {
        moduleConfigCommand(commandSource, module, configName, datatype, null, set);
    }

    public static void moduleConfigCommand(CommandDispatcher<FabricClientCommandSource> commandSource, Module module, String configName, Datatypes datatype, SuggestionProvider<FabricClientCommandSource> suggestion, ModuleSettingSetter set) {
        ArgumentType<?> argumentType = switch (datatype) {
            case Bool -> bool();
            case Double -> doubleArg();
            case Float -> floatArg();
            case Integer -> integer();
            case Long -> longArg();
            case String -> string();
        };

        commandSource.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("set")
                        .then(ClientCommandManager.literal(module.id)
                                .then(ClientCommandManager.literal(configName)
                                        .then(ClientCommandManager.argument("setting", argumentType)
                                                .suggests(suggestion)
                                                .executes(set::set))))));
    }
}
