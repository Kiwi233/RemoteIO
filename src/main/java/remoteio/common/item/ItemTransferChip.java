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
import remoteio.common.core.TransferType;
import remoteio.common.core.handler.GuiHandler;
import remoteio.common.tile.TileRemoteInterface;
import remoteio.common.tile.core.TileIOCore;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemTransferChip extends ItemSelectiveMeta {

    public ItemTransferChip() {
        super(new int[]{
                        TransferType.MATTER_ITEM,
                        TransferType.MATTER_FLUID,
                        TransferType.MATTER_ESSENTIA,

                        TransferType.ENERGY_IC2,
                        TransferType.ENERGY_RF,

                        TransferType.NETWORK_AE,

                        TransferType.REDSTONE
                },

                new String[]{
                        "item",
                        "fluid",
                        "essentia",

                        "energy_ic2",
                        "energy_rf",

                        "network_ae",

                        "redstone"
                });

        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (stack.getItemDamage() == TransferType.ENERGY_RF) {
            if (stack.hasTagCompound()) {
                tooltip.add("Push Power: " + stack.getTagCompound().getBoolean("pushPower"));
                tooltip.add("Power Rate: " + stack.getTagCompound().getInteger("maxPushRate"));
            }
        }
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

                    if (TileEntityHopper.putStackInInventoryAllSlots(io.transferChips, chip, EnumFacing.DOWN) == null) {
                        io.callback(io.transferChips);
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
        if (world.isRemote && player.isSneaking()) {
            if (stack.getItemDamage() == TransferType.ENERGY_RF)
                player.openGui(RemoteIO.instance, GuiHandler.GUI_RF_CONFIG, world, 0, 0, 0);
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