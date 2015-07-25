package ovh.adiantek.hatari.mods;

import static net.minecraft.enchantment.EnumEnchantmentType.all;
import static net.minecraft.enchantment.EnumEnchantmentType.armor;
import static net.minecraft.enchantment.EnumEnchantmentType.armor_feet;
import static net.minecraft.enchantment.EnumEnchantmentType.armor_head;
import static net.minecraft.enchantment.EnumEnchantmentType.armor_legs;
import static net.minecraft.enchantment.EnumEnchantmentType.armor_torso;
import static net.minecraft.enchantment.EnumEnchantmentType.breakable;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;

public class AutoArmor extends Modification implements ItemListener {
	private static final int[] helmetPriority = { 298, 314, 302, 306, 310 };
	private static final int[] chestPriority = { 299, 315, 303, 307, 311 };
	private static final int[] legsPriority = { 300, 316, 304, 308, 312 };
	private static final int[] bootsPriority = { 301, 317, 305, 309, 313 };
	private ItemStack[] armorStack;
	private int[] enchants = getObject("enchants", new int[] { 0, 0, 0, 0 });
	private double percent = 0;
	public AutoArmor() {
		super(AutoArmor.class, Categories.COMBAT, "Auto-Armor");
		FMLCommonHandler.instance().bus().register(this);
		this.addToggleCommand("autoarmor", "Wear automatically armor if you don't wear");
	}
	@Override
	public JComponent openConfig() {
		JPanel pr = new JPanel();
		pr.setBorder(BorderFactory.createTitledBorder("Enchantments priority"));
		pr.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		String[] types = new String[]{"Helmet:", "Chest:", "Legs:", "Boots:"};
		for(int i=0; i<4; i++) {
			constraints.gridy = i;
			constraints.fill=GridBagConstraints.NONE;
			constraints.weightx = 0.0D;
			pr.add(new JLabel(types[i]), constraints);
			constraints.fill=GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1.0D;
			JComboBox jcb = new JComboBox();
			Enchantment[] e = getAllEnchantments(i);
			for(Enchantment ed : e) {
				jcb.addItem(EnchantmentItem.get(ed));
			}
			jcb.setName(i+"");
			jcb.addItemListener(this);
			jcb.setSelectedItem(EnchantmentItem.instances[enchants[i]]);
			pr.add(jcb, constraints);
		}
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(pr);
		return new JScrollPane(panel);
	}
	@Override
	public void resetConfig() {
		percent=0;
		enchants=setObject("enchants", new int[]{0,0,0,0});
	}
	//TODO
	private boolean damaged(ItemStack item) {
		int max = item.getMaxDamage();
		int dur = max - item.getItemDamageForDisplay();
		return dur * 100.0d / max <= percent;
	}

	private void wear(int[] priority, int type) {
		EntityClientPlayerMP player = mc.thePlayer;
		ContainerPlayer inv = (ContainerPlayer) player.inventoryContainer;
		ArrayList<Slot> slots = new ArrayList<Slot>();
		for (int k = 0; k < 1; k++) {
			slots.clear();
			for (int j = priority.length-1; j >=0; j--) {
				int find = priority[j];
				for (int i = 9; i < inv.inventorySlots.size(); i++) {
					Slot slot = inv.getSlot(i);
					if (slot.getStack()!=null && Item.getIdFromItem(slot.getStack().getItem()) == priority[j]) {
					// 	if (k != 0 || !damaged(slot.getStack()))
							slots.add(slot);
					}
				}
				if (slots.size() > 0)
					break;
			}
			if (slots.size() == 0)
				continue;
			Slot best = slots.get(0);
			int level = getLevel(best, type);
			slots.remove(0);
			for (Slot s : slots) {
				int lvl = getLevel(s, type);
				if (lvl > level) {
					level = lvl;
					best = s;
				}
			}
			if ((best.slotNumber >= 36) && (best.slotNumber <= 44)) {
				player.inventory.currentItem = (best.slotNumber - 36);
			}
			mc.playerController.windowClick(0,
					best.slotNumber, 0, 1, player);
			return;
		}
	}
	private int getLevel(Slot slot, int type) {
		return EnchantmentHelper.getEnchantmentLevel(enchants[type],
				slot.getStack());
	}
	private Enchantment[] getAllEnchantments(int type) {
		ArrayList<Enchantment> enchs = new ArrayList<Enchantment>();
		for(int i=0; i<Enchantment.enchantmentsList.length; i++) {
			Enchantment e = Enchantment.enchantmentsList[i];
			if(e==null)
				continue;
			if(e.type==all || e.type==breakable || e.type==armor)
				enchs.add(e);
			else if(type==0 && e.type==armor_head)
				enchs.add(e);
			else if(type==1 && e.type==armor_torso)
				enchs.add(e);
			else if(type==2 && e.type==armor_legs)
				enchs.add(e);
			else if(type==3 && e.type==armor_feet)
				enchs.add(e);
		}
		return enchs.toArray(new Enchantment[enchs.size()]);
	}
	public static class EnchantmentItem {
		static EnchantmentItem[] instances = new EnchantmentItem[Enchantment.enchantmentsList.length];
		public static EnchantmentItem get(Enchantment e) {
			return instances[e.effectId];
		}
		static {
			for(int i=0; i<instances.length; i++) {
				if(Enchantment.enchantmentsList[i]!=null) {
					instances[i]=new EnchantmentItem(Enchantment.enchantmentsList[i]);
				}
			}
		}
		final Enchantment e;
		final String toString;
		public EnchantmentItem(Enchantment e) {
			this.e=e;
			toString=(e.getName());
		}
		public String toString() {
			return StatCollector.translateToLocal(toString);
		}
		public boolean equals(Object o) {
			if(o == null || !(o instanceof EnchantmentItem))
				return false;
			return ((EnchantmentItem)o).e.effectId==e.effectId;
		}
	}
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() != ItemEvent.SELECTED)
			return;
		int id = Integer.valueOf(((JComboBox)e.getSource()).getName());
		enchants[id]=((EnchantmentItem)e.getItem()).e.effectId;
		setObject("enchants", enchants);
	}
	@Override
	protected boolean onEnable() {
		return true;
	}
	@Override
	protected boolean onDisable() {
		return true;
	}
	@Executor
	public void event(String command) {
		toggle();
	}
	@SubscribeEvent
	public void event(TickEvent.ClientTickEvent e) {
		if (isEnabled() && mc.thePlayer!=null && mc.thePlayer.inventory!=null) {
			armorStack = mc.thePlayer.inventory.armorInventory;
			if (armorStack[3] == null || damaged(armorStack[3])) {
				wear(helmetPriority, 0);
			}
			if (armorStack[2] == null || damaged(armorStack[2])) {
				wear(chestPriority, 1);
			}
			if (armorStack[1] == null || damaged(armorStack[1])) {
				wear(legsPriority, 2);
			}
			if (armorStack[0] == null || damaged(armorStack[0])) {
				wear(bootsPriority, 3);
			}
		}
	}
}
