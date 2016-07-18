package remoteio.common.tile;

import cofh.api.energy.IEnergyHandler;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fml.common.Optional;
import remoteio.common.core.TransferType;
import remoteio.common.core.UpgradeType;
import remoteio.common.core.helper.PlayerHelper;
import remoteio.common.core.helper.mod.IC2Helper;
import remoteio.common.core.helper.transfer.IC2TransferHelper;
import remoteio.common.core.helper.transfer.RFTransferHelper;
import remoteio.common.inventory.InventoryArray;
import remoteio.common.item.ItemWirelessTransmitter;
import remoteio.common.lib.DependencyInfo;
import remoteio.common.lib.VisualState;
import remoteio.common.tile.core.TileIOCore;

/**
 * @author dmillerw
 */
@Optional.InterfaceList({
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IENERGYSOURCE, modid = DependencyInfo.ModIds.IC2),
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IENERGYEMITTER, modid = DependencyInfo.ModIds.IC2),
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IENERGYSINK, modid = DependencyInfo.ModIds.IC2),
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IENERGYACCEPTOR, modid = DependencyInfo.ModIds.IC2),
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IENERGYTILE, modid = DependencyInfo.ModIds.IC2),
        @Optional.Interface(iface = DependencyInfo.Paths.COFH.IENERGYHANDLER, modid = DependencyInfo.ModIds.COFH_API)
})
public class TileRemoteInventory extends TileIOCore implements
        IInventory,
        IEnergySource, // IC2
        IEnergySink, // IC2
        IEnergyHandler // COFH
{

    @Override
    public void callback(IInventory inventory) {
        if (!hasWorldObj() || getWorldObj().isRemote) {
            return;
        }

        // I think IC2 caches tile state...
        if (registeredWithIC2) {
            IC2Helper.unloadEnergyTile(this);
            registeredWithIC2 = false;
        }

        if (hasTransferChip(TransferType.ENERGY_IC2) && getPlayer() != null) {
            IC2Helper.loadEnergyTile(this);
            registeredWithIC2 = true;
        }

        // Clear missing upgrade flag
        missingUpgrade = false;

        updateVisualState();
        updateNeighbors();
    }

    public String target;

    private boolean hasPlayer = false;
    private boolean registeredWithIC2 = false;
    private boolean missingUpgrade = false;

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        if (target != null && !target.isEmpty()) {
            nbt.setString("target", target);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("target")) {
            target = nbt.getString("target");
        } else {
            target = "";
        }
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 10 == 0) {
            EntityPlayer player = getPlayer();
            boolean hasPlayer = player != null;
            if (hasPlayer != this.hasPlayer) {
                markForUpdate();
                updateVisualState();
                updateNeighbors();

                this.hasPlayer = hasPlayer;
            }
        }
    }

    @Override
    public void onChunkUnload() {
        IC2Helper.unloadEnergyTile(this);
    }

    @Override
    public void invalidate() {
        IC2Helper.unloadEnergyTile(this);
    }

	/* CHIP METHODS */

    public EntityPlayer getPlayer() {
        if (target == null || target.isEmpty()) {
            return null;
        }

        EntityPlayer player = PlayerHelper.getPlayerForUsername(target);

        if (player != null) {
            if (!ItemWirelessTransmitter.hasValidRemote(player)) {
                return null;
            }
        }

        return player;
    }

    public IInventory getPlayerInventory(int transferType) {
        EntityPlayer player = getPlayer();
        if (player != null && hasTransferChip(transferType)) {
            return new InventoryArray(player.inventory.mainInventory);
        }
        return null;
    }

	/* END CHIP METHODS */

    public void setPlayer(String target) {
        if (registeredWithIC2) {
            IC2Helper.unloadEnergyTile(this);
            registeredWithIC2 = false;
        }

        this.target = target;

        if (!registeredWithIC2 && hasTransferChip(TransferType.ENERGY_IC2)) {
            IC2Helper.loadEnergyTile(this);
            registeredWithIC2 = true;
        }

        updateVisualState();
        updateNeighbors();
        markForUpdate();
    }

    public VisualState calculateVisualState() {
        if (target == null || target.isEmpty()) {
            return VisualState.INACTIVE;
        } else {
            EntityPlayer player = getPlayer();

            if (player == null) {
                return VisualState.INACTIVE_BLINK;
            }

            if (hasUpgradeChip(UpgradeType.SIMPLE_CAMO)) {
                return VisualState.CAMOUFLAGE_SIMPLE;
            }

            return missingUpgrade ? VisualState.ACTIVE_BLINK : VisualState.ACTIVE;
        }
    }

    /* IINVENTORY */
    @Override
    public int getSizeInventory() {
        IInventory inventoryPlayer = getPlayerInventory(TransferType.MATTER_ITEM);
        return inventoryPlayer != null ? inventoryPlayer.getSizeInventory() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        IInventory inventoryPlayer = getPlayerInventory(TransferType.MATTER_ITEM);
        return inventoryPlayer != null ? inventoryPlayer.getStackInSlot(slot) : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        IInventory inventoryPlayer = getPlayerInventory(TransferType.MATTER_ITEM);
        return inventoryPlayer != null ? inventoryPlayer.decrStackSize(slot, amount) : null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        IInventory inventoryPlayer = getPlayerInventory(TransferType.MATTER_ITEM);
        return inventoryPlayer != null ? inventoryPlayer.getStackInSlotOnClosing(slot) : null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        IInventory inventoryPlayer = getPlayerInventory(TransferType.MATTER_ITEM);
        if (inventoryPlayer != null) inventoryPlayer.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName() {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        IInventory inventoryPlayer = getPlayerInventory(TransferType.MATTER_ITEM);
        return inventoryPlayer != null ? inventoryPlayer.getInventoryStackLimit() : 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        IInventory inventoryPlayer = getPlayerInventory(TransferType.MATTER_ITEM);
        return inventoryPlayer != null && inventoryPlayer.isItemValidForSlot(slot, stack);
    }

    /* IENERGYSOURCE */
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public double getOfferedEnergy() {
        IInventory inventory = getPlayerInventory(TransferType.ENERGY_IC2);
        return inventory != null ? IC2TransferHelper.getCharge(inventory) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public void drawEnergy(double amount) {
        IInventory inventory = getPlayerInventory(TransferType.ENERGY_IC2);
        if (inventory != null) IC2TransferHelper.drain(inventory, amount);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return true;
    }

    /* IENERGYEMITTER */
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public int getSourceTier() {
        return Integer.MAX_VALUE;
    }

    /* IENERGYSINK */
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public double getDemandedEnergy() {
        IInventory IInventory = getPlayerInventory(TransferType.ENERGY_IC2);
        return IInventory != null ? IC2TransferHelper.requiresCharge(IInventory) ? 32D : 0D : 0D;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public int getSinkTier() {
        return Integer.MAX_VALUE;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        IInventory IInventory = getPlayerInventory(TransferType.ENERGY_IC2);
        return IInventory != null ? IC2TransferHelper.fill(IInventory, amount) : 0D;
    }

    /* IENERGYACCEPTOR */
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return getPlayerInventory(TransferType.ENERGY_IC2) != null;
    }

    /* IENERGYHANDLER */
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        IInventory inventory = getPlayerInventory(TransferType.ENERGY_RF);
        return inventory != null ? RFTransferHelper.fill(inventory, maxReceive, simulate) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        IInventory inventory = getPlayerInventory(TransferType.ENERGY_RF);
        return inventory != null ? RFTransferHelper.drain(inventory, maxExtract, simulate) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public int getEnergyStored(ForgeDirection from) {
        IInventory inventory = getPlayerInventory(TransferType.ENERGY_RF);
        return inventory != null ? RFTransferHelper.getCharge(inventory) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public int getMaxEnergyStored(ForgeDirection from) {
        IInventory inventory = getPlayerInventory(TransferType.ENERGY_RF);
        return inventory != null ? RFTransferHelper.getMaxCharge(inventory) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public boolean canConnectEnergy(ForgeDirection from) {
        return getPlayerInventory(TransferType.ENERGY_RF) != null;
    }
}