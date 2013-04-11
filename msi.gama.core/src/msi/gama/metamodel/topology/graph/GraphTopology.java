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
package msi.gama.metamodel.topology.graph;

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.topology.filter.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Maths;

/**
 * The class GraphTopology.
 * 
 * @author drogoul
 * @since 27 nov. 2011
 * 
 */
public class GraphTopology extends AbstractTopology {

	/**
	 * @param scope
	 * @param env
	 * @param torus
	 */
	public GraphTopology(final IScope scope, final IShape env, final GamaSpatialGraph graph) {
		super(scope, env, false);
		places = graph;
	}

	// The default topologies for graphs.
	public GraphTopology(final GamaSpatialGraph graph) {
		this(GAMA.getDefaultScope(), GAMA.getDefaultScope().getWorldScope().getGeometry(), graph);
	}

	@Override
	protected boolean canCreateAgents() {
		return true;
	}

	/**
	 * @throws GamaRuntimeException
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public IPath pathBetween(final IScope scope, final IShape source, final IShape target) {

		IShape edgeS = null, edgeT = null;

		IAgentFilter filter = In.edgesOf(getPlaces());

		edgeS =
			source instanceof ILocation ? getAgentClosestTo((ILocation) source, filter)
				: getAgentClosestTo(source, filter);
		edgeT =
			target instanceof ILocation ? getAgentClosestTo((ILocation) target, filter)
				: getAgentClosestTo(target, filter);
		if ( edgeS == null || edgeT == null ) { return null; }
		return pathBetweenCommon(edgeS, edgeT, source, target);

	}

	public IPath pathBetweenCommon(final IShape edgeS, final IShape edgeT, final IShape source,
		final IShape target) {
		if ( edgeS == edgeT ) { return new GamaPath(this, source, target, GamaList.with(edgeS)); }
		IShape s1 = null;
		IShape t1 = null;
		IShape s2 = null;
		IShape t2 = null;
		t1 = (IShape) getPlaces().getEdgeSource(edgeT);
		t2 = (IShape) getPlaces().getEdgeTarget(edgeT);
		s1 = (IShape) getPlaces().getEdgeSource(edgeS);
		s2 = (IShape) getPlaces().getEdgeTarget(edgeS);
		if ( t1 == null || t2 == null || s1 == null || s2 == null ) { return null; }
		IShape nodeT = t1;
		if ( t1.getLocation().euclidianDistanceTo(target.getLocation()) > t2.getLocation()
			.euclidianDistanceTo(target.getLocation()) ) {
			nodeT = t2;
		}

		Object nodeS = s1;
		if ( s1 == nodeT ||
			s2 != nodeT &&
			s1.getLocation().euclidianDistanceTo(source.getLocation()) > s2.getLocation()
				.euclidianDistanceTo(source.getLocation()) ) {
			nodeS = s2;
		}
		IList<IShape> edges = getPlaces().computeBestRouteBetween(nodeS, nodeT);

		if ( edges.isEmpty() || edges.get(0) == null ) { return null; }
		HashSet edgesSetInit =
			new HashSet(Arrays.asList(edges.get(0).getInnerGeometry().getCoordinates()));
		HashSet edgesSetS = new HashSet(Arrays.asList(edgeS.getInnerGeometry().getCoordinates()));
		if ( !edgesSetS.equals(edgesSetInit) ) {
			edges.add(0, edgeS);
		}
		HashSet edgesSetEnd =
			new HashSet(Arrays.asList(edges.get(edges.size() - 1).getInnerGeometry()
				.getCoordinates()));
		HashSet edgesSetT = new HashSet(Arrays.asList(edgeT.getInnerGeometry().getCoordinates()));

		if ( !edgesSetT.equals(edgesSetEnd) ) {
			edges.add(edgeT);
		}
		return new GamaPath(this, source, target, edges);
	}

	@Override
	public IPath pathBetween(IScope scope, final ILocation source, final ILocation target) {
		IShape edgeS = null, edgeT = null;

		if ( !this.getPlaces().getEdges().isEmpty() ) {
			if ( this.getPlaces() instanceof GamaSpatialGraph &&
				!((GamaSpatialGraph) this.getPlaces()).isAgentEdge() ) {
				double distMinT = Double.MAX_VALUE;
				double distMinS = Double.MAX_VALUE;
				for ( IShape shp : this.getPlaces().getEdges() ) {
					double distS = shp.euclidianDistanceTo(source);
					double distT = shp.euclidianDistanceTo(target);
					if ( distS < distMinS ) {
						distMinS = distS;
						edgeS = shp;
					}
					if ( distT < distMinT ) {
						distMinT = distT;
						edgeT = shp;
					}
				}
			} else {
				IAgentFilter filter = In.edgesOf(getPlaces());
				edgeS = getAgentClosestTo(source, filter);
				edgeT = getAgentClosestTo(target, filter);
			}
		}
		return pathBetweenCommon(edgeS, edgeT, source, target);
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return "GraphTopology";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml() {
		return "GraphTopology";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy(IScope scope) {
		return new GraphTopology(scope, environment, (GamaSpatialGraph) places);
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#getRandomPlace()
	 */

	@Override
	public ISpatialGraph getPlaces() {
		return (GamaSpatialGraph) super.getPlaces();
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final ILocation p) {
		return isValidGeometry(p.getGeometry());
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IShape g) {
		// Geometry g2 = g.getInnerGeometry();
		for ( IShape g1 : places ) {
			if ( g1.intersects(g) ) { return true; }
			// TODO covers or intersects ?
		}
		return false;
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#distanceBetween(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry, java.lang.Double)
	 */
	@Override
	public Double distanceBetween(IScope scope, final IShape source, final IShape target) {
		IPath path = this.pathBetween(scope, source, target);
		if ( path == null ) { return Double.MAX_VALUE; }
		return path.getDistance(scope);
	}

	@Override
	public Double distanceBetween(IScope scope, final ILocation source, final ILocation target) {
		IPath path = this.pathBetween(scope, source, target);
		if ( path == null ) { return Double.MAX_VALUE; }
		return path.getDistance(scope);
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#directionInDegreesTo(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Integer directionInDegreesTo(IScope scope, final IShape source, final IShape target) {
		IPath path = this.pathBetween(scope, source, target);
		if ( path == null ) { return null; }
		// LineString ls = (LineString) path.getEdgeList().first().getInnerGeometry();
		// TODO Check this
		final double dx = target.getLocation().getX() - source.getLocation().getX();
		final double dy = target.getLocation().getY() - source.getLocation().getY();
		final double result = Maths.atan2(dy, dx);
		return Maths.checkHeading((int) result);
	}

	/**
	 * @see msi.gama.environment.ITopology#getAgentsIn(msi.gama.interfaces.IGeometry,
	 *      msi.gama.environment.IAgentFilter, boolean)
	 */
	@Override
	public IList<IAgent> getAgentsIn(final IShape source, final IAgentFilter f,
		final boolean covered) {
		List<IAgent> agents = super.getAgentsIn(source, f, covered);
		GamaList<IAgent> result = new GamaList();
		for ( IAgent ag : agents ) {
			if ( !ag.dead() && isValidGeometry(ag) ) {
				result.add(ag);
			}
		}
		return result;
	}

	@Override
	public boolean isTorus() {
		// TODO Auto-generated method stub
		return false;
	}
}
