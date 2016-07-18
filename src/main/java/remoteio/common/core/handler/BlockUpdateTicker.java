package remoteio.common.core.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import remoteio.common.block.BlockSkylight;
import remoteio.common.lib.ModBlocks;

import java.util.List;
import java.util.Set;

/**
 * @author dmillerw
 */
public class BlockUpdateTicker {

    public static final int MAX_PER_TICK = 10;

    public static final class BlockUpdate {
        public final BlockPos pos;
        public final int dimension;

        public final Block block;
        public final IBlockState state;

        public BlockUpdate(BlockPos pos, int dimension, Block block, IBlockState state) {
            this.pos = pos;
            this.dimension = dimension;
            this.block = block;
            this.state = state;
        }

        public void apply(World world) {
            if (world.getBlockState(pos).getBlock() == block) {
                world.setBlockState(pos, state, 3);
            } else {
                world.setBlockState(pos, block.getDefaultState(), 3);
            }
            world.checkLightFor(EnumSkyBlock.BLOCK, pos);
            world.checkLightFor(EnumSkyBlock.SKY, pos);
            for (EnumFacing side : EnumFacing.HORIZONTALS) {
                Block block = world.getBlockState(pos.add(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ())).getBlock();
                if (block != null && block == ModBlocks.skylight) {
                    ((BlockSkylight) block).onBlockUpdate(world, x + side.offsetX, y + side.offsetY, z + side.offsetZ, ModBlocks.skylight, meta);
                }
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BlockUpdate that = (BlockUpdate) o;

            if (dimension != that.dimension) return false;
            if (pos.getX() != that.pos.getX()) return false;
            if (pos.getY() != that.pos.getY()) return false;
            if (pos.getZ() != that.pos.getZ()) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = pos.getX();
            result = 31 * result + pos.getY();
            result = 31 * result + pos.getZ();
            result = 31 * result + dimension;
            return result;
        }
    }

    public static void registerBlockUpdate(World world, BlockPos pos, Block block, IBlockState state) {
        blockUpdateSet.add(new BlockUpdate(pos, world.provider.getDimension(), block, state));
    }

    private static Set<BlockUpdate> blockUpdateSet = Sets.newConcurrentHashSet();

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        List<BlockUpdate> removalList = Lists.newArrayList();
        int updateCount = 0;
        for (BlockUpdate blockUpdate : blockUpdateSet) {
            if (updateCount >= MAX_PER_TICK) break;
            if (event.world.provider.getDimension() == blockUpdate.dimension) {
                blockUpdate.apply(event.world);
                removalList.add(blockUpdate);
                updateCount++;
            }
        }
        for (int i = 0; i < removalList.size(); i++) {
            blockUpdateSet.remove(removalList.get(i));
        }
    }
}