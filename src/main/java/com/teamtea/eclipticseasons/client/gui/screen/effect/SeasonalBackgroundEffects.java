package com.teamtea.eclipticseasons.client.gui.screen.effect;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class SeasonalBackgroundEffects {
    protected static final int[] PETAL_COLORS = {
            0xFFD990A8, 0xFFF1B6C5, 0xFFFFD5DF, 0xFFE895B4
    };
    protected static final int[] GLINT_COLORS = {
            0xFFFFE59A, 0xFFFFF0B8, 0xFFFFD66B
    };
    protected static final int[] LEAF_COLORS = {
            0xFFD86436, 0xFFE88B3D, 0xFFC94D32, 0xFFEAAF4E, 0xFFB85C35
    };
    protected static final int[] SNOW_COLORS = {
            0xFFDDEEFF, 0xFFF2F7FF, 0xFFFFFFFF
    };

    protected final Season season;
    protected final RandomSource random = RandomSource.create();
    protected final List<Particle> particles = new ArrayList<>();

    protected int width;
    protected int height;
    protected float time;
    protected long lastFrameTime;

    public SeasonalBackgroundEffects(Season season) {
        this.season = season;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        time = 0;
        lastFrameTime = System.nanoTime();
        particles.clear();

        int area = width * height;
        int count = switch (season) {
            case SPRING -> Mth.clamp(area / 14_000, 16, 44);
            case SUMMER -> Mth.clamp(area / 20_000, 10, 28);
            case AUTUMN -> Mth.clamp(area / 12_000, 22, 52);
            case WINTER -> Mth.clamp(area / 8_000, 28, 80);
            default -> 0;
        };

        for (int i = 0; i < count; i++) {
            Particle particle = createParticle();
            init(particle, true);
            particles.add(particle);
        }
    }

    public void render(GuiGraphics graphics) {
        long now = System.nanoTime();
        float delta = Math.min((now - lastFrameTime) / 1_000_000_000F, 0.1F);
        lastFrameTime = now;
        time += delta;

        update(delta);

        for (Particle particle : particles) {
            switch (season) {
                case SPRING -> renderPetal(graphics, particle);
                case SUMMER -> renderGlint(graphics, particle);
                case AUTUMN -> renderLeaf(graphics, particle);
                case WINTER -> renderSnow(graphics, particle);
            }
        }
    }

    protected Particle createParticle() {
        return new Particle();
    }

    protected void update(float delta) {
        for (Particle particle : particles) {
            particle.age += delta;

            switch (season) {
                case SPRING -> updatePetal(particle, delta);
                case SUMMER -> updateGlint(particle, delta);
                case AUTUMN -> updateLeaf(particle, delta);
                case WINTER -> updateSnow(particle, delta);
            }

            if (expired(particle)) {
                init(particle, false);
            }
        }
    }

    protected boolean expired(Particle particle) {
        return particle.age >= particle.lifetime
                || particle.y > height + 6
                || particle.x < -8
                || particle.x > width + 8;
    }

    protected void init(Particle particle, boolean anywhere) {
        switch (season) {
            case SPRING -> initPetal(particle, anywhere);
            case SUMMER -> initGlint(particle, anywhere);
            case AUTUMN -> initLeaf(particle, anywhere);
            case WINTER -> initSnow(particle, anywhere);
        }
    }

    protected void initPetal(Particle particle, boolean anywhere) {
        particle.x = random.nextFloat() * width;
        particle.y = anywhere ? random.nextFloat() * height : -3 - random.nextFloat() * 16;
        particle.vx = 1.2F + random.nextFloat() * 2.4F;
        particle.vy = 2F + random.nextFloat() * 2.8F;
        particle.phase = random.nextFloat() * Mth.TWO_PI;
        particle.age = 0;
        particle.lifetime = 24F + random.nextFloat() * 16F;
        particle.color = PETAL_COLORS[random.nextInt(PETAL_COLORS.length)];
        particle.variant = random.nextInt(6);
    }

    protected void initGlint(Particle particle, boolean anywhere) {
        particle.x = 8 + random.nextFloat() * Math.max(1, width - 16);
        particle.y = 8 + random.nextFloat() * Math.max(1, height - 16);
        particle.vx = 0;
        particle.vy = 0;
        particle.phase = random.nextFloat() * Mth.TWO_PI;
        particle.lifetime = 4F + random.nextFloat() * 5F;
        particle.age = anywhere ? random.nextFloat() * particle.lifetime : 0;
        particle.color = GLINT_COLORS[random.nextInt(GLINT_COLORS.length)];
        particle.variant = random.nextInt(4);
    }

    protected void initLeaf(Particle particle, boolean anywhere) {
        particle.x = random.nextFloat() * width;
        particle.y = anywhere ? random.nextFloat() * height : -3 - random.nextFloat() * 16;
        particle.vx = -1.6F + random.nextFloat() * 3.2F;
        particle.vy = 5.5F + random.nextFloat() * 5.5F;
        particle.phase = random.nextFloat() * Mth.TWO_PI;
        particle.age = 0;
        particle.lifetime = 24F + random.nextFloat() * 16F;
        particle.color = LEAF_COLORS[random.nextInt(LEAF_COLORS.length)];
        particle.variant = random.nextInt(8);
    }

    protected void initSnow(Particle particle, boolean anywhere) {
        particle.x = random.nextFloat() * width;
        particle.y = anywhere ? random.nextFloat() * height : -2 - random.nextFloat() * 14;
        particle.vx = -0.4F + random.nextFloat() * 0.8F;
        particle.vy = 2.8F + random.nextFloat() * 5F;
        particle.phase = random.nextFloat() * Mth.TWO_PI;
        particle.age = 0;
        particle.lifetime = 30F + random.nextFloat() * 20F;
        particle.color = SNOW_COLORS[random.nextInt(SNOW_COLORS.length)];
        particle.variant = random.nextInt(5);
    }

    protected void updatePetal(Particle particle, float delta) {
        particle.x += particle.vx * delta;
        particle.y += particle.vy * delta;
    }

    protected void updateGlint(Particle particle, float delta) {
    }

    protected void updateLeaf(Particle particle, float delta) {
        float sway = Mth.sin(particle.phase + time * 0.85F) * 2F;
        particle.x += (particle.vx + sway) * delta;
        particle.y += particle.vy * delta;
    }

    protected void updateSnow(Particle particle, float delta) {
        float sway = Mth.sin(particle.phase + time * 0.55F) * 0.7F;
        particle.x += (particle.vx + sway) * delta;
        particle.y += particle.vy * delta;
    }

    protected void renderPetal(GuiGraphics graphics, Particle particle) {
        graphics.pose().pushPose();
        graphics.pose().translate(pixelPosition(particle.x), pixelPosition(particle.y), 0);

        int color = fade(particle, 0.72F);
        float opacity = opacity(color);
        int light = alpha(lighten(particle.color, 1.13F), opacity);
        int shade = alpha(darken(particle.color, 0.86F), opacity);

        switch (particle.variant) {
            case 0 -> {
                pixel(graphics, 0, 0, light);
                pixel(graphics, 1, 0, color);
                pixel(graphics, 2, 1, shade);
            }
            case 1 -> {
                pixel(graphics, 1, 0, light);
                pixel(graphics, 0, 1, color);
                pixel(graphics, 1, 1, shade);
            }
            case 2 -> {
                pixel(graphics, 0, 0, shade);
                pixel(graphics, 0, 1, color);
                pixel(graphics, 1, 2, light);
            }
            case 3 -> {
                pixel(graphics, 2, 0, light);
                pixel(graphics, 1, 1, color);
                pixel(graphics, 0, 1, shade);
            }
            case 4 -> {
                pixel(graphics, 0, 0, light);
                pixel(graphics, 1, 0, color);
                pixel(graphics, 0, 1, shade);
                pixel(graphics, 1, 1, color);
            }
            default -> {
                pixel(graphics, 1, 0, light);
                pixel(graphics, 0, 1, color);
                pixel(graphics, 1, 1, color);
                pixel(graphics, 2, 1, shade);
            }
        }

        graphics.pose().popPose();
    }

    protected void renderGlint(GuiGraphics graphics, Particle particle) {
        float brightness = Mth.sin(particle.age / particle.lifetime * Mth.PI);
        if (brightness < 0.18F) {
            return;
        }

        graphics.pose().pushPose();
        graphics.pose().translate(pixelPosition(particle.x), pixelPosition(particle.y), 0);

        int core = alpha(lighten(particle.color, 1.08F), brightness * 0.78F);
        int ray = alpha(particle.color, brightness * 0.44F);

        pixel(graphics, 0, 0, core);

        if (brightness > 0.68F) {
            switch (particle.variant) {
                case 0 -> {
                    pixel(graphics, -1, 0, ray);
                    pixel(graphics, 1, 0, ray);
                }
                case 1 -> {
                    pixel(graphics, 0, -1, ray);
                    pixel(graphics, 0, 1, ray);
                }
                case 2 -> {
                    pixel(graphics, -1, 0, ray);
                    pixel(graphics, 1, 0, ray);
                    pixel(graphics, 0, -1, ray);
                    pixel(graphics, 0, 1, ray);
                }
                default -> {
                    pixel(graphics, -1, -1, ray);
                    pixel(graphics, 1, 1, ray);
                }
            }
        }

        graphics.pose().popPose();
    }

    protected void renderLeaf(GuiGraphics graphics, Particle particle) {
        graphics.pose().pushPose();
        graphics.pose().translate(pixelPosition(particle.x), pixelPosition(particle.y), 0);

        int color = fade(particle, 0.7F);
        float opacity = opacity(color);
        int light = alpha(lighten(particle.color, 1.12F), opacity);
        int dark = alpha(darken(particle.color, 0.72F), opacity);
        int stem = alpha(darken(particle.color, 0.48F), opacity * 0.88F);

        switch (particle.variant) {
            // 向右的细长叶片
            case 0 -> {
                pixel(graphics, -1, 1, stem);
                pixel(graphics, 0, 0, light);
                pixel(graphics, 0, 1, color);
                pixel(graphics, 1, 0, color);
                pixel(graphics, 1, 1, dark);
                pixel(graphics, 2, 1, color);
            }
            // 向左的细长叶片
            case 1 -> {
                pixel(graphics, 2, 1, stem);
                pixel(graphics, 1, 0, light);
                pixel(graphics, 1, 1, color);
                pixel(graphics, 0, 0, color);
                pixel(graphics, 0, 1, dark);
                pixel(graphics, -1, 1, color);
            }
            // 右下方向
            case 2 -> {
                pixel(graphics, 0, -1, stem);
                pixel(graphics, 0, 0, light);
                pixel(graphics, 1, 0, color);
                pixel(graphics, 0, 1, color);
                pixel(graphics, 1, 1, dark);
                pixel(graphics, 1, 2, color);
            }
            // 左下方向
            case 3 -> {
                pixel(graphics, 1, -1, stem);
                pixel(graphics, 1, 0, light);
                pixel(graphics, 0, 0, color);
                pixel(graphics, 1, 1, color);
                pixel(graphics, 0, 1, dark);
                pixel(graphics, 0, 2, color);
            }
            // 小而卷曲的叶片
            case 4 -> {
                pixel(graphics, -1, 0, stem);
                pixel(graphics, 0, 0, dark);
                pixel(graphics, 0, -1, light);
                pixel(graphics, 1, 0, color);
                pixel(graphics, 1, 1, dark);
            }
            case 5 -> {
                pixel(graphics, 1, -1, stem);
                pixel(graphics, 1, 0, dark);
                pixel(graphics, 0, 0, color);
                pixel(graphics, 0, 1, light);
                pixel(graphics, -1, 1, dark);
            }
            // 近乎侧面的窄叶
            case 6 -> {
                pixel(graphics, -1, 0, stem);
                pixel(graphics, 0, 0, dark);
                pixel(graphics, 1, 0, color);
                pixel(graphics, 2, 0, light);
                pixel(graphics, 1, 1, dark);
            }
            default -> {
                pixel(graphics, 0, -1, stem);
                pixel(graphics, 0, 0, dark);
                pixel(graphics, 0, 1, color);
                pixel(graphics, 0, 2, light);
                pixel(graphics, 1, 1, dark);
            }
        }

        graphics.pose().popPose();
    }

    protected void renderSnow(GuiGraphics graphics, Particle particle) {
        graphics.pose().pushPose();
        graphics.pose().translate(pixelPosition(particle.x), pixelPosition(particle.y), 0);

        int color = fade(particle, 0.76F);
        float opacity = opacity(color);
        int light = alpha(lighten(particle.color, 1.06F), opacity);
        int soft = alpha(particle.color, opacity * 0.62F);

        switch (particle.variant) {
            case 0 -> pixel(graphics, 0, 0, light);
            case 1 -> {
                pixel(graphics, 0, 0, light);
                pixel(graphics, 1, 0, soft);
            }
            case 2 -> {
                pixel(graphics, 0, 0, light);
                pixel(graphics, 0, 1, soft);
            }
            case 3 -> {
                pixel(graphics, 0, 0, light);
                pixel(graphics, -1, 0, soft);
                pixel(graphics, 1, 0, soft);
            }
            default -> {
                pixel(graphics, 0, 0, light);
                pixel(graphics, 1, 0, soft);
                pixel(graphics, 0, 1, soft);
            }
        }

        graphics.pose().popPose();
    }

    protected int fade(Particle particle, float opacity) {
        float edge = Math.min(
                particle.age / 0.8F,
                (particle.lifetime - particle.age) / 0.8F
        );
        return alpha(particle.color, opacity * Mth.clamp(edge, 0F, 1F));
    }

    protected static float pixelPosition(float value) {
        return Math.round(value * 2F) * 0.5F;
    }

    protected static float opacity(int color) {
        return (color >>> 24) / 255F;
    }

    protected static int alpha(int color, float opacity) {
        return Mth.clamp((int) (opacity * 255), 0, 255) << 24
                | color & 0xFFFFFF;
    }

    protected static int lighten(int color, float factor) {
        int r = Math.min(255, (int) ((color >> 16 & 0xFF) * factor));
        int g = Math.min(255, (int) ((color >> 8 & 0xFF) * factor));
        int b = Math.min(255, (int) ((color & 0xFF) * factor));
        return color & 0xFF000000 | r << 16 | g << 8 | b;
    }

    protected static int darken(int color, float factor) {
        int r = (int) ((color >> 16 & 0xFF) * factor);
        int g = (int) ((color >> 8 & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return color & 0xFF000000 | r << 16 | g << 8 | b;
    }

    protected static void pixel(
            GuiGraphics graphics,
            int x,
            int y,
            int color
    ) {
        graphics.fill(x, y, x + 1, y + 1, color);
    }

    protected static class Particle {
        public float x, y;
        public float vx, vy;
        public float phase;
        public float age, lifetime;
        public int color, variant;
    }
}