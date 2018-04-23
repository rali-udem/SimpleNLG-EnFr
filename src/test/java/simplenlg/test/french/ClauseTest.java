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

import simplenlg.features.ClauseStatus;
import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Tense;
import simplenlg.features.french.FrenchFeature;
import simplenlg.features.french.FrenchLexicalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.AdvPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;

/**
 * The Class STest. translated and adapted by vaudrypl
 */
public class ClauseTest extends SimpleNLG4TestBase {

	// set up a few more fixtures
	/** The s4. */
	SPhraseSpec s1, s2, s3, s4;

	/**
	 * Instantiates a new s test.
	 * 
	 * @param name
	 *            the name
	 */
	public ClauseTest(String name) {
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
		// la femme a embrassé l'homme derrière le rideau
		this.s1 = this.factory.createClause();
		this.s1.setSubject(this.femme);
		this.s1.setVerbPhrase(this.embrasser);
		this.s1.setObject(this.homme);

		// il y a le chien sur le rocher
		this.s2 = this.factory.createClause();
		this.s2.setSubject("il"); //$NON-NLS-1$
		s2.getSubject().setFeature(LexicalFeature.EXPLETIVE_SUBJECT, true);
		this.s2.addComplement("y");
		this.s2.setVerb("avoir"); //$NON-NLS-1$
		this.s2.setObject(this.chien);
		this.s2.addPostModifier(this.surLeRocher);

		// the man gives the woman John's flower
		// l'homme donne à la femme la fleur de Jean
		this.s3 = this.factory.createClause();
		this.s3.setSubject(new NPPhraseSpec(homme));
		this.s3.setVerbPhrase(this.donner);

		NPPhraseSpec fleur = this.factory.createNounPhrase("un", "fleur"); //$NON-NLS-1$
//		NPPhraseSpec jean = this.phraseFactory.createNounPhrase("Jean"); //$NON-NLS-1$
//		jean.setFeature(Feature.POSSESSIVE, true);
//		fleur.setFeature(InternalFeature.SPECIFIER, jean);
		this.s3.setObject(fleur);
		this.s3.setIndirectObject(new NPPhraseSpec(femme));

		this.s4 = this.factory.createClause();
		WordElement cependant = this.lexicon.lookupWord("cependant");
		this.s4.setFeature(Feature.CUE_PHRASE, cependant); //$NON-NLS-1$
		this.s4.addFrontModifier("demain"); //$NON-NLS-1$

		CoordinatedPhraseElement subject = this.factory
				.createCoordinatedPhrase(this.factory
						.createNounPhrase("Jane"), this.factory //$NON-NLS-1$
						.createNounPhrase("André")); //$NON-NLS-1$

		this.s4.setSubject(subject);

		PhraseElement pick = this.factory.createVerbPhrase("ramasser"); //$NON-NLS-1$
		this.s4.setVerbPhrase(pick);
		this.s4.setObject("les balles"); //$NON-NLS-1$
		this.s4.addPostModifier("dans le magasin"); //$NON-NLS-1$
		this.s4.setFeature(Feature.TENSE, Tense.FUTURE);
	}

