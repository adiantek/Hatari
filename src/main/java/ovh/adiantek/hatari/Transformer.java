package ovh.adiantek.hatari;

import javax.swing.JOptionPane;

import ovh.adiantek.hatari.mods.AutoFish;
import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName,
			byte[] basicClass) {
		if(name.equals("net.minecraft.network.play.server.S12PacketEntityVelocity") || transformedName.equals("net.minecraft.network.play.server.S12PacketEntityVelocity")) {
			System.out.println(name);
			return AutoFish.transform(basicClass);
		}
		return basicClass;
	}
}
