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

package simplenlg.test.english;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import simplenlg.features.Feature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.phrasespec.SPhraseSpec;

/**
 * JUnit test case for interrogatives.
 * 
 * @author agatt
 */
public class InterrogativeTest extends SimpleNLG4TestBase {

	// set up a few more fixtures
	/** The s5. */
	SPhraseSpec s1, s2, s3, s4, s5;

	/**
	 * Instantiates a new interrogative test.
	 * 
	 * @param name
	 *            the name
	 */
	public InterrogativeTest(String name) {
		super(name);
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

		// the woman kissed the man behind the curtain

		// // there is the dog on the rock
		// this.s2 = new SPhraseSpec();
		// this.s2.setSubject("there");
		// this.s2.setHead("be");
		// this.s2.setComplement(this.dog);
		// this.s2.addModifier(SModifierPosition.POST_VERB, this.onTheRock);
		//
		// // the man gives the woman John's flower
		PhraseElement john = this.phraseFactory.createNounPhrase("John"); //$NON-NLS-1$
		john.setFeature(Feature.POSSESSIVE, true);
		PhraseElement flower = this.phraseFactory.createNounPhrase(john,
				"flower"); //$NON-NLS-1$
		PhraseElement _woman = this.phraseFactory.createNounPhrase(
				"the", "woman"); //$NON-NLS-1$ //$NON-NLS-2$
		this.s3 = this.phraseFactory.createClause(this.man, this.give, flower);
		this.s3.setIndirectObject(_woman);

		CoordinatedPhraseElement subjects = phraseFactory.createCoordinatedPhrase(
				this.phraseFactory.createNounPhrase("Jane"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Andrew")); //$NON-NLS-1$
		this.s4 = this.phraseFactory.createClause(subjects, "pick up", //$NON-NLS-1$
				"the balls"); //$NON-NLS-1$
		this.s4.addPostModifier("in the shop"); //$NON-NLS-1$
		this.s4.setFeature(Feature.CUE_PHRASE, "however"); //$NON-NLS-1$
		this.s4.addFrontModifier("tomorrow"); //$NON-NLS-1$
		this.s4.setFeature(Feature.TENSE,Tense.FUTURE);
		// this.s5 = new SPhraseSpec();
		// this.s5.setSubject(new NPPhraseSpec("the", "dog"));
		// this.s5.setHead("be");
		// this.s5.setComplement(new NPPhraseSpec("the", "rock"),
		// DiscourseFunction.OBJECT);

	}

	/**
	 * Tests a couple of fairly simple questions.
	 */
	@Test
	public void testSimpleQuestions() {
		setUp();
		this.phraseFactory.setLexicon(this.lexicon);
//		this.realiser.setLexicon(this.lexicon);

		// simple present
		this.s1 = this.phraseFactory.createClause(this.woman, this.kiss,
				this.man);
		this.s1.setFeature(Feature.TENSE,Tense.PRESENT);
		this.s1.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);

		NLGFactory docFactory = new NLGFactory(this.lexicon);
		DocumentElement sent = docFactory.createSentence(this.s1);
		Assert.assertEquals("Does the woman kiss the man?", this.realiser //$NON-NLS-1$
				.realise(sent).getRealisation());

		// simple past
		// sentence: "the woman kissed the man"
		this.s1 = this.phraseFactory.createClause(this.woman, this.kiss,
				this.man);
		this.s1.setFeature(Feature.TENSE,Tense.PAST);
		this.s1.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals("did the woman kiss the man", this.realiser //$NON-NLS-1$
				.realise(this.s1).getRealisation());

		// copular/existential: be-fronting
		// sentence = "there is the dog on the rock"
		this.s2 = this.phraseFactory.createClause("there", "be", this.dog); //$NON-NLS-1$ //$NON-NLS-2$
		this.s2.addPostModifier(this.onTheRock);
		this.s2.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals("is there the dog on the rock", this.realiser //$NON-NLS-1$
				.realise(this.s2).getRealisation());

		// perfective
		// sentence -- "there has been the dog on the rock"
		this.s2 = this.phraseFactory.createClause("there", "be", this.dog); //$NON-NLS-1$ //$NON-NLS-2$
		this.s2.addPostModifier(this.onTheRock);
		this.s2.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		this.s2.setFeature(Feature.PERFECT, true);
		Assert.assertEquals("has there been the dog on the rock", //$NON-NLS-1$
				this.realiser.realise(this.s2).getRealisation());

		// progressive
		// sentence: "the man was giving the woman John's flower"
		PhraseElement john = this.phraseFactory.createNounPhrase("John"); //$NON-NLS-1$
		john.setFeature(Feature.POSSESSIVE, true);
		PhraseElement flower = this.phraseFactory.createNounPhrase(john,
				"flower"); //$NON-NLS-1$
		PhraseElement _woman = this.phraseFactory.createNounPhrase(
				"the", "woman"); //$NON-NLS-1$ //$NON-NLS-2$
		this.s3 = this.phraseFactory.createClause(this.man, this.give, flower);
		this.s3.setIndirectObject(_woman);
		this.s3.setFeature(Feature.TENSE,Tense.PAST);
		this.s3.setFeature(Feature.PROGRESSIVE, true);
		this.s3.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		NLGElement realised = this.realiser.realise(this.s3);
		Assert.assertEquals("was the man giving the woman John's flower", //$NON-NLS-1$
				realised.getRealisation());

		// modal
		// sentence: "the man should be giving the woman John's flower"
		setUp();
		john = this.phraseFactory.createNounPhrase("John"); //$NON-NLS-1$
		john.setFeature(Feature.POSSESSIVE, true);
		flower = this.phraseFactory.createNounPhrase(john, "flower"); //$NON-NLS-1$
		_woman = this.phraseFactory.createNounPhrase("the", "woman"); //$NON-NLS-1$ //$NON-NLS-2$
		this.s3 = this.phraseFactory.createClause(this.man, this.give, flower);
		this.s3.setIndirectObject(_woman);
		this.s3.setFeature(Feature.TENSE,Tense.PAST);
		this.s3.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		this.s3.setFeature(Feature.MODAL, "should"); //$NON-NLS-1$
		Assert.assertEquals(
				"should the man have given the woman John's flower", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());

		// complex case with cue phrases
		// sentence: "however, tomorrow, Jane and Andrew will pick up the balls
		// in the shop"
		// this gets the front modifier "tomorrow" shifted to the end
		setUp();
		CoordinatedPhraseElement subjects = phraseFactory.createCoordinatedPhrase(
				this.phraseFactory.createNounPhrase("Jane"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Andrew")); //$NON-NLS-1$
		this.s4 = this.phraseFactory.createClause(subjects, "pick up", //$NON-NLS-1$
				"the balls"); //$NON-NLS-1$
		this.s4.addPostModifier("in the shop"); //$NON-NLS-1$
		this.s4.setFeature(Feature.CUE_PHRASE, "however,"); //$NON-NLS-1$
		this.s4.addFrontModifier("tomorrow"); //$NON-NLS-1$
		this.s4.setFeature(Feature.TENSE,Tense.FUTURE);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert
				.assertEquals(
						"however, will Jane and Andrew pick up the balls in the shop tomorrow", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());
	}

	/**
	 * Test for sentences with negation.
	 */
	@Test
	public void testNegatedQuestions() {
		setUp();
		this.phraseFactory.setLexicon(this.lexicon);
//		this.realiser.setLexicon(this.lexicon);

		// sentence: "the woman did not kiss the man"
		this.s1 = this.phraseFactory.createClause(this.woman, "kiss",
				this.man);
		this.s1.setFeature(Feature.TENSE,Tense.PAST);
		this.s1.setNegated(true);
		this.s1.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals("did the woman not kiss the man", this.realiser //$NON-NLS-1$
				.realise(this.s1).getRealisation());

		// sentence: however, tomorrow, Jane and Andrew will not pick up the
		// balls in the shop
		CoordinatedPhraseElement subjects = phraseFactory.createCoordinatedPhrase(
				this.phraseFactory.createNounPhrase("Jane"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Andrew")); //$NON-NLS-1$
		this.s4 = this.phraseFactory.createClause(subjects, "pick up", //$NON-NLS-1$
				"the balls"); //$NON-NLS-1$
		this.s4.addPostModifier("in the shop"); //$NON-NLS-1$
		this.s4.setFeature(Feature.CUE_PHRASE, "however,"); //$NON-NLS-1$
		this.s4.addFrontModifier("tomorrow"); //$NON-NLS-1$
		this.s4.setFeature(Feature.NEGATED,true);
		this.s4.setFeature(Feature.TENSE,Tense.FUTURE);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert
				.assertEquals(
						"however, will Jane and Andrew not pick up the balls in the shop tomorrow", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());
	}

	/**
	 * Tests for coordinate VPs in question form.
	 */
	@Test
	public void testCoordinateVPQuestions() {

		// create a complex vp: "kiss the dog and walk in the room"
		setUp();
		CoordinatedPhraseElement complex = phraseFactory.createCoordinatedPhrase(
				this.kiss, this.walk);
		this.kiss.addComplement(this.dog);
		this.walk.addComplement(this.inTheRoom);

		// sentence: "However, tomorrow, Jane and Andrew will kiss the dog and
		// will walk in the room"
		CoordinatedPhraseElement subjects = phraseFactory.createCoordinatedPhrase(
				this.phraseFactory.createNounPhrase("Jane"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Andrew")); //$NON-NLS-1$
		this.s4 = this.phraseFactory.createClause(subjects, complex);
		this.s4.setFeature(Feature.CUE_PHRASE, "however"); //$NON-NLS-1$
		this.s4.addFrontModifier("tomorrow"); //$NON-NLS-1$
		this.s4.setFeature(Feature.TENSE,Tense.FUTURE);
		Assert
				.assertEquals(
						"however tomorrow Jane and Andrew will kiss the dog and will walk in the room", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());
		// Added by vaudrypl
		this.s4.setFeature(Feature.TENSE,Tense.PRESENT);
		this.s4.setFeature(Feature.PERFECT,true);
		Assert
				.assertEquals(
						"however tomorrow Jane and Andrew have kissed the dog and walked in the room", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());
		this.s4.setFeature(Feature.PERFECT,false);

		// setting to interrogative should automatically give us a single,
		// wide-scope aux
		setUp();
		subjects = phraseFactory.createCoordinatedPhrase(this.phraseFactory
				.createNounPhrase("Jane"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Andrew")); //$NON-NLS-1$
		this.kiss.addComplement(this.dog);
		this.walk.addComplement(this.inTheRoom);
		complex = phraseFactory.createCoordinatedPhrase(this.kiss, this.walk);
		this.s4 = this.phraseFactory.createClause(subjects, complex);
		this.s4.setFeature(Feature.CUE_PHRASE, "however"); //$NON-NLS-1$
		this.s4.addFrontModifier("tomorrow"); //$NON-NLS-1$
		this.s4.setFeature(Feature.TENSE,Tense.FUTURE);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);

		Assert
				.assertEquals(
						"however will Jane and Andrew kiss the dog and walk in the room tomorrow", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());

		// slightly more complex -- perfective
		setUp();
//		this.realiser.setLexicon(this.lexicon);
		subjects = phraseFactory.createCoordinatedPhrase(this.phraseFactory
				.createNounPhrase("Jane"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Andrew")); //$NON-NLS-1$
		complex = phraseFactory.createCoordinatedPhrase(this.kiss, this.walk);
		this.kiss.addComplement(this.dog);
		this.walk.addComplement(this.inTheRoom);
		this.s4 = this.phraseFactory.createClause(subjects, complex);
		this.s4.setFeature(Feature.CUE_PHRASE, "however"); //$NON-NLS-1$
		this.s4.addFrontModifier("tomorrow"); //$NON-NLS-1$
		this.s4.setFeature(Feature.TENSE,Tense.FUTURE);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		this.s4.setFeature(Feature.PERFECT, true);

		Assert
				.assertEquals(
						"however will Jane and Andrew have kissed the dog and walked in the room tomorrow", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());
	}

	/**
	 * Test for simple WH questions in present tense.
	 */
	@Test
	public void testSimpleQuestions2() {
		setUp();
//		this.realiser.setLexicon(this.lexicon);
		PhraseElement s = this.phraseFactory.createClause("the woman", "kiss", //$NON-NLS-1$ //$NON-NLS-2$
				"the man"); //$NON-NLS-1$

		// try with the simple yes/no type first
		s.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals("does the woman kiss the man", this.realiser //$NON-NLS-1$
				.realise(s).getRealisation());

		// now in the passive
		s = this.phraseFactory.createClause("the woman", "kiss", //$NON-NLS-1$ //$NON-NLS-2$
				"the man"); //$NON-NLS-1$
		s.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		s.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("is the man kissed by the woman", this.realiser //$NON-NLS-1$
				.realise(s).getRealisation());

		// // subject interrogative with simple present
		// // sentence: "the woman kisses the man"
		s = this.phraseFactory.createClause("the woman", "kiss", //$NON-NLS-1$ //$NON-NLS-2$
				"the man"); //$NON-NLS-1$
		s.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_SUBJECT);

		Assert.assertEquals("who kisses the man", this.realiser.realise(s) //$NON-NLS-1$
				.getRealisation());

		// object interrogative with simple present
		s = this.phraseFactory.createClause("the woman", "kiss", //$NON-NLS-1$ //$NON-NLS-2$
				"the man"); //$NON-NLS-1$
		s.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_OBJECT);
		Assert.assertEquals("who does the woman kiss", this.realiser //$NON-NLS-1$
				.realise(s).getRealisation());

		// subject interrogative with passive
		s = this.phraseFactory.createClause("the woman", "kiss", //$NON-NLS-1$ //$NON-NLS-2$
				"the man"); //$NON-NLS-1$
		s.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_SUBJECT);
		s.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("who is the man kissed by", this.realiser //$NON-NLS-1$
				.realise(s).getRealisation());
	}

	/**
	 * Test for wh questions.
	 */
	@Test
	public void testWHQuestions() {

		// subject interrogative
		setUp();
//		this.realiser.setLexicon(this.lexicon);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals(
				"however who will pick up the balls in the shop tomorrow", //$NON-NLS-1$
				this.realiser.realise(this.s4).getRealisation());

		// subject interrogative in passive
		setUp();
		this.s4.setFeature(Feature.PASSIVE, true);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_SUBJECT);

		Assert
				.assertEquals(
						"however who will the balls be picked up in the shop by tomorrow", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());

		// object interrogative
		setUp();
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHAT_OBJECT);
		Assert
				.assertEquals(
						"however what will Jane and Andrew pick up in the shop tomorrow", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());

		// object interrogative with passive
		setUp();
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHAT_OBJECT);
		this.s4.setFeature(Feature.PASSIVE, true);

		Assert
				.assertEquals(
						"however what will be picked up in the shop by Jane and Andrew tomorrow", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());

		// how-question + passive
		setUp();
		this.s4.setFeature(Feature.PASSIVE, true);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.HOW);
		Assert
				.assertEquals(
						"however how will the balls be picked up in the shop by Jane and Andrew tomorrow", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());

		// // why-question + passive
		setUp();
		this.s4.setFeature(Feature.PASSIVE, true);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHY);
		Assert
				.assertEquals(
						"however why will the balls be picked up in the shop by Jane and Andrew tomorrow", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());

		// how question with modal
		setUp();
		this.s4.setFeature(Feature.PASSIVE, true);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.HOW);
		this.s4.setFeature(Feature.MODAL, "should"); //$NON-NLS-1$
		Assert
				.assertEquals(
						"however how should the balls be picked up in the shop by Jane and Andrew tomorrow", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());

		// indirect object
		setUp();
//		this.realiser.setLexicon(this.lexicon);
		this.s3.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_INDIRECT_OBJECT);
		Assert.assertEquals("who does the man give John's flower to", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());
	}

	/**
	 * Test questyions in the tutorial.
	 */
	@Test
	public void testTutorialQuestions() {
		setUp();
//		this.realiser.setLexicon(this.lexicon);

		PhraseElement p = this.phraseFactory.createClause("Mary", "chase", //$NON-NLS-1$ //$NON-NLS-2$
				"George"); //$NON-NLS-1$
		p.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals("does Mary chase George", this.realiser.realise(p) //$NON-NLS-1$
				.getRealisation());

		p = this.phraseFactory.createClause("Mary", "chase", //$NON-NLS-1$ //$NON-NLS-2$
				"George"); //$NON-NLS-1$
		p.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_OBJECT);
		Assert.assertEquals("who does Mary chase", this.realiser.realise(p) //$NON-NLS-1$
				.getRealisation());

	}
}
