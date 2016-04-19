// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.term;

import aterm.ATermAppl;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.mindswap.pellet.KnowledgeBase;

public class AbstractKBTests
{
	public static String base = "file:" + PelletTestSuite.base + "misc/";

	protected static final ATermAppl A = term("A");
	protected static final ATermAppl B = term("B");
	protected static final ATermAppl C = term("C");
	protected static final ATermAppl D = term("D");
	protected static final ATermAppl E = term("E");
	protected static final ATermAppl F = term("F");
	protected static final ATermAppl G = term("g");

	protected static final ATermAppl p = term("p");
	protected static final ATermAppl q = term("q");
	protected static final ATermAppl r = term("r");
	protected static final ATermAppl s = term("s");
	protected static final ATermAppl f = term("f");

	protected static final ATermAppl a = term("a");
	protected static final ATermAppl b = term("b");
	protected static final ATermAppl c = term("c");
	protected static final ATermAppl d = term("d");
	protected static final ATermAppl e = term("e");

	protected KnowledgeBase kb;

	@Before
	public void initializeKB()
	{
		kb = new KnowledgeBase();
	}

	@After
	public void disposeKB()
	{
		kb = null;
	}

	protected void classes(final ATermAppl... classes)
	{
		for (final ATermAppl cls : classes)
			kb.addClass(cls);
	}

	protected void objectProperties(final ATermAppl... props)
	{
		for (final ATermAppl p : props)
			kb.addObjectProperty(p);
	}

	protected void dataProperties(final ATermAppl... props)
	{
		for (final ATermAppl p : props)
			kb.addDatatypeProperty(p);
	}

	protected void annotationProperties(final ATermAppl... props)
	{
		for (final ATermAppl p : props)
			kb.addAnnotationProperty(p);
	}

	protected void individuals(final ATermAppl... inds)
	{
		for (final ATermAppl ind : inds)
			kb.addIndividual(ind);
	}

	public static <T> Set<Set<T>> singletonSets(final T... es)
	{
		final Set<Set<T>> set = new HashSet<>();
		for (final T e : es)
			set.add(Collections.singleton(e));
		return set;
	}
}
