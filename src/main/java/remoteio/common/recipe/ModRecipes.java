package remoteio.common.recipe;

import ic2.api.item.IC2Items;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import remoteio.common.core.TransferType;
import remoteio.common.core.UpgradeType;
import remoteio.common.core.helper.ModHelper;
import remoteio.common.core.helper.RecipeHelper;
import remoteio.common.lib.ModBlocks;
import remoteio.common.lib.ModItems;

/**
 * @author dmillerw
 */
public class ModRecipes {

    public static void initialize() {
        // RESERVOIR
        RecipeHelper.addOreRecipe(
                new ItemStack(ModBlocks.machine, 1, 0),
                "SGS",
                "GWG",
                "SGS",
                'S', Blocks.STONE,
                'G', Blocks.GLASS,
                'W', Items.WATER_BUCKET
        );

        // LAVA HEATER
        RecipeHelper.addOreRecipe(
                new ItemStack(ModBlocks.machine, 1, 1),
                "SIS",
                "ILI",
                "SIS",
                'S', Blocks.STONE,
                'I', Blocks.IRON_BARS,
                'L', Items.LAVA_BUCKET
        );

        // REMOTE INTERFACE
        RecipeHelper.addOreRecipe(
                new ItemStack(ModBlocks.remoteInterface),
                " E ",
                "RGR",
                "RRR",
                'E', Items.ENDER_PEARL,
                'R', Items.REDSTONE,
                'G', Blocks.GOLD_BLOCK
        );

        // LINKER
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.linker),
                " G ",
                "GEI",
                " IR",
                'G', Items.GOLD_INGOT,
                'E', Items.ENDER_PEARL,
                'I', Items.IRON_INGOT,
                'R', Items.REDSTONE
        );

        // REMOTE ACCESSOR
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.remoteAccessor),
                " I ",
                "LCL",
                " T ",
                'I', ModBlocks.remoteInterface,
                'L', ModItems.linker,
                'C', ModItems.locationChip,
                'T', ModItems.wirelessTransmitter
        );

        // SKY LIGHT
        GameRegistry.addShapedRecipe(
                new ItemStack(ModBlocks.skylight),
                "SGS",
                "GRG",
                "STS",
                'S', Blocks.STONE,
                'G', Blocks.GLASS,
                'R', Items.REDSTONE
        );

        // INTELLIGENT WORKBENCH
        GameRegistry.addShapelessRecipe(
                new ItemStack(ModBlocks.intelligentWorkbench),
                Blocks.CRAFTING_TABLE,
                ModItems.locationChip
        );

        // IO TOOL
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.ioTool),
                " I ",
                "RSI",
                "IR ",
                'I', Items.IRON_INGOT,
                'R', Items.REDSTONE,
                'S', Items.STICK
        );

