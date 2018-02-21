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

package simplenlg.phrasespec;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.Person;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;

/**
 * <p>
 * This class defines a noun phrase. It is essentially a wrapper around the
 * <code>PhraseElement</code> class, with methods for setting common
 * constituents such as specifier. For example, the <code>setNoun</code> method
 * in this class sets the head of the element to be the specified noun
 * 
 * From an API perspective, this class is a simplified version of the
 * NPPhraseSpec class in simplenlg V3. It provides an alternative way for
 * creating syntactic structures, compared to directly manipulating a V4
 * <code>PhraseElement</code>.
 * 
 * Methods are provided for setting and getting the following constituents:
 * <UL>
 * <li>Specifier (eg, "the")
 * <LI>PreModifier (eg, "green")
 * <LI>Noun (eg, "apple")
 * <LI>PostModifier (eg, "in the shop")
 * </UL>
 * 
 * NOTE: The setModifier method will attempt to automatically determine whether
 * a modifier should be expressed as a PreModifier, or PostModifier
 * 
 * NOTE: Specifiers are currently pretty basic, this needs more development
 * 
 * Features (such as number) must be accessed via the <code>setFeature</code>
 * and <code>getFeature</code> methods (inherited from <code>NLGElement</code>).
 * Features which are often set on NPPhraseSpec include
 * <UL>
 * <LI>Number (eg, "the apple" vs "the apples")
 * <LI>Possessive (eg, "John" vs "John's")
 * <LI>Pronominal (eg, "the apple" vs "it")
 * </UL>
 * 
 * <code>NPPhraseSpec</code> are produced by the <code>createNounPhrase</code>
 * method of a <code>PhraseFactory</code>
 * </p>
 * @author E. Reiter, University of Aberdeen.
 * @version 4.1
 * 
 */
public class NPPhraseSpec extends PhraseElement {

	public NPPhraseSpec(NLGFactory phraseFactory) {
		super(PhraseCategory.NOUN_PHRASE);
		this.setFactory(phraseFactory);
	}
	
