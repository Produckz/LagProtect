package com.lagprotect.mixin;

import com.lagprotect.CustomKeepAliveAccess;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerboundKeepAlivePacket.class)
public abstract class KeepAlivePacketMixin {

    @Inject(
            method = "handle(Lnet/minecraft/network/protocol/common/ServerCommonPacketListener;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void lagprotect$interceptCustomKeepAlive(ServerCommonPacketListener listener, CallbackInfo ci) {
        ServerboundKeepAlivePacket packet = (ServerboundKeepAlivePacket) (Object) this;
        // Custom challenge ids are negative; vanilla ids are Util.getMillis() and always positive
        if (packet.getId() >= 0L) {
            return;
        }
        if (listener instanceof CustomKeepAliveAccess access) {
            access.lagprotect$handleCustomKeepAlive(packet);
        }
        ci.cancel();
    }
}
