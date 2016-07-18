package remoteio.common.core.helper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModAPIManager;

/**
 * @author dmillerw
 */
public class ModHelper {

    public static boolean isModLoaded(String modId) {
        return Loader.isModLoaded(modId) || ModAPIManager.INSTANCE.hasAPI(modId);
    }

    public static ItemStack getThaumcraftItem(String id, int meta) {
        if (Loader.isModLoaded("Thaumcraft")) {
            return getItemFromStaticClass("thaumcraft.common.config.ConfigItems", id, meta);
        } else {
            return null;
        }
    }

    public static ItemStack getItemFromStaticClass(String cls, String item, int meta) {
        ItemStack stack = null;

        try {
            Object obj = Class.forName(cls).getField(item).get(null);
            if (obj instanceof Item) {
                stack = new ItemStack((Item) obj, 1, meta);
            } else if (obj instanceof ItemStack) {
                stack = (ItemStack) obj;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return stack;
    }

    public static ItemStack getItemFromClass(Object cls, String item, int meta) {
        ItemStack stack = null;

        try {
            Object obj = cls.getClass().getField(item).get(cls);
            if (obj instanceof Item) {
                stack = new ItemStack((Item) obj, 1, meta);
            } else if (obj instanceof ItemStack) {
                stack = (ItemStack) obj;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return stack;
    }
}