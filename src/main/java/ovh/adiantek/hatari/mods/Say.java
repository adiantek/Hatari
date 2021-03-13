package ovh.adiantek.hatari.mods;

import net.minecraft.client.gui.GuiChat;
import ovh.adiantek.hatari.CommandManager;
import ovh.adiantek.hatari.Modification;

public class Say extends Modification {
	public Say() {
		super(Say.class, "Say");
		CommandManager
				.createNewCommand()
				.setCommand("say")
				.setDescription("Use this to say something without tripping the command interpreter.")
				.setExecutor(this)
				.setRequestArguments(
						new CommandManager.CommandValidator[] { new CommandManager.StringValidator() },
						new String[]{"msg"}, true).register();
	}
	@Executor
	public void event(String command, String... msg) {
		StringBuilder sb = new StringBuilder();
		
		for(String m : msg) {
			sb.append(' ');
			sb.append(m);
		}
		String s = sb.toString();
		if(s.length()>0)s=s.substring(1);
		if(s.length()>100) {
			viewMessage("Warning: Chat message is limited to 100 characters.");
		}
		GuiChat gc = new GuiChat();
		gc.mc=mc;
		
		gc.func_146403_a(s);
	}
}
