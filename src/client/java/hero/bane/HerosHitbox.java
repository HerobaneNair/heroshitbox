package hero.bane;

import hero.bane.command.HerosHitboxCommand;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class HerosHitbox implements ClientModInitializer {
	public static MinecraftClient client;
	public static boolean isOn = true;
	public static boolean line1 = true;
	public static boolean line2 = false;
	public static boolean pearlChanged = true;
	public static boolean windChanged = true;
	public static boolean crystalChanged = true;
	public static boolean interactionChanged = true;
	public static boolean elytraHitboxChanged = false;
	public static boolean shieldHitboxChanged = true;

	@Override
	public void onInitializeClient() {
		client = MinecraftClient.getInstance();
		HerosHitboxCommand.registerCommands();
	}
}