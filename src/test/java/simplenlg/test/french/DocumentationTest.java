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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import simplenlg.aggregation.ClauseCoordinationRule;
import simplenlg.features.ClauseStatus;
import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.features.LexicalFeature;
import simplenlg.features.french.FrenchFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.AdjPhraseSpec;
import simplenlg.phrasespec.AdvPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.Realiser;

/**
 * Tests the examples of the French documentation
 * 
 * @author vaudrypl
 */
public class DocumentationTest extends SimpleNLG4TestBase {

	NPPhraseSpec snMaison;
	
	/**
	 * Instantiates a new s test.
	 * 
	 * @param name
	 *            the name
	 */
	public DocumentationTest(String name) {
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
		snMaison = factory.createNounPhrase("le", "maison");
	}

	@Test
	public void testNoun() {
		Assert.assertEquals("la maison", this.realiser //$NON-NLS-1$
				.realise(snMaison).getRealisation());

		snMaison.setFeature(Feature.ELIDED, true);
		
		Assert.assertEquals("", this.realiser //$NON-NLS-1$
				.realise(snMaison).getRealisation());

		snMaison.setFeature(Feature.ELIDED, false);
		
		Assert.assertEquals("la maison", this.realiser //$NON-NLS-1$
				.realise(snMaison).getRealisation());

		WordElement qualite = lexicon.getWord("qualité");
		InflectedWordElement qualiteElided = new InflectedWordElement(qualite);
		qualiteElided.setFeature(Feature.ELIDED, true);
		
		NPPhraseSpec toutQualite = factory.createNounPhrase("tout", qualite);
		toutQualite.setPlural(true);
		PPPhraseSpec deToutesLesQualites = factory.createPrepositionPhrase("de", toutQualite);
		
		NPPhraseSpec laPlusImportante = factory.createNounPhrase();
		laPlusImportante.setHead(qualiteElided);
		laPlusImportante.setSpecifier("le");
		AdjPhraseSpec plusImportant = factory.createAdjectivePhrase("important");
		plusImportant.addModifier("plus");
		laPlusImportante.addPreModifier(plusImportant);
		
		NPPhraseSpec leCourage = factory.createNounPhrase("le", "courage");
		
		SPhraseSpec proposition = factory.createClause(leCourage, "être", laPlusImportante);
		proposition.addFrontModifier(deToutesLesQualites);
		
		Assert.assertEquals("De toutes les qualités, le courage est la plus importante.",
				this.realiser.realiseSentence(proposition));
		
		// style télégraphique
		NPPhraseSpec jeanPierre = factory.createNounPhrase("Jean-Pierre");
		NPPhraseSpec moi = factory.createNounPhrase("moi");
		CoordinatedPhraseElement sujet = factory.createCoordinatedPhrase(jeanPierre, moi);
		SPhraseSpec texto = factory.createClause(sujet, "partir");
		texto.setFeature(Feature.TENSE, Tense.FUTURE);
		NPPhraseSpec train = factory.createNounPhrase("le", "train");
		train.addPreModifier("premier");
		PPPhraseSpec moyenDeTransport = factory.createPrepositionPhrase("par", train);
		texto.addComplement(moyenDeTransport);
		
		sujet.setFeature(Feature.ELIDED, true);
		
		Assert.assertEquals("Partirons par le premier train.",
				this.realiser.realiseSentence(texto));
	}

	@Test
	public void testInflected() {
		WordElement gracieux = lexicon.getWord("gracieux", LexicalCategory.ADJECTIVE);
		InflectedWordElement gracieuxInfl = new InflectedWordElement(gracieux);
		gracieuxInfl.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		gracieuxInfl.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		
		Assert.assertEquals("gracieuses", //$NON-NLS-1$
				this.realiser.realise(gracieuxInfl).getRealisation());

		WordElement le = lexicon.getWord("le", LexicalCategory.DETERMINER);
		InflectedWordElement leInfl = new InflectedWordElement(le);
		leInfl.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
//		leInfl.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		
		Assert.assertEquals("la", //$NON-NLS-1$
				this.realiser.realise(leInfl).getRealisation());

//		WordElement devenir = lexicon.getWord("devenir", LexicalCategory.VERB);
//		InflectedWordElement devenirInfl = new InflectedWordElement(devenir);
		VPPhraseSpec devenirInfl = factory.createVerbPhrase("devenir");
		devenirInfl.setFeature(Feature.PERSON, Person.SECOND);
		devenirInfl.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		devenirInfl.setFeature(Feature.TENSE, Tense.FUTURE);
		
		Assert.assertEquals("deviendrez", //$NON-NLS-1$
				this.realiser.realise(devenirInfl).getRealisation());
	}
	
