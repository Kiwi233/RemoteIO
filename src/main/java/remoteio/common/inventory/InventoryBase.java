package remoteio.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;

public class InventoryBase implements IInventory {

    protected ItemStack[] inv;

    @Override
    public int getSizeInventory() {
        return inv.length;
    }

    @Nullable
    @Override
    public ItemStack getStackInSlot(int index) {
        return inv[index];
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.inv[index] != null) {
            ItemStack stack;

            if (this.inv[index].stackSize <= count) {
                stack = this.inv[index];
                this.inv[index] = null;
                return stack;
            } else {
                stack = this.inv[index].splitStack(count);

                if (this.inv[index].stackSize == 0) {
                    this.inv[index] = null;
                }

                return stack;
            }
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (this.inv[index] != null) {
            ItemStack itemstack = this.inv[index];
            this.inv[index] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        this.inv[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.inv.length; ++i) {
            this.inv[i] = null;
        }
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(getName());
    }
}