package ovh.adiantek.hatari.mods;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.minecraft.client.Minecraft;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import ovh.adiantek.hatari.Modification;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.handshake.FMLHandshakeMessage.ModList;

public class HideMods extends Modification implements ItemListener {
	private static HideMods instance;
	private static final String LABEL_MODS = "It will show in console: Client attempting to join with %s mods : %s";
	private JLabel mods_ = new JLabel("");
	private TreeMap<String, Boolean> mods = getObject("md", new TreeMap<String, Boolean>());
	public HideMods() {
		super(HideMods.class, "Hide mods");
		instance=this;
	}
	
	public String toString(ModContainer mod) {
		return mod.getModId();
	}
	private void reloadMods() {
		for(ModContainer mod : Loader.instance().getActiveModList()) {
			String str = toString(mod);
			if(!mods.containsKey(str)) {
				mods.put(str, !mod.getModId().equalsIgnoreCase("hatari"));
			}
		}
	}
	@Override
	public JComponent openConfig() {
		reloadMods();
		JPanel main = new JPanel(new BorderLayout());
		JPanel lista = new JPanel(new GridLayout(0,1));
		boolean isFirst = true;
		for(Map.Entry<String, Boolean> mod : mods.entrySet()){
			if(isFirst) {
				lista.add(new JLabel("Installed mods:"));
				isFirst=false;
			}
			JCheckBox jcb = new JCheckBox(mod.getKey());
			jcb.setSelected(mods.get(jcb.getText()));
			jcb.addItemListener(this);
			lista.add(jcb);
		}
		ModList list = new ModList(Loader.instance().getActiveModList());
		main.add(new JScrollPane(lista), BorderLayout.CENTER);
		main.add(mods_=new JLabel(), BorderLayout.SOUTH);
		mods_.setText(String.format(LABEL_MODS, list.modListSize(), list.modListAsString()));
		return main;
	}
	@Override
	public void resetConfig() {
		mods=new TreeMap<String, Boolean>();
	}
	@Override
	protected void save() {
		setObject("md", mods);
		
	}
	public static void call(ModList list) {
		instance.reloadMods();
		Map<String, String> mapa = list.modList();
		HashMap<String, String> newMap = new HashMap<String, String>(){

			@Override
			public void putAll(Map<? extends String, ? extends String> m) {
				for(Map.Entry<? extends String, ? extends String> mp : m.entrySet()) {
					put(mp.getKey(), mp.getValue());
				}
			}

			@Override
			public String putIfAbsent(String key, String value) {
				String oldValue = get(key);
				if(oldValue==null) {
					put(key, value);
				}
				return oldValue;
			}

			@Override
			public String put(String key, String value) {
				if(Minecraft.getMinecraft().isSingleplayer()) {
					return super.put(key, value);
				}
				String oldValue = get(key);
				Boolean val = HideMods.instance.mods.get(key);
				if(val==null) {
					val=!key.equals("hatari");
				}
				if(val) {
					return super.put(key, value);
				}
				return oldValue;
			}
			
		};
		newMap.putAll(mapa);
		
		try {
			Field f = list.getClass().getDeclaredField("modTags");
			f.setAccessible(true);
			f.set(list, newMap);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static byte[] transform(byte[] b) {
		ClassNode node = new ClassNode();
		ClassReader reader = new ClassReader(b);
		reader.accept(node, 0);
		for(MethodNode mn : node.methods) {
			if(mn.name.equals("<init>")) {
				MethodNode mv = new MethodNode();
				Label l0 = new Label();
				mv.visitCode();
				mv.visitLabel(l0);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESTATIC, "ovh/adiantek/hatari/mods/HideMods", "call", "(Lcpw/mods/fml/common/network/handshake/FMLHandshakeMessage$ModList;)V", false);
				mv.visitEnd();
				InsnList m = mn.instructions;
				for(int i=0; i<m.size(); i++) {
					if(m.get(i).getOpcode()==RETURN) {
						m.insertBefore(m.get(i), mv.instructions);
						break;
					}
				}
			}
		}
		ClassWriter cw = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS
				| ClassWriter.COMPUTE_FRAMES);
		node.accept(cw);
		return cw.toByteArray();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox cb = (JCheckBox) e.getSource();
		mods.put(cb.getText(), cb.isSelected());
		reloadMods();
		ModList list = new ModList(Loader.instance().getActiveModList());
		mods_.setText(String.format(LABEL_MODS, list.modListSize(), list.modListAsString()));
		
	}
}
