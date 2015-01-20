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
package simplenlg.test;

import junit.framework.Assert;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.phrasespec.AdvPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * Further tests from third parties
 * @author Albert Gatt, University of Malta and University of Aberdeen
 *
 */
public class ExternalTests2 extends SimpleNLG4Test {

	public ExternalTests2(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Check that empty phrases are not realised as "null"
	 */
	public void testEmptyPhraseRealisation() {
		SPhraseSpec emptyClause = this.phraseFactory.createClause();
		Assert.assertEquals("", this.realiser.realise(emptyClause).getRealisation());
	}
		
	/**
	 * Check that empty coordinate phrases are not realised as "null"
	 */
	public void testEmptyCoordination() {		
		//first a simple phrase with no coordinates
		CoordinatedPhraseElement coord = this.phraseFactory.createCoordinatedPhrase();
		Assert.assertEquals("", this.realiser.realise(coord).getRealisation());
		
		//now one with a premodifier and nothing else
		coord.addPreModifier(this.phraseFactory.createAdjectivePhrase("nice"));
		Assert.assertEquals("nice", this.realiser.realise(coord).getRealisation());			
	}
	
	/**
	 * Test change from "a" to "an" in the presence of a premodifier with a vowel
	 */
	public void testIndefiniteWithPremodifier() {
		SPhraseSpec s = this.phraseFactory.createClause("there", "be");
		s.setFeature(Feature.TENSE, Tense.PRESENT);
		NPPhraseSpec np = this.phraseFactory.createNounPhrase("a", "stenosis");
		s.setObject(np);
		
		//check without modifiers -- article should be "a"
		Assert.assertEquals("there is a stenosis", this.realiser.realise(s).getRealisation());
		
		//add a single modifier -- should turn article to "an"
		np.addPreModifier(this.phraseFactory.createAdjectivePhrase("eccentric"));
		Assert.assertEquals("there is an eccentric stenosis", this.realiser.realise(s).getRealisation());
	}
	
	/**
	 * Test for comma separation between premodifers
	 */
	public void testMultipleAdjPremodifiers() {
		NPPhraseSpec np = this.phraseFactory.createNounPhrase("a", "stenosis");
		np.addPreModifier(this.phraseFactory.createAdjectivePhrase("eccentric"));
		np.addPreModifier(this.phraseFactory.createAdjectivePhrase("discrete"));
		Assert.assertEquals("an eccentric, discrete stenosis", this.realiser.realise(np).getRealisation());				
	}
	
	/**
	 * Test for comma separation between verb premodifiers
	 */
	public void testMultipleAdvPremodifiers() {	
		AdvPhraseSpec adv1 =this.phraseFactory.createAdverbPhrase("slowly");
		AdvPhraseSpec adv2 =this.phraseFactory.createAdverbPhrase("discretely");

		//case 1: concatenated premods: should have comma
		VPPhraseSpec vp = this.phraseFactory.createVerbPhrase("run");
		vp.addPreModifier(adv1);
		vp.addPreModifier(adv2);
		Assert.assertEquals("slowly, discretely runs", this.realiser.realise(vp).getRealisation());
				
		//case 2: coordinated premods: no comma
		VPPhraseSpec vp2 = this.phraseFactory.createVerbPhrase("eat");
		vp2.addPreModifier(this.phraseFactory.createCoordinatedPhrase(adv1, adv2));
		Assert.assertEquals("slowly and discretely eats", this.realiser.realise(vp2).getRealisation());
	}

	public void testParticipleModifier() {
		
		String verb = "associate";
		VPPhraseSpec adjP = this.phraseFactory.createVerbPhrase(verb);
		adjP.setFeature(Feature.TENSE, Tense.PAST);
		
		NPPhraseSpec np = this.phraseFactory.createNounPhrase("a", "thrombus");
		np.addPreModifier(adjP);
		String realised = this.realiser.realise(np).getRealisation();
		System.out.println(realised);
		// cch TESTING The following line doesn't work when the lexeme is a
		// verb.
		// morphP.preMod.Add(new AdjPhraseSpec((Lexeme)modifier));

		// It doesn't work for verb "associate" as adjective past participle.
		// Instead of realizing as "associated" it realizes as "ed".
		// Need to use verb phrase.

		// cch TODO : handle general case making phrase type corresponding to
		// lexeme category and usage.
	}
	
	/**
	 * Check that setComplement replaces earlier complements
	 */
	public void testSetComplement() {
		SPhraseSpec s = this.phraseFactory.createClause();
		s.setSubject("I");
		s.setVerb("see");
		s.setObject("a dog");
		
		Assert.assertEquals("I see a dog", this.realiser.realise(s).getRealisation());
		
		s.setObject("a cat");
		Assert.assertEquals("I see a cat", this.realiser.realise(s).getRealisation());
		
		s.setObject("a wolf");
		Assert.assertEquals("I see a wolf", this.realiser.realise(s).getRealisation());

	}
}
