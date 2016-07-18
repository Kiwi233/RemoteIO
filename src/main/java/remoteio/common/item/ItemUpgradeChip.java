package remoteio.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import remoteio.common.RemoteIO;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.core.UpgradeType;
import remoteio.common.core.handler.GuiHandler;
import remoteio.common.tile.TileRemoteInterface;
import remoteio.common.tile.core.TileIOCore;

/**
 * @author dmillerw
 */
public class ItemUpgradeChip extends ItemSelectiveMeta {

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
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile != null && tile instanceof TileIOCore) {
                TileIOCore io = (TileIOCore) tile;

                if (!(io instanceof TileRemoteInterface) || !((TileRemoteInterface) io).locked) {
                    ItemStack chip = stack.copy();
                    chip.stackSize = 1;

                    if (TileEntityHopper.putStackInInventoryAllSlots(io.upgradeChips, chip, EnumFacing.DOWN) == null) {
                        io.callback(io.upgradeChips);
                        if (stack.stackSize == 1) {
                            player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
                        } else {
                            ItemStack stack1 = stack.copy();
                            stack1.stackSize = stack.stackSize - 1;
                            player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack1);
                        }
                        return EnumActionResult.SUCCESS;
                    }

                    if (io instanceof TileRemoteInterface)
                        ((TileRemoteInterface) io).updateRemotePosition();
                    io.updateVisualState();
                    io.markForUpdate();
                }
            }
        }

        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                switch (stack.getItemDamage()) {
                    case UpgradeType.SIMPLE_CAMO:
                        player.openGui(RemoteIO.instance, GuiHandler.GUI_SIMPLE_CAMO, world, 0, 0, 0);
                        break;
                }
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    /*@Override //TODO Move to new system
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (pass == 1) {
            return names.get(stack.getItemDamage()).hashCode();
        }
        return 0xFFFFFF;
    }*/
}