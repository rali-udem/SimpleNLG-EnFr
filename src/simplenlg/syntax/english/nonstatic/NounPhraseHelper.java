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
package simplenlg.syntax.english.nonstatic;

import java.util.ArrayList;
import java.util.List;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.Person;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.AdjPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.syntax.AbstractNounPhraseHelper;

/**
 * <p>
 * This class contains methods to help the syntax processor realise noun
 * phrases for English. It is a non static version by vaudrypl of the class of
 * the same name in the <code>simplenlg.syntax.english</code> package.
 * </p>
 * modified by vaudrypl :
 * abstract class replaced by public class
 * private static methods replaced by protected methods
 * parent.realise(element) replaced by element.realiseSyntax()
 * SyntaxProcessor parent arguments removed
 * PhraseHelper replaced by phrase.getPhraseHelper()
 * now extends AbstractNounPhraseHelper
 * 
 * @author E. Reiter and D. Westwater, University of Aberdeen.
 * @version 4.0
 */
public class NounPhraseHelper extends AbstractNounPhraseHelper {

	/** The qualitative position for ordering premodifiers. */
	private static final int QUALITATIVE_POSITION = 1;

	/** The colour position for ordering premodifiers. */
	private static final int COLOUR_POSITION = 2;

	/** The classifying position for ordering premodifiers. */
	private static final int CLASSIFYING_POSITION = 3;

	/** The noun position for ordering premodifiers. */
	private static final int NOUN_POSITION = 4;

	/**
	 * Sort the list of premodifiers for this noun phrase using adjective
	 * ordering (ie, "big" comes before "red")
	 * 
	 * @param originalModifiers
	 *            the original listing of the premodifiers.
	 * @return the sorted <code>List</code> of premodifiers.
	 */
	@Override
	protected List<NLGElement> sortNPPreModifiers(List<NLGElement> originalModifiers) {

		List<NLGElement> orderedModifiers = null;

		if (originalModifiers == null || originalModifiers.size() <= 1) {
			orderedModifiers = originalModifiers;
		} else {
			orderedModifiers = new ArrayList<NLGElement>(originalModifiers);
			boolean changesMade = false;
			do {
				changesMade = false;
				for (int i = 0; i < orderedModifiers.size() - 1; i++) {
					if (getMinPos(orderedModifiers.get(i)) > getMaxPos(orderedModifiers
							.get(i + 1))) {
						NLGElement temp = orderedModifiers.get(i);
						orderedModifiers.set(i, orderedModifiers.get(i + 1));
						orderedModifiers.set(i + 1, temp);
						changesMade = true;
					}
				}
			} while (changesMade == true);
		}
		return orderedModifiers;
	}

	/**
	 * Determines the minimim position at which this modifier can occur.
	 * 
	 * @param modifier
	 *            the modifier to be checked.
	 * @return the minimum position for this modifier.
	 */
	protected int getMinPos(NLGElement modifier) {
		int position = QUALITATIVE_POSITION;

		if (modifier.isA(LexicalCategory.NOUN)
				|| modifier.isA(PhraseCategory.NOUN_PHRASE)) {

			position = NOUN_POSITION;
		} else if (modifier.isA(LexicalCategory.ADJECTIVE)
				|| modifier.isA(PhraseCategory.ADJECTIVE_PHRASE)) {
			WordElement adjective = getHeadWordElement(modifier);

			if (adjective.getFeatureAsBoolean(LexicalFeature.QUALITATIVE)
					.booleanValue()) {
				position = QUALITATIVE_POSITION;
			} else if (adjective.getFeatureAsBoolean(LexicalFeature.COLOUR)
					.booleanValue()) {
				position = COLOUR_POSITION;
			} else if (adjective
					.getFeatureAsBoolean(LexicalFeature.CLASSIFYING)
					.booleanValue()) {
				position = CLASSIFYING_POSITION;
			}
		}
		return position;
	}

