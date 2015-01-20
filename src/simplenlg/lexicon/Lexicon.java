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
package simplenlg.lexicon;

import java.util.List;
import java.util.Map;

import simplenlg.framework.Language;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;

/**
 * This is the generic abstract class for a Lexicon. In simplenlg V4, a
 * <code>Lexicon</code> is a collection of
 * {@link simplenlg.framework.WordElement} objects; it does not do any
 * morphological processing (as was the case in simplenlg V3). Information about
 * <code>WordElement</code> can be obtained from a database (
 * {@link simplenlg.lexicon.NIHDBLexicon}) or from an XML file (
 * {@link simplenlg.lexicon.XMLLexicon}). Simplenlg V4 comes with a default
 * (XML) lexicon, which is retrieved by the <code>getDefaultLexicon</code>
 * method.
 * 
 * There are several ways of retrieving words. If in doubt, use
 * <code>lookupWord</code>. More control is available from the
 * <code>getXXXX</code> methods, which allow words to retrieved in several ways
 * <OL>
 * <LI>baseform and {@link simplenlg.framework.LexicalCategory}; for example
 * "university" and <code>Noun</code>
 * <LI>just baseform; for example, "university"
 * <LI>ID string (if this is supported by the underlying DB or XML file); for
 * example "E0063257" is the ID for "university" in the NIH Specialist lexicon
 * <LI>variant; this looks for a word given a form of the word which may be
 * inflected (eg, "universities") or a spelling variant (eg, "color" for
 * "colour"). Acronyms are not considered to be variants (eg, "UK" and
 * "United Kingdom" are regarded as different words). <br>
 * <I>Note:</I> variant lookup is not guaranteed, this is a feature which
 * hopefully will develop over time
 * <LI>variant and {@link simplenlg.framework.LexicalCategory}; for example
 * "universities" and <code>Noun</code>
 * </OL>
 * 
 * For each type of lookup, there are three methods
 * <UL>
 * <LI> <code>getWords</code>: get all matching
 * {@link simplenlg.framework.WordElement} in the Lexicon. For example,
 * <code>getWords("dog")</code> would return a <code>List</code> of two
 * <code>WordElement</code>, one for the noun "dog" and one for the verb "dog".
 * If there are no matching entries in the lexicon, this method returns an empty
 * collection
 * <LI> <code>getWord</code>: get a single matching
 * {@link simplenlg.framework.WordElement} in the Lexicon. For example,
 * <code>getWord("dog")</code> would a <code> for either the noun "dog" or the
 * verb "dog" (unpredictable).   If there are no matching entries in
 * the lexicon, this method will create a default <code>WordElement</code> based
 * on the information specified.
 * <LI> <code>hasWord</code>: returns <code>true</code> if the Lexicon contains
 * at least one matching <code>WordElement</code>
 * </UL>
 * 
 * @author Albert Gatt (simplenlg v3 lexicon)
 * @author Ehud Reiter (simplenlg v4 lexicon)
 */

public abstract class Lexicon {

	// The language of this lexicon.
	// added by vaudrypl 
	private final Language language;
	
	/****************************************************************************/
	// constructors and related
	/****************************************************************************/

	/**
	 * Creates a new lexicon with default associated language.
	 * @author vaudrypl
	 */
	public Lexicon() {
		this(Language.DEFAULT_LANGUAGE);
	}

	/**
	 * Creates a new lexicon with the associated language.
	 * 
	 * @param language
	 *            the associated language
	 * @author vaudrypl
	 */
	public Lexicon(Language newLanguage) {
		if (newLanguage == null) newLanguage = Language.DEFAULT_LANGUAGE;
		this.language = newLanguage;
	}

	/**
	 * Creates a new lexicon with the associated language
	 * ISO 639-1 two letter code.
	 * 
	 * @param language
	 *            the ISO 639-1 two letter code of the language
	 * @author vaudrypl
	 */
	public Lexicon(String newLanguageCode) {
		this( Language.convertCodeToLanguage( newLanguageCode ) );
	}

	/**
	 * Gets the language used by this factory.
	 * 
	 * @author vaudrypl
	 */
	public Language getLanguage() {
		return this.language;
	}

