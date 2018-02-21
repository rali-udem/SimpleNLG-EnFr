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

import org.junit.Test;

import junit.framework.Assert;
import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.framework.PhraseElement;

/**
 * These are tests for verb conjugation.
 * @author vaudrypl
 */
public class ConjugationTest extends SimpleNLG4TestBase {

	SPhraseSpec s;
	
	/**
	 * Instantiates a new conjugation test.
	 * 
	 * @param name
	 *            the name
	 */
	public ConjugationTest(String name) {
		super(name);
	}

	/**
	 * Test indicative present on verbs of each conjugation group.
	 */
	@Test
	public void testIndicPresent() {
		// verbs of the first group
		s = factory.createClause("je","marcher");
		s.setFeature(Feature.TENSE, Tense.PRESENT);
		Assert.assertEquals( "je marche", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu marches", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle marche", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous marchons", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous marchez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles marchent", realise(s) );
		
		s.setVerb("noyer");
		s.setObject("se");
		s.setSubject("je");
		Assert.assertEquals( "je me noie", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu te noies", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle se noie", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous nous noyons", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous vous noyez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles se noient", realise(s) );
		// remove reflexive pronoun
		((PhraseElement) (s.getVerbPhrase()))
			.removeComplements(DiscourseFunction.OBJECT);
		
		// verbs of the second group
		s.setVerb("finir");
		s.setSubject("je");
		Assert.assertEquals( "je finis", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu finis", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle finit", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous finissons", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous finissez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles finissent", realise(s) );
		
		// verbs of the third group
		s.setVerb("prendre");
		s.setSubject("je");
		Assert.assertEquals( "je prends", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu prends", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle prend", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous prenons", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous prenez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles prennent", realise(s) );		

		s.setVerb("voir");
		s.setSubject("je");
		Assert.assertEquals( "je vois", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu vois", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle voit", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous voyons", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous voyez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles voient", realise(s) );		
	}
	
