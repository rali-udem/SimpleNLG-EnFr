/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is "Simplenlg".
 *
 * The Initial Developer of the Original Code is Ehud Reiter, Albert Gatt and Dave Westwater.
 * Portions created by Ehud Reiter, Albert Gatt and Dave Westwater are Copyright (C) 2010-11 The University of Aberdeen. All Rights Reserved.
 *
 * Contributor(s): Ehud Reiter, Albert Gatt, Dave Wewstwater, Roman Kutlak, Margaret Mitchell, Pierre-Luc Vaudry.
 */
package simplenlg.test.french;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import simplenlg.aggregation.BackwardConjunctionReductionRule;
import simplenlg.aggregation.Aggregator;
import simplenlg.aggregation.ClauseCoordinationRule;
import simplenlg.aggregation.ForwardConjunctionReductionRule;
import simplenlg.features.Feature;
import simplenlg.framework.NLGElement;
import simplenlg.phrasespec.SPhraseSpec;

/**
 * Some tests for aggregation.
 * 
 * @author Albert Gatt, University of Malta & University of Aberdeen
 * translated and adapted by vaudrypl
 */
public class ClauseAggregationTest extends SimpleNLG4TestBase {
	// set up a few more fixtures
	/** The s4. */
	SPhraseSpec s1, s2, s3, s4, s5, s6;
	Aggregator aggregator;
	ClauseCoordinationRule coord;
	ForwardConjunctionReductionRule fcr;
	BackwardConjunctionReductionRule bcr;

