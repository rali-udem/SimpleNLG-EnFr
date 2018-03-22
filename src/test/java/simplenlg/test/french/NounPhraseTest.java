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

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.AdjPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * Tests for the NPPhraseSpec and CoordinateNPPhraseSpec classes.
 * 
 * @author agatt
 * translated and adapted by vaudrypl
 */
public class NounPhraseTest extends SimpleNLG4TestBase {

	/**
	 * Instantiates a new nP test.
	 * 
	 * @param name
	 *            the name
	 */
	public NounPhraseTest(String name) {
		super(name);
	}

	/**
	 * Test the setPlural() method in noun phrases.
	 */
	@Test
	public void testPlural() {
		this.np4.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		Assert.assertEquals(
				"les rochers", this.realiser.realise(this.np4).getRealisation()); //$NON-NLS-1$

		this.np5.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		Assert
				.assertEquals(
						"les rideaux", this.realiser.realise(this.np5).getRealisation()); //$NON-NLS-1$

		this.np5.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR);
		Assert.assertEquals(NumberAgreement.SINGULAR, this.np5
				.getFeature(Feature.NUMBER));
		Assert
				.assertEquals(
						"le rideau", this.realiser.realise(this.np5).getRealisation()); //$NON-NLS-1$

		this.np5.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		Assert
				.assertEquals(
						"les rideaux", this.realiser.realise(this.np5).getRealisation()); //$NON-NLS-1$
	}

	/**
	 * Test the pronominalisation method for full NPs.
	 */
	@Test
	public void testPronominalisation() {
		// sing
		this.proTest1.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		this.proTest1.setFeature(Feature.PRONOMINAL, true);
		Assert.assertEquals("elle", this.realiser.realise(this.proTest1).getRealisation()); //$NON-NLS-1$

//		// sing, possessive
//		this.proTest1.setFeature(Feature.POSSESSIVE, true);
//		Assert.assertEquals("her", this.realiser.realise(this.proTest1).getRealisation()); //$NON-NLS-1$
//
		// plural pronoun
		this.proTest2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		this.proTest2.setFeature(Feature.PRONOMINAL, true);
		Assert.assertEquals(
				"elles", this.realiser.realise(this.proTest2).getRealisation()); //$NON-NLS-1$

		NPPhraseSpec leChat = this.factory.createNounPhrase("le", "dragon");
		leChat.setPlural(true);
		leChat.setFeature(Feature.PRONOMINAL, true);
		Assert.assertEquals( "eux",
				this.realiser.realise(leChat).getRealisation()); //$NON-NLS-1$
		SPhraseSpec manger = this.factory.createClause(leChat, "manger");
		Assert.assertEquals( "ils mangent",
				this.realiser.realise(manger).getRealisation()); //$NON-NLS-1$
		
		// accusative: "them"
		this.proTest2.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.OBJECT);
		Assert.assertEquals(
				"les", this.realiser.realise(this.proTest2).getRealisation()); //$NON-NLS-1$
	}

	/**
	 * Test premodification in NPS.
	 */
	@Test
	public void testPremodification() {
		this.homme.addPreModifier(this.salace);
		Assert.assertEquals("le salace homme", this.realiser //$NON-NLS-1$
				.realise(this.homme).getRealisation());

		this.femme.addPreModifier(this.beau);
		Assert.assertEquals("la belle femme", this.realiser.realise( //$NON-NLS-1$
				this.femme).getRealisation());
		
		// "des" -> "de" in front of premodifiers
		NPPhraseSpec np = factory.createNounPhrase("un", "homme");
		np.setPlural(true);
		Assert.assertEquals("des hommes", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np.addModifier("gentil");
		Assert.assertEquals("des hommes gentils", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np.clearModifiers();
		np.addModifier("beau");
		Assert.assertEquals("de beaux hommes", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
	}

	/**
	 * Test "partitif" determiner.
	 */
	@Test
	public void testPartitif() {
		NPPhraseSpec np = factory.createNounPhrase("du", "sable");
		Assert.assertEquals("du sable", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np.setPlural(true);
		Assert.assertEquals("des sables", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np = factory.createNounPhrase("du", "eau");
		Assert.assertEquals("de l'eau", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np.setPlural(true);
		Assert.assertEquals("des eaux", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np = factory.createNounPhrase("du", "pluie");
		Assert.assertEquals("de la pluie", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np.setPlural(true);
		Assert.assertEquals("des pluies", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
	}

	/**
	 * Test indefinite determiners.
	 */
	@Test
	public void testIndefiniteDeterminers() {
		NPPhraseSpec np = factory.createNounPhrase("tout", "personne");
		Assert.assertEquals("toute personne", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np.setPlural(true);
		Assert.assertEquals("toutes les personnes", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np = factory.createNounPhrase("quelque", "enfant");
		Assert.assertEquals("quelque enfant", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np.setPlural(true);
		Assert.assertEquals("quelques enfants", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np = factory.createNounPhrase("quelques", "enfant");
		Assert.assertEquals("quelques enfants", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np = factory.createNounPhrase("plusieurs", "enfant");
		Assert.assertEquals("plusieurs enfants", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
	}

	/**
	 * Test demonstrative determiners.
	 */
	@Test
	public void testDemonstratives() {
		NPPhraseSpec np = factory.createNounPhrase("ce", "sable");
		Assert.assertEquals("ce sable", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np.setPlural(true);
		Assert.assertEquals("ces sables", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np = factory.createNounPhrase("ce -ci", "ensemble");
		Assert.assertEquals("cet ensemble-ci", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np.setPlural(true);
		Assert.assertEquals("ces ensembles-ci", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		WordElement cela = lexicon.lookupWord("ce -là");
		np = factory.createNounPhrase(cela, "pluie");
		np.addModifier("diluvien");
		Assert.assertEquals("cette pluie diluvienne-là", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
		np.setPlural(true);
		Assert.assertEquals("ces pluies diluviennes-là", this.realiser.realise( //$NON-NLS-1$
				np).getRealisation());
	}

	/**
	 * Test prepositional postmodification.
	 */
	@Test
	public void testPostmodification() {
		this.homme.addComplement(this.surLeRocher);
		Assert.assertEquals("l'homme sur le rocher", this.realiser.realise( //$NON-NLS-1$
				this.homme).getRealisation());

		this.femme.addComplement(this.derriereLeRideau);
		Assert.assertEquals("la femme derrière le rideau", this.realiser //$NON-NLS-1$
				.realise(this.femme).getRealisation());
	}

//	/**
//	 * Test possessive constructions.
//	 */
//	@Test
//	public void testPossessive() {
//
//		// simple possessive 's: 'a man's'
//		PhraseElement possNP = this.phraseFactory.createNounPhrase("a", "man"); //$NON-NLS-1$ //$NON-NLS-2$
//		possNP.setFeature(Feature.POSSESSIVE, true);
//		Assert.assertEquals("a man's", this.realiser.realise(possNP) //$NON-NLS-1$
//				.getRealisation());
//
//		// now set this possessive as specifier of the NP 'the dog'
//		this.chien.setFeature(InternalFeature.SPECIFIER, possNP);
//		Assert.assertEquals("a man's dog", this.realiser.realise(this.chien) //$NON-NLS-1$
//				.getRealisation());
//
//		// convert possNP to pronoun and turn "a dog" into "his dog"
//		// need to specify gender, as default is NEUTER
//		possNP.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
//		possNP.setFeature(Feature.PRONOMINAL, true);
//		Assert.assertEquals("his dog", this.realiser.realise(this.chien) //$NON-NLS-1$
//				.getRealisation());
//
//		// make it slightly more complicated: "his dog's rock"
//		this.chien.setFeature(Feature.POSSESSIVE, true); // his dog's
//
//		// his dog's rock (substituting "the"
//		// for the
//		// entire phrase)
//		this.np4.setFeature(InternalFeature.SPECIFIER, this.chien);
//		Assert.assertEquals("his dog's rock", this.realiser.realise(this.np4) //$NON-NLS-1$
//				.getRealisation());
//	}

	/**
	 * Test NP coordination.
	 */
	@Test
	public void testCoordination() {

		CoordinatedPhraseElement cnp1 = factory.createCoordinatedPhrase(this.chien,
				this.femme);
		// simple coordination
		Assert.assertEquals("le chien et la femme", this.realiser //$NON-NLS-1$
				.realise(cnp1).getRealisation());

		// simple coordination with complementation of entire coordinate NP
		cnp1.addComplement(this.derriereLeRideau);
		Assert.assertEquals("le chien et la femme derrière le rideau", //$NON-NLS-1$
				this.realiser.realise(cnp1).getRealisation());

		// raise the specifier in this cnp
		// Assert.assertEquals(true, cnp1.raiseSpecifier()); // should return
		// true as all
		// sub-nps have same spec
		// assertEquals("the dog and woman behind the curtain",
		// realiser.realise(cnp1));
	}

	/**
	 * Another battery of tests for NP coordination.
	 */
	@Test
	public void testCoordination2() {

		// simple coordination of complementised nps
		this.chien.clearComplements();
		this.femme.clearComplements();

		// RAISE_SPECIFIER does nothing in French
		CoordinatedPhraseElement cnp1 = factory.createCoordinatedPhrase(this.chien,
				this.femme);
		cnp1.setFeature(Feature.RAISE_SPECIFIER, true);
		NLGElement realised = this.realiser.realise(cnp1);
		Assert.assertEquals("le chien et la femme",  realised.getRealisation());

		this.chien.addComplement(this.surLeRocher);
		this.femme.addComplement(this.derriereLeRideau);

		CoordinatedPhraseElement cnp2 = factory.createCoordinatedPhrase(this.chien,
				this.femme);

		this.femme.setFeature(InternalFeature.RAISED, false);
		Assert.assertEquals(
				"le chien sur le rocher et la femme derrière le rideau", //$NON-NLS-1$
				this.realiser.realise(cnp2).getRealisation());

		// complementised coordinates + outer pp modifier
		cnp2.addPostModifier(this.dansLaPiece);
		Assert
				.assertEquals(
						"le chien sur le rocher et la femme derrière le rideau dans la pièce", //$NON-NLS-1$
						this.realiser.realise(cnp2).getRealisation());

		// set the specifier for this cnp; should unset specifiers for all inner
		// coordinates
		NLGElement every = this.factory.createWord(
				"un", LexicalCategory.DETERMINER); //$NON-NLS-1$

		cnp2.setFeature(InternalFeature.SPECIFIER, every);

		Assert
				.assertEquals(
						"un chien sur le rocher et une femme derrière le rideau dans la pièce", //$NON-NLS-1$
						this.realiser.realise(cnp2).getRealisation());

		// pronominalise one of the constituents
		this.chien.setFeature(Feature.PRONOMINAL, true); // ="it"
		this.chien.setFeature(InternalFeature.SPECIFIER, this.factory
				.createWord("le", LexicalCategory.DETERMINER));
		// raising spec still returns true as spec has been set
		cnp2.setFeature(Feature.RAISE_SPECIFIER, true);

		// CNP should be realised with pronominal internal const
		Assert.assertEquals(
				"lui et une femme derrière le rideau dans la pièce", //$NON-NLS-1$
				this.realiser.realise(cnp2).getRealisation());
	}

//	/**
//	 * Test possessives in coordinate NPs.
//	 */
//	@Test
//	public void testPossessiveCoordinate() {
//		// simple coordination
//		CoordinatedPhraseElement cnp2 = phraseFactory.createCoordinatedPhrase(this.chien,
//				this.femme);
//		Assert.assertEquals("le chien et la femme", this.realiser //$NON-NLS-1$
//				.realise(cnp2).getRealisation());
//
//		// set possessive -- wide-scope by default
//		cnp2.setFeature(Feature.POSSESSIVE, true);
//		Assert.assertEquals("the dog and the woman's", this.realiser.realise( //$NON-NLS-1$
//				cnp2).getRealisation());
//
//		// set possessive with pronoun
//		this.chien.setFeature(Feature.PRONOMINAL, true);
//		this.chien.setFeature(Feature.POSSESSIVE, true);
//		cnp2.setFeature(Feature.POSSESSIVE, true);
//		Assert.assertEquals("its and the woman's", this.realiser.realise(cnp2) //$NON-NLS-1$
//				.getRealisation());
//
//	}

	/**
	 * Test LE vs L'.
	 */
	@Test
	public void testApostrophe() {
		PhraseElement _chien = this.factory.createNounPhrase("le", "chien"); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.assertEquals("le chien", this.realiser.realise(_chien) //$NON-NLS-1$
				.getRealisation());

		_chien.addPreModifier("énorme"); //$NON-NLS-1$

		Assert.assertEquals("l'énorme chien", this.realiser.realise(_chien) //$NON-NLS-1$
				.getRealisation());

		PhraseElement elephant = this.factory.createNounPhrase(
				"le", "éléphant"); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.assertEquals("l'éléphant", this.realiser.realise(elephant) //$NON-NLS-1$
				.getRealisation());

		elephant.addPreModifier("gros"); //$NON-NLS-1$
		Assert.assertEquals("le gros éléphant", this.realiser.realise(elephant) //$NON-NLS-1$
				.getRealisation());

		// test treating of plural specifiers
		_chien.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		Assert.assertEquals("les énormes chiens", this.realiser.realise(_chien) //$NON-NLS-1$
				.getRealisation());
	}

	/**
	 * Test Modifier "guess" placement. 2
	 */
	@Test
	public void testModifier() {
		NPPhraseSpec _chienne = this.factory.createNounPhrase("un", "chienne"); //$NON-NLS-1$ //$NON-NLS-2$
		_chienne.addModifier("élancé"); //$NON-NLS-1$

		Assert.assertEquals("une chienne élancée", this.realiser.realise(_chienne) //$NON-NLS-1$
				.getRealisation());

		_chienne.addModifier("grand"); //$NON-NLS-1$
		Assert.assertEquals("une grande chienne élancée", this.realiser.realise(_chienne) //$NON-NLS-1$
				.getRealisation());

		_chienne.addModifier("dans le parc"); //$NON-NLS-1$
		_chienne.setPlural(true);
		Assert.assertEquals("de grandes chiennes élancées dans le parc", this.realiser.realise( //$NON-NLS-1$
				_chienne).getRealisation());
	}
		
	/**
	 * Test ordinal adjectives. They must be placed as premodifiers by default
	 * and they must not provoke vowel elision in the preceding determiner.
	 */
	@Test
	public void testOrdinalAdjectives() {
		NPPhraseSpec _chien = factory.createNounPhrase("le", "chien");
		_chien.addModifier("premier");
		Assert.assertEquals("le premier chien", realise(_chien));
		_chien.clearModifiers();
		_chien.addModifier("onzième");
		Assert.assertEquals("le onzième chien",
				realise(_chien));
		_chien.clearModifiers();
		AdjPhraseSpec ordinal = factory.createAdjectivePhrase("onzième");
		_chien.addModifier(ordinal);
		Assert.assertEquals("le onzième chien", realise(_chien));
		_chien.clearModifiers();
		_chien.addModifier("vingt et unième");
		Assert.assertEquals("le vingt et unième chien", realise(_chien));
		_chien.clearModifiers();
		ordinal = factory.createAdjectivePhrase("vingt et unième");
		_chien.addModifier(ordinal);
		Assert.assertEquals("le vingt et unième chien", realise(_chien));
	}
		
	/**
	 * Test cardinal adjectives. (Not implemented.)
	 */
/*	@Test
	public void testCardinalAdjectives() {
		NPPhraseSpec np = factory.createNounPhrase("homme");
		String[] cardinals = {"deux","trois","quatre","cinq","six","sept","huit",
				"neuf","dix","onze","douze","treize","quatorze","quinze","seize",
				"dix-sept","dix-huit","vingt","vingt et un", "vingt-deux","trente",
				"quarante","cinquante","soixante","soixante-dix","cent","deux cent",
				"deux cent un","mille","deux mille trois"};
		// Cardinals as determiners make the nound phrase plural.
		for (String det : cardinals) {
			np.setSpecifier(det);
			Assert.assertEquals(det + " hommes", realise(np));
		}
	}
*/

	/**
	 * Test ASPIRED_H French lexical feature.
	 */
	@Test
	public void testApiredH() {
		NPPhraseSpec npH = factory.createNounPhrase("le", "homme");
		Assert.assertEquals("l'homme", realise(npH));
		
		npH.setNoun("héros");
		Assert.assertEquals("le héros", realise(npH));
		
		npH.setSpecifier("son");
		npH.setNoun("habileté");
		Assert.assertEquals("son habileté", realise(npH));

		npH.setNoun("hache");
		Assert.assertEquals("sa hache", realise(npH));
	}		
}