	@Test
	public void testAdjPhrase() {
//		AdjPhraseSpec beau = factory.createAdjectivePhrase("beau");
//		WordElement le = lexicon.getWord("le", LexicalCategory.DETERMINER);
////		WordElement gentil = lexicon.getWord("gentil", LexicalCategory.ADJECTIVE);
////		beau.addModifier(gentil);
////		beau.addModifier(le);
//		beau.addModifier("plus");
//		
//		NPPhraseSpec femme = factory.createNounPhrase(le, "femme");
//		femme.addPostModifier(le);
//		femme.addPostModifier(beau);
//		
//		Assert.assertEquals("la femme la plus belle", //$NON-NLS-1$
//				this.realiser.realise(femme).getRealisation());
//		
//		// fails
//		femme.setPlural(true);
//		Assert.assertEquals("les femmes les plus belles", //$NON-NLS-1$
//				this.realiser.realise(femme).getRealisation());
		
		AdvPhraseSpec lentement = factory.createAdverbPhrase("lentement");
		lentement.addModifier("le");
		lentement.addModifier("plus");
		
		Assert.assertEquals("le plus lentement", //$NON-NLS-1$
				this.realiser.realise(lentement).getRealisation());
	}

	@Test
	public void testNounPhrase() {
		NPPhraseSpec snBelEndroit = factory.createNounPhrase("un", "endroit");
		snBelEndroit.addModifier("beau");
		Assert.assertEquals("un bel endroit", this.realiser //$NON-NLS-1$
				.realise(snBelEndroit).getRealisation());
		snBelEndroit.clearModifiers();
		snBelEndroit.addPostModifier("beau");
		Assert.assertEquals("un endroit beau", this.realiser //$NON-NLS-1$
				.realise(snBelEndroit).getRealisation());
		snBelEndroit.clearModifiers();
		snBelEndroit.addModifier("magnifique");
		Assert.assertEquals("un endroit magnifique", this.realiser //$NON-NLS-1$
				.realise(snBelEndroit).getRealisation());
		snBelEndroit.clearModifiers();
		snBelEndroit.addPreModifier("magnifique");
		Assert.assertEquals("un magnifique endroit", this.realiser //$NON-NLS-1$
				.realise(snBelEndroit).getRealisation());
		
		Assert.assertEquals("la maison", this.realiser //$NON-NLS-1$
				.realise(snMaison).getRealisation());

		NPPhraseSpec snSable = factory.createNounPhrase("du", "sable");		
		Assert.assertEquals("du sable", this.realiser //$NON-NLS-1$
				.realise(snSable).getRealisation());
		
		NPPhraseSpec snViande = factory.createNounPhrase("du", "viande");		
		Assert.assertEquals("de la viande", this.realiser //$NON-NLS-1$
				.realise(snViande).getRealisation());
		
		NPPhraseSpec snEau = factory.createNounPhrase("du", "eau");		
		Assert.assertEquals("de l'eau", this.realiser //$NON-NLS-1$
				.realise(snEau).getRealisation());
		
		WordElement assez = lexicon.getWord("assez", LexicalCategory.ADVERB);
		NPPhraseSpec snVin = factory.createNounPhrase(assez, "vin");		
		Assert.assertEquals("assez de vin", this.realiser //$NON-NLS-1$
				.realise(snVin).getRealisation());
		WordElement beaucoup = lexicon.getWord("beaucoup", LexicalCategory.ADVERB);
		NPPhraseSpec snEleves = factory.createNounPhrase(beaucoup, "élève");
		snEleves.setPlural(true);
		Assert.assertEquals("beaucoup d'élèves", this.realiser //$NON-NLS-1$
				.realise(snEleves).getRealisation());
		
		NPPhraseSpec snChemise = factory.createNounPhrase("ce -là", "chemise");
		snChemise.setPlural(true);
		Assert.assertEquals("ces chemises-là", this.realiser.realise( //$NON-NLS-1$
				snChemise).getRealisation());

		NPPhraseSpec snInstrument = factory.createNounPhrase("ce -ci", "instrument");
		snInstrument.setSpecifier("ce -ci");
		snInstrument.addModifier("chirurgical");
		Assert.assertEquals("cet instrument chirurgical-ci", this.realiser.realise( //$NON-NLS-1$
				snInstrument).getRealisation());

		AdjPhraseSpec sadjBeau = factory.createAdjectivePhrase("beau");
		AdvPhraseSpec sadvTres = factory.createAdverbPhrase("très");
		sadjBeau.addModifier(sadvTres);
		snMaison.addModifier(sadjBeau);
		snMaison.addModifier("spacieux");
		snMaison.setPlural(true);
		
		Assert.assertEquals("les très belles maisons spacieuses", this.realiser //$NON-NLS-1$
				.realise(snMaison).getRealisation());

		outln(snMaison);
	}

