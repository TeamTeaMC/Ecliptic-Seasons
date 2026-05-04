package com.teamtea.eclipticseasons.client.color.season;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.LeaveColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.SpruceLeavesColor;
import com.teamtea.eclipticseasons.api.data.client.ColorMode;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;

public class FoliageColorSourceDefault {
    public static String createSingle() {
        StringBuilder s = new StringBuilder(Blocks.SPRUCE_LEAVES.builtInRegistryHolder().unwrapKey().get().location().toString());
        s.append("@");
        for (SpruceLeavesColor spruceLeavesColor : SpruceLeavesColor.collectValues()) {
            s.append(String.format("#%06X", (0xFFFFFF & spruceLeavesColor.getColor())));
            if (spruceLeavesColor.getMix() < 1) {
                s.append("|").append(spruceLeavesColor.getMix());
            }
            s.append(",");
        }
        s.append(String.format("#%06X", (0xFFFFFF & FoliageColor.getEvergreenColor())));
        return s.toString();
    }

    public static List<? extends String> createConfig() {
        return List.of(createSingle());
    }

    public static boolean isValid(String s) {
        return parse(s) != null;
    }


    public static ColorHolder parse(String s) {
        String[] split = s.split("@");
        if (split.length == 2) {
            Block block;
            try {
                ResourceLocation parse = new ResourceLocation(split[0]);
                block = BuiltInRegistries.BLOCK.get(parse);
                if (block == Blocks.AIR) return null;
            } catch (Exception e) {
                EclipticSeasons.logger(e);
                return null;
            }
            String[] split1 = split[1].split(",");

            int totalValues = split1.length;
            int inputSize = totalValues - 2;

            if (inputSize <= 0 || 24 % inputSize != 0) {
                EclipticSeasons.logger("Invalid data stride. Input size " + inputSize + " cannot cover 24 solar terms.");
                return null;
            }

            int baseColor = 0;
            try {
                DynamicLeaveColor baseLeaveInstance = createInstance(split1[totalValues - 1]);
                if (baseLeaveInstance == null) return null;
                baseColor = baseLeaveInstance.getColor();
            } catch (NumberFormatException e) {
                EclipticSeasons.logger(e);
                return null;
            }

            LeaveColor[] leaveColors = new LeaveColor[25];

            DynamicLeaveColor lastColorInstance = createInstance(split1[inputSize]);
            if (lastColorInstance == null) return null;
            leaveColors[24] = lastColorInstance;

            int ratio = 24 / inputSize;
            for (int i = 0; i < inputSize; i++) {
                DynamicLeaveColor colorInstance = createInstance(split1[i]);
                if (colorInstance == null) return null;
                for (int j = 0; j < ratio; j++) {
                    leaveColors[i * ratio + j] = colorInstance;
                }
            }
            return new ColorHolder(block, baseColor, leaveColors);
        }
        return null;
    }

    private static DynamicLeaveColor createInstance(String string) {
        String[] split2 = string.split("\\|");
        try {
            if (split2.length > 0) {
                float mix = split2.length > 1 ? Float.parseFloat(split2[1]) : 1;
                boolean isSColor = split2[0].startsWith("#");
                ColorMode.Instance instance = new ColorMode(
                        isSColor ? Optional.empty() : Optional.of(Integer.parseInt(split2[0])),
                        mix == 1 ? Optional.empty() : Optional.of(mix),
                        !isSColor ? Optional.empty() : Optional.of(split2[0])
                ).toInstance();
                return new DynamicLeaveColor(instance);
            }
        } catch (NumberFormatException e) {
            EclipticSeasons.logger(e);
            return null;
        }
        return null;
    }

    public record ColorHolder(Block block, int base, LeaveColor[] values) {
    }

    public record DynamicLeaveColor(ColorMode.Instance mode) implements LeaveColor {
        @Override
        public int getColor() {
            return mode.value();
        }

        @Override
        public float getMix() {
            return mode.mix();
        }
    }
}
