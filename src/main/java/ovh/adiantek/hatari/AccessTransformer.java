package ovh.adiantek.hatari;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AccessTransformer extends
		cpw.mods.fml.common.asm.transformers.AccessTransformer {
	public AccessTransformer() throws IOException {
		super("META-INF/modid_at.cfg");
	}
}
