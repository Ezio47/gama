package msi.gama.jogl.utils.collada;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import msi.gama.jogl.scene.GeometryObject;

import com.jmex.model.collada.ColladaImporter;



public class ColladaReader {
 
	public ColladaReader(){
		System.out.println("ColladaReader");
		InputStream input = null;
		try {
			input = new FileInputStream("/Users/Arno/Projects/Gama/Sources/GAMA_CURRENT2/msi.gama.jogl/src/collada/astroBoy.DAE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		ColladaImporter.load(input, "model");
    
				
		/*ArrayList<String> geometryNames = ColladaImporter.getGeometryNames();
		
		Iterator<String> it = geometryNames.iterator();
		while (it.hasNext()) {
		System.out.println( it);
		}*/
	}
}
	