package com.teamtea.eclipticseasons.compat.kubejs;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class KubeJSPluginImpl implements KubeJSPlugin {

    public static final ESBindings ES_BINDINGS = new ESBindings();

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("EclipticSeasonsApi", EclipticSeasonsApi.getInstance());
        bindings.add("EclipticSeasonsBindings", ES_BINDINGS);
    }

    public static class ESBindings {
        public boolean testInput(Object obj) {
            EclipticSeasons.logger(obj.toString(),"9431043");
            return true;
        }

        public boolean canSnowyBlockInteract() {
            return EclipticUtil.canSnowyBlockInteract();
        }

        public boolean removeSnowyStatus(Level level, BlockPos blockpos) {
            if (level instanceof ServerLevel serverLevel) {
                SnowyMapChecker.removeSnowyStatus(serverLevel, blockpos);
            } else {
                ClientCon.agent.setChunkDirty(SectionPos.of(blockpos));
            }
            return true;
        }
    }
}