	public static final Realiser realiser = new Realiser();
	
	public static void outln(NLGElement outElement) {
		NLGElement realisedElement = realiser.realise(outElement);
		String realisation = realisedElement.getRealisation();
		System.out.println(realisation);
	}
	
	@Test
	public void testPrepositionPhrase() {
		Assert.assertEquals("la maison", this.realiser //$NON-NLS-1$
				.realise(snMaison).getRealisation());

		AdjPhraseSpec sadjBeau = factory.createAdjectivePhrase("beau");
		AdvPhraseSpec sadvTres = factory.createAdverbPhrase("très");
		sadjBeau.addModifier(sadvTres);
		snMaison.addModifier(sadjBeau);
		snMaison.addModifier("spacieux");
		snMaison.setPlural(true);
		
		PPPhraseSpec complementDuNomCategorie = factory.createPrepositionPhrase("de", "campagne");
		snMaison.addComplement(complementDuNomCategorie);
		PPPhraseSpec complementDuNomMatiere = factory.createPrepositionPhrase("en", "pierre");
		snMaison.addModifier(complementDuNomMatiere);
		
		Assert.assertEquals("les très belles maisons de campagne spacieuses en pierre", this.realiser //$NON-NLS-1$
				.realise(snMaison).getRealisation());

		NPPhraseSpec fruits = factory.createNounPhrase("fruit");
		fruits.setPlural(true);
		PPPhraseSpec complementDeLAdjectif = factory.createPrepositionPhrase("de", fruits);
		AdjPhraseSpec plein = factory.createAdjectivePhrase("plein");
		plein.addComplement(complementDeLAdjectif);
		NPPhraseSpec corbeille = factory.createNounPhrase("un", "corbeille");
		corbeille.addModifier(plein);
		
		Assert.assertEquals("une corbeille pleine de fruits", this.realiser //$NON-NLS-1$
				.realise(corbeille).getRealisation());
	}

	@Test
	public void testVerbPhrase() {
		VPPhraseSpec sv = factory.createVerbPhrase("tomber");
		sv.setFeature(Feature.TENSE, Tense.PAST);
		sv.setFeature(Feature.PROGRESSIVE, true);
		sv.addModifier("souvent");
		Assert.assertEquals("tombait souvent", //$NON-NLS-1$
				 realiser.realise(sv).getRealisation());

		sv = factory.createVerbPhrase("rire");
		sv.setFeature(Feature.PERSON, Person.SECOND);
		sv.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		Assert.assertEquals("riez", //$NON-NLS-1$
				 realiser.realise(sv).getRealisation());

		sv = factory.createVerbPhrase("aboyer");
		sv.setFeature(Feature.TENSE, Tense.FUTURE);
		sv.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		sv.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("n'aboieront pas", //$NON-NLS-1$
				 realiser.realise(sv).getRealisation());
		
		sv = factory.createVerbPhrase("vendre");
		sv.setObject(snMaison);
		sv.setFeature(Feature.FORM, Form.INFINITIVE);
		Assert.assertEquals("vendre la maison", //$NON-NLS-1$
				 realiser.realise(sv).getRealisation());
	}
	