	/**
	 * returns the default built-in lexicon
	 * 
	 * @return default lexicon
	 */
	public static Lexicon getDefaultLexicon() {
		return new simplenlg.lexicon.english.XMLLexicon();
	}

	/**
	 * create a default WordElement. May be overridden by specific types of
	 * lexicon
	 * 
	 * @param baseForm
	 *            - base form of word
	 * @param category
	 *            - category of word
	 * @return WordElement entry for specified info
	 */
	protected WordElement createWord(String baseForm, LexicalCategory category) {
		return new WordElement(baseForm, category, this); // return default
		// WordElement of this
		// baseForm, category
	}

	/**
	 * create a default WordElement. May be overridden by specific types of
	 * lexicon
	 * 
	 * @param baseForm
	 *            - base form of word
	 * @return WordElement entry for specified info
	 */
	protected WordElement createWord(String baseForm) {
		return new WordElement(baseForm, this); // return default WordElement of this
		// baseForm
	}

	/***************************************************************************/
	// default methods for looking up words
	// These try the following (in this order)
	// 1) word with matching base
	// 2) word with matching variant
	// 3) word with matching ID
	// 4) create a new word
	/***************************************************************************/

	/**
	 * General word lookup method, tries base form, variant, ID (in this order)
	 * Creates new word if can't find existing word
	 * 
	 * @param baseForm
	 * @param category
	 * @return word
	 */
	public WordElement lookupWord(String baseForm, LexicalCategory category) {
		if (hasWord(baseForm, category))
			return getWord(baseForm, category);
		else if (hasWordFromVariant(baseForm, category))
			return getWordFromVariant(baseForm, category);
		else if (hasWordByID(baseForm))
			return getWordByID(baseForm);
		else
			return createWord(baseForm, category);
	}

	/**
	 * General word lookup method, tries base form, variant, ID (in this order)
	 * Creates new word if can't find existing word
	 * 
	 * @param baseForm
	 * @return word
	 */
	public WordElement lookupWord(String baseForm) {
		return lookupWord(baseForm, LexicalCategory.ANY);
	}

	/****************************************************************************/
	// get words by baseform and category
	// fundamental version is getWords(String baseForm, Category category),
	// this must be defined by subclasses. Other versions are convenience
	// methods. These may be overriden for efficiency, but this is not required.
	/****************************************************************************/

	/**
	 * returns all Words which have the specified base form and category
	 * 
	 * @param baseForm
	 *            - base form of word, eg "be" or "dog" (not "is" or "dogs")
	 * @param category
	 *            - syntactic category of word (ANY for unknown)
	 * @return collection of all matching Words (may be empty)
	 */
	abstract public List<WordElement> getWords(String baseForm,
			LexicalCategory category);

	/**
	 * get a WordElement which has the specified base form and category
	 * 
	 * @param baseForm
	 *            - base form of word, eg "be" or "dog" (not "is" or "dogs")
	 * @param category
	 *            - syntactic category of word (ANY for unknown)
	 * @return if Lexicon contains such a WordElement, it is returned (the first
	 *         match is returned if there are several matches). If the Lexicon
	 *         does not contain such a WordElement, a new WordElement is created
	 *         and returned
	 */
	public WordElement getWord(String baseForm, LexicalCategory category) {// convenience
		// method
		// derived
		// from
		// other
		// methods
		List<WordElement> wordElements = getWords(baseForm, category);
		if (wordElements.isEmpty())
			return createWord(baseForm, category); // return default WordElement
		// of this baseForm,
		// category
		else
			return wordElements.get(0); // else return first match
	}

	/**
	 * return <code>true</code> if the lexicon contains a WordElement which has
	 * the specified base form and category
	 * 
	 * @param baseForm
	 *            - base form of word, eg "be" or "dog" (not "is" or "dogs")
	 * @param category
	 *            - syntactic category of word (ANY for unknown)
	 * @return <code>true</code> if Lexicon contains such a WordElement
	 */
	public boolean hasWord(String baseForm, LexicalCategory category) {// convenience
		// method
		// derived
		// from
		// other
		// methods)
		// {
		return !getWords(baseForm, category).isEmpty();
	}

