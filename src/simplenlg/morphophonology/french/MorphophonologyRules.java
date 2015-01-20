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

package simplenlg.morphophonology.french;

import java.util.HashMap;
import java.util.Map;

import simplenlg.features.*;
import simplenlg.features.french.FrenchLexicalFeature;
import simplenlg.features.french.PronounType;
import simplenlg.framework.ElementCategory;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.Language;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.morphophonology.MorphophonologyRulesInterface;
import simplenlg.phrasespec.NPPhraseSpec;

/**
 * This contains the French morphophonology rules.
 * 
 * References :
 * 
 * Grevisse, Maurice (1993). Le bon usage, grammaire française,
 * 12e édition refondue par André Goosse, 8e tirage, Éditions Duculot,
 * Louvain-la-Neuve, Belgique.

 * @author vaudrypl
 */
public class MorphophonologyRules implements MorphophonologyRulesInterface {

public static final String vowels_regex =
	"a|A|ä|Ä|à|À|â|Â|e|E|ë|Ë|é|É|è|È|ê|Ê|i|I|ï|Ï|î|Î|o|O|ô|Ô|u|U|û|Û|ü|Ü|ù|Ù|y|Y|ý|Ý|ÿ|Ÿ";

	/**
	 * This method performs the morphophonology on two
	 * StringElements.
	 * 
	 */
	public void doMorphophonology(StringElement leftWord, StringElement rightWord) {
		
		ElementCategory leftCategory = leftWord.getCategory();
		ElementCategory rightCategory = rightWord.getCategory();
		NLGElement leftParent = leftWord.getParent();
		String leftRealisation = leftWord.getRealisation();
		String rightRealisation = rightWord.getRealisation();
		
		if (leftRealisation != null && rightRealisation != null) {
		
			if (LexicalCategory.PREPOSITION.equalTo(leftCategory)
					&& (LexicalCategory.DETERMINER.equalTo(rightCategory)
							|| rightWord.getFeature(FrenchLexicalFeature.PRONOUN_TYPE)
								== PronounType.RELATIVE)) {
				// if the preposition is "de" or endswith " de"
				if (leftRealisation.matches("(.+ |)de\\z")) {
					// "de" + "le" = "du"
					if (rightRealisation.matches("le(quel)?")) {
						String withoutDe = leftRealisation.substring(0,leftRealisation.length()-2);
						leftWord.setRealisation(withoutDe + "du"
								+ rightRealisation.substring(2));
						rightWord.setRealisation(null);
						// "de" + "les" = "des"
					} else if (rightRealisation.matches("les(quel(le)?s)?")) {
						String withoutDe = leftRealisation.substring(0,leftRealisation.length()-2);
						leftWord.setRealisation(withoutDe + "des"
								+ rightRealisation.substring(3));
						rightWord.setRealisation(null);
					}
				}
				// if the preposition is "à" or endswith " à"
				if (leftRealisation.matches("(.+ |)à\\z")) {
					// "à" + "le" = "au"
					if (rightRealisation.matches("le(quel)?")) {
						String withoutA = leftRealisation.substring(0,leftRealisation.length()-1);
						leftWord.setRealisation(withoutA + "au"
								+ rightRealisation.substring(2));
						rightWord.setRealisation(null);
						// "à" + "les" = "aux"
					} else if (rightRealisation.matches("les(quel(le)?s)?")) {
						String withoutA = leftRealisation.substring(0,leftRealisation.length()-1);
						leftWord.setRealisation(withoutA + "aux"
								+ rightRealisation.substring(3));
						rightWord.setRealisation(null);
					}
				}
			}
	
			// special rule with "en" and "y" : the personal pronoun immediately preceding it
			// takes non detached form even if it is attached to an imperative verb
			Object person = leftWord.getFeature(Feature.PERSON);
			Boolean person1or2 = (person == Person.FIRST || person == Person.SECOND);
			if (LexicalCategory.PRONOUN.equalTo(leftCategory) && person1or2 &&
					leftWord.getFeature(FrenchLexicalFeature.PRONOUN_TYPE) == PronounType.PERSONAL
					&& leftWord.getFeature(Feature.NUMBER) == NumberAgreement.SINGULAR
					&& leftWord.getFeatureAsBoolean(FrenchLexicalFeature.DETACHED)
					&& LexicalCategory.PRONOUN.equalTo(rightCategory) &&
					rightWord.getFeature(FrenchLexicalFeature.PRONOUN_TYPE) == PronounType.SPECIAL_PERSONAL)
			{	
				NLGElement baseWord = leftWord.getFeatureAsElement(InternalFeature.BASE_WORD);
				if (baseWord instanceof WordElement) {
					Map<String,Object> features = new HashMap<String,Object>( baseWord.getAllFeatures() );
					features.put(FrenchLexicalFeature.DETACHED, false);
					features.remove(LexicalFeature.DEFAULT_INFL);
					features.remove(LexicalFeature.INFLECTIONS);
					features.put(InternalFeature.DISCOURSE_FUNCTION, null);
					WordElement newBaseWord = baseWord.getLexicon().getWord(LexicalCategory.PRONOUN, features);
					if (newBaseWord != null) {
						InflectedWordElement inflectedNewBaseWord = new InflectedWordElement(newBaseWord);
						leftRealisation = newBaseWord.getBaseForm();
						
						// change leftWord : creating a new StringElement wouldn't change the word, so we must
						// modify the existing one mimicking the StringElement constructor
						leftWord.clearAllFeatures();
						for(String feature : inflectedNewBaseWord.getAllFeatureNames()) {
							leftWord.setFeature(feature, inflectedNewBaseWord.getFeature(feature));
						}
						leftWord.setCategory(inflectedNewBaseWord.getCategory());
						leftWord.setFeature(Feature.ELIDED, false);
						leftWord.setRealisation(leftRealisation);
					}
					
				}
			}
			
			// words who have their last vowel elided
			// and take an apostrophe when in front of a vowel
			// (and singular for determiners)
			if (( ((leftWord.getFeatureAsBoolean(FrenchLexicalFeature.VOWEL_ELISION)
							&& leftRealisation != null && !leftRealisation.isEmpty()
							&& !leftWord.isPlural())
						|| leftRealisation.endsWith(" de") || leftRealisation.endsWith(" que"))
					&& beginsWithVowel(rightWord))
				|| ("si".equals(leftRealisation) || leftRealisation.endsWith(" si"))
					&& rightRealisation.matches("il(s)?") ) {
				
				// remove last letter (vowel) of left word and append an apostrophe
				// the orthography processing will later assure that no space is put
				// after the apostrophe
				String newLeft =
					leftRealisation.substring(0, leftRealisation.length()-1) + "'";
				leftWord.setRealisation(newLeft);
			}
				
			if (leftParent != null) {
				if ( LexicalCategory.DETERMINER.equalTo(leftCategory)
					|| LexicalCategory.ADJECTIVE.equalTo(leftCategory) ) {
					// Get gender from parent or "grand-parent" for adjectives
					boolean feminine = false;
					if (!leftParent.hasFeature(LexicalFeature.GENDER) && leftParent.getParent() != null) {
						leftParent = leftParent.getParent();
					}
					feminine = Gender.FEMININE.equals( leftParent.getFeature(LexicalFeature.GENDER) );
					// adjectives who have a different form in front of a vowel when masculine singular,
					// possessive determiners when feminine singular
					// and non possessive determiners when masculine singular
					String liaisonForm = leftWord.getFeatureAsString(FrenchLexicalFeature.LIAISON);
					boolean possessive = leftWord.getFeatureAsBoolean(Feature.POSSESSIVE);
					if ( liaisonForm != null && beginsWithVowel(rightWord)
						&& !leftParent.isPlural()
						&& ((leftCategory == LexicalCategory.DETERMINER && possessive == feminine ) 
							|| (leftCategory == LexicalCategory.ADJECTIVE && !feminine
									&& rightCategory == LexicalCategory.NOUN))) {
						leftWord.setRealisation(liaisonForm);
					}
				}
			}
			
			// remove duplicate "de" or "que"
			if ("de".equals(leftRealisation) &&
						("de".equals(rightRealisation) || "du".equals(rightRealisation)
							|| "d'".equals(rightRealisation))
					|| "que".equals(leftRealisation) &&
						("que".equals(rightRealisation)	|| "qu'".equals(rightRealisation)) ) {
				leftWord.setRealisation(null);
			}
		}
	}
	
	/**
	 * Tells if a word begins with a vowel or an "aspired h"
	 * 
	 * @param word
	 * @return true if the words begins with a vowel or an "aspired h"
	 */
	public boolean beginsWithVowel(StringElement word)
	{
		// A word can be marked as having a so-called "aspired h"
		// even if it isn't written with an "h" at the beginning.
		// Numerals are also considered to have this trait.
		// ("le onzième jour", "le huit du mois")
		String realisation = word.getRealisation();
		return ( realisation.matches("\\A(" + vowels_regex + "|h|H).*") &&
					!word.getFeatureAsBoolean(FrenchLexicalFeature.ASPIRED_H)
					&& !realisation.endsWith("ième"));
	}
}