	@Test
	public void testClause() {
		SPhraseSpec proposition = factory.createClause("on", "tomber");
		proposition.setFeature(Feature.TENSE, Tense.PAST);
		proposition.setFeature(Feature.PROGRESSIVE, true);
		proposition.addModifier("souvent");
		Assert.assertEquals("on tombait souvent", this.realiser //$NON-NLS-1$
				.realise(proposition).getRealisation());

		proposition = factory.createClause("vous", "rire");
		proposition.addModifier("beaucoup");
		Assert.assertEquals("vous riez beaucoup", this.realiser //$NON-NLS-1$
				.realise(proposition).getRealisation());

		proposition = factory.createClause("ils", "aboyer");
		proposition.setFeature(Feature.TENSE, Tense.FUTURE);
		proposition.addModifier("fort");
		Assert.assertEquals("ils aboieront fort", this.realiser //$NON-NLS-1$
				.realise(proposition).getRealisation());
		
		proposition = factory.createClause("elle", "vendre");
		proposition.addPreModifier("rapidement");
		proposition.setFeature(Feature.TENSE, Tense.PAST);
		proposition.setObject(snMaison);
		snMaison.setPlural(true);
		Assert.assertEquals("elle a rapidement vendu les maisons", this.realiser //$NON-NLS-1$
				.realise(proposition).getRealisation());
		
		proposition.setSubject("tu");
		snMaison.setFeature(Feature.PRONOMINAL, true);
		Assert.assertEquals("tu les as rapidement vendues", this.realiser //$NON-NLS-1$
				.realise(proposition).getRealisation());
		
		proposition.setFeature(Feature.FORM, Form.IMPERATIVE);
		proposition.setFeature(Feature.TENSE, Tense.PRESENT);
		proposition.setIndirectObject("ils");
		proposition.clearModifiers();
		proposition.addFrontModifier("demain");
		Assert.assertEquals("demain, vends-les-leur", this.realiser //$NON-NLS-1$
				.realise(proposition).getRealisation());
		
		proposition.setFeature(Feature.NEGATED, true);
		proposition.clearModifiers();
		PPPhraseSpec avantDemain = factory.createPrepositionPhrase("avant", "demain");
		proposition.addModifier(avantDemain);
		Assert.assertEquals("ne les leur vends pas avant demain", this.realiser //$NON-NLS-1$
				.realise(proposition).getRealisation());
		
		proposition = factory.createClause("elles", "aller");
		proposition.setFeature(Feature.TENSE, Tense.PAST);
		proposition.setFeature(Feature.PERFECT, true);
		NPPhraseSpec snLeParc = factory.createNounPhrase("le", "parc");
		PPPhraseSpec spAuParc = factory.createPrepositionPhrase("à", snLeParc);
		proposition.setComplement(spAuParc);
		Assert.assertEquals("elles étaient allées au parc", this.realiser //$NON-NLS-1$
				.realise(proposition).getRealisation());
		
		SPhraseSpec proposition2 = factory.createClause("vous", "dire", proposition);
		Assert.assertEquals("vous dites qu'elles étaient allées au parc", this.realiser //$NON-NLS-1$
				.realise(proposition2).getRealisation());
		
		proposition.setFeature(Feature.TENSE, Tense.CONDITIONAL);
		
		SPhraseSpec proposition3 = factory.createClause("il", "être", "riche");
		proposition3.setFeature(Feature.TENSE, Tense.CONDITIONAL);
		proposition3.setFeature(Feature.COMPLEMENTISER, "si");
		snMaison = factory.createNounPhrase("un", "maison");
		SPhraseSpec proposition4 = factory.createClause("il", "acheter", snMaison);
		proposition4.setFeature(Feature.TENSE, Tense.CONDITIONAL);
		proposition4.addFrontModifier(proposition3);
		Assert.assertEquals("s'il était riche, il achèterait une maison", this.realiser //$NON-NLS-1$
				.realise(proposition4).getRealisation());
		
		SPhraseSpec proposition5 = factory.createClause("personne", "marcher");
		proposition5.setFeature(Feature.MODAL, "pouvoir");
		Assert.assertEquals("personne ne peut marcher", this.realiser //$NON-NLS-1$
				.realise(proposition5).getRealisation());
		proposition5.setSubject("moi");
		proposition5.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("je ne peux pas marcher", this.realiser //$NON-NLS-1$
				.realise(proposition5).getRealisation());
		proposition5.setFeature(FrenchFeature.NEGATION_AUXILIARY, "plus");
		Assert.assertEquals("je ne peux plus marcher", this.realiser //$NON-NLS-1$
				.realise(proposition5).getRealisation());
	}

	@Test
	public void testRelativeClause() {
		NPPhraseSpec snAgent = factory.createNounPhrase("le", "agent");
		snAgent.addModifier("immobilier");
		NPPhraseSpec snMaison = factory.createNounPhrase("le", "maison");
		snMaison.setPlural(true);
		NPPhraseSpec snCliente = factory.createNounPhrase("le", "cliente");
		NPPhraseSpec snBureau = factory.createNounPhrase("son", "bureau");
		PPPhraseSpec spDansBureau = factory.createPrepositionPhrase("dans", snBureau);
		SPhraseSpec proposition6 = factory.createClause(snAgent, "vendre", snMaison);
		proposition6.setIndirectObject(snCliente);
		proposition6.addComplement(spDansBureau);
		proposition6.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals("l'agent immobilier a vendu les maisons à la cliente dans son bureau",
				realise(proposition6));
		
		proposition6.setFeature(FrenchFeature.RELATIVE_PHRASE, snAgent);
		Assert.assertEquals("qui a vendu les maisons à la cliente dans son bureau",
				realise(proposition6));
		
		proposition6.setFeature(FrenchFeature.RELATIVE_PHRASE, snMaison);
		Assert.assertEquals("que l'agent immobilier a vendu à la cliente dans son bureau",
				realise(proposition6));
		
		proposition6.setFeature(FrenchFeature.RELATIVE_PHRASE, snCliente);
		Assert.assertEquals("auquel l'agent immobilier a vendu les maisons dans son bureau",
				realise(proposition6));
		
		proposition6.setFeature(FrenchFeature.RELATIVE_PHRASE, spDansBureau);
		Assert.assertEquals("dans lequel l'agent immobilier a vendu les maisons à la cliente",
				realise(proposition6));
		
		// Dummy elements.
		NPPhraseSpec snFactice = factory.createNounPhrase();
		snFactice.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.SUBJECT);
		proposition6.setFeature(FrenchFeature.RELATIVE_PHRASE, snFactice);
		Assert.assertEquals("qui a vendu les maisons à la cliente dans son bureau",
				realise(proposition6));
		