	/**
	 * returns all Words which have the specified base form
	 * 
	 * @param baseForm
	 *            - base form of word, eg "be" or "dog" (not "is" or "dogs")
	 * @return collection of all matching Words (may be empty)
	 */
	public List<WordElement> getWords(String baseForm) { // convenience method
		// derived from
		// other methods
		return getWords(baseForm, LexicalCategory.ANY);
	}

	/**
	 * get a WordElement which has the specified base form (of any category)
	 * 
	 * @param baseForm
	 *            - base form of word, eg "be" or "dog" (not "is" or "dogs")
	 * @return if Lexicon contains such a WordElement, it is returned (the first
	 *         match is returned if there are several matches). If the Lexicon
	 *         does not contain such a WordElement, a new WordElement is created
	 *         and returned
	 */
	public WordElement getWord(String baseForm) { // convenience method derived
		// from other methods
		List<WordElement> wordElements = getWords(baseForm);

		if (wordElements.isEmpty())
			return createWord(baseForm); // return default WordElement of this
		// baseForm
		else
			return wordElements.get(0); // else return first match
	}

	/**
	 * return <code>true</code> if the lexicon contains a WordElement which has
	 * the specified base form (in any category)
	 * 
	 * @param baseForm
	 *            - base form of word, eg "be" or "dog" (not "is" or "dogs")
	 * @return <code>true</code> if Lexicon contains such a WordElement
	 */
	public boolean hasWord(String baseForm) {// convenience method derived from
		// other methods) {
		return !getWords(baseForm).isEmpty();
	}

	/****************************************************************************/
	// get words by ID
	// fundamental version is getWordsByID(String id),
	// this must be defined by subclasses.
	// Other versions are convenience methods
	// These may be overriden for efficiency, but this is not required.
	/****************************************************************************/

	/**
	 * returns a List of WordElement which have this ID. IDs are
	 * lexicon-dependent, and should be unique. Therefore the list should
	 * contain either zero elements (if no such word exists) or one element (if
	 * the word is found)
	 * 
	 * @param id
	 *            - internal lexicon ID for a word
	 * @return either empty list (if no word with this ID exists) or list
	 *         containing the matching word
	 */
	abstract public List<WordElement> getWordsByID(String id);

	/**
	 * get a WordElement with the specified ID
	 * 
	 * @param id
	 *            internal lexicon ID for a word
	 * @return WordElement with this ID if found; otherwise a new WordElement is
	 *         created with the ID as the base form
	 */
	public WordElement getWordByID(String id) {
		List<WordElement> wordElements = getWordsByID(id);
		if (wordElements.isEmpty())
			return createWord(id); // return WordElement based on ID; may help
		// in debugging...
		else
			return wordElements.get(0); // else return first match
	}

	/**
	 * return <code>true</code> if the lexicon contains a WordElement which the
	 * specified ID
	 * 
	 * @param id
	 *            - internal lexicon ID for a word
	 * @return <code>true</code> if Lexicon contains such a WordElement
	 */
	public boolean hasWordByID(String id) {// convenience method derived from
		// other methods) {
		return !getWordsByID(id).isEmpty();
	}

	/****************************************************************************/
	// get words by variant - try to return a WordElement given an inflectional
	// or spelling
	// variant. For the moment, acronyms are considered as separate words, not
	// variants
	// (this may change in the future)
	// fundamental version is getWordsFromVariant(String baseForm, Category
	// category),
	// this must be defined by subclasses. Other versions are convenience
	// methods. These may be overriden for efficiency, but this is not required.
	/****************************************************************************/

	/**
	 * returns Words which have an inflected form and/or spelling variant that
	 * matches the specified variant, and are in the specified category. <br>
	 * <I>Note:</I> the returned word list may not be complete, it depends on
	 * how it is implemented by the underlying lexicon
	 * 
	 * @param variant
	 *            - base form, inflected form, or spelling variant of word
	 * @param category
	 *            - syntactic category of word (ANY for unknown)
	 * @return list of all matching Words (empty list if no matching WordElement
	 *         found)
	 */
	abstract public List<WordElement> getWordsFromVariant(String variant,
			LexicalCategory category);

