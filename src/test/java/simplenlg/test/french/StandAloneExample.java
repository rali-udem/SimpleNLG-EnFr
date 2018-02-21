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

import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.LexicalFeature;
import simplenlg.features.Tense;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.french.XMLLexicon;
import simplenlg.phrasespec.AdjPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.Realiser;

/**
 * @author D. Westwater, Data2Text Ltd
 *
 * translated and adapted by vaudrypl
 */
public class StandAloneExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// below is a simple complete example of using simplenlg V4
		// afterwards is an example of using simplenlg just for morphology
		
		// set up
		Lexicon lexicon = new XMLLexicon();                          // default simplenlg lexicon
		NLGFactory nlgFactory = new NLGFactory(lexicon);             // factory based on lexicon

		// create sentences
		// 	"John did not go to the bigger park. He played football there."
		NPPhraseSpec thePark = nlgFactory.createNounPhrase("le", "parc");   // create an NP
		AdjPhraseSpec bigp = nlgFactory.createAdjectivePhrase("grand");        // create AdjP
		thePark.addModifier(bigp);                                        // add adj as modifier in NP
		// above relies on default placement rules.  You can force placement as a postmodifier
		// (before head) by using addPostModifier
		PPPhraseSpec toThePark = nlgFactory.createPrepositionPhrase("à");    // create a PP
		toThePark.setObject(thePark);                                     // set PP object
		// could also just say nlgFactory.createPrepositionPhrase("à", thePark);

		SPhraseSpec johnGoToThePark = nlgFactory.createClause("Jean",      // create sentence
				"aller", toThePark);

		johnGoToThePark.setFeature(Feature.TENSE,Tense.PAST);              // set tense
		johnGoToThePark.setFeature(Feature.NEGATED, true);                 // set negated
		
		// note that constituents (such as subject and object) are set with setXXX methods
		// while features are set with setFeature

		DocumentElement sentence = nlgFactory							// create a sentence DocumentElement from SPhraseSpec
				.createSentence(johnGoToThePark);

		// below creates a sentence DocumentElement by concatenating strings
		StringElement hePlayed = new StringElement("il a joué");        
		StringElement there = new StringElement("là-bas");
		WordElement football = (WordElement)nlgFactory.createNLGElement("football");
		NPPhraseSpec leFootball = nlgFactory.createNounPhrase("le", football);
		PPPhraseSpec auFootball = nlgFactory.createPrepositionPhrase("à", leFootball);

		DocumentElement sentence2 = nlgFactory.createSentence();
		sentence2.addComponent(hePlayed);
		sentence2.addComponent(auFootball);
		sentence2.addComponent(there);

		// now create a paragraph which contains these sentences
		DocumentElement paragraph = nlgFactory.createParagraph();
		paragraph.addComponent(sentence);
		paragraph.addComponent(sentence2);

		// create a realiser.
		Realiser realiser = new Realiser();
//		realiser.setDebugMode(true);     // uncomment this to print out debug info during realisation
		NLGElement realised = realiser.realise(paragraph);

		System.out.println(realised.getRealisation());

		// end of main example
		
		// second example - using simplenlg just for morphology
		// in V4 morphology is done by a MorphologyProcessor, not by the lexicon
		
		// create inflected word
		NLGElement word = nlgFactory.createWord("beau", LexicalCategory.ADJECTIVE);
		// setPlural is an exception to the general rule that features are set with setFeature
		word.setPlural(true);
		word.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		
		// get result from realiser
		String result = realiser.realise(word).getRealisation();
		
		System.out.println(result);
	}
}
