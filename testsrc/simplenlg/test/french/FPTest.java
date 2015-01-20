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

import org.junit.Before;
import org.junit.Test;

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.features.Gender;
import simplenlg.features.LexicalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;

/**
 * Test added to break the realiser 
 * 
 * @author portet
 * 
 * translated and adapted by vaudrypl
 */

public class FPTest extends SimpleNLG4TestBase {
	
	
	NLGFactory docFactory;
	
	/**
	 * Instantiates a new text spec test.
	 * 
	 * @param name
	 *            the name
	 */
	public FPTest(String name) {
		super(name);
	}

	/**
	 * Set up the variables we'll need for this simplenlg.test to run (Called
	 * automatically by JUnit)
	 */
	@Override
	@Before
	protected void setUp() {
		super.setUp();
		docFactory = new NLGFactory(this.lexicon);
	}

	/**
	 * Basic tests.
	 */
	@Test
	public void testHerLover() {
		this.factory.setLexicon(this.lexicon);
//		this.realiser.setLexicon(this.lexicon);
		
		// Create the pronoun 'she'
		NLGElement son = factory.createWord("son",LexicalCategory.DETERMINER);

		// Set possessive on the pronoun to make it 'her'
//		she.setFeature(Feature.POSSESSIVE, true);

		// Create a noun phrase with the subject lover and the determiner
		// as she
		PhraseElement herLover = factory.createNounPhrase(son,"amant");

		// Create a clause to say 'he be her lover'
		PhraseElement clause = factory.createClause("il", "être", herLover);

		// Add the cue phrase need the comma as orthography
		// currently doesn't handle this.
		// This could be expanded to be a noun phrase with determiner
		// 'two' and noun 'week', set to plural and with a premodifier of
		// 'after'
		clause.setFeature(Feature.CUE_PHRASE, "après deux semaines");

		// Add the 'for a fortnight' as a post modifier. Alternatively
		// this could be added as a prepositional phrase 'for' with a
		// complement of a noun phrase ('a' 'fortnight')
		clause.addPostModifier("pour une quinzaine de jours");

		// Set 'be' to 'was' as past tense
		clause.setFeature(Feature.TENSE,Tense.PAST);

		// Add the clause to a sentence.
		DocumentElement sentence1 = docFactory.createSentence(clause);

		// Realise the sentence
//		realiser.setDebugMode(true);
		NLGElement realised = this.realiser.realise(sentence1);

		// Retrieve the realisation and dump it to the console
//		System.out.println(realised.getRealisation()); 		
//		Assert.assertEquals("After two weeks, he was her lover for a fortnight.",
		Assert.assertEquals("Après deux semaines, il a été son amant pour une quinzaine de jours.",
				realised.getRealisation());
	}

	/**
	 * Basic tests.
	 */
	@Test
	public void testHerLovers() {
		this.factory.setLexicon(this.lexicon);

		// Create the pronoun 'she'
		NLGElement son = factory.createWord("son",LexicalCategory.DETERMINER);

		// Set possessive on the pronoun to make it 'her'
//		she.setFeature(Feature.POSSESSIVE, true);

		// Create a noun phrase with the subject lover and the determiner
		// as she
		PhraseElement herLover = factory.createNounPhrase(son,"amant");
		herLover.setPlural(true);
		herLover.setFeature(LexicalFeature.GENDER, Gender.FEMININE);

		// Create the pronoun 'he'
		NLGElement he = factory.createNounPhrase("elle");
		he.setPlural(true);

		// Create a clause to say 'they be her lovers'
		PhraseElement clause = factory.createClause(he, "être", herLover);

		// Add the cue phrase need the comma as orthography
		// currently doesn't handle this.
		// This could be expanded to be a noun phrase with determiner
		// 'two' and noun 'week', set to plural and with a premodifier of
		// 'after'
		clause.setFeature(Feature.CUE_PHRASE, "après deux semaines");

		// Add the 'for a fortnight' as a post modifier. Alternatively
		// this could be added as a prepositional phrase 'for' with a
		// complement of a noun phrase ('a' 'fortnight')
		clause.addPostModifier("pour une quinzaine de jours");

		// Set 'be' to 'was' as past tense
		clause.setFeature(Feature.TENSE,Tense.PAST);
		
		// Add the clause to a sentence.
		DocumentElement sentence1 = docFactory.createSentence(clause);

		// Realise the sentence
		NLGElement realised = this.realiser.realise(sentence1);

		// Retrieve the realisation and dump it to the console
//		System.out.println(realised.getRealisation()); 

		Assert.assertEquals("Après deux semaines, elles ont été ses amantes pour une quinzaine de jours.", //$NON-NLS-1$
				realised.getRealisation());
	}

	/**
	 * combine two S's using cue phrase and gerund.
	 */
	@Test
	public void testDavesHouse() {
		this.factory.setLexicon(this.lexicon);

		PhraseElement born = factory.createClause("Dave Bus", "être", "né");
//		born.setFeature(Feature.TENSE,Tense.PAST);
//		born.addPostModifier("à");
		born.setFeature(Feature.COMPLEMENTISER, "dans laquelle");

		PhraseElement theHouse = factory.createNounPhrase("le", "maison");
		theHouse.addComplement(born);

		PhraseElement clause = factory.createClause(theHouse, "être", factory.createPrepositionPhrase("à", "Édinbourg"));
		DocumentElement sentence = docFactory.createSentence(clause);
		NLGElement realised = realiser.realise(sentence);
//		System.out.println(realised.getRealisation()); 

		// Retrieve the realisation and dump it to the console
//		Assert.assertEquals("The house which Dave Bus was born in is in Edinburgh.",
		Assert.assertEquals("La maison dans laquelle Dave Bus est né est à Édinbourg.",
				realised.getRealisation());
	}

