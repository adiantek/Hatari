package ovh.adiantek.hatari.mods;

import java.net.SocketAddress;
import java.util.IdentityHashMap;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.client.C01PacketChatMessage;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

//TODO End this
public class AutoLogin extends Modification {
	private IdentityHashMap<SocketAddress, String> passwords = new IdentityHashMap<SocketAddress, String>();
	public AutoLogin() {
		super(AutoLogin.class, Categories.MISC, "Auto-Login");
		addToggleCommand("autologin", "Automatically login on server");
		FMLCommonHandler.instance().bus().register(this);
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
	public void onLogin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
		if(!e.isLocal && isEnabled()) {
			String pwd = passwords.get(e.manager.getSocketAddress());
			if(pwd!=null) {
				new GuiChat().func_146403_a("/login "+pwd);
			}
			
		}
		
	}
}
