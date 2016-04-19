package com.clarkparsia.pellint.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellint.model.LintFixer;
import com.clarkparsia.pellint.test.PellintTestCase;
import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 */
public class LintFixerTest extends PellintTestCase
{
	private OWLAxiom[] m_Axioms;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();

		m_Axioms = new OWLAxiom[] { OWL.subClassOf(m_Cls[0], OWL.or(m_Cls[1], m_Cls[2], m_Cls[3])), OWL.equivalentClasses(m_Cls[0], m_Cls[1]), OWL.differentFrom(m_Ind[2], m_Ind[3]) };
	}

	@Test
	public void testRemoveAndAdd() throws OWLException
	{
		addAxiom(m_Axioms[0]);
		addAxiom(m_Axioms[1]);

		final Set<OWLAxiom> axiomsToRemove = CollectionUtil.asSet(m_Axioms[0], m_Axioms[1]);
		final Set<OWLAxiom> axiomsToAdd = CollectionUtil.asSet(m_Axioms[2]);
		final LintFixer fixer = new LintFixer(axiomsToRemove, axiomsToAdd);
		assertTrue(fixer.apply(m_Manager, m_Ontology));

		final Set<OWLAxiom> axioms = m_Ontology.getAxioms();
		assertEquals(1, axioms.size());
		assertTrue(axioms.contains(m_Axioms[2]));
	}

	@Test
	public void testOldAxiomsDontExist() throws OWLException
	{
		addAxiom(m_Axioms[0]);

		final Set<OWLAxiom> axiomsToRemove = CollectionUtil.asSet(m_Axioms[0], m_Axioms[1]);
		final Set<OWLAxiom> axiomsToAdd = CollectionUtil.asSet(m_Axioms[2]);
		final LintFixer fixer = new LintFixer(axiomsToRemove, axiomsToAdd);
		assertFalse(fixer.apply(m_Manager, m_Ontology));

		final Set<OWLAxiom> axioms = m_Ontology.getAxioms();
		assertEquals(1, axioms.size());
		assertTrue(axioms.contains(m_Axioms[0]));
	}

	@Test
	public void testNewAxiomsAlreadyExist() throws OWLException
	{
		addAxiom(m_Axioms[0]);
		addAxiom(m_Axioms[1]);
		addAxiom(m_Axioms[2]);

		final Set<OWLAxiom> axiomsToRemove = CollectionUtil.asSet(m_Axioms[0], m_Axioms[1]);
		final Set<OWLAxiom> axiomsToAdd = CollectionUtil.asSet(m_Axioms[2]);
		final LintFixer fixer = new LintFixer(axiomsToRemove, axiomsToAdd);
		assertTrue(fixer.apply(m_Manager, m_Ontology));

		final Set<OWLAxiom> axioms = m_Ontology.getAxioms();
		assertEquals(1, axioms.size());
		assertTrue(axioms.contains(m_Axioms[2]));
	}

}
