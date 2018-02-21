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


import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import simplenlg.features.Gender;
import simplenlg.features.LexicalFeature;
import simplenlg.framework.Language;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.MultipleLexicon;
import simplenlg.lexicon.french.XMLLexicon;

/**
 * @author D. Westwater, Data2Text Ltd
 *
 */
public class MultipleLexiconTest {

	// XML lexicon location
	static final String xmlLexiconFilePath = "test-secondary-french-lexicon.xml";
	
	// multi lexicon
	MultipleLexicon lexicon;


	@Before
	public void setUp() throws Exception {
		URI secondaryLexicon = null;
		try {
			secondaryLexicon = getClass().getClassLoader().getResource(xmlLexiconFilePath).toURI();
		} catch (URISyntaxException ex) {
			System.out.println(ex.toString());
		}
		this.lexicon = new MultipleLexicon(Language.FRENCH,
				new XMLLexicon(), new XMLLexicon(secondaryLexicon));
	}

	@After
	public void tearDown() throws Exception {
		lexicon.close();
	}

	@Test
	public void testBasics() {
		SharedLexiconTests.doBasicTests(lexicon);
	}
	
	@Test
	public void testMultipleSpecifics() {
		// try to get a word which is only in default lexicon
		WordElement souris = lexicon.getWord("souris");
		Assert.assertEquals(Gender.FEMININE, souris.getFeature(LexicalFeature.GENDER));
		Assert.assertEquals(Language.FRENCH, souris.getLanguage());
		
		// try to get a word which is only in secondary lexicon
		WordElement imprimante = lexicon.getWord("imprimante");
		Assert.assertEquals(Gender.FEMININE, imprimante.getFeature(LexicalFeature.GENDER));
		Assert.assertEquals(Language.FRENCH, imprimante.getLanguage());

		// try to get a word which doesn't exist in either lexicon
		WordElement anticonst = lexicon.getWord("anticonstitutionnellement", LexicalCategory.ADVERB);
		Assert.assertEquals(Language.FRENCH, anticonst.getLanguage());

		// test alwaysSearchAll flag
		boolean alwaysSearchAll = lexicon.isAlwaysSearchAll();
		
		// "sourire" as verb exists in the default lexicon,
		// but as noun only in secondary lexicon
		lexicon.setAlwaysSearchAll(true);
		 // 2 = once in default plus once in secondary lexicon
		Assert.assertEquals(2, lexicon.getWords("sourire").size());
		
		lexicon.setAlwaysSearchAll(false);
		Assert.assertEquals(1, lexicon.getWords("sourire").size());

		// restore flag to original state
		lexicon.setAlwaysSearchAll(alwaysSearchAll);	
	}


}
