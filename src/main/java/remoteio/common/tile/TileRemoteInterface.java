package remoteio.common.tile;

import cofh.api.energy.IEnergyHandler;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.Optional;
import remoteio.common.core.TransferType;
import remoteio.common.core.UpgradeType;
import remoteio.common.core.helper.RotationHelper;
import remoteio.common.core.helper.mod.IC2Helper;
import remoteio.common.lib.DependencyInfo;
import remoteio.common.lib.DimensionalCoords;
import remoteio.common.lib.ModItems;
import remoteio.common.lib.VisualState;
import remoteio.common.tile.core.TileIOCore;
import remoteio.common.tracker.BlockTracker;
import remoteio.common.tracker.RedstoneTracker;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author dmillerw
 */
@Optional.InterfaceList({
        //@Optional.Interface(iface = DependencyInfo.Paths.Thaumcraft.IASPECTCONTAINER, modid = DependencyInfo.ModIds.THAUMCRAFT),
        //@Optional.Interface(iface = DependencyInfo.Paths.Thaumcraft.IASPECTSOURCE, modid = DependencyInfo.ModIds.THAUMCRAFT),
        //@Optional.Interface(iface = DependencyInfo.Paths.Thaumcraft.IESSENTIATRANSPORT, modid = DependencyInfo.ModIds.THAUMCRAFT),
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IENERGYSOURCE, modid = DependencyInfo.ModIds.IC2),
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IENERGYEMITTER, modid = DependencyInfo.ModIds.IC2),
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IENERGYSINK, modid = DependencyInfo.ModIds.IC2),
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IENERGYACCEPTOR, modid = DependencyInfo.ModIds.IC2),
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IENERGYTILE, modid = DependencyInfo.ModIds.IC2),
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IENERGYSTORAGE, modid = DependencyInfo.ModIds.IC2),
        @Optional.Interface(iface = DependencyInfo.Paths.COFH.IENERGYHANDLER, modid = DependencyInfo.ModIds.COFH_API),
        //@Optional.Interface(iface = DependencyInfo.Paths.AE2.IGRIDHOST, modid = DependencyInfo.ModIds.AE2),
        //@Optional.Interface(iface = DependencyInfo.Paths.AE2.IGRIDBLOCK, modid = DependencyInfo.ModIds.AE2),
        //@Optional.Interface(iface = DependencyInfo.Paths.Thaumcraft.IWANDABLE, modid = DependencyInfo.ModIds.THAUMCRAFT),
        @Optional.Interface(iface = DependencyInfo.Paths.IC2.IWRENCHABLE, modid = DependencyInfo.ModIds.IC2)
})
public class TileRemoteInterface extends TileIOCore implements BlockTracker.ITrackerCallback,
        IInventory,
        ISidedInventory,
        IFluidHandler,
        ITickable,
        //IAspectSource, // THAUMCRAFT
        //IEssentiaTransport, // THAUMCRAFT
        IEnergySource, // IC2
        IEnergySink, // IC2
        IEnergyStorage, // IC2
        IEnergyHandler, // COFH
        cofh.api.energy.IEnergyStorage, // COFH
        //IGridHost, // AE2
        //IGridBlock, //AE2
        //IWandable, // THAUMCRAFT
        IWrenchable // IC2
{ //TODO Cleanup

    @Override
    public void callback(IBlockAccess world, int x, int y, int z) {
        updateVisualState();
        updateNeighbors();
    }

    @Override
    public void callback(IInventory inventory) {
        if (!hasWorldObj() || getWorld().isRemote) {
            return;
        }

        // I think IC2 caches tile state...
        if (registeredWithIC2) {
            IC2Helper.unloadEnergyTile(this);
            registeredWithIC2 = false;
        }

        if (hasTransferChip(TransferType.ENERGY_IC2) && remotePosition != null && remotePosition.getTileEntity() != null) {
            IC2Helper.loadEnergyTile(this);
            registeredWithIC2 = true;
        }

        if (trackingRedstone) {
            RedstoneTracker.unregister(this);
            trackingRedstone = false;
        }

        RedstoneTracker.register(this);

        // Clear missing upgrade flag
        missingUpgrade = false;

        updateVisualState();
        updateNeighbors();
    }

    public DimensionalCoords remotePosition;

    //private LinkedGridNode aeGridNode;

    // THIS IS NOT AN ANGLE, BUT THE NUMBER OF LEFT-HAND ROTATIONS!
    public int rotationY = 0;

    public boolean locked = false;

    private int lastRedstoneLevel = 0;

    private boolean registeredWithIC2 = false;
    private boolean trackingRedstone = false;
    private boolean missingUpgrade = false;
    private boolean tracking = false;

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        if (remotePosition != null) {
            NBTTagCompound tag = new NBTTagCompound();
            remotePosition.writeToNBT(tag);
            nbt.setTag("position", tag);
        }

        nbt.setInteger("axisY", this.rotationY);
        nbt.setBoolean("locked", locked);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("position")) {
            remotePosition = DimensionalCoords.fromNBT(nbt.getCompoundTag("position"));
        } else {
            tracking = true;
        }

        rotationY = nbt.getInteger("axisY");
        locked = nbt.getBoolean("locked");
    }

    @Override
    public void onClientUpdate(NBTTagCompound nbt) {
        super.onClientUpdate(nbt);

        if (nbt.hasKey("position")) {
            remotePosition = DimensionalCoords.fromNBT(nbt.getCompoundTag("position"));
        } else if (nbt.hasKey("position_null")) {
            remotePosition = null;
        }

        if (nbt.hasKey("axisY")) {
            rotationY = nbt.getInteger("axisY");
        }
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            if (!tracking) {
                /*if (Loader.isModLoaded(DependencyInfo.ModIds.AE2)) {
                    if (remotePosition != null && remotePosition.getTileEntity() != this && hasTransferChip(TransferType.NETWORK_AE)) {
                        if (remotePosition.getTileEntity() instanceof IGridHost) {
                            aeGridNode = new LinkedGridNode(((IGridHost) remotePosition.getTileEntity()).getGridNode(ForgeDirection.UNKNOWN), this);
                            aeGridNode.updateState();
                        }
                    }

                    for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
                        TileEntity tileEntity = worldObj.getTileEntity(xCoord + forgeDirection.offsetZ, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);
                        if (tileEntity != null && tileEntity instanceof IGridHost) {
                            IGridNode gridNode = ((IGridHost) tileEntity).getGridNode(forgeDirection.getOpposite());
                            if (gridNode != null)
                                gridNode.updateState();
                        }
                    }
                }*/

                BlockTracker.INSTANCE.startTracking(remotePosition, this);
                tracking = true;
            }

            if (!trackingRedstone) {
                RedstoneTracker.register(this);
                trackingRedstone = true;
            }

            if (remotePosition != null && hasTransferChip(TransferType.REDSTONE)) {
                int redstone = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord);
                if (redstone != lastRedstoneLevel) {
                    remotePosition.markForUpdate();
                    lastRedstoneLevel = redstone;
                }
            }

            if (ModAPIManager.INSTANCE.hasAPI(DependencyInfo.ModIds.COFH_API)) {
                ItemStack rfTransferChip = null;
                for (int i=0; i<transferChips.getSizeInventory(); i++) {
                    ItemStack itemStack = transferChips.getStackInSlot(i);
                    if (itemStack != null && itemStack.hasTagCompound() &&  itemStack.getItem() == ModItems.transferChip && itemStack.getItemDamage() == TransferType.ENERGY_RF) {
                        rfTransferChip = itemStack;
                        break;
                    }
                }

                if (rfTransferChip != null) {
                    int maxPushPower = rfTransferChip.getTagCompound().getInteger("maxPushRate");

                    if (maxPushPower > 0) {
                        int count = 0;
                        IEnergyHandler[] energyHandlers = new IEnergyHandler[ForgeDirection.VALID_DIRECTIONS.length];
                        for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
                            TileEntity tileEntity = worldObj.getTileEntity(xCoord + forgeDirection.offsetX, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);
                            if (tileEntity instanceof IEnergyHandler && getEnergyStored(forgeDirection.getOpposite()) > 0) {
                                count++;
                                energyHandlers[forgeDirection.ordinal()] = (IEnergyHandler) tileEntity;
                            }
                        }

                        if (count > 0) {
                            int perBlock = maxPushPower / count;
                            for (int i=0; i<energyHandlers.length; i++) {
                                IEnergyHandler energyHandler = energyHandlers[i];
                                if (energyHandler != null) {
                                    ForgeDirection to = ForgeDirection.getOrientation(i);
                                    ForgeDirection from = to.getOpposite();
                                    energyHandler.receiveEnergy(from, extractEnergy(to, perBlock, false), false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onChunkUnload() {
       IC2Helper.unloadEnergyTile(this);

        /*if (Loader.isModLoaded(DependencyInfo.ModIds.AE2)) {
            if (aeGridNode != null) {
                aeGridNode.destroy();
                aeGridNode.updateState();
            }
        }*/

        RedstoneTracker.unregister(this);
        BlockTracker.INSTANCE.stopTracking(remotePosition);
    }

    @Override
    public void invalidate() {
        IC2Helper.unloadEnergyTile(this);

        /*if (Loader.isModLoaded(DependencyInfo.ModIds.AE2)) {
            if (aeGridNode != null) {
                aeGridNode.destroy();
                aeGridNode.updateState();
            }
        }*/

        RedstoneTracker.unregister(this);
        BlockTracker.INSTANCE.stopTracking(remotePosition);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

	/* BEGIN CLIENT UPDATE METHODS
     * 'update' methods are used to calculate what should be sent to the client
	 * 'send' methods actually send the data to the client, and take a single parameter
	 *  that is the data to be sent
	 *
	 *  Methods pertaining to the same data are lumped together */

    /**
     * Sends the remote position to the client
     */
    public void updateRemotePosition() {
        sendRemotePosition(this.remotePosition);
    }

    public void sendRemotePosition(DimensionalCoords coords) {
        NBTTagCompound nbt = new NBTTagCompound();
        if (coords != null) {
            NBTTagCompound tag = new NBTTagCompound();
            coords.writeToNBT(tag);
            nbt.setTag("position", tag);
        } else {
            nbt.setBoolean("position_null", true);
        }
        sendClientUpdate(nbt);
    }

    /**
     * Sends the passed in theta modifer to the client
     * Different than normal update methods due to how it's calculated
     */
    public void updateRotation(int modification) {
        this.rotationY += modification;
        if (rotationY > 3) {
            rotationY = 0;
        } else if (rotationY < 0) {
            rotationY = 3;
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("axisY", this.rotationY);
        sendClientUpdate(nbt);
    }

    public VisualState calculateVisualState() {
        if (remotePosition == null) {
            return VisualState.INACTIVE;
        } else {
            if (!remotePosition.blockExists()) {
                return VisualState.INACTIVE_BLINK;
            }

            boolean simple = hasUpgradeChip(UpgradeType.SIMPLE_CAMO);
            boolean remote = hasUpgradeChip(UpgradeType.REMOTE_CAMO);

            if (simple && !remote) {
                return VisualState.CAMOUFLAGE_SIMPLE;
            } else if (!simple && remote) {
                return VisualState.CAMOUFLAGE_REMOTE;
            } else if (simple && remote) {
                return VisualState.CAMOUFLAGE_BOTH;
            }

            return missingUpgrade ? VisualState.ACTIVE_BLINK : VisualState.ACTIVE;
        }
    }

    @Override
    public void sendVisualState(VisualState visualState) {
        super.sendVisualState(visualState);

        if (visualState == VisualState.CAMOUFLAGE_REMOTE) {
            updateRemotePosition();
        }
    }

    /**
     * Sets the server-side remote position to the passed in position, and resets the tracker
     */
    public void setRemotePosition(DimensionalCoords coords) {
        IC2Helper.unloadEnergyTile(this);

        /*if (Loader.isModLoaded(DependencyInfo.ModIds.AE2)) {
            if (aeGridNode != null) {
                aeGridNode.destroy();
                aeGridNode.updateState();
            }
        }*/

        RedstoneTracker.unregister(this);
        BlockTracker.INSTANCE.stopTracking(remotePosition);
        remotePosition = coords;
        RedstoneTracker.register(this);
        BlockTracker.INSTANCE.startTracking(remotePosition, this);

        IC2Helper.loadEnergyTile(this);

        /*if (Loader.isModLoaded(DependencyInfo.ModIds.AE2)) {
            if (remotePosition != null && remotePosition.getTileEntity() != this && hasTransferChip(TransferType.NETWORK_AE)) {
                if (remotePosition.getTileEntity() instanceof IGridHost) {
                    aeGridNode = new LinkedGridNode(((IGridHost) remotePosition.getTileEntity()).getGridNode(ForgeDirection.UNKNOWN), this);
                    aeGridNode.updateState();
                }
            }

            for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
                TileEntity tileEntity = worldObj.getTileEntity(xCoord + forgeDirection.offsetZ, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);
                if (tileEntity != null && tileEntity instanceof IGridHost) {
                    IGridNode gridNode = ((IGridHost) tileEntity).getGridNode(forgeDirection.getOpposite());
                    if (gridNode != null)
                        gridNode.updateState();
                }
            }
        }*/

        worldObj.notifyBlockOfStateChange(pos, this.getBlockType());
        markForUpdate();
    }

	/* END CLIENT UPDATE METHODS */

    public Object getUpgradeImplementation(Class cls) {
        return getUpgradeImplementation(cls, -1);
    }

    public Object getUpgradeImplementation(Class cls, int upgradeType) {
        if (remotePosition == null) {
            return null;
        }

        if (!remotePosition.blockExists()) {
            return null;
        }

        TileEntity remote = remotePosition.getTileEntity();

        if (remote != null) {
            if (!(cls.isInstance(remote))) {
                return null;
            }
        } else {
            if (!(cls.isInstance(remotePosition.getBlock()))) {
                return null;
            }
        }

        if (upgradeType != -1) {
            if (!hasUpgradeChip(upgradeType)) {
                return null;
            }
        }

        return cls.cast(remote);
    }

    public Object getTransferImplementation(Class cls) {
        return getTransferImplementation(cls, true);
    }

    public Object getTransferImplementation(Class cls, boolean requiresChip) {
        if (remotePosition == null) {
            return null;
        }

        if (!remotePosition.blockExists()) {
            return null;
        }

        TileEntity remote = remotePosition.getTileEntity();

        if (remote != null) {
            if (!(cls.isInstance(remote))) {
                return null;
            }
        } else {
            if (!(cls.isInstance(remotePosition.getBlock()))) {
                return null;
            }
        }

        if (requiresChip) {
            if (!hasTransferChip(TransferType.getTypeForInterface(cls))) {
                if (missingUpgrade == false) {
                    missingUpgrade = true;
                    updateVisualState();
                }
                return null;
            }
        }

        return cls.cast(remote);
    }

    public EnumFacing getAdjustedSide(EnumFacing facing) {
        return facing;
//        return ForgeDirection.getOrientation(RotationHelper.getRotatedSide(0, rotationY, 0, side.ordinal()));
    }

    public int getAdjustedSide(int side) {
        return RotationHelper.getRotatedSide(0, rotationY, 0, side);
    }

	/* START IMPLEMENTATIONS */

    /* IINVENTORY */
    @Override
    public int getSizeInventory() {
        IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
        return inventory != null ? inventory.getSizeInventory() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
        return inventory != null ? inventory.getStackInSlot(slot) : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
        return inventory != null ? inventory.decrStackSize(slot, amt) : null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
        return inventory != null ? inventory.removeStackFromSlot(index) : null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
        if (inventory != null) inventory.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getName() {
        IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
        return inventory != null ? inventory.getName() : null;
    }

    @Override
    public boolean hasCustomName() {
        IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
        return inventory != null && inventory.hasCustomName();
    }

    @Override
    public int getInventoryStackLimit() {
        IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
        return inventory != null ? inventory.getInventoryStackLimit() : 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
        return inventory != null && inventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
        return inventory != null && inventory.isItemValidForSlot(index, stack);
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

    }

    /* ISIDEDINVENTORY */
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        ISidedInventory sidedInventory = (ISidedInventory) getTransferImplementation(ISidedInventory.class);
        if (sidedInventory != null) {
            return sidedInventory.getSlotsForFace(getAdjustedSide(side));
        } else {
            IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
            if (inventory != null) {
                int[] array = new int[inventory.getSizeInventory()];
                for (int i=0; i<array.length; i++) {
                    array[i] = i;
                }
                return array;
            }
        }
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        ISidedInventory sidedInventory = (ISidedInventory) getTransferImplementation(ISidedInventory.class);
        if (sidedInventory != null) {
            return sidedInventory.canInsertItem(index, stack, getAdjustedSide(direction));
        } else {
            IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);

            if (inventory != null) {
                return inventory.isItemValidForSlot(index, stack);
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        ISidedInventory sidedInventory = (ISidedInventory) getTransferImplementation(ISidedInventory.class);
        if (sidedInventory != null) {
            return sidedInventory.canExtractItem(index, stack, getAdjustedSide(direction));
        } else {
            IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);

            if (inventory != null) {
                return true;
            }
        }
        return false;
    }

    /* IFLUIDHANDLER */
    @Override
    public int fill(FluidStack resource, boolean doFill) {
        IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
        return fluidHandler != null ? fluidHandler.fill(getAdjustedSide(from), resource, doFill) : 0;
    }

    @Override
    @Nullable
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
        return fluidHandler != null ? fluidHandler.drain(getAdjustedSide(from), resource, doDrain) : null;
    }

    @Override
    @Nullable
    public FluidStack drain(int maxDrain, boolean doDrain) {
        IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
        return fluidHandler != null ? fluidHandler.drain(getAdjustedSide(from), maxDrain, doDrain) : null;
    }

    /*@Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
        return fluidHandler != null && fluidHandler.canFill(getAdjustedSide(from), fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
        return fluidHandler != null && fluidHandler.canDrain(getAdjustedSide(from), fluid);
    }*/

    @Override
    public IFluidTankProperties[] getTankProperties() {
        IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
        return fluidHandler != null ? fluidHandler.getTankInfo(getAdjustedSide(from)) : new FluidTankInfo[0];
    }

    /* IASPECTSOURCE */
/*    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public AspectList getAspects() {
        IAspectSource aspectContainer = (IAspectSource) getTransferImplementation(IAspectSource.class);
        return aspectContainer != null ? aspectContainer.getAspects() : new AspectList();
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public void setAspects(AspectList aspects) {
        IAspectSource aspectContainer = (IAspectSource) getTransferImplementation(IAspectSource.class);
        if (aspectContainer != null) aspectContainer.setAspects(aspects);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public boolean doesContainerAccept(Aspect tag) {
        IAspectSource aspectContainer = (IAspectSource) getTransferImplementation(IAspectSource.class);
        return aspectContainer != null && aspectContainer.doesContainerAccept(tag);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public int addToContainer(Aspect tag, int amount) {
        IAspectSource aspectContainer = (IAspectSource) getTransferImplementation(IAspectSource.class);
        return aspectContainer != null ? aspectContainer.addToContainer(tag, amount) : amount;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public boolean takeFromContainer(Aspect tag, int amount) {
        IAspectSource aspectContainer = (IAspectSource) getTransferImplementation(IAspectSource.class);
        return aspectContainer != null && aspectContainer.takeFromContainer(tag, amount);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public boolean takeFromContainer(AspectList ot) {
        IAspectSource aspectContainer = (IAspectSource) getTransferImplementation(IAspectSource.class);
        return aspectContainer != null && aspectContainer.takeFromContainer(ot);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        IAspectSource aspectContainer = (IAspectSource) getTransferImplementation(IAspectSource.class);
        return aspectContainer != null && aspectContainer.doesContainerContainAmount(tag, amount);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public boolean doesContainerContain(AspectList ot) {
        IAspectSource aspectContainer = (IAspectSource) getTransferImplementation(IAspectSource.class);
        return aspectContainer != null && aspectContainer.doesContainerContain(ot);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public int containerContains(Aspect tag) {
        IAspectSource aspectContainer = (IAspectSource) getTransferImplementation(IAspectSource.class);
        return aspectContainer != null ? aspectContainer.containerContains(tag) : 0;
    }

    *//* IESSENTIATRANSPORT *//*
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public boolean isConnectable(ForgeDirection face) {
        IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
        return essentiaTransport != null && essentiaTransport.isConnectable(getAdjustedSide(face));
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public boolean canInputFrom(ForgeDirection face) {
        return true;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public boolean canOutputTo(ForgeDirection face) {
        return true;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public void setSuction(Aspect aspect, int amount) {
        IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
        if (essentiaTransport != null) essentiaTransport.setSuction(aspect, amount);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public Aspect getSuctionType(ForgeDirection face) {
        IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
        return essentiaTransport != null ? essentiaTransport.getSuctionType(getAdjustedSide(face)) : null;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public int getSuctionAmount(ForgeDirection face) {
        IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
        return essentiaTransport != null ? essentiaTransport.getSuctionAmount(getAdjustedSide(face)) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
        return essentiaTransport != null ? essentiaTransport.takeEssentia(aspect, amount, getAdjustedSide(face)) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
        return essentiaTransport != null ? essentiaTransport.addEssentia(aspect, amount, getAdjustedSide(face)) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public Aspect getEssentiaType(ForgeDirection face) {
        IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
        return essentiaTransport != null ? essentiaTransport.getEssentiaType(getAdjustedSide(face)) : null;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public int getEssentiaAmount(ForgeDirection face) {
        IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
        return essentiaTransport != null ? essentiaTransport.getEssentiaAmount(getAdjustedSide(face)) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public int getMinimumSuction() {
        IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
        return essentiaTransport != null ? essentiaTransport.getMinimumSuction() : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public boolean renderExtendedTube() {
        IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
        return essentiaTransport != null && essentiaTransport.renderExtendedTube();
    }*/

    /* IENERGYSOURCE */
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public double getOfferedEnergy() {
        IEnergySource energySource = (IEnergySource) getTransferImplementation(IEnergySource.class);
        return energySource != null ? energySource.getOfferedEnergy() : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public void drawEnergy(double amount) {
        IEnergySource energySource = (IEnergySource) getTransferImplementation(IEnergySource.class);
        if (energySource != null) energySource.drawEnergy(amount);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public boolean emitsEnergyTo(IEnergyAcceptor acceptor, EnumFacing facing) {
        IEnergySource energySource = (IEnergySource) getTransferImplementation(IEnergySource.class);
        return energySource != null && energySource.emitsEnergyTo(acceptor, getAdjustedSide(facing));
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public int getSourceTier() {
        IEnergySource energySource = (IEnergySource) getTransferImplementation(IEnergySource.class);
        return energySource != null ? energySource.getSourceTier() : 0;
    }

    /* IENERGYSINK */
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public double getDemandedEnergy() {
        IEnergySink energySink = (IEnergySink) getTransferImplementation(IEnergySink.class);
        return energySink != null ? energySink.getDemandedEnergy() : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public int getSinkTier() {
        IEnergySink energySink = (IEnergySink) getTransferImplementation(IEnergySink.class);
        return energySink != null ? energySink.getSinkTier() : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public double injectEnergy(EnumFacing facing, double amount, double voltage) {
        IEnergySink energySink = (IEnergySink) getTransferImplementation(IEnergySink.class);
        return energySink != null ? energySink.injectEnergy(getAdjustedSide(facing), amount, voltage) : 0;
    }

    /* IENERGYACCEPTOR */
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing facing) {
        IEnergyAcceptor energyAcceptor = (IEnergyAcceptor) getTransferImplementation(IEnergyAcceptor.class);
        return energyAcceptor != null && energyAcceptor.acceptsEnergyFrom(emitter, getAdjustedSide(facing));
    }

    /* IENERGYSTORAGE */
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public int getStored() {
        IEnergyStorage energyStorage = (IEnergyStorage) getTransferImplementation(IEnergyStorage.class);
        return energyStorage != null ? energyStorage.getStored() : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public void setStored(int energy) {
        IEnergyStorage energyStorage = (IEnergyStorage) getTransferImplementation(IEnergyStorage.class);
        if (energyStorage != null) energyStorage.setStored(energy);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public int addEnergy(int amount) {
        IEnergyStorage energyStorage = (IEnergyStorage) getTransferImplementation(IEnergyStorage.class);
        return energyStorage != null ? energyStorage.addEnergy(amount) : getStored();
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public int getCapacity() {
        IEnergyStorage energyStorage = (IEnergyStorage) getTransferImplementation(IEnergyStorage.class);
        return energyStorage != null ? energyStorage.getCapacity() : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public int getOutput() {
        IEnergyStorage energyStorage = (IEnergyStorage) getTransferImplementation(IEnergyStorage.class);
        return energyStorage != null ? energyStorage.getOutput() : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public double getOutputEnergyUnitsPerTick() {
        IEnergyStorage energyStorage = (IEnergyStorage) getTransferImplementation(IEnergyStorage.class);
        return energyStorage != null ? energyStorage.getOutputEnergyUnitsPerTick() : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public boolean isTeleporterCompatible(EnumFacing facing) {
        IEnergyStorage energyStorage = (IEnergyStorage) getTransferImplementation(IEnergyStorage.class);
        return energyStorage != null && energyStorage.isTeleporterCompatible(getAdjustedSide(facing));
    }

    /* IENERGYHANDLER - COFH*/

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public int getEnergyStored(EnumFacing from) {
        IEnergyHandler energyHandler = (IEnergyHandler) getTransferImplementation(IEnergyHandler.class);
        return energyHandler != null ? energyHandler.getEnergyStored(getAdjustedSide(from)) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public int getMaxEnergyStored(EnumFacing from) {
        IEnergyHandler energyHandler = (IEnergyHandler) getTransferImplementation(IEnergyHandler.class);
        return energyHandler != null ? energyHandler.getMaxEnergyStored(getAdjustedSide(from)) : 0;
    }

    /* IENERGYSTORAGE - COFH*/

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public int receiveEnergy(int maxReceive, boolean simulate) {
        cofh.api.energy.IEnergyStorage energyHandler = (cofh.api.energy.IEnergyStorage) getTransferImplementation(IEnergyHandler.class);
        return energyHandler != null ? energyHandler.receiveEnergy(maxReceive, simulate) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public int extractEnergy(int maxExtract, boolean simulate) {
        cofh.api.energy.IEnergyStorage energyHandler = (cofh.api.energy.IEnergyStorage) getTransferImplementation(IEnergyHandler.class);
        return energyHandler != null ? energyHandler.extractEnergy(maxExtract, simulate) : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public boolean canConnectEnergy(EnumFacing from) {
        IEnergyHandler energyHandler = (IEnergyHandler) getTransferImplementation(IEnergyHandler.class);
        return energyHandler != null && energyHandler.canConnectEnergy(getAdjustedSide(from));
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public int getEnergyStored() {

    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.COFH_API)
    public int getMaxEnergyStored() {

    }

    /* IWANDABLE */
/*    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        IWandable wandable = (IWandable) getUpgradeImplementation(IWandable.class, UpgradeType.REMOTE_ACCESS);
        return wandable != null ? wandable.onWandRightClick(world, wandstack, player, x, y, z, getAdjustedSide(side), md) : -1;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        IWandable wandable = (IWandable) getUpgradeImplementation(IWandable.class, UpgradeType.REMOTE_ACCESS);
        return wandable != null ? wandable.onWandRightClick(world, wandstack, player) : wandstack;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
        IWandable wandable = (IWandable) getUpgradeImplementation(IWandable.class, UpgradeType.REMOTE_ACCESS);
        if (wandable != null) wandable.onUsingWandTick(wandstack, player, count);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.THAUMCRAFT)
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
        IWandable wandable = (IWandable) getUpgradeImplementation(IWandable.class, UpgradeType.REMOTE_ACCESS);
        if (wandable != null) wandable.onWandStoppedUsing(wandstack, world, player, count);
    }*/

    /* IWRENCHABLE */
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public EnumFacing getFacing(World world, BlockPos pos) {
        IWrenchable wrenchable = (IWrenchable) getUpgradeImplementation(IWrenchable.class, UpgradeType.REMOTE_ACCESS);
        return wrenchable != null ? wrenchable.getFacing(world, pos) : EnumFacing.DOWN;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public boolean setFacing(World world, BlockPos pos, EnumFacing facing, EntityPlayer player) {
        IWrenchable wrenchable = (IWrenchable) getUpgradeImplementation(IWrenchable.class, UpgradeType.REMOTE_ACCESS);
        if (wrenchable != null) wrenchable.setFacing((short) getAdjustedSide(facing));
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer entityPlayer) {
        return false;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.IC2)
    public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity tileEntity, EntityPlayer player, int i) {
        return null;
    }

    /* IGRIDHOST */
/*    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public IGridNode getGridNode(ForgeDirection dir) {
        IGridHost gridNode = (IGridHost) getTransferImplementation(IGridHost.class);
        return gridNode != null ? gridNode.getGridNode(getAdjustedSide(dir)) : null;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return AECableType.GLASS;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public void securityBreak() {
        worldObj.setBlockToAir(xCoord, yCoord, zCoord);
    }

    *//* IGRIDBLOCK *//*
    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public double getIdlePowerUsage() {
        IGridBlock gridBlock = (IGridBlock) getTransferImplementation(IGridBlock.class);
        return gridBlock != null ? gridBlock.getIdlePowerUsage() : 0;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public EnumSet<GridFlags> getFlags() {
        IGridBlock gridBlock = (IGridBlock) getTransferImplementation(IGridBlock.class);
        return gridBlock != null ? getFlags() : EnumSet.noneOf(GridFlags.class);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public boolean isWorldAccessible() {
        return true;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public AEColor getGridColor() {
        IGridBlock gridBlock = (IGridBlock) getTransferImplementation(IGridBlock.class);
        return gridBlock != null ? gridBlock.getGridColor() : AEColor.Transparent;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public void onGridNotification(GridNotification notification) {
        IGridBlock gridBlock = (IGridBlock) getTransferImplementation(IGridBlock.class);
        if (gridBlock != null) gridBlock.onGridNotification(notification);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public void setNetworkStatus(IGrid grid, int channelsInUse) {
        IGridBlock gridBlock = (IGridBlock) getTransferImplementation(IGridBlock.class);
        if (gridBlock != null) gridBlock.setNetworkStatus(grid, channelsInUse);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public EnumSet<ForgeDirection> getConnectableSides() {
        IGridBlock gridBlock = (IGridBlock) getTransferImplementation(IGridBlock.class);
        return gridBlock != null ? gridBlock.getConnectableSides() : EnumSet.noneOf(ForgeDirection.class);
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public IGridHost getMachine() {
        return this;
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public void gridChanged() {
        IGridBlock gridBlock = (IGridBlock) getTransferImplementation(IGridBlock.class);
        if (gridBlock != null) gridBlock.gridChanged();
    }

    @Override
    @Optional.Method(modid = DependencyInfo.ModIds.AE2)
    public ItemStack getMachineRepresentation() {
        IGridBlock gridBlock = (IGridBlock) getTransferImplementation(IGridBlock.class);
        return gridBlock != null ? getMachineRepresentation() : new ItemStack(this.blockType);
    }*/

    /* END IMPLEMENTATIONS */
}