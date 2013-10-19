package com.dmillerw.remoteIO.core.config;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;

import com.dmillerw.remoteIO.block.BlockHeater;
import com.dmillerw.remoteIO.block.BlockIO;
import com.dmillerw.remoteIO.block.BlockReservoir;
import com.dmillerw.remoteIO.item.ItemTool;
import com.dmillerw.remoteIO.item.ItemUpgrade;
import com.dmillerw.remoteIO.item.Upgrade;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class RIOConfiguration {

	private final Configuration config;
	
	public int blockRIOID;
	public int blockHeaterID;
	public int blockReservoirID;
	
	public int itemToolID;
	public int itemUpgradeID;
	
	public Block blockRIO;
	public Block blockHeater;
	public Block blockReservoir;
	
	public Item itemTool;
	public Item itemUpgrade;
	
	public RIOConfiguration(Configuration config) {
		this.config = config;
	}
	
	public void scanConfig() {
		this.config.load();
		this.blockRIOID = config.getBlock("block_id.RIO", 600).getInt(600);
		this.blockHeaterID = config.getBlock("block_id.heater", 601).getInt(601);
		this.blockReservoirID = config.getBlock("block_id.reservoir", 602).getInt(602);
		this.itemToolID = config.getItem("item_id.tool", 6000).getInt(6000);
		this.itemUpgradeID = config.getItem("item_id.upgrade", 6001).getInt(6001);
		if (this.config.hasChanged()) {
			this.config.save();
		}
		
		// Item/Block init
		this.blockRIO = new BlockIO(blockRIOID).setUnlocalizedName("blockIO");
		GameRegistry.registerBlock(this.blockRIO, "blockIO");
		LanguageRegistry.addName(this.blockRIO, "IO Block");
		
		this.blockHeater = new BlockHeater(blockHeaterID).setUnlocalizedName("blockHeater");
		GameRegistry.registerBlock(this.blockHeater, "blockHeater");
		LanguageRegistry.addName(this.blockHeater, "Lava-Powered Heater");
		
		this.blockReservoir = new BlockReservoir(blockReservoirID).setUnlocalizedName("blockReservoir");
		GameRegistry.registerBlock(this.blockReservoir, "blockReservoir");
		LanguageRegistry.addName(this.blockReservoir, "Water Reservoir");
		
		this.itemTool = new ItemTool(itemToolID).setUnlocalizedName("itemTool");
		GameRegistry.registerItem(this.itemTool, "itemTool");
		LanguageRegistry.addName(this.itemTool, "RemoteIO Linker");
		
		this.itemUpgrade = new ItemUpgrade(itemUpgradeID).setUnlocalizedName("itemUpgrade");
		GameRegistry.registerItem(this.itemUpgrade, "itemUpgrade");
		for (Upgrade upgrade : Upgrade.values()) {
			LanguageRegistry.addName(upgrade.toItemStack(), upgrade.localizedName);
		}
	}
	
}
