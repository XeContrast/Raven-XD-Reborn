package keystrokesmod.mixins.impl.gui;

import de.florianmichael.viamcp.gui.GuiProtocolSelector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends MixinGuiScreen {
    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        this.buttonList.add(new GuiButton(69, 5, 5, 90, 20, "Version"));
    }

    @Inject(method = "actionPerformed",at = @At("HEAD"))
    public void idk(GuiButton p_actionPerformed_1_, CallbackInfo ci) {
        if (p_actionPerformed_1_.enabled) {
            if (p_actionPerformed_1_.id == 69) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiProtocolSelector((GuiScreen) (Object)this));
            }
        }
    }
}
