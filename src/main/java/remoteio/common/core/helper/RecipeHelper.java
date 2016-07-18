package remoteio.common.core.helper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @author dmillerw
 */
public class RecipeHelper {

    public static void addOreRecipe(ItemStack output, Object... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            ItemStack stack = null;
            if (inputs[i] instanceof Block) {
                stack = new ItemStack((Block) inputs[i]);
            } else if (inputs[i] instanceof Item) {
                stack = new ItemStack((Item) inputs[i]);
            } else if (inputs[i] instanceof ItemStack) {
                stack = (ItemStack) inputs[i];
            }

            String tag = OreHelper.getOreTag(stack);

            if (!tag.isEmpty()) {
                inputs[i] = tag;
            }
        }

        GameRegistry.addRecipe(new ShapedOreRecipe(output, inputs));
    }

    public static void addDependentOreRecipe(String modId, ItemStack output, Object... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            ItemStack stack = null;
            if (inputs[i] instanceof Block) {
                stack = new ItemStack((Block) inputs[i]);
            } else if (inputs[i] instanceof Item) {
                stack = new ItemStack((Item) inputs[i]);
            } else if (inputs[i] instanceof ItemStack) {
                stack = (ItemStack) inputs[i];
            }

            String tag = OreHelper.getOreTag(stack);

            if (!tag.isEmpty()) {
                inputs[i] = tag;
            }
        }

        if (ModHelper.isModLoaded(modId)) {
            GameRegistry.addRecipe(new ShapedOreRecipe(output, inputs));
        }
    }

    public static void addDependentRecipe(String modId, ItemStack output, Object... inputs) {
        if (ModHelper.isModLoaded(modId)) {
            GameRegistry.addShapedRecipe(output, inputs);
        }
    }
}
