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
import simplenlg.features.Tense;
import simplenlg.features.Gender;
import simplenlg.features.LexicalFeature;
import simplenlg.features.french.FrenchLexicalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * Some tests for coordination, especially of coordinated VPs with modifiers.
 * 
 * @author Albert Gatt
 * 
 * translated and adapted by vaudrypl
 */
public class CoordinationTest extends SimpleNLG4TestBase {

	public CoordinationTest(String name) {
		super(name);
	}

	/**
	 * Test coordination without a conjunction.
	 */
	@Test
	public void testNoConjunction() {
		SPhraseSpec clause1 = factory.createClause("Arthur", "être", "fort"),
			clause2 = factory.createClause("Luc", "est", "agile");		
		CoordinatedPhraseElement coord = factory.createCoordinatedPhrase(clause1, clause2);
		coord.setConjunction("");
		Assert.assertEquals("Arthur est fort, Luc est agile", realise(coord));
	}
	
	/**
	 * Test pre and post-modification of coordinate VPs inside a sentence.
	 */
	@Test
	public void testModifiedCoordVP() {
		CoordinatedPhraseElement coord = this.factory
				.createCoordinatedPhrase(this.seLever, this.tomber);
		coord.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals("s'est levé et est tombé", this.realiser
				.realise(coord).getRealisation());

//		// add a premodifier
//		coord.addPreModifier("slowly");
//		Assert.assertEquals("slowly got up and fell down", this.realiser
//				.realise(coord).getRealisation());

		// adda postmodifier
		coord.addPostModifier(this.derriereLeRideau);
		Assert.assertEquals("s'est levé et est tombé derrière le rideau",
				this.realiser.realise(coord).getRealisation());

		// put within the context of a sentence
		SPhraseSpec s = this.factory.createClause();
		s.setSubject("Jacques");
		s.setVerbPhrase(coord);
		s.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals(
				"Jacques s'est levé et est tombé derrière le rideau",
				this.realiser.realise(s).getRealisation());

//		// add premod to the sentence
//		s.addPreModifier(this.lexicon
//				.getWord("toutefois", LexicalCategory.ADVERB));
//		Assert.assertEquals(
//				"Jacques s'est levé et est tombé derrière le rideau",
//				this.realiser.realise(s).getRealisation());

		// add postmod to the sentence
		s.addPostModifier(this.dansLaPiece);
		Assert
				.assertEquals(
						"Jacques s'est levé et est tombé derrière le rideau dans la pièce",
						this.realiser.realise(s).getRealisation());
		
		// change conjunction for conjunction with comma
		coord.setConjunction("mais");
		Assert.assertEquals(
				"Jacques s'est levé, mais est tombé derrière le rideau dans la pièce",
				this.realiser.realise(s).getRealisation());
		
	}

	/**
	 * Test due to Chris Howell -- create a complex sentence with front modifier
	 * and coordinateVP. This is a version in which we create the coordinate
	 * phrase directly.
	 */
	@Test
	public void testCoordinateVPComplexSubject() {
		// "As a result of the procedure the patient had an adverse contrast media reaction and went into cardiogenic shock."
		SPhraseSpec s = this.factory.createClause();
		
		s.setSubject(this.factory.createNounPhrase("le", "patient"));

		// first VP
		VPPhraseSpec vp1 = this.factory.createVerbPhrase(this.lexicon
				.getWord("avoir", LexicalCategory.VERB));
		WordElement reaction = this.lexicon.lookupWord("réaction",	LexicalCategory.NOUN);
		reaction.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		NPPhraseSpec np1 = this.factory.createNounPhrase("un", reaction);
				
		np1.addModifier(this.lexicon.getWord("négatif",
				LexicalCategory.ADJECTIVE));
		vp1.addComplement(np1);

		// second VP
		WordElement entrer = this.lexicon.lookupWord("entrer", LexicalCategory.VERB);
		entrer.setFeature(FrenchLexicalFeature.AUXILIARY_ETRE, true);
		VPPhraseSpec vp2 = this.factory.createVerbPhrase(entrer);
		PPPhraseSpec pp = this.factory.createPrepositionPhrase("en", this.lexicon.getWord("choc cardiaque", LexicalCategory.NOUN));
		vp2.addComplement(pp);
		
		//coordinate
		CoordinatedPhraseElement coord = this.factory.createCoordinatedPhrase(vp1, vp2);
		coord.setFeature(Feature.TENSE, Tense.PAST);		
//		Assert.assertEquals("had an adverse contrast media reaction and went into cardiogenic shock", this.realiser.realise(coord).getRealisation());
		Assert.assertEquals("a eu une réaction négative et est entré en choc cardiaque", this.realiser.realise(coord).getRealisation());
		
		//now put this in the sentence
		s.setVerbPhrase(coord);
		s.addFrontModifier("suite à l'opération");
		Assert.assertEquals("suite à l'opération, le patient a eu une réaction négative et est entré en choc cardiaque", this.realiser.realise(s).getRealisation());
		
	}
	/**
	 * @author vaudrypl
	 */
	@Test
	public void testCoordinationOrthography() {
		SPhraseSpec s = this.factory.createClause();
		s.setSubject("je");
		s.setVerb("manger");
		
		CoordinatedPhraseElement coord = factory.createCoordinatedPhrase();
		NPPhraseSpec pomme = factory.createNounPhrase("le", "pomme");
		NPPhraseSpec gateau = factory.createNounPhrase("le", "gâteau");
		NPPhraseSpec pain = factory.createNounPhrase("le", "pain");
		NPPhraseSpec fromage = factory.createNounPhrase("le", "fromage");
		coord.addCoordinate(pomme);
		coord.addCoordinate(gateau);
		coord.addCoordinate(pain);
		coord.addCoordinate(fromage);
		
		s.setObject(coord);
		
		// conjunctions without comma
		Assert.assertEquals("je mange la pomme, le gâteau, le pain et le fromage",
				this.realiser.realise(s).getRealisation());
		s.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("la pomme, le gâteau, le pain et le fromage sont mangés par moi",
				this.realiser.realise(s).getRealisation());
		coord.setConjunction("ou");
		Assert.assertEquals("la pomme, le gâteau, le pain ou le fromage est mangé par moi",
				this.realiser.realise(s).getRealisation());
		s.setFeature(Feature.PASSIVE, false);
		Assert.assertEquals("je mange la pomme, le gâteau, le pain ou le fromage",
				this.realiser.realise(s).getRealisation());
		
		// repeated conjunction, with comma
		coord.setConjunction("ni");
		// in the case of "ni", "ne" is added even if the sentence is not specified as negated
		Assert.assertEquals("je ne mange ni la pomme, ni le gâteau, ni le pain, ni le fromage",
				this.realiser.realise(s).getRealisation());
		s.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("ni la pomme, ni le gâteau, ni le pain, ni le fromage ne sont mangés par moi",
				this.realiser.realise(s).getRealisation());
		s.setFeature(Feature.PASSIVE, false);
		SPhraseSpec s2 = this.factory.createClause(coord, "pourrir");
		s2.addModifier("rapidement");
		Assert.assertEquals("ni la pomme, ni le gâteau, ni le pain, ni le fromage ne pourrissent rapidement",
				this.realiser.realise(s2).getRealisation());
		// return to another conjunction and see if "ne" disapears
		coord.setConjunction("ou");
		Assert.assertEquals("je mange la pomme, le gâteau, le pain ou le fromage",
				this.realiser.realise(s).getRealisation());
		Assert.assertEquals("la pomme, le gâteau, le pain ou le fromage pourrit rapidement",
				this.realiser.realise(s2).getRealisation());
	}
}
