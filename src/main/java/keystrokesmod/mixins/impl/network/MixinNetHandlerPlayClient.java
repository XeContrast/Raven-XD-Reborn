package keystrokesmod.mixins.impl.network;


import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import keystrokesmod.Raven;
import keystrokesmod.event.PostVelocityEvent;
import keystrokesmod.event.PreVelocityEvent;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.utility.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {
    @Shadow
    private WorldClient clientWorldController;

    @Inject(method = "handleEntityVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V"), cancellable = true)
    public void onPreHandleEntityVelocity(S12PacketEntityVelocity packet, CallbackInfo ci) {
        if (!Utils.nullCheck()) return;

        if (packet.getEntityID() == Raven.mc.thePlayer.getEntityId()) {
            if (ModuleManager.longJump.isEnabled()) return;

            PreVelocityEvent event = new PreVelocityEvent(packet.getMotionX(), packet.getMotionY(), packet.getMotionZ());
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) ci.cancel();

            Entity entity = this.clientWorldController.getEntityByID(packet.getEntityID());
            entity.setVelocity((double) packet.getMotionX() / 8000.0, (double)packet.getMotionY() / 8000.0, (double)packet.getMotionZ() / 8000.0);
            ci.cancel();
        }
    }

    @Inject(method = "handleEntityVelocity", at = @At("RETURN"))
    public void onPostHandleEntityVelocity(S12PacketEntityVelocity packet, CallbackInfo ci) {
        if (!Utils.nullCheck()) return;

        if (packet.getEntityID() == Raven.mc.thePlayer.getEntityId()) {
            MinecraftForge.EVENT_BUS.post(new PostVelocityEvent());
        }
    }

    @Inject(method = "handleConfirmTransaction",at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V",shift = At.Shift.AFTER), cancellable = true)
    public void fixVia(S32PacketConfirmTransaction p_handleConfirmTransaction_1_, CallbackInfo ci) {
        if (ViaLoadingBase.getInstance().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_17)) {
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(p_handleConfirmTransaction_1_.getWindowId(), (short) 0, false));
            ci.cancel();
        }
    }
}
