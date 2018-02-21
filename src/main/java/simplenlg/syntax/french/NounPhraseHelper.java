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
package simplenlg.syntax.french;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Person;
import simplenlg.features.french.FrenchLexicalFeature;
import simplenlg.features.french.PronounType;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.AdjPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.syntax.AbstractNounPhraseHelper;

/**
 * This class contains static methods to help the syntax processor realise noun
 * phrases for French.
 * 
 * @author vaudrypl
 */
public class NounPhraseHelper extends AbstractNounPhraseHelper
{
	/**
	 * Creates the appropriate personal pronoun for the noun phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @return the <code>NLGElement</code> representing the pronominal.
	 */
	@Override
	protected NLGElement createPronoun(PhraseElement phrase) {

		// this will contain the features we want the pronoun to have
		Map<String, Object> pronounFeatures = new HashMap<String, Object>();
		
		pronounFeatures.put(FrenchLexicalFeature.PRONOUN_TYPE, PronounType.PERSONAL);

		Object personValue = phrase.getFeature(Feature.PERSON);
		Person person;
		if (personValue instanceof Person) {
			pronounFeatures.put(Feature.PERSON, personValue);
			person = (Person) personValue;
		}
		// default person is THIRD
		else {
			pronounFeatures.put(Feature.PERSON, Person.THIRD);
			person = Person.THIRD;
		}
		
		// only check gender feature for third person pronouns
		if (person == Person.THIRD) {
			Object genderValue = phrase.getFeature(LexicalFeature.GENDER);
			if (genderValue instanceof Gender) {
				pronounFeatures.put(LexicalFeature.GENDER, genderValue);
			}
			// default gender is MASCULINE
			else {
				pronounFeatures.put(LexicalFeature.GENDER, Gender.MASCULINE);
			}
		}
		
		Object numberValue = phrase.getFeature(Feature.NUMBER);
		if (numberValue instanceof NumberAgreement) {
			pronounFeatures.put(Feature.NUMBER, numberValue);
		}
		// default number is SINGULAR
		else {
			pronounFeatures.put(Feature.NUMBER, NumberAgreement.SINGULAR);
		}
		
		pronounFeatures.put(Feature.POSSESSIVE,
				phrase.getFeatureAsBoolean(Feature.POSSESSIVE));
		
		NLGFactory phraseFactory = phrase.getFactory();
		Lexicon lexicon = phraseFactory.getLexicon();
		// search the lexicon for the right pronoun
		WordElement proElement =
			lexicon.getWord(LexicalCategory.PRONOUN, pronounFeatures);
		
		// if the right pronoun is not found in the lexicon,
		// take "il" as a last resort
		if (proElement == null) proElement = lexicon.lookupWord("il", LexicalCategory.PRONOUN);
			
		// AG: createWord now returns WordElement; so we embed it in an
		// inflected word element here
		InflectedWordElement element = new InflectedWordElement(proElement);
		
		if (phrase.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
			element.setFeature(InternalFeature.DISCOURSE_FUNCTION, 
					phrase.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		}		
		if (phrase.hasFeature(Feature.PASSIVE)) {
			element.setFeature(Feature.PASSIVE, 
					phrase.getFeature(Feature.PASSIVE));
		}		

		return element;
	}
	
	/**
	 * Add a modifier to a noun phrase. Use heuristics to decide where it goes.
	 * 
	 * based on simplenl.phrasespec.NPPhraseSpec.addModifier(Object modifier)
	 * 
	 * @param nounPhrase
	 * @param modifier
	 */
	@Override
	public void addModifier(NPPhraseSpec nounPhrase, Object modifier) {
		// string which is one lexicographic word is looked up in lexicon,
		// preposed adjective is preModifier
		// Everything else is postModifier
		if (modifier == null)
			return;

		// get modifier as NLGElement if possible
		NLGElement modifierElement = null;
		if (modifier instanceof NLGElement)
			modifierElement = (NLGElement) modifier;
		else if (modifier instanceof String) {
			String modifierString = (String) modifier;
			Lexicon lexicon = nounPhrase.getLexicon();
			if (lexicon.hasWord(modifierString)) {
				modifierElement = lexicon.lookupWord(modifierString);
			}
			else if (isOrdinal(modifierString) ||
						(modifierString.length() > 0 && !modifierString.contains(" "))) {
				modifierElement = nounPhrase.getFactory().createWord(modifier,
						LexicalCategory.ADJECTIVE);
			}
		}

		// if no modifier element, must be a complex string, add as postModifier
		if (modifierElement == null) {
			nounPhrase.addPostModifier((String) modifier);
			return;
		}

		// AdjP is premodifer
		if (modifierElement instanceof AdjPhraseSpec) {
			AdjPhraseSpec modifierAdjPhrase = (AdjPhraseSpec) modifierElement;
			NLGElement modifierHead = modifierAdjPhrase.getHead();
			List<NLGElement> modifierComplements =
				modifierAdjPhrase.getFeatureAsElementList(InternalFeature.COMPLEMENTS);
			if ((modifierHead.getFeatureAsBoolean(FrenchLexicalFeature.PREPOSED)
						|| isOrdinal(modifierHead))
					&& modifierComplements.isEmpty()) {
				nounPhrase.addPreModifier(modifierElement);
				return;
			}
		}

		// else extract WordElement if modifier is a single word
		WordElement modifierWord = null;
		if (modifierElement instanceof WordElement)
			modifierWord = (WordElement) modifierElement;
		else if (modifierElement instanceof InflectedWordElement)
			modifierWord = ((InflectedWordElement) modifierElement)
					.getBaseWord();

		// check if modifier is an adjective
		if (modifierWord != null
				&& modifierWord.getCategory() == LexicalCategory.ADJECTIVE
				&& (modifierElement.getFeatureAsBoolean(FrenchLexicalFeature.PREPOSED)
						|| isOrdinal(modifierWord))) {
			nounPhrase.addPreModifier(modifierWord);
			return;
		}

		// default case
		nounPhrase.addPostModifier(modifierElement);
	}

	/**
	 * Recognises ordinal adjectives by their ending ("ième").
	 * Exceptions : "premier", "second" and "dernier" are treated
	 * in the lexicon.
	 * 
	 * @param element
	 * @return
	 * 			true if the element represents an ordinal adjective
	 * 			false otherwise
	 */
	protected boolean isOrdinal(NLGElement element) {
		String baseForm = null;
		
		if (element instanceof WordElement) {
			baseForm = ((WordElement) element).getBaseForm();
		} else if (element instanceof StringElement) {
			baseForm = ((StringElement) element).getRealisation();
		}
		
		return isOrdinal(baseForm);
	}
		
	/**
	 * Recognises ordinal adjectives by their ending ("ième").
	 * Exceptions : "premier", "second" and "dernier" are treated
	 * in the lexicon.
	 * 
	 * @param expression
	 * @return
	 * 			true if the expression represents an ordinal adjective
	 * 			false otherwise
	 */
	protected boolean isOrdinal(String expression) {
		return (expression != null && expression.endsWith("ième"));
	}
		
	/**
	 * The main method for realising noun phrases.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> to be realised.
	 * @return the realised <code>NLGElement</code>.
	 */
	@Override
	public ListElement realise(PhraseElement phrase) {
		ListElement realisedElement = null;

			realisedElement = new ListElement(phrase);

		// Creates the appropriate pronoun if the noun phrase
		// is pronominal.
		if (phrase.getFeatureAsBoolean(Feature.PRONOMINAL)) {
			realisedElement.addComponent(createPronoun(phrase));
			
		} else {
			NLGElement specifierElement = 
				phrase.getFeatureAsElement(InternalFeature.SPECIFIER);
			Lexicon lexicon = specifierElement != null ? specifierElement.getLexicon() : null;

			// if it is the "partitif" determiner "du"
			if (lexicon != null
					&& !phrase.getFeatureAsBoolean(InternalFeature.RAISED)
					&& specifierElement.equals(lexicon.getWord("du", LexicalCategory.DETERMINER)) ) {
				
				WordElement de = lexicon.getWord("de", LexicalCategory.PREPOSITION);
				NLGElement realisedDe = de.realiseSyntax();
				if (realisedDe != null) {
					realisedDe.setFeature(InternalFeature.DISCOURSE_FUNCTION,
							DiscourseFunction.SPECIFIER);
					realisedElement.addComponent(realisedDe);
				}
				NPPhraseSpec subphrase = new NPPhraseSpec((NPPhraseSpec)phrase);
				WordElement newDeterminer = lexicon.getWord("le", LexicalCategory.DETERMINER);
				// if the noun phrase is the direct object of a negated verb,
				// the determiner is reduced to "de" instead of "du"
				if (checkNegatedObject(phrase)) newDeterminer = null;
				subphrase.setSpecifier(newDeterminer);
				realisedElement.addComponent( super.realise(subphrase) );
				
			// changes "un" to "de" if the noun phrase is the direct object of a negated verb
			} else if (lexicon != null
					&& !phrase.getFeatureAsBoolean(InternalFeature.RAISED)
					&& specifierElement.equals(lexicon.getWord("un", LexicalCategory.DETERMINER))
					&& checkNegatedObject(phrase)) {
				NPPhraseSpec newPhrase = new NPPhraseSpec((NPPhraseSpec)phrase);
				WordElement de = lexicon.getWord("de", LexicalCategory.PREPOSITION);
				newPhrase.setSpecifier(de);
				realisedElement = super.realise(newPhrase);
			} else {
				realisedElement = super.realise(phrase);
			}
		}
		// to take care of demonstrative determiners with particle (e.g. "ce chien-là")
		addParticle(phrase, realisedElement);
		
		return realisedElement;
	}

	/**
	 * Checks if the noun phrase is the direct object of a negated verb
	 * 
	 * @param phrase	the noun phrase
	 * @return true if the noun phrase is the direct object of a negated verb
	 */
	protected boolean checkNegatedObject(PhraseElement phrase) {
		boolean returnValue = false;
		
		NLGElement parent = phrase.getParent();
		if (parent != null) {
			Object function = phrase.getFeature(InternalFeature.DISCOURSE_FUNCTION);
			boolean passive = parent.getFeatureAsBoolean(Feature.PASSIVE);
			boolean negated = parent.getFeatureAsBoolean(Feature.NEGATED);
			boolean ne_only_negation = parent.checkIfNeOnlyNegation();
			negated = negated || ne_only_negation;
			if (!passive && negated && function == DiscourseFunction.OBJECT) {
				returnValue = true;
			}
		}
		
		return returnValue;
	}

	/**
	 * Adds the particle feature (if any) to the last element syntactically realised
	 * 
	 * @param phrase			the noun phrase
	 * @param realisedElement	the ListElement of syntactically realised elements
	 */
	protected void addParticle(PhraseElement phrase, ListElement realisedElement) {
		NLGElement specifier = phrase.getFeatureAsElement(InternalFeature.SPECIFIER);
		
		if (specifier != null) {
			String particle = specifier.getFeatureAsString(Feature.PARTICLE);
			
			if (particle != null) {
				NLGElement lastElement = realisedElement.getRightMostTerminalElement();		
				
				if (lastElement instanceof InflectedWordElement) {
					lastElement.setFeature(Feature.PARTICLE, particle);
				} else if (lastElement instanceof StringElement) {
					String realisation = lastElement.getRealisation();
					realisation += "-" + particle;
					lastElement.setRealisation(realisation);
				}
			}
		}
	}

	/**
	 * Realises the specifier of the noun phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	@Override
	protected void realiseSpecifier(PhraseElement phrase,
			ListElement realisedElement) {
		super.realiseSpecifier(phrase, realisedElement);
		
//		NLGElement parent = phrase.getParent()
		
		NLGElement specifierElement = 
			phrase.getFeatureAsElement(InternalFeature.SPECIFIER);

		// add "de" to adverbs used as determiners (e.g. "beaucoup de vin")
		if (specifierElement != null
				&& !phrase.getFeatureAsBoolean(InternalFeature.RAISED)
				&& specifierElement.isA(LexicalCategory.ADVERB)) {
			
			Lexicon lexicon = specifierElement.getLexicon();
			WordElement de = lexicon.getWord("de", LexicalCategory.PREPOSITION);
			NLGElement realisedDe = de.realiseSyntax();
			if (realisedDe != null) {
				realisedDe.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						DiscourseFunction.SPECIFIER);
				realisedElement.addComponent(realisedDe);
			}
		}
	}
	
}
