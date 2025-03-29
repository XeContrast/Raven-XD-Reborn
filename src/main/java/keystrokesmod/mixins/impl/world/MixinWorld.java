package keystrokesmod.mixins.impl.world;

import de.florianmichael.viamcp.fixes.FixedSoundEngine;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(World.class)
public class MixinWorld {
    @Inject(method = "destroyBlock",at = @At("HEAD"), cancellable = true)
    public void fixSound(BlockPos p_destroyBlock_1_, boolean p_destroyBlock_2_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(FixedSoundEngine.destroyBlock((World) (Object)this, p_destroyBlock_1_, p_destroyBlock_2_));
    }
}
