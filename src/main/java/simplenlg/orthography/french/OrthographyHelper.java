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
package simplenlg.orthography.french;

import java.util.List;

import simplenlg.features.*;
import simplenlg.features.french.FrenchLexicalFeature;
import simplenlg.features.french.FrenchInternalFeature;
import simplenlg.framework.ElementCategory;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.StringElement;
import simplenlg.orthography.OrthographyHelperInterface;

/**
 * <p>
 * This processing module deals with punctuation when applied to
 * <code>DocumentElement</code>s for French.
 *  
 * @author vaudrypl
 */
public class OrthographyHelper extends simplenlg.orthography.english.OrthographyHelper
	implements OrthographyHelperInterface {

	/**
	 * Realises coordinated phrases. Where there are more than two coordinates,
	 * then a comma replaces the conjunction word between all the coordinates
	 * save the last two. For example, <em>John and Peter and Simon</em> becomes
	 * <em>John, Peter and Simon</em>.
	 * 
	 * @param components
	 *            the <code>List</code> of <code>NLGElement</code>s representing
	 *            the components that make up the sentence.
	 * @return the realised element as an <code>NLGElement</code>.
	 */
	@Override
	public NLGElement realiseCoordinatedPhrase(List<NLGElement> components) {
		StringBuffer realisation = new StringBuffer();
		NLGElement realisedChild = null;

		int length = components.size();

		for (int index = 0; index < length; index++) {
			realisedChild = components.get(index);
			if ( DiscourseFunction.CONJUNCTION.equals(realisedChild
					.getFeature(InternalFeature.DISCOURSE_FUNCTION)) ) {
				boolean repeated = realisedChild
					.getFeatureAsBoolean(FrenchLexicalFeature.REPEATED_CONJUNCTION);
				if (index == 0) {
					if (repeated) {
						realisedChild = realisedChild.realiseOrthography();
						realisation.append(realisedChild.getRealisation());
						realisation.append(' '); //$NON-NLS-1$
					}
				} else if (index < length - 2 && !repeated) {
					realisation.append(", "); //$NON-NLS-1$
				} else {
					// for conjunctions other than "et" and "ou"
					if (!realisedChild.getFeatureAsBoolean(LexicalFeature.NO_COMMA)) {
					realisation.append(", "); //$NON-NLS-1$
					}
					realisedChild = realisedChild.realiseOrthography();
					realisation.append(realisedChild.getRealisation());
					realisation.append(' '); //$NON-NLS-1$
				}
			} else {
				realisedChild = realisedChild.realiseOrthography();
				realisation.append(realisedChild.getRealisation());
				realisation.append(' '); //$NON-NLS-1$
			}
		}
		// remove extra spaces
//		String realisationString = realisation.toString().replace(" ,", ",").trim();
		String realisationString = realisation.toString().replaceAll(" (?=( |,))", "").trim();
		return new StringElement(realisationString); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Realises a list of elements appending the result to the on-going
	 * realisation.
	 * 
	 * @param realisation
	 *            the <code>StringBuffer<code> containing the current 
	 * 			  realisation of the sentence.
	 * @param components
	 *            the <code>List</code> of <code>NLGElement</code>s representing
	 *            the components that make up the sentence.
	 * @param listSeparator
	 *            the string to use to separate elements of the list, empty if
	 *            no separator needed
	 */
	@Override
	protected void realiseList(StringBuffer realisation,
			List<NLGElement> components, String listSeparator) {

		NLGElement realisedChild = null;
		boolean imperative = false;

		for (int i = 0; i < components.size(); i++) {
			NLGElement thisElement = components.get(i);
			realisedChild = thisElement.realiseOrthography();
			
			// Test on childRealisation added by vaudrypl to prevent
			// unwanted spaces and separators when child is elided.
			String childRealisation = realisedChild.getRealisation();
			if (childRealisation != null && !childRealisation.isEmpty() ) {
				
				realisation.append(childRealisation);
				
				boolean separatorAdded = false;
				Object function = thisElement.getFeature(InternalFeature.DISCOURSE_FUNCTION);
				ElementCategory category = thisElement.getCategory();
				if (components.size() > 1 && i < components.size() - 1) {
					ElementCategory nextCategory = components.get(i+1).getCategory();
					NLGElement parent = thisElement.getParent();
					ElementCategory parentCategory = parent != null ? parent.getCategory() : null;
					if (listSeparator != null && !listSeparator.isEmpty()) {
						realisation.append(listSeparator);
						separatorAdded = true;
					} else if ( parentCategory != PhraseCategory.ADVERB_PHRASE &&
							(category == LexicalCategory.ADVERB
									|| category == PhraseCategory.ADVERB_PHRASE) &&
							(nextCategory == LexicalCategory.ADVERB
									|| nextCategory == PhraseCategory.ADVERB_PHRASE)) {
						realisation.append(",");
						separatorAdded = true;
					}
				}

				if ( !separatorAdded && (DiscourseFunction.FRONT_MODIFIER.equals(function)
							|| DiscourseFunction.CUE_PHRASE.equals(function))
						&& !thisElement.getFeatureAsBoolean(LexicalFeature.NO_COMMA)) {
					realisation.append(",");
					separatorAdded = true;
				}
				
				// put a dash between an imperative verb and its clitic complements
				char lastChar = childRealisation.charAt(childRealisation.length()-1);
				boolean dashAdded = false;
				if (!separatorAdded && i < components.size() - 1) {
					NLGElement nextElement = components.get(i+1);
					if ( (thisElement.isA(LexicalCategory.VERB) || thisElement.isA(LexicalCategory.MODAL)
							|| thisElement.isA(LexicalCategory.AUXILIARY))
							&& thisElement.getFeature(Feature.FORM) == Form.IMPERATIVE) {
						imperative = true;
					}
					
					if (imperative
							&& (nextElement.isA(LexicalCategory.PRONOUN)
									|| nextElement.isA(PhraseCategory.NOUN_PHRASE))
							&& nextElement.getFeatureAsBoolean(FrenchInternalFeature.CLITIC)) {
						if (lastChar != '\'') {
							realisation.append('-');
							dashAdded = true;
						}
					} else {
						imperative = false;
					}
				}
				
				// Insert a space between words, except after an apostrophe or a just added dash
				if (lastChar != '\'' && !dashAdded) realisation.append(' ');
			}
		}
		
		if (realisation.length() > 0 && realisation.toString().endsWith(" ")) {
			realisation.setLength(realisation.length() - 1);
		}
	}
	
	/**
	 * Realise the orthograph of a ListElement.
	 * Based on English orthography processor
	 * 
	 * @param element ListElement to be realised
	 * @return orthographically realised form
	 */
	@Override
	public StringElement realiseListElement(ListElement element) {
		StringElement realisedElement = null;

		if (element != null) {
			ElementCategory category = element.getCategory();

			StringBuffer buffer = new StringBuffer();
			realiseList(buffer, element.getChildren(), "");
			realisedElement = new StringElement(buffer.toString());

			//make the realised element inherit the original category
			//essential if list items are to be properly formatted later
			realisedElement.setCategory(category);
		}

		return realisedElement;
	}

}
