package com.lagprotect.mixin;

import com.lagprotect.CustomKeepAliveAccess;
import com.lagprotect.LagProtect;
//? if >=1.21.11 {
import net.minecraft.util.Util;
//?} else {
/*import net.minecraft.Util;
*///?}
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin extends ServerCommonPacketListenerImpl implements CustomKeepAliveAccess {
    @Unique
    private static final long RESPONSE_GRACE_MS = 1000L;
    @Unique
    private static final long MAX_PROTECTION_MS = 15000L;
    @Unique
    private static final long MIN_CHALLENGE_ID = -10000L;

    // Written on the server thread, read on the network thread (and vice versa)
    @Unique
    private volatile long lagprotect$challenge = 0L;
    @Unique
    private volatile boolean lagprotect$pending = false;
    @Unique
    private volatile long lagprotect$pendingSince = 0L;
    @Unique
    private volatile long lagprotect$lastMovePacket = 0L;
    @Unique
    private boolean lagprotect$wasProtected = false;

    @Shadow
    public ServerPlayer player;

    @Shadow
    private int tickCount;

    public ServerGamePacketListenerMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
        super(server, connection, cookie);
    }

    @Override
    public void lagprotect$handleCustomKeepAlive(ServerboundKeepAlivePacket packet) {
        if (lagprotect$pending && packet.getId() == lagprotect$challenge) {
            lagprotect$pending = false;
        }
    }

    @Override
    public boolean lagprotect$isProtected() {
        if (!lagprotect$pending) {
            return false;
        }
        long now = Util.getMillis();
        long overdue = now - lagprotect$pendingSince;
        return overdue > RESPONSE_GRACE_MS
                && overdue < MAX_PROTECTION_MS
                && now - lagprotect$lastMovePacket > RESPONSE_GRACE_MS;
    }

    @Inject(method = "handleMovePlayer", at = @At("HEAD"))
    private void lagprotect$trackClientActivity(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        lagprotect$lastMovePacket = Util.getMillis();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void lagprotect$keepAlive(CallbackInfo ci) {
        boolean nowProtected = lagprotect$isProtected();
        if (nowProtected != lagprotect$wasProtected) {
            lagprotect$wasProtected = nowProtected;
            if (nowProtected) {
                LagProtect.LOGGER.info("[Lag Protect] {} protected", player.getName().getString());
            } else {
                LagProtect.LOGGER.info("[Lag Protect] {} no longer protected", player.getName().getString());
            }
        }

        //? if >=1.21.11 {
        int interval = player.level().getGameRules().get(LagProtect.LAG_PROTECT);
        //?} else {
        /*int interval = server.getGameRules().getInt(LagProtect.LAG_PROTECT);
        *///?}
        if (tickCount % interval != 0) {
            return;
        }

        if (lagprotect$pending) {
            if (Util.getMillis() - lagprotect$pendingSince >= MAX_PROTECTION_MS) {
                lagprotect$pending = false;
            }
            return;
        }

        lagprotect$challenge = lagprotect$challenge <= MIN_CHALLENGE_ID ? -1L : lagprotect$challenge - 1L;
        lagprotect$pendingSince = Util.getMillis();
        lagprotect$pending = true;
        this.send(new ClientboundKeepAlivePacket(lagprotect$challenge));
    }
}