	/**
	 * Determines the maximim position at which this modifier can occur.
	 * 
	 * @param modifier
	 *            the modifier to be checked.
	 * @return the maximum position for this modifier.
	 */
	protected int getMaxPos(NLGElement modifier) {
		int position = NOUN_POSITION;

		if (modifier.isA(LexicalCategory.ADJECTIVE)
				|| modifier.isA(PhraseCategory.ADJECTIVE_PHRASE)) {
			WordElement adjective = getHeadWordElement(modifier);

			if (adjective.getFeatureAsBoolean(LexicalFeature.CLASSIFYING)
					.booleanValue()) {
				position = CLASSIFYING_POSITION;
			} else if (adjective.getFeatureAsBoolean(LexicalFeature.COLOUR)
					.booleanValue()) {
				position = COLOUR_POSITION;
			} else if (adjective
					.getFeatureAsBoolean(LexicalFeature.QUALITATIVE)
					.booleanValue()) {
				position = QUALITATIVE_POSITION;
			} else {
				position = CLASSIFYING_POSITION;
			}
		}
		return position;
	}

	/**
	 * Creates the appropriate pronoun for the noun phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @return the <code>NLGElement</code> representing the pronominal.
	 */
	@Override
	protected NLGElement createPronoun(PhraseElement phrase) {

		String pronoun = "it"; //$NON-NLS-1$
		NLGFactory phraseFactory = phrase.getFactory();
		Object personValue = phrase.getFeature(Feature.PERSON);

		if (Person.FIRST.equals(personValue)) {
			pronoun = "I"; //$NON-NLS-1$
		} else if (Person.SECOND.equals(personValue)) {
			pronoun = "you"; //$NON-NLS-1$
		} else {
			Object genderValue = phrase.getFeature(LexicalFeature.GENDER);
			if (Gender.FEMININE.equals(genderValue)) {
				pronoun = "she"; //$NON-NLS-1$
			} else if (Gender.MASCULINE.equals(genderValue)) {
				pronoun = "he"; //$NON-NLS-1$
			}
		}
		// AG: createWord now returns WordElement; so we embed it in an
		// inflected word element here
		NLGElement element;
		NLGElement proElement = phraseFactory.createWord(pronoun,
				LexicalCategory.PRONOUN);
		
		if (proElement instanceof WordElement) {
			element = new InflectedWordElement((WordElement) proElement);
			element.setFeature(LexicalFeature.GENDER, ((WordElement) proElement).getFeature(LexicalFeature.GENDER));			
		} else {
			element = proElement;
		}
		
		element.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.SPECIFIER);
		element.setFeature(Feature.POSSESSIVE, phrase.getFeature(Feature.POSSESSIVE));
		element.setFeature(Feature.NUMBER, phrase.getFeature(Feature.NUMBER));
		
		if (phrase.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
			element.setFeature(InternalFeature.DISCOURSE_FUNCTION, phrase
					.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		}		

		return element;
	}
	
	/**
	 * Add a modifier to a noun phrase. Use heuristics to decide where it goes.
	 * 
	 * code moved from simplenl.phrasespec.NPPhraseSpec.addModifier(Object modifier)
	 * by vaudrypl
	 * 
	 * @param nounPhrase
	 * @param modifier
	 * 
	 */
	@Override
	public void addModifier(NPPhraseSpec nounPhrase, Object modifier) {
		// string which is one lexicographic word is looked up in lexicon,
		// adjective is preModifier
		// Everything else is postModifier
		if (modifier == null)
			return;

		// get modifier as NLGElement if possible
		NLGElement modifierElement = null;
		if (modifier instanceof NLGElement)
			modifierElement = (NLGElement) modifier;
		else if (modifier instanceof String) {
			String modifierString = (String) modifier;
			if (modifierString.length() > 0 && !modifierString.contains(" "))
				modifierElement = nounPhrase.getFactory().createWord(modifier,
						LexicalCategory.ANY);
		}

		// if no modifier element, must be a complex string, add as postModifier
		if (modifierElement == null) {
			nounPhrase.addPostModifier((String) modifier);
			return;
		}

		// AdjP is premodifer
		if (modifierElement instanceof AdjPhraseSpec) {
			nounPhrase.addPreModifier(modifierElement);
			return;
		}

		// else extract WordElement if modifier is a single word
		WordElement modifierWord = null;
		if (modifierElement != null && modifierElement instanceof WordElement)
			modifierWord = (WordElement) modifierElement;
		else if (modifierElement != null
				&& modifierElement instanceof InflectedWordElement)
			modifierWord = ((InflectedWordElement) modifierElement)
					.getBaseWord();

		// check if modifier is an adjective
		if (modifierWord != null
				&& modifierWord.getCategory() == LexicalCategory.ADJECTIVE) {
			nounPhrase.addPreModifier(modifierWord);
			return;
		}

		// default case
		nounPhrase.addPostModifier(modifierElement);
	}
}
