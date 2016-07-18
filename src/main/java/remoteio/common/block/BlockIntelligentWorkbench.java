package remoteio.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import remoteio.common.RemoteIO;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.core.handler.GuiHandler;
import remoteio.common.core.helper.InventoryHelper;
import remoteio.common.lib.ModInfo;
import remoteio.common.tile.TileIntelligentWorkbench;

/**
 * @author dmillerw
 */
public class BlockIntelligentWorkbench extends BlockContainer {

    public static IIcon blank;
    public static IIcon top;
    public static IIcon overlay;

    public BlockIntelligentWorkbench() {
        super(Material.iron);

        setHardness(5F);
        setResistance(5F);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
        player.openGui(RemoteIO.instance, GuiHandler.GUI_INTELLIGENT_WORKBENCH, world, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            TileIntelligentWorkbench tile = (TileIntelligentWorkbench) world.getTileEntity(x, y, z);

            if (tile != null) {
                InventoryHelper.dropContents(tile.craftMatrix, world, x, y, z);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        blank = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blank");
        top = register.registerIcon(ModInfo.RESOURCE_PREFIX + "workbench");
        overlay = register.registerIcon(ModInfo.RESOURCE_PREFIX + "overlay/workbench");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? top : blank;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileIntelligentWorkbench();
    }
}
