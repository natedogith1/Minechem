package minechem.item.bucket;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import minechem.Minechem;
import minechem.fluid.MinechemFluidBlock;
import minechem.item.MinechemChemicalType;
import minechem.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import java.util.HashMap;
import java.util.Map;

public class MinechemBucketHandler
{
    private static MinechemBucketHandler instance;
    public Map<MinechemFluidBlock, Item> buckets = new HashMap<MinechemFluidBlock, Item>();

    public static MinechemBucketHandler getInstance()
    {
        if (instance == null)
        {
            instance = new MinechemBucketHandler();
        }
        return instance;
    }

    private MinechemBucketHandler()
    {

    }

    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
        ItemStack result = fillCustomBucket(event.world, event.target);

        if (result == null) {
            return;
        }

        event.result = result;
        event.setResult(Event.Result.ALLOW);
    }

    private ItemStack fillCustomBucket(World world, MovingObjectPosition pos) {
        Block block = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);

        Item bucket = buckets.get(block);

        if (bucket != null && world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ) == 0) {
            world.setBlockToAir(pos.blockX, pos.blockY, pos.blockZ);
            return new ItemStack(bucket);
        } else {
            return null;
        }
    }

    public void registerCustomMinechemBucket(MinechemFluidBlock block,MinechemChemicalType type, String prefix)
    {
        if (buckets.get(block) != null) return;

        MinechemBucketItem bucket = new MinechemBucketItem(block, block.getFluid(),type);
        GameRegistry.registerItem(bucket, Reference.ID + "Bucket." + prefix + block.getFluid().getName());
        FluidContainerRegistry.registerFluidContainer(block.getFluid(), new ItemStack(bucket), new ItemStack(Items.bucket));
        GameRegistry.addRecipe(new MinechemBucketRecipe(bucket, type));
        buckets.put(block, bucket);
        Minechem.PROXY.onAddBucket(bucket);
    }

}
