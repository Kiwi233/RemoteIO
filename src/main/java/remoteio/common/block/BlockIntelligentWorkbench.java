package remoteio.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import remoteio.common.RemoteIO;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.core.handler.GuiHandler;
import remoteio.common.core.helper.InventoryHelper;
import remoteio.common.tile.TileIntelligentWorkbench;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class BlockIntelligentWorkbench extends BlockContainer {

    public BlockIntelligentWorkbench() {
        super(Material.IRON);

        setHardness(5F);
        setResistance(5F);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        player.openGui(RemoteIO.instance, GuiHandler.GUI_INTELLIGENT_WORKBENCH, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            TileIntelligentWorkbench tile = (TileIntelligentWorkbench) world.getTileEntity(pos);

            if (tile != null) {
                InventoryHelper.dropContents(tile.craftMatrix, world, pos);
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileIntelligentWorkbench();
    }
}