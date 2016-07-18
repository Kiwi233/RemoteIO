package remoteio.common.block.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import remoteio.common.RemoteIO;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.core.helper.InventoryHelper;
import remoteio.common.core.helper.mod.ToolHelper;
import remoteio.common.tile.TileRemoteInterface;
import remoteio.common.tile.core.TileIOCore;

/**
 * @author dmillerw
 */
public abstract class BlockIOCore extends BlockContainer {

    public BlockIOCore() {
        super(Material.IRON);
        setHardness(5F);
        setResistance(5F);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
        TileIOCore tile = (TileIOCore) world.getTileEntity(x, y, z);

        if (player.getCurrentEquippedItem() != null) {
            ItemStack itemStack = player.getCurrentEquippedItem();

            if (ToolHelper.isTool(itemStack, player, world, x, y, z)) {
                if (!world.isRemote) {
                    if (!player.isSneaking()) {
                        if (tile instanceof TileRemoteInterface)
                            ((TileRemoteInterface) tile).updateRotation(1);
                    } else {
                        if (!(tile instanceof TileRemoteInterface) || !((TileRemoteInterface) tile).locked)
                            player.openGui(RemoteIO.instance, getGuiID(), world, x, y, z);
                    }
                }
            }
        }
        return false;
    }

    public abstract int getGuiID();

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            TileIOCore tile = (TileIOCore) world.getTileEntity(x, y, z);

            if (tile != null) {
                InventoryHelper.dropContents(tile.upgradeChips, world, x, y, z);
                InventoryHelper.dropContents(tile.transferChips, world, x, y, z);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileIOCore tile = (TileIOCore) world.getTileEntity(x, y, z);

        if (tile != null) {
            if (!tile.visualState.isCamouflage()) {
                return icons[tile.visualState.ordinal()];
            } else if (tile.simpleCamo != null) {
                return Block.getBlockFromItem(tile.simpleCamo.getItem()).getIcon(side, tile.simpleCamo.getItemDamage());
            }
        }

        return icons[0];
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return getTileEntity();
    }

    public abstract TileIOCore getTileEntity();
}