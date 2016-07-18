package remoteio.common.inventory.container.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import remoteio.common.inventory.InventoryItem;

/**
 * @author dmillerw
 */
public class ContainerItem extends Container {

    protected InventoryItem inventory;

    private int activeSlot = -1;

    public ContainerItem(InventoryItem inventory, int activeSlot) {
        this.inventory = inventory;
        this.activeSlot = activeSlot;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (slotId == activeSlot) return null;
        return super.slotClick(slotId, dragType, clickType, player);
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int id) {
        ItemStack stack = null;
        Slot slot = this.inventorySlots.get(id);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            stack = itemstack1.copy();

            if (id < 9) {
                if (!this.mergeItemStack(itemstack1, inventory.getSizeInventory(), 36 + inventory.getSizeInventory(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, inventory.getSizeInventory(), false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == stack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }
        return stack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        inventory.writeToNBT();

        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, inventory.getStack());
        player.inventory.markDirty();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}