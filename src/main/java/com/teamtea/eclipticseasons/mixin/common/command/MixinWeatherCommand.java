package com.teamtea.eclipticseasons.mixin.common.command;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.command.CommandHandler;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.WeatherCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WeatherCommand.class)
public class MixinWeatherCommand {

    @Inject(method = "setClear", at = @At(value = "TAIL"))
    private static void es$setClear(CommandSourceStack sourceStack, int i, CallbackInfoReturnable<Integer> cir) {
        try {
            CommandHandler.setBiomeRain(sourceStack, bh->true, false, false);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "setRain", at = @At(value = "TAIL"))
    private static void es$setRain(CommandSourceStack sourceStack, int i, CallbackInfoReturnable<Integer> cir) {
        try {
            CommandHandler.setBiomeRain(sourceStack,bh->true, true, false);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "setThunder", at = @At(value = "TAIL"))
    private static void es$setThunder(CommandSourceStack sourceStack, int i, CallbackInfoReturnable<Integer> cir) {
        try {
            CommandHandler.setBiomeRain(sourceStack, bh->true, true, true);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }
}
