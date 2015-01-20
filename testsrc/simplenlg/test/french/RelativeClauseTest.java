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

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Tense;
import simplenlg.features.french.FrenchFeature;
import simplenlg.features.french.FrenchInternalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;

/**
 * These are tests for verb conjugation.
 * @author vaudrypl
 */
public class RelativeClauseTest extends SimpleNLG4TestBase {

	SPhraseSpec clause;
	
	/**
	 * Instantiates a new conjugation test.
	 * 
	 * @param name
	 *            the name
	 */
	public RelativeClauseTest(String name) {
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

		// "l'homme a donné une fleur à la femme"
		clause = factory.createClause();
		clause.setSubject(factory.createNounPhrase("le","homme"));
		clause.setVerbPhrase(donner);
		clause.setFeature(Feature.TENSE, Tense.PAST);		
		NPPhraseSpec fleur = factory.createNounPhrase("un", "fleur");
		clause.setObject(fleur);
		clause.setIndirectObject(factory.createNounPhrase("le","femme"));
	}

	/**
	 * Initial test for basic clauses.
	 */
	@Test
	public void testBasic() {
		Assert.assertEquals("l'homme a donné une fleur à la femme",
				realise(clause));
	}
	/**
	 * Test relative clauses with past participle agreement.
	 */
	@Test
	public void testRelativeClause() {
		// Subject relative phrase.
		NPPhraseSpec mainNP = factory.createNounPhrase("la", "personne");
		mainNP.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		mainNP.addModifier(clause);
		
		// By specifying a real subject of the clause.
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, clause.getSubject());
		Assert.assertEquals("les personnes qui ont donné une fleur à la femme",
				realise(mainNP));
		
