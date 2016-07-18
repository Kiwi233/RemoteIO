package remoteio.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import remoteio.common.RemoteIO;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.core.UpgradeType;
import remoteio.common.core.handler.GuiHandler;
import remoteio.common.lib.ModInfo;
import remoteio.common.tile.TileRemoteInterface;
import remoteio.common.tile.core.TileIOCore;

/**
 * @author dmillerw
 */
public class ItemUpgradeChip
extends ItemSelectiveMeta {
    private IIcon[] icons;

    public ItemUpgradeChip() {
        super(
                new int[]{
                    UpgradeType.REMOTE_CAMO,
                    UpgradeType.REMOTE_ACCESS,
                    UpgradeType.SIMPLE_CAMO,
                },

                new String[]{
                    "remote_camo",
                    "remote_access",
                    "simple_camo"
                }
        );

        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(x, y, z);

            if (tile != null && tile instanceof TileIOCore) {
                TileIOCore io = (TileIOCore) tile;

                if (!(io instanceof TileRemoteInterface) || !((TileRemoteInterface) io).locked) {
                    ItemStack chip = stack.copy();
                    chip.stackSize = 1;

                    if (TileEntityHopper.func_145889_a(io.upgradeChips, chip, ForgeDirection.UNKNOWN.ordinal()) == null) {
                        io.callback(io.upgradeChips);
                        if (stack.stackSize == 1) {
                            player.setCurrentItemOrArmor(0, null);
                        } else {
                            ItemStack stack1 = stack.copy();
                            stack1.stackSize = stack.stackSize - 1;
                            player.setCurrentItemOrArmor(0, stack1);
                        }
                        return true;
                    }

                    if (io instanceof TileRemoteInterface)
                        ((TileRemoteInterface) io).updateRemotePosition();
                    io.updateVisualState();
                    io.markForUpdate();
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                switch (stack.getItemDamage()) {
                    case UpgradeType.SIMPLE_CAMO:
                        player.openGui(RemoteIO.instance, GuiHandler.GUI_SIMPLE_CAMO, world, 0, 0, 0);
                        break;
                }
            }
        }
        return stack;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (pass == 1) {
            return names.get(stack.getItemDamage()).hashCode();
        }
        return 0xFFFFFF;
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass == 1 ? icons[1] : icons[0];
    }

    @Override
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[2];
        icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "chip");
        icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "plate_upgrade");
    }
}
