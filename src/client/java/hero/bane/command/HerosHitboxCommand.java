package hero.bane.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import hero.bane.HerosHitbox;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HerosHitboxCommand {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(HerosHitboxCommand.class);

    public static void registerCommands() {
        LOGGER.info("Registering HerosHitbox commands");
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("heroshitbox")
                            .executes(HerosHitboxCommand::toggleAll)
                            .then(ClientCommandManager.literal("line1").executes(HerosHitboxCommand::toggleLine1))
                            .then(ClientCommandManager.literal("line2").executes(HerosHitboxCommand::toggleLine2))
                            .then(ClientCommandManager.literal("pearl").executes(HerosHitboxCommand::togglePearl))
                            .then(ClientCommandManager.literal("wind").executes(HerosHitboxCommand::toggleWind))
                            .then(ClientCommandManager.literal("crystal").executes(HerosHitboxCommand::toggleCrystal))
                            .then(ClientCommandManager.literal("interaction").executes(HerosHitboxCommand::toggleInteraction))
                            .then(ClientCommandManager.literal("gliding").executes(HerosHitboxCommand::toggleGliding))
                            .then(ClientCommandManager.literal("backstab").executes(HerosHitboxCommand::toggleBackstab))
            );
        });
    }

    private static int toggleAll(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
        HerosHitbox.isOn = !HerosHitbox.isOn;
        say("Hitboxes are now " + (HerosHitbox.isOn ? "On" : "Off"));
        return 1;
    }

    private static int toggleLine1(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
        HerosHitbox.line1 = !HerosHitbox.line1;
        say("Line 1 is now " + (HerosHitbox.line1 ? "On" : "Off"));
        return 1;
    }

    private static int toggleLine2(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
        HerosHitbox.line2 = !HerosHitbox.line2;
        say("Line 2 is now " + (HerosHitbox.line2 ? "On" : "Off"));
        return 1;
    }

    private static int togglePearl(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
        HerosHitbox.pearlChanged = !HerosHitbox.pearlChanged;
        say("Pearl Hitbox is now " + (HerosHitbox.pearlChanged ? "On" : "Off"));
        return 1;
    }

    private static int toggleWind(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
        HerosHitbox.windChanged = !HerosHitbox.windChanged;
        say("Wind Charge Hitbox is now " + (HerosHitbox.windChanged ? "On" : "Off"));
        return 1;
    }

    private static int toggleCrystal(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
        HerosHitbox.crystalChanged = !HerosHitbox.crystalChanged;
        say("Crystal Hitbox is now " + (HerosHitbox.crystalChanged ? "On" : "Off"));
        return 1;
    }

    private static int toggleInteraction(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
        HerosHitbox.interactionChanged = !HerosHitbox.interactionChanged;
        say("Interaction Hitbox is now " + (HerosHitbox.interactionChanged ? "On" : "Off"));
        return 1;
    }

    private static int toggleGliding(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
        HerosHitbox.elytraHitboxChanged = !HerosHitbox.elytraHitboxChanged;
        say("Elytra Hitbox is now " + (HerosHitbox.elytraHitboxChanged ? "On" : "Off"));
        return 1;
    }

    private static int toggleBackstab(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
        HerosHitbox.shieldHitboxChanged = !HerosHitbox.shieldHitboxChanged;
        say("Player Backstab Hitbox is now " + (HerosHitbox.shieldHitboxChanged ? "On" : "Off"));
        return 1;
    }

    private static void say(String message) {
        if (client.player != null) {
            client.player.sendMessage(Text.literal("[heroshitbox] " + message), false);
        }
    }
}
