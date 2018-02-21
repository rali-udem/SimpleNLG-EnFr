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
import simplenlg.features.Form;
import simplenlg.features.InterrogativeType;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.phrasespec.NPPhraseSpec;
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

		// "l'homme donne à la femme la fleur de Jean"
		PhraseElement jean = factory.createNounPhrase("Jean"); //$NON-NLS-1$
		PhraseElement fleur = factory.createNounPhrase("le", "fleur");
		fleur.addComplement(factory.createPrepositionPhrase("de", jean));
		PhraseElement _femme = factory.createNounPhrase(
				"le", "femme"); //$NON-NLS-1$ //$NON-NLS-2$
		s3 = factory.createClause(homme, donner, fleur);
		s3.setIndirectObject(_femme);

		// "cependant, hier, Jane et André ont ramassé les balles dans le magasin"
		CoordinatedPhraseElement subjects = factory.createCoordinatedPhrase(
				factory.createNounPhrase("Jane"), //$NON-NLS-1$
				factory.createNounPhrase("André")); //$NON-NLS-1$
		NPPhraseSpec balles = factory.createNounPhrase("le", "balle");
		balles.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		s4 = factory.createClause(subjects, "ramasser", balles); //$NON-NLS-1$
		s4.addPostModifier("dans le magasin"); //$NON-NLS-1$
		s4.setFeature(Feature.CUE_PHRASE, "cependant"); //$NON-NLS-1$
		s4.addFrontModifier("hier"); //$NON-NLS-1$
		s4.setFeature(Feature.TENSE,Tense.PAST);

	}

	/**
	 * Tests a couple of fairly simple questions.
	 */
	@Test
	public void testSimpleQuestions() {
		setUp();
		factory.setLexicon(lexicon);
//		realiser.setLexicon(lexicon);

		// simple present
		s1 = factory.createClause(femme, embrasser,
				homme);
		s1.setFeature(Feature.TENSE,Tense.PRESENT);
		s1.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);

		Assert.assertEquals("Est-ce que la femme embrasse l'homme?",
				realiser.realiseSentence(s1));

		// simple past
		// sentence: "le femme a embrassé l'homme"
		s1 = factory.createClause(femme, embrasser,
				homme);
		s1.setFeature(Feature.TENSE,Tense.PAST);
		s1.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals("Est-ce que la femme a embrassé l'homme?",
				realiser.realiseSentence(s1));

		// sentence = "il y a un chien sur le rocher"
		s2 = factory.createClause("il", "avoir", 
				factory.createNounPhrase("un", "chien"));
		s2.addComplement(factory.createNounPhrase("y"));
		s2.addPostModifier(surLeRocher);
		s2.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals("Est-ce qu'il y a un chien sur le rocher?",
				realiser.realiseSentence(s2));

		// perfective
		// sentence -- "il y a eu un chien sur le rocher"
		s2 = factory.createClause("il", "avoir", 
				factory.createNounPhrase("un", "chien"));
		s2.addComplement(factory.createNounPhrase("y"));
		s2.addPostModifier(surLeRocher);
		s2.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		s2.setFeature(Feature.PERFECT, true);
		Assert.assertEquals("Est-ce qu'il y a eu un chien sur le rocher?",
				realiser.realiseSentence(s2));

		// progressive
		// sentence: "l'homme est en train de donner à la femme la fleur de Jean"
		PhraseElement jean = factory.createNounPhrase("Jean"); //$NON-NLS-1$
		NPPhraseSpec fleur = factory.createNounPhrase("le", "fleur");
		fleur.addComplement(factory.createPrepositionPhrase("de", jean));
		PhraseElement _woman = factory.createNounPhrase(
				"le", "femme"); //$NON-NLS-1$ //$NON-NLS-2$
		s3 = factory.createClause(homme, donner, fleur);
		s3.setIndirectObject(_woman);
		s3.setFeature(Feature.PROGRESSIVE, true);
		s3.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals("Est-ce que l'homme est en train de donner à la femme la fleur de Jean?",
				realiser.realiseSentence(s3));

		// modal
		// sentence: "le homme doit avoir donné à la femme la fleur de Jean"
		setUp();
		jean = factory.createNounPhrase("Jean"); //$NON-NLS-1$
		fleur = factory.createNounPhrase("le", "fleur");
		fleur.addComplement(factory.createPrepositionPhrase("de", jean));
		_woman = factory.createNounPhrase("le", "femme"); //$NON-NLS-1$ //$NON-NLS-2$
		s3 = factory.createClause(homme, donner, fleur);
		s3.setIndirectObject(_woman);
		s3.setFeature(Feature.TENSE,Tense.PAST);
		s3.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		s3.setFeature(Feature.MODAL, "devoir"); //$NON-NLS-1$
		Assert.assertEquals("Est-ce que l'homme doit avoir donné à la femme la fleur de Jean?",
				realiser.realiseSentence(s3));

		// complex case with cue phrases
		// sentence: "cependant demain, Jane et André ramasseront les balles
		// dans le magasin"
		// this gets the front modifier "demain" shifted at the end
		setUp();
		CoordinatedPhraseElement subjects = factory.createCoordinatedPhrase(
				factory.createNounPhrase("Jane"), //$NON-NLS-1$
				factory.createNounPhrase("André")); //$NON-NLS-1$
		s4 = factory.createClause(subjects, "ramasser", //$NON-NLS-1$
				"les balles"); //$NON-NLS-1$
		s4.addPostModifier("dans le magasin"); //$NON-NLS-1$
		s4.setFeature(Feature.CUE_PHRASE, "cependant"); //$NON-NLS-1$
		s4.addFrontModifier("demain"); //$NON-NLS-1$
		s4.setFeature(Feature.TENSE,Tense.FUTURE);
		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals(
			"Cependant, est-ce que Jane et André ramasseront les balles dans le magasin demain?", //$NON-NLS-1$
			realiser.realiseSentence(s4));
	}

	/**
	 * Test for sentences with negation.
	 */
	@Test
	public void testNegatedQuestions() {
		setUp();
		factory.setLexicon(lexicon);
//		realiser.setLexicon(lexicon);

		// sentence: "le femme n'embrasse pas l'homme"
		s1 = factory.createClause(femme, "embrasser",
				homme);
		s1.setFeature(Feature.TENSE,Tense.PAST);
		s1.setNegated(true);
		s1.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals("Est-ce que la femme n'a pas embrassé l'homme?",
				realiser.realiseSentence(s1));

		// sentence: cependant hier, Jane et André n'ont pas ramassé les
		// balles dans le magasin
		CoordinatedPhraseElement subjects = factory.createCoordinatedPhrase(
				factory.createNounPhrase("Jane"), //$NON-NLS-1$
				factory.createNounPhrase("André")); //$NON-NLS-1$
		s4 = factory.createClause(subjects, "ramasser", //$NON-NLS-1$
				"les balles"); //$NON-NLS-1$
		s4.addPostModifier("dans le magasin"); //$NON-NLS-1$
		s4.setFeature(Feature.CUE_PHRASE, "cependant"); //$NON-NLS-1$
		s4.addFrontModifier("hier"); //$NON-NLS-1$
		s4.setFeature(Feature.NEGATED,true);
		s4.setFeature(Feature.TENSE,Tense.PAST);
		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals(
						"Cependant, est-ce que Jane et André n'ont pas ramassé les balles dans le magasin hier?", //$NON-NLS-1$
						realiser.realiseSentence(s4));
	}

	/**
	 * Tests for coordinate VPs dans question form.
	 */
	@Test
	public void testCoordinateVPQuestions() {

		// create a complex vp: "embrasser le garçon et marcher dans la pièce"
		setUp();
		CoordinatedPhraseElement complex = factory.createCoordinatedPhrase(
				embrasser, marcher);
		embrasser.addComplement(garcon);
		marcher.addComplement(dansLaPiece);

		// sentence: "cependant hier, Jane et André ont embrassé le chien et
		// ont marché dans la pièce"
		CoordinatedPhraseElement subjects = factory.createCoordinatedPhrase(
				factory.createNounPhrase("Jane"), //$NON-NLS-1$
				factory.createNounPhrase("André")); //$NON-NLS-1$
		s4 = factory.createClause(/*subjects, complex*/);
		s4.setSubject(subjects);
		s4.setVerbPhrase(complex);
		s4.setFeature(Feature.CUE_PHRASE, "cependant"); //$NON-NLS-1$
		s4.addFrontModifier("hier"); //$NON-NLS-1$
		s4.setFeature(Feature.TENSE,Tense.PAST);

		Assert.assertEquals(
						"Cependant, hier, Jane et André ont embrassé le garçon et ont marché dans la pièce.",
						realiser.realiseSentence(s4));

		setUp();
		subjects = factory.createCoordinatedPhrase(factory
				.createNounPhrase("Jane"), //$NON-NLS-1$
				factory.createNounPhrase("André")); //$NON-NLS-1$
		embrasser.addComplement(garcon);
		marcher.addComplement(dansLaPiece);
		complex = factory.createCoordinatedPhrase(embrasser, marcher);
		s4 = factory.createClause(/*subjects, complex*/);
		s4.setSubject(subjects);
		s4.setVerbPhrase(complex);
		s4.setFeature(Feature.CUE_PHRASE, "cependant"); //$NON-NLS-1$
		s4.addFrontModifier("hier"); //$NON-NLS-1$
		s4.setFeature(Feature.TENSE,Tense.PAST);
		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);

		Assert.assertEquals(
						"Cependant, est-ce que Jane et André ont embrassé le garçon et ont marché dans la pièce hier?", //$NON-NLS-1$
						realiser.realiseSentence(s4));

		// slightly more complex -- progressive
		setUp();