		// With a dummy element with discourse function SUBJECT.
		NLGElement dummyElement = factory.createStringElement();
		dummyElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.SUBJECT);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, dummyElement);
		Assert.assertEquals("les personnes qui ont donné une fleur à la femme",
				realise(mainNP));
		// Passive, relativised subject.
		clause.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("les personnes par qui une fleur a été donnée à la femme",
				realise(mainNP));
		clause.setFeature(Feature.PASSIVE, false);

		// Direct object.
		mainNP.setNoun("fleur");
		mainNP.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, clause.getObject());
		Assert.assertEquals("les fleurs que l'homme a données à la femme",
				realise(mainNP));
		dummyElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.OBJECT);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, dummyElement);
		Assert.assertEquals("les fleurs que l'homme a données à la femme",
				realise(mainNP));
		// Passive, relativised direct object.
		clause.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("les fleurs qui ont été données à la femme par l'homme",
				realise(mainNP));
		clause.setFeature(Feature.PASSIVE, false);

		// Indirect object (general).
		mainNP.setNoun("personne");
		mainNP.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, clause.getIndirectObject());
		Assert.assertEquals("les personnes auxquelles l'homme a donné une fleur",
				realise(mainNP));
		dummyElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.INDIRECT_OBJECT);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, dummyElement);
		Assert.assertEquals("les personnes auxquelles l'homme a donné une fleur",
				realise(mainNP));
		
		// Indirect object (1st and 2nd person).
		mainNP.setSpecifier(null);
		mainNP.setPronoun("toi");
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, clause.getIndirectObject());
		Assert.assertEquals("toi à qui l'homme a donné une fleur",
				realise(mainNP));
		dummyElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.INDIRECT_OBJECT);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, dummyElement);
		Assert.assertEquals("toi à qui l'homme a donné une fleur",
				realise(mainNP));
		
		// Indirect object (proper name).
		mainNP.setSpecifier(null);
		NLGElement properName = factory.createWord("Jean-Pierre", LexicalCategory.NOUN);
		properName.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
		properName.setFeature(LexicalFeature.PROPER, true);
		mainNP.setNoun(properName);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, clause.getIndirectObject());
		Assert.assertEquals("Jean-Pierre à qui l'homme a donné une fleur",
				realise(mainNP));
		dummyElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.INDIRECT_OBJECT);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, dummyElement);
		Assert.assertEquals("Jean-Pierre à qui l'homme a donné une fleur",
				realise(mainNP));
		
		// Indirect object (3nd person, neuter gender).
		mainNP.setSpecifier(null);
		mainNP.setPronoun("ce");
		mainNP.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		clause.setVerb("répondre");
		clause.clearComplements();
		NPPhraseSpec npQuestion = factory.createNounPhrase("une","question");
		clause.setIndirectObject(npQuestion);
		clause.setObject("que oui");
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, clause.getIndirectObject());
		Assert.assertEquals("ce à quoi l'homme a répondu que oui",
				realise(mainNP));
		dummyElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.INDIRECT_OBJECT);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, dummyElement);
		Assert.assertEquals("ce à quoi l'homme a répondu que oui",
				realise(mainNP));
		
		// Past participle agreement with the subject with auxiliary "être"
		mainNP.setSpecifier("le");
		mainNP.setNoun("personne");
		mainNP.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		clause.setVerb("aller");
		clause.clearComplements();
		PPPhraseSpec montreal = factory.createPrepositionPhrase("à","Montréal");
		clause.addComplement(montreal);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, clause.getSubject());
		Assert.assertEquals("les personnes qui sont allées à Montréal",
				realise(mainNP));
		dummyElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.SUBJECT);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, dummyElement);
		Assert.assertEquals("les personnes qui sont allées à Montréal",
				realise(mainNP));

		// Other kinds of complement.
		// Here only a complement that is specifically specified as the
		// relative phrase will be omitted from the realised form.
		mainNP.setNoun("pièce");
		mainNP.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		PPPhraseSpec cuisine = factory.createPrepositionPhrase("dans",
				factory.createNounPhrase("le", "cuisine"));
		clause.clearComplements();
		clause.addComplement(cuisine);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, cuisine);
		Assert.assertEquals("les pièces dans lesquelles l'homme est allé",
				realise(mainNP));
		// A dummy preposition phrase can be used to specify the preposition used
		// with the relative pronoun.
		mainNP.setNoun("heure");
		PPPhraseSpec dummyPrepositionPhrase = factory.createPrepositionPhrase("autour de");
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, dummyPrepositionPhrase);
		Assert.assertEquals("l'heure autour de laquelle l'homme est allé dans la cuisine",
				realise(mainNP));
		mainNP.setNoun("heure");
		// de + lesquels = desquels
		mainNP.setNoun("gens");
		mainNP.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		clause.setVerb("danser");
		Assert.assertEquals("les gens autour desquels l'homme a dansé dans la cuisine",
				realise(mainNP));
		// With a front modifier. It should be placed at the end of the relative clause,
		// without a comma.
		clause.addFrontModifier("hier");
		Assert.assertEquals("les gens autour desquels l'homme a dansé dans la cuisine hier",
				realise(mainNP));
		
		// Complement with preposition "de" are replaced by relative pronoun "dont".
		clause.setVerb("parler");
		mainNP.setNoun("personne");
		PPPhraseSpec dunEtudiant = factory.createPrepositionPhrase("de",
				factory.createNounPhrase("un","étudiant"));
		clause.setIndirectObject(dunEtudiant);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, dunEtudiant);
		Assert.assertEquals("la personne dont l'homme a parlé dans la cuisine hier",
				realise(mainNP));
		// Pronoun "en" implies preposition "de", so it is replaced by "dont" also.
		NPPhraseSpec en = factory.createNounPhrase("en");
		clause.setIndirectObject(en);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, en);
		Assert.assertEquals("la personne dont l'homme a parlé dans la cuisine hier",
				realise(mainNP));
		// "dont" can replace the complement of a noun introduced by prepositon "de".
		clause.setVerb("donner");
		NPPhraseSpec nom = factory.createNounPhrase("le", "nom");
		PPPhraseSpec dePierre = factory.createPrepositionPhrase("de", "Pierre");
		nom.addComplement(dePierre);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, dePierre);
		clause.setObject(nom);
		NPPhraseSpec femme = factory.createNounPhrase("un","femme");
		clause.setIndirectObject(femme);
		Assert.assertEquals("la personne dont l'homme a donné le nom à une femme dans la cuisine hier",
				realise(mainNP));
		// When the parent of the relative prepositional phrase is also a prepositional phrase,
		// "dont" cannot be used.
		setUp();
		clause.setVerb("répondre");
		clause.addFrontModifier("hier");
		clause.clearComplements();
		NPPhraseSpec epouse = factory.createNounPhrase("le", "épouse");
		epouse.addComplement(dePierre);
		clause.setIndirectObject(factory.createPrepositionPhrase("à", epouse));
		mainNP.clearModifiers();
		// Matrix clause form.
		Assert.assertEquals("hier, l'homme a répondu à l'épouse de Pierre",
				realise(clause));
		// Relative clause form.
		mainNP.setNoun("professeur");
		mainNP.addModifier(clause);
		clause.setFeature(FrenchFeature.RELATIVE_PHRASE, dePierre);
		Assert.assertEquals("le professeur à l'épouse duquel l'homme a répondu hier",
				realise(mainNP));
		// Without preposition, the default preposition "à" is added during realisation
		// for an indirect object.
		clause.setIndirectObject(epouse);
		Assert.assertEquals("le professeur à l'épouse duquel l'homme a répondu hier",
				realise(mainNP));
		Assert.assertFalse("RELATIVISED internal feature not removed after use.",
				dePierre.hasFeature(FrenchInternalFeature.RELATIVISED));
	}
}
