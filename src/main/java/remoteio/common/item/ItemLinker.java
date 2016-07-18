package remoteio.common.item;

import net.minecraft.item.Item;
import remoteio.common.core.TabRemoteIO;

//TODO: Migrate Location Chip Logic to this
public final class ItemLinker
extends Item {
    public ItemLinker() {
        this.setMaxStackSize(1);
        this.setCreativeTab(TabRemoteIO.TAB);
    }
}