package keystrokesmod.mixins.impl.gui;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.render.NoBackground;
import keystrokesmod.module.impl.player.ChestStealer;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.render.BackgroundUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {

    @Shadow
    protected List<GuiButton> buttonList;

    @Shadow
    protected abstract void keyTyped(char p_keyTyped_1_, int p_keyTyped_2_);

    @Inject(method = "handleKeyboardInput",at = @At("HEAD"),cancellable = true)
    private void inputFix(CallbackInfo ci) {
        if (Keyboard.getEventKey() == 0 && Keyboard.getEventCharacter() >= ' ' || Keyboard.getEventKeyState()) {
            this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }

        ci.cancel();
    }

    @Inject(method = "drawDefaultBackground", at = @At("HEAD"), cancellable = true)
    public void onDrawDefaultBackground(CallbackInfo ci) {
        if (Utils.nullCheck() && (NoBackground.noRender() || ChestStealer.noChestRender()))
            ci.cancel();
    }

    @Inject(method = "drawBackground", at = @At("HEAD"), cancellable = true)
    public void onDrawBackground(int p_drawWorldBackground_1_, @NotNull CallbackInfo ci) {
        if (!ModuleManager.clientTheme.isEnabled() || !ModuleManager.clientTheme.background.isToggled())
            return;

        BackgroundUtils.renderBackground((GuiScreen) (Object) this);
        ci.cancel();
    }
}
