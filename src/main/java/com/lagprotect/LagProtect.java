package com.lagprotect;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? if >=1.21.11 {
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
//?} else {
/*import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
*///?}

public class LagProtect implements ModInitializer {
    public static final String MOD_ID = "lag_protect";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    //? if >=1.21.11 {
    public static GameRule<Integer> LAG_PROTECT;
    //?} else {
    /*public static GameRules.Key<GameRules.IntegerValue> LAG_PROTECT;
    *///?}

    @Override
    public void onInitialize() {
        //? if >=1.21.11 {
        // Default namespace until MC-303846 lets /gamerule accept namespaced ids
        LAG_PROTECT = GameRuleBuilder.forInteger(30)
                .range(0, 200)
                .buildAndRegister(Identifier.withDefaultNamespace("lag_protect"));
        //?} else {
        /*LAG_PROTECT = GameRuleRegistry.register("lag_protect", GameRules.Category.MISC,
                GameRuleFactory.createIntRule(30, 0, 200));
        *///?}

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayer player && player.connection instanceof CustomKeepAliveAccess access) {
                return !access.lagprotect$isProtected();
            }
            return true;
        });
    }
}
