package remoteio.common.block.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import remoteio.common.RemoteIO;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.core.helper.InventoryHelper;
import remoteio.common.core.helper.mod.ToolHelper;
import remoteio.common.lib.ModInfo;
import remoteio.common.tile.TileRemoteInterface;
import remoteio.common.tile.core.TileIOCore;

/**
 * @author dmillerw
 */
public abstract class BlockIOCore extends BlockContainer {
    @SideOnly(Side.CLIENT)
    public static IIcon[] icons;
    @SideOnly(Side.CLIENT)
    public static IIcon[] overlays;

    public BlockIOCore() {
        super(Material.iron);
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
                        if (!(tile instanceof TileRemoteInterface) || !((TileRemoteInterface)tile).locked)
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

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
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

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        icons = new IIcon[4];
        icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "inactive");
        icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "inactive_blink");
        icons[2] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "active");
        icons[3] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "active_blink");
        overlays = new IIcon[4];
        overlays[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "overlay/inactive");
        overlays[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "overlay/inactive_blink");
        overlays[2] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "overlay/active");
        overlays[3] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "overlay/active_blink");
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return getTileEntity();
    }

    public abstract TileIOCore getTileEntity();
}
