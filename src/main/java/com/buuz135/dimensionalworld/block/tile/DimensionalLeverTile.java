package com.buuz135.dimensionalworld.block.tile;

import com.buuz135.dimensionalworld.DimensionalWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class DimensionalLeverTile extends TileEntity {

    private BlockPos destination;
    private ResourceLocation destinationType;

    public DimensionalLeverTile() {
        super(DimensionalWorld.TYPE);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        destination = null;
        destinationType = null;
        if (compound.contains("DestX")) destination = new BlockPos(compound.getInt("DestX"), compound.getInt("DestY"), compound.getInt("DestZ"));
        if (compound.contains("DestType")) destinationType = new ResourceLocation(compound.getString("DestType"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (destination != null){
            compound.putInt("DestX", destination.getX());
            compound.putInt("DestY", destination.getY());
            compound.putInt("DestZ", destination.getZ());
        }
        if (destinationType != null){
            compound.putString("DestType", destinationType.toString());
        }
        return super.write(compound);
    }

    public BlockPos getDestination() {
        return destination;
    }

    public void setDestination(BlockPos destination) {
        this.destination = destination;
    }

    public ResourceLocation getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(ResourceLocation destinationType) {
        this.destinationType = destinationType;
    }
}
