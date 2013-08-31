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
package msi.gama.precompiler;

import java.lang.annotation.*;

/**
 * The Class GamlAnnotations. GamlAnnotations contains several annotation classes that are used to
 * tag the methods and classes that will be used by GAML for instatiating its basic elements
 * (statements, species, skills, variables, operators, etc.). These annotations are parsed at
 * compile-time to create the corresponding GAML elements. Some are used at runtime as well.
 * 
 * @author Alexis Drogoul
 * @since 8 juin 07
 * 
 * 
 */

public final class GamlAnnotations {

	@Retention(RetentionPolicy.SOURCE)
	@Target(ElementType.TYPE)
	@Inherited
	public static @interface display {

		/**
		 * The keyword that will allow to open this display in GAML (in "display type: keyword").
		 * @return
		 */
		String value();
	}

	/**
	 * 
	 * The class facets. Describes a list of facet used by a symbol (a statement, a declaration) in
	 * GAML. Can only be declared in classes annotated with symbol
	 * 
	 * @author drogoul
	 * @since 2 juin 2012
	 * 
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(ElementType.TYPE)
	@Inherited
	public static @interface facets {

		/**
		 * Value.
		 * 
		 * @return an Array of @facet, each representing a facet name, type..
		 */
		facet[] value();

		/**
		 * Combinations.
		 * 
		 * @return The different combinations of facets that are allowed. Not completely functional
		 *         yet.
		 */
		combination[] combinations() default {};

