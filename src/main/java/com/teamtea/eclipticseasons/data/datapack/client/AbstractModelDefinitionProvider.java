package com.teamtea.eclipticseasons.data.datapack.client;

import com.google.common.base.Preconditions;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.data.datapack.client.base.ESClientDataMapProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractModelDefinitionProvider extends ESClientDataMapProvider<ESModelLoadedJson> {

    public AbstractModelDefinitionProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_MODEL_DEFINITION, ESModelLoadedJson.CODEC);
        this.blockModels = new InnerESBlockModelProvider(output, modid, helper);
    }

    private final InnerESBlockModelProvider blockModels;

    protected InnerESBlockModelProvider models() {
        return blockModels;
    }

    @Override
    protected abstract void gather(HolderLookup.Provider provider) ;

    @Override
    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        return CompletableFuture.allOf(
                super.run(output, provider),
                models().generateAll(output));
    }

    protected static class InnerESBlockModelProvider extends ModelProvider<SlefBlockModelBuilder> {

        public InnerESBlockModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
            super(output, modid, BLOCK_FOLDER, SlefBlockModelBuilder::new, existingFileHelper);
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
            return CompletableFuture.allOf();
        }

        @Override
        public @NotNull String getName() {
            return "ES Block Model : "+modid;
        }

        @Override
        protected void registerModels() {
        }

        // If set folder would not use folder
        private ResourceLocation extendWithFolder(ResourceLocation rl) {
            if (rl.getPath().contains("/")) {
                return rl;
            }
            return new ResourceLocation(rl.getNamespace(), folder + "/" + rl.getPath());
        }

        @Override
        public SlefBlockModelBuilder getBuilder(String path) {
            Preconditions.checkNotNull(path, "Path must not be null");
            ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path));
            this.existingFileHelper.trackGenerated(outputLoc, MODEL);
            return generatedModels.computeIfAbsent(outputLoc, factory);
        }

        @Override
        public CompletableFuture<?> generateAll(CachedOutput cache) {
            return super.generateAll(cache);
        }


    }

    public static class SlefBlockModelBuilder extends ModelBuilder<SlefBlockModelBuilder> {
        public SlefBlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
            super(outputLocation, existingFileHelper);
        }

        private SlefBlockModelBuilder self() {
            return this;
        }

        @Override
        public SlefBlockModelBuilder texture(String key, String texture) {
            Preconditions.checkNotNull(key, "Key must not be null");
            Preconditions.checkNotNull(texture, "Texture must not be null");
            if (texture.charAt(0) == '#') {
                this.textures.put(key, texture);
                return self();
            } else {
                ResourceLocation asLoc;
                if (texture.contains(":")) {
                    asLoc = new ResourceLocation(texture);
                } else {
                    asLoc = new ResourceLocation(getLocation().getNamespace(), texture);
                }
                return texture(key, asLoc);
            }
        }


        @Override
        public SlefBlockModelBuilder texture(String key, ResourceLocation texture) {
            Preconditions.checkNotNull(key, "Key must not be null");
            Preconditions.checkNotNull(texture, "Texture must not be null");
            // Preconditions.checkArgument(existingFileHelper.exists(texture, ModelProvider.TEXTURE),
            //         "Texture %s does not exist in any known resource pack", texture);
            this.textures.put(key, texture.toString());
            return self();
        }
    }
}
