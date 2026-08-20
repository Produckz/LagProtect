package com.lagprotect;

import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;

public interface CustomKeepAliveAccess {
    void lagprotect$handleCustomKeepAlive(ServerboundKeepAlivePacket packet);

    boolean lagprotect$isProtected();
}
