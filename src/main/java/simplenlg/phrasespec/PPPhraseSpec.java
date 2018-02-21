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

import java.util.List;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.InternalFeature;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.NLGFactory;

/**
 * <p>
 * This class defines a prepositional phrase.  It is essentially
 * a wrapper around the <code>PhraseElement</code> class, with methods
 * for setting common constituents such as object.
 * For example, the <code>setPreposition</code> method in this class sets
 * the head of the element to be the specified preposition
 *
 * From an API perspective, this class is a simplified version of the PPPhraseSpec
 * class in simplenlg V3.  It provides an alternative way for creating syntactic
 * structures, compared to directly manipulating a V4 <code>PhraseElement</code>.
 * 
 * Methods are provided for setting and getting the following constituents:
 * <UL>
 * <LI>Preposition		(eg, "in")
 * <LI>Object     (eg, "the shop")
 * </UL>
 * 
 * NOTE: PPPhraseSpec do not usually have modifiers or (user-set) features
 * 
 * <code>PPPhraseSpec</code> are produced by the <code>createPrepositionalPhrase</code>
 * method of a <code>PhraseFactory</code>
 * </p>
 * 
 * @author E. Reiter, University of Aberdeen.
 * @version 4.1
 * 
 */
public class PPPhraseSpec extends PhraseElement {

	public PPPhraseSpec(NLGFactory phraseFactory) {
		super(PhraseCategory.PREPOSITIONAL_PHRASE);
		this.setFactory(phraseFactory);
	}
	
	/** sets the preposition (head) of a prepositional phrase
	 * @param preposition
	 */
	public void setPreposition(Object preposition) {
		if (preposition instanceof NLGElement)
			setHead(preposition);
		else {
			// create noun as word
			NLGElement prepositionalElement = getFactory().createWord(preposition, LexicalCategory.PREPOSITION);

			// set head of NP to nounElement
			setHead(prepositionalElement);
		}
	}

	/**
	 * @return preposition (head) of prepositional phrase
	 */
	public NLGElement getPreposition() {
		return getHead();
	}
	
	/** Sets the  object of a PP
	 *
	 * @param object
	 */
	// changed by vaudrypl; code moved to addObject()
	public void setObject(Object object) {
		clearComplements();
		addObject(object);
	}
	public void addObject(Object object) {
		// changed by vaudrypl because of changes in createNounPhrase(object)
		PhraseElement objectPhrase;
		if (object instanceof PhraseElement) {
			objectPhrase = (PhraseElement) object;
		} else {
			objectPhrase = getFactory().createNounPhrase(object);
		}
		
		objectPhrase.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.OBJECT);
		// changed by vaudrypl
		super.addComplement(objectPhrase);
		objectPhrase.setParent(this);
	}
	// added by vaudrypl
	@Override
	public void addComplement(NLGElement newComplement) {
		addObject(newComplement);
	}
	@Override
	public void addComplement(String newComplement) {
		addObject(newComplement);
	}
	@Override
	public void setComplement(NLGElement newComplement) {
		setObject(newComplement);
	}
	@Override
	public void setComplement(String newComplement) {
		setObject(newComplement);
	}	
	
	/**
	 * @return object of PP (assume only one)
	 */
	public NLGElement getObject() {
		List<NLGElement> complements = getFeatureAsElementList(InternalFeature.COMPLEMENTS);
		for (NLGElement complement: complements)
			if (complement.getFeature(InternalFeature.DISCOURSE_FUNCTION) == DiscourseFunction.OBJECT)
				return complement;
		return null;
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
		boolean returnValue = false;
		
		NLGElement object = getObject();
		if (object != null) {
			returnValue = object.checkIfNeOnlyNegation();
		}
		
		return returnValue;
	}
}
