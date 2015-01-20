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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import simplenlg.aggregation.ClauseCoordinationRule;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Gender;
import simplenlg.features.LexicalFeature;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;

/**
 * Tests from third parties
 * @author ereiter
 * 
 * translated and adapted by vaudrypl
 */
public class ExternalTest extends SimpleNLG4TestBase {

	public ExternalTest(String name) {
		super(name);
	}

	/**
	 * Basic tests
	 * 
	 */
	@Test
	public void testForcher() {
		// Bjorn Forcher's tests
		this.factory.setLexicon(this.lexicon);
		WordElement marie = this.lexicon.lookupWord("Marie", LexicalCategory.NOUN);
		marie.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		SPhraseSpec s1 = this.factory.createClause(null, "associer",
				marie);
		s1.setFeature(Feature.PASSIVE, true);
		PhraseElement pp1 = this.factory.createPrepositionPhrase("avec"); //$NON-NLS-1$
		pp1.addComplement("Pierre"); //$NON-NLS-1$
		pp1.addComplement("Paul"); //$NON-NLS-1$
		s1.addPostModifier(pp1);

		Assert.assertEquals("Marie est associée avec Pierre et Paul", //$NON-NLS-1$
				this.realiser.realise(s1).getRealisation());
		SPhraseSpec s2 = this.factory.createClause();
		s2.setSubject(this.factory
				.createNounPhrase("Pierre")); //$NON-NLS-1$
		s2.setVerb("avoir"); //$NON-NLS-1$
		s2.setObject("quelque chose à voir"); //$NON-NLS-1$
		s2.addPostModifier(this.factory.createPrepositionPhrase(
				"avec", "Paul")); //$NON-NLS-1$ //$NON-NLS-2$


		Assert.assertEquals("Pierre a quelque chose à voir avec Paul", //$NON-NLS-1$
				this.realiser.realise(s2).getRealisation());
	}

	@Test
	public void testLu() {
		// Xin Lu's test
		this.factory.setLexicon(this.lexicon);
		PhraseElement s1 = this.factory.createClause("nous", //$NON-NLS-1$
				"considérer", //$NON-NLS-1$
				"Jean"); //$NON-NLS-1$
		s1.addPostModifier("un ami"); //$NON-NLS-1$

		Assert.assertEquals("nous considérons Jean un ami", this.realiser //$NON-NLS-1$
				.realise(s1).getRealisation());
	}

	@Test
	public void testDwight() {
		// Rachel Dwight's test
		this.factory.setLexicon(this.lexicon);

		NPPhraseSpec noun4 = this.factory
				.createNounPhrase("gène FGFR3 dans toutes les cellules"); //$NON-NLS-1$

		noun4.setSpecifier("le");

		PhraseElement prep1 = this.factory.createPrepositionPhrase(
				"de", noun4); //$NON-NLS-1$

		NLGElement mere = factory.createNLGElement("mère du patient", LexicalCategory.NOUN);
		mere.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		PhraseElement noun1 = this.factory.createNounPhrase(
				"le", mere); //$NON-NLS-1$ //$NON-NLS-2$
		PhraseElement noun2 = this.factory.createNounPhrase(
				"le", "père du patient"); //$NON-NLS-1$ //$NON-NLS-2$

		PhraseElement noun3 = this.factory
				.createNounPhrase("copie modifiée"); //$NON-NLS-1$
		noun3.addPreModifier("une"); //$NON-NLS-1$
		noun3.addComplement(prep1);

		CoordinatedPhraseElement coordNoun1 = factory.createCoordinatedPhrase(
				noun1, noun2);
		coordNoun1.setConjunction( "ou"); //$NON-NLS-1$

		PhraseElement verbPhrase1 = this.factory.createVerbPhrase("avoir"); //$NON-NLS-1$
		verbPhrase1.setFeature(Feature.TENSE,Tense.PRESENT);

		PhraseElement sentence1 = this.factory.createClause(coordNoun1,
				verbPhrase1, noun3);

		Assert.assertEquals(
//				"the patient's mother or the patient's father has one changed copy of the FGFR3 gene in every cell", //$NON-NLS-1$
				"la mère du patient ou le père du patient a une copie modifiée du gène FGFR3 dans toutes les cellules",
						this.realiser.realise(sentence1).getRealisation());

		// Rachel's second test
		noun3 = this.factory.createNounPhrase("un", "test génétique"); //$NON-NLS-1$ //$NON-NLS-2$
		noun2 = this.factory.createNounPhrase("un", "test LDL"); //$NON-NLS-1$ //$NON-NLS-2$
		noun1 = this.factory.createNounPhrase("le", "clinique"); //$NON-NLS-1$ //$NON-NLS-2$
		verbPhrase1 = this.factory.createVerbPhrase("exécuter"); //$NON-NLS-1$

		CoordinatedPhraseElement coord1 = factory.createCoordinatedPhrase(noun2,
				noun3);
		sentence1 = this.factory.createClause(noun1, verbPhrase1, coord1);
		sentence1.setFeature(Feature.TENSE,Tense.PAST);

		Assert
				.assertEquals(
//						"the clinic performed an LDL test and a gene test", this.realiser //$NON-NLS-1$
						"la clinique a exécuté un test LDL et un test génétique", this.realiser //$NON-NLS-1$
								.realise(sentence1).getRealisation());
	}

