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

package simplenlg.examples;

import java.util.EnumMap;
import java.util.Map;

import simplenlg.framework.*;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

import simplenlg.examples.DictioEntry;

/**
 * @author vaudrypl
 *
 */
public class GreetCrowdBilingual {
	
	/**
	 * Loads lexicons and factories for two languages (English and French).
	 * Builds a dictionary for each language to map some word senses to WordElements.
	 * Builds two equivalent clauses in those two languages and prints the result.
	 */
	public static void main(String[] args) {

        Lexicon englishLexicon = new simplenlg.lexicon.english.XMLLexicon();
        NLGFactory englishFactory = new NLGFactory(englishLexicon);

        Lexicon frenchLexicon = new simplenlg.lexicon.french.XMLLexicon();
        NLGFactory frenchFactory = new NLGFactory(frenchLexicon);
        
        Realiser realiser = new Realiser();
        
        Map<DictioEntry,WordElement> englishSensesMapping = createEnglishSensesMapping(englishLexicon);
        Map<DictioEntry,WordElement> frenchSensesMapping = createFrenchSensesMapping(frenchLexicon);
        
        SPhraseSpec englishGreeting = buildGreetCrowd(englishFactory, englishSensesMapping);
        SPhraseSpec frenchGreeting = buildGreetCrowd(frenchFactory, frenchSensesMapping);
       
        DocumentElement paragraph = englishFactory.createParagraph();
        paragraph.addComponent(englishGreeting);
        paragraph.addComponent(frenchGreeting);
        DocumentElement document = englishFactory.createDocument("Bilingual greeting\n");
        document.addComponent(paragraph);
        
        String outString = realiser.realise(document).getRealisation();
        System.out.print(outString);
	}

	/**
	 * Builds a dictionary associating word senses with WordElements
	 * from an English simpleNLG lexicon.
	 * 
	 * @param englishLexicon
	 * @return a populated English dictionary
	 */
	public static Map<DictioEntry, WordElement> createEnglishSensesMapping(
			Lexicon englishLexicon) {
		Map<DictioEntry,WordElement> englishSensesMapping =
			new EnumMap<DictioEntry,WordElement>(DictioEntry.class);
		
		englishSensesMapping.put(DictioEntry.ADULT_MALE,
				englishLexicon.getWord("man", LexicalCategory.NOUN));
		englishSensesMapping.put(DictioEntry.BILINGUAL,
				englishLexicon.getWord("bilingual", LexicalCategory.ADJECTIVE));
		englishSensesMapping.put(DictioEntry.CROWD,
				englishLexicon.getWord("crowd", LexicalCategory.NOUN));
		englishSensesMapping.put(DictioEntry.DEFINITE_DETERMINER,
				englishLexicon.getWord("the", LexicalCategory.DETERMINER));
		englishSensesMapping.put(DictioEntry.GREETING_VERB,
				englishLexicon.getWord("greet", LexicalCategory.VERB));
		englishSensesMapping.put(DictioEntry.LANGUAGE_PREPOSITION,
				englishLexicon.getWord("in", LexicalCategory.PREPOSITION));
		englishSensesMapping.put(DictioEntry.OLD,
				englishLexicon.getWord("old", LexicalCategory.ADJECTIVE));
		englishSensesMapping.put(DictioEntry.SPANISH_LANGUAGE,
				englishLexicon.getWord("Spanish", LexicalCategory.NOUN));
		englishSensesMapping.put(DictioEntry.YET,
				englishLexicon.getWord("yet", LexicalCategory.ADVERB));
		
		return englishSensesMapping;
	}

	/**
	 * Builds a dictionary associating word senses with WordElements
	 * from a French simpleNLG lexicon.
	 * 
	 * @param englishLexicon
	 * @return a populated French dictionary
	 */
	private static Map<DictioEntry, WordElement> createFrenchSensesMapping(
			Lexicon frenchLexicon) {
		Map<DictioEntry,WordElement> frenchSensesMapping =
			new EnumMap<DictioEntry,WordElement>(DictioEntry.class);
		
		frenchSensesMapping.put(DictioEntry.ADULT_MALE,
				frenchLexicon.getWord("homme", LexicalCategory.NOUN));
		frenchSensesMapping.put(DictioEntry.BILINGUAL,
				frenchLexicon.getWord("bilingue", LexicalCategory.ADJECTIVE));
		frenchSensesMapping.put(DictioEntry.CROWD,
				frenchLexicon.getWord("foule", LexicalCategory.NOUN));
		frenchSensesMapping.put(DictioEntry.DEFINITE_DETERMINER,
				frenchLexicon.getWord("le", LexicalCategory.DETERMINER));
		frenchSensesMapping.put(DictioEntry.GREETING_VERB,
				frenchLexicon.getWord("saluer", LexicalCategory.VERB));
		frenchSensesMapping.put(DictioEntry.LANGUAGE_PREPOSITION,
				frenchLexicon.getWord("en", LexicalCategory.PREPOSITION));
		frenchSensesMapping.put(DictioEntry.OLD,
				frenchLexicon.getWord("vieux", LexicalCategory.ADJECTIVE));
		frenchSensesMapping.put(DictioEntry.SPANISH_LANGUAGE,
				frenchLexicon.getWord("espagnol", LexicalCategory.NOUN));
		frenchSensesMapping.put(DictioEntry.YET,
				frenchLexicon.getWord("encore", LexicalCategory.ADVERB));
		
		return frenchSensesMapping;
	}

	/**
	 * Builds a clause saying that an old bilingual man did not yet greet
	 * the crowd in Spanish yet (only in English and French).
	 * The NP representing the crowd is replaced by a pronoun.
	 * 
	 * @param factory
	 * @param wordSensesMapping
	 * @return	the builded clause
	 */
	public static SPhraseSpec buildGreetCrowd(NLGFactory factory,
			Map<DictioEntry, WordElement> wordSensesMapping) {
		
		WordElement definiteDeterminer = wordSensesMapping.get(DictioEntry.DEFINITE_DETERMINER);
		WordElement adultMale = wordSensesMapping.get(DictioEntry.ADULT_MALE);
		WordElement old = wordSensesMapping.get(DictioEntry.OLD);
		WordElement bilingual = wordSensesMapping.get(DictioEntry.BILINGUAL);
		WordElement greetingVerb = wordSensesMapping.get(DictioEntry.GREETING_VERB);
		WordElement crowd = wordSensesMapping.get(DictioEntry.CROWD);
		WordElement yet = wordSensesMapping.get(DictioEntry.YET);
		WordElement in = wordSensesMapping.get(DictioEntry.LANGUAGE_PREPOSITION);
		WordElement spanish = wordSensesMapping.get(DictioEntry.SPANISH_LANGUAGE);
		
        NPPhraseSpec theMan = factory.createNounPhrase(definiteDeterminer, adultMale);
        theMan.addModifier(old);
        theMan.addModifier(bilingual);
        
        NPPhraseSpec theCrowd = factory.createNounPhrase(definiteDeterminer, crowd);
        theCrowd.setFeature(Feature.PRONOMINAL, true);
        
        SPhraseSpec greeting = factory.createClause(theMan, greetingVerb, theCrowd);
        
        greeting.setFeature(Feature.NEGATED, true);
        greeting.setFeature(Feature.TENSE, Tense.PAST);
        
        greeting.addPreModifier(yet);
        
        PPPhraseSpec inSpanish = factory.createPrepositionPhrase(in, spanish);
        greeting.addModifier(inSpanish);
        
		return greeting;
	}

}
