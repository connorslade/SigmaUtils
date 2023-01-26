package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.settings.*;
import com.connorcode.sigmautils.misc.util.NetworkUtils;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.*;

import static com.connorcode.sigmautils.SigmaUtils.directory;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.CommandSource.suggestMatching;

public class Dump implements Command {
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("dump")
                        .then(ClientCommandManager.argument("resource", string())
                                .suggests(((context, builder) -> suggestMatching(new String[]{
                                        "packets",
                                        "docs"
                                }, builder)))
                                .executes(ctx -> {
                                    try {
                                        return execute(ctx);
                                    } catch (IOException e) {
                                        ctx.getSource().sendError(Text.of("Error: " + e.getMessage()));
                                    }
                                    return 0;
                                }))));
    }

    int execute(CommandContext<FabricClientCommandSource> context) throws IOException {
        var resource = getString(context, "resource");

        var dumps = directory.resolve("dump").toFile();
        dumps.mkdirs();

        if (resource.equals("packets")) {
            StringBuilder out = new StringBuilder("# Generated with `/util dump packets` (only works in dev builds)\n");
            var mr = FabricLoader.getInstance().getMappingResolver();
            for (var i : NetworkUtils.getPackets().keySet().stream().sorted(Comparator.comparing(Class::getSimpleName)).toList())
                out.append(mr.unmapClassName("intermediary", i.getName()))
                        .append(" ")
                        .append(i.getSimpleName())
                        .append("\n");
            Files.write(dumps.toPath().resolve("packets.txt"), out.toString().getBytes());
            return 0;
        }

        if (resource.equals("docs")) {
            var docs = directory.resolve("dump").resolve("docs").toFile();
            docs.mkdirs();

            var categories = new HashMap<Category, List<Module>>();

            for (var i : SigmaUtils.modules.values())
                categories.computeIfAbsent(i.category, k -> new ArrayList<>()).add(i);

            for (var i : categories.entrySet().stream().sorted(Comparator.comparing(a -> a.getKey().name())).toList()) {
                var name = i.getKey().id;
                name = name.substring(0, 1).toUpperCase() + name.substring(1);

                var file = docs.toPath().resolve(name + ".md").toFile();
                var text = new StringBuilder();

                for (var j : i.getValue().stream().sorted(Comparator.comparing(a -> a.name)).toList())
                    text.append(getModuleDocs(j));

                Files.write(file.toPath(), text.toString().getBytes());
            }
        }

        return 0;
    }

    String getModuleDocs(Module module) {
        var builder = new StringBuilder();
        builder.append("# ").append(module.getClass().getSimpleName()).append("\n\n");
        builder.append(module.description).append("\n\n");

        var categories = new HashMap<String, List<Setting<?>>>();
        var settings = 0;
        for (var i : Config.moduleSettings.get(module.getClass())) {
            if (i instanceof DummySetting || i.getId().equals("keybind")) continue;
            categories.computeIfAbsent(i.getCategory(), k -> new ArrayList<>()).add(i);
            settings++;
        }

        if (categories.isEmpty()) return builder.toString();
        builder.append("<details>\n<summary>Settings (").append(settings).append(")</summary>\n<br>\n\n");
        for (var i : categories.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
            builder.append("> ## ").append(i.getKey()).append("\n>\n");
            for (var j : i.getValue().stream().sorted(Comparator.comparing(Setting::getName)).toList()) {
                builder.append("> ### ")
                        .append(j.getName())
                        .append(" - ")
                        .append(settingType(j))
                        .append("\n>\n");

                var desc = j.getDescription();
                if (desc == null) desc = Text.of("No description provided.");
                builder.append("> ").append(desc.getString()).append("\n>\n");

                if (j instanceof EnumSetting<?> es) {
                    builder.append("> #### Options\n>\n");
                    var docs = j instanceof Util.DocumentedEnum doc ? Arrays.stream(doc.getDocs())
                            .iterator() : Collections.emptyIterator();
                    for (var k : es.getEnum().getEnumConstants())
                        builder.append("> - ")
                                .append(k.name())
                                .append(docs.hasNext() ? " - " : "")
                                .append(docs.hasNext() ? docs.next() : "")
                                .append("\n");
                }
            }
        }

        builder.append("</details>\n\n");
        return builder.toString();
    }

    String settingType(Setting<?> setting) {
        if (setting instanceof NumberSetting ns) {
            if (ns.getPrecision() == 0) return "Integer";
            return "Double";
        }
        if (setting instanceof BoolSetting) return "Boolean";
        if (setting instanceof EnumSetting<?> es) return String.format("Enum\\<%s\\>", es.getEnum().getSimpleName());
        if (setting instanceof DummySetting) return "Dummy";
        if (setting instanceof KeyBindSetting) return "Keybinding";
        if (setting instanceof StringSetting) return "String";
        if (setting instanceof DynamicListSetting<?>) return "List";
        return "Unknown";
    }
}