	@Test
	public void testNovelli() {
		// Nicole Novelli's test
		PhraseElement p = this.factory.createClause(
				"Marie", "pourchasser", "Georges"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		PhraseElement pp = this.factory.createPrepositionPhrase(
				"dans", "le parc"); //$NON-NLS-1$ //$NON-NLS-2$
		p.addPostModifier(pp);

		Assert.assertEquals("Marie pourchasse Georges dans le parc", this.realiser //$NON-NLS-1$
				.realise(p).getRealisation());

		// another question from Nicole
		SPhraseSpec run = this.factory.createClause(
				"tu", "aller", "courir"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		run.setFeature(Feature.MODAL, "devoir"); //$NON-NLS-1$
		run.addPreModifier("vraiment"); //$NON-NLS-1$
		SPhraseSpec think = this.factory.createClause("je", "croire"); //$NON-NLS-1$ //$NON-NLS-2$
		think.setObject(run);
//		run.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);

		String text = this.realiser.realise(think).getRealisation();
//		Assert.assertEquals("I think you should really go running", text); //$NON-NLS-1$
		Assert.assertEquals("je crois que tu dois vraiment aller courir", text); //$NON-NLS-1$
	}

	@Test
	public void testPiotrek() {
		// Piotrek Smulikowski's test
		this.factory.setLexicon(this.lexicon);
		PhraseElement sent = this.factory.createClause(
				"je", "tirer", "le canard"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		sent.setFeature(Feature.TENSE,Tense.PAST);

		NPPhraseSpec stand = factory.createNounPhrase("le", "stand de tir");
		PhraseElement loc = this.factory.createPrepositionPhrase(
				"à", stand ); //$NON-NLS-1$ //$NON-NLS-2$
		sent.addPostModifier(loc);
		sent.setFeature(Feature.CUE_PHRASE, "alors"); //$NON-NLS-1$

//		Assert.assertEquals("then I shot the duck at the Shooting Range", //$NON-NLS-1$
		Assert.assertEquals("alors, j'ai tiré le canard au stand de tir", //$NON-NLS-1$
				this.realiser.realise(sent).getRealisation());
	}

	@Test
	public void testPrescott() {
		// Michael Prescott's test
		this.factory.setLexicon(this.lexicon);
		PhraseElement embedded = this.factory.createClause(
				"Jill", "pousser", "Spot"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		PhraseElement sent = this.factory.createClause(
				"Jack", "voir", embedded); //$NON-NLS-1$ //$NON-NLS-2$
		embedded.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);
		embedded.setFeature(Feature.FORM, Form.BARE_INFINITIVE);

		Assert.assertEquals("Jack voit Jill pousser Spot", this.realiser //$NON-NLS-1$
				.realise(sent).getRealisation());
	}

//	@Test
//	public void testWissner() {
//		// Michael Wissner's text
//
//		setUp();
//
//		PhraseElement p = this.phraseFactory.createClause("un loup", "manger"); //$NON-NLS-1$ //$NON-NLS-2$
//		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
//		Assert.assertEquals("what does a wolf eat", this.realiser.realise(p) //$NON-NLS-1$
//				.getRealisation());
//
//	}
	
	@Test
	public void testPhan() {
		// Thomas Phan's text

		setUp();

	      PhraseElement subjectElement = factory.createNounPhrase("je");
	      PhraseElement verbElement = factory.createVerbPhrase("fuir");

	      PhraseElement prepPhrase = factory.createPrepositionPhrase("de");
	      prepPhrase.addComplement("la maison");

	      verbElement.addComplement(prepPhrase);
	      SPhraseSpec newSentence = factory.createClause();
	      newSentence.setSubject(subjectElement);
	      newSentence.setVerbPhrase(verbElement);

		Assert.assertEquals("je fuis de la maison", this.realiser.realise(newSentence) //$NON-NLS-1$
				.getRealisation());

	}

	@Test
	public void testKerber() {
		// Frederic Kerber's tests
        SPhraseSpec sp =  factory.createClause("il", "avoir");
        SPhraseSpec secondSp = factory.createClause();
        secondSp.setVerb("bâtir");
        secondSp.setObject("une maison");
        secondSp.setFeature(Feature.FORM,Form.INFINITIVE);
        secondSp.setFeature(Feature.COMPLEMENTISER, "pour");
        sp.setObject("besoin de pierre");
        sp.addComplement(secondSp);
//        Assert.assertEquals("he needs stone to build a house", this.realiser.realise(sp).getRealisation());
        Assert.assertEquals("il a besoin de pierre pour bâtir une maison", this.realiser.realise(sp).getRealisation());
       
        SPhraseSpec sp2 =  factory.createClause("il", "donner");
        sp2.setIndirectObject("je");
        sp2.setObject("le livre");
        Assert.assertEquals("il me donne le livre", this.realiser.realise(sp2).getRealisation());

	}
	
//	@Test
//	public void testStephenson() {
//		// Bruce Stephenson's test
//		SPhraseSpec qs2 = this.phraseFactory.createClause();
//		qs2 = this.phraseFactory.createClause();
//		qs2.setSubject("moles d'or");
//		qs2.setVerb("sont");
//		qs2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
//		qs2.setFeature(Feature.PASSIVE, false);
//		qs2.setFeature(Feature.INTERROGATIVE_TYPE,InterrogativeType.HOW_MANY);
//		qs2.setObject("in a 2.50 g sample of pure Gold");
//		DocumentElement sentence = this.phraseFactory.createSentence(qs2);
//		Assert.assertEquals("How many moles of Gold are in a 2.50 g sample of pure Gold?", this.realiser.realise(sentence).getRealisation());
//	}
	
//	@Test
//	public void testPierre() {
//		// John Pierre's test
//		SPhraseSpec p = this.phraseFactory.createClause("Mary", "chase", "George");
//		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
//		Assert.assertEquals("What does Mary chase?", realiser.realiseSentence(p));
//
//		p = this.phraseFactory.createClause("Mary", "chase", "George");
//		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
//		Assert.assertEquals("Does Mary chase George?", realiser.realiseSentence(p));
//
//		p = this.phraseFactory.createClause("Mary", "chase", "George");
//		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHERE);
//		Assert.assertEquals("Where does Mary chase George?", realiser.realiseSentence(p));
//
//		p = this.phraseFactory.createClause("Mary", "chase", "George");
//		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHY);
//		Assert.assertEquals("Why does Mary chase George?", realiser.realiseSentence(p));
//
//		p = this.phraseFactory.createClause("Mary", "chase", "George");
//		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW);
//		Assert.assertEquals("How does Mary chase George?", realiser.realiseSentence(p));
//
//
//	}
	
	@Test
	public void testData2Text() {
		// Data2Text tests
		// test OK to have number at end of sentence
		SPhraseSpec p = this.factory.createClause("le chien", "peser", "12 kg");
		Assert.assertEquals("Le chien pèse 12 kg.", realiser.realiseSentence(p));
		
//		// test OK to have "there be" sentence with "there" as a StringElement
//		NLGElement dataDropout2 = this.phraseFactory.createNLGElement("data dropouts");
//		dataDropout2.setPlural(true);
//		SPhraseSpec sentence2 = this.phraseFactory.createClause();
//		sentence2.setSubject(this.phraseFactory.createStringElement("there"));
//		sentence2.setVerb("be");
//		sentence2.setObject(dataDropout2);
//		Assert.assertEquals("There are data dropouts.", realiser.realiseSentence(sentence2));
		
		// test OK to have gerund form verb
		SPhraseSpec weather1 = this.factory.createClause("SE 10-15", "virer", "S 15-20");
		weather1.setFeature(Feature.FORM, Form.GERUND);
		Assert.assertEquals("SE 10-15 virant S 15-20.", realiser.realiseSentence(weather1));		

		// test OK to have subject only
		SPhraseSpec weather2 = this.factory.createClause("nuageux et brumeux", "être", "XXX");
		weather2.getVerbPhrase().setFeature(Feature.ELIDED, true);
		Assert.assertEquals("Nuageux et brumeux.", realiser.realiseSentence(weather2));
		
		// test OK to have VP only
		SPhraseSpec weather3 = this.factory.createClause("S 15-20", "augmenter", "20-25");
		weather3.setFeature(Feature.FORM, Form.GERUND);
		weather3.getSubject().setFeature(Feature.ELIDED, true);
		Assert.assertEquals("Augmentant 20-25.", realiser.realiseSentence(weather3));		
		
		// conjoined test
		SPhraseSpec weather4 = this.factory.createClause("S 20-25", "devenir", "SSE");
		weather4.setFeature(Feature.FORM, Form.GERUND);
		weather4.getSubject().setFeature(Feature.ELIDED, true);
		
		CoordinatedPhraseElement coord = factory.createCoordinatedPhrase();
		coord.addCoordinate(weather1);
		coord.addCoordinate(weather3);
		coord.addCoordinate(weather4);
		coord.setConjunction("puis");
		Assert.assertEquals("SE 10-15 virant S 15-20, augmentant 20-25, puis devenant SSE.", realiser.realiseSentence(coord));		
		

		// no verb
		SPhraseSpec weather5 = this.factory.createClause("pluie", null, "fort");
		Assert.assertEquals("Pluie forte.", realiser.realiseSentence(weather5));

	}
	
	@Test
	public void testRafael() {
		// Rafael Valle's tests
		List<NLGElement> ss=new ArrayList<NLGElement>();
		ClauseCoordinationRule coord = new ClauseCoordinationRule();
		coord.setFactory(this.factory);
		
		ss.add(this.agreePhrase("John Lennon")); // john lennon agreed with it  
		ss.add(this.disagreePhrase("Geri Halliwell")); // Geri Halliwell disagreed with it
		ss.add(this.commentPhrase("Melanie B")); // Mealnie B commented on it
		ss.add(this.agreePhrase("tu")); // you agreed with it
		ss.add(this.commentPhrase("Emma Bunton")); //Emma Bunton commented on it

		List<NLGElement> results=coord.apply(ss);
		List<String> ret=this.realizeAll(results);
//		Assert.assertEquals("[John Lennon and you agreed with it, Geri Halliwell disagreed with it, Melanie B and Emma Bunton commented on it]", ret.toString());
		Assert.assertEquals("[John Lennon et toi l'avez approuvé, Geri Halliwell l'a désapprouvé, Melanie B et Emma Bunton l'ont commenté]", ret.toString());
	}
	
	private NLGElement commentPhrase(String name){  // used by testRafael
		SPhraseSpec s = factory.createClause();
		s.setSubject(factory.createNounPhrase(name));
		s.setVerbPhrase(factory.createVerbPhrase("commenter"));
		s.setObject("il");
		s.setFeature(Feature.TENSE, Tense.PAST);
		return s;
	}

	private NLGElement agreePhrase(String name){  // used by testRafael
		SPhraseSpec s = factory.createClause();
		s.setSubject(factory.createNounPhrase(name));
		s.setVerbPhrase(factory.createVerbPhrase("approuver"));
		s.setObject("il");
		s.setFeature(Feature.TENSE, Tense.PAST);
		return s;
	}

	private NLGElement disagreePhrase(String name){  // used by testRafael
		SPhraseSpec s = factory.createClause();
		s.setSubject(factory.createNounPhrase(name));
		s.setVerbPhrase(factory.createVerbPhrase("désapprouver"));
		s.setObject("il");
		s.setFeature(Feature.TENSE, Tense.PAST);
		return s;
	}

	private ArrayList<String> realizeAll(List<NLGElement> results){ // used by testRafael
		ArrayList<String> ret=new ArrayList<String>();
		for (NLGElement e : results) {
			String r = this.realiser.realise(e).getRealisation();
			ret.add(r);
		}
		return ret;
	}
	
	@Test
	public void testWikipedia() {
		// test code fragments in wikipedia
		// realisation
		NPPhraseSpec subject = factory.createNounPhrase("le", "femme");
		subject.setPlural(true);
		SPhraseSpec sentence = factory.createClause(subject, "fumer");
		sentence.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("Les femmes ne fument pas.", realiser.realiseSentence(sentence));

		// aggregation
		SPhraseSpec s1 = factory.createClause("l'homme", "être", "affamé");
		SPhraseSpec s2 = factory.createClause("l'homme", "achète", "une pomme");
		NLGElement result = new ClauseCoordinationRule().apply(s1, s2);
//		Assert.assertEquals("The man is hungry and buys an apple.", realiser.realiseSentence(result));
		Assert.assertEquals("L'homme est affamé et achète une pomme.", realiser.realiseSentence(result));

	}
}
