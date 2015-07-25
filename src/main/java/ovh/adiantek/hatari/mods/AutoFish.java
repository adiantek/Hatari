package ovh.adiantek.hatari.mods;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import ovh.adiantek.hatari.CommandManager;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AutoFish extends Modification implements ChangeListener {
	private long lastPacketTime = getLong("lastPacketTime", 0);
	private long shouldRecast = getLong("shouldRecast", 0);
	private boolean recast = getBoolean("recast", true);
	private boolean multirod = getBoolean("multirod", true);
	private static AutoFish instance;
	@Override
	public JComponent openConfig() {
		JPanel panel = new JPanel(new GridBagLayout());
		JCheckBox recast = new JCheckBox("Recast fishing rod");
		JCheckBox multirod = new JCheckBox("Use MultiRod");
		recast.setName("recast");
		multirod.setName("multirod");
		recast.setSelected(this.recast);
		multirod.setSelected(this.multirod);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridy = 0;
		constraints.fill=GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		panel.add(recast, constraints);
		constraints.gridy=1;
		panel.add(multirod, constraints);
		recast.addChangeListener(this);
		return new JScrollPane(panel);
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		JCheckBox jcb = (JCheckBox)e.getSource();
		if(jcb.getName().equals("recast"))
			recast=setBoolean("recast", jcb.isSelected());
		else
			multirod=setBoolean("multirod", jcb.isSelected());
	}
	@Override
	public void resetConfig() {
		recast=setBoolean("recast", true);
		multirod=setBoolean("multirod", true);
	}

	public AutoFish() {
		super(AutoFish.class, Categories.PLAYER, "Auto-Fish");
		MinecraftForge.EVENT_BUS.register(this);
		addToggleCommand("autofish", "Enable or disable Auto-Fish");
		addToggleCommand("autofish multirod", "Use multirod");
		addToggleCommand("autofish recast", "Enable or disable recast fishing rod");
		instance = this;
	}

	@Override
	protected boolean onEnable() {
		return true;
	}

	@Override
	protected boolean onDisable() {
		return true;
	}
	@SubscribeEvent
	public final void onDraw(RenderGameOverlayEvent.Text draw) {
		if (!isEnabled())
			return;
		if (mc.thePlayer == null)
			return;
		EntityPlayer player = mc.thePlayer;
		if (shouldRecast != -1
				&& System.currentTimeMillis() - shouldRecast >= 100L && recast) {
			if (player.getCurrentEquippedItem() == null && multirod) {
				player.inventory.func_146030_a(Items.fishing_rod, 0, false,
						false);
			}
			try {
				mc.playerController.sendUseItem(player, mc.theWorld,
						player.getCurrentEquippedItem());
			} catch (Throwable t) {

			}
			shouldRecast = setLong("shouldRecast", -1);
		}
	}

	public String getTitle() {
		return "AutoFish";
	}

	private void process_(S12PacketEntityVelocity packet) {
		if (!isEnabled())
			return;
		int id = packet.func_149412_c();
		if (mc.thePlayer.fishEntity != null
				&& mc.thePlayer.fishEntity.getEntityId() == id) {
			EntityFishHook hook = mc.thePlayer.fishEntity;
			World w = hook.worldObj;
			if (w.isAABBInMaterial(hook.boundingBox, Material.water)) {
				int mX = packet.func_149411_d();
				int mY = packet.func_149410_e();
				int mZ = packet.func_149409_f();
				if (mX == 0f && mZ == 0 && mY < 0.0F) {
					if (System.currentTimeMillis() - lastPacketTime >= 1000L) {
						mc.playerController.sendUseItem(mc.thePlayer, w,
								mc.thePlayer.getCurrentEquippedItem());
						lastPacketTime = setLong("lastPacketTime",
								System.currentTimeMillis());
						shouldRecast = setLong("shouldRecast",
								System.currentTimeMillis());
					}
				}
			}
		}
	}

	public static void process(S12PacketEntityVelocity packet) {
		if (instance != null)
			instance.process_(packet);
	}
	public static byte[] transform(byte[] basicClass) {
		ClassNode node = new ClassNode();
		ClassReader reader = new ClassReader(basicClass);
		reader.accept(node, 0);
		for (MethodNode mn : node.methods) {
			if (mn.name.equals("processPacket")) {
				InsnList m = mn.instructions;
				MethodNode mv = new MethodNode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(
						INVOKESTATIC,
						"ovh/adiantek/hatari/mods/AutoFish",
						"process",
						"(Lnet/minecraft/network/play/server/S12PacketEntityVelocity;)V",
						false);
				mv.visitEnd();
				m.insert(mv.instructions);
			}
		}
		ClassWriter cw = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS
				| ClassWriter.COMPUTE_FRAMES);
		node.accept(cw);
		return cw.toByteArray();
	}
	@Executor
	public void exec(String command) {
		if (command.equals("autofish"))
			toggle();
		if (command.equals("autofish recast")) {
			recast = setBoolean("recast", !recast);
			if (recast) {
				viewMessage("Enabled recasting rod.");
			} else {
				viewMessage("Disabled recasting rod.");
			}
		}
		if (command.equals("autofish multirod")) {
			multirod = setBoolean("multirod", !multirod);
			if (recast) {
				viewMessage("Enabled MultiRod.");
			} else {
				viewMessage("Disabled MultiRod.");
			}
		}
	}
}