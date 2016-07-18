package remoteio.common.core.helper.mod;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import remoteio.common.lib.DependencyInfo;

/**
 * @author dmillerw
 */
public class IC2Helper {

    public static void loadEnergyTile(TileEntity tileEntity) {
        if (Loader.isModLoaded(DependencyInfo.ModIds.IC2)) {
            internalLoadEnergyTile(tileEntity);
        }
    }

    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    private static void internalLoadEnergyTile(TileEntity tileEntity) {
        if (tileEntity instanceof IEnergyTile)
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) tileEntity));
    }

    public static void unloadEnergyTile(TileEntity tileEntity) {
        if (Loader.isModLoaded(DependencyInfo.ModIds.IC2)) {
            internalUnloadEnergyTile(tileEntity);
        }
    }

    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    private static void internalUnloadEnergyTile(TileEntity tileEntity) {
        if (tileEntity instanceof IEnergyTile)
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) tileEntity));
    }
}
