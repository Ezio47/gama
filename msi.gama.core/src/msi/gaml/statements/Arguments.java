/*********************************************************************************************
 * 
 *
 * 'Arguments.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gama.metamodel.agent.IAgent;

/**
 * @author drogoul
 */
public class Arguments extends Facets {

	/*
	 * The caller represents the agent in the context of which the arguments need to be evaluated.
	 */
	IAgent caller;

	public Arguments() {}

	public Arguments(final IAgent caller) {
		this.caller = caller;
	}

	public Arguments(final Arguments args) {
		super(args);
		this.caller = args.caller;

		// for ( final Map.Entry<String, IExpressionDescription> entry : args.entrySet() ) {
		// if ( entry != null ) {
		// put(entry.getKey(), entry.getValue());
		// }
		// }
	}

	public void setCaller(final IAgent caller) {
		this.caller = caller;
	}

	public IAgent getCaller() {
		return caller;
	}

}
