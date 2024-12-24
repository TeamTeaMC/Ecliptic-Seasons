package com.teamtea.eclipticseasons.common.misc;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.util.ColorHelper;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
        HashMap<Holder<Biome>,Color> hashSet = new HashMap<>();
        // HashSet<Color> hashSetColor = new HashSet<>();
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

                Color color=null;
                if (biome != null) {
                     color=new Color(ColorHelper.simplyMixColor(biome.value().getGrassColor(biomePos.getX(), biomePos.getZ()), 0.85f,
                            biome.value().getWaterColor(), 0.15f));
                    color=new Color(RandomSource.create(biome.getRegisteredName().hashCode()).nextInt(256*256*256));

                    graphics2D.setColor(color);
                    id=0;
                }

                if (biome == null
                        || biome.is(Biomes.THE_VOID)
                        || id == -1) {
                    color=Color.BLACK;
                    graphics2D.setColor(color);
                }
                if (biome != null && biome.is(Biomes.PLAINS)) {
                    color=Color.RED;
                    graphics2D.setColor(color);
                }
                graphics2D.fillRect(i, j, 1, 1);

                hashSet.put(biome,color);
            }
        }
        graphics2D.setColor(Color.CYAN);
        Font monospaced = new Font("Monospaced", 0, 12);
        graphics2D.setFont(monospaced);

        graphics2D.drawString("⭐",ChunkInfoMap.getChunkValue(source.getPlayer().getBlockX()) - 5,
                ChunkInfoMap.getChunkValue(source.getPlayer().getBlockZ()) - 5);
        int i =0;
        for (Map.Entry<Holder<Biome>, Color> holderColorEntry : hashSet.entrySet()) {
            // graphics2D.setColor(Color.WHITE);
            // graphics2D.drawString( holderColorEntry.getKey().getRegisteredName()+","+ Component.translatable(Util.makeDescriptionId("biome", holderColorEntry.getKey().getKey().location())).getString(),4,
            //         20*(++i)-1);
            graphics2D.setColor(holderColorEntry.getValue());
            graphics2D.drawString( holderColorEntry.getKey().getRegisteredName()+","+ Component.translatable(Util.makeDescriptionId("biome", holderColorEntry.getKey().getKey().location())).getString(),5,
                    20*(++i));
        }
        // graphics2D.fillArc(ChunkInfoMap.getChunkValue(source.getPlayer().getBlockX()) - 5,
        //         ChunkInfoMap.getChunkValue(source.getPlayer().getBlockZ()) - 5,
        //         10, 10,0,360);

        graphics2D.dispose();
        try {
            if (!new File(EclipticSeasonsApi.MODID).exists()) {
                new File(EclipticSeasonsApi.MODID).mkdir();
            }
            String s = source.getLevel() instanceof ServerLevel serverLevel ?
                    serverLevel.toString().split("\\[")[1].split("]")[0] : "client";
            if (!new File(EclipticSeasonsApi.MODID+"/"+s).exists()) {
                new File(EclipticSeasonsApi.MODID+"/"+s).mkdir();
            }
            String s1 = "%s/%s/%s_%s.png".formatted(EclipticSeasonsApi.MODID,
                    s,
                    x, z);
            ImageIO.write(image, "png", new File(s1));
            source.sendSystemMessage(Component.literal("export ok for "+s1));
        } catch (IOException e) {
            source.sendSystemMessage(Component.literal("export fail \n%s".formatted(e.getMessage())));
        }
        return 1;
    }

}
