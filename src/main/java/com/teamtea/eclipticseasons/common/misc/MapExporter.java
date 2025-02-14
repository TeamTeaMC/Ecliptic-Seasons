package com.teamtea.eclipticseasons.common.misc;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapExporter {
    public static int exportMap(CommandSourceStack source, BlockPos pos) {
        int x = MapChecker.blockToRegionCoord(pos.getX());
        int z = MapChecker.blockToRegionCoord(pos.getZ());
        Level level = source.getUnsidedLevel();
        ChunkInfoMap map = MapChecker.getChunkMap(level, x, z);

        if (map == null) return 0;

        int size = MapChecker.ChunkSize;
        int ax = MapChecker.ChunkSizeAxis;

        int offset = 200;
        BufferedImage image = new BufferedImage(size + offset, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, offset, size);

        HashMap<Holder<Biome>, Color> hashSet = new HashMap<>();
        // HashSet<Color> hashSetColor = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                var biomePos = new BlockPos((x << ax) + i, pos.getY(), (z << ax) + j);
                Holder<Biome> biome = MapChecker.getSurfaceBiome(level,
                        biomePos);
                Color color;
                if (biome.is(Biomes.THE_VOID) ) {
                    color = Color.BLACK;
                    // biome=biome.is(Biomes.THE_VOID)?biome:
                    //         level.registryAccess().holderOrThrow(Biomes.THE_VOID);
                }else if ( !MapChecker.isLoadNearBy(level, biomePos)
                        &&biome==level.getUncachedNoiseBiome(pos.getX()>>2,pos.getY()>>2,pos.getZ()>>2)) {
                    color = Color.LIGHT_GRAY;
                    biome=null;
                } else if (biome.is(Biomes.PLAINS)) {
                    color = Color.RED;
                } else {
                    // color = new Color(ColorHelper.simplyMixColor(biome.value().getGrassColor(biomePos.getX(), biomePos.getZ()), 0.85f,
                    //         biome.value().getWaterColor(), 0.15f));
                    color = new Color(RandomSource.create(biome.getRegisteredName().hashCode()).nextInt(256 * 256 * 256));
                }

                graphics2D.setColor(color);
                graphics2D.fillRect(i + offset, j, 1, 1);

                hashSet.put(biome, color);
            }
        }

        int xp = ChunkInfoMap.getChunkValue(pos.getX()) - 5;
        int zp = ChunkInfoMap.getChunkValue(pos.getZ()) - 5;

        graphics2D.setColor(Color.WHITE);
        graphics2D.drawLine(offset, zp, size + offset, zp);
        graphics2D.drawLine(xp + offset, 0, xp + offset, size);

        graphics2D.setColor(Color.YELLOW);
        Font monospaced = new Font("Monospaced", 0, 12);
        graphics2D.setFont(monospaced);
        graphics2D.drawString("⭐", xp + offset, zp);

        int i = 0;
        for (Map.Entry<Holder<Biome>, Color> holderColorEntry : hashSet.entrySet()) {
            // graphics2D.setColor(Color.WHITE);
            // graphics2D.drawString( holderColorEntry.getKey().getRegisteredName()+","+ Component.translatable(Util.makeDescriptionId("biome", holderColorEntry.getKey().getKey().location())).getString(),4,
            //         20*(++i)-1);
            graphics2D.setColor(holderColorEntry.getValue());
            // List<String> sss= new ArrayList<>();
            // sss.add("s");
            graphics2D.drawString(
                    holderColorEntry.getKey()==null?"not load":holderColorEntry.getKey().getRegisteredName() + "," + Component.translatable(Util.makeDescriptionId("biome", holderColorEntry.getKey().getKey().location())).getString(), 5,
                    20 * (++i));
        }
        // graphics2D.fillArc(ChunkInfoMap.getChunkValue(source.getPlayer().getBlockX()) - 5,
        //         ChunkInfoMap.getChunkValue(source.getPlayer().getBlockZ()) - 5,
        //         10, 10,0,360);

        graphics2D.dispose();
        try {
            if (!new File(EclipticSeasonsApi.MODID).exists()) {
                new File(EclipticSeasonsApi.MODID).mkdir();
            }
            String s = level instanceof ServerLevel serverLevel ?
                    serverLevel.toString().split("\\[")[1].split("]")[0] :
                    ServerLifecycleHooks.getCurrentServer() == null ? ClientCon.ServerName :
                            ServerLifecycleHooks.getCurrentServer().getMotd();
            s+="~"+level.dimension().location().toString().replace(":","_");
            if (!new File(EclipticSeasonsApi.MODID + "/" + s).exists()) {
                new File(EclipticSeasonsApi.MODID + "/" + s).mkdir();
            }
            String s1 = "%s/%s/%s_%s.png".formatted(EclipticSeasonsApi.MODID,
                    s,
                    x, z);
            ImageIO.write(image, "png", new File(s1));
            source.sendSystemMessage(Component.literal("export ok for " + s1));
        } catch (IOException e) {
            source.sendSystemMessage(Component.literal("export fail \n%s".formatted(e.getMessage())));
        }
        return 1;
    }

}