	/**
	 * returns a WordElement which has the specified inflected form and/or
	 * spelling variant that matches the specified variant, of the specified
	 * category
	 * 
	 * @param variant
	 *            - base form, inflected form, or spelling variant of word
	 * @param category
	 *            - syntactic category of word (ANY for unknown)
	 * @return a matching WordElement (if found), otherwise a new word is
	 *         created using thie variant as the base form
	 */
	public WordElement getWordFromVariant(String variant,
			LexicalCategory category) {
		List<WordElement> wordElements = getWordsFromVariant(variant, category);
		if (wordElements.isEmpty())
			return createWord(variant, category); // return default WordElement
		// using variant as base
		// form
		else
			return wordElements.get(0); // else return first match

	}

	/**
	 * return <code>true</code> if the lexicon contains a WordElement which
	 * matches the specified variant form and category
	 * 
	 * @param variant
	 *            - base form, inflected form, or spelling variant of word
	 * @param category
	 *            - syntactic category of word (ANY for unknown)
	 * @return <code>true</code> if Lexicon contains such a WordElement
	 */
	public boolean hasWordFromVariant(String variant, LexicalCategory category) {// convenience
		// method
		// derived
		// from
		// other
		// methods)
		// {
		return !getWordsFromVariant(variant, category).isEmpty();
	}

	/**
	 * returns Words which have an inflected form and/or spelling variant that
	 * matches the specified variant, of any category. <br>
	 * <I>Note:</I> the returned word list may not be complete, it depends on
	 * how it is implemented by the underlying lexicon
	 * 
	 * @param variant
	 *            - base form, inflected form, or spelling variant of word
	 * @return list of all matching Words (empty list if no matching WordElement
	 *         found)
	 */
	public List<WordElement> getWordsFromVariant(String variant) {
		return getWordsFromVariant(variant, LexicalCategory.ANY);
	}

	/**
	 * returns a WordElement which has the specified inflected form and/or
	 * spelling variant that matches the specified variant, of any category.
	 * 
	 * @param variant
	 *            - base form, inflected form, or spelling variant of word
	 * @return a matching WordElement (if found), otherwise a new word is
	 *         created using thie variant as the base form
	 */
	public WordElement getWordFromVariant(String variant) {
		List<WordElement> wordElements = getWordsFromVariant(variant);
		if (wordElements.isEmpty())
			return createWord(variant); // return default WordElement using
		// variant as base form
		else
			return wordElements.get(0); // else return first match
	}

	/**
	 * return <code>true</code> if the lexicon contains a WordElement which
	 * matches the specified variant form (in any category)
	 * 
	 * @param variant
	 *            - base form, inflected form, or spelling variant of word
	 * @return <code>true</code> if Lexicon contains such a WordElement
	 */
	public boolean hasWordFromVariant(String variant) {// convenience method
		// derived from other
		// methods) {
		return !getWordsFromVariant(variant).isEmpty();
	}

	/****************************************************************************/
	// other methods
	/****************************************************************************/

	/**
	 * close the lexicon (if necessary) if lexicon does not need to be closed,
	 * this does nothing
	 */
	public void close() {
		// default method does nothing
	}

	/**
	 * Get the coordination conjunction used for addition in this lexicon.
	 * (normally "and" in English, "et" in French, etc.)
	 * If the lexicon uses the same word IDs than the NIH Specialist lexicon
	 * and the default English XML lexicon, than this would be "E0008890".
	 * If this is not found, it selects the conjunction in function of
	 * the language of this lexicon. The default is "and". It creates it if
	 * it not found.
	 * This can be overridden by subclasses if this default implementation
	 * is inadequate.
	 * 
	 * @return	the coordination conjunction used for addition in this lexicon
	 * 
	 * @author vaudrypl
	 */
	public WordElement getAdditionCoordConjunction() {
		WordElement conjunction;
		
		if (hasWordByID("E0008890")) {
			conjunction = getWordByID("E0008890");
		} else {
			switch (getLanguage()) {
			case FRENCH :
				conjunction = lookupWord("et", LexicalCategory.CONJUNCTION);
				break;
			case ENGLISH : default:
				conjunction = lookupWord("and", LexicalCategory.CONJUNCTION);
				break;
			}
		}
		
		return conjunction;
	}
	
