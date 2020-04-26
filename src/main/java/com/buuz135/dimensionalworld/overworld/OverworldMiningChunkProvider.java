package com.buuz135.dimensionalworld.overworld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;

public class OverworldMiningChunkProvider extends ChunkGenerator<OverworldMiningChunkProvider.Config> {


    public OverworldMiningChunkProvider(IWorld p_i49954_1_, BiomeProvider p_i49954_2_) {
        super(p_i49954_1_, p_i49954_2_, Config.get());
    }

    @Override
    public void func_225551_a_(WorldGenRegion p_225551_1_, IChunk chunk) {
        BlockState bedrock = Blocks.BEDROCK.getDefaultState();
        BlockState stone = Blocks.STONE.getDefaultState();

        BlockPos.Mutable pos = new BlockPos.Mutable();

        int x;
        int z;

        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                chunk.setBlockState(pos.add(x, 0, z), bedrock, false);
            }
        }

        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                int height = 70;
                for (int y = 1 ; y < height ; y++) {
                    chunk.setBlockState(pos.add(x, y, z), stone, false);
                }
                for (int y = height ; y < height +3 ; y++) {
                    chunk.setBlockState(pos.add(x, y, z), Blocks.DIRT.getDefaultState(), false);
                }
                chunk.setBlockState(pos.add(x, height + 3, z), Blocks.GRASS_BLOCK.getDefaultState(), false);
            }
        }
    }

    @Override
    public int getGroundHeight() {
        return world.getSeaLevel() +1;
    }

    @Override
    public void makeBase(IWorld worldIn, IChunk chunkIn) {

    }

    @Override
    public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type heightmapType) {
        return 0;
    }

    public static class Config extends GenerationSettings{

        public static Config get(){
            return new Config();
        }

    }
}