	/**
	 * Instantiates a new clause aggregation test.
	 * 
	 * @param name
	 *            the name
	 */
	public ClauseAggregationTest(String name) {
		super(name);
		aggregator = new Aggregator();
		aggregator.initialise();
		coord = new ClauseCoordinationRule();
		fcr = new ForwardConjunctionReductionRule();
		bcr = new BackwardConjunctionReductionRule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.test.SimplenlgTest#setUp()
	 */
	@Override
	@Before
	protected void setUp() {
		super.setUp();

		// la femme embrasse l'homme derrière le rideau
		this.s1 = this.factory.createClause();
		this.s1.setSubject(this.femme);
		this.s1.setVerbPhrase(this.factory.createVerbPhrase("embrasser"));
		this.s1.setObject(this.homme);
		this.s1.addPostModifier(this.factory
				.createPrepositionPhrase("derrière", this.factory
						.createNounPhrase("le", "rideau")));

		// la femme frappe le chien derrière le rocher
		this.s2 = this.factory.createClause();
		this.s2.setSubject(this.factory.createNounPhrase("le", "femme")); //$NON-NLS-1$
		this.s2.setVerbPhrase(this.factory.createVerbPhrase("frapper")); //$NON-NLS-1$
		this.s2.setObject(this.factory.createNounPhrase("le", "chien"));
		this.s2.addPostModifier(this.surLeRocher);

		// la femme frappe le chien derrière le rideau
		this.s3 = this.factory.createClause();
		this.s3.setSubject(this.factory.createNounPhrase("le", "femme")); //$NON-NLS-1$
		this.s3.setVerbPhrase(this.factory.createVerbPhrase("frapper")); //$NON-NLS-1$
		this.s3.setObject(this.factory.createNounPhrase("le", "chien"));
		this.s3.addPostModifier(this.factory
				.createPrepositionPhrase("derrière", this.factory
						.createNounPhrase("le", "rideau")));

		// la femme frappe le chien derrière le rideau
		this.s4 = this.factory.createClause();
		this.s4.setSubject(this.homme); //$NON-NLS-1$
		this.s4.setVerbPhrase(this.factory.createVerbPhrase("frapper")); //$NON-NLS-1$
		this.s4.setObject(this.factory.createNounPhrase("le", "chien"));
		this.s4.addPostModifier(this.derriereLeRideau);

		// la fille frappe le chien derrière le rideau
		this.s5 = this.factory.createClause();
		this.s5.setSubject(this.factory.createNounPhrase("le", "fille")); //$NON-NLS-1$
		this.s5.setVerbPhrase(this.factory.createVerbPhrase("frapper")); //$NON-NLS-1$
		this.s5.setObject(this.factory.createNounPhrase("le", "chien"));
		this.s5.addPostModifier(this.derriereLeRideau);

		// la femme embrasse le chien derrière le rideau
		this.s6 = this.factory.createClause();
		this.s6.setSubject(this.factory.createNounPhrase("le", "femme")); //$NON-NLS-1$
		this.s6.setVerbPhrase(this.factory.createVerbPhrase("embrasser")); //$NON-NLS-1$
		this.s6.setObject(this.factory.createNounPhrase("le", "chien"));
		this.s6.addPostModifier(this.factory
				.createPrepositionPhrase("derrière", this.factory
						.createNounPhrase("le", "rideau")));
	}

	/**
	 * Test clause coordination with two sentences with same subject but
	 * different postmodifiers: fails
	 */
	@Test
	public void testCoordinationSameSubjectFail() {
		List<NLGElement> elements = Arrays.asList((NLGElement) this.s1,
				(NLGElement) this.s2);
		List<NLGElement> result = this.coord.apply(elements);
		Assert.assertEquals(2, result.size());
	}

	/**
	 * Test clause coordination with two sentences one of which is passive:
	 * fails
	 */
	@Test
	public void testCoordinationPassiveFail() {
		this.s1.setFeature(Feature.PASSIVE, true);
		List<NLGElement> elements = Arrays.asList((NLGElement) this.s1,
				(NLGElement) this.s2);
		List<NLGElement> result = this.coord.apply(elements);
		Assert.assertEquals(2, result.size());
	}

//	/**
//	 * Test clause coordination with 2 sentences with same subject: succeeds
//	 */
//	@Test
//	public void testCoordinationSameSubjectSuccess() {
//		List<NLGElement> elements = Arrays.asList((NLGElement) this.s1,
//				(NLGElement) this.s3);
//		List<NLGElement> result = this.coord.apply(elements);
//		Assert.assertTrue(result.size() == 1); // should only be one sentence
//		NLGElement aggregated = result.get(0);
//		Assert
//				.assertEquals(
//						"the woman kisses the man and kicks the dog behind the curtain", //$NON-NLS-1$
//						this.realiser.realise(aggregated).getRealisation());
//	}

	/**
	 * Test clause coordination with 2 sentences with same VP: succeeds
	 */
	@Test
	public void testCoordinationSameVP() {
		List<NLGElement> elements = Arrays.asList((NLGElement) this.s3,
				(NLGElement) this.s4);
		List<NLGElement> result = this.coord.apply(elements);
		Assert.assertTrue(result.size() == 1); // should only be one sentence
		NLGElement aggregated = result.get(0);
		Assert.assertEquals(
				"la femme et l'homme frappent le chien derrière le rideau", //$NON-NLS-1$
				this.realiser.realise(aggregated).getRealisation());
	}

	/**
	 * Coordination of sentences with front modifiers: should preserve the mods
	 */
	@Test
	public void testCoordinationWithModifiers() {
		// now add a couple of front modifiers
		this.s3.addFrontModifier(this.factory
				.createAdverbPhrase("cependant"));
		this.s4.addFrontModifier(this.factory
				.createAdverbPhrase("cependant"));
		List<NLGElement> elements = Arrays.asList((NLGElement) this.s3,
				(NLGElement) this.s4);
		List<NLGElement> result = this.coord.apply(elements);
		Assert.assertTrue(result.size() == 1); // should only be one sentence
		NLGElement aggregated = result.get(0);
		Assert
				.assertEquals(
						"cependant, la femme et l'homme frappent le chien derrière le rideau", //$NON-NLS-1$
						this.realiser.realise(aggregated).getRealisation());
	}

	/**
	 * Test coordination of 3 sentences with the same VP
	 */
	public void testCoordinationSameVP2() {
		List<NLGElement> elements = Arrays.asList((NLGElement) this.s3,
				(NLGElement) this.s4, (NLGElement) this.s5);
		List<NLGElement> result = this.coord.apply(elements);
		Assert.assertTrue(result.size() == 1); // should only be one sentence
		NLGElement aggregated = result.get(0);
		Assert
				.assertEquals(
						"la femme, l'homme et la fille frappent le chien derrière le rideau", //$NON-NLS-1$
						this.realiser.realise(aggregated).getRealisation());
	}

	/**
	 * Forward conjunction reduction test
	 */
	@Test
	public void testForwardConjReduction() {
		NLGElement aggregated = this.fcr.apply(this.s2, this.s3);
		Assert
				.assertEquals(
						"la femme frappe le chien sur le rocher et frappe le chien derrière le rideau", //$NON-NLS-1$
						this.realiser.realise(aggregated).getRealisation());
	}

	/**
	 * Backward conjunction reduction test
	 */
	@Test
	public void testBackwardConjunctionReduction() {
		NLGElement aggregated = this.bcr.apply(this.s3, this.s6);
		Assert
				.assertEquals(
						"la femme frappe et la femme embrasse le chien derrière le rideau",
						this.realiser.realise(aggregated).getRealisation());
	}
	
	/**
	 * Test multiple aggregation procedures in a single aggregator. 
	 */
//	@Test
//	public void testForwardBackwardConjunctionReduction() {
//		this.aggregator.addRule(this.fcr);
//		this.aggregator.addRule(this.bcr);
//		realiser.setDebugMode(true);
//		List<NLGElement> result = this.aggregator.realise(Arrays.asList((NLGElement) this.s2, (NLGElement) this.s3));
//		Assert.assertTrue(result.size() == 1); // should only be one sentence
//		NLGElement aggregated = result.get(0);
//		NLGElement aggregated = this.phraseFactory.createdCoordinatedPhrase(this.s2, this.s3);
//		Assert
//				.assertEquals(
//						"the woman kicks the dog on the rock and behind the curtain", //$NON-NLS-1$
//						this.realiser.realise(aggregated).getRealisation());
//	}

}
