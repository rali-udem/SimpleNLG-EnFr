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
package simplenlg.orthography.english;

import java.util.List;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.ElementCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.StringElement;

import simplenlg.orthography.OrthographyHelperInterface;

/**
 * <p>
 * This processing module deals with punctuation when applied to
 * <code>DocumentElement</code>s for English. The punctuation currently handled by this
 * processor includes the following (as of version 4.0):
 * <ul>
 * <li>Capitalisation of the first letter in sentences.</li>
 * <li>Termination of sentences with a period if not interrogative.</li>
 * <li>Termination of sentences with a question mark if they are interrogative.</li>
 * <li>Replacement of multiple conjunctions with a comma. For example,
 * <em>John and Peter and Simon</em> becomes <em>John, Peter and Simon</em>.</li>
 * </ul>
 * </p>
 * 
 * 
 * Most of the code taken from OrthographyProcessor by D. Westwater, University of Aberdeen.
 * Transformed to implement OrthographyHelperInterface.
 * 
 * @author vaudrypl
 * 
 */
public class OrthographyHelper implements OrthographyHelperInterface {

	/**
	 * Realise the orthograph of a ListElement.
	 * 
	 * @param element ListElement to be realised
	 * @return orthographically realised form
	 */
	@Override
	public StringElement realiseListElement(ListElement element) {
		StringElement realisedElement = null;

		if (element != null) {
			ElementCategory category = element.getCategory();

			// AG: changes here: if we have a premodifier, then we ask the
			// realiseList method to separate with a comma.
			StringBuffer buffer = new StringBuffer();
			List<NLGElement> children = element.getChildren();
			Object function = children.isEmpty() ? null : children.get(0)
					.getFeature(InternalFeature.DISCOURSE_FUNCTION);

            if (!children.isEmpty()
                    && DiscourseFunction.PRE_MODIFIER.equals(children.get(0).getFeature(InternalFeature.DISCOURSE_FUNCTION))
                    && !children.get(0).getFeatureAsBoolean(LexicalFeature.NO_COMMA)) {
				realiseList(buffer, element.getChildren(), ",");
			} else {
				realiseList(buffer, element.getChildren(), "");
			}

			// realiseList(buffer, element.getChildren(), "");
			realisedElement = new StringElement(buffer.toString());

			//make the realised element inherit the original category
			//essential if list items are to be properly formatted later
			realisedElement.setCategory(category);
		}

		return realisedElement;
	}

	/**
	 * Performs the realisation on a sentence. This includes adding the
	 * terminator and capitalising the first letter.
	 * 
	 * @param components
	 *            the <code>List</code> of <code>NLGElement</code>s representing
	 *            the components that make up the sentence.
	 * @param element
	 *            the <code>NLGElement</code> representing the sentence.
	 * @return the realised element as an <code>NLGElement</code>.
	 */
	@Override
	public NLGElement realiseSentence(List<NLGElement> components,
			NLGElement element) {

		NLGElement realisedElement = null;
		if (components != null && components.size() > 0) {
			StringBuffer realisation = new StringBuffer();
			realiseList(realisation, components, "");

			if (realisation.length() > 0) {
				capitaliseFirstLetter(realisation);
				terminateSentence(realisation, element.getFeatureAsBoolean(
						InternalFeature.INTERROGATIVE).booleanValue());
				realisation.append(' ');
			}

			((DocumentElement) element).clearComponents();
			element.setRealisation(realisation.toString());
			realisedElement = element;
		}
		return realisedElement;
	}

	/**
	 * Adds the sentence terminator to the sentence. This is a period ('.') for
	 * normal sentences or a question mark ('?') for interrogatives.
	 * 
	 * @param realisation
	 *            the <code>StringBuffer<code> containing the current 
	 * realisation of the sentence.
	 * @param interrogative
	 *            a <code>boolean</code> flag showing <code>true</code> if the
	 *            sentence is an interrogative, <code>false</code> otherwise.
	 */
	protected void terminateSentence(StringBuffer realisation,
			boolean interrogative) {
		char character = realisation.charAt(realisation.length() - 2);
		if (character != '.' && character != '?') {
			if (interrogative) {
				realisation.append('?');
			} else {
				realisation.append('.');
			}
		}
	}

	/**
	 * Capitalises the first character of a sentence if it is a lower case
	 * letter.
	 * 
	 * @param realisation
	 *            the <code>StringBuffer<code> containing the current 
	 * realisation of the sentence.
	 */
	protected void capitaliseFirstLetter(StringBuffer realisation) {
		char character = realisation.charAt(0);
		if (character >= 'a' && character <= 'z') {
			character = (char) ('A' + (character - 'a'));
			realisation.setCharAt(0, character);
		// added by vaudrypl
		} else if (character >= 'à' && character <= 'ý') {
			character = (char) ('À' + (character - 'à'));
			realisation.setCharAt(0, character);
		}
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
	protected void realiseList(StringBuffer realisation,
			List<NLGElement> components, String listSeparator) {

		NLGElement realisedChild = null;

		for (int i = 0; i < components.size(); i++) {
			NLGElement thisElement = components.get(i);
				realisedChild = thisElement.realiseOrthography();
				
				// Test on childRealisation added by vaudrypl to prevent
				// unwanted spaces and separators when child is elided.
				String childRealisation = realisedChild.getRealisation();
				if (childRealisation != null && !childRealisation.isEmpty() ) {
					
					realisation.append(childRealisation);
		
					if (components.size() > 1 && i < components.size() - 1) {
						realisation.append(listSeparator);
					}
		
					realisation.append(' ');
				}
		}
		
		if (realisation.length() > 0) {
			realisation.setLength(realisation.length() - 1);
		}
	}

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
			if (index < length - 2
					&& DiscourseFunction.CONJUNCTION.equals(realisedChild
							.getFeature(InternalFeature.DISCOURSE_FUNCTION))) {
				if (index > 0) realisation.append(", "); //$NON-NLS-1$
			} else {
				realisedChild = realisedChild.realiseOrthography();
				realisation.append(realisedChild.getRealisation()).append(' ');
			}
		}
		// modified by vaudrypl
		// remove extra spaces
//		realisation.setLength(realisation.length() - 1);
		String realisationString = realisation.toString().replaceAll(" (?=( |,))", "").trim();
		return new StringElement(realisationString); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
