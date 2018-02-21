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

import junit.framework.Assert;

import org.junit.Test;

import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;

// TODO: Auto-generated Javadoc
/**
 * This class groups together some tests for prepositional phrases and
 * coordinate prepositional phrases.
 * @author agatt
 * translated and adapted by vaudrypl
 */
public class PrepositionalPhraseTest extends SimpleNLG4TestBase {

	/**
	 * Instantiates a new pP test.
	 * 
	 * @param name
	 *            the name
	 */
	public PrepositionalPhraseTest(String name) {
		super(name);
	}

	/**
	 * Basic test for the pre-set PP fixtures.
	 */
	@Test
	public void testBasic() {
		Assert.assertEquals("dans la pièce", this.realiser //$NON-NLS-1$
				.realise(this.dansLaPiece).getRealisation());
		Assert.assertEquals("derrière le rideau", this.realiser //$NON-NLS-1$
				.realise(this.derriereLeRideau).getRealisation());
		Assert.assertEquals("sur le rocher", this.realiser //$NON-NLS-1$
				.realise(this.surLeRocher).getRealisation());
	}

	/**
	 * Morphophonology.
	 */
	@Test
	public void testMorphophonology() {
		PPPhraseSpec aProposDuChien = this.factory.createPrepositionPhrase("à propos de", this.chien);
		Assert.assertEquals("à propos du chien", this.realiser //$NON-NLS-1$
				.realise(aProposDuChien).getRealisation());
		NPPhraseSpec mois = this.factory.createNounPhrase("le", "mois");
		mois.addModifier("prochain");
		PPPhraseSpec diciAuMoisProchain = this.factory.createPrepositionPhrase("d'ici à", mois);
		Assert.assertEquals("d'ici au mois prochain", this.realiser //$NON-NLS-1$
				.realise(diciAuMoisProchain).getRealisation());
	}

	/**
	 * Test for coordinate NP complements of PPs.
	 */
	@Test
	public void testComplementation() {
		this.dansLaPiece.clearComplements();
		this.dansLaPiece.addComplement(factory.createCoordinatedPhrase(
				this.factory.createNounPhrase("le", "salon"), //$NON-NLS-1$ //$NON-NLS-2$
				this.factory.createNounPhrase("le", "cuisine"))); //$NON-NLS-1$//$NON-NLS-2$
		Assert.assertEquals("dans le salon et la cuisine", this.realiser //$NON-NLS-1$
				.realise(this.dansLaPiece).getRealisation());
	}

	/**
	 * Test for PP coordination.
	 */
	public void testCoordination() {
		// simple coordination

		CoordinatedPhraseElement coord1 = factory.createCoordinatedPhrase(
				this.dansLaPiece, this.derriereLeRideau);
		Assert.assertEquals("dans la pièce et derrière le rideau", this.realiser //$NON-NLS-1$
				.realise(coord1).getRealisation());

		// change the conjunction
		coord1.setFeature(Feature.CONJUNCTION, "ou"); //$NON-NLS-1$
		Assert.assertEquals("dans la pièce ou derrière le rideau", this.realiser //$NON-NLS-1$
				.realise(coord1).getRealisation());

		// new coordinate
		CoordinatedPhraseElement coord2 = factory.createCoordinatedPhrase(
				this.surLeRocher, this.sousLaTable);
		coord2.setFeature(Feature.CONJUNCTION, "ou"); //$NON-NLS-1$
		Assert.assertEquals("sur le rocher ou sous la table", this.realiser //$NON-NLS-1$
				.realise(coord2).getRealisation());

		// coordinate two coordinates
		CoordinatedPhraseElement coord3 = factory.createCoordinatedPhrase(coord1,
				coord2);

		String text = this.realiser.realise(coord3).getRealisation();
		Assert
				.assertEquals(
						"dans la pièce ou derrière le rideau et sur le rocher ou sous la table", //$NON-NLS-1$
						text);
	}
}