//		realiser.setLexicon(lexicon);
		subjects = factory.createCoordinatedPhrase(factory
				.createNounPhrase("Jane"), //$NON-NLS-1$
				factory.createNounPhrase("André")); //$NON-NLS-1$
		complex = factory.createCoordinatedPhrase(embrasser, marcher);
		embrasser.addComplement(garcon);
		marcher.addComplement(dansLaPiece);
		s4 = factory.createClause(/*subjects, complex*/);
		s4.setSubject(subjects);
		s4.setVerbPhrase(complex);
		s4.setFeature(Feature.CUE_PHRASE, "cependant"); //$NON-NLS-1$
		s4.addFrontModifier("hier"); //$NON-NLS-1$
		s4.setFeature(Feature.TENSE,Tense.PAST);
		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		s4.setFeature(Feature.PROGRESSIVE, true);
		s4.setFeature(Feature.PERFECT, true);

		Assert.assertEquals(
				"Cependant, est-ce que Jane et André avaient été en train d'embrasser le garçon et avaient été en train de marcher dans la pièce hier?", //$NON-NLS-1$
						realiser.realiseSentence(s4));
	}

	/**
	 * Test for simple WH questions in present tense.
	 */
	@Test
	public void testSimpleQuestions2() {
		setUp();
//		realiser.setLexicon(lexicon);
		PhraseElement s = factory.createClause(
				factory.createNounPhrase("le", "femme"),
				"embrasser", //$NON-NLS-1$ //$NON-NLS-2$
				factory.createNounPhrase("le", "homme")); //$NON-NLS-1$

		// try with the simple yes/no type first
		s.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		Assert.assertEquals("Est-ce que la femme embrasse l'homme?",
				realiser.realiseSentence(s));

		// now in the passive
		s.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("Est-ce que l'homme est embrassé par la femme?",
				realiser.realiseSentence(s));

		// // subject interrogative with simple present
		s.setFeature(Feature.PASSIVE, false);
		s.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals("Qui est-ce qui embrasse l'homme?",
				realiser.realiseSentence(s));

		// object interrogative with simple present
		s.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_OBJECT);
		Assert.assertEquals("Qui est-ce que la femme embrasse?",
				realiser.realiseSentence(s));

		// subject interrogative with passive
		s.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_SUBJECT);
		s.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("Par qui est-ce que l'homme est embrassé?",
				realiser.realiseSentence(s));

		// object interrogative with passive
		s.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_OBJECT);
		Assert.assertEquals("Qui est-ce qui est embrassé par la femme?",
				realiser.realiseSentence(s));
	}

	/**
	 * Test for wh questions.
	 */
	@Test
	public void testWHQuestions() {

		// subject interrogative
		setUp();
//		realiser.setLexicon(lexicon);
		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals(
				"Cependant, qui est-ce qui a ramassé les balles dans le magasin hier?",
				realiser.realiseSentence(s4));

		// subject interrogative in passive
		setUp();
		s4.setFeature(Feature.PASSIVE, true);
		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals(
				"Cependant, par qui est-ce que les balles ont été ramassées dans le magasin hier?",
				realiser.realiseSentence(s4));

		// object interrogative
		setUp();
		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals(
				"Cependant, qu'est-ce que Jane et André ont ramassé dans le magasin hier?",
				realiser.realiseSentence(s4));

		// object interrogative with passive
		setUp();
		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHAT_OBJECT);
		s4.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals(
				"Cependant, qu'est-ce qui a été ramassé dans le magasin par Jane et André hier?",
				realiser.realiseSentence(s4));

		// how-question + passive
		setUp();
		s4.setFeature(Feature.PASSIVE, true);
		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.HOW);
		Assert.assertEquals(
				"Cependant, comment est-ce que les balles ont été ramassées dans le magasin par Jane et André hier?",
				realiser.realiseSentence(s4));

		// // how-question + passive
		setUp();
		s4.setFeature(Feature.PASSIVE, true);
		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHY);
		Assert.assertEquals(
				"Cependant, pourquoi est-ce que les balles ont été ramassées dans le magasin par Jane et André hier?",
				realiser.realiseSentence(s4));

		// how question with modal
		setUp();
		s4.setFeature(Feature.PASSIVE, true);
		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.HOW);
		s4.setFeature(Feature.MODAL, "devoir"); //$NON-NLS-1$
		Assert.assertEquals(
				"Cependant, comment est-ce que les balles doivent avoir été ramassées dans le magasin par Jane et André hier?",
				realiser.realiseSentence(s4));

		s4.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.HOW_MANY);
		Assert.assertEquals(
				"Cependant, combien de balles doivent avoir été ramassées dans le magasin par Jane et André hier?",
				realiser.realiseSentence(s4));
		
		// indirect object
		setUp();
