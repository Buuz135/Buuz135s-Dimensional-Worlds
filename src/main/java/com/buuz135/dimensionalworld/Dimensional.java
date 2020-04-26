package com.buuz135.dimensionalworld;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.registries.ObjectHolder;

public class Dimensional {

    public static final ResourceLocation OVERWORLD = new ResourceLocation(DimensionalWorld.MODID, "overworld");

    @ObjectHolder(DimensionalWorld.MODID + ":overworld")
    public static ModDimension OVERWORLD_DIMENSION;

    public static DimensionType OVERWORLD_TYPE;
}
