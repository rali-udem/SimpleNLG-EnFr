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
import simplenlg.features.LexicalFeature;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.StringElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.NPPhraseSpec;

/**
 * This class incorporates a few tests for adjectival phrases. Also tests for
 * adverbial phrase specs, which are very similar
 * @author agatt
 * translated and adapted by vaudrypl
 */
public class AdjectivePhraseTest extends SimpleNLG4TestBase {

	/**
	 * Instantiates a new adj p test.
	 * 
	 * @param name
	 *            the name
	 */
	public AdjectivePhraseTest(String name) {
		super(name);
	}

	/**
	 * Test premodification & coordination of Adjective Phrases (Not much else
	 * to simplenlg.test)
	 */
	@Test
	public void testAdj() {

		// form the adjphrase "incroyablement salace"
		this.salace.addPreModifier(this.factory.createAdverbPhrase("incroyablement")); //$NON-NLS-1$
		Assert.assertEquals("incroyablement salace", this.realiser //$NON-NLS-1$
				.realise(this.salace).getRealisation());

		// form the adjphrase "étonnamment beau"
		this.beau.addPreModifier("étonnamment"); //$NON-NLS-1$
		Assert.assertEquals("étonnamment beau", this.realiser //$NON-NLS-1$
				.realise(this.beau).getRealisation());

		// coordinate the two aps
		CoordinatedPhraseElement coordap = factory.createCoordinatedPhrase(
				this.salace, this.beau);
		Assert.assertEquals("incroyablement salace et étonnamment beau", //$NON-NLS-1$
				this.realiser.realise(coordap).getRealisation());

		// changing the inner conjunction with String
		coordap.setFeature(Feature.CONJUNCTION, "mais"); //$NON-NLS-1$
		Assert.assertEquals("incroyablement salace, mais étonnamment beau", //$NON-NLS-1$
				this.realiser.realise(coordap).getRealisation());

		// changing the inner conjunction with WordElement
		WordElement ou = (WordElement) factory.createWord("ou", LexicalCategory.CONJUNCTION);
		coordap.setFeature(Feature.CONJUNCTION, ou); //$NON-NLS-1$
		Assert.assertEquals("incroyablement salace ou étonnamment beau", //$NON-NLS-1$
				this.realiser.realise(coordap).getRealisation());

		// coordinate this with a new AdjPhraseSpec
		CoordinatedPhraseElement coord2 = factory.createCoordinatedPhrase(coordap,
				this.stupefiant);
		Assert.assertEquals(
				"incroyablement salace ou étonnamment beau et stupéfiant", //$NON-NLS-1$
				this.realiser.realise(coord2).getRealisation());

		// add a premodifier the coordinate phrase, yielding
		// "sérieusement et indéniablement incroyablement salace ou étonnamment beau et stupéfiant"
		CoordinatedPhraseElement preMod = factory.createCoordinatedPhrase(
				new StringElement("sérieusement"), new StringElement("indéniablement")); //$NON-NLS-1$//$NON-NLS-2$

		coord2.addPreModifier(preMod);
		Assert
				.assertEquals(
						"sérieusement et indéniablement incroyablement salace ou étonnamment beau et stupéfiant", //$NON-NLS-1$
						this.realiser.realise(coord2).getRealisation());

		// adding a coordinate rather than coordinating should give a different
		// result
		coordap.addCoordinate(this.stupefiant);
		Assert.assertEquals(
				"incroyablement salace, étonnamment beau ou stupéfiant", //$NON-NLS-1$
				this.realiser.realise(coordap).getRealisation());

	}

	/**
	 * Simple test of adverbials
	 */
	@Test
	public void testAdv() {

		PhraseElement sent = this.factory.createClause("Jean", "manger"); //$NON-NLS-1$ //$NON-NLS-2$

		PhraseElement adv = this.factory.createAdverbPhrase("rapidement"); //$NON-NLS-1$

		sent.addModifier(adv);

		Assert.assertEquals("Jean mange rapidement", this.realiser.realise(sent) //$NON-NLS-1$
				.getRealisation());

		adv.addPreModifier("très"); //$NON-NLS-1$

		Assert.assertEquals("Jean mange très rapidement", this.realiser.realise( //$NON-NLS-1$
				sent).getRealisation());

		NPPhraseSpec chienne = factory.createNounPhrase("le", "chienne");
		chienne.setPlural(true);
		sent = factory.createClause(chienne, "japper");
		sent.setFeature(Feature.TENSE, Tense.PAST);
		sent.addModifier("fort");
		Assert.assertEquals("les chiennes ont jappé fort", //$NON-NLS-1$
				this.realiser.realise(sent).getRealisation());
		
		sent.clearModifiers();
		sent.addPreModifier("fort");
		Assert.assertEquals("les chiennes ont fort jappé", //$NON-NLS-1$
				this.realiser.realise(sent).getRealisation());

		sent.clearModifiers();
		sent.addPostModifier("fort");
		Assert.assertEquals("les chiennes ont jappé fort", //$NON-NLS-1$
				this.realiser.realise(sent).getRealisation());

		sent.clearModifiers();
		sent.addFrontModifier("fort");
		Assert.assertEquals("fort, les chiennes ont jappé", //$NON-NLS-1$
				this.realiser.realise(sent).getRealisation());
	}

	/**
	 * Test for multiple adjective pre-modifiers without comma-separation. For symmetry with {@link simplenlg.test.english.AdjectivePhraseTest#testMultipleModifiers()}
	 */
	@Test
	public void testMultipleModifiers() {
		PhraseElement np = this.factory.createNounPhrase("le", "bateau");
		WordElement beau = this.lexicon.getWord("beau", LexicalCategory.ADJECTIVE);
		np.addPreModifier(beau);
		WordElement grand = this.lexicon.getWord("grand", LexicalCategory.ADJECTIVE);
		np.addPreModifier(grand);
		Assert.assertEquals("le beau grand bateau", this.realiser.realise(np).getRealisation());

	}
}
