package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;

import java.util.List;

public class SnowyMakerItem extends Item {
    public SnowyMakerItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getHand() == InteractionHand.MAIN_HAND) {
            var level = pContext.getLevel();
            var clickedPos = pContext.getClickedPos();
            var chunkPos = new ChunkPos(clickedPos);
            var sectionPos = SectionPos.of(clickedPos);
            if (level.isLoaded(clickedPos)
                    && (pContext.getPlayer() == null
                    || (level.mayInteract(pContext.getPlayer(), pContext.getClickedPos())
                    // 这个东西也许应该冒险模式可以用
                    // && pContext.getPlayer().mayUseItemAt(clickedPos, pContext.getClickedFace(), pContext.getItemInHand())
            )
            )
            ) {
                if (level instanceof ServerLevel serverLevel) {
                    var chunk = serverLevel.getChunkAt(pContext.getClickedPos());
                    if (!chunk.hasData(EclipticSeasons.ModContents.SNOWY_REMOVER)) {
                        chunk.setData(EclipticSeasons.ModContents.SNOWY_REMOVER, new SnowyRemover(new int[16][16]));
                    }
                    var data = chunk.getData(EclipticSeasons.ModContents.SNOWY_REMOVER);
                    data.setChunkPos(clickedPos,
                            data.notSnowyAt(clickedPos) ?
                                    SnowyRemover.SNOWY :
                                    SnowyRemover.NONE_SNOWY);

                    var distance =
                            (serverLevel.getServer() instanceof DedicatedServer dedicatedServer ?
                                    dedicatedServer.getProperties().viewDistance :
                                    Minecraft.getInstance().options.renderDistance().get())
                                    * 16;
                    var players = serverLevel.getPlayers(
                            serverPlayer -> {
                                var onPos = serverPlayer.getOnPos();
                                return onPos.distToCenterSqr(clickedPos.getCenter()) < distance;
                            }
                    );
                    for (ServerPlayer player : players) {
                        MapChecker.updateChunk(chunk, chunkPos, player, List.of(sectionPos.y()), List.of(clickedPos));
                    }

                    if (data.allSnowAble()) {
                        chunk.removeData(EclipticSeasons.ModContents.SNOWY_REMOVER);
                    }

                    chunk.setUnsaved(true);
                    // serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    //         clickedPos.getX() + 0.5d,
                    //         clickedPos.getY() + 1.2d,
                    //         clickedPos.getZ() + 0.5d,
                    //         10,
                    //         Mth.randomBetween(level.getRandom(), -1.0F, 1.0F) * 0.083333336D,
                    //         0.05D,
                    //         Mth.randomBetween(level.getRandom(), -1.0F, 1.0F) * 0.083333336D
                    //         , 0.01d);

                } else {

                    var data = level.getChunkAt(pContext.getClickedPos()).getData(EclipticSeasons.ModContents.SNOWY_REMOVER);

                    var type = data.notSnowyAt(clickedPos) ? ParticleTypes.SMOKE : ParticleTypes.SNOWFLAKE;
                    for (int i = 0; i < 10; i++) {
                        level.addParticle(
                                type,
                                clickedPos.getX() + 0.5f,
                                clickedPos.getY() + 1,
                                clickedPos.getZ() + 0.5f,
                                Mth.randomBetween(level.getRandom(), -1.0F, 1.0F) * 0.083333336F,
                                0.05F,
                                Mth.randomBetween(level.getRandom(), -1.0F, 1.0F) * 0.083333336F
                        );
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.useOn(pContext);
    }
}
