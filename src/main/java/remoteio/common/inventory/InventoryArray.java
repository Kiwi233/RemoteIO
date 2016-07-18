package remoteio.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class InventoryArray extends InventoryBase implements IInventory {

    public InventoryArray(ItemStack[] inv) {
        this.inv = inv;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }
}