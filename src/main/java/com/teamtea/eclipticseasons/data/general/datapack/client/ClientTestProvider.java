package com.teamtea.eclipticseasons.data.general.datapack.client;

import com.teamtea.eclipticseasons.api.data.client.model.multipart.AndConditionLike;
import com.teamtea.eclipticseasons.api.data.client.model.multipart.KeyValueConditionLike;
import com.teamtea.eclipticseasons.api.data.client.model.multipart.OrConditionLike;
import com.teamtea.eclipticseasons.api.data.client.model.multipart.SelectorLike;
import com.teamtea.eclipticseasons.api.data.client.model.variant.MultiVariantLike;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.data.api.provider.base.ESClientDataMapProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClientTestProvider extends ESClientDataMapProvider<SelectorLike> {
    public ClientTestProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_TEST, SelectorLike.CODEC);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        add("test", new SelectorLike(
                new AndConditionLike(
                        List.of(new OrConditionLike(List.of(new KeyValueConditionLike("dd", "cc"),
                                        new AndConditionLike(List.of(new KeyValueConditionLike("sa", "s"),new KeyValueConditionLike("csda", "fad"))))),
                                new KeyValueConditionLike("ee", "ff"),
                                new KeyValueConditionLike("cc", "bb"))
                )
                , new MultiVariantLike(List.of())
        ));
        add("test45", new SelectorLike(
                new AndConditionLike(
                       List.of( new KeyValueConditionLike("ee", "ff"),  new KeyValueConditionLike("cc", "bb")
                             )
                )
                , new MultiVariantLike(List.of())
        ));
        add("test2", new SelectorLike(
                new OrConditionLike(
                        List.of(
                                new KeyValueConditionLike("ee", "ff"),
                                new KeyValueConditionLike("cc", "bb"))
                )
                , new MultiVariantLike(List.of())
        ));
    }
}