		PPPhraseSpec spFactice = factory.createPrepositionPhrase("lors de");
		proposition6.setFeature(FrenchFeature.RELATIVE_PHRASE, spFactice);
		Assert.assertEquals("lors duquel l'agent immobilier a vendu les maisons à la cliente dans son bureau",
				realise(proposition6));
		
		// With the relative clause as a complement to a main noun phrase.
		NPPhraseSpec snPrincipal = factory.createNounPhrase("le", "personne");
		snPrincipal.addModifier(proposition6);
		proposition6.setFeature(FrenchFeature.RELATIVE_PHRASE, snAgent);
		Assert.assertEquals("la personne qui a vendu les maisons à la cliente dans son bureau",
				realise(snPrincipal));
		
		snPrincipal.setNoun("propriété");
		proposition6.setFeature(FrenchFeature.RELATIVE_PHRASE, snMaison);
		Assert.assertEquals("la propriété que l'agent immobilier a vendue à la cliente dans son bureau",
				realise(snPrincipal));
		
		snPrincipal.setNoun("personne");
		proposition6.setFeature(FrenchFeature.RELATIVE_PHRASE, snCliente);
		Assert.assertEquals("la personne à laquelle l'agent immobilier a vendu les maisons dans son bureau",
				realise(snPrincipal));
		snPrincipal.setSpecifier(null);
		snPrincipal.setPronoun("vous");
		Assert.assertEquals("vous à qui l'agent immobilier a vendu les maisons dans son bureau",
				realise(snPrincipal));
		WordElement JeanPierre = lexicon.lookupWord("Jean-Pierre", LexicalCategory.NOUN);
		JeanPierre.setFeature(LexicalFeature.PROPER, true);
		snPrincipal.setNoun(JeanPierre);
		Assert.assertEquals("Jean-Pierre à qui l'agent immobilier a vendu les maisons dans son bureau",
				realise(snPrincipal));
		
		snPrincipal.setSpecifier("le");
		snPrincipal.setNoun("pièce");
		proposition6.setFeature(FrenchFeature.RELATIVE_PHRASE, spDansBureau);
		Assert.assertEquals("la pièce dans laquelle l'agent immobilier a vendu les maisons à la cliente",
				realise(snPrincipal));
		
		NPPhraseSpec snMinistre = factory.createNounPhrase("le", "ministre");
		PPPhraseSpec spDuMinistre = factory.createPrepositionPhrase("de", snMinistre);
		snMaison.addComplement(spDuMinistre);
		SPhraseSpec proposition7 = factory.createClause(null, "vendre", snMaison);
		proposition7.setFeature(Feature.PASSIVE, true);
		proposition7.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals("les maisons du ministre ont été vendues", realise(proposition7));
		
		proposition7.setFeature(FrenchFeature.RELATIVE_PHRASE, spDuMinistre);
		Assert.assertEquals("dont les maisons ont été vendues", realise(proposition7));
		
