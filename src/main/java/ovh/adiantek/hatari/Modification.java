package ovh.adiantek.hatari;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;

import ovh.adiantek.hatari.windows.ActiveMods;
import ovh.adiantek.hatari.windows.Categories;

public class Modification extends Configurator {
	public static final Logger LOG = LogManager.getLogger("H");
	private boolean isEnabled = false;
	final String name;
	public String help;
	protected static Minecraft mc;
	private static Configurator staticConf = new Configurator(Modification.class);
	static ArrayList<Modification> modifications = new ArrayList<Modification>();
	public Modification(Class<?> parent, String name) {
		super(parent);
		if(getClass()!=parent) {
			throw new RuntimeException("Invalid class declaration in modification");
		}
		this.name=name;
		
		modifications.add(this);
		if(staticConf.getBoolean("isEnabled_"+getClass().getName(), false)) {
			enable();
		}
		if(mc==null)
			mc=Minecraft.getMinecraft();
	}
	public Modification(Class<?> parent, Categories cat, String name) {
		super(parent);
		cat.addModification(name, this);
		this.name=name;
		modifications.add(this);
		if(staticConf.getBoolean("isEnabled_"+getClass().getName(), false)) {
			enable();
		}
		if(mc==null)
			mc=Minecraft.getMinecraft();
	}
	public JComponent openConfig() {
		return null;
	}
	public void resetConfig() {}
	protected void save() {}
	static void saveAll() {
		for(Modification m : modifications) {
			try {
				LOG.info("Saving: "+m);
				m.save();
				staticConf.setBoolean("isEnabled_"+m.getClass().getName(), m.isEnabled);
			} catch(Throwable t) {
				LOG.error("Error while saving "+m.name+" ("+m+")", t);
			}
		}
	}
	protected boolean onEnable() {
		return false;
	}
	protected boolean onDisable() {
		return false;
	}
	protected final void setEnabledState(boolean isEnabled) {
		this.isEnabled=isEnabled;
		ActiveMods.update();
	}
	
	public final boolean enable() {
		if(isEnabled)
			return false;
		try {
			if(onEnable()) {
				isEnabled=true;
				viewMessage("Enabled "+name+"!");
				ActiveMods.update();
				return true;
			} else {
				LOG.error("Failed enable "+this+"!");
			}
		} catch(Throwable t) {
			LOG.error("Error while enabling "+this, t);
		}
		return false;
	}
	public final boolean disable() {
		if(!isEnabled)
			return false;
		try {
			if(onDisable()) {
				isEnabled=false;
				viewMessage("Disabled "+name+"!");
				ActiveMods.update();
				return true;
			} else {
				LOG.error("Failed disable "+this+"!");
			}
		} catch(Throwable t) {
			LOG.error("Error while disabling "+this, t);
		}
		return false;
	}
	public final boolean toggle() {
		return isEnabled?disable():enable();
	}
	public final boolean isEnabled() {
		return isEnabled;
	}
	public static final void viewMessage(String s, Object... format) {
			try {
			s=String.format(s, format);
			} catch(Throwable t){
				
			}
		for(String st : s.split("\n")) {
			LOG.info(ChatColor.stripColor(s));
			try {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new HatariChatComponentText(ChatColor.AQUA+"[H]"+ChatColor.RESET+" "+st.replace("\t", "    ")));
			}catch(Throwable t){}
		}
	}
	public static EntityPlayer findPlayer(String username) {
		List<EntityPlayer> players = Minecraft.getMinecraft().theWorld.playerEntities;
		for(EntityPlayer ep : players) {
			if(ep.getCommandSenderName().equals(username)) {
				return ep;
			}
		}
		for(EntityPlayer ep : players) {
			if(ep.getCommandSenderName().equalsIgnoreCase(username)) {
				return ep;
			}
		}
		for(EntityPlayer ep : players) {
			try {
			if(ep.getCommandSenderName().matches(username)) {
				return ep;
			}} catch(Throwable t){}
		}
		for(EntityPlayer ep : players) {
			if(ep.getCommandSenderName().contains(username)) {
				return ep;
			}
		}
		for(EntityPlayer ep : players) {
			if(ep.getCommandSenderName().toLowerCase().contains(username.toLowerCase())) {
				return ep;
			}
		}
		return null;
	}
	protected void addToggleCommand(String command, String help) {
		CommandManager
				.createNewCommand()
				.setCommand(command)
				.setExecutor(this)
				.setDescription(help)
				.setRequestArguments(
						new CommandManager.CommandValidator[] {}, new String[] {}, false)
				.register();
		if(command.split(" ", 2).length==1)
			this.help=help;
	}
	public void hide(){}
	public void show(){}
	public final static boolean isShouldVisible() {
		return Hatari.visible;
	}
	public final static int getNextButtonID() {
		return Hatari.buttonCounter++;
	}
	@Retention(value = RUNTIME)
	@Target(value = METHOD)
	public @interface Executor {}
}