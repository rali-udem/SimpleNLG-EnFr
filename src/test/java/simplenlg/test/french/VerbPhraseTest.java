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

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.Form;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * These are tests for the verb phrase and coordinate VP classes.
 * @author agatt
 * translated and adapted by vaudrypl
 */
public class VerbPhraseTest extends SimpleNLG4TestBase {

	/**
	 * Instantiates a new vP test.
	 * 
	 * @param name
	 *            the name
	 */
	public VerbPhraseTest(String name) {
		super(name);
	}

	/**
	 * Tests for partitive direct object under negation.
	 */
	@Test
	public void testPartitiveNegation() {
		VPPhraseSpec boire = factory.createVerbPhrase("boire");
		NPPhraseSpec duVin = factory.createNounPhrase("du", "vin");
		boire.setObject(duVin);
		Assert.assertEquals("boit du vin",
				this.realiser.realise(boire).getRealisation()); //$NON-NLS-1$
		boire.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("ne boit pas de vin",
				this.realiser.realise(boire).getRealisation()); //$NON-NLS-1$
	}

	/**
	 * Tests for the tense and aspect.
	 */
	@Test
	public void testSimplePast() {
		// "fell down"
		this.tomber.setFeature(Feature.TENSE,Tense.PAST);
		Assert
				.assertEquals(
						"est tombé", this.realiser.realise(this.tomber).getRealisation()); //$NON-NLS-1$

		tomber.setFeature(Feature.PROGRESSIVE, true);
		tomber.setFeature(Feature.FORM, Form.SUBJUNCTIVE);
		Assert.assertEquals("ait été en train de tomber", realise(tomber));
	}

	/**
	 * Test tense aspect.
	 */
	@Test
	public void testTenseAspect() {
		// had fallen down
//		this.realiser.setLexicon(this.lexicon);
		this.tomber.setFeature(Feature.TENSE,Tense.PAST);
		this.tomber.setFeature(Feature.PERFECT, true);

		Assert.assertEquals("était tombé", this.realiser.realise( //$NON-NLS-1$
				this.tomber).getRealisation());

		// had been falling down
		this.tomber.setFeature(Feature.PROGRESSIVE, true);
		Assert.assertEquals("avait été en train de tomber", this.realiser.realise( //$NON-NLS-1$
				this.tomber).getRealisation());

		// will have been kicked
		this.frapper.setFeature(Feature.PASSIVE, true);
		this.frapper.setFeature(Feature.PERFECT, true);
		this.frapper.setFeature(Feature.TENSE,Tense.FUTURE);
		Assert.assertEquals("aura été frappé", this.realiser.realise( //$NON-NLS-1$
				this.frapper).getRealisation());

		// will have been being kicked
		this.frapper.setFeature(Feature.PROGRESSIVE, true);
		Assert.assertEquals("aura été en train d'être frappé", this.realiser //$NON-NLS-1$
				.realise(this.frapper).getRealisation());

		// will not have been being kicked
		this.frapper.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("n'aura pas été en train d'être frappé", this.realiser //$NON-NLS-1$
				.realise(this.frapper).getRealisation());

		// passivisation should suppress the complement
		this.frapper.clearComplements();
		this.frapper.addComplement(this.homme);
		Assert.assertEquals("n'aura pas été en train d'être frappé", this.realiser //$NON-NLS-1$
				.realise(this.frapper).getRealisation());

		// de-passivisation should now give us "will have been kicking the man"
		this.frapper.setFeature(Feature.PASSIVE, false);
		Assert.assertEquals("n'aura pas été en train de frapper l'homme", this.realiser //$NON-NLS-1$
				.realise(this.frapper).getRealisation());

		// remove the future tense --
		// this is a test of an earlier bug that would still realise "will"
		this.frapper.setFeature(Feature.TENSE,Tense.PRESENT);
		Assert.assertEquals("n'a pas été en train de frapper l'homme", this.realiser //$NON-NLS-1$
				.realise(this.frapper).getRealisation());
	}

	/**
	 * Test for realisation of VP complements.
	 */
	@Test
	public void testComplementation() {

		// was kissing Mary
		PhraseElement mary = this.factory.createNounPhrase("Marie"); //$NON-NLS-1$
		mary.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.OBJECT);
		this.embrasser.clearComplements();
		this.embrasser.addComplement(mary);
		this.embrasser.setFeature(Feature.PROGRESSIVE, true);
		this.embrasser.setFeature(Feature.TENSE,Tense.PAST);