		/**
		 * Ommissible.
		 * 
		 * @return the facet that can be safely omitted by the modeler (provided its value is the
		 *         first following the keyword of the statement).
		 */
		String omissible() default "name";

	}

	/**
	 * 
	 * The class facet. Describes a facet in a list of facets
	 * 
	 * @see facets
	 * @author drogoul
	 * @since 2 juin 2012
	 * 
	 */
	@Retention(RetentionPolicy.SOURCE)
	public static @interface facet {

		/**
		 * Name.
		 * 
		 * @return the name of the facet. Must be unique within a symbol.
		 */
		String name();

		/**
		 * Type.
		 * 
		 * @return The string values of the different types that can be taken by this facet.
		 * @see msi.gaml.types.IType
		 */

		int[] type();

		/**
		 * Values.
		 * 
		 * @return the values that can be taken by this facet. The value of the facet expression
		 *         will be chosen among the values described here
		 */
		String[] values() default {};

		/**
		 * Optional.
		 * 
		 * @return whether or not this facet is optional or mandatory.
		 */

		boolean optional() default false;

		/**
		 * Doc.
		 * 
		 * @return the documentation associated to the facet.
		 * @see doc
		 */
		doc[] doc() default {};
	}

	@Retention(RetentionPolicy.SOURCE)
	public static @interface combination {

		/**
		 * Value.
		 * 
		 * @return an Array of String, each representing a possible combination of facets names
		 */
		String[] value();

	}

	/**
	 * 
	 * The class type. Allows to declare a new datatype in GAML.
	 * 
	 * @author drogoul
	 * @since 2 juin 2012
	 * 
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Inherited
	@Target(ElementType.TYPE)
	public static @interface type {

		/**
		 * Name.
		 * 
		 * @return a String representing the type name in GAML
		 */
		String name();

		/**
		 * @return the unique identifier for this type. User-added types can be chosen
		 *         between IType.AVAILABLE_TYPE and IType.SPECIES_TYPE (exclusive)
		 */
		int id();

		/**
		 * @return the list of Java Classes this type is "wrapping" (i.e. representing). The first
		 *         one is the one that will be used preferentially throughout GAMA. The other ones
		 *         are to ensure compatibility, in operators, with compatible Java classes (for
		 *         instance, List and GamaList).
		 */
		Class[] wraps();

		/**
		 * @return the kind of Variable used to store this type. see ISymbolKind.Variable.
		 */

		int kind() default ISymbolKind.Variable.REGULAR;

		/**
		 * Doc.
		 * 
		 * @return the documentation attached to this type
		 * @see doc
		 */
		doc[] doc() default {};

	}

	/**
	 * 
	 * The class skill. Allows to define a new skill (class grouping variables and actions that can
	 * be used by agents).
	 * 
	 * @see vars
	 * @see action
	 * @author drogoul
	 * @since 2 juin 2012
	 * 
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(ElementType.TYPE)
	public static @interface skill {

		/**
		 * Name.
		 * 
		 * @return a String representing the skill name in GAML (must be unique throughout GAML)
		 */
		String name();

		/**
		 * Attach_to.
		 * 
		 * @return An array of species names to which the skill will be automatically added
		 *         (complements the "skills" parameter of species)
		 * @see species
		 */
		String[] attach_to() default {};

		/**
		 * Doc.
		 * 
		 * @return the documentation attached to this skill
		 * @see doc
		 */
		doc[] doc() default {};

	}

	/**
	 * 
	 * The class inside. Used in conjunction with symbol. Provides a way to tell where this symbol
	 * should be located in a model (i.e. what its parents should be). Either direct symbol names
	 * (in symbols) or generic symbol kinds can be used
	 * @see symbol
	 * @see ISymbolKind
	 * @author drogoul
	 * @since 2 juin 2012
	 * 
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(ElementType.TYPE)
	@Inherited
	public static @interface inside {

		String[] symbols() default {};

		int[] kinds() default {};
	}

	/**
	 * 
	 * The class species. The class annotated with this annotation will be the support of a species
	 * of agents.
	 * 
	 * @see vars
	 * @see action
	 * @author drogoul
	 * @since 2 juin 2012
	 * 
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(ElementType.TYPE)
	public static @interface species {

		/**
		 * Name.
		 * 
		 * @return The name of the species that will be created with this class as base. Must be
		 *         unique throughout GAML.
		 */
		String name();

		/**
		 * Skills.
		 * 
		 * @return An array of skill names that will be automatically attached to this species.
		 *         Example: <code> @species(value="animal" skills={"moving"}) </code>
		 * @see skill
		 */
		String[] skills() default {};

		/**
		 * Doc.
		 * 
		 * @return the documentation attached to this species
		 * @see doc
		 */
		doc[] doc() default {};
	}

	/**
	 * The Interface args. Describes the names of the arguments passed to an action.
	 * @deprecated use action.args() instead
	 * @see action
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(ElementType.METHOD)
	@Deprecated
	public static @interface args {

		/**
		 * Value.
		 * 
		 * 
		 * @return an Array of strings, each representing an argument that can be passed to a
		 *         action. Used to tag the method that will implement the action.
		 */
		String[] names();

	}

	/**
	 * 
	 * The class action. Used to tag a method that will be considered as an action (or
	 * primitive) in GAML. The method must have the following signature:
	 * <code>Object methodName(IScope) throws GamaRuntimeException </code> and be contained in a
	 * class annotated with @species or @skill (or a related class, like a subclass or an interface)
	 * 
	 * @see species
	 * @see skill
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(ElementType.METHOD)
	public static @interface action {

		/**
		 * Value.
		 * 
		 * @return the name of the action as it can be used in GAML.
		 */
		String name();

		/**
		 * Abstract
		 */

		boolean virtual() default false;

		/**
		 * Args
		 * 
		 * @return the list of arguments passed to this action. Each argument is an instance of arg
		 * @see arg
		 */
		arg[] args() default {};

		/**
		 * Doc.
		 * 
		 * @return the documentation attached to this action (arguments are documented individually)
		 * @see doc
		 */
		doc[] doc() default {};
	}

	/**
	 * 
	 * The class arg. Describes an argument passed to an action.
	 * 
	 * @author drogoul
	 * @since 2 juin 2012
	 * 
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Inherited
	public static @interface arg {

		/**
		 * Name.
		 * 
		 * @return the name of the argument as it can be used in GAML
		 */
		String name() default "";

		/**
		 * Type.
		 * 
		 * @return An array containing the textual representation of the types that can be taken by
		 *         the argument (see IType)
		 */
		int[] type() default {};

		/**
		 * Optional.
		 * 
		 * @return whether this argument is optional or not
		 * @change AD 31/08/13 : the default is now true.
		 */
		boolean optional() default true;

		/**
		 * Doc.
		 * 
		 * @return the documentation attached to this argument
		 * @see doc
		 */
		doc[] doc() default {};
	}

	/**
	 * 
	 * The class vars. Used to describe the variables defined by a @species, a @skill or the
	 * implementation class of a @type
	 * 
	 * @author drogoul
	 * @since 2 juin 2012
	 * 
	 */
	@Retention(RetentionPolicy.SOURCE)
	public static @interface vars {

		/**
		 * Value.
		 * 
		 * @return an Array of var instances, each representing a variable
		 * @see var
		 */
		var[] value();
	}

	/**
	 * 
	 * The class var. Used to describe a single variable or field.
	 * 
	 * @author drogoul
	 * @since 2 juin 2012
	 * 
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target({})
	public static @interface var {

		/**
		 * Name.
		 * 
		 * @return The name of the variable as it can be used in GAML.
		 */
		String name();

		/**
		 * Type.
		 * 
		 * @return The textual representation of the type of the variable (see IType)
		 */
		int type();

		/**
		 * Of.
		 * 
		 * @return The textual representation of the content type of the variable (see
		 *         IType#defaultContentType())
		 */
		int of() default 0;

		/**
		 * Index.
		 * 
		 * @return The textual representation of the index type of the variable (see
		 *         IType#defaultKeyType())
		 */
		int index() default 0;

		/**
		 * Constant
		 * 
		 * @return whether or not this variable should be considered as non modifiable
		 */
		boolean constant() default false;

		/**
		 * Init
		 * 
		 * @return the initial value of this variable as a String that will be interpreted by GAML
		 */
		String init() default "";

		/**
		 * Depends_on.
		 * 
		 * @return an array of String representing the names of the variables on which this variable
		 *         depends (so that they are computed before)
		 */
		String[] depends_on() default {};

		/**
		 * Species.
		 * 
		 * @return the species of the variable value in case its type is IType.AGENT.
		 * @deprecated use type instead with the name of the species
		 */
		@Deprecated
		String species() default "";

		/**
		 * Doc.
		 * 
		 * @return the documentation attached to this variable
		 * @see doc
		 */
		doc[] doc() default {};
	}

	/**
	 * The Interface symbol. Represents a "symbol" in GAML, i.e. either a statement or a declaration
	 * (variable, species, model, etc.). Elements annotated by this annotation should indicate
	 * what kind of symbol they represent
	 * @see ISymbolKind
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(ElementType.TYPE)
	public static @interface symbol {

		/**
		 * Name.
		 * 
		 * @return an Array of strings, each representing a possible keyword for a GAML statement.
		 * 
		 */
		String[] name() default {};

		/**
		 * Kind.
		 * 
		 * @return the kind of the annotated symbol.
		 * @see ISymbolKind
		 */
		int kind();

		/**
		 * NoScope.
		 * 
		 * @return Indicates if the statement (usually a sequence) defines its own scope.
		 *         Otherwise, all the temporary variables defined in it are actually defined in the
		 *         super-scope
		 */
		boolean with_scope() default true;

		/**
		 * WithSequence.
		 * 
		 * @return Indicates wether or not a sequence can ou should follow the symbol denoted by
		 *         this class.
		 */
		boolean with_sequence();

		/**
		 * WithArgs.
		 * 
		 * @return Indicates wether or not the symbol denoted by this class will accept arguments
		 */
		boolean with_args() default false;

		/**
		 * RemoteContext.
		 * 
		 * @return Indicates that the context of this statement is actually an hybrid context:
		 *         although it will be executed in a remote context, any temporary variables
		 *         declared in the enclosing scopes should be passed on as if the statement was
		 *         executed in the current context.
		 */

		boolean remote_context() default false;

		/**
		 * Doc.
		 * 
		 * @return the documentation attached to this symbol.
		 * @see doc
		 */
		doc[] doc() default {};

		/**
		 * 
		 * @return Indicates that this statement must be unique in its super context (for example,
		 *         only one return is allowed in the body of an action).
		 */
		boolean unique_in_context() default false;

		/**
		 * 
		 * @return Indicates that only one statement with the same name should be allowed in the
		 *         same super context
		 */
		boolean unique_name() default false;

	}

	/**
	 * Written by drogoul Modified on 9 ao�t 2010
	 * 
	 * Used to annotate methods that can be used as operators in GAML.
	 * 
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	public static @interface operator {

		/**
		 * @return an Array of strings, each representing a possible keyword for the operator. Does
		 *         not need to be unique throughout GAML
		 * 
		 */
		String[] value();

		/**
		 * @return true if this operator should be treated as an iterator (i.e.requires initializing
		 *         the special variable "each" of WorldSkill within the method)
		 * @see WorldSkill
		 */

		boolean iterator() default false;

		/**
		 * @return whether or not the operator can be evaluated as a constant if its child (resp.
		 *         children) is (resp. are) constant.
		 */
		boolean can_be_const() default false;

		/**
		 * @return the type of the content if the returned value is a container. Can be directly a
		 *         type in IType or one of the constants declared in ITypeProvider (in which case,
		 *         the content type is searched using this provider).
		 * @see IType
		 * @see ITypeProvider
		 */
		int content_type() default ITypeProvider.NONE;

		/**
		 * @return the type of the index if the returned value is a container. Can be directly a
		 *         type in IType or one of the constants declared in ITypeProvider (in which case,
		 *         the index type is searched using this provider).
		 * @see IType
		 * @see ITypeProvider
		 */
		int index_type() default ITypeProvider.NONE;

		/**
		 * @return if the argument is a container, return the types expected for its contents.
		 *         Should be an array of IType.XXX.
		 * @see IType
		 * @see ITypeProvider
		 */
		int[] expected_content_type() default {};

		/**
		 * @return the respective priority of the operator w.r.t. to the others. Priorities are
		 *         classified in several categories, and specified using one of the constants
		 *         declared in IPriority.
		 * @deprecated This annotation is now deprecated as it is not used anymore by the parser. It
		 *             can be safely removed.
		 * @see IPriority
		 */
		// @Deprecated
		// short priority() default IPriority.DEFAULT;

		/**
		 * 
		 * @return the type of the expression if it cannot be determined at compile time (i.e. when
		 *         the return type is "Object"). Can be directly a type in IType or one of the
		 *         constants declared in ITypeProvider (in which case, the type is searched using
		 *         this provider).
		 * @see IType
		 * @see ITypeProvider
		 */
		int type() default ITypeProvider.NONE;

		/**
		 * Doc.
		 * 
		 * @return the documentation attached to this operator.
		 * @see doc
		 */
		doc[] doc() default {};
	}

	/**
	 * The class getter. Indicates that a method is to be used as a getter for a variable
	 * defined in the class. The variable must be defined on its own (in vars).
	 * 
	 * @see vars
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(ElementType.METHOD)
	public static @interface getter {

		/**
		 * Value.
		 * 
		 * @return the name of the variable for which the annotated method is to be considered as a
		 *         getter.
		 */
		String value();

		/**
		 * Initializer.
		 * 
		 * @return whether or not this getter shoud also be used as an initializer
		 */
		boolean initializer() default false;

	}

	/**
	 * The class setter. Indicates that a method is to be used as a setter for a variable
	 * defined in the class. The variable must be defined on its own (in vars).
	 * 
	 * @see vars
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(ElementType.METHOD)
	public static @interface setter {

		/**
		 * Value.
		 * 
		 * @return the name of the variable for which the annotated method is to be considered as a
		 *         getter.
		 */
		String value();
	}

	/**
	 * 
	 * The class factory. Denotes that a class (that must implement ISymbolFactory) can be used to
	 * parse and compile specific kinds of symbols
	 * 
	 * @see ISymbolFactory
	 * @see ISymbolKind
	 * @author drogoul
	 * @since 2 juin 2012
	 * 
	 */

	@Retention(RetentionPolicy.SOURCE)
	@Target(ElementType.TYPE)
	public static @interface factory {

		/**
		 * @return The symbol kinds defining the symbols this factory is intended to parse and
		 *         compile
		 * @see ISymbolKind
		 */
		int[] handles();

		/**
		 * @return The subfactories that this factory can invocate, based on the kind of symbols
		 *         they are handling
		 * @deprecated This annotation is not used anymore and can be safely removed
		 * @see ISymbolKind
		 */
		@Deprecated
		int[] uses() default {};

	}

	/**
	 * 
	 * The class doc. Provides a unified way of attaching documentation to the various GAML elements
	 * tagged by the other annotations. The documentation is automatically assembled at compile time
	 * and also used at runtime in GAML editors
	 * 
	 * @author Benoit Gaudou
	 * @since 2 juin 2012
	 * 
	 */

	@Retention(RetentionPolicy.SOURCE)
	// @Target({ ElementType.TYPE, ElementType.METHOD })
	@Inherited
	public static @interface doc {

		/**
		 * Value.
		 * 
		 * @return a String representing the documentation of a GAML element
		 */
		String value() default "";

		/**
		 * Deprecated.
		 * 
		 * @return a String indicating (if it is not empty) that the element is deprecated and
		 *         defining, if possible, what to use instead
		 */
		String deprecated() default "";

		/**
		 * Returns.
		 * 
		 * @return the documentation concerning the value(s) returned by this element (if any).
		 */
		String returns() default "";

		/**
		 * Comment.
		 * 
		 * @return An optional comment that will appear differently from the documentation itself.
		 */
		String comment() default "";

		/**
		 * Special_cases
		 * 
		 * @return An array of String representing the documentation of the "special cases" in which
		 *         the documented element takes part
		 */
		String[] special_cases() default {};

		/**
		 * Examples
		 * 
		 * @return An array of String representing some examples or use-cases about how to use this
		 *         element
		 */
		String[] examples() default {};

		/**
		 * See.
		 * 
		 * @return An array of String representing cross-references to other elements in GAML
		 */
		String[] see() default {};
	}

}
