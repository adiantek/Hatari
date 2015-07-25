package ovh.adiantek.hatari;

import java.awt.Desktop;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UpdateCheck extends Thread {
	public static String newVersion = Hatari.VERSION;
	public static String newUrl = null;

	private UpdateCheck() {
		super("Hatari Update Checker");
		start();
	}

	public Integer versionCompare(String str1, String str2) {
		String[] vals1 = str1.split("\\.");
		String[] vals2 = str2.split("\\.");
		int i = 0;
		while (i < vals1.length && i < vals2.length
				&& vals1[i].equals(vals2[i])) {
			i++;
		}
		if (i < vals1.length && i < vals2.length) {
			int diff = Integer.valueOf(vals1[i]).compareTo(
					Integer.valueOf(vals2[i]));
			return Integer.signum(diff);
		} else {
			return Integer.signum(vals1.length - vals2.length);
		}
	}

	private void checkFile(String name, String url) {
		if (!name.endsWith(".jar"))
			return;
		if (!name.startsWith("hatari-"))
			return;
		name = name.substring(7);
		name = name.substring(0, name.length() - 4);
		Integer compare = versionCompare(newVersion, name);
		if (compare < 0) {
			newVersion = name;
			newUrl = url;
		}
		return;
	}

	public void run() {
		try {
			URL u = new URL("https://api.github.com/repos/adiantek/Hatari/contents/build/libs");
			JsonElement jelement = new JsonParser().parse(new InputStreamReader(u.openStream(), Charset.forName("UTF-8")));
			Iterator<JsonElement> i = jelement.getAsJsonArray().iterator();
			JsonObject el;
			while(i.hasNext()) {
				try {
					el=i.next().getAsJsonObject();
					checkFile(el.get("name").getAsString(), el.get("download_url").getAsString());
				} catch(Throwable t2) {
					t2.printStackTrace();
				}
			}
			checkFile(String.format("hatari-%s.jar", Hatari.VERSION), null);
			if(newUrl!=null) {
		        JOptionPane pane = new JOptionPane("Your version of Hatari ("+Hatari.VERSION+") is old. The newest version is "+newVersion+". Do you want download update?", JOptionPane.QUESTION_MESSAGE,
                        JOptionPane.YES_NO_OPTION);
				JDialog dialog = pane.createDialog("Update is avaiable");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
				dialog.dispose();
				if(pane.getValue()!=null) {
					if(pane.getValue() instanceof Integer && pane.getValue()==Integer.valueOf(0)) {
						try {
							Desktop.getDesktop().browse(
									new URI(newUrl));
						} catch (Throwable throwable) {
							System.err.println("Couldn\'t open link");
							throwable.printStackTrace();
						}
					}
				}
			}
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

	public static void check() {
	}

	static {
		new UpdateCheck();
	}
}
