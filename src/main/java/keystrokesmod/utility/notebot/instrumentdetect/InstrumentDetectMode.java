/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package keystrokesmod.utility.notebot.instrumentdetect;

import lombok.Getter;
import net.minecraft.block.BlockNote;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;

@Getter
public enum InstrumentDetectMode {
    BlockState((InstrumentDetectMode::fromBlock)),
    BelowBlock(((noteBlock, blockPos) -> {
        assert Minecraft.getMinecraft().theWorld != null;
        return fromBlock(Minecraft.getMinecraft().theWorld.getBlockState(blockPos.down()), blockPos.down());
    }));

    private static String fromBlock(@NotNull BlockState blockState, BlockPos blockPos) {
        return fromBlock(blockState.getBaseState(), blockPos);
    }

    private static String fromBlock(@NotNull IBlockState noteBlock, BlockPos blockPos) {
        return ((BlockNote) noteBlock.getBlock()).getInstrument(noteBlock.getBlock().getMetaFromState(noteBlock) & 7);
    }

    private final InstrumentDetectFunction instrumentDetectFunction;

    InstrumentDetectMode(InstrumentDetectFunction instrumentDetectFunction) {
        this.instrumentDetectFunction = instrumentDetectFunction;
    }

}
