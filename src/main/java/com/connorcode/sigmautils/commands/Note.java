package com.connorcode.sigmautils.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.minecraft.command.CommandSource.suggestMatching;

/*
New note system:
- /util note <global | server> <new | delete | list | edit> <name>

Global notes are notes that are accessible from any server.
Server notes are notes that are only accessible from the server you are on diffrent local worlds are considered diffrent servers.

Creating a new note will make a new note with the name you give it and open the editor for it.
Deleting a note will delete the note with the name you give it.
Listing notes will list all the notes you have on the server or globally.
Editing a note will open the editor for the note with the name you give it.
 */

public class Note implements Command {
    private static HashMap<String, List<SavedNote>> notes = new HashMap<>();
    private static List<SavedNote> globalNotes = new ArrayList<>();

    private static int noteAction(CommandContext<FabricClientCommandSource> context) {
        String action = getString(context, "action");
        switch (action) {
            case "new" -> newNote(context);
            case "delete" -> deleteNote(context);
            case "edit" -> editNote(context);
            default -> context.getSource().sendError(Text.of("Invalid action"));
        }
        return 0;
    }

    private static int chatNode(CommandContext<FabricClientCommandSource> context) {
        var message = getString(context, "text");
        context.getSource()
                .getPlayer()
                .sendMessage(Text.of(message));
        return 0;
    }

    private static void newNote(CommandContext<FabricClientCommandSource> context) {
        var name = getString(context, "name");
        var getLocation = getType(context);
        var world = getWorldId();
    }

    private static void deleteNote(CommandContext<FabricClientCommandSource> context) {
    }

    private static int listNotes(CommandContext<FabricClientCommandSource> context) {
        return 0;
    }

    private static void editNote(CommandContext<FabricClientCommandSource> context) {
    }

    private static String getWorldId() {
        var server = client.getCurrentServerEntry();
        if (server == null) return Objects.requireNonNull(client.getServer()).getName();
        else return server.address;
    }

    private static Type getType(CommandContext<FabricClientCommandSource> context) {
        return switch (getString(context, "type")) {
            case "global" -> Type.Global;
            case "server" -> Type.Server;
            default -> {
                context.getSource().sendError(Text.of("Invalid type. Must be global or server"));
                yield null;
            }
        };
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("note")
                        .then(ClientCommandManager.argument("type", string())
                                .suggests((context, builder) -> suggestMatching(new String[]{
                                        "global",
                                        "server"
                                }, builder))
                                .then(ClientCommandManager.argument("action", string())
                                        .suggests((context, builder) -> suggestMatching(new String[]{
                                                "new",
                                                "delete",
                                                "edit"
                                        }, builder))
                                        .then(ClientCommandManager.argument("name", string())
                                                .executes(Note::noteAction)))
                                .then(ClientCommandManager.literal("list")
                                        .executes(Note::listNotes))
                        )
                        .then(ClientCommandManager.literal("chat")
                                .then(ClientCommandManager.argument("text", greedyString())
                                        .executes(Note::chatNode)))));
    }

    enum Type {
        Global,
        Server
    }

    static class SavedNote {
        String name;
        String text;
        long created;
        long lastEdited;
    }

    static class NoteEditScreen extends Screen {
        SavedNote note;
        int noteWidth = 250;

        public NoteEditScreen(SavedNote note) {
            super(Text.of("Note Editor"));
            this.note = note;
        }

        @Override
        protected void init() {

        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            renderBackground(matrices);

            var lines = textRenderer.wrapLines(StringVisitable.plain(note.text), noteWidth);
            var startX = width / 2f - noteWidth / 2f;
            var height = lines.size() * 10;

            fill(matrices, (int) startX, 0, (int) (startX + noteWidth), height, 0x7F000000);

            for (int i = 0; i < lines.size(); i++)
                textRenderer.draw(matrices, lines.get(i), startX, i * 10, 0xFFFFFF);

            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
