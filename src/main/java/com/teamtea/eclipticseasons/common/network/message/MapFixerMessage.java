package com.teamtea.eclipticseasons.common.network.message;


import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public class MapFixerMessage   {
    public final List<Integer> startYList;
    public final List<BlockPos> blockPosList;

    public MapFixerMessage(List<BlockPos> blockPosList, List<Integer> startYList) {
        this.blockPosList = blockPosList;
        this.startYList = startYList;
    }

    public MapFixerMessage(FriendlyByteBuf buf) {
        startYList=new ArrayList<>();
        blockPosList=new ArrayList<>();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            startYList.add(buf.readVarInt());
        }
        size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            blockPosList.add(buf.readBlockPos());
        }
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(startYList.size());
        for (Integer i : startYList) {
            buf.writeVarInt(i);
        }
        buf.writeVarInt(blockPosList.size());
        for (BlockPos pos : blockPosList) {
            buf.writeBlockPos(pos);
        }
    }

}
