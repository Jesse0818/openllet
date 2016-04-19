// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import aterm.ATermAppl;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Represents an edge cached for a {@link CachedNode}. A cached edge stores the information about only one node (the neighbor of the cached node
 * where this edge is stored) and for that node only the name is stored. This keeps the memory footprint of cached nodes to a minimum without causing any slow
 * downs (since cached nodes are used only in limited ways).
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public abstract class CachedEdge implements Edge
{
	protected ATermAppl neighbor;
	private final Role role;

	private DependencySet depends;

	public CachedEdge(final Role role, final ATermAppl neighbor, final DependencySet ds)
	{
		this.role = role;
		this.neighbor = neighbor;
		this.depends = ds.cache();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DependencySet getDepends()
	{
		return depends;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Individual getFrom()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ATermAppl getFromName()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getNeighbor(final Node node)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role getRole()
	{
		return role;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getTo()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ATermAppl getToName()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDepends(final DependencySet ds)
	{
		depends = ds;
	}

	@Override
	public String toString()
	{
		return "[" + role + ", " + neighbor + "] - " + depends;
	}
}
