package ovh.adiantek.hatari.mods;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.awt.GridLayout;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.play.client.C01PacketChatMessage;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import ovh.adiantek.hatari.CommandManager;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class AutoLogin extends Modification {
	private static AutoLogin instance;
	private SocketAddress current;
	private HashMap<String, HashMap<String, String>> passwords = getObject(
			"pwd", new HashMap<String, HashMap<String, String>>());

	public AutoLogin() {
		super(AutoLogin.class, Categories.MISC, "Auto-Login");
		addToggleCommand("autologin", "Automatically login on cracked servers);
		addToggleCommand("autologin generate",
				"Generate random password and copy to clipboard");
		CommandManager
				.createNewCommand()
				.setCommand("autologin generate")
				.setDescription(
						"Generate a random password and copy it to clipboard")
				.setExecutor(this)
				.setRequestArguments(
						new CommandManager.CommandValidator[] { new CommandManager.IntegerValidator() },
						new String[] { "length" }, false).register();
		instance = this;
		FMLCommonHandler.instance().bus().register(this);
	}

	private JScrollPane createGUI(String username) {
		JPanel panel = new JPanel(new GridLayout(0, 3));
		panel.add(new JLabel("IP of server"));
		panel.add(new JLabel("Password"));
		panel.add(new JLabel("Delete?"));
		HashMap<String, String> mapa = new HashMap<String, String>();
		for(Entry<String, String> entry : mapa.entrySet()) {
			JTextField ip = new JTextField(entry.getKey());
			JPasswordField pwd = new JPasswordField(entry.getKey());
		}
		return new JScrollPane(panel);
	}
	/*
	@Override
	public JComponent openConfig() { //TODO Make config
		JTabbedPane jtp = new JTabbedPane();
		for(String str : passwords.keySet()) {
			jtp.addTab(str, createGUI(str));
		}
		return jtp;
	}
	*/
	@Override
	public void resetConfig() {
		passwords=setObject("pwd", new HashMap<String, HashMap<String, String>>());
	}

	@Executor
	public void event(String cmd) {
		if(cmd.equals("autologin generate")) {
			event(cmd, 16);
		} else
			toggle();
	}
	@Executor
	public void event(String cmd, int len) {
		Random r = new Random();
		String password = "";
		for(int i=0; i<len; i++)
			password+=((char)(r.nextInt(126-33)+33));
		viewMessage("Generated password: "+password);
		GuiScreen.setClipboardString(password);
		viewMessage("Copied to clipboard!");
	}

	@Override
	protected boolean onEnable() {
		return true;
	}

	@Override
	protected boolean onDisable() {
		return true;
	}

	private String getPassword(String username, String address) {
		HashMap<String, String> pwd = passwords.get(username);
		System.out.println(pwd);
		if (pwd == null)
			return null;
		return pwd.get(address);
	}

	private void setPassword(String username, String address, String password) {
		HashMap<String, String> pwd = passwords.get(username);
		if (pwd == null)
			pwd = new HashMap<String, String>();
		if (!password.equals(pwd.put(address, password))) {
			viewMessage("Saved password!");
		}
		passwords.put(username, pwd);
		setObject("pwd", passwords);
	}

	public static void process(String str) {
		if (mc.isSingleplayer())
			return;
		if (mc.thePlayer == null)
			return;
		if (!instance.isEnabled())
			return;
		if (str.startsWith("/l ") || str.startsWith("/login ")) {
			String password = str.substring(str.indexOf(' ') + 1);
			instance.setPassword(mc.session.username,
					mc.currentServerData.serverIP, password);
		}
	}

	public static byte[] transform(byte[] basicClass) {
		ClassNode node = new ClassNode();
		ClassReader reader = new ClassReader(basicClass);
		reader.accept(node, 0);
		for (MethodNode mn : node.methods) {
			if (mn.name.equals("addToSentMessages")) {
				InsnList m = mn.instructions;
				MethodNode mv = new MethodNode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKESTATIC,
						"ovh/adiantek/hatari/mods/AutoLogin", "process",
						"(Ljava/lang/String;)V", false);
				m.insert(mv.instructions);
			}
		}
		ClassWriter cw = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
		node.accept(cw);
		return cw.toByteArray();
	}

	@SubscribeEvent
	public void event(FMLNetworkEvent.ClientConnectedToServerEvent e) {
		if (!e.isLocal && isEnabled()) {
			String pwd = getPassword(mc.session.username,
					mc.currentServerData.serverIP);
			if (pwd != null) {
				e.manager.scheduleOutboundPacket(new C01PacketChatMessage(
						"/login " + pwd));
			}
		}
	}
}
