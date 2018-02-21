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
package simplenlg.framework;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.lexicon.Lexicon;
import simplenlg.morphophonology.MorphophonologyRulesInterface;

/**
 * <p>
 * This class defines an element for representing canned text within the
 * SimpleNLG library. Once assigned a value, the string element should not be
 * changed by any other processors.
 * </p>
 * 
 * 
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 * 
 */
public class StringElement extends NLGElement {

	// Morphology rule sets used by realiseMorphophonology() to inflect the word
	// instantiated by getMorphophonologyRuleSet(Language language)
	private static Map<Language, MorphophonologyRulesInterface> morphophonologyRuleSets = null; 

	/**
	 * Constructs a new string element representing some canned text.
	 * 
	 * @param value
	 *            the text for this string element.
	 */
	public StringElement(String value) {
		setCategory(PhraseCategory.CANNED_TEXT);
		setFeature(Feature.ELIDED, false);
		setRealisation(value);
	}

	/**
	 * Constructs a new string element from an InflectedWordElement.
	 * 
	 * @param value
	 *            the text for this string element.
	 * @author vaudrypl
	 */
	public StringElement(String form, InflectedWordElement word) {
		//the StringElement inherits all features from the inflected word
		for(String feature : word.getAllFeatureNames()) {
			setFeature(feature, word.getFeature(feature));
		}
		setCategory(word.getCategory());
		setFeature(Feature.ELIDED, false);
		setRealisation(form);
	}

	/**
	 * The string element contains no children so this method will always return
	 * an empty list.
	 */
	@Override
	public List<NLGElement> getChildren() {
		return new ArrayList<NLGElement>();
	}

	// Based on WordElement.toString()
	// changed by vaudrypl
	@Override
	public String toString() {
		ElementCategory _category = getCategory();
		StringBuffer buffer = new StringBuffer("StringElement["); //$NON-NLS-1$
		buffer.append(getRealisation()).append(':');
		if (_category != null) {
			buffer.append(_category.toString());
		} else {
			buffer.append("no category"); //$NON-NLS-1$
		}
		buffer.append(']');
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see simplenlg.framework.NLGElement#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return super.equals(o) && (o instanceof StringElement) && realisationsMatch((StringElement) o);
	}

	private boolean realisationsMatch(StringElement o) {
		if  (getRealisation() == null) {
			return o.getRealisation() == null;
		}
		else
			return getRealisation().equals(o.getRealisation());
	}

	@Override
	public String printTree(String indent) {
		StringBuffer print = new StringBuffer();
		print
				.append("StringElement: content=\"").append(getRealisation()).append('\"'); //$NON-NLS-1$
		// print category added by vaudrypl
		if (getCategory() != null) {
			print.append(", category=").append(getCategory().toString()); //$NON-NLS-1$
		}
		
		Map<String, Object> features = this.getAllFeatures();
		if (features != null) {
			print.append(", features=").append(features.toString()); //$NON-NLS-1$
		}
		print.append('\n');
		return print.toString();
	}

	/**
	 * @return the lexicon associated with this StringElement's base word
	 * @author vaudrypl
	 */
	public Lexicon getLexicon() {
		Lexicon lexicon = null;
		if ( hasFeature( InternalFeature.BASE_WORD ) ) {
			WordElement baseWord = (WordElement) getFeature( InternalFeature.BASE_WORD );
			lexicon = baseWord.getLexicon();
		}
		return lexicon;
	}
	
	/**
	 * @return the language associated with this StringElement's lexicon
	 * @author vaudrypl
	 */
	public Language getLanguage() {
		Lexicon lexicon = getLexicon();
		if (lexicon == null) return Language.DEFAULT_LANGUAGE;
		else return lexicon.getLanguage();
	}
	
	/**
	 * @return the morphophonology rule set to be used to inflect this word
	 * @author vaudrypl
	 */
	public MorphophonologyRulesInterface getMorphophonologyRuleSet()
	{
		return getMorphophonologyRuleSet( getLanguage() );
	}
	
	/**
	 * This static method returns the morphophonology rule set corresponding to
	 * a particular language and instantiates it if necessary.
	 * 
	 * @param language
	 * @return the morphophonology rule set to be used for this language
	 * @author vaudrypl
	 */
	public static MorphophonologyRulesInterface getMorphophonologyRuleSet(Language language)
	{
		if (morphophonologyRuleSets == null) {
			morphophonologyRuleSets =
				new EnumMap<Language,MorphophonologyRulesInterface>(Language.class); 
		}
		MorphophonologyRulesInterface ruleSet = morphophonologyRuleSets.get(language);
		if (ruleSet == null) {
			switch (language) {
			case ENGLISH:
				ruleSet = new simplenlg.morphophonology.english.MorphophonologyRules();
				break;
			case FRENCH:
				ruleSet = new simplenlg.morphophonology.french.MorphophonologyRules();
				break;
			}
			morphophonologyRuleSets.put(language, ruleSet);
		}
		return ruleSet;
	}

	/**
	 * Realisation method for the morphophonology stage.
	 * 
	 * @param nextElement	the method does nothing if this parameter is null
	 * @return morphophonologically realised form
	 * @author vaudrypl
	 */
	protected NLGElement realiseMorphophonology(NLGElement nextElement)
	{
		if (nextElement instanceof StringElement)
		{
			StringElement rightWord = (StringElement) nextElement;
			MorphophonologyRulesInterface leftWordRules = this.getMorphophonologyRuleSet(),
				rightWordRules = rightWord.getMorphophonologyRuleSet();
			// Use morphophonology rules from the languages of both words
			// if they are different. (Those of the left word first,
			// for no particular reason.)
			leftWordRules.doMorphophonology(this, rightWord);
			if (leftWordRules != rightWordRules) {
				rightWordRules.doMorphophonology(this, rightWord);				
			}
		}
		return this;
	}

	/**
	 * 
	 * @return this StringElement itself
	 * 
	 *         See superclass version in NLGElement.
	 *         
	 * @author vaudrypl
	 */
	@Override
	public StringElement getLeftMostStringElement()
	{
		return this;
	}

	/**
	 * 
	 * @return this StringElement itself
	 * 
	 *         See superclass version in NLGElement.
	 *         
	 * @author vaudrypl
	 */
	@Override
	public StringElement getRightMostStringElement()
	{
		return this;
	}
	
	/**
	 * 
	 * @return this StringElement itself
	 * 
	 *         See superclass version in NLGElement.
	 *         
	 * @author vaudrypl
	 */
	@Override
	public NLGElement getRightMostTerminalElement()
	{
		return this;
	}
}
