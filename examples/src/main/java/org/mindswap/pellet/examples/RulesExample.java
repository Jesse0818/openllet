// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.examples;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 * An example program that tests the DL-safe rules example from Table 3 in the paper: B. Motik, U. Sattler, R. Studer. Query Answering for OWL-DL with Rules.
 * Proc. of the 3rd International Semantic Web Conference (ISWC 2004), Hiroshima, Japan, November, 2004, pp. 549-563
 *
 * @author Evren Sirin
 */
public class RulesExample
{
	public static void main(final String[] args)
	{
		final String ont = "http://owldl.com/ontologies/dl-safe.owl";

		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, null);
		model.read(ont);

		final ObjectProperty sibling = model.getObjectProperty(ont + "#sibling");

		final OntClass BadChild = model.getOntClass(ont + "#BadChild");
		final OntClass Child = model.getOntClass(ont + "#Child");

		final Individual Abel = model.getIndividual(ont + "#Abel");
		final Individual Cain = model.getIndividual(ont + "#Cain");
		final Individual Remus = model.getIndividual(ont + "#Remus");
		final Individual Romulus = model.getIndividual(ont + "#Romulus");

		model.prepare();

		// Cain has sibling Abel due to SiblingRule
		printPropertyValues(Cain, sibling);
		// Abel has sibling Cain due to SiblingRule and rule works symmetric
		printPropertyValues(Abel, sibling);
		// Remus is not inferred to have a sibling because his father is not
		// known
		printPropertyValues(Remus, sibling);
		// No siblings for Romulus for same reasons
		printPropertyValues(Romulus, sibling);

		// Cain is a BadChild due to BadChildRule
		printInstances(BadChild);
		// Cain is a Child due to BadChildRule and ChildRule2
		// Oedipus is a Child due to ChildRule1 and ChildRule2 combined with the
		// unionOf type
		printInstances(Child);
	}

	public static void printPropertyValues(final Individual ind, final Property prop)
	{
		System.out.print(ind.getLocalName() + " has " + prop.getLocalName() + "(s): ");
		printIterator(ind.listPropertyValues(prop));
	}

	public static void printInstances(final OntClass cls)
	{
		System.out.print(cls.getLocalName() + " instances: ");
		printIterator(cls.listInstances());
	}

	public static void printIterator(final ExtendedIterator i)
	{
		if (!i.hasNext())
			System.out.print("none");
		else
			while (i.hasNext())
			{
				final Resource val = (Resource) i.next();
				System.out.print(val.getLocalName());
				if (i.hasNext())
					System.out.print(", ");
			}
		System.out.println();
	}
}
