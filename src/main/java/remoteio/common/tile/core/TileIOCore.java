package remoteio.common.tile.core;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import remoteio.common.core.UpgradeType;
import remoteio.common.core.helper.InventoryHelper;
import remoteio.common.inventory.InventoryItem;
import remoteio.common.inventory.InventoryNBT;
import remoteio.common.lib.ModItems;
import remoteio.common.lib.VisualState;

/**
 * @author dmillerw
 */
public abstract class TileIOCore extends TileCore implements InventoryNBT.IInventoryCallback {

    public VisualState visualState = VisualState.INACTIVE;
    public ItemStack simpleCamo;

    public InventoryNBT transferChips = new InventoryNBT(this, 9, 1);
    public InventoryNBT upgradeChips = new InventoryNBT(this, 9, 1);

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        transferChips.writeToNBT("TransferChips", nbt);
        upgradeChips.writeToNBT("UpgradeChips", nbt);

        if (simpleCamo != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            simpleCamo.writeToNBT(nbtTagCompound);
            nbt.setTag("simple", nbtTagCompound);
        }

        nbt.setByte("state", (byte) visualState.ordinal());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        transferChips.readFromNBT("TransferChips", nbt);
        upgradeChips.readFromNBT("UpgradeChips", nbt);

        if (nbt.hasKey("simple")) {
            simpleCamo = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("simple"));
        } else {
            simpleCamo = null;
        }

        visualState = VisualState.values()[nbt.getByte("state")];
    }

    @Override
    public void onClientUpdate(NBTTagCompound nbt) {
        if (nbt.hasKey("state")) {
            visualState = VisualState.values()[nbt.getByte("state")];
        }

        if (nbt.hasKey("simple")) {
            simpleCamo = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("simple"));
        } else {
            simpleCamo = null;
        }
    }

    public boolean hasTransferChip(int type) {
        return InventoryHelper.containsStack(transferChips, new ItemStack(ModItems.transferChip, 1, type), true, false);
    }

    public boolean hasUpgradeChip(int type) {
        return InventoryHelper.containsStack(upgradeChips, new ItemStack(ModItems.upgradeChip, 1, type), true, false);
    }

    public void updateSimpleCamouflage() {
        for (ItemStack stack1 : InventoryHelper.toArray(upgradeChips)) {
            if (stack1 != null && stack1.getItemDamage() == UpgradeType.SIMPLE_CAMO) {
                ItemStack stack = new InventoryItem(stack1, 1).getStackInSlot(0);
                if (stack != null) {
                    this.simpleCamo = stack;
                    sendSimpleCamouflage(stack);
                    break;
                }
            }
        }
    }

    public void sendSimpleCamouflage(ItemStack stack) {
        NBTTagCompound nbt = new NBTTagCompound();
        if (stack != null) {
            NBTTagCompound tag = new NBTTagCompound();
            stack.writeToNBT(tag);
            nbt.setTag("simple", tag);
        } else {
            nbt.setBoolean("simple_null", true);
        }
        sendClientUpdate(nbt);
    }

	/* BEGIN CLIENT UPDATE METHODS
     * 'update' methods are used to calculate what should be sent to the client
	 * 'send' methods actually send the data to the client, and take a single parameter
	 *  that is the data to be sent
	 *
	 *  Methods pertaining to the same data are lumped together */

    public abstract VisualState calculateVisualState();

    /**
     * This method kind of ignores the rules set above, as different blocks have different rules regarding visual state
     * so it calls an abstract method to determine the state, then updates as normal
     */
    public void updateVisualState() {
        VisualState state = calculateVisualState();
        this.visualState = state;
        sendVisualState(state);
    }

    public void sendVisualState(VisualState visualState) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("state", (byte) visualState.ordinal());
        sendClientUpdate(nbt);

        if (visualState == VisualState.CAMOUFLAGE_SIMPLE || visualState == VisualState.CAMOUFLAGE_BOTH) {
            updateSimpleCamouflage();
        }
    }

	/* END CLIENT UPDATE METHODS */
}