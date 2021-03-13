package ovh.adiantek.hatari.mods;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;


// TODO Don't use tool, if HP < x
// HP =inv.getCurrentItem().getMaxDamage() - inv.getCurrentItem().getItemDamageForDisplay()
public class AutoTool extends Modification {
	private MovingObjectPosition omo;
	public AutoTool() {
		super(AutoTool.class, Categories.PLAYER, "Auto-Tool");
		MinecraftForge.EVENT_BUS.register(this);
		addToggleCommand("autotool", "Automatically selects the best tool for the job");
	}
	protected boolean onEnable() {
		return true;
	}
	protected boolean onDisable() {
		return true;
	}
	@SubscribeEvent
	public void clickBlock(DrawBlockHighlightEvent pie) {
		omo = pie.target;
		if(mc.theWorld==null||omo==null)
			return;
		Block b = mc.theWorld.getBlock(omo.blockX, omo.blockY, omo.blockZ);
		if(b==Blocks.air)
			return;
		if(mc.debug.contains(", speed: ")) {
			mc.debug=mc.debug.substring(0, mc.debug.indexOf(", speed: "));
		}
		mc.debug+=", speed: "+strength(b, omo.blockX, omo.blockY, omo.blockZ)*15+"/15";
		if(!mc.playerController.isHittingBlock)
			return;
		if(!isEnabled())
			return;
		InventoryPlayer inv = mc.thePlayer.inventory;
		int curr = inv.currentItem;
		float currS = strength(b, omo.blockX, omo.blockY, omo.blockZ);
		for(int i=0; i<9; i++) {
			if(inv.getStackInSlot(i)!=null) {
				inv.currentItem=i;
				float str = strength(b, omo.blockX, omo.blockY, omo.blockZ);
				if(str>currS) {
					currS=str;
					curr=i;
				}
			}
		}
		inv.currentItem=curr;
	}
	private static float strength(Block block, int x, int y, int z) {
		return block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, x, y, z);
	}
	@Executor
	public void event(String command) {
		toggle();
	}
}
