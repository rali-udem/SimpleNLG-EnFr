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

import java.util.List;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.french.FrenchLexicalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.ElementCategory;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.WordElement;
import simplenlg.framework.LexicalCategory;

/**
 * Abstract class for the CoordinatedPhrase syntax helper.
 * Most methods taken from or based on English clause syntax helper.
 * 
 * @author vaudrypl
 */
public abstract class AbstractCoordinatedPhraseHelper {
	/**
	 * The main method for realising coordinated phrases.
	 * 
	 * @param phrase
	 *            the <code>CoordinatedPhrase</code> to be realised.
	 * @return the realised <code>NLGElement</code>.
	 */
	public NLGElement realise(CoordinatedPhraseElement phrase) {
		ListElement realisedElement = null;

		if (phrase != null) {
			// vaudrypl added phrase argument to ListElement constructor
			// to copy all features from the CoordinatedPhraseElement
			realisedElement = new ListElement(phrase);
			PhraseElement.getPhraseHelper(phrase.getLanguage())
				.realiseList(realisedElement, phrase.getPreModifiers(),
					DiscourseFunction.PRE_MODIFIER);
			
			NLGFactory factory = phrase.getFactory();
			CoordinatedPhraseElement coordinated = new CoordinatedPhraseElement(factory);

			List<NLGElement> children = phrase.getChildren();
			
			// conjunction class changed to WordElement by vaudrypl
			WordElement conjunction = phrase.getConjunction();
			coordinated.setConjunction(conjunction);
			coordinated.setFeature(Feature.CONJUNCTION_TYPE, phrase
					.getFeature(Feature.CONJUNCTION_TYPE));

			InflectedWordElement conjunctionElement = null;

			if (children != null && children.size() > 0) {
				if (phrase.getFeatureAsBoolean(Feature.RAISE_SPECIFIER)
						.booleanValue()) {

					raiseSpecifier(children);
				}

				NLGElement child = phrase.getLastCoordinate();
				child.setFeature(Feature.POSSESSIVE, phrase
						.getFeature(Feature.POSSESSIVE));

				// changed by vaudrypl : this loop now starts at index = 0 instead of 1
				for (int index = 0; index < children.size(); index++) {
					child = children.get(index);
					setChildFeatures(phrase, child);
					if (index > 0 && phrase.getFeatureAsBoolean(Feature.AGGREGATE_AUXILIARY)
							.booleanValue()) {
						child.setFeature(InternalFeature.REALISE_AUXILIARY,
								false);
					}

					if (index > 0 && child.isA(PhraseCategory.CLAUSE)) {
						child
								.setFeature(
										Feature.SUPRESSED_COMPLEMENTISER,
										phrase
												.getFeature(Feature.SUPRESSED_COMPLEMENTISER));
					}

					// arguments changed and test added by vaudrypl
					if (index != 0 ||
							conjunction.getFeatureAsBoolean(FrenchLexicalFeature.REPEATED_CONJUNCTION)) {
						conjunctionElement = new InflectedWordElement(conjunction);
						
						conjunctionElement.setFeature(
								InternalFeature.DISCOURSE_FUNCTION,
								DiscourseFunction.CONJUNCTION);
						coordinated.addCoordinate(conjunctionElement);
					}
					coordinated.addCoordinate(child.realiseSyntax());
				}
				realisedElement.addComponent(coordinated);
			}

			PhraseElement.getPhraseHelper(phrase.getLanguage())
				.realiseList(realisedElement, phrase.getPostModifiers(),
						DiscourseFunction.POST_MODIFIER);
			PhraseElement.getPhraseHelper(phrase.getLanguage())
				.realiseList(realisedElement, phrase.getComplements(),
						DiscourseFunction.COMPLEMENT);
		}
		return realisedElement;
	}

	/**
	 * Sets the common features from the phrase to the child element.
	 * modified by vaudrypl
	 * 
	 * @param phrase
	 *            the <code>CoordinatedPhraseElement</code>
	 * @param child
	 *            a single coordinated <code>NLGElement</code> within the
	 *            coordination.
	 */
	protected void setChildFeatures(CoordinatedPhraseElement phrase,
			NLGElement child) {
		ElementCategory category = child.getCategory();
		
		if (phrase.hasFeature(Feature.PROGRESSIVE)) {
			child.setFeature(Feature.PROGRESSIVE, phrase
					.getFeature(Feature.PROGRESSIVE));
		}
		if (phrase.hasFeature(Feature.PERFECT)) {
			child.setFeature(Feature.PERFECT, phrase
					.getFeature(Feature.PERFECT));
		}
		
		if ( !(LexicalCategory.NOUN.equalTo(category) ||
				PhraseCategory.NOUN_PHRASE.equalTo(category)) ) {
			if (phrase.hasFeature(LexicalFeature.GENDER)) {
				child.setFeature(LexicalFeature.GENDER, phrase
						.getFeature(LexicalFeature.GENDER));
			}
		}
		
		if (phrase.hasFeature(Feature.NUMBER)) {
			child.setFeature(Feature.NUMBER, phrase.getFeature(Feature.NUMBER));
		}
		if (phrase.hasFeature(Feature.TENSE)) {
			child.setTense(phrase.getTense());
		}
		if (phrase.hasFeature(Feature.PERSON)) {
			child.setFeature(Feature.PERSON, phrase.getFeature(Feature.PERSON));
		}
		if (phrase.hasFeature(Feature.NEGATED)) {
			child.setNegated(phrase.isNegated());
		}
		if (phrase.hasFeature(Feature.MODAL)) {
			child.setFeature(Feature.MODAL, phrase.getFeature(Feature.MODAL));
		}
		if (phrase.hasFeature(Feature.FORM)) {
			child.setFeature(Feature.FORM, phrase.getFeature(Feature.FORM));
		}
		if (phrase.hasFeature(InternalFeature.SPECIFIER)) {
			child.setFeature(InternalFeature.SPECIFIER, phrase
					.getFeature(InternalFeature.SPECIFIER));
		}
		if (phrase.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
			child.setFeature(InternalFeature.DISCOURSE_FUNCTION, phrase
					.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		}
		if (phrase.hasFeature(InternalFeature.CLAUSE_STATUS)) {
			child.setFeature(InternalFeature.CLAUSE_STATUS, phrase
					.getFeature(InternalFeature.CLAUSE_STATUS));
		}
		if (phrase.hasFeature(Feature.INTERROGATIVE_TYPE)) {
			child.setFeature(InternalFeature.IGNORE_MODAL, true);
		}
	}

	/**
	 * Checks to see if the specifier can be raised and then raises it.
	 * 
	 * @param children
	 *            the <code>List</code> of coordinates in the
	 *            <code>CoordinatedPhraseElement</code>
	 */
	abstract protected void raiseSpecifier(List<NLGElement> children);

}
