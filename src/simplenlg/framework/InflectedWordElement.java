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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.lexicon.Lexicon;
import simplenlg.morphology.MorphologyRulesInterface;

/**
 * <p>
 * This class defines the <code>NLGElement</code> that is used to represent an
 * word that requires inflection by the morphology. It has convenience methods
 * for retrieving the base form of the word (for example, <em>kiss</em>,
 * <em>eat</em>) and for setting and retrieving the base word. The base word is
 * a <code>WordElement</code> constructed by the lexicon.
 * </p>
 * 
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 * 
 */
public class InflectedWordElement extends NLGElement {
	
	// Morphology rule sets used by realiseMorphology() to inflect the word
	// instantiated by getMorphologyRuleSet(Language language)
	private static Map<Language, MorphologyRulesInterface> morphologyRuleSets = null; 

	/**
	 * Constructs a new inflected word using the giving word as the base form.
	 * Constructing the word also requires a lexical category (such as noun,
	 * verb).
	 * 
	 * @param word
	 *            the base form for this inflected word.
	 * @param category
	 *            the lexical category for the word.
	 */
	public InflectedWordElement(String word, LexicalCategory category) {
		super();
		setFeature(LexicalFeature.BASE_FORM, word);
		setCategory(category);
	}

	/**
	 * Constructs a new inflected word from a WordElement
	 * 
	 * @param word
	 *            underlying wordelement
	 */
	public InflectedWordElement(WordElement word) {
		super();
		// vaudrypl added null test
		if (word != null) {
			//the inflected word inherits all features from the base word
			// (moved from WordElement.realiseSyntax())
			for(String feature : word.getAllFeatureNames()) {
				setFeature(feature, word.getFeature(feature));
			}
			
			setFeature(InternalFeature.BASE_WORD, word);
			// AG: changed to use the default spelling variant
			// setFeature(LexicalFeature.BASE_FORM, word.getBaseForm());
			String defaultSpelling = word.getDefaultSpellingVariant();
			setFeature(LexicalFeature.BASE_FORM, defaultSpelling);
			setCategory(word.getCategory());
		} else {
			setCategory(LexicalCategory.ANY);
		}
	}

	/**
	 * @return the lexicon associated with this inflected word's base word
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
	 * @return the language associated with this inflected word's lexicon
	 * @author vaudrypl
	 */
	@Override
	public Language getLanguage() {
		Lexicon lexicon = getLexicon();
		if (lexicon == null) return Language.DEFAULT_LANGUAGE;
		else return lexicon.getLanguage();
	}
	
	/**
	 * @return the morphology rule set to be used to inflect this word
	 * @author vaudrypl
	 */
	public MorphologyRulesInterface getMorphologyRuleSet()
	{
		return getMorphologyRuleSet( getLanguage() );
	}
	
	/**
	 * This static method returns the morphology rule set corresponding to
	 * a particular language and instantiates it if necessary.
	 * 
	 * @param language
	 * @return the morphology rule set to be used for this language
	 * @author vaudrypl
	 */
	public static MorphologyRulesInterface getMorphologyRuleSet(Language language)
	{
		if (morphologyRuleSets == null) {
			morphologyRuleSets =
				new EnumMap<Language,MorphologyRulesInterface>(Language.class); 
		}
		MorphologyRulesInterface ruleSet = morphologyRuleSets.get(language);
		if (ruleSet == null) {
			switch (language) {
			case ENGLISH:
				ruleSet = new simplenlg.morphology.english.NonStaticMorphologyRules();
				break;
			case FRENCH:
				ruleSet = new simplenlg.morphology.french.MorphologyRules();
				break;
			}
			morphologyRuleSets.put(language, ruleSet);
		}
		return ruleSet;
	}

	/**
	 * This method returns null as the inflected word has no child components.
	 */
	@Override
	public List<NLGElement> getChildren() {
		return null;
	}

	@Override
	public String toString() {
		return "InflectedWordElement[" + getBaseForm() + ':' //$NON-NLS-1$
				+ getCategory().toString() + ']';
	}

	@Override
	public String printTree(String indent) {
		StringBuffer print = new StringBuffer();
		print.append("InflectedWordElement: base=").append(getBaseForm()) //$NON-NLS-1$
				.append(", category=").append(getCategory().toString()).append( //$NON-NLS-1$
						", ").append(super.toString()).append('\n'); //$NON-NLS-1$
		return print.toString();
	}

