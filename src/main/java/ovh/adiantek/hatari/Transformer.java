package ovh.adiantek.hatari;

import static org.objectweb.asm.Opcodes.*;

import javax.swing.JOptionPane;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import ovh.adiantek.hatari.mods.AutoFish;
import ovh.adiantek.hatari.mods.AutoLogin;
import ovh.adiantek.hatari.mods.HideMods;
import cpw.mods.fml.common.network.handshake.FMLHandshakeMessage.ModList;

public class Transformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName,
			byte[] basicClass) {
		if(name.equals("net.minecraft.network.play.server.S12PacketEntityVelocity") || transformedName.equals("net.minecraft.network.play.server.S12PacketEntityVelocity")) {
			return AutoFish.transform(basicClass);
		} else if(name.equals("cpw.mods.fml.common.network.handshake.FMLHandshakeMessage$ModList")||transformedName.equals("cpw.mods.fml.common.network.handshake.FMLHandshakeMessage$ModList")) {
			return HideMods.transform(basicClass);
		} else if(name.contains("GuiNewChat")||transformedName.contains("GuiNewChat")) {
			return AutoLogin.transform(basicClass);
			//	throw new RuntimeException();
		}
		return basicClass;
	}
}