	/**
	 * combine two S's using cue phrase and gerund.
	 */
	@Test
	public void testDaveAndAlbertsHouse() {
		this.factory.setLexicon(this.lexicon);

		NLGElement dave = factory.createWord("Dave Bus", LexicalCategory.NOUN);
		NLGElement albert = factory.createWord("Albert", LexicalCategory.NOUN);
		
		CoordinatedPhraseElement coord1 = factory.createCoordinatedPhrase(
				dave, albert);
		
		PhraseElement born = factory.createClause(coord1, "être", "né");
//		born.setFeature(Feature.TENSE,Tense.PAST);
//		born.addPostModifier("in");
		born.setFeature(Feature.COMPLEMENTISER, "dans laquelle");

		PhraseElement theHouse = factory.createNounPhrase("le", "maison");
		theHouse.addComplement(born);

		PhraseElement clause = factory.createClause(theHouse, "être", factory.createPrepositionPhrase("à", "Édinbourg"));
		DocumentElement sentence = docFactory.createSentence(clause);
		
		// Retrieve the realisation and dump it to the console
		NLGElement realised = realiser.realise(sentence);
//		System.out.println(realised.getRealisation()); 

//		Assert.assertEquals("The house which Dave Bus and Albert were born in is in Edinburgh.",
		Assert.assertEquals("La maison dans laquelle Dave Bus et Albert sont nés est à Édinbourg.",
				realised.getRealisation());
	}

	
	@Test
	public void testEngineerHolidays() {
		this.factory.setLexicon(this.lexicon);

		// Inner clause is 'I' 'make' 'sentence' 'for'.
		PhraseElement inner = factory.createClause("je","faire", "un gâteau");
		// Inner clause set to progressive.
//		inner.setFeature(Feature.PROGRESSIVE,true);
		
		//Complementiser on inner clause is 'whom'
		inner.setFeature(Feature.COMPLEMENTISER, "à qui");
		
		// create the engineer and add the inner clause as post modifier 
		PhraseElement engineer = factory.createNounPhrase("l'ingénieur");
		engineer.addComplement(inner);
		
		// Outer clause is: 'the engineer' 'go' (preposition 'to' 'holidays')
		PhraseElement outer = factory.createClause(engineer,"aller",factory.createPrepositionPhrase("en","vacances"));

		// Outer clause tense is Future.
		outer.setFeature(Feature.TENSE, Tense.FUTURE);
		
		// Possibly progressive as well not sure.
//		outer.setFeature(Feature.PROGRESSIVE,true);
		
		//Outer clause postmodifier would be 'tomorrow'
		outer.addPostModifier("demain");
		DocumentElement sentence = docFactory.createSentence(outer);
		NLGElement realised = realiser.realise(sentence);
//		System.out.println(realised.getRealisation()); 

		// Retrieve the realisation and dump it to the console
//		Assert.assertEquals("The engineer whom I am making sentence for will be going to holidays tomorrow.",
		Assert.assertEquals("L'ingénieur à qui je fais un gâteau ira en vacances demain.",
				realised.getRealisation());
	}

	
	@Test
	public void testHousePoker() {
		setUp();
//		this.realiser.setLexicon(this.lexicon);
		
		PhraseElement inner = factory.createClause("je", "jouer", "au poker");
		inner.setFeature(Feature.TENSE,Tense.PAST);
		inner.setFeature(Feature.COMPLEMENTISER, "où");
		
		PhraseElement house = factory.createNounPhrase("le", "maison");
		house.addComplement(inner);
		
		SPhraseSpec outer = factory.createClause(null, "abandonner", house);
		
		outer.addPostModifier("depuis 1986");
		
		outer.setFeature(Feature.PASSIVE, true);
		outer.setFeature(Feature.PERFECT, true);
		
		DocumentElement sentence = docFactory.createSentence(outer);
		NLGElement realised = realiser.realise(sentence);
//		System.out.println(realised.getRealisation()); 

		// Retrieve the realisation and dump it to the console
//		Assert.assertEquals("The house where I played poker has been abandoned since 1986.",
		Assert.assertEquals("La maison où j'ai joué au poker a été abandonnée depuis 1986.",
				realised.getRealisation());
	}
	
	
	@Test
	public void testMayonnaise() {
		this.factory.setLexicon(this.lexicon);

		NPPhraseSpec sandwich = factory.createNounPhrase(LexicalCategory.NOUN, "sandwich");
		sandwich.setPlural(true);
		sandwich.setSpecifier("un");
		// 
		SPhraseSpec first = factory.createClause("je", "faire", sandwich);
		first.setIndirectObject("moi");
		first.setFeature(Feature.TENSE,Tense.PAST);
		first.setFeature(Feature.PROGRESSIVE,true);
		first.setPlural(false);
		
		PhraseElement second = factory.createClause("la mayonnaise", "couler");
		second.setFeature(Feature.TENSE,Tense.PAST);
		// 
		second.setFeature(Feature.COMPLEMENTISER, "quand");
		
		first.addComplement(second);
		
		DocumentElement sentence = docFactory.createSentence(first);
		// Retrieve the realisation and dump it to the console
		NLGElement realised = realiser.realise(sentence);
//		System.out.println(realised.getRealisation()); 

//		Assert.assertEquals("I was making sandwiches when the mayonnaise ran out.",
		Assert.assertEquals("Je me faisais des sandwichs quand la mayonnaise a coulé.",
				realised.getRealisation());
	}
	
	
	
}
