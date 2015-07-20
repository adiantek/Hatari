package ovh.adiantek.hatari;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AccessTransformer extends
		cpw.mods.fml.common.asm.transformers.AccessTransformer {
	private static AccessTransformer instance;
	private static List mapFiles = new LinkedList();
	public AccessTransformer() throws IOException {
		super();
		instance = this;
		mapFiles.add("META-INF/modid_at.cfg");
		Iterator it = mapFiles.iterator();
		while (it.hasNext()) {
			String file = (String) it.next();
			this.readMapFile(file);
		}
	}
	public static void addTransformerMap(String mapFileName) {
		if (instance == null) {
			mapFiles.add(mapFileName);
		} else {
			instance.readMapFile(mapFileName);
		}
	}
	private void readMapFile(String name) {
		try {
			Method e = cpw.mods.fml.common.asm.transformers.AccessTransformer.class.getDeclaredMethod("readMapFile",
					new Class[] { String.class });
			e.setAccessible(true);
			e.invoke(this, new Object[] { name });
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