/*        // Testing Recipe
        RecipeHelper.addOreRecipe(
                new ItemStack(Items.diamond_sword),
                " C ",
                " C ",
                " S ",
                'C', Blocks.cobblestone,
                'S', Items.stick
        );*/

        // PDA
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.pda),
                "IGI",
                "IRI",
                "IBI",
                'I', Items.IRON_INGOT,
                'R', Items.REDSTONE,
                'G', Blocks.GLASS,
                'B', Blocks.STONE_BUTTON
        );

        // LOCATION CHIP
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.locationChip),
                "R",
                "P",
                "G",
                'R', Items.REDSTONE,
                'P', Items.PAPER,
                'G', Items.GOLD_NUGGET
        );

        // BLANK PLATE
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.blankPlate),
                "III",
                'I', Items.IRON_INGOT
        );

        // WIRELESS TRANSMITTER
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.wirelessTransmitter),
                " E ",
                "S  ",
                "IRI",
                'E', Items.ENDER_PEARL,
                'S', Items.STICK,
                'I', Items.IRON_INGOT,
                'R', Items.REDSTONE
        );

        // TRANSFER TYPE - ITEM
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.transferChip, 1, TransferType.MATTER_ITEM),
                " B ",
                "ICI",
                'B', ModItems.blankPlate,
                'I', Blocks.CHEST,
                'C', ModItems.locationChip
        );

        // TRANSFER TYPE - FLUID
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.transferChip, 1, TransferType.MATTER_FLUID),
                " B ",
                "ICI",
                'B', ModItems.blankPlate,
                'I', Items.BUCKET,
                'C', ModItems.locationChip
        );

        // TRANSFER TYPE - ESSENTIA
        RecipeHelper.addDependentOreRecipe(
                "Thaumcraft",
                new ItemStack(ModItems.transferChip, 1, TransferType.MATTER_ESSENTIA),
                " B ",
                "ICI",
                'B', ModItems.blankPlate,
                'I', ModHelper.getThaumcraftItem("itemEssence", OreDictionary.WILDCARD_VALUE),
                'C', ModItems.locationChip
        );

        // TRANSFER TYPE - IC2
        for (ItemStack cable : getIC2Cables()) {
            RecipeHelper.addDependentOreRecipe(
                    "IC2",
                    new ItemStack(ModItems.transferChip, 1, TransferType.ENERGY_IC2),
                    " B ",
                    "ICI",
                    'B', ModItems.blankPlate,
                    'I', cable,
                    'C', ModItems.locationChip
            );
        }

        // TRANSFER TYPE - RF
        RecipeHelper.addDependentOreRecipe(
                "CoFHAPI|energy",
                new ItemStack(ModItems.transferChip, 1, TransferType.ENERGY_RF),
                " B ",
                "ICI",
                'B', ModItems.blankPlate,
                'I', Items.REDSTONE,
                'C', ModItems.locationChip
        );

        // TRANSFER TYPE - AE2 NETWORK
        /*if (Loader.isModLoaded(DependencyInfo.ModIds.AE2)) {
            Object component = AEApi.instance().blocks().blockController.block();
            if (component == null) {
                component = AEApi.instance().blocks().blockChest.block();
            }

            if (component != null) {
                RecipeHelper.addDependentRecipe(
                        DependencyInfo.ModIds.AE2,
                        new ItemStack(ModItems.transferChip, 1, TransferType.NETWORK_AE),
                        " B ",
                        " C ",
                        " I ",
                        'B', ModItems.blankPlate,
                        'I', component,
                        'C', ModItems.locationChip
                );
            }
        }*/

        // TRANSFER TYPE - REDSTONE
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.transferChip, 1, TransferType.REDSTONE),
                " B ",
                " C ",
                " I ",
                'B', ModItems.blankPlate,
                'I', Blocks.REDSTONE_BLOCK,
                'C', ModItems.locationChip
        );

        // UPGRADE TYPE - REMOTE CAMOUFLAGE
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.upgradeChip, 1, UpgradeType.REMOTE_CAMO),
                " B ",
                "ICI",
                'B', ModItems.blankPlate,
                'I', Items.ENDER_PEARL,
                'C', ModItems.locationChip
        );

        // UPGRADE TYPE - REMOTE ACCESS
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.upgradeChip, 1, UpgradeType.REMOTE_ACCESS),
                "B",
                "C",
                "R",
                'B', ModItems.blankPlate,
                'C', ModItems.locationChip,
                'R', ModItems.wirelessTransmitter
        );
    }

    private static ItemStack[] getIC2Cables() {
        if (Loader.isModLoaded("IC2")) {
            String[] cableTypes = new String[]{"copper", "insulatedCopper", "gold", "insulatedGold", "iron", "insulatedIron", "insulatedTin", "glassFiber", "tin"};
            ItemStack[] cables = new ItemStack[cableTypes.length];

            try {
                for (int i = 0; i < cableTypes.length; i++) {
                    cables[i] = IC2Items.getItem(cableTypes[i] + "CableItem");
                }
            } catch (Exception ex) {
                FMLLog.warning("Tried to get IC2 power cables, but failed! IC2 support will not be available!");
                return new ItemStack[0];
            }

            return cables;
        }

        return new ItemStack[0];
    }
}