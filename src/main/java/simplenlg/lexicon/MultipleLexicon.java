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

import java.util.ArrayList;
import java.util.List;

import simplenlg.framework.Language;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;

/** This class contains a set of lexicons, which are searched in
 * order for the specified word
 * 
 * @author ereiter
 *
 */
public class MultipleLexicon extends Lexicon {
	
	/* if this flag is true, all lexicons are searched for
	 * this word, even after a match is found
	 * it is false by default
	 * */
	private boolean alwaysSearchAll = false;
	
	/* list of lexicons, in order in which they are searched */
	private List<Lexicon> lexiconList = null;

	/**********************************************************************/
	// constructors
	/**********************************************************************/
	
	/**
	 * create an empty multi lexicon
	 */
	public MultipleLexicon() {
		super();
		lexiconList = new ArrayList<Lexicon>();
		alwaysSearchAll = false;
	}
	
	/** create a multi lexicon with the specified lexicons
	 * @param lexicons
	 */
	public MultipleLexicon(Lexicon... lexicons) {
		this();
		for (Lexicon lex: lexicons)
			lexiconList.add(lex);
	}
	
	/**
	 * create an empty multi lexicon
	 * with specified associated language
	 * @param language
	 * @author vaudrypl
	 */
	public MultipleLexicon(Language language) {
		super(language);
		lexiconList = new ArrayList<Lexicon>();
		alwaysSearchAll = false;
	}
	
	/** create a multi lexicon with the specified lexicons
	 * with specified associated language
	 * @param language
	 * @param lexicons
	 * @author vaudrypl
	 */
	public MultipleLexicon(Language language, Lexicon... lexicons) {
		this(language);
		for (Lexicon lex: lexicons)
			lexiconList.add(lex);
	}
	
	/**
	 * create an empty multi lexicon
	 * with specified associated language ISO 639-1 two letter code
	 * @param language
	 * @author vaudrypl
	 */
	public MultipleLexicon(String languageCode) {
		super(languageCode);
		lexiconList = new ArrayList<Lexicon>();
		alwaysSearchAll = false;
	}
	
	/** create a multi lexicon with the specified lexicons
	 * with specified associated language ISO 639-1 two letter code
	 * @param language
	 * @param lexicons
	 * @author vaudrypl
	 */
	public MultipleLexicon(String languageCode, Lexicon... lexicons) {
		this(languageCode);
		for (Lexicon lex: lexicons)
			lexiconList.add(lex);
	}
	
	/**********************************************************************/
	// routines to add more lexicons, change flags
	/**********************************************************************/

	/** add lexicon at beginning of list (is searched first)
	 * @param lex
	 */
	public void addInitialLexicon(Lexicon lex) {
		lexiconList.add(0, lex);
	}

	/** add lexicon at end of list (is searched last)
	 * @param lex
	 */
	public void addFinalLexicon(Lexicon lex) {
		// corrected by vaudrypl
		// lexiconList.add(0, lex);
		lexiconList.add(lex);
	}

	/**
	 * @return the alwaysSearchAll
	 */
	public boolean isAlwaysSearchAll() {
		return alwaysSearchAll;
	}

	/**
	 * @param alwaysSearchAll the alwaysSearchAll to set
	 */
	public void setAlwaysSearchAll(boolean alwaysSearchAll) {
		this.alwaysSearchAll = alwaysSearchAll;
	}

	/**********************************************************************/
	// main methods
	/**********************************************************************/

	/* (non-Javadoc)
	 * @see simplenlg.lexicon.Lexicon#getWords(java.lang.String, simplenlg.features.LexicalCategory)
	 */
	@Override
	public List<WordElement> getWords(String baseForm, LexicalCategory category) {
		List<WordElement> result = new ArrayList<WordElement>();
		for (Lexicon lex: lexiconList) {
			List<WordElement> lexResult = lex.getWords(baseForm, category);
			if (lexResult != null && !lexResult.isEmpty()) {
				result.addAll(lexResult);
				if (!alwaysSearchAll)
					return result;
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see simplenlg.lexicon.Lexicon#getWordsByID(java.lang.String)
	 */
	@Override
	public List<WordElement> getWordsByID(String id) {
		List<WordElement> result = new ArrayList<WordElement>();
		for (Lexicon lex: lexiconList) {
			List<WordElement> lexResult = lex.getWordsByID(id);
			if (lexResult != null && !lexResult.isEmpty()) {
				result.addAll(lexResult);
				if (!alwaysSearchAll)
					return result;
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see simplenlg.lexicon.Lexicon#getWordsFromVariant(java.lang.String, simplenlg.features.LexicalCategory)
	 */
	@Override
	public List<WordElement> getWordsFromVariant(String variant, LexicalCategory category) {
		List<WordElement> result = new ArrayList<WordElement>();
		for (Lexicon lex: lexiconList) {
			List<WordElement> lexResult = lex.getWordsFromVariant(variant, category);
			if (lexResult != null && !lexResult.isEmpty()) {
				result.addAll(lexResult);
				if (!alwaysSearchAll)
					return result;
			}
		}
		return result;
	}


	/**
	 * Creates a default WordElement with the specified category
	 * using the first lexicon in the lexicon list.
	 * If the lexicon list is empty, uses the method from
	 * the Lexicon superclass.
	 * 
	 * @param baseForm
	 *            - base form of word
	 * @param category
	 *            - category of word
	 * @return WordElement entry for specified info
	 * @author vaudrypl
	 */
	@Override
	protected WordElement createWord(String baseForm, LexicalCategory category) {
		WordElement newWord;
		if (lexiconList.isEmpty()) {
			newWord = super.createWord(baseForm, category);
		} else {
			Lexicon firstLexicon = lexiconList.get(0);
			newWord = firstLexicon.createWord(baseForm, category);
		}
		return newWord; // return default
		// WordElement of this
		// baseForm, category
	}

	/**
	 * Creates a default WordElement using the first lexicon
	 * in the lexicon list.
	 * If the lexicon list is empty, uses the method from
	 * the Lexicon superclass.
	 * 
	 * @param baseForm
	 *            - base form of word
	 * @return WordElement entry for specified info
	 * @author vaudrypl
	 */
	@Override
	protected WordElement createWord(String baseForm) {
		WordElement newWord;
		if (lexiconList.isEmpty()) {
			newWord = super.createWord(baseForm);
		} else {
			Lexicon firstLexicon = lexiconList.get(0);
			newWord = firstLexicon.createWord(baseForm);
		}
		return newWord; // return default
		// WordElement of this
		// baseForm
	}

	/**********************************************************************/
	// other methods
	/**********************************************************************/

	/* (non-Javadoc)
	 * @see simplenlg.lexicon.Lexicon#close()
	 */
	@Override
	public void close() {
		// close component lexicons
		for (Lexicon lex: lexiconList)
			lex.close();
	}


}
