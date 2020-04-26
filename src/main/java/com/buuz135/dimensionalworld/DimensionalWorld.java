package com.buuz135.dimensionalworld;

import com.buuz135.dimensionalworld.block.DimensionalLever;
import com.buuz135.dimensionalworld.block.tile.DimensionalLeverTile;
import com.buuz135.dimensionalworld.overworld.OverworldMiningModDimension;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

@Mod(DimensionalWorld.MODID)
public class DimensionalWorld {

    public static final String MODID = "dimensionalworld";
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ItemGroup TAB = new ItemGroup(MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(OVERWORLD_LEVER);
        }
    };

    public static final DimensionalLever OVERWORLD_LEVER = new DimensionalLever(Dimensional.OVERWORLD);
    public static final TileEntityType<?> TYPE = TileEntityType.Builder.create(DimensionalLeverTile::new, OVERWORLD_LEVER).build(null).setRegistryName(MODID, "lever_tile");

    public DimensionalWorld() {
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> this::client);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        DimensionManager.registerOrGetDimension(Dimensional.OVERWORLD, Dimensional.OVERWORLD_DIMENSION, new PacketBuffer(Unpooled.buffer()), true);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onDimensionRegistry(final RegistryEvent.Register<ModDimension> event) {
           event.getRegistry().register(new OverworldMiningModDimension().setRegistryName(Dimensional.OVERWORLD));
        }

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            event.getRegistry().register(OVERWORLD_LEVER);
        }

        @SubscribeEvent
        public static void onTileRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            event.getRegistry().register(TYPE);
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(new BlockItem(OVERWORLD_LEVER, new Item.Properties().maxStackSize(1).group(TAB)).setRegistryName(OVERWORLD_LEVER.getRegistryName()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void client(){
        RenderTypeLookup.setRenderLayer(OVERWORLD_LEVER, RenderType.getCutoutMipped());
    }
}
