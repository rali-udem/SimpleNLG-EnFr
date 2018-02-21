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
import simplenlg.features.Gender;
import simplenlg.features.LexicalFeature;
import simplenlg.features.french.FrenchLexicalFeature;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;

/**
 * @author D. Westwater, Data2Text Ltd
 * 
 * translated and adapted by vaudrypl
 */
public class SharedLexiconTests {

	public static void doBasicTests(Lexicon lexicon) {
		// test getWords. Should be 2 "can" (of any cat), 1 noun tree, 0 adj
		// trees
		Assert.assertEquals(2, lexicon.getWords("personne").size());
		Assert.assertEquals(1, lexicon.getWords("personne", LexicalCategory.NOUN)
				.size());
		Assert.assertEquals(0, lexicon.getWords("personne",
				LexicalCategory.ADJECTIVE).size());

		// test getWord. Comparative of ADJ "bon" is "meilleur"
		WordElement bon = lexicon.getWord("bon", LexicalCategory.ADJECTIVE);
		Assert.assertEquals("meilleur", bon
				.getFeatureAsString(LexicalFeature.COMPARATIVE));

		// test getWord. There is only one "femme", and its plural is "femme".
		// It is feminine, it's masculine form is "homme",
		// it is not an acronym and not proper
		WordElement femme = lexicon.getWord("femme");

		Assert.assertEquals(Gender.FEMININE, femme.getFeature(
				LexicalFeature.GENDER));
		Assert.assertEquals("homme", femme
				.getFeatureAsString(FrenchLexicalFeature.OPPOSITE_GENDER));
		Assert.assertEquals(null, femme
				.getFeatureAsString(LexicalFeature.ACRONYM_OF));
		Assert.assertEquals(false, femme.getFeatureAsBoolean(
				LexicalFeature.PROPER).booleanValue());
		
		// test hasWord
		Assert.assertEquals(true, lexicon.hasWord("maison")); // "tree" exists
		Assert.assertEquals(false, lexicon.hasWord("maison",
				LexicalCategory.ADVERB)); // but not as an adverb

		// test getWordByID
		WordElement manger = lexicon.getWordByID("manger_2");
		Assert.assertEquals("manger", manger.getBaseForm());
		Assert.assertEquals(LexicalCategory.VERB, manger.getCategory());

		// test getWordFromVariant
		WordElement verb;
		verb = lexicon.getWordFromVariant("mangeant");
		Assert.assertEquals("manger", verb.getBaseForm());
		Assert.assertEquals(LexicalCategory.VERB, verb.getCategory());
		verb = lexicon.getWordFromVariant("étées");
		Assert.assertEquals("être", verb.getBaseForm());
		Assert.assertEquals(LexicalCategory.VERB, verb.getCategory());
		verb = lexicon.getWordFromVariant("soyez");
		Assert.assertEquals("être", verb.getBaseForm());
		Assert.assertEquals(LexicalCategory.VERB, verb.getCategory());
		verb = lexicon.getWordFromVariant("ferai");
		Assert.assertEquals("faire", verb.getBaseForm());
		Assert.assertEquals(LexicalCategory.VERB, verb.getCategory());
		verb = lexicon.getWordFromVariant("iront");
		Assert.assertEquals("aller", verb.getBaseForm());
		Assert.assertEquals(LexicalCategory.VERB, verb.getCategory());
		verb = lexicon.getWordFromVariant("avions");
		Assert.assertEquals("avoir", verb.getBaseForm());
		Assert.assertEquals(LexicalCategory.VERB, verb.getCategory());
		verb = lexicon.getWordFromVariant("vient");
		Assert.assertEquals("venir", verb.getBaseForm());
		Assert.assertEquals(LexicalCategory.VERB, verb.getCategory());

		// test ÊTRE is handled OK
		Assert.assertEquals("été", lexicon.getWordFromVariant("est",
				LexicalCategory.VERB).getFeatureAsString(
				LexicalFeature.PAST_PARTICIPLE));

		// test non-existent word
		Assert.assertEquals(0, lexicon.getWords("akjmchsgk").size());

		// test lookup word method
		Assert.assertEquals(lexicon.lookupWord("dire", LexicalCategory.VERB)
				.getBaseForm(), "dire");
		Assert.assertEquals(lexicon.lookupWord("dit", LexicalCategory.VERB)
				.getBaseForm(), "dire");
		Assert.assertEquals(lexicon
				.lookupWord("dire_2", LexicalCategory.VERB).getBaseForm(),
				"dire");
	}

}
