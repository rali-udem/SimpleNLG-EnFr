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

package simplenlg.morphology;

import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;

/**
 * Interface used by the abstract morphology processor
 * to call morphology rules specific to each language.
 * 
 * @author vaudrypl
 *
 */
public interface MorphologyRulesInterface {

	/**
	 * This method performs the morphology for nouns.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public StringElement doNounMorphology( InflectedWordElement element,
			WordElement baseWord);

	/**
	 * This method performs the morphology for verbs.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public NLGElement doVerbMorphology( InflectedWordElement element,
			WordElement baseWord);

	/**
	 * This method performs the morphology for adjectives.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public NLGElement doAdjectiveMorphology( InflectedWordElement element,
			WordElement baseWord);

	/**
	 * This method performs the morphology for adverbs.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public NLGElement doAdverbMorphology(InflectedWordElement element,
			WordElement baseWord);

	/**
	 * This method performs the morphology for pronouns.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public NLGElement doPronounMorphology(InflectedWordElement element);

	/**
	 * This method performs the morphology for determiners.
	 * Modified from the same method in the
	 * simplenlg.morphology.english.MorphologyRules class.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 */
	public NLGElement doDeterminerMorphology(InflectedWordElement element);
}
