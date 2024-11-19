package com.teamtea.eclipticseasons.common.misc;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.util.ColorHelper;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class MapExporter {
    public static int exportMap(CommandSourceStack source, BlockPos pos) {
        int x = MapChecker.blockToSectionCoord(pos.getX());
        int z = MapChecker.blockToSectionCoord(pos.getZ());
        ChunkInfoMap map = MapChecker.getChunkMap(source.getLevel(),x,z);

        if (map == null) return 0;
        
        int size=MapChecker.ChunkSize;
        int ax=MapChecker.ChunkSizeAxis;

        
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        HashSet<Holder<Biome>> hashSet = new HashSet<>();
        HashSet<Color> hashSetColor = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (int j =0; j < size; j++) {
                int id = map.getBiome(i, j);
                Holder<Biome> biome = null;
                if (id > 0) {
                    biome = MapChecker.idToBiome(source.getLevel(), id);
                } else {
                    // var biomePos = new BlockPos((x << ax) + i, pos.getY(), (z << ax) + j);
                    // // EclipticSeasonsMod.logger(biomePos.toString());
                    // biome = source.getLevel().getBiome(
                    //         biomePos);
                    // continue;
                }

                var biomePos = new BlockPos((x << ax) + i, pos.getY(), (z << ax) + j);
                biome = MapChecker.getSurfaceBiome(source.getLevel(),
                        biomePos);
                hashSet.add(biome);

                if (biome != null) {
                    var color=new Color(ColorHelper.simplyMixColor(biome.value().getGrassColor(biomePos.getX(), biomePos.getZ()), 0.85f,
                            biome.value().getWaterColor(), 0.15f));
                    graphics2D.setColor(color);
                    hashSetColor.add(color);
                    id=0;
                }

                if (biome == null
                        || biome.is(Biomes.THE_VOID)
                        || id == -1) {
                    graphics2D.setColor(Color.ORANGE);
                }
                graphics2D.fillRect(i, j, 1, 1);
            }
        }
        graphics2D.setColor(Color.PINK);
        graphics2D.fillRect(ChunkInfoMap.getChunkValue(source.getPlayer().getBlockX()) - 5,
                ChunkInfoMap.getChunkValue(source.getPlayer().getBlockZ()) - 5,
                10, 10);

        graphics2D.dispose();
        try {
            if (!new File(EclipticSeasonsApi.MODID).exists()) {
                new File(EclipticSeasonsApi.MODID).mkdir();
            }
            ImageIO.write(image, "png", new File("%s/%s_%s.png".formatted(EclipticSeasonsApi.MODID, x, z)));
            source.sendSystemMessage(Component.literal("export ok"));
        } catch (IOException e) {
            source.sendSystemMessage(Component.literal("export fail \n%s".formatted(e.getMessage())));
        }
        return 1;
    }

}
