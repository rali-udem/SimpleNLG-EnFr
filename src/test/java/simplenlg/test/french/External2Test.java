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
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.phrasespec.AdvPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * Further tests from third parties
 * @author Albert Gatt, University of Malta and University of Aberdeen
 * 
 * translated and adapted by vaudrypl
 */
public class External2Test extends SimpleNLG4TestBase {

	public External2Test(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Check that empty phrases are not realised as "null"
	 */
	public void testEmptyPhraseRealisation() {
		SPhraseSpec emptyClause = this.factory.createClause();
		Assert.assertEquals("", this.realiser.realise(emptyClause).getRealisation());
	}
		
	/**
	 * Check that empty coordinate phrases are not realised as "null"
	 */
	public void testEmptyCoordination() {		
		//first a simple phrase with no coordinates
		CoordinatedPhraseElement coord = this.factory.createCoordinatedPhrase();
		Assert.assertEquals("", this.realiser.realise(coord).getRealisation());
		
		//now one with a premodifier and nothing else
		coord.addPreModifier(this.factory.createAdjectivePhrase("beau"));
		Assert.assertEquals("beau", this.realiser.realise(coord).getRealisation());			
	}
	
	/**
	 * Test for no comma separation between premodifers that are not adverbs
	 */
	public void testMultipleAdjPremodifiers() {
		NPPhraseSpec np = this.factory.createNounPhrase("un", "homme");
		np.addPreModifier(this.factory.createAdjectivePhrase("beau"));
		np.addPreModifier(this.factory.createAdjectivePhrase("grand"));
		Assert.assertEquals("un beau grand homme", this.realiser.realise(np).getRealisation());				
	}
	
	/**
	 * Test for comma separation between adverb premodifiers
	 */
	public void testMultipleAdvPremodifiers() {	
		AdvPhraseSpec adv1 =this.factory.createAdverbPhrase("lentement");
		AdvPhraseSpec adv2 =this.factory.createAdverbPhrase("discrètement");

		//case 1: concatenated premods: should have comma
		VPPhraseSpec vp = this.factory.createVerbPhrase("marcher");
		vp.addPreModifier(adv1);
		vp.addPreModifier(adv2);
		Assert.assertEquals("marche lentement, discrètement", this.realiser.realise(vp).getRealisation());
				
		//case 2: coordinated premods: no comma
		VPPhraseSpec vp2 = this.factory.createVerbPhrase("manger");
		vp2.addPreModifier(this.factory.createCoordinatedPhrase(adv1, adv2));
		Assert.assertEquals("mange lentement et discrètement", this.realiser.realise(vp2).getRealisation());
	}

	public void testParticipleModifier() {
		
		String verb = "déterminer";
		NLGElement adjP = this.factory.createNLGElement(verb, LexicalCategory.VERB);
		adjP.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
		
		NPPhraseSpec np = this.factory.createNounPhrase("un", "femme");
		np.setPlural(true);
		np.addModifier(adjP);
		String realised = this.realiser.realise(np).getRealisation();
//		System.out.println(realised);
		Assert.assertEquals("des femmes déterminées", realised);
		adjP.setFeature(Feature.FORM, Form.PRESENT_PARTICIPLE);
		realised = this.realiser.realise(np).getRealisation();
		Assert.assertEquals("des femmes déterminantes", realised);
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
		SPhraseSpec s = this.factory.createClause();
		s.setSubject("je");
		s.setVerb("voir");
		s.setObject("un chien");
		
		Assert.assertEquals("je vois un chien", this.realiser.realise(s).getRealisation());
		
		s.setObject("un chat");
		Assert.assertEquals("je vois un chat", this.realiser.realise(s).getRealisation());
		
		s.setObject("un loup");
		Assert.assertEquals("je vois un loup", this.realiser.realise(s).getRealisation());

	}
}
