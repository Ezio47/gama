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
package msi.gaml.statements;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

@symbol(name = { IKeyword.ASPECT }, kind = ISymbolKind.BEHAVIOR, with_sequence = true, unique_name = true)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = true) }, omissible = IKeyword.NAME)
public class AspectStatement extends AbstractStatementSequence {

	public static IExecutable DEFAULT_ASPECT = new IExecutable() {

		@Override
		public Rectangle2D executeOn(final IScope scope) throws GamaRuntimeException {
			IAgent agent = scope.getAgentScope();
			if ( agent != null ) {
				final IGraphics g = scope.getGraphics();
				if ( g == null ) { return null; }
				try {
					agent.acquireLock();
					if ( agent.dead() ) { return null; }
					if ( agent == GuiUtils.getHighlightedAgent() ) {
						g.beginHighlight();
					}
					final Color c =
						agent.getSpecies().hasVar(IKeyword.COLOR) ? Cast.asColor(scope,
							agent.getDirectVarValue(scope, IKeyword.COLOR)) : GamaPreferences.CORE_COLOR
							.getValue();
					IShape ag = agent.getGeometry();
					String defaultShape = GamaPreferences.CORE_SHAPE.getValue();
					if ( !defaultShape.equals("shape") ) {
						// Optimize this
						Double defaultSize = GamaPreferences.CORE_SIZE.getValue();
						ILocation point = agent.getLocation();
						if ( defaultShape.equals("circle") ) {
							ag = GamaGeometryType.buildCircle(defaultSize, point);
						} else if ( defaultShape.equals("square") ) {
							ag = GamaGeometryType.buildSquare(defaultSize, point);
						} else if ( defaultShape.equals("triangle") ) {
							ag = GamaGeometryType.buildTriangle(defaultSize, point);
						} else if ( defaultShape.equals("sphere") ) {
							ag = GamaGeometryType.buildSphere(defaultSize, point);
						} else if ( defaultShape.equals("cube") ) {
							ag = GamaGeometryType.buildCube(defaultSize, point);
						} else if ( defaultShape.equals("point") ) {
							ag = GamaGeometryType.createPoint(point);
						}
					}
					final IShape ag2 = (IShape) ag.copy(scope);
					final Rectangle2D r = g.drawGamaShape(scope, ag2, c, true, Color.black, 0, false);
					return r;
				} finally {
					g.endHighlight();
					agent.releaseLock();
				}
			}
			return null;
		}

		// @Override
		// public Rectangle2D drawOverlay(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		// return null;
		// }

	};

	public AspectStatement(final IDescription desc) {
		super(desc);
		setName(getLiteral(IKeyword.NAME, IKeyword.DEFAULT));
	}

	@Override
	// public Rectangle2D draw(final IScope scope, final IAgent agent) throws GamaRuntimeException {
	public Rectangle2D executeOn(final IScope scope) {
		IAgent agent = scope.getAgentScope();
		if ( agent != null ) {
			final IGraphics g = scope.getGraphics();
			if ( g == null ) { return null; }
			try {
				agent.acquireLock();
				if ( scope.interrupted() ) { return null; }
				if ( agent == GuiUtils.getHighlightedAgent() ) {
					g.beginHighlight();
				}
				return (Rectangle2D) super.executeOn(scope);
				// Object[] result = new Object[1];
				// if ( scope.execute(this, agent, null, result) && result[0] instanceof Rectangle2D ) { return
				// (Rectangle2D) result[0]; }
				// return null;
			} finally {
				g.endHighlight();
				agent.releaseLock();
			}

		}
		return null;

	}

	// @Override
	// public Rectangle2D drawOverlay(final IScope scope, final IAgent agent) throws GamaRuntimeException {
	// if ( agent != null ) {
	// final IGraphics g = scope.getGraphics();
	// if ( g == null ) { return null; }
	// try {
	// agent.acquireLock();
	// if ( agent.dead() ) { return null; }
	// final Color c =
	// agent.getSpecies().hasVar(IKeyword.COLOR) ? Cast.asColor(scope,
	// agent.getDirectVarValue(scope, IKeyword.COLOR)) : Color.YELLOW;
	// final IShape ag = agent.getGeometry();
	// final IShape ag2 = (IShape) ag.copy(scope);
	// final Rectangle2D r = g.drawGamaShapeOverlay(scope, ag2, c, true, Color.black, 0, false);
	// return r;
	// } finally {
	// agent.releaseLock();
	// }
	// }
	// return null;
	//
	// }

	@Override
	public Rectangle2D privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		Rectangle2D result = null;
		for ( int i = 0; i < commands.length; i++ ) {
			final Object c = commands[i].executeOn(stack);
			if ( result != null ) {
				if ( c instanceof Rectangle2D ) {
					result = result.createUnion((Rectangle2D) c);
				}
			} else if ( c instanceof Rectangle2D ) {
				result = (Rectangle2D) c;
			}
		}
		return result;
	}
}
