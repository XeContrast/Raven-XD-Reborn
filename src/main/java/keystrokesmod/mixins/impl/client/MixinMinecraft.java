package keystrokesmod.mixins.impl.client;

import de.florianmichael.viamcp.fixes.AttackOrder;
import keystrokesmod.Raven;
import keystrokesmod.event.ClickEvent;
import keystrokesmod.event.PreTickEvent;
import keystrokesmod.event.RightClickEvent;
import keystrokesmod.event.WorldChangeEvent;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.combat.HitBox;
import keystrokesmod.module.impl.combat.Reach;
import keystrokesmod.module.impl.exploit.ExploitFixer;
import keystrokesmod.module.impl.render.Animations;
import keystrokesmod.module.impl.render.FreeLook;
import keystrokesmod.module.impl.render.Watermark;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.profile.Profile;
import keystrokesmod.utility.render.BackgroundUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;

import static keystrokesmod.Raven.mc;

@Mixin(value = Minecraft.class, priority = 1001)
public abstract class MixinMinecraft  implements IThreadListener, IPlayerUsage {
    @Unique private @Nullable WorldClient raven_XD$lastWorld = null;

    @Shadow
    public abstract void updateDisplay();

    @Mutable
    @Final
    @Shadow
    public DefaultResourcePack mcDefaultResourcePack;

    @Shadow
    public abstract void draw(int p_draw_1_, int p_draw_2_, int p_draw_3_, int p_draw_4_, int p_draw_5_, int p_draw_6_, int p_draw_7_, int p_draw_8_, int p_draw_9_, int p_draw_10_);

    @Shadow
    public int displayWidth;

    @Shadow
    public int displayHeight;

    @Shadow
    private static final Logger logger = LogManager.getLogger();

    @Shadow
    public MovingObjectPosition objectMouseOver;

    @Inject(method = "runTick", at = @At("HEAD"))
    private void runTickPre(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new PreTickEvent());

        if (raven_XD$lastWorld != mc.theWorld && Utils.nullCheck()) {
            MinecraftForge.EVENT_BUS.post(new WorldChangeEvent());
        }

        this.raven_XD$lastWorld = mc.theWorld;
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;onStoppedUsingItem(Lnet/minecraft/entity/player/EntityPlayer;)V",
            shift = At.Shift.BY, by = 2
    ))
    private void onRunTick$usingWhileDigging(CallbackInfo ci) {
        if (ModuleManager.animations != null && ModuleManager.animations.isEnabled() && Animations.swingWhileDigging.isToggled()
                && mc.gameSettings.keyBindAttack.isKeyDown()) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                mc.thePlayer.swingItem();
            }
        }
    }

    @Inject(method = "clickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;swingItem()V"), cancellable = true)
    private void beforeSwingByClick(CallbackInfo ci) {
        ClickEvent event = new ClickEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "clickMouse",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;swingItem()V"))
    public void fixSwing(CallbackInfo ci) {
        AttackOrder.sendConditionalSwing(this.objectMouseOver);
    }

    @Inject(method = "clickMouse",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;attackEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V"))
    public void fixAttack(CallbackInfo ci) {
        AttackOrder.sendFixedAttack(mc.thePlayer, this.objectMouseOver.entityHit);
    }
    /**
     * @author xia__mc
     * @reason to fix reach and hitBox won't work with autoClicker
     */
    @Inject(method = "clickMouse", at = @At("HEAD"))
    private void onLeftClickMouse(CallbackInfo ci) {
        FreeLook.call();
        Reach.call();
        HitBox.call();
    }

    /**
     * @author xia__mc
     * @reason to fix freelook do impossible action
     */
    @Inject(method = "rightClickMouse", at = @At("HEAD"), cancellable = true)
    private void onRightClickMouse(CallbackInfo ci) {
        RightClickEvent event = new RightClickEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "crashed", at = @At("HEAD"), cancellable = true)
    private void onCrashed(CrashReport crashReport, CallbackInfo ci) {
        try {
            if (ExploitFixer.onCrash(crashReport)) {
                ci.cancel();
            }
        } catch (Throwable ignored) {
        }
    }

    @Inject(method = "createDisplay", at = @At(value = "RETURN"))
    private void onSetTitle(@NotNull CallbackInfo ci) {
        Display.setTitle("RavenXD " + Watermark.VERSION);
    }

    /**
     * @author XeContrast
     * @reason Fix
     */
    @Overwrite
    public void drawSplashScreen(TextureManager p_drawSplashScreen_1_) {
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int i = scaledresolution.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i, true);
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        InputStream inputstream = null;

        try {
            inputstream = this.mcDefaultResourcePack.getInputStream(BackgroundUtils.getLogoPng());
            p_drawSplashScreen_1_.bindTexture(BackgroundUtils.getLogoPng());
        } catch (IOException var12) {
            logger.error("Unable to load logo: {}", BackgroundUtils.getLogoPng(), var12);
        } finally {
            IOUtils.closeQuietly(inputstream);
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0, this.displayHeight, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(this.displayWidth, this.displayHeight, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(this.displayWidth, 0.0, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int j = 256;
        int k = 256;
        this.draw((scaledresolution.getScaledWidth() - j) / 2, (scaledresolution.getScaledHeight() - k) / 2, 0, 0, j, k, 255, 255, 255, 255);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        this.updateDisplay();
    }

    @Inject(method = "shutdown",at = @At("HEAD"))
    public void saveConfig(CallbackInfo ci) {
        Raven.profileManager.saveProfile(new Profile(Raven.configName, 0));
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(GameConfiguration p_i45547_1_, CallbackInfo ci) {
        this.mcDefaultResourcePack = new DefaultResourcePack((new ResourceIndex(p_i45547_1_.folderInfo.assetsDir, p_i45547_1_.folderInfo.assetIndex)).getResourceMap());
    }
}