	/**
	 * Test indicative "imparfait" on verbs of each conjugation group.
	 */
	@Test
	public void testIndicImparfait() {
		// verbs of the first group
		s = factory.createClause("je","marcher");
		s.setFeature(Feature.TENSE, Tense.PAST);
		s.setFeature(Feature.PROGRESSIVE, true);
		Assert.assertEquals( "je marchais", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu marchais", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle marchait", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous marchions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous marchiez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles marchaient", realise(s) );
		
		s.setVerb("noyer");
		s.setObject("se");
		s.setSubject("je");
		Assert.assertEquals( "je me noyais", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu te noyais", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle se noyait", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous nous noyions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous vous noyiez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles se noyaient", realise(s) );
		// remove reflexive pronoun
		((PhraseElement) (s.getVerbPhrase()))
			.removeComplements(DiscourseFunction.OBJECT);
		
		// verbs of the second group
		s.setVerb("finir");
		s.setSubject("je");
		Assert.assertEquals( "je finissais", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu finissais", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle finissait", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous finissions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous finissiez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles finissaient", realise(s) );
		
		// verbs of the third group
		s.setVerb("prendre");
		s.setSubject("je");
		Assert.assertEquals( "je prenais", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu prenais", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle prenait", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous prenions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous preniez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles prenaient", realise(s) );		

		s.setVerb("voir");
		s.setSubject("je");
		Assert.assertEquals( "je voyais", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu voyais", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle voyait", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous voyions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous voyiez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles voyaient", realise(s) );		
	}
	
	/**
	 * Test indicative "passé composé" on verbs of each conjugation group.
	 */
	@Test
	public void testPasseCompose() {
		// verbs of the first group
		s = factory.createClause("je","marcher");
		s.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals( "j'ai marché", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu as marché", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle a marché", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous avons marché", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous avez marché", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles ont marché", realise(s) );
		
		s.setVerb("noyer");
		s.setObject("se");
		s.setSubject("je");
		Assert.assertEquals( "je me suis noyé", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu t'es noyé", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle s'est noyée", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous nous sommes noyés", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous vous êtes noyés", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles se sont noyées", realise(s) );
		// remove reflexive pronoun
		((PhraseElement) (s.getVerbPhrase()))
			.removeComplements(DiscourseFunction.OBJECT);
		
		// verbs of the second group
		s.setVerb("finir");
		s.setSubject("je");
		Assert.assertEquals( "j'ai fini", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu as fini", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle a fini", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous avons fini", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous avez fini", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles ont fini", realise(s) );
		
		// verbs of the third group
		s.setVerb("prendre");
		s.setSubject("je");
		Assert.assertEquals( "j'ai pris", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu as pris", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle a pris", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous avons pris", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous avez pris", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles ont pris", realise(s) );		

		s.setVerb("voir");
		s.setSubject("je");
		Assert.assertEquals( "j'ai vu", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu as vu", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle a vu", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous avons vu", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous avez vu", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles ont vu", realise(s) );		
	}

	/**
	 * Test indicative "plus-que-parfait" on verbs of each conjugation group.
	 */
	@Test
	public void testIndicPlusQueParfait() {
		// verbs of the first group
		s = factory.createClause("je","marcher");
		s.setFeature(Feature.TENSE, Tense.PAST);
		s.setFeature(Feature.PERFECT, true);
		Assert.assertEquals( "j'avais marché", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu avais marché", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle avait marché", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous avions marché", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous aviez marché", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles avaient marché", realise(s) );
		
		s.setVerb("noyer");
		s.setObject("se");
		s.setSubject("je");
		Assert.assertEquals( "je m'étais noyé", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu t'étais noyé", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle s'était noyée", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous nous étions noyés", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous vous étiez noyés", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles s'étaient noyées", realise(s) );
		// remove reflexive pronoun
		((PhraseElement) (s.getVerbPhrase()))
			.removeComplements(DiscourseFunction.OBJECT);
		
		// verbs of the second group
		s.setVerb("finir");
		s.setSubject("je");
		Assert.assertEquals( "j'avais fini", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu avais fini", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle avait fini", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous avions fini", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous aviez fini", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles avaient fini", realise(s) );
		
		// verbs of the third group
		s.setVerb("prendre");
		s.setSubject("je");
		Assert.assertEquals( "j'avais pris", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu avais pris", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle avait pris", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous avions pris", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous aviez pris", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles avaient pris", realise(s) );		

		s.setVerb("voir");
		s.setSubject("je");
		Assert.assertEquals( "j'avais vu", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu avais vu", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle avait vu", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous avions vu", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous aviez vu", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles avaient vu", realise(s) );		
	}

	/**
	 * Test simple future on verbs of each conjugation group.
	 */
	@Test
	public void testSimpleFuture() {
		// verbs of the first group
		s = factory.createClause("je","marcher");
		s.setFeature(Feature.TENSE, Tense.FUTURE);
		Assert.assertEquals( "je marcherai", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu marcheras", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle marchera", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous marcherons", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous marcherez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles marcheront", realise(s) );
		
		s.setVerb("noyer");
		s.setObject("se");
		s.setSubject("je");
		Assert.assertEquals( "je me noierai", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu te noieras", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle se noiera", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous nous noierons", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous vous noierez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles se noieront", realise(s) );
		// remove reflexive pronoun
		((PhraseElement) (s.getVerbPhrase()))
			.removeComplements(DiscourseFunction.OBJECT);
		
		// verbs of the second group
		s.setVerb("finir");
		s.setSubject("je");
		Assert.assertEquals( "je finirai", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu finiras", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle finira", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous finirons", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous finirez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles finiront", realise(s) );
		
		// verbs of the third group
		s.setVerb("prendre");
		s.setSubject("je");
		Assert.assertEquals( "je prendrai", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu prendras", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle prendra", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous prendrons", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous prendrez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles prendront", realise(s) );		

		s.setVerb("voir");
		s.setSubject("je");
		Assert.assertEquals( "je verrai", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu verras", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle verra", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous verrons", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous verrez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles verront", realise(s) );		
	}
	
	/**
	 * Test future anterior on verbs of each conjugation group.
	 */
	@Test
	public void testFutureAnterior() {
		// verbs of the first group
		s = factory.createClause("je","marcher");
		s.setFeature(Feature.TENSE, Tense.FUTURE);
		s.setFeature(Feature.PERFECT, true);
		Assert.assertEquals( "j'aurai marché", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu auras marché", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle aura marché", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous aurons marché", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous aurez marché", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles auront marché", realise(s) );
		
		s.setVerb("noyer");
		s.setObject("se");
		s.setSubject("je");
		Assert.assertEquals( "je me serai noyé", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu te seras noyé", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle se sera noyée", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous nous serons noyés", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous vous serez noyés", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles se seront noyées", realise(s) );
		// remove reflexive pronoun
		((PhraseElement) (s.getVerbPhrase()))
			.removeComplements(DiscourseFunction.OBJECT);
		
		// verbs of the second group
		s.setVerb("finir");
		s.setSubject("je");
		Assert.assertEquals( "j'aurai fini", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu auras fini", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle aura fini", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous aurons fini", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous aurez fini", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles auront fini", realise(s) );
		
		// verbs of the third group
		s.setVerb("prendre");
		s.setSubject("je");
		Assert.assertEquals( "j'aurai pris", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu auras pris", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle aura pris", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous aurons pris", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous aurez pris", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles auront pris", realise(s) );		
	
		s.setVerb("voir");
		s.setSubject("je");
		Assert.assertEquals( "j'aurai vu", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu auras vu", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle aura vu", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous aurons vu", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous aurez vu", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles auront vu", realise(s) );		
	}

	/**
	 * Test conditional present on verbs of each conjugation group.
	 */
	@Test
	public void testConditionalPresent() {
		// verbs of the first group
		s = factory.createClause("je","marcher");
		s.setFeature(Feature.TENSE, Tense.CONDITIONAL);
		Assert.assertEquals( "je marcherais", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu marcherais", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle marcherait", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous marcherions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous marcheriez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles marcheraient", realise(s) );
		
		s.setVerb("noyer");
		s.setObject("se");
		s.setSubject("je");
		Assert.assertEquals( "je me noierais", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu te noierais", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle se noierait", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous nous noierions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous vous noieriez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles se noieraient", realise(s) );
		// remove reflexive pronoun
		((PhraseElement) (s.getVerbPhrase()))
			.removeComplements(DiscourseFunction.OBJECT);
		
		// verbs of the second group
		s.setVerb("finir");
		s.setSubject("je");
		Assert.assertEquals( "je finirais", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu finirais", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle finirait", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous finirions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous finiriez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles finiraient", realise(s) );
		
		// verbs of the third group
		s.setVerb("prendre");
		s.setSubject("je");
		Assert.assertEquals( "je prendrais", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu prendrais", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle prendrait", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous prendrions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous prendriez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles prendraient", realise(s) );		

		s.setVerb("voir");
		s.setSubject("je");
		Assert.assertEquals( "je verrais", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu verrais", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle verrait", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous verrions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous verriez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles verraient", realise(s) );		
	}
	
	/**
	 * Test conditional past on verbs of each conjugation group.
	 */
	@Test
	public void testConditionalPast() {
		// verbs of the first group
		s = factory.createClause("je","marcher");
		s.setFeature(Feature.TENSE, Tense.CONDITIONAL);
		s.setFeature(Feature.PERFECT, true);
		Assert.assertEquals( "j'aurais marché", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu aurais marché", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle aurait marché", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous aurions marché", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous auriez marché", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles auraient marché", realise(s) );
		
		s.setVerb("noyer");
		s.setObject("se");
		s.setSubject("je");
		Assert.assertEquals( "je me serais noyé", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu te serais noyé", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle se serait noyée", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous nous serions noyés", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous vous seriez noyés", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles se seraient noyées", realise(s) );
		// remove reflexive pronoun
		((PhraseElement) (s.getVerbPhrase()))
			.removeComplements(DiscourseFunction.OBJECT);
		
		// verbs of the second group
		s.setVerb("finir");
		s.setSubject("je");
		Assert.assertEquals( "j'aurais fini", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu aurais fini", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle aurait fini", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous aurions fini", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous auriez fini", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles auraient fini", realise(s) );
		
		// verbs of the third group
		s.setVerb("prendre");
		s.setSubject("je");
		Assert.assertEquals( "j'aurais pris", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu aurais pris", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle aurait pris", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous aurions pris", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous auriez pris", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles auraient pris", realise(s) );		
	
		s.setVerb("voir");
		s.setSubject("je");
		Assert.assertEquals( "j'aurais vu", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "tu aurais vu", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "elle aurait vu", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "nous aurions vu", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "vous auriez vu", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "elles auraient vu", realise(s) );		
	}

	/**
	 * Test subjunctive present on verbs of each conjugation group.
	 */
	@Test
	public void testSubjPresent() {
		// verbs of the first group
		s = factory.createClause("je","marcher");
		s.setFeature(Feature.TENSE, Tense.PRESENT);
		s.setFeature(Feature.FORM, Form.SUBJUNCTIVE);
		Assert.assertEquals( "que je marche", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu marches", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle marche", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous marchions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous marchiez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles marchent", realise(s) );
		
		s.setVerb("noyer");
		s.setObject("se");
		s.setSubject("je");
		Assert.assertEquals( "que je me noie", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu te noies", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle se noie", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous nous noyions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous vous noyiez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles se noient", realise(s) );
		// remove reflexive pronoun
		((PhraseElement) (s.getVerbPhrase()))
			.removeComplements(DiscourseFunction.OBJECT);
		
		// verbs of the second group
		s.setVerb("finir");
		s.setSubject("je");
		Assert.assertEquals( "que je finisse", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu finisses", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle finisse", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous finissions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous finissiez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles finissent", realise(s) );
		
		// verbs of the third group
		s.setVerb("prendre");
		s.setSubject("je");
		Assert.assertEquals( "que je prenne", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu prennes", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle prenne", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous prenions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous preniez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles prennent", realise(s) );

		s.setVerb("voir");
		s.setSubject("je");
		Assert.assertEquals( "que je voie", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu voies", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle voie", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous voyions", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous voyiez", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles voient", realise(s) );
		
		// Test that subjective "future" and "conditional"
		// are realised as present.
		s.setSubject("je");
		s.setFeature(Feature.TENSE, Tense.FUTURE);
		Assert.assertEquals( "que je voie", realise(s) );
		s.setFeature(Feature.TENSE, Tense.CONDITIONAL);
		Assert.assertEquals( "que je voie", realise(s) );
	}

	/**
	 * Test subjunctive past on verbs of each conjugation group.
	 */
	@Test
	public void testSubjPast() {
		// verbs of the first group
		s = factory.createClause("je","marcher");
		s.setFeature(Feature.TENSE, Tense.PAST);
		s.setFeature(Feature.FORM, Form.SUBJUNCTIVE);
		Assert.assertEquals( "que j'aie marché", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu aies marché", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle ait marché", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous ayons marché", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous ayez marché", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles aient marché", realise(s) );
		
		s.setVerb("noyer");
		s.setObject("se");
		s.setSubject("je");
		Assert.assertEquals( "que je me sois noyé", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu te sois noyé", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle se soit noyée", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous nous soyons noyés", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous vous soyez noyés", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles se soient noyées", realise(s) );
		// remove reflexive pronoun
		((PhraseElement) (s.getVerbPhrase()))
			.removeComplements(DiscourseFunction.OBJECT);
		
		// verbs of the second group
		s.setVerb("finir");
		s.setSubject("je");
		Assert.assertEquals( "que j'aie fini", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu aies fini", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle ait fini", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous ayons fini", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous ayez fini", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles aient fini", realise(s) );
		
		// verbs of the third group
		s.setVerb("prendre");
		s.setSubject("je");
		Assert.assertEquals( "que j'aie pris", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu aies pris", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle ait pris", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous ayons pris", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous ayez pris", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles aient pris", realise(s) );		

		s.setVerb("voir");
		s.setSubject("je");
		Assert.assertEquals( "que j'aie vu", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu aies vu", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle ait vu", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous ayons vu", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous ayez vu", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles aient vu", realise(s) );
		
		// Check that "present perfect is realised" as past.
		s.setFeature(Feature.TENSE, Tense.PRESENT);
		s.setFeature(Feature.PERFECT, true);
		Assert.assertEquals( "qu'elles aient vu", realise(s) );
	}

	/**
	 * Test subjunctive past "surcomposé" on verbs of each conjugation group.
	 */
	@Test
	public void testSubjPastSurcomp() {
		// verbs of the first group
		s = factory.createClause("je","marcher");
		s.setFeature(Feature.TENSE, Tense.PAST);
		s.setFeature(Feature.FORM, Form.SUBJUNCTIVE);
		s.setFeature(Feature.PERFECT, true);
		Assert.assertEquals( "que j'aie eu marché", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu aies eu marché", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle ait eu marché", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous ayons eu marché", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous ayez eu marché", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles aient eu marché", realise(s) );
		
		s.setVerb("noyer");
		s.setObject("se");
		s.setSubject("je");
		Assert.assertEquals( "que je me sois eu noyé", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu te sois eu noyé", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle se soit eu noyée", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous nous soyons eu noyés", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous vous soyez eu noyés", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles se soient eu noyées", realise(s) );
		// remove reflexive pronoun
		((PhraseElement) (s.getVerbPhrase()))
			.removeComplements(DiscourseFunction.OBJECT);
		
		// verbs of the second group
		s.setVerb("finir");
		s.setSubject("je");
		Assert.assertEquals( "que j'aie eu fini", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu aies eu fini", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle ait eu fini", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous ayons eu fini", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous ayez eu fini", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles aient eu fini", realise(s) );
		
		// verbs of the third group
		s.setVerb("prendre");
		s.setSubject("je");
		Assert.assertEquals( "que j'aie eu pris", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu aies eu pris", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle ait eu pris", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous ayons eu pris", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous ayez eu pris", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles aient eu pris", realise(s) );		

		s.setVerb("voir");
		s.setSubject("je");
		Assert.assertEquals( "que j'aie eu vu", realise(s) );
		s.setSubject("tu");
		Assert.assertEquals( "que tu aies eu vu", realise(s) );
		s.setSubject("elle");
		Assert.assertEquals( "qu'elle ait eu vu", realise(s) );
		s.setSubject("nous");
		Assert.assertEquals( "que nous ayons eu vu", realise(s) );
		s.setSubject("vous");
		Assert.assertEquals( "que vous ayez eu vu", realise(s) );
		s.setSubject("elles");
		Assert.assertEquals( "qu'elles aient eu vu", realise(s) );
	}
}
