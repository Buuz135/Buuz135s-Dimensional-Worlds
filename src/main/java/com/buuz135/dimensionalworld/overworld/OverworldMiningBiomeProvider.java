package com.buuz135.dimensionalworld.overworld;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.*;

public class OverworldMiningBiomeProvider extends BiomeProvider {

    protected OverworldMiningBiomeProvider() {
        super(Collections.singleton(Biomes.PLAINS));
        biome = Biomes.PLAINS;
    }
    private final Biome biome;
    private static final List<Biome> SPAWN = Collections.singletonList(Biomes.PLAINS);

    @Override
    public List<Biome> getBiomesToSpawnIn() {
        return SPAWN;
    }

    @Override
    public Set<Biome> func_225530_a_(int p_225530_1_, int p_225530_2_, int p_225530_3_, int p_225530_4_) {
        return Collections.singleton(biome);
    }

    @Override
    public boolean hasStructure(Structure<?> structure) {
        return false;
    }

    @Override
    public Set<BlockState> getSurfaceBlocks() {
        if (topBlocksCache.isEmpty()) {
            topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
        }
        return topBlocksCache;
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        return biome;
    }

}
