package hero.bane.mixin;

import hero.bane.HerosHitbox;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    private static void drawVector(MatrixStack matrices, VertexConsumer vertexConsumers, Vector3f offset, Vec3d vec, int color) {
		throw new UnsupportedOperationException("Something happened");
    }

	@Inject(
			method = {"renderHitbox"},
			at = {@At("HEAD")},
			cancellable = true
	)
	private static void otherHitboxChange(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, CallbackInfo ci) {
		if (HerosHitbox.pearlChanged && entity instanceof EnderPearlEntity) {
			drawBoxHelper(matrices, vertices, entity, "#00FFFF", 1.0F);
			ci.cancel();
		} else if (HerosHitbox.windChanged && entity instanceof WindChargeEntity) {
			drawBoxHelper(matrices, vertices, entity, "#F9FFC0", 1.0F);
			double[] rgb = colorSeparator("#F9FFC0");
			float halfSize = 1.15625F;
			Box extraBox = new Box(entity.getX() - (double)halfSize, entity.getY() - (double)halfSize, entity.getZ() - (double)halfSize, entity.getX() + (double)halfSize, entity.getY() + (double)halfSize, entity.getZ() + (double)halfSize);
			WorldRenderer.drawBox(matrices, vertices, extraBox.offset(-entity.getX(), -entity.getY(), -entity.getZ()), (float)rgb[0], (float)rgb[1], (float)rgb[2], 0.5F);
			ci.cancel();
		} else if (HerosHitbox.crystalChanged && entity instanceof EndCrystalEntity) {
			drawBoxHelper(matrices, vertices, entity, "#ECC0FF", 0.5F);
			ci.cancel();
		} else if (HerosHitbox.interactionChanged && entity instanceof InteractionEntity) {
			drawBoxHelper(matrices, vertices, entity, "#FFFFFF", 0.2F);
			ci.cancel();
		} else if ((HerosHitbox.shieldHitboxChanged || HerosHitbox.elytraHitboxChanged) && entity instanceof PlayerEntity) {
			if (HerosHitbox.shieldHitboxChanged) {
				if (behindEntity(entity)) {
					if (((PlayerEntity) entity).isFallFlying() && HerosHitbox.elytraHitboxChanged) {
						drawBoxHelper(matrices, vertices, entity, "#AAFFFF", 1.0F);
					} else {
						drawBoxHelper(matrices, vertices, entity, "#AAFFAA", 1.0F);
					}
				} else {
					if (((PlayerEntity) entity).isFallFlying() && HerosHitbox.elytraHitboxChanged) {
						drawBoxHelper(matrices, vertices, entity, "#FFAAFF", 1.0F);
					} else {
						drawBoxHelper(matrices, vertices, entity, "#FFAAAA", 1.0F);
					}
				}
			} else if (((PlayerEntity) entity).isFallFlying()) {
				drawBoxHelper(matrices, vertices, entity, "#AAAAFF", 1.0F);
			}
			onDrawVectorHead(matrices, vertices, entity, tickDelta, red, green, blue, ci);
			ci.cancel();
		}
	}

	@Inject(
			method = {"renderHitbox"},
			at = {@At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;drawVector(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lorg/joml/Vector3f;Lnet/minecraft/util/math/Vec3d;I)V"
			)},
			cancellable = true
	)
	private static void onDrawVectorHead(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, CallbackInfo ci) {
		if (HerosHitbox.isOn) {
			if (entity instanceof PlayerEntity player) {
                if (HerosHitbox.line1 || HerosHitbox.line2) {
					Vec3d velocity = entity.getVelocity();
					int ping = 25;
					PlayerEntity myPlayer = HerosHitbox.client.player;

					assert myPlayer != null;

					PlayerListEntry playerEntry = Objects.requireNonNull(HerosHitbox.client.getNetworkHandler()).getPlayerListEntry(player.getUuid());
					if (playerEntry != null) {
						ping = playerEntry.getLatency() == 0 ? playerEntry.getLatency() : 25;
					}

					Vec3d facingMeWithY;
					if (HerosHitbox.line1) {
						facingMeWithY = entity.getRotationVec(tickDelta);
						double forwardVelocity = velocity.dotProduct(facingMeWithY);
						double forwardLength = 3.0D + forwardVelocity * (1.0D + (double)ping / 1000.0D);
						forwardLength = Math.max(forwardLength, 1.0D);
						drawVector(matrices, vertices, new Vector3f(0.0F, entity.getStandingEyeHeight(), 0.0F), facingMeWithY.multiply(forwardLength), colorCalc(10.0D * forwardVelocity));
					}

					if (HerosHitbox.line2 && !myPlayer.equals(player)) {
						facingMeWithY = myPlayer.getPos().subtract(entity.getPos());
						Vec3d facingMe = (new Vec3d(facingMeWithY.x, 0.0D, facingMeWithY.y)).normalize();
						double clientVelocity = velocity.dotProduct(facingMe);
						double clientLength = 1.5D + clientVelocity * (1.0D + (double)ping / 1000.0D);
						clientLength = Math.max(clientLength, 1.0D);
						drawVector(matrices, vertices, new Vector3f(0.0F, entity.getStandingEyeHeight() - 1.0F, 0.0F), facingMe.multiply(clientLength), colorCalc(10.0D * clientVelocity));
					}
				}
			} else {
				drawVector(matrices, vertices, new Vector3f(0.0F, entity.getStandingEyeHeight(), 0.0F), entity.getRotationVec(tickDelta).multiply(2.0F), -16776961);
			}
			ci.cancel();
		}
	}

	@Unique
	private static boolean behindEntity(Entity entity) {
		if (entity instanceof PlayerEntity targetPlayer) {
			if (HerosHitbox.client.player != null && targetPlayer != HerosHitbox.client.player) {
				Vec3d toClient = HerosHitbox.client.player.getPos().subtract(targetPlayer.getPos()).normalize();
				Vec3d facing = targetPlayer.getRotationVec(1.0F).normalize();
				return toClient.dotProduct(facing) <= 0D;
			}
		}

		return false;
	}

	@Unique
	private static int colorCalc(double offset) {
		double fx = 2.0D / (1.0D + Math.exp(-offset));
		int red = (int)(255.0D * Math.max(1.0D - fx, 0.0D));
		int green = (int)(255.0D * Math.max(fx - 1.0D, 0.0D));
		int blue = (int)(255.0D * Math.max(1.0D - Math.abs(fx - 1.0D), 0.0D));
		return -16777216 | red << 16 | green << 8 | blue;
	}

	@Unique
	private static double[] colorSeparator(String color) {
		if (color.startsWith("#")) {
			color = color.substring(1);
		}

		int r = Integer.parseInt(color.substring(0, 2), 16);
		int g = Integer.parseInt(color.substring(2, 4), 16);
		int b = Integer.parseInt(color.substring(4, 6), 16);
		return new double[]{(double)r / 255.0D, (double)g / 255.0D, (double)b / 255.0D};
	}

	@Unique
	private static void drawBoxHelper(MatrixStack matrices, VertexConsumer vertices, Entity entity, String color, float alpha) {
		Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
		double[] rgb = colorSeparator(color);
		WorldRenderer.drawBox(matrices, vertices, box, (float)rgb[0], (float)rgb[1], (float)rgb[2], alpha);
	}
}