		Assert.assertEquals("embrassait Marie", this.realiser //$NON-NLS-1$
				.realise(this.embrasser).getRealisation());

		CoordinatedPhraseElement mary2 = factory.createCoordinatedPhrase(mary,
				this.factory.createNounPhrase("Susanne")); //$NON-NLS-1$
		// add another complement -- should come out as "Mary and Susan"
		this.embrasser.clearComplements();
		this.embrasser.addComplement(mary2);
		Assert.assertEquals("embrassait Marie et Susanne", this.realiser //$NON-NLS-1$
				.realise(this.embrasser).getRealisation());

		// passivise -- should make the direct object complement disappear
		// Note: The verb doesn't come out as plural because agreement
		// is determined by the sentential subjects and this VP isn't inside a
		// sentence
		this.embrasser.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("était embrassé", this.realiser //$NON-NLS-1$
				.realise(this.embrasser).getRealisation());

		// make it plural (this is usually taken care of in SPhraseSpec)
		this.embrasser.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		this.embrasser.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		Assert.assertEquals("étaient embrassées", this.realiser.realise( //$NON-NLS-1$
				this.embrasser).getRealisation());

		// depassivise and add post-mod: yields "was kissing Mary in the room"
		this.embrasser.addPostModifier(this.dansLaPiece);
		this.embrasser.setFeature(Feature.PASSIVE, false);
		this.embrasser.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR);
		this.embrasser.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
		Assert.assertEquals("embrassait Marie et Susanne dans la pièce", //$NON-NLS-1$
				this.realiser.realise(this.embrasser).getRealisation());

		// passivise again: should make direct object disappear, but not postMod
		// ="was being kissed in the room"
		this.embrasser.setFeature(Feature.PASSIVE, true);
		this.embrasser.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		this.embrasser.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		Assert.assertEquals("étaient embrassées dans la pièce", this.realiser //$NON-NLS-1$
				.realise(this.embrasser).getRealisation());
	}

	/**
	 * Test for realisation of clitic VP complements.
	 * @author vaudrypl
	 */
	@Test
	public void testCliticsComplementation() {

		PhraseElement mary = this.factory.createNounPhrase("elle"); //$NON-NLS-1$
		this.embrasser.clearComplements();
		this.embrasser.setObject(mary);
		this.embrasser.setFeature(Feature.PROGRESSIVE, true);
		this.embrasser.setFeature(Feature.TENSE,Tense.PAST);

		Assert.assertEquals("l'embrassait", this.realiser //$NON-NLS-1$
				.realise(this.embrasser).getRealisation());

		NPPhraseSpec mary2 = this.factory.createNounPhrase("elles"); //$NON-NLS-1$
		// plural complement
		this.embrasser.clearComplements();
		this.embrasser.setObject(mary2);
		Assert.assertEquals("les embrassait", this.realiser //$NON-NLS-1$
				.realise(this.embrasser).getRealisation());

		// passivise -- should make the direct object complement disappear
		// Note: The verb doesn't come out as plural because agreement
		// is determined by the sentential subjects and this VP isn't inside a
		// sentence
		this.embrasser.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("était embrassé", this.realiser //$NON-NLS-1$
				.realise(this.embrasser).getRealisation());

		// make it plural and feminine (this is usually taken care of in SPhraseSpec)
		this.embrasser.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		this.embrasser.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		Assert.assertEquals("étaient embrassées", this.realiser.realise( //$NON-NLS-1$
				this.embrasser).getRealisation());

		// depassivise and add post-mod
		this.embrasser.addPostModifier(this.dansLaPiece);
		this.embrasser.setFeature(Feature.PASSIVE, false);
		this.embrasser.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR);
		this.embrasser.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
		Assert.assertEquals("les embrassait dans la pièce", //$NON-NLS-1$
				this.realiser.realise(this.embrasser).getRealisation());

		// passivise again: should make direct object disappear, but not postMod
		this.embrasser.setFeature(Feature.PASSIVE, true);
		this.embrasser.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		this.embrasser.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		Assert.assertEquals("étaient embrassées dans la pièce", this.realiser //$NON-NLS-1$
				.realise(this.embrasser).getRealisation());
		
		// two compatible pronouns
		VPPhraseSpec referer = this.factory.createVerbPhrase("référer");
		referer.setObject("il");
		referer.setIndirectObject("tu");
		Assert.assertEquals("te le réfère", this.realiser.realise(referer).getRealisation());		
		
		// "lui" and "leur" go after the direct object clitic
		referer.setObject("elle");
		referer.setIndirectObject("elle");
		Assert.assertEquals("la lui réfère", this.realiser.realise(referer).getRealisation());
		referer.setObject("elles");
		referer.setIndirectObject("elles");
		Assert.assertEquals("les leur réfère", this.realiser.realise(referer).getRealisation());

		// "me" (and others) cannot be redoubled, the indiect object stays after the verb
		// and therefore gets a preposition (independantly of the clitics selection)
		referer.setObject("moi");
		referer.setIndirectObject("moi");
		Assert.assertEquals("me réfère à moi", this.realiser.realise(referer).getRealisation());		
		referer.setObject("se");
		referer.setIndirectObject("se");
		Assert.assertEquals("se réfère à soi",
				this.realiser.realise(referer).getRealisation());		
		
		// "me" (and others) cannot be side to side with "lui" and "leur"
		referer.setObject("nous");
		referer.setIndirectObject("leur");
		Assert.assertEquals("nous réfère à eux", this.realiser.realise(referer).getRealisation());		
		referer.setObject("moi");
		referer.setIndirectObject("il");
		Assert.assertEquals("me réfère à lui", this.realiser.realise(referer).getRealisation());	
		
		// "y" and "en" go after the other clitics, in that order
		referer.addComplement("en");
		referer.addComplement("y");
		Assert.assertEquals("m'y en réfère à lui", this.realiser.realise(referer).getRealisation());
		
		// clitics go before the first verb element
		referer.clearComplements();
		referer.setObject("elle");
		referer.setIndirectObject("elle");
		referer.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals("la lui a référée", this.realiser.realise(referer).getRealisation());

		// verb complement clitics go between "ne" and the first verb element when negated
		referer.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("ne la lui a pas référée", this.realiser.realise(referer).getRealisation());
		referer.setFeature(Feature.NEGATED, false);
		
		// clitics go before the infinitive main verb
		referer.setFeature(Feature.TENSE, Tense.PRESENT);
		referer.setFeature(Feature.PROGRESSIVE, true);
		Assert.assertEquals("est en train de la lui référer", this.realiser.realise(referer).getRealisation());
		
		// test with pronominalised noun phrase
		NPPhraseSpec objet = this.factory.createNounPhrase("la", "chienne");
		objet.setFeature(Feature.PRONOMINAL, true);
		referer.setObject(objet);
		Assert.assertEquals("est en train de la lui référer", this.realiser.realise(referer).getRealisation());
		
		// with modal verbs, clitic rising only occurs on a limited number of verbs, like "faire"
		VPPhraseSpec voir = this.factory.createVerbPhrase("voir");
		voir.setObject("il");
		voir.setFeature(Feature.MODAL, "vouloir");
		Assert.assertEquals("veut le voir", this.realiser.realise(voir).getRealisation());
		voir.setFeature(Feature.MODAL, "faire");
		Assert.assertEquals("le fait voir", this.realiser.realise(voir).getRealisation());
		
		// imperative
		voir.setFeature(Feature.FORM, Form.IMPERATIVE);
		voir.setFeature(Feature.MODAL, "vouloir");
		Assert.assertEquals("vois-le", this.realiser.realise(voir).getRealisation());
		voir.setFeature(Feature.MODAL, "faire");
		Assert.assertEquals("fais-le voir", this.realiser.realise(voir).getRealisation());
		this.donner.clearComplements();
		this.donner.setFeature(Feature.FORM, Form.IMPERATIVE);
		this.donner.setIndirectObject("je");
		this.donner.addComplement("en");
		Assert.assertEquals("donne-m'en", this.realiser.realise(this.donner).getRealisation());
		this.donner.clearComplements();
		this.donner.setObject("il");
		this.donner.setIndirectObject("elle");
		this.donner.addModifier("tout de suite");
		Assert.assertEquals("donne-le-lui tout de suite", this.realiser.realise(this.donner).getRealisation());
		this.donner.setIndirectObject("je");
		Assert.assertEquals("donne-moi-le tout de suite", this.realiser.realise(this.donner).getRealisation());
		this.donner.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("ne me le donne pas tout de suite", this.realiser.realise(this.donner).getRealisation());
	}

	/**
	 * This tests for the default complement ordering, relative to pre and
	 * postmodifiers.
	 */
	@Test
	public void testComplementation2() {
		// give the woman the dog
		this.femme.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.INDIRECT_OBJECT);
		this.chien.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.OBJECT);
		this.donner.clearComplements();
		this.donner.setFeature(Feature.TENSE, Tense.PAST);
		this.donner.setFeature(Feature.PERFECT, true);
		this.donner.setFeature(Feature.PROGRESSIVE, true);
		this.donner.setFeature(Feature.NEGATED, true);
		this.donner.addComplement(this.chien);
		this.donner.addComplement(this.femme);
		Assert.assertEquals("n'avait pas été en train de donner le chien à la femme", this.realiser.realise( //$NON-NLS-1$
				this.donner).getRealisation());

		// add a few premodifiers and postmodifiers
		this.donner.addPreModifier("lentement"); //$NON-NLS-1$
		this.donner.addModifier(this.derriereLeRideau);
		this.donner.addPostModifier(this.dansLaPiece);
		Assert
				.assertEquals(
						"n'avait pas été en train de donner lentement le chien à la femme derrière le rideau dans la pièce", //$NON-NLS-1$
						this.realiser.realise(this.donner).getRealisation());

		this.donner.setFeature(Feature.TENSE, Tense.PRESENT);
		this.donner.setFeature(Feature.PERFECT, false);
		this.donner.setFeature(Feature.PROGRESSIVE, false);
		this.donner.setFeature(Feature.NEGATED, false);
		Assert
		.assertEquals(
				"donne lentement le chien à la femme derrière le rideau dans la pièce", //$NON-NLS-1$
				this.realiser.realise(this.donner).getRealisation());

		// reset the arguments
		this.donner.clearComplements();
		this.donner.addComplement(this.chien);
		CoordinatedPhraseElement womanBoy = factory.createCoordinatedPhrase(
				this.femme, this.garcon);
		womanBoy.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.INDIRECT_OBJECT);
		this.donner.addComplement(womanBoy);

		// if we unset the passive, we should get the indirect objects
		// they won't be coordinated
		this.donner.setFeature(Feature.PASSIVE, false);
		Assert
				.assertEquals(
						"donne lentement le chien à la femme et au garçon derrière le rideau dans la pièce", //$NON-NLS-1$
						this.realiser.realise(this.donner).getRealisation());

		// set them to a coordinate instead
		// set ONLY the complement INDIRECT_OBJECT, leaves OBJECT intact
		this.donner.clearComplements();
		this.donner.addComplement(womanBoy);
		this.donner.addComplement(this.chien);
		List<NLGElement> complements = this.donner
				.getFeatureAsElementList(InternalFeature.COMPLEMENTS);

		int indirectCount = 0;
		for (NLGElement eachElement : complements) {
			if (DiscourseFunction.INDIRECT_OBJECT.equals(eachElement
					.getFeature(InternalFeature.DISCOURSE_FUNCTION))) {
				indirectCount++;
			}
		}
		Assert.assertEquals(1, indirectCount); // only one indirect object
		// where
		// there were two before

		Assert.assertEquals(
						"donne lentement le chien à la femme et au garçon derrière le rideau dans la pièce", //$NON-NLS-1$
						this.realiser.realise(this.donner).getRealisation());
		
		// with an auxiliary
		this.donner.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals(
				"a lentement donné le chien à la femme et au garçon derrière le rideau dans la pièce", //$NON-NLS-1$
				this.realiser.realise(this.donner).getRealisation());

		// with "lentement" as complement instead of premodifier
		// it is placed in first in the complements because it is shorter than the other complements
		// (direct and indirect objects)
		this.donner.removeFeature(InternalFeature.PREMODIFIERS);
		this.donner.addComplement("lentement");
		Assert.assertEquals(
				"a donné lentement le chien à la femme et au garçon derrière le rideau dans la pièce", //$NON-NLS-1$
				this.realiser.realise(this.donner).getRealisation());
	}

	/**
	 * Test for partitif direct object.
	 */
	@Test
	public void testPartitifObject() {
		VPPhraseSpec avoir = this.factory.createVerbPhrase("avoir");
		NPPhraseSpec beurre = this.factory.createNounPhrase("du", "beurre");
		avoir.setObject(beurre);
		Assert.assertEquals("a du beurre", this.realiser.realise( //$NON-NLS-1$
				avoir).getRealisation());
		
		avoir.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("n'a pas de beurre", this.realiser.realise( //$NON-NLS-1$
				avoir).getRealisation());
	}
	
	/**
	 * Test for complements raised in the passive case.
	 */
	@Test
	public void testPassiveComplement() {
		// add some arguments
		this.chien.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.OBJECT);
		this.femme.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.INDIRECT_OBJECT);
		this.donner.addComplement(this.chien);
		this.donner.addComplement(this.femme);
		Assert.assertEquals("donne le chien à la femme", this.realiser.realise( //$NON-NLS-1$
				this.donner).getRealisation());

		// add a few premodifiers and postmodifiers
		this.donner.addModifier("lentement"); //$NON-NLS-1$
		this.donner.addPostModifier(this.derriereLeRideau);
		this.donner.addPostModifier(this.dansLaPiece);
		Assert
				.assertEquals(
						"donne le chien à la femme lentement derrière le rideau dans la pièce", //$NON-NLS-1$
						this.realiser.realise(this.donner).getRealisation());

		// passivise: This should suppress "the dog"
		this.donner.clearComplements();
		this.donner.addComplement(this.chien);
		this.donner.addComplement(this.femme);
		this.donner.setFeature(Feature.PASSIVE, true);

		Assert.assertEquals(
				"est donné à la femme lentement derrière le rideau dans la pièce", //$NON-NLS-1$
				this.realiser.realise(this.donner).getRealisation());
	}

	/**
	 * Test VP with sentential complements. This tests for structures like "said
	 * that John was walking"
	 */
	@Test
	public void testClausalComp() {
		this.factory.setLexicon(this.lexicon);
		SPhraseSpec s = this.factory.createClause();

		s.setSubject(this.factory
				.createNounPhrase("Jean")); //$NON-NLS-1$

		// Create a sentence first
		CoordinatedPhraseElement maryAndSusan = factory.createCoordinatedPhrase(
				this.factory.createNounPhrase("Marie"), //$NON-NLS-1$
				this.factory.createNounPhrase("Susanne")); //$NON-NLS-1$

		this.embrasser.clearComplements();
		s.setVerbPhrase(this.embrasser);
		s.setObject(maryAndSusan);
		s.setFeature(Feature.PROGRESSIVE, true);
		s.setFeature(Feature.TENSE,Tense.PAST);
		s.addPostModifier(this.dansLaPiece);
		Assert.assertEquals("Jean embrassait Marie et Susanne dans la pièce", //$NON-NLS-1$
				this.realiser.realise(s).getRealisation());

		// make the main VP past
		this.dire.setFeature(Feature.TENSE,Tense.PAST);
		Assert.assertEquals("a dit", this.realiser.realise(this.dire) //$NON-NLS-1$
				.getRealisation());

		// now add the sentence as complement of "say". Should make the sentence
		// subordinate
		// note that sentential punctuation is suppressed
		this.dire.addComplement(s);
		Assert.assertEquals(
				"a dit que Jean embrassait Marie et Susanne dans la pièce", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());

		// add a postModifier to the main VP
		// yields [says [that John was kissing Mary and Susan in the room]
		// [behind the curtain]]
		this.dire.addPostModifier(this.derriereLeRideau);
		Assert
				.assertEquals(
						"a dit que Jean embrassait Marie et Susanne dans la pièce derrière le rideau", //$NON-NLS-1$
						this.realiser.realise(this.dire).getRealisation());

		// create a new sentential complement
		PhraseElement s2 = this.factory.createClause(this.factory
				.createNounPhrase("tout"), //$NON-NLS-1$
				"aller", //$NON-NLS-1$
				this.factory.createAdjectivePhrase("bien")); //$NON-NLS-1$

		s2.setFeature(Feature.TENSE,Tense.FUTURE);
		Assert.assertEquals("tout ira bien", this.realiser.realise(s2) //$NON-NLS-1$
				.getRealisation());

		// add the new complement to the VP
		// yields [said [that John was kissing Mary and Susan in the room and
		// all will be fine] [behind the curtain]]
		CoordinatedPhraseElement s3 = factory.createCoordinatedPhrase(s, s2);
		this.dire.clearComplements();
		this.dire.addComplement(s3);

		// first with outer complementiser suppressed
		s3.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);
		Assert.assertEquals(
				"a dit que Jean embrassait Marie et Susanne dans la pièce " //$NON-NLS-1$
						+ "et tout ira bien derrière le rideau", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());

		setUp();
		s = this.factory.createClause();

		s.setSubject(this.factory
				.createNounPhrase("Jean")); //$NON-NLS-1$

		// Create a sentence first
		maryAndSusan = factory.createCoordinatedPhrase(
				this.factory.createNounPhrase("Marie"), //$NON-NLS-1$
				this.factory.createNounPhrase("Susanne")); //$NON-NLS-1$

		s.setVerbPhrase(this.embrasser);
		s.setObject(maryAndSusan);
		s.setFeature(Feature.PROGRESSIVE, true);
		s.setFeature(Feature.TENSE,Tense.PAST);
		s.addPostModifier(this.dansLaPiece);
		s2 = this.factory.createClause(this.factory
				.createNounPhrase("tout"), //$NON-NLS-1$
				"aller", //$NON-NLS-1$
				this.factory.createAdjectivePhrase("bien")); //$NON-NLS-1$

		s2.setFeature(Feature.TENSE,Tense.FUTURE);
		// then with complementiser not suppressed and not aggregated
		s3 = factory.createCoordinatedPhrase(s, s2);
		this.dire.addComplement(s3);
		this.dire.setFeature(Feature.TENSE,Tense.PAST);
		this.dire.addPostModifier(this.derriereLeRideau);
		
		Assert.assertEquals(
				"a dit que Jean embrassait Marie et Susanne dans la pièce et " //$NON-NLS-1$
						+ "que tout ira bien derrière le rideau", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());

	}

	/**
	 * Test VP coordination and aggregation:
	 * <OL>
	 * <LI>If the simplenlg.features of a coordinate VP are set, they should be
	 * inherited by its daughter VP;</LI>
	 * <LI>2. We can aggregate the coordinate VP so it's realised with one
	 * wide-scope auxiliary</LI>
	 */
	@Test
	public void testCoordination() {
		// simple case
		this.embrasser.addComplement(this.homme);
		this.frapper.addComplement(this.garcon);

		CoordinatedPhraseElement coord1 = factory.createCoordinatedPhrase(
				this.embrasser, this.frapper);

		coord1.setFeature(Feature.PERSON, Person.THIRD);
		coord1.setFeature(Feature.TENSE,Tense.PAST);
		Assert.assertEquals("a embrassé l'homme et a frappé le garçon", this.realiser //$NON-NLS-1$
				.realise(coord1).getRealisation());

		// with negation: should be inherited by all components
		coord1.setFeature(Feature.NEGATED, true);
//		this.realiser.setLexicon(this.lexicon);
		Assert.assertEquals("n'a pas embrassé l'homme et n'a pas frappé le garçon", //$NON-NLS-1$
				this.realiser.realise(coord1).getRealisation());

		// set a modal
		coord1.setFeature(Feature.MODAL, "pouvoir"); //$NON-NLS-1$
		Assert
				.assertEquals(
						"ne peut pas avoir embrassé l'homme et ne peut pas avoir frappé le garçon", //$NON-NLS-1$
						this.realiser.realise(coord1).getRealisation());

		// set perfect and progressive
		coord1.setFeature(Feature.PERFECT, true);
		coord1.setFeature(Feature.PROGRESSIVE, true);
		Assert.assertEquals("ne peut pas avoir été en train d'embrasser l'homme et " //$NON-NLS-1$
				+ "ne peut pas avoir été en train de frapper le garçon", this.realiser.realise( //$NON-NLS-1$
				coord1).getRealisation());

		// now aggregate
		coord1.setFeature(Feature.AGGREGATE_AUXILIARY, true);
		Assert.assertEquals(
				"ne peut pas avoir été en train d'embrasser l'homme et frapper le garçon", //$NON-NLS-1$
				this.realiser.realise(coord1).getRealisation());
	}


	/**
	 * Test ASPIRED_H French lexical feature.
	 */
	@Test
	public void testApiredH() {
		VPPhraseSpec vpH = factory.createVerbPhrase("habiller");
		vpH.setObject("se");
		Assert.assertEquals("s'habille", realise(vpH));
		
		vpH.setVerb("hâter");
		Assert.assertEquals("se hâte", realise(vpH));
	}		
}
