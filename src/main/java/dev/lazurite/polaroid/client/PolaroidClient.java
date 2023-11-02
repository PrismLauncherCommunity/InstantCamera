package dev.lazurite.polaroid.client;

import dev.lazurite.polaroid.Polaroid;
import dev.lazurite.polaroid.client.render.PolaroidPhotoRenderer;
import dev.lazurite.polaroid.client.util.PhotoUtil;
import dev.lazurite.polaroid.item.CameraItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
public class PolaroidClient implements ClientModInitializer {
    public static final ResourceLocation CAMERA_SCOPE = new ResourceLocation(Polaroid.MODID, "textures/misc/polaroid_camera_scope.png");
    public static PolaroidPhotoRenderer PHOTO_RENDERER;

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(this::onClientReady);
        ClientPlayConnectionEvents.DISCONNECT.register(this::onClientDisconnect);
        ClientTickEvents.START_CLIENT_TICK.register(this::onClientTick);
    }


    protected void onClientReady(Minecraft minecraft) {
        PHOTO_RENDERER = new PolaroidPhotoRenderer(Minecraft.getInstance().getTextureManager());
    }

    void onClientDisconnect(ClientPacketListener listener, Minecraft client) {
        if (PHOTO_RENDERER != null) {
            PHOTO_RENDERER.clear();
        }
    }

    /**
     * This event is responsible for checking if the player is attacking while holding a {@link CameraItem}.
     * If so, a photo capture is queued for the renderer.
     * @param minecraft
     */
    protected void onClientTick(Minecraft minecraft) {
        final var player = minecraft.player;

        if (player != null &&
                player.isScoping() &&
                player.getMainHandItem().is(Polaroid.CAMERA_ITEM) &&
                minecraft.options.keyAttack.consumeClick()
        ) PhotoUtil.queuePhotoCapture();
    }
}