	/**
	 * Retrieves the base form for this element. The base form is the originally
	 * supplied word.
	 * 
	 * @return a <code>String</code> forming the base form of the element.
	 */
	public String getBaseForm() {
		return getFeatureAsString(LexicalFeature.BASE_FORM);
	}

	/**
	 * Sets the base word for this element.
	 * 
	 * @param word
	 *            the <code>WordElement</code> representing the base word as
	 *            read from the lexicon.
	 */
	public void setBaseWord(WordElement word) {
		setFeature(InternalFeature.BASE_WORD, word);
	}

	/**
	 * Retrieves the base word for this element.
	 * 
	 * @return the <code>WordElement</code> representing the base word as read
	 *         from the lexicon.
	 */
	public WordElement getBaseWord() {
		NLGElement baseWord = this
				.getFeatureAsElement(InternalFeature.BASE_WORD);
		return baseWord instanceof WordElement ? (WordElement) baseWord : null;
	}

	/**
	 * Realisation method for the syntax stage.
	 * based on english SyntaxProcessor
	 * 
	 * @return syntactically realised form
	 * @author vaudrypl
	 */
	public NLGElement realiseSyntax()
	{
		if (getFeatureAsBoolean(Feature.ELIDED).booleanValue()) {
			return null;
		}
		
		// Gets the baseWord in the lexicon if the
		// inflected word doesn't already have one.

		String baseForm = getBaseForm();
		ElementCategory category = getCategory();

		if (getLexicon() != null && baseForm != null) {
			WordElement word = getBaseWord();
		
			if (word == null) {
				if (category instanceof LexicalCategory) {
					word = getLexicon().lookupWord(baseForm,
							(LexicalCategory) category);
				} else {
					word = getLexicon().lookupWord(baseForm);
				}
			}
		
			if (word != null) setBaseWord(word);
		}
		
		return this;
	}

	/**
	 * Realisation method for the morphology stage.
	 * Based on the doMorphology() method from
	 * simplenlg.morphology.english.MorphologyProcessor.
	 * Call to modified doDeterminerMorphology() method
	 * added.
	 * 
	 * @return morphologically realised form
	 * @author vaudrypl
	 */
	public NLGElement realiseMorphology()
	{
		NLGElement realisedElement = null;
		if (getFeatureAsBoolean(InternalFeature.NON_MORPH)
				.booleanValue()) {
			// vaudrypl added 'this' as second argument
			realisedElement = new StringElement(getBaseForm(), this);
			realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
					getFeature(InternalFeature.DISCOURSE_FUNCTION));
		} else {
			NLGElement baseWord = getFeatureAsElement(InternalFeature.BASE_WORD);

			Lexicon lexicon = getLexicon();
			if (baseWord == null && lexicon != null) {
				baseWord = lexicon.lookupWord(getBaseForm());
			}
			ElementCategory category = getCategory();
			if (category instanceof LexicalCategory) {
				MorphologyRulesInterface ruleSet = getMorphologyRuleSet();
				switch ((LexicalCategory) category) {
				case PRONOUN:
					realisedElement = ruleSet.doPronounMorphology(this);
					break;

				case NOUN:
					realisedElement = ruleSet.doNounMorphology(this,
							(WordElement) baseWord);
					break;

				case VERB:
					realisedElement = ruleSet.doVerbMorphology(this,
							(WordElement) baseWord);
					break;

				case ADJECTIVE:
					realisedElement = ruleSet.doAdjectiveMorphology(
							this, (WordElement) baseWord);
					break;

				case ADVERB:
					realisedElement = ruleSet.doAdverbMorphology(
							this, (WordElement) baseWord);
					break;
				
				case DETERMINER:
					realisedElement = ruleSet.doDeterminerMorphology(this);
					break;

				default:
					// vaudrypl added 'this' as second argument
					realisedElement = new StringElement(getBaseForm(), this);
					realisedElement
							.setFeature(
									InternalFeature.DISCOURSE_FUNCTION,
									getFeature(InternalFeature.DISCOURSE_FUNCTION));
				}
			}
		}
		return realisedElement;
	}

	/**
	 * 
	 * @return this InflectedWordElement itself
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
