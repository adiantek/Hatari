package ovh.adiantek.hatari;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import ovh.adiantek.hatari.mods.AntiInvisible;
import ovh.adiantek.hatari.mods.AutoArmor;
import ovh.adiantek.hatari.mods.AutoFish;
import ovh.adiantek.hatari.mods.AutoRespawn;
import ovh.adiantek.hatari.mods.AutoSign;
import ovh.adiantek.hatari.mods.AutoTool;
import ovh.adiantek.hatari.mods.AutoWalk;
import ovh.adiantek.hatari.mods.ChangeUsername;
import ovh.adiantek.hatari.mods.Console;
import ovh.adiantek.hatari.mods.Fly;
import ovh.adiantek.hatari.mods.Friends;
import ovh.adiantek.hatari.mods.HighJump;
import ovh.adiantek.hatari.mods.InvSee;
import ovh.adiantek.hatari.mods.KillAura;
import ovh.adiantek.hatari.mods.NoHurtShakes;
import ovh.adiantek.hatari.mods.Say;
import ovh.adiantek.hatari.mods.SelfKick;
import ovh.adiantek.hatari.windows.ActiveMods;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Mod(modid = Hatari.MODID, version = Hatari.VERSION)
@TransformerExclusions({"ovh.adiantek.hatari"})
public class Hatari implements IFMLLoadingPlugin {
	@Instance("hatari")
	public static Hatari instance;
	static boolean visible = true;
	public static final PrintStream out = System.out;
	public static final PrintStream err = System.err;
	static int buttonCounter = 10000;
	private boolean lastClicked = false;
	public static final String MODID = "hatari";
	public static final String VERSION = "1.0";
	private static TreeMap<String, Modification> commands = new TreeMap<String, Modification>();
	@EventHandler
    public void preInit(FMLPreInitializationEvent event) {
		Modification.loadConfig(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(GuiHatariGame.instance);
    }
	@EventHandler
	public void init(FMLInitializationEvent event) {
	    Categories.init();
		new ActiveMods();
		initMods();
		FMLCommonHandler.instance().bus().register(this);
	}
	private boolean isPressedF4 = false;
	private boolean isPressedH = false;
	private void toggleVisible() {
		visible=!visible;
		if(!visible) {
			GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
			if(gi==null)
				return;
			GuiNewChat gnc = gi.getChatGUI();
			if(gnc==null)
				return;
	        Iterator<ChatLine> iterator = gnc.chatLines.iterator();
	        ChatLine chatline;
	        while (iterator.hasNext()) {
	            chatline = iterator.next();
	            if(chatline.func_151461_a() instanceof HatariChatComponentText) {
	            	iterator.remove();
	            }
	        }
			gnc.refreshChat();
		}
	}
	@SubscribeEvent
	public void event(InputEvent.KeyInputEvent tick) {
		int key = Keyboard.getEventKey();
		if(key==Keyboard.KEY_F4)
			isPressedF4=Keyboard.getEventKeyState();
		else if(key==Keyboard.KEY_H)
			isPressedH=Keyboard.getEventKeyState();
		if(isPressedF4 && isPressedH)
			toggleVisible();
	}
	public void initMods() {
		new AntiInvisible();
		new AutoArmor();
		new AutoFish();
		new AutoRespawn();
		new AutoSign();
		new AutoTool();
		new AutoWalk();
		new ChangeUsername();
		new Console();
		new Fly();
		new Friends();
		new HighJump();
		new InvSee();
		new KillAura();
		new NoHurtShakes();
		new Say();
		new SelfKick();
		new SettingsWindow.SettingsMod();
	}
	public static void addCommand(String command, Modification object) {
		commands.put(command, object);
	}
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"ovh.adiantek.hatari.AccessTransformer"};
	}
	@Override
	public String getModContainerClass() {
		return null;
	}
	@Override
	public String getSetupClass() {
		return null;
	}
	@Override
	public void injectData(Map<String, Object> data) {
	}
	@Override
	public String getAccessTransformerClass() {
		return "ovh.adiantek.hatari.Transformer";
	}
	private static class ConsoleThread extends Thread {
		ConsoleThread() {
			super("Console");
		}
		public void run() {
			String charset = "UTF-8";
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in, Charset.forName(charset)));
			Modification.viewMessage("Console is enabled! Current encoding: %s", charset);
			while(true) {
				String line;
				try {
					line = br.readLine();
					if(line!=null) {
						if(line.toLowerCase().equals("help") || line.toLowerCase().startsWith("help ")) {
							String add = line.substring(4);
							if(add.startsWith(" "))
								add=add.substring(1);
							ArrayList<String> lista = CommandManager.getHelpForPrefix(add);
							for(String ln : lista) {
								Modification.viewMessage(ln);
							}
						} else if(!CommandManager.invoke(line)) {
							Modification.LOG.error("Command not found: "+line);
							Modification.LOG.error("Type \"help\" for help.");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	static {
		Modification.viewMessage("Starting console thread...");
		Console.init();
		new ConsoleThread().start();
	}
}