	/**
	 * Copy constructor
	 * @param original
	 * @author vaudrypl
	 */
	public NPPhraseSpec(NPPhraseSpec original) {
		super(PhraseCategory.NOUN_PHRASE);
		this.setFactory(original.getFactory());
		this.setParent(original.getParent());
		for(String feature : original.getAllFeatureNames()) {
			setFeature(feature, original.getFeature(feature));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.framework.PhraseElement#setHead(java.lang.Object) This
	 * version sets NP default features from the head
	 */
	@Override
	public void setHead(Object newHead) {
		super.setHead(newHead);
		setNounPhraseFeatures(getFeatureAsElement(InternalFeature.HEAD));
	}

	/**
	 * A helper method to set the features required for noun phrases, from the
	 * head noun
	 * 
	 * @param phraseElement
	 *            the phrase element.
	 * @param nounElement
	 *            the element representing the noun.
	 */
	private void setNounPhraseFeatures(NLGElement nounElement) {
		if (nounElement == null)
			return;

		setFeature(Feature.POSSESSIVE, nounElement != null ? nounElement
				.getFeatureAsBoolean(Feature.POSSESSIVE) : Boolean.FALSE);
		setFeature(InternalFeature.RAISED, false);
		setFeature(InternalFeature.ACRONYM, false);

		if (nounElement != null && nounElement.hasFeature(Feature.NUMBER)) {

			setFeature(Feature.NUMBER, nounElement.getFeature(Feature.NUMBER));
		} else {
			setPlural(false);
		}
		if (nounElement != null && nounElement.hasFeature(Feature.PERSON)) {

			setFeature(Feature.PERSON, nounElement.getFeature(Feature.PERSON));
		} else {
			setFeature(Feature.PERSON, Person.THIRD);
		}
		if (nounElement != null
				&& nounElement.hasFeature(LexicalFeature.GENDER)) {

			setFeature(LexicalFeature.GENDER, nounElement
					.getFeature(LexicalFeature.GENDER));
		// commented out by vaudrypl to make french new nouns work
//		} else {
//			setFeature(LexicalFeature.GENDER, Gender.NEUTER);
		}

		if (nounElement != null
				&& nounElement.hasFeature(LexicalFeature.EXPLETIVE_SUBJECT)) {

			setFeature(LexicalFeature.EXPLETIVE_SUBJECT, nounElement
					.getFeature(LexicalFeature.EXPLETIVE_SUBJECT));
		}

		// added by vaudrypl
		if (nounElement != null
				&& nounElement.getFeatureAsBoolean(LexicalFeature.REFLEXIVE)) {
			setFeature(LexicalFeature.REFLEXIVE, true);
		} else {
			setFeature(LexicalFeature.REFLEXIVE, false);
		}
		if (nounElement != null
				&& nounElement.getFeatureAsBoolean(LexicalFeature.PROPER)) {
			setFeature(LexicalFeature.PROPER, true);
		} else {
			setFeature(LexicalFeature.PROPER, false);
		}

		setFeature(Feature.ADJECTIVE_ORDERING, true);
	}

	/**
	 * sets the noun (head) of a noun phrase
	 * 
	 * @param noun
	 */
	public void setNoun(Object noun) {
		NLGElement nounElement = getFactory().createNLGElement(noun,
				LexicalCategory.NOUN);
		setHead(nounElement);
	}

	/**
	 * Sets the pronoun (head) of a noun phrase.
	 * Removes any specifier by default.
	 * 
	 * @param noun
	 * 
	 * @author vaudrypl
	 */
	public void setPronoun(Object pronoun) {
		NLGElement pronounElement = getFactory().createNLGElement(pronoun,
				LexicalCategory.PRONOUN);
		setHead(pronounElement);
	}

	/**
	 * @return noun (head) of noun phrase
	 */
	public NLGElement getNoun() {
		return getHead();
	}

	/**
	 * sets the specifier of a noun phrase. Can be determiner (eg "the"),
	 * possessive (eg, "John's")
	 * 
	 * @param specifier
	 */
	public void setSpecifier(Object specifier) {
		NLGElement specifierElement;
		if (specifier instanceof NLGElement) {
			specifierElement = (NLGElement) specifier;
		} else {
			// create specifier as word (assume determiner)
			specifierElement = getFactory().createWord(specifier,
					LexicalCategory.DETERMINER);
		}

		// set specifier feature
		setFeature(InternalFeature.SPECIFIER, specifierElement);
		if (specifierElement != null) {
			specifierElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
					DiscourseFunction.SPECIFIER);
			
			// added by vaudrypl
			specifierElement.setParent(this);
			NLGElement head = getHead();
			if (head instanceof WordElement && head.isA(LexicalCategory.PRONOUN)) {
				String baseForm = ((WordElement)head).getBaseForm();
				head = getLexicon().lookupWord(baseForm, LexicalCategory.NOUN);
				setNoun(head);
			}
			
			if (specifierElement.hasFeature(Feature.NUMBER)) {
				setFeature(Feature.NUMBER, specifierElement.getFeature(Feature.NUMBER));
			}
		}
	}

	/**
	 * @return specifier (eg, determiner) of noun phrase
	 */
	public NLGElement getSpecifier() {
		return getFeatureAsElement(InternalFeature.SPECIFIER);
	}

	/**
	 * Add a modifier to an NP Use heuristics to decide where it goes
	 * 
	 * @param modifier
	 */
	@Override
	public void addModifier(Object modifier) {
		getNounPhraseHelper().addModifier(this, modifier);
	}
	
	/**
	 * Adds a new pre-modifier to the phrase element.
	 * 
	 * @param newPreModifier
	 *            the new pre-modifier as a <code>String</code>. It is used to
	 *            create a <code>StringElement</code>.
	 * @author vaudrypl
	 */
	public void addPreModifier(String newPreModifier) {
		Lexicon lexicon = getLexicon();
		NLGElement newElement;
		if (lexicon.hasWord(newPreModifier)) {
			newElement = lexicon.lookupWord(newPreModifier);
		}
		else {
			newElement = getFactory().createNLGElement(newPreModifier,
					LexicalCategory.ADJECTIVE);
		}
		addPreModifier(newElement);
	}

	/**
	 * Checks if this element must provoke a negation, but with only
	 * the adverb "ne", in French.
	 * 
	 * @return true if the element provokes a negation with only "ne"
	 * 
	 * @author vaudrypl
	 */
	@Override
	public boolean checkIfNeOnlyNegation() {
		boolean specifierNeNegation = false;
		NLGElement specifier = getSpecifier();
		if (specifier != null) {
			specifierNeNegation = specifier.checkIfNeOnlyNegation();
		}
		
		boolean headNeNegation = false;
		NLGElement head = getHead();
		if (head != null) {
			headNeNegation = head.checkIfNeOnlyNegation();
		}
		
		return specifierNeNegation || headNeNegation;
	}
}
