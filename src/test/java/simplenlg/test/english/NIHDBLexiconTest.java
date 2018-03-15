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

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import simplenlg.features.LexicalFeature;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.NIHDBLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.realiser.english.Realiser;

/**
 * Tests for NIHDBLexicon
 * 
 * @author Ehud Reiter
 */
public class NIHDBLexiconTest extends TestCase {

	// lexicon object -- an instance of Lexicon
	NIHDBLexicon lexicon = null;

	// DB location -- change this to point to the lex access data dir
	static String DB_FILENAME = "src/test/resources/NIHLexicon/lexAccess2013.data";

	@Override
	@Before
	/*
	 * * Sets up the accessor and runs it -- takes ca. 26 sec
	 */
	public void setUp() {
		this.lexicon = new NIHDBLexicon(DB_FILENAME);
	}

	/**
	 * Close the lexicon
	 */
	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		if (lexicon != null)
			lexicon.close();
	}

	@Test
	public void testBasics() {
		SharedLexiconTests.doBasicTests(lexicon);
	}

	@Test
	public void testAcronyms() {
		WordElement uk = lexicon.getWord("UK");
		WordElement unitedKingdom = lexicon.getWord("United Kingdom");
		List<NLGElement> fullForms = uk
				.getFeatureAsElementList(LexicalFeature.ACRONYM_OF);

		// "uk" is an acronym of 3 full forms
		Assert.assertEquals(3, fullForms.size());
		Assert.assertTrue(fullForms.contains(unitedKingdom));
	}

	@Test
	public void testStandardInflections() {
		// test keepStandardInflection flag
		boolean keepInflectionsFlag = lexicon.isKeepStandardInflections();

		lexicon.setKeepStandardInflections(true);
		WordElement dog = lexicon.getWord("dog", LexicalCategory.NOUN);
		Assert.assertEquals("dogs", dog
				.getFeatureAsString(LexicalFeature.PLURAL));

		lexicon.setKeepStandardInflections(false);
		WordElement cat = lexicon.getWord("cat", LexicalCategory.NOUN);
		Assert
				.assertEquals(null, cat
						.getFeatureAsString(LexicalFeature.PLURAL));

		// restore flag to original state
		lexicon.setKeepStandardInflections(keepInflectionsFlag);
	}

	

}