		snPrincipal = factory.createNounPhrase("le", "homme");
		snPrincipal.addModifier("politique");
		snPrincipal.addModifier("célèbre");
		snPrincipal.addModifier(proposition7);
		Assert.assertEquals("l'homme politique célèbre dont les maisons ont été vendues", realise(snPrincipal));
	}

	@Test
	public void testInterrogative() {
		NPPhraseSpec snAgent = factory.createNounPhrase("le", "agent");
		snAgent.addModifier("immobilier");
		NPPhraseSpec snMaison = factory.createNounPhrase("un", "maison");
		NPPhraseSpec snCliente = factory.createNounPhrase("le", "cliente");
		SPhraseSpec proposition9 = factory.createClause(snAgent, "vendre", snMaison);
		proposition9.setIndirectObject(snCliente);
		proposition9.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals("L'agent immobilier a vendu une maison à la cliente.",
				realiser.realiseSentence(proposition9));
		
		proposition9.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_INDIRECT_OBJECT);
		Assert.assertEquals("À qui est-ce que l'agent immobilier a vendu une maison?",
				realiser.realiseSentence(proposition9));
		
		proposition9.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("Qu'est-ce que l'agent immobilier a vendu à la cliente?",
				realiser.realiseSentence(proposition9));
		
		proposition9.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW);
		Assert.assertEquals("Comment est-ce que l'agent immobilier a vendu une maison à la cliente?",
				realiser.realiseSentence(proposition9));
		
		proposition9.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHERE);
		Assert.assertEquals("Où est-ce que l'agent immobilier a vendu une maison à la cliente?",
				realiser.realiseSentence(proposition9));
		
		proposition9.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHY);
		Assert.assertEquals("Pourquoi est-ce que l'agent immobilier a vendu une maison à la cliente?",
				realiser.realiseSentence(proposition9));
		
		proposition9.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW_MANY);
		Assert.assertEquals("Combien d'agents immobiliers ont vendu une maison à la cliente?",
				realiser.realiseSentence(proposition9));

		proposition9.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("Combien de maisons ont été vendues à la cliente par l'agent immobilier?",
				realiser.realiseSentence(proposition9));
		
		proposition9.setFeature(Feature.PASSIVE, false);
		proposition9.setFeature(Feature.TENSE, Tense.CONDITIONAL);
		proposition9.setFeature(Feature.PERFECT, true);
		proposition9.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		Assert.assertEquals("Est-ce que l'agent immobilier aurait vendu une maison à la cliente?",
				realiser.realiseSentence(proposition9));
		
		SPhraseSpec propositionPrincipale = factory.createClause("vous", "dire", proposition9);
		propositionPrincipale.setIndirectObject("nous");
		propositionPrincipale.setFeature(Feature.FORM, Form.IMPERATIVE);
		Assert.assertEquals("Dites-nous si l'agent immobilier aurait vendu une maison à la cliente.",
				realiser.realiseSentence(propositionPrincipale));

		proposition9.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_INDIRECT_OBJECT);
		Assert.assertEquals("À qui est-ce que l'agent immobilier aurait vendu une maison?",
				realiser.realiseSentence(proposition9));
		
		Assert.assertEquals("Dites-nous à qui est-ce que l'agent immobilier aurait vendu une maison.",
				realiser.realiseSentence(propositionPrincipale));
	}
	
	@Test
	public void testCoordination() {
		SPhraseSpec proposition = factory.createClause();
		proposition.setVerb("devenir");
		NPPhraseSpec snMarie = factory.createNounPhrase("Marie");
		snMarie.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		NPPhraseSpec snJulie = factory.createNounPhrase("Julie");
		snJulie.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		CoordinatedPhraseElement coord = factory.createCoordinatedPhrase();
		coord.addCoordinate(snMarie);
		coord.addCoordinate(snJulie);
		proposition.setSubject(coord);
		proposition.setObject("gentil");
		proposition.setFeature(Feature.TENSE, Tense.FUTURE);
		proposition.setFeature(Feature.PERFECT, true);
		Assert.assertEquals("Marie et Julie seront devenues gentilles", this.realiser //$NON-NLS-1$
				.realise(proposition).getRealisation());

		coord.setConjunction("ou");
		Assert.assertEquals("Marie ou Julie sera devenue gentille", this.realiser //$NON-NLS-1$
				.realise(proposition).getRealisation());
	}

	@Test
	public void testCannedText() {
		StringElement peche = factory.createStringElement("à la pêche au requin");
		StringElement pierre = factory.createStringElement("Pierre part à la chasse");
		
		Map<String,Object> features = new HashMap<String,Object>();
		features.put(Feature.PERSON, Person.FIRST);
		features.put(Feature.NUMBER, NumberAgreement.SINGULAR);
		WordElement pronom = lexicon.getWord(LexicalCategory.PRONOUN, features);
		
		SPhraseSpec proposition2 = factory.createClause(pronom, "aller", "se");
		proposition2.addComplement("en");
		proposition2.addComplement(peche);
		proposition2.addPreModifier("ensuite");
		
		CoordinatedPhraseElement coord = factory.createCoordinatedPhrase();
		coord.addCoordinate(pierre);
		coord.addCoordinate(proposition2);
		Assert.assertEquals("Pierre part à la chasse et je m'en vais ensuite à la pêche au requin", //$NON-NLS-1$
				this.realiser.realise(coord).getRealisation());
	}

	@Test
	public void testAggregation() {
		WordElement Marie = lexicon.getWord("Marie", LexicalCategory.NOUN);
		Marie.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		Marie.setFeature(LexicalFeature.PROPER, true);
		WordElement Julie = lexicon.getWord("Julie", LexicalCategory.NOUN);
		Julie.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		Julie.setFeature(LexicalFeature.PROPER, true);
		WordElement Martin = lexicon.getWord("Martin", LexicalCategory.NOUN);
		Martin.setFeature(LexicalFeature.PROPER, true);
		Martin.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
		NLGElement proposition1 = factory.createClause(Marie, "devenir", "gentil");
		NLGElement proposition2 = factory.createClause(Julie, "devenir", "gentil");
		NLGElement proposition3 = factory.createClause(Martin, "devenir", "gentil");
		proposition1.setFeature(Feature.TENSE, Tense.PAST);
		proposition2.setFeature(Feature.TENSE, Tense.PAST);
		proposition3.setFeature(Feature.TENSE, Tense.PAST);
		
		List<NLGElement> elements = Arrays.asList(proposition1,	proposition2, proposition3);
		ClauseCoordinationRule clauseCoord = new ClauseCoordinationRule();
		List<NLGElement> result = clauseCoord.apply(elements);
		Assert.assertTrue(result.size() == 1); // should only be one sentence
		NLGElement aggregated = result.get(0);
		Assert.assertEquals("Marie, Julie et Martin sont devenus gentils", //$NON-NLS-1$
				this.realiser.realise(aggregated).getRealisation());
		
//		Aggregator aggregator = new Aggregator();
//		aggregator.initialise();
//		aggregator.setFactory(factory);
//		aggregator.addRule(new ClauseCoordinationRule());
//		aggregated = aggregator.realise(elements);
	}

	@Test
	public void testFormattedText() {
		NPPhraseSpec snLeChien = factory.createNounPhrase("le", "chien");
		NPPhraseSpec snUnOs = factory.createNounPhrase("un", "os");
		NPPhraseSpec snLeCiel = factory.createNounPhrase("le", "ciel");
		NPPhraseSpec snDesNuages = factory.createNounPhrase("un", "nuage");
		snDesNuages.setPlural(true);
		NPPhraseSpec snMartin = factory.createNounPhrase("Martin");
		NPPhraseSpec snMartinPronom = factory.createNounPhrase(snMartin);
		snMartinPronom.setFeature(Feature.PRONOMINAL, true);
		SPhraseSpec proposition1 = factory.createClause(snLeChien, "ronger", snUnOs);
		SPhraseSpec proposition2 = factory.createClause(snMartin, "appeler", snLeChien);
		SPhraseSpec proposition4 = factory.createClause(snMartin, "regarder", snLeCiel);
		SPhraseSpec proposition5 = factory.createClause(snMartinPronom, "voir", snDesNuages);
		proposition5.addComplement("y");
		CoordinatedPhraseElement coord = factory.createCoordinatedPhrase(proposition4, proposition5);
		
		DocumentElement phrase1 = factory.createSentence(proposition1);
		DocumentElement phrase3 = factory.createSentence(snLeChien, "regarder", snMartinPronom);
		DocumentElement phrase4 = factory
			.createSentence("le chien semble considérer longuement la question");
		DocumentElement phrase5 = factory.createSentence(coord);
		
		DocumentElement paragraphe1 = factory.createParagraph(phrase1);
		DocumentElement paragraphe2 = factory.createParagraph();
		paragraphe2.addComponent(proposition2);
		paragraphe2.addComponent(phrase3);
		paragraphe2.addComponent(phrase4);
		DocumentElement paragraphe3 = factory.createParagraph(phrase5);
		
		List<DocumentElement> listePara = Arrays.asList(paragraphe1,paragraphe2);
		DocumentElement section1 = factory.createSection("\nTitre de la section 1\n", listePara);
		DocumentElement section2 = factory.createSection("\nTitre de la section 2\n", paragraphe3);
		
		DocumentElement document = factory.createDocument("\nTitre du document\n", section1);
		document.addComponent(section2);
		
		String realised = realiser.realise(document).getRealisation();
		System.out.print(realised);
		
		Assert.assertEquals("\nTitre du document\n\n\nTitre de la section 1\n\n"
				+ "Le chien ronge un os.\n\n"
				+ "Martin appelle le chien. Le chien le regarde. "
				+ "Le chien semble considérer longuement la question.\n\n"
				+ "\nTitre de la section 2\n\n"
				+ "Martin regarde le ciel et il y voit des nuages.\n\n"
				, realised);
	}
	
	@Test
	public void testBilingual() {
		Lexicon frenchLexicon = new simplenlg.lexicon.french.XMLLexicon();
		Lexicon englishLexicon = new simplenlg.lexicon.english.XMLLexicon();
		NLGFactory frenchFactory = new NLGFactory(frenchLexicon);
		NLGFactory englishFactory = new NLGFactory(englishLexicon);
		Realiser realiser = new Realiser();
		
		WordElement detLe = frenchLexicon.lookupWord("le", LexicalCategory.DETERMINER);
		NPPhraseSpec snLeChien = frenchFactory.createNounPhrase(detLe, "chien");
		NPPhraseSpec snUnOs = frenchFactory.createNounPhrase("un", "os");
		snLeChien.setPlural(true);
		SPhraseSpec proposition1 = frenchFactory.createClause(snLeChien, "ronger", snUnOs);

		NPPhraseSpec npTheDog = englishFactory.createNounPhrase("the", "dog");
		npTheDog.setPlural(true);
		WordElement detA = englishLexicon.lookupWord("a", LexicalCategory.DETERMINER);
		NPPhraseSpec npABone = englishFactory.createNounPhrase(detA, "bone");
		PPPhraseSpec ppAtABone = englishFactory.createPrepositionPhrase("at", npABone);
		SPhraseSpec clause2 = frenchFactory.createClause(npTheDog, "gnaw", ppAtABone);

		NPPhraseSpec npLeDog = englishFactory.createNounPhrase(detLe, "dog");
		npLeDog.setPlural(true);
		NPPhraseSpec snAOs = frenchFactory.createNounPhrase(detA, "os");
		SPhraseSpec proposition3 = frenchFactory.createClause(npLeDog, "ronger", snAOs);
		
		DocumentElement phrase1 = frenchFactory.createSentence(proposition1);
		
		DocumentElement paragraphe1 = englishFactory.createParagraph(phrase1);
		paragraphe1.addComponent(clause2);
		DocumentElement paragraphe2 = frenchFactory.createParagraph(proposition3);
		
		List<DocumentElement> listePara = Arrays.asList(paragraphe1,paragraphe2);
		DocumentElement section1 = frenchFactory.createSection("\nTitre de la section 1\n", listePara);
		
		DocumentElement document = englishFactory.createDocument("\nTitle of the document\n", section1);
		
		String realised = realiser.realise(document).getRealisation();
		System.out.print(realised);
		
		Assert.assertEquals("\nTitle of the document\n\n\nTitre de la section 1\n\n"
				+ "Les chiens rongent un os. "
				+ "The dogs gnaw at a bone.\n\n"
				+ "Les dogs rongent an os.\n\n"
				, realised);
	}

	@Test
	public void testBilingual2() {
		Lexicon frenchLexicon = new simplenlg.lexicon.french.XMLLexicon();
		Lexicon englishLexicon = new simplenlg.lexicon.english.XMLLexicon();
		NLGFactory frenchFactory = new NLGFactory(frenchLexicon);
		NLGFactory englishFactory = new NLGFactory(englishLexicon);
		Realiser realiser = new Realiser();
		
		WordElement detLe = frenchLexicon.lookupWord("le", LexicalCategory.DETERMINER);
		WordElement detA = englishLexicon.lookupWord("a", LexicalCategory.DETERMINER);
	
		NPPhraseSpec npLeDog = englishFactory.createNounPhrase(detLe, "man");
		npLeDog.setPlural(true);
		NPPhraseSpec snAOs = frenchFactory.createNounPhrase(detA, "auto");
		SPhraseSpec proposition = frenchFactory.createClause(npLeDog, "conduire", snAOs);
		
		String realised = realiser.realiseSentence(proposition);
		System.out.println("début:" + realised + ":fin");
		
		Assert.assertEquals("Les men conduisent an auto.", realised);
	}
	
	@Test
	public void testVerbForms() {
		factory = new NLGFactory(new simplenlg.lexicon.english.XMLLexicon());
		SPhraseSpec manger = factory.createClause("he", "eat");
//		SPhraseSpec manger = factory.createClause("il", "manger");
//		manger.setFeature(Feature.FORM, Form.SUBJUNCTIVE);
		for (Tense tense : Tense.values()) {
			for (Boolean progressive : Arrays.asList(Boolean.FALSE, Boolean.TRUE)) {
				for (Boolean perfect : Arrays.asList(Boolean.FALSE, Boolean.TRUE)) {
				manger.setFeature(Feature.TENSE, tense);
				manger.setFeature(Feature.PROGRESSIVE, progressive);
				manger.setFeature(Feature.PERFECT, perfect);
				outln(manger);
				}
			}
		}
	}
}
