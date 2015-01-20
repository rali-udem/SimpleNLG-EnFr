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

package simplenlg.syntax;

import java.util.Stack;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.features.InterrogativeType;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * Abstract class for the VerbPhrase syntax helper.
 * Based on English VerbPhrase syntax helper.
 * 
 * @author vaudrypl
 */
public abstract class AbstractVerbPhraseHelper {
	/**
	 * The main method for realising verb phrases.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> to be realised.
	 * @return the realised <code>NLGElement</code>.
	 */
	public NLGElement realise(PhraseElement phrase) {
		ListElement realisedElement = null;
		Stack<NLGElement> vgComponents = null;
		Stack<NLGElement> mainVerbRealisation = new Stack<NLGElement>();
		Stack<NLGElement> auxiliaryRealisation = new Stack<NLGElement>();

		if (phrase != null) {
			vgComponents = createVerbGroup(phrase);
			splitVerbGroup(vgComponents, mainVerbRealisation,
					auxiliaryRealisation);

			// vaudrypl added phrase argument to ListElement constructor
			// to copy all features from the PhraseElement
			realisedElement = new ListElement(phrase);

			if (!phrase.hasFeature(InternalFeature.REALISE_AUXILIARY)
					|| phrase.getFeatureAsBoolean(InternalFeature.REALISE_AUXILIARY)
							.booleanValue()) {

				realiseAuxiliaries(realisedElement,
						auxiliaryRealisation);

				phrase.getPhraseHelper().realiseList(realisedElement, phrase
						.getPreModifiers(), DiscourseFunction.PRE_MODIFIER);

				realiseMainVerb(phrase, mainVerbRealisation,
						realisedElement);
				
			} else if (isCopular(phrase.getHead())) {
				realiseMainVerb(phrase, mainVerbRealisation,
						realisedElement);
				phrase.getPhraseHelper().realiseList(realisedElement, phrase
						.getPreModifiers(), DiscourseFunction.PRE_MODIFIER);
			
			} else {
				phrase.getPhraseHelper().realiseList(realisedElement, phrase
						.getPreModifiers(), DiscourseFunction.PRE_MODIFIER);
				realiseMainVerb(phrase, mainVerbRealisation,
						realisedElement);
			}
			realiseComplements(phrase, realisedElement);
			phrase.getPhraseHelper().realiseList(realisedElement, phrase
					.getPostModifiers(), DiscourseFunction.POST_MODIFIER);
		}
		
		return realisedElement;
	}

	/**
	 * Creates a stack of verbs for the verb phrase. Additional auxiliary verbs
	 * are added as required based on the features of the verb phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @return the verb group as a <code>Stack</code> of <code>NLGElement</code>
	 *         s.
	 */
	abstract protected Stack<NLGElement> createVerbGroup(PhraseElement phrase);

	/**
	 * Splits the stack of verb components into two sections. One being the verb
	 * associated with the main verb group, the other being associated with the
	 * auxiliary verb group.
	 * 
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param mainVerbRealisation
	 *            the main group of verbs.
	 * @param auxiliaryRealisation
	 *            the auxiliary group of verbs.
	 */
	abstract protected void splitVerbGroup(Stack<NLGElement> vgComponents,
			Stack<NLGElement> mainVerbRealisation,
			Stack<NLGElement> auxiliaryRealisation);

	/**
	 * Realises the auxiliary verbs in the verb group.
	 * 
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 * @param auxiliaryRealisation
	 *            the stack of auxiliary verbs.
	 */
	protected void realiseAuxiliaries(ListElement realisedElement,
			Stack<NLGElement> auxiliaryRealisation) {

		NLGElement aux = null;
		NLGElement currentElement = null;
		while (!auxiliaryRealisation.isEmpty()) {
			aux = auxiliaryRealisation.pop();
			currentElement = aux.realiseSyntax();
			if (currentElement != null) {
				realisedElement.addComponent(currentElement);
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						DiscourseFunction.AUXILIARY);
			}
		}
	}

	/**
	 * Realises the main group of verbs in the phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param mainVerbRealisation
	 *            the stack of the main verbs in the phrase.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	protected void realiseMainVerb(PhraseElement phrase,
			Stack<NLGElement> mainVerbRealisation,
			ListElement realisedElement) {

		NLGElement currentElement = null;
		NLGElement main = null;

		while (!mainVerbRealisation.isEmpty()) {
			main = mainVerbRealisation.pop();
			main.setFeature(Feature.INTERROGATIVE_TYPE, phrase
					.getFeature(Feature.INTERROGATIVE_TYPE));
			currentElement = main.realiseSyntax();
			
			if (currentElement != null) {
				realisedElement.addComponent(currentElement);
			}
		}
	}

	/**
	 * Realises the complements of this phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	protected void realiseComplements(PhraseElement phrase,
			ListElement realisedElement) {

		ListElement indirects = new ListElement();
		ListElement directs = new ListElement();
		ListElement unknowns = new ListElement();
		Object discourseValue = null;
		NLGElement currentElement = null;

		for (NLGElement complement : phrase
				.getFeatureAsElementList(InternalFeature.COMPLEMENTS)) {

			discourseValue = complement.getFeature(InternalFeature.DISCOURSE_FUNCTION);
			// added by vaudrypl
			if (!(discourseValue instanceof DiscourseFunction)) {
				discourseValue = DiscourseFunction.COMPLEMENT;
			}
			
			currentElement = complement.realiseSyntax();
			if (currentElement != null) {
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						// changed by vaudrypl
						// DiscourseFunction.COMPLEMENT);
						discourseValue);

				if (DiscourseFunction.INDIRECT_OBJECT.equals(discourseValue)) {
					indirects.addComponent(currentElement);
				} else if (DiscourseFunction.OBJECT.equals(discourseValue)) {
					directs.addComponent(currentElement);
				} else {
					unknowns.addComponent(currentElement);
				}
			}
		}
		if (!InterrogativeType.isIndirectObject(phrase
				.getFeature(Feature.INTERROGATIVE_TYPE))) {
			realisedElement.addComponents(indirects.getChildren());
		}
		if (!phrase.getFeatureAsBoolean(Feature.PASSIVE).booleanValue()) {
			if (!InterrogativeType.isObject(phrase
					.getFeature(Feature.INTERROGATIVE_TYPE))) {
				realisedElement.addComponents(directs.getChildren());
			}
			realisedElement.addComponents(unknowns.getChildren());
		}
	}

	/**
	 * Checks to see if the base form of the word is copular, i.e. <em>be</em>.
	 * 
	 * @param element
	 *            the element to be checked
	 * @return <code>true</code> if the element is copular.
	 */
	abstract public boolean isCopular(NLGElement element);
	
	/**
	 * Add a modifier to a verb phrase. Use heuristics to decide where it goes.
	 * 
	 * @param verbPhrase
	 * @param modifier
	 * 
	 */
	abstract public void addModifier(VPPhraseSpec verbPhrase, Object modifier);
}
