/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.filter;

import java.util.*;
import msi.gama.metamodel.shape.*;
import msi.gama.util.GamaList;
import msi.gaml.species.ISpecies;

public class Different implements IAgentFilter {

	private static final Different instance = new Different();

	public static Different with() {
		return instance;
	}

	@Override
	public boolean accept(final IShape source, final IShape a) {
		return a.getGeometry() != source.getGeometry();
	}

	/**
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#filter(msi.gama.metamodel.shape.IShape,
	 *      java.util.List)
	 */
	@Override
	public List<? extends IShape> filter(final IShape source, final List<? extends IShape> ags) {
		List<IShape> result = new GamaList(ags.size());
		for ( IShape s : ags ) {
			if ( accept(source, s) ) {
				result.add(s);
			}
		}
		return result;
	}

	@Override
	public boolean filterSpecies(final ISpecies s) {
		return false;
	}

	/**
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#accept(msi.gama.metamodel.shape.ILocation,
	 *      msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean accept(final ILocation source, final IShape a) {
		return !a.getLocation().equals(source);
	}

	/**
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#filter(msi.gama.metamodel.shape.ILocation,
	 *      java.util.List)
	 */
	@Override
	public List<? extends IShape> filter(final ILocation source, final List<? extends IShape> ags) {
		List<IShape> result = new GamaList(ags.size());
		for ( IShape s : ags ) {
			if ( accept(source, s) ) {
				result.add(s);
			}
		}
		return result;
	}

	/**
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getShapes()
	 */
	@Override
	public Collection<? extends IShape> getShapes() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#identicalTo(msi.gama.metamodel.topology.filter.IAgentFilter)
	 */
	@Override
	public boolean identicalTo(final IAgentFilter f) {
		return f instanceof Different;
	}

	/**
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getSize()
	 */
	@Override
	public int getSize() {
		return 0;
	}

}