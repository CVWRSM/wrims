package wrimsv2_plugin.tools;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;

public class ProcImage {
	static HashMap<ImageDescriptor, Image> imageCache=new HashMap<ImageDescriptor, Image>();
	
	public static Image getControlImage(){
		ImageDescriptor descriptor = null;
		descriptor = DebugCorePlugin.getImageDescriptor("control_icon.png");

		//obtain the cached image corresponding to the descriptor
		Image image = (Image)imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}
	
	public static void disposeImages(){
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}
}
