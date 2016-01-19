/*********************************************************************************************
 *
 *
 * 'MyTexture.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.vividsolutions.jts.geom.Polygon;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.util.ImageUtils;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import ummisco.gama.opengl.files.GLModel;
import ummisco.gama.opengl.files.ModelLoaderOBJ;

public class GeometryCache {

	private  final Map<String, Integer> GEOMETRIES = new ConcurrentHashMap(100, 0.75f, 1);
	private  final GeometryAsyncBuilder BUILDER ;
	
	 Integer openNestedGLListIndex;
	private  GLUT myGlut = new GLUT();
	
	public GeometryCache(GL2 gl){
		BUILDER = new GeometryAsyncBuilder(gl);
	}
	// Assumes the texture has been created. But it may be processed at the time
	// of the call, so we wait for its availability.
	public  Integer getListIndex(final GL gl, final String file) {
		if ( file == null ) { return null; }
		Integer index = GEOMETRIES.get(file);
		while (index == null) {
			index = GEOMETRIES.get(file);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return index;
	}

	public  void initializeStaticGeometry(final GamaFile file) {

	    if ( contains(file.getFile().getAbsolutePath().toString()) ) { return; }	
		
		
		
		
		BuildingTask task = new BuildingTask(null, file);
		BUILDER.tasks.offer(task);
	}

	/**
	 * @param image
	 * @return
	 */
	public  boolean contains(final String file) {
		return GEOMETRIES.containsKey(file);
	}

	public  Integer buildList(final GL gl, final GamaFile string) {
		Integer index = openNestedGLListIndex; 
		
		if ( index == null ) {	
			String obj = string.getFile().toString();
			String fmtl = string.getFile().getAbsolutePath().replaceAll(".obj", ".mtl");
			GLModel asset3Dmodel = ModelLoaderOBJ.LoadModel(obj, fmtl, (GL2) gl);
			index = ((GL2) gl).glGenLists(1);
			((GL2) gl).glNewList(index, GL2.GL_COMPILE);
			asset3Dmodel.draw((GL2) gl);		
			((GL2) gl).glEndList();
		}
		((GL2) gl).glCallList(index);
		openNestedGLListIndex = index;
		return openNestedGLListIndex;
	}


	public  class GeometryAsyncBuilder implements Runnable {

		final LinkedBlockingQueue<GLTask> tasks = new LinkedBlockingQueue<GLTask>();
		final Thread loadingThread;
		protected GL2 gl;

		public GeometryAsyncBuilder(GL2 gl) {
			this.gl = gl;
			loadingThread = new Thread(this, "Geometry building thread");
			loadingThread.start();
		}

		@Override
		public void run() {
			final ArrayList<GLTask> copy = new ArrayList();
			while (true) {
				tasks.drainTo(copy);
				try {
					gl.getContext().makeCurrent();
					for ( GLTask currentTask : copy ) {
						currentTask.runIn(gl);
					}

				} finally {
						gl.getContext().release();
			}}
		}

	}

	protected interface GLTask {

		abstract void runIn(GL gl);
	}

	protected class DestroyingTask implements GLTask {

		final int[] geometryIds;

		DestroyingTask(final int[] geometryIds) {
			this.geometryIds = geometryIds;
		}

		@Override
		public void runIn(final GL gl) {
			gl.glDeleteTextures(geometryIds.length, geometryIds, 0);
		}

	}

	protected  class BuildingTask implements GLTask {

		protected final GamaFile string;


		BuildingTask(final GL2 gl, final GamaFile file) {
			this.string = file;

		}

		@Override
		public void runIn(final GL gl) {
			if ( contains(string.getFile().getAbsolutePath().toString()) ) { return; }
			Integer index = buildList(gl, string);
			// We use the original image to keep track of the texture
			if ( index != null ) {
				GEOMETRIES.put(string.getFile().getAbsolutePath().toString(), index);
			}
			gl.glFinish();	
		}
	}
}