//		realiser.setLexicon(lexicon);
		s3.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHO_INDIRECT_OBJECT);
		Assert.assertEquals("À qui est-ce que l'homme donne la fleur de Jean?", //$NON-NLS-1$
				realiser.realiseSentence(s3));
	}

	/*
	 * Test indirect interrogative sentences.
	 */
	@Test
	public void testIndirectInterrogative() {
		SPhraseSpec subordinate = factory.createClause(
				factory.createNounPhrase("tout le","monde"),
				"comprendre");	
		SPhraseSpec mainClause = factory.createClause(
				factory.createNounPhrase("le", "professeur"),
				"demander",
				subordinate);
		
		subordinate.setFeature(Feature.FORM, Form.SUBJUNCTIVE);
		Assert.assertEquals("Le professeur demande que tout le monde comprenne.",
				realiser.realiseSentence(mainClause));
		
		subordinate.setFeature(Feature.FORM, Form.NORMAL);
		subordinate.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		Assert.assertEquals("Le professeur demande si tout le monde comprend.",
				realiser.realiseSentence(mainClause));

		subordinate.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals("Le professeur demande qui est-ce qui comprend.",
				realiser.realiseSentence(mainClause));

		subordinate.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("Le professeur demande qu'est-ce que tout le monde comprend.",
				realiser.realiseSentence(mainClause));
		
		// "si" added by the user transforms conditional to past,
		// but not "si" added for the purpose of indirect interrogative.
		subordinate.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		subordinate.setSubject("ils");
		subordinate.setFeature(Feature.TENSE, Tense.CONDITIONAL);
		SPhraseSpec conditionalClause = factory.createClause("il","expliquer");
		conditionalClause.addModifier("mieux");
		conditionalClause.setFeature(Feature.TENSE, Tense.CONDITIONAL);
		conditionalClause.setFeature(Feature.COMPLEMENTISER, "si");
		subordinate.setComplement(conditionalClause);
		Assert.assertEquals(
				"Le professeur demande s'ils comprendraient s'il expliquait mieux.",
				realiser.realiseSentence(mainClause));
	}
}