	/**
	 * Get the preposition used for passive subjects in this lexicon.
	 * (normally "by" in English, "par" in French, etc.)
	 * If the lexicon uses the same word IDs than the NIH Specialist lexicon
	 * and the default English XML lexicon, than this would be "E0014539".
	 * If this is not found, it selects the preposition in function of
	 * the language of this lexicon. The default is "by". It creates it if
	 * it not found.
	 * This can be overridden by subclasses if this default implementation
	 * is inadequate.
	 * 
	 * @return	the preposition used for passive subjects in this lexicon
	 * 
	 * @author vaudrypl
	 */
	public WordElement getPassivePreposition() {
		WordElement preposition;
		
		if (hasWordByID("E0014539")) {
			preposition = getWordByID("E0014539");
		} else {
			switch (getLanguage()) {
			case FRENCH :
				preposition = lookupWord("par", LexicalCategory.PREPOSITION); 
				break;
			case ENGLISH : default:
				preposition = lookupWord("by", LexicalCategory.PREPOSITION); 
				break;
			}
		}
		
		return preposition;
	}
	
	/**
	 * Get the default complementiser for clauses.
	 * This can be overridden by subclasses if this default implementation
	 * is inadequate.
	 * 
	 * @return	the default complementiser for clauses in this lexicon
	 * 
	 * @author vaudrypl
	 */
	public WordElement getDefaultComplementiser() {
		WordElement complementiser;
		
		switch (getLanguage()) {
		case FRENCH :
			complementiser = lookupWord("que", LexicalCategory.COMPLEMENTISER); 
			break;
		case ENGLISH : default:
			complementiser = lookupWord("that", LexicalCategory.COMPLEMENTISER); 
			break;
		}
		
		return complementiser;
	}
	
	/**
	 * Looks for all words in the lexicon matching the category and features
	 * provided. Not implemented in the Lexicon base class. Will trow an
	 * exception if called and not overridden. (An abstract method could
	 * have been used here, of course, but we did it this way to preserve
	 * compatibility with the NIHDBLexicon subclass.)
	 * If some of the features provided have a value of null or Boolean.FALSE,
	 * This method will also include words who don't have those features at all.
	 * This allows default values for features not determined by the word. 
	 * 
	 * @param category	category of the returned WordElement
	 * @param features	features and their corrsponding values that
	 *					the WordElement returned must have (it can have others)
	 * @return			list of all WordElements found that matches the argument
	 * 
	 * @author vaudrypl
	 */
	public List<WordElement> getWords(LexicalCategory category,
			Map<String, Object> features) {
		throw new UnsupportedOperationException("Method not implemented.");
	}
	
	/**
	 * Looks for a word in the lexicon matching the category and features
	 * provided. Make sure to override getWordsByCategoryAndFeatures()
	 * before calling this method.
	 * 
	 * @param category	category of the returned WordElement
	 * @param features	features and their corrsponding values that
	 *					the WordElement returned must have (it can have others)
	 * @return			first WordElement found that matches the argument
	 * 
	 * @author vaudrypl
	 */
	public WordElement getWord(LexicalCategory category,
			Map<String, Object> features) {
		List<WordElement> wordElements = getWords(category,
				features);
		if (wordElements.isEmpty())
			return null;
		else
			return wordElements.get(0); // else return first match
	}
	
	/**
	 * Says if a word in the lexicon matches the category and features
	 * provided. Make sure to override getWordsByCategoryAndFeatures()
	 * before calling this method.
	 * 
	 * @param category	category of the returned WordElement
	 * @param features	features and their corrsponding values that
	 *					the WordElement returned must have (it can have others)
	 * @return <code>true</code> if Lexicon contains such a WordElement
	 * 
	 * @author vaudrypl
	 */
	public boolean hasWord(LexicalCategory category,
			Map<String, Object> features) {// convenience method derived from
		// other methods) {
		return !getWords(category, features).isEmpty();
	}

}