	/**
	 * Initial test for basic sentences.
	 */
	@Test
	public void testBasic() {
		Assert.assertEquals("la femme embrasse l'homme", this.realiser //$NON-NLS-1$
				.realise(this.s1).getRealisation());
		Assert.assertEquals("il y a le chien sur le rocher", this.realiser //$NON-NLS-1$
				.realise(this.s2).getRealisation());

		setUp();
		Assert.assertEquals("l'homme donne une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());
		Assert
				.assertEquals(
						"cependant, demain, Jane et André ramasseront les balles dans le magasin", //$NON-NLS-1$
						this.realiser.realise(this.s4).getRealisation());
		
		// without comma
		this.s4.getFeatureAsElement(Feature.CUE_PHRASE).setFeature(LexicalFeature.NO_COMMA, true);
		this.s4.getFrontModifiers().get(0).setFeature(LexicalFeature.NO_COMMA, true);
		Assert
		.assertEquals(
				"cependant demain Jane et André ramasseront les balles dans le magasin", //$NON-NLS-1$
				this.realiser.realise(this.s4).getRealisation());
		this.s4.getFeatureAsElement(Feature.CUE_PHRASE).setFeature(LexicalFeature.NO_COMMA, false);
		this.s4.getFrontModifiers().get(0).setFeature(LexicalFeature.NO_COMMA, false);
	}

	/**
	 * Test did not
	 */
	@Test
	public void testNegation() {
		NPPhraseSpec pomme = this.factory.createNounPhrase("un", "pomme");
		SPhraseSpec s = factory.createClause("Jean", "manger", pomme);
		s.setTense(Tense.PAST);
		s.setNegated(true);

		Assert.assertEquals("Jean n'a pas mangé de pomme", //$NON-NLS-1$
				this.realiser.realise(s).getRealisation());

		s.setFeature(FrenchFeature.NEGATION_AUXILIARY, "plus");
		Assert.assertEquals("Jean n'a plus mangé de pomme", //$NON-NLS-1$
				this.realiser.realise(s).getRealisation());
		
		s.setFeature(FrenchFeature.NEGATION_AUXILIARY, null);
		Assert.assertEquals("Jean n'a pas mangé de pomme", //$NON-NLS-1$
				this.realiser.realise(s).getRealisation());
		
		WordElement point = this.lexicon.getWord("point");
		s.setFeature(FrenchFeature.NEGATION_AUXILIARY, point);
		Assert.assertEquals("Jean n'a point mangé de pomme", //$NON-NLS-1$
				this.realiser.realise(s).getRealisation());
		
		s.setSubject("personne");
		Assert.assertEquals("personne n'a mangé de pomme", //$NON-NLS-1$
				this.realiser.realise(s).getRealisation());

		s.setFeature(FrenchFeature.NEGATION_AUXILIARY, "plus");
		Assert.assertEquals("personne n'a plus mangé de pomme", //$NON-NLS-1$
				this.realiser.realise(s).getRealisation());
		s.setFeature(Feature.NEGATED, false);
		Assert.assertEquals("personne n'a mangé de pomme", //$NON-NLS-1$
				this.realiser.realise(s).getRealisation());
}

	/**
	 * Test that pronominal args are being correctly cast as NPs.
	 */
	@Test
	public void testPronounArguments() {
		// the subject of s2 should have been cast into a pronominal NP
		NLGElement subj = this.s2.getFeatureAsElementList(
				InternalFeature.SUBJECTS).get(0);
		Assert.assertTrue(subj.isA(PhraseCategory.NOUN_PHRASE));
		// Assert.assertTrue(LexicalCategory.PRONOUN.equals(((PhraseElement)
		// subj)
		// .getCategory()));
	}
	
	/**
	 * Test demonstratives.
	 */
	@Test
	public void testDemonstratives() {
		NPPhraseSpec individu = this.factory.createNounPhrase("ce", "individu");
		SPhraseSpec sentence = this.factory.createClause("ce", "être", individu);
		Assert.assertEquals("c'est cet individu", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		sentence = this.factory.createClause("celles-ci", "être", "armé");
		Assert.assertEquals("celles-ci sont armées", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
	}
	
	/**
	 * Test reflexive object with auxiliary.
	 */
	@Test
	public void testReflexiveObjectAuxiliary() {
		// not reflexive, with auxiliary avoir
		SPhraseSpec sentence1 = this.factory.createClause("je", "frappe", "elles");
		sentence1.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals("je les ai frappées", //$NON-NLS-1$
				this.realiser.realise(sentence1).getRealisation());
		SPhraseSpec sentence2 = this.factory.createClause("tu", "donner", "un cadeau");
		sentence2.setIndirectObject("moi");
		sentence2.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals("tu m'as donné un cadeau", //$NON-NLS-1$
				this.realiser.realise(sentence2).getRealisation());
		
		sentence1.setObject("se");
		Assert.assertEquals("je me suis frappé", //$NON-NLS-1$
				this.realiser.realise(sentence1).getRealisation());
		sentence1.setSubject("nous");
//		sentence1.setObject("se");
		Assert.assertEquals("nous nous sommes frappés", //$NON-NLS-1$
				this.realiser.realise(sentence1).getRealisation());
		
		sentence2.setSubject("moi");
		Assert.assertEquals("je me suis donné un cadeau", //$NON-NLS-1$
				this.realiser.realise(sentence2).getRealisation());
		sentence2.setSubject("elles");
		sentence2.setIndirectObject("elles");
		Assert.assertEquals("elles leur ont donné un cadeau", //$NON-NLS-1$
				this.realiser.realise(sentence2).getRealisation());
		sentence2.setIndirectObject("se");
		Assert.assertEquals("elles se sont données un cadeau", //$NON-NLS-1$
				this.realiser.realise(sentence2).getRealisation());
	}
	
	/**
	 * Test that negative indefinite pronouns as subjects or verb complements
	 * provoke "ne" only negation.
	 */
	@Test
	public void testNegativeIndefinitePronouns() {
		SPhraseSpec sentence = this.factory.createClause("personne", "voir", "moi");
		Assert.assertEquals("personne ne me voit", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		sentence.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("personne ne me voit", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		sentence.setFeature(Feature.NEGATED, false);
		
		sentence.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("je ne suis vu par personne", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());		
		sentence.setSubject("toi");
		Assert.assertEquals("je suis vu par toi", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		
		sentence = this.factory.createClause("moi", "voir", "rien");
		Assert.assertEquals("je ne vois rien", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		sentence.setObject("toi");
		Assert.assertEquals("je te vois", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());

		sentence = this.factory.createClause("personne", "parle");
		sentence.setIndirectObject("moi");
		Assert.assertEquals("personne ne me parle", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		sentence.setSubject("toi");
		Assert.assertEquals("tu me parles", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		NPPhraseSpec snAucunDet = this.factory.createNounPhrase("aucun", "femme");
		sentence.setSubject(snAucunDet);
		Assert.assertEquals("aucune femme ne me parle", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		NPPhraseSpec snAucunDet2 = this.factory.createNounPhrase("aucun", "personne");
		sentence.setSubject(snAucunDet2);
		Assert.assertEquals("aucune personne ne me parle", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		snAucunDet2.setSpecifier("un");
		Assert.assertEquals("une personne me parle", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		
		sentence = this.factory.createClause("moi", "parle");
		sentence.setIndirectObject("personne");
		Assert.assertEquals("je ne parle à personne", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		sentence.setIndirectObject("toi");
		Assert.assertEquals("je te parle", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		
		PPPhraseSpec indirectObject =
			this.factory.createPrepositionPhrase("à", "personne");
		sentence.setIndirectObject(indirectObject);
		Assert.assertEquals("je ne parle à personne", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
		sentence.setIndirectObject("toi");
		Assert.assertEquals("je te parle", //$NON-NLS-1$
				this.realiser.realise(sentence).getRealisation());
	}

	/**
	 * Tests for setting tense, aspect and passive from the sentence interface.
	 */
	@Test
	public void testTenses() {
		// simple past
		this.s3.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals("l'homme a donné une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());

		// perfect
		this.s3.setFeature(Feature.PERFECT, true);
		Assert.assertEquals("l'homme avait donné une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());

		// negation
		this.s3.setFeature(Feature.NEGATED, true);
		Assert.assertEquals(
				"l'homme n'avait pas donné de fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());

		this.s3.setFeature(Feature.PROGRESSIVE, true);
		Assert
				.assertEquals(
						"l'homme n'avait pas été en train de donner de fleur à la femme", //$NON-NLS-1$
						this.realiser.realise(this.s3).getRealisation());

		// passivisation with direct and indirect object
		this.s3.setFeature(Feature.PASSIVE, true);
		// Assert.assertEquals(
		// "John's flower had not been being given the woman by the man",
		// //$NON-NLS-1$
		// this.realiser.realise(this.s3).getRealisation());
	}

	/**
	 * Test what happens when a sentence is subordinated as complement of a
	 * verb.
	 */
	@Test
	public void testSubordination() {

		// subordinate sentence by setting it as complement of a verb
		this.dire.addComplement(this.s3);

		// check the getter
		Assert.assertEquals(ClauseStatus.SUBORDINATE, this.s3
				.getFeature(InternalFeature.CLAUSE_STATUS));

		// check realisation
		Assert.assertEquals(
				"dit que l'homme donne une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());
		
		this.s3.setFeature(Feature.COMPLEMENTISER, "alors que");
		this.s3.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.COMPLEMENT);
		this.dire.setObject("cela");
		Assert.assertEquals(
				"dit cela alors que l'homme donne une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());
		this.s3.getSubject().setFeature(Feature.PRONOMINAL, true);
		Assert.assertEquals(
				"dit cela alors qu'il donne une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());
		this.s3.setFeature(Feature.COMPLEMENTISER, "lorsque");
		Assert.assertEquals(
				"dit cela lorsqu'il donne une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());
	}

	/**
	 * Test "si" as complementiser. Elision ("s'") only when followed by "il".
	 */
	@Test
	public void testSi() {

		// subordinate sentence by setting it as complement of a verb
		this.dire.addComplement(this.s3);

		// check the getter
		Assert.assertEquals(ClauseStatus.SUBORDINATE, this.s3
				.getFeature(InternalFeature.CLAUSE_STATUS));
		
		this.s3.setFeature(Feature.COMPLEMENTISER, "si");
		
		// check realisation
		Assert.assertEquals(
				"dit si l'homme donne une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());
		
		this.s3.getSubject().setFeature(Feature.PRONOMINAL, true);
		// check realisation
		Assert.assertEquals(
				"dit s'il donne une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());

		this.s3.setFeature(Feature.COMPLEMENTISER, "même si");
		this.s3.getSubject().setFeature(Feature.PRONOMINAL, false);
		
		// check realisation
		Assert.assertEquals(
				"dit même si l'homme donne une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());
		
		this.s3.getSubject().setFeature(Feature.PRONOMINAL, true);
		// check realisation
		Assert.assertEquals(
				"dit même s'il donne une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());
		
		this.s3.getSubject().setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		// check realisation
		Assert.assertEquals(
				"dit même s'ils donnent une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());
		
		this.s3.setSubject("elle");
		Assert.assertEquals(
				"dit même si elle donne une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());
	}

	/**
	 * Test tenses with "si" as complementiser.
	 * Future becomes present and conditional becomes past.
	 */
	@Test
	public void testSiTenses() {
		// Simple future.	
		SPhraseSpec partir = factory.createClause("tu", "partir");
		partir.setFeature(Feature.TENSE, Tense.FUTURE);
		// With another complementiser.
		partir.setFeature(Feature.COMPLEMENTISER, "quand");
		NPPhraseSpec temps = factory.createNounPhrase("du", "temps");
		temps.addModifier("beau");
		SPhraseSpec avoir = factory.createClause("tu", "avoir", temps);
		avoir.setFeature(Feature.TENSE, Tense.FUTURE);
		avoir.addFrontModifier(partir);
		partir.setFeature(LexicalFeature.NO_COMMA, true);
		AdvPhraseSpec temporalAdverb = factory.createAdverbPhrase("demain");
		avoir.addFrontModifier(temporalAdverb);
		Assert.assertEquals( "quand tu partiras demain, tu auras du beau temps",
				realise(avoir) );		
		// With complementiser "si".
		partir.setFeature(Feature.COMPLEMENTISER, "si");
		Assert.assertEquals( "si tu pars demain, tu auras du beau temps",
				realise(avoir) );

		// Future anterior.		
		partir.setFeature(Feature.PERFECT, true);
		// With another complementiser.
		partir.setFeature(Feature.COMPLEMENTISER, "quand");
		Assert.assertEquals( "quand tu seras parti demain, tu auras du beau temps",
				realise(avoir) );	
		// With complementiser "si".
		partir.setFeature(Feature.COMPLEMENTISER, "si");
		Assert.assertEquals( "si tu es parti demain, tu auras du beau temps",
				realise(avoir) );

		// Conditional present.		
		partir.setFeature(Feature.TENSE, Tense.CONDITIONAL);
		partir.setFeature(Feature.PERFECT, false);
		avoir.setFeature(Feature.TENSE, Tense.CONDITIONAL);
		// With another complementiser.
		partir.setFeature(Feature.COMPLEMENTISER, "dans le cas où");
		Assert.assertEquals( "dans le cas où tu partirais demain, tu aurais du beau temps",
				realise(avoir) );	
		// With complementiser "si".
		partir.setFeature(Feature.COMPLEMENTISER, "si");
		Assert.assertEquals( "si tu partais demain, tu aurais du beau temps",
				realise(avoir) );

		// Conditional past.		
		partir.setFeature(Feature.PERFECT, true);
		partir.setSubject("elle");
		avoir.setSubject("elle");
		temporalAdverb.setAdverb("hier");
		avoir.addModifier("maintenant");
		// With another complementiser.
		partir.setFeature(Feature.COMPLEMENTISER, "dans le cas où");
		Assert.assertEquals(
				"dans le cas où elle serait partie hier, elle aurait du beau temps maintenant",
				realise(avoir) );	
		// With complementiser "si".
		partir.setFeature(Feature.COMPLEMENTISER, "si");
		Assert.assertEquals( "si elle était partie hier, elle aurait du beau temps maintenant",
				realise(avoir) );
	}
	
	/**
	 * Test the various forms of a sentence, including subordinates.
	 */
	@Test
	public void testForm() {

		// check the getter method
		Assert.assertEquals(Form.NORMAL, this.s1.getFeatureAsElement(
				InternalFeature.VERB_PHRASE).getFeature(Feature.FORM));

		// infinitive
		this.s1.setFeature(Feature.FORM, Form.INFINITIVE);

		Assert
				.assertEquals(
						"embrasser l'homme", this.realiser.realise(this.s1).getRealisation()); //$NON-NLS-1$

//		// gerund with "there"
//		this.s2.setFeature(Feature.FORM, Form.GERUND);
//		// Assert.assertEquals("there being the dog on the rock", this.realiser
//		// //$NON-NLS-1$
//		// .realise(this.s2).getRealisation());
//		Assert.assertEquals("le chien étant sur le rocher", this.realiser //$NON-NLS-1$
//				.realise(this.s2).getRealisation());
//
//		// gerund with possessive
//		this.s3.setFeature(Feature.FORM, Form.GERUND);
//		// Assert.assertEquals("the man's giving the woman John's flower",
//		// //$NON-NLS-1$
//		// this.realiser.realise(this.s3).getRealisation());
//		Assert.assertEquals("l'homme ayant donné une fleur à la femme", //$NON-NLS-1$
//				this.realiser.realise(this.s3).getRealisation());

		// imperative
		this.s3.setFeature(Feature.FORM, Form.IMPERATIVE);

		Assert.assertEquals("donne une fleur à la femme", this.realiser //$NON-NLS-1$
				.realise(this.s3).getRealisation());

		// subordinating the imperative to a verb should turn it to infinitive
		this.dire.addComplement(this.s3);

		Assert.assertEquals("dit donner une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());
		WordElement de = this.lexicon.lookupWord("de");
		this.s3.setFeature(Feature.COMPLEMENTISER, de);
		Assert.assertEquals("dit de donner une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());
		WordElement que = this.lexicon.lookupWord("que", LexicalCategory.COMPLEMENTISER);
		this.s3.setFeature(Feature.COMPLEMENTISER, que);
		Assert.assertEquals("dit donner une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.dire).getRealisation());

		// imperative -- case II
		this.s4.setFeature(Feature.FORM, Form.IMPERATIVE);
		Assert.assertEquals(
				"cependant, demain, ramassez les balles dans le magasin", //$NON-NLS-1$
				this.realiser.realise(this.s4).getRealisation());

		// infinitive -- case II
		this.s4 = this.factory.createClause();
		this.s4.setFeature(Feature.CUE_PHRASE, "cependant"); //$NON-NLS-1$
		this.s4.addFrontModifier("demain"); //$NON-NLS-1$

		CoordinatedPhraseElement subject = factory
				.createCoordinatedPhrase(this.factory
						.createNounPhrase("Jane"), this.factory //$NON-NLS-1$
						.createNounPhrase("André")); //$NON-NLS-1$

		this.s4.setFeature(InternalFeature.SUBJECTS, subject);

		PhraseElement pick = this.factory.createVerbPhrase("ramasser"); //$NON-NLS-1$
		this.s4.setFeature(InternalFeature.VERB_PHRASE, pick);
		this.s4.setObject("les balles"); //$NON-NLS-1$
		this.s4.addPostModifier("dans le magasin"); //$NON-NLS-1$
		this.s4.setFeature(Feature.TENSE, Tense.FUTURE);
		this.s4.setFeature(Feature.FORM, Form.INFINITIVE);
		Assert.assertEquals(
				"ramasser les balles dans le magasin demain", //$NON-NLS-1$
				this.realiser.realise(this.s4).getRealisation());
		
		NPPhraseSpec fille = factory.createNounPhrase("le", "fille");
		SPhraseSpec propImper = factory.createClause(fille, "promener", "se");
		Assert.assertEquals("la fille se promène", realise(propImper));
		propImper.setFeature(Feature.FORM, Form.IMPERATIVE);
		Assert.assertEquals("promène-toi", realise(propImper));
	}

	/**
	 * Slightly more complex tests for forms.
	 */
	public void testForm2() {
		// set s4 as subject of a new sentence
		SPhraseSpec temp = this.factory.createClause(this.s4, "être", //$NON-NLS-1$
				"rassurant"); //$NON-NLS-1$

		Assert.assertEquals(
				"le fait que cependant, demain, Jane et André ramasseront "
				+ "les balles dans le magasin est rassurant"
				/*
				 * "however tomorrow Jane and Andrew's picking up the " +
				 * //$NON-NLS-1$ "balls in the shop is recommended"
				 */, //$NON-NLS-1$
				this.realiser.realise(temp).getRealisation());

		// compose this with a new sentence
		// ER - switched direct and indirect object in sentence
		SPhraseSpec temp2 = this.factory.createClause("je", "dire", temp); //$NON-NLS-1$ //$NON-NLS-2$
		temp2.setFeature(Feature.TENSE, Tense.FUTURE);

		PhraseElement indirectObject = this.factory
				.createNounPhrase("Jean"); //$NON-NLS-1$

		temp2.setIndirectObject(indirectObject);

		Assert.assertEquals("je dirai à Jean que le fait que cependant, demain, "
				+ "Jane et André ramasseront les balles dans le magasin est rassurant", //$NON-NLS-1$
				this.realiser.realise(temp2).getRealisation());

		// turn s4 to imperative and put it in indirect object position

		this.s4 = this.factory.createClause();
		this.s4.setFeature(Feature.CUE_PHRASE, "cependant"); //$NON-NLS-1$
		this.s4.addFrontModifier("demain"); //$NON-NLS-1$

		CoordinatedPhraseElement subject = factory
				.createCoordinatedPhrase(this.factory
						.createNounPhrase("Jane"), this.factory //$NON-NLS-1$
						.createNounPhrase("André")); //$NON-NLS-1$

		this.s4.setSubject(subject);

		PhraseElement pick = this.factory.createVerbPhrase("ramasser"); //$NON-NLS-1$
		this.s4.setVerbPhrase(pick);
		this.s4.setObject("les balles"); //$NON-NLS-1$
		this.s4.addPostModifier("dans le magasin"); //$NON-NLS-1$
		this.s4.setFeature(Feature.TENSE, Tense.FUTURE);
		this.s4.setFeature(Feature.FORM, Form.IMPERATIVE);

		temp2 = this.factory.createClause("je", "dire", this.s4); //$NON-NLS-1$ //$NON-NLS-2$
		indirectObject = this.factory.createNounPhrase("Jean"); //$NON-NLS-1$
		temp2.setIndirectObject(indirectObject);
		temp2.setFeature(Feature.TENSE, Tense.FUTURE);

		// We must specified that we want "de" as complementiser, because not putting one
		// is possible, but has another meaning
		// CUE_PHRASE are now removed in French infinitive phrases 
		Object complementiser = this.s4.getFeature(Feature.COMPLEMENTISER);
		WordElement de = this.lexicon.lookupWord("de");
		this.s4.setFeature(Feature.COMPLEMENTISER, de);
		Assert.assertEquals("je dirai à Jean de ramasser les balles " //$NON-NLS-1$
				+ "dans le magasin demain", this.realiser.realise(temp2) //$NON-NLS-1$
				.getRealisation());
		this.s4.setFeature(Feature.COMPLEMENTISER, complementiser);
	}

	// /**
	// * Tests for gerund forms and genitive subjects.
	// */
	// @Test
	// public void testGerundsubject() {
	//
	// // the man's giving the woman John's flower upset Peter
	// // (*) l'homme donnant une fleur à la femme a dérangé Pierre
	// SPhraseSpec _s4 = this.phraseFactory.createClause();
	// _s4.setVerbPhrase(this.phraseFactory
	// .createVerbPhrase("déranger")); //$NON-NLS-1$
	// _s4.setFeature(Feature.TENSE,Tense.PAST);
	// _s4.setObject(this.phraseFactory.createNounPhrase("Pierre"));
	// //$NON-NLS-1$
	// this.s3.setFeature(Feature.PERFECT, true);
	//
	// // set the sentence as subject of another: makes it a gerund
	// _s4.setSubject(this.s3);
	//
	// // suppress the genitive realisation of the NP subject in gerund
	// // sentences
	// this.s3.setFeature(Feature.SUPPRESS_GENITIVE_IN_GERUND, true);
	//
	// // check the realisation: subject should not be genitive
	// Assert.assertEquals(
	// "the man having given the woman John's flower upset Peter", //$NON-NLS-1$
	// this.realiser.realise(_s4).getRealisation());
	//
	// }

	// /**
	// * Some tests for multiple embedded sentences.
	// */
	// @Test
	// public void testComplexSentence1() {
	// setUp();
	// // the man's giving the woman John's flower upset Peter
	// SPhraseSpec complexS = this.phraseFactory.createClause();
	// complexS.setVerbPhrase(this.phraseFactory.createVerbPhrase("upset"));
	// //$NON-NLS-1$
	// complexS.setFeature(Feature.TENSE,Tense.PAST);
	// complexS.setObject(this.phraseFactory.createNounPhrase("Peter"));
	// //$NON-NLS-1$
	// this.s3.setFeature(Feature.PERFECT, true);
	// complexS.setSubject( this.s3);
	//
	// // check the realisation: subject should be genitive
	// Assert.assertEquals(
	// "the man's having given the woman John's flower upset Peter",
	// //$NON-NLS-1$
	// this.realiser.realise(complexS).getRealisation());
	//
	// setUp();
	// // coordinate sentences in subject position
	// SPhraseSpec s5 = this.phraseFactory.createClause();
	// s5.setSubject( this.phraseFactory
	// .createNounPhrase("some", "person")); //$NON-NLS-1$ //$NON-NLS-2$
	// s5.setVerbPhrase(this.phraseFactory.createVerbPhrase("stroke"));
	// //$NON-NLS-1$
	// s5.setObject(this.phraseFactory.createNounPhrase("the", "cat"));
	// //$NON-NLS-1$ //$NON-NLS-2$
	//
	// CoordinatedPhraseElement coord =
	// phraseFactory.createCoordinatedPhrase(this.s3,
	// s5);
	// complexS = this.phraseFactory.createClause();
	// complexS.setVerbPhrase(this.phraseFactory.createVerbPhrase("upset"));
	// //$NON-NLS-1$
	// complexS.setFeature(Feature.TENSE,Tense.PAST);
	// complexS.setObject(this.phraseFactory.createNounPhrase("Peter"));
	// //$NON-NLS-1$
	// complexS.setSubject(coord);
	// this.s3.setFeature(Feature.PERFECT, true);
	//
	// Assert.assertEquals("the man's having given the woman John's flower "
	// //$NON-NLS-1$
	// + "and some person's stroking the cat upset Peter", //$NON-NLS-1$
	// this.realiser.realise(complexS).getRealisation());
	//
	// setUp();
	// // now subordinate the complex sentence
	// // coord.setClauseStatus(SPhraseSpec.ClauseType.MAIN);
	// SPhraseSpec s6 = this.phraseFactory.createClause();
	// s6.setVerbPhrase(this.phraseFactory.createVerbPhrase("tell"));
	// //$NON-NLS-1$
	// s6.setFeature(Feature.TENSE,Tense.PAST);
	// s6.setSubject(this.phraseFactory
	// .createNounPhrase("the", "boy")); //$NON-NLS-1$ //$NON-NLS-2$
	// // ER - switched indirect and direct object
	// PhraseElement indirect = this.phraseFactory.createNounPhrase("every",
	// //$NON-NLS-1$
	// "girl"); //$NON-NLS-1$
	// s6.setIndirectObject(indirect);
	// complexS = this.phraseFactory.createClause();
	// complexS.setVerbPhrase(this.phraseFactory.createVerbPhrase("upset"));
	// //$NON-NLS-1$
	// complexS.setFeature(Feature.TENSE,Tense.PAST);
	// complexS.setObject(this.phraseFactory.createNounPhrase("Peter"));
	// //$NON-NLS-1$
	// s6.setObject(complexS);
	// coord = phraseFactory.createCoordinatedPhrase(this.s3, s5);
	// complexS.setSubject(coord);
	// this.s3.setFeature(Feature.PERFECT, true);
	// Assert.assertEquals(
	// "the boy told every girl that the man's having given the woman "
	// //$NON-NLS-1$
	// + "John's flower and some person's stroking the cat " //$NON-NLS-1$
	// + "upset Peter", //$NON-NLS-1$
	// this.realiser.realise(s6).getRealisation());
	//
	// }

	/**
	 * More coordination tests.
	 */
	@Test
	public void testComplexSentence3() {
		setUp();

		this.s1 = this.factory.createClause();
		this.s1.setSubject(this.femme);
		this.s1.setVerb("embrasser");
		this.s1.setObject(this.homme);

		PhraseElement _man = this.factory.createNounPhrase("le", "homme"); //$NON-NLS-1$ //$NON-NLS-2$
		this.s3 = this.factory.createClause();
		this.s3.setSubject(_man);
		this.s3.setVerb("donner");

		NPPhraseSpec flower = this.factory.createNounPhrase("un", "fleur"); //$NON-NLS-1$
		this.s3.setObject(flower);

		PhraseElement _woman = this.factory.createNounPhrase(
				"le", "femme"); //$NON-NLS-1$ //$NON-NLS-2$
		this.s3.setIndirectObject(_woman);

		// the coordinate sentence allows us to raise and lower complementiser
		CoordinatedPhraseElement coord2 = factory
				.createCoordinatedPhrase(this.s1, this.s3);
		coord2.setFeature(Feature.TENSE, Tense.PAST);

		Assert
				.assertEquals(
						"la femme a embrassé l'homme et l'homme a donné une fleur à la femme", //$NON-NLS-1$
						// "the woman kissed the man and the man gave the woman
						// John's flower", //$NON-NLS-1$
						this.realiser.realise(coord2).getRealisation());
	}

	/**
	 * Tests recogition of strings in API.
	 */
	@Test
	public void testStringRecognition() {

		// test recognition of forms of "être"
		PhraseElement _s1 = this.factory.createClause(
				"mon chat", "être", "triste"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Assert
				.assertEquals(
						"mon chat est triste", this.realiser.realise(_s1).getRealisation()); //$NON-NLS-1$

		// test recognition of pronoun for afreement
		PhraseElement _s2 = this.factory.createClause(
				"je", "vouloir", "Marie"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		Assert.assertEquals(
				"je veux Marie", this.realiser.realise(_s2).getRealisation()); //$NON-NLS-1$

		// test recognition of pronoun for correct form
		PhraseElement subject = this.factory.createNounPhrase("chien"); //$NON-NLS-1$
		subject.setFeature(InternalFeature.SPECIFIER, "un"); //$NON-NLS-1$
		subject.addPostModifier("d'à côté"); //$NON-NLS-1$
		PhraseElement object = this.factory.createNounPhrase("je"); //$NON-NLS-1$
		PhraseElement s = this.factory.createClause(subject,
				"pourchasser", object); //$NON-NLS-1$
		s.setFeature(Feature.PROGRESSIVE, true);
		Assert.assertEquals("un chien d'à côté est en train de me pourchasser", //$NON-NLS-1$
				this.realiser.realise(s).getRealisation());
	}

	/**
	 * Tests complex agreement.
	 */
	@Test
	public void testAgreement() {

		// basic agreement
		NPPhraseSpec np = this.factory.createNounPhrase("chien"); //$NON-NLS-1$
		np.setSpecifier("le"); //$NON-NLS-1$
		np.addModifier("fâché"); //$NON-NLS-1$
		PhraseElement _s1 = this.factory.createClause(np,
				"pourchasser", "Jean"); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.assertEquals("le chien fâché pourchasse Jean", this.realiser //$NON-NLS-1$
				.realise(_s1).getRealisation());

		// plural
		np = this.factory.createNounPhrase("chien"); //$NON-NLS-1$
		np.setSpecifier("le"); //$NON-NLS-1$
		np.addModifier("fâché"); //$NON-NLS-1$
		np.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		_s1 = this.factory.createClause(np, "pourchasser", "Jean"); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.assertEquals("les chiens fâchés pourchassent Jean", this.realiser //$NON-NLS-1$
				.realise(_s1).getRealisation());

		// test agreement with "there is"
		np = this.factory.createNounPhrase("chien"); //$NON-NLS-1$
		np.addModifier("fâché"); //$NON-NLS-1$
		np.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR);
		np.setSpecifier("un"); //$NON-NLS-1$
		SPhraseSpec _s2 = this.factory.createClause("il", "avoir", np); //$NON-NLS-1$ //$NON-NLS-2$
		_s2.getSubject().setFeature(LexicalFeature.EXPLETIVE_SUBJECT, true);
		_s2.addComplement("y");
		Assert.assertEquals("il y a un chien fâché", this.realiser //$NON-NLS-1$
				.realise(_s2).getRealisation());

		// plural with "there"
		np = this.factory.createNounPhrase("chien"); //$NON-NLS-1$
		np.addModifier("fâché"); //$NON-NLS-1$
		np.setSpecifier("un"); //$NON-NLS-1$
		np.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		_s2 = this.factory.createClause("il", "avoir", np); //$NON-NLS-1$ //$NON-NLS-2$
		_s2.addComplement("y");
		Assert.assertEquals("il y a des chiens fâchés", this.realiser //$NON-NLS-1$
				.realise(_s2).getRealisation());
	}

	/**
	 * Tests passive.
	 */
	@Test
	public void testPassive() {
		// passive with just complement
		SPhraseSpec _s1 = this.factory.createClause(null,
				"intuber", this.factory.createNounPhrase("le", "bébé")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		_s1.setFeature(Feature.PASSIVE, true);

		Assert.assertEquals("le bébé est intubé", this.realiser //$NON-NLS-1$
				.realise(_s1).getRealisation());

		// passive with subject and complement
		_s1 = this.factory.createClause(null,
				"intuber", this.factory.createNounPhrase("le", "bébé")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		_s1.setSubject(this.factory.createNounPhrase("l'infirmière")); //$NON-NLS-1$
		_s1.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("le bébé est intubé par l'infirmière", //$NON-NLS-1$
				this.realiser.realise(_s1).getRealisation());

		// passive with subject and indirect object
		PhraseElement morphine = this.factory
			.createNounPhrase("50ug de morphine"); //$NON-NLS-1$
		morphine.setPlural(true);
		SPhraseSpec _s2 = this.factory.createClause(null, "donner", //$NON-NLS-1$
				morphine);
		_s2.setIndirectObject(this.factory.createNounPhrase("le", "bébé")); //$NON-NLS-1$ //$NON-NLS-2$
		_s2.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("50ug de morphine sont donnés au bébé", //$NON-NLS-1$
				this.realiser.realise(_s2).getRealisation());

		// passive with subject, complement and indirect object
		morphine = this.factory.createNounPhrase("50ug de morphine"); //$NON-NLS-1$
		morphine.setPlural(true);
		_s2 = this.factory.createClause(this.factory
				.createNounPhrase("le", "infirmière"), "donner", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				morphine); //$NON-NLS-1$ //$NON-NLS-2$

		_s2.setIndirectObject(this.factory.createNounPhrase("le", "bébé")); //$NON-NLS-1$ //$NON-NLS-2$
		_s2.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("50ug de morphine sont donnés au bébé par l'infirmière", //$NON-NLS-1$
				this.realiser.realise(_s2).getRealisation());

		// test agreement in passive
		SPhraseSpec _s3 = this.factory.createClause(factory
				.createCoordinatedPhrase("mon chien", "ton chat"), "pourchasser", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"Georges"); //$NON-NLS-1$
		_s3.setFeature(Feature.TENSE, Tense.PAST);
		_s3.addFrontModifier("hier"); //$NON-NLS-1$
		Assert.assertEquals("hier, mon chien et ton chat ont pourchassé Georges", //$NON-NLS-1$
				this.realiser.realise(_s3).getRealisation());

		_s3 = this.factory.createClause(factory
				.createCoordinatedPhrase("mon chien", "ton chat"), "pourchasser", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				this.factory.createNounPhrase("Georges")); //$NON-NLS-1$
		_s3.setFeature(Feature.TENSE, Tense.PAST);
		_s3.addFrontModifier("hier"); //$NON-NLS-1$
		_s3.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals(
				"hier, Georges a été pourchassé par mon chien et ton chat", //$NON-NLS-1$
				this.realiser.realise(_s3).getRealisation());
		_s3.setObject("elles");
		Assert.assertEquals(
				"hier, elles ont été pourchassées par mon chien et ton chat", //$NON-NLS-1$
				this.realiser.realise(_s3).getRealisation());

		// test correct pronoun forms
		PhraseElement _s4 = this.factory.createClause(this.factory
				.createNounPhrase("il"), "pourchasser", //$NON-NLS-1$ //$NON-NLS-2$
				this.factory.createNounPhrase("je")); //$NON-NLS-1$
		Assert.assertEquals("il me pourchasse", this.realiser.realise(_s4) //$NON-NLS-1$
				.getRealisation());
		_s4 = this.factory
				.createClause(
						this.factory.createNounPhrase("il"), "pourchasser", this.factory.createNounPhrase("je")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		_s4.setFeature(Feature.PASSIVE, true);
		Assert
				.assertEquals(
						"je suis pourchassé par lui", this.realiser.realise(_s4).getRealisation()); //$NON-NLS-1$

		// same thing, but giving the S constructor "me". Should recognise
		// correct pro
		// anyway
		PhraseElement _s5 = this.factory
				.createClause("lui", "pourchasser", "je"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Assert.assertEquals(
				"il me pourchasse", this.realiser.realise(_s5).getRealisation()); //$NON-NLS-1$

		_s5 = this.factory.createClause("lui", "pourchasser", "moi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		_s5.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals(
						"je suis pourchassé par lui", this.realiser.realise(_s5).getRealisation()); //$NON-NLS-1$
		NPPhraseSpec unePomme = this.factory.createNounPhrase("une", "pomme");
		unePomme.setFeature(Feature.PRONOMINAL, true);
		PhraseElement _s6 = this.factory
			.createClause("je", "mange", unePomme);
		_s6.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("elle est mangée par moi",
				this.realiser.realise(_s6).getRealisation());

	}

	/**
	 * Tests tenses with modals.
	 */
	public void testModal() {

		setUp();
		// simple modal in present tense
		this.s3.setFeature(Feature.MODAL, "devoir"); //$NON-NLS-1$
		Assert.assertEquals("l'homme doit donner une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());

		// modal + future -- uses present
		setUp();
		this.s3.setFeature(Feature.MODAL, "devoir"); //$NON-NLS-1$
		this.s3.setFeature(Feature.TENSE, Tense.FUTURE);
		Assert.assertEquals("l'homme devra donner une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());

		// modal + present progressive
		setUp();
		this.s3.setFeature(Feature.MODAL, "devoir"); //$NON-NLS-1$
		this.s3.setFeature(Feature.TENSE, Tense.FUTURE);
		this.s3.setFeature(Feature.PROGRESSIVE, true);
		Assert.assertEquals("l'homme devra être en train de donner une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());

		// modal + past tense
		setUp();
		this.s3.setFeature(Feature.MODAL, "devoir"); //$NON-NLS-1$
		this.s3.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals(
				"l'homme doit avoir donné une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());

		// modal + past progressive
		setUp();
		this.s3.setFeature(Feature.MODAL, "devoir"); //$NON-NLS-1$
		this.s3.setFeature(Feature.TENSE, Tense.PAST);
		this.s3.setFeature(Feature.PROGRESSIVE, true);

		Assert.assertEquals(
				"l'homme doit avoir été en train de donner une fleur à la femme", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());

	}

	/**
	 * Test for adjectival attribute agreement.
	 * @author vaudrypl
	 */
	@Test
	public void testAttribute() {
		setUp();
		// subject attribute :
		// with "être", the basic copular verb
		this.femme.setPlural(true);
		SPhraseSpec clause = factory.createClause(this.femme, "être", this.beau); 
		Assert.assertEquals("les femmes sont belles", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		clause.setSubject(this.homme);
		Assert.assertEquals("l'homme est beau", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		
		// with "devenir", another copular verb and adjective "beau" given as a string 
		clause = factory.createClause(this.femme, "devenir", "beau");
		Assert.assertEquals("les femmes deviennent belles", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		clause.setSubject(this.homme);
		Assert.assertEquals("l'homme devient beau", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		
		// object attribute : add adjective as modifier to verb phrase
		clause = factory.createClause("ils", "trouver", this.femme);
		clause.addModifier(this.beau);
		Assert.assertEquals("ils trouvent les femmes belles", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		this.femme.setFeature(Feature.PRONOMINAL, true);
		Assert.assertEquals("ils les trouvent belles", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		clause.setObject(this.homme);
		Assert.assertEquals("ils trouvent l'homme beau", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		
		// participle attribute
		this.femme.setFeature(Feature.PRONOMINAL, false);
		String verb = "impressionner";
		NLGElement adjP = this.factory.createNLGElement(verb, LexicalCategory.VERB);
		adjP.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
		clause = factory.createClause(this.femme, "être", adjP); 
		Assert.assertEquals("les femmes sont impressionnées", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		adjP.setFeature(Feature.FORM, Form.PRESENT_PARTICIPLE);
		clause = factory.createClause("ils", "trouver", this.femme);
		clause.addModifier(adjP);
		Assert.assertEquals("ils trouvent les femmes impressionnantes", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
	}

	/**
	 * Tests past participle agreement.
	 * @author vaudrypl
	 */
	@Test
	public void testPastParticipleAgreement() {
		setUp();
		
		// passive
		s1.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("l'homme est embrassé par la femme", //$NON-NLS-1$
				this.realiser.realise(this.s1).getRealisation());
		this.femme.setPlural(true);
		SPhraseSpec clause = factory.createClause(this.homme, this.embrasser, this.femme); 
		clause.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("les femmes sont embrassées par l'homme", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		
		// past with auxiliary "être"
		clause = factory.createClause(this.homme, "aller");
		clause.setFeature(Feature.TENSE, Tense.PAST);
		clause.setComplement(this.dansLaPiece);
		Assert.assertEquals("l'homme est allé dans la pièce", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		clause.setSubject(this.femme);
		Assert.assertEquals("les femmes sont allées dans la pièce", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		
		// past with auxiliary "avoir"
		// without direct object
		clause.setSubject(this.homme);
		clause.setVerb("manger");
		Assert.assertEquals("l'homme a mangé dans la pièce", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		clause.setSubject(this.femme);
		Assert.assertEquals("les femmes ont mangé dans la pièce", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		
		// with non-clitic direct object (pomme is feminine in French)
		NPPhraseSpec pommes = factory.createNounPhrase("un", "pomme");
		pommes.setPlural(true);
		clause.setObject(pommes);
		Assert.assertEquals("les femmes ont mangé des pommes dans la pièce", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		clause.setSubject(this.homme);
		Assert.assertEquals("l'homme a mangé des pommes dans la pièce", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		
		// with clitic direct object
		pommes.setFeature(Feature.PRONOMINAL, true);
		Assert.assertEquals("l'homme les a mangées dans la pièce", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		clause.setObject("la");
		Assert.assertEquals("l'homme l'a mangée dans la pièce", //$NON-NLS-1$
				this.realiser.realise(clause).getRealisation());
		
//		// in a relative clause with relative pronoun as subject
//		clause.clearComplements();
//		this.femme.addModifier(clause);
//		clause.setFeature(Feature.COMPLEMENTISER, "qui");
//		Assert.assertEquals("les femmes qui ont mangé dans la pièce", //$NON-NLS-1$
//				this.realiser.realise(clause).getRealisation());
//
//		// in a relative clause with relative pronoun as object
//		Assert.assertEquals("les pommes que l'homme a mangées dans la pièce", //$NON-NLS-1$
//				this.realiser.realise(clause).getRealisation());
//		
//		// in clause subordinate to a verb
	}

	/**
	 * Tests for partitive direct object under negation.
	 */
	@Test
	public void testPartitiveNegation() {
		SPhraseSpec boire = factory.createClause("il", "boire");
		NPPhraseSpec duVin = factory.createNounPhrase("du", "vin");
		boire.setObject(duVin);
		Assert.assertEquals("il boit du vin",
				this.realiser.realise(boire).getRealisation()); //$NON-NLS-1$
		boire.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("il ne boit pas de vin",
				this.realiser.realise(boire).getRealisation()); //$NON-NLS-1$
		boire.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("du vin n'est pas bu par lui",
				this.realiser.realise(boire).getRealisation()); //$NON-NLS-1$

		SPhraseSpec manger = factory.createClause("il", "manger");
		NPPhraseSpec delaViande = factory.createNounPhrase("du", "viande");
		manger.setObject(delaViande);
		Assert.assertEquals("il mange de la viande",
				this.realiser.realise(manger).getRealisation()); //$NON-NLS-1$
		manger.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("il ne mange pas de viande",
				this.realiser.realise(manger).getRealisation()); //$NON-NLS-1$
		manger.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("de la viande n'est pas mangée par lui",
				this.realiser.realise(manger).getRealisation()); //$NON-NLS-1$
	}

}
