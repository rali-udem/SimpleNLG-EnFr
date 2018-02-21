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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import simplenlg.features.LexicalFeature;
import simplenlg.framework.Language;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;

/**
 * This class loads words from an XML lexicon. All features specified in the
 * lexicon are loaded
 * 
 * @author ereiter
 * 
 */
public class XMLLexicon extends Lexicon {

	// node names in lexicon XML files
	private static final String XML_BASE = "base"; // base form of Word
	private static final String XML_CATEGORY = "category"; // base form of Word
	private static final String XML_ID = "id"; // base form of Word
	private static final String XML_WORD = "word"; // node defining a word

	// inflectional codes which need to be set as part of INFLECTION feature
	private static final List<String> INFL_CODES = Arrays.asList(new String[] {
			"reg", "irreg", "uncount", "inv", "metareg", "glreg", "nonCount", "sing", "groupuncount" });

	// lexicon
	private Set<WordElement> words; // set of words
	private Map<String, WordElement> indexByID; // map from ID to word
	private Map<String, List<WordElement>> indexByBase; // map from base to set
	// of words with this
	// baseform
	protected /*private*/ Map<String, List<WordElement>> indexByVariant; // map from variants

	// to set of words
	// with this variant

	// added by vaudrypl
	protected Map<LexicalCategory, List<WordElement>> indexByCategory; // map from variants

	
	/**********************************************************************/
	// constructors
	/**********************************************************************/

	/**
	 * Load an XML Lexicon from a named file
	 * 
	 * @param filename
	 */
	public XMLLexicon(String filename) {
		super();
		File file = new File(filename);
		createLexicon(file.toURI());
	}

	/**
	 * Load an XML Lexicon from a File
	 * 
	 * @param file
	 */
	public XMLLexicon(File file) {
		super();
		createLexicon(file.toURI());
	}

	/**
	 * Load an XML Lexicon from a URI
	 * 
	 * @param lexiconURI
	 */
	public XMLLexicon(URI lexiconURI) {
		super();
		createLexicon(lexiconURI);
	}

	public XMLLexicon() {
		this(Language.DEFAULT_LANGUAGE);
	}

	/**
	 * Loads the default XML lexicon corresponding to a particular language 
	 * 
	 * @param language
	 */
	public XMLLexicon(Language language) {
		super(language);
		if (language == null) language = Language.DEFAULT_LANGUAGE;
		
		String xmlLexiconFilePath;
		switch (language) {
		case FRENCH :
			xmlLexiconFilePath = "default-french-lexicon.xml";
			break;
		default :
			xmlLexiconFilePath = "default-lexicon.xml";
		}
		
		try {
			createLexicon(getClass().getClassLoader().getResource(xmlLexiconFilePath).toURI());
		} catch (URISyntaxException ex) {
			System.out.println(ex.toString());
		}
	}
	
	/**
	 * Load an XML Lexicon from a named file
	 * with the associated language
	 * 
	 * @param language
	 *            the associated language
	 * @param filename
	 * @author vaudrypl
	 */
	public XMLLexicon(Language language, String filename) {
		super(language);
		File file = new File(filename);
		createLexicon(file.toURI());
	}

	/**
	 * Load an XML Lexicon from a named file
	 * with the ISO 639-1 two letter code of the associated language 
	 * 
	 * @param language
	 *            the associated language ISO 639-1 two letter code
	 * @param filename
	 * @author vaudrypl
	 */
	public XMLLexicon(String languageCode, String filename) {
		super(languageCode);
		File file = new File(filename);
		createLexicon(file.toURI());
	}

	/**
	 * Load an XML Lexicon from a file
	 * with the associated language
	 * 
	 * @param language
	 *            the associated language
	 * @param file
	 * @author vaudrypl
	 */
	public XMLLexicon(Language language, File file) {
		super(language);
		createLexicon(file.toURI());
	}

	/**
	 * Load an XML Lexicon from a file
	 * with the ISO 639-1 two letter code of the associated language 
	 * 
	 * @param language
	 *            the associated language ISO 639-1 two letter code
	 * @param file
	 * @author vaudrypl
	 */
	public XMLLexicon(String languageCode, File file) {
		super(languageCode);
		createLexicon(file.toURI());
	}

	/**
	 * Load an XML Lexicon from a URI
	 * with the associated language
	 * 
	 * @param language
	 *            the associated language
	 * @param lexiconURI
	 * @author vaudrypl
	 */
	public XMLLexicon(Language language, URI lexiconURI) {
		super(language);
		createLexicon(lexiconURI);
	}

	/**
	 * Load an XML Lexicon from a URI
	 * with the ISO 639-1 two letter code of the associated language 
	 * 
	 * @param language
	 *            the associated language ISO 639-1 two letter code
	 * @param lexiconURI
	 * @author vaudrypl
	 */
	public XMLLexicon(String languageCode, URI lexiconURI) {
		super(languageCode);
		createLexicon(lexiconURI);
	}

	/**
	 * method to actually load and index the lexicon from a URI
	 * 
	 * vaudrypl removed call to addSpecialCases() and moved this
	 * method to simplenlg.lexicon.english.XMLLexicon
	 * 
	 * @param uri
	 */
	private void createLexicon(URI lexiconURI) {
		// initialise objects
		words = new HashSet<WordElement>();
		indexByID = new HashMap<String, WordElement>();
		indexByBase = new HashMap<String, List<WordElement>>();
		indexByVariant = new HashMap<String, List<WordElement>>();
		// added by vaudrypl
		indexByCategory = new EnumMap<LexicalCategory, List<WordElement>>(LexicalCategory.class);

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(lexiconURI.toString());

			if (doc != null) {
				Element lexRoot = doc.getDocumentElement();
				NodeList wordNodes = lexRoot.getChildNodes();
				for (int i = 0; i < wordNodes.getLength(); i++) {
					Node wordNode = wordNodes.item(i);
					// ignore things that aren't elements
					if (wordNode.getNodeType() == Node.ELEMENT_NODE) {
						WordElement word = convertNodeToWord(wordNode);
						if (word != null) {
							words.add(word);
							IndexWord(word);
						}
					}
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * create a simplenlg WordElement from a Word node in a lexicon XML file
	 * 
	 * @param wordNode
	 * @return
	 * @throws XPathUtilException
	 *
	 * access level modifier changed by Pierre-Luc Vaudry from private
	 * to protected to allow overriding in subclass
	 */ 
	/*private*/ protected WordElement convertNodeToWord(Node wordNode) {
		// if this isn't a Word node, ignore it
		if (!wordNode.getNodeName().equalsIgnoreCase(XML_WORD))
			return null;		

		// // if there is no base, flag an error and return null
		// String base = XPathUtil.extractValue(wordNode, Constants.XML_BASE);
		// if (base == null) {
		// System.out.println("Error in loading XML lexicon: Word with no base");
		// return null;
		// }

		// create word
		WordElement word = new WordElement(this);
		List<String> inflections = new ArrayList<String>();
		
		// now copy features
		NodeList nodes = wordNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node featureNode = nodes.item(i);

			if (featureNode.getNodeType() == Node.ELEMENT_NODE) {
				String feature = featureNode.getNodeName().trim();
				String value = featureNode.getTextContent();

				if (value != null)
					value = value.trim();

				if (feature == null) {
					System.out.println("Error in XML lexicon node for "
							+ word.toString());
					break;
				}

				if (feature.equalsIgnoreCase(XML_BASE)) {
					word.setBaseForm(value);
				} else if (feature.equalsIgnoreCase(XML_CATEGORY))
					word.setCategory(LexicalCategory.valueOf(value
							.toUpperCase()));
				else if (feature.equalsIgnoreCase(XML_ID))
					word.setId(value);
				else if (value == null || value.equals("")) {
					if (INFL_CODES.contains(feature)) {
						// if this is an infl code, add it to inflections
						inflections.add(feature);
					} else {
						//otherwise assume it's a boolean feature
						word.setFeature(feature, true);
					}
				} else
					word.setFeature(feature, value);
			}

		}
		
		//if no infl specified, assume regular
		if(inflections.isEmpty()) {
			inflections.add("reg");
		} 
		
		//default inflection code is "reg" if we have it, else random pick form infl codes available
		String defaultInfl = inflections.contains("reg") ? "reg" : inflections.get(0);
		
		word.setFeature(LexicalFeature.INFLECTIONS, inflections);
		word.setFeature(LexicalFeature.DEFAULT_INFL, defaultInfl);

		// done, return word
		return word;
	}

	/**
	 * add word to internal indices
	 * 
	 * @param word
	 */
	private void IndexWord(WordElement word) {
		// first index by base form
		String base = word.getBaseForm();
		// shouldn't really need is, as all words have base forms
		if (base != null) {
			updateIndex(word, base, indexByBase);
		}

		// now index by ID, which should be unique (if present)
		String id = word.getId();
		if (id != null) {
			if (indexByID.containsKey(id))
				System.out.println("Lexicon error: ID " + id
						+ " occurs more than once");
			indexByID.put(id, word);
		}

		// now index by variant
		for (String variant : getVariants(word)) {
			updateIndex(word, variant, indexByVariant);
		}

		// added by vaudrypl
		// now index by category
		LexicalCategory category = (LexicalCategory) word.getCategory();
		// shouldn't really need is, as all words have category
		if (category != null) {
			if (!indexByCategory.containsKey(category)) {
				indexByCategory.put(category, new ArrayList<WordElement>());
			}
			indexByCategory.get(category).add(word);
		}

		// done
	}

	/**
	 * routine for getting morph variants, should be overridden by subclass
	 * for specific language
	 * @param word
	 * @return set of variants of the word
	 * @author vaudrypl
	 */
	protected Set<String> getVariants(WordElement word) {
		Set<String> variants = new HashSet<String>();
		variants.add(word.getBaseForm());
		return variants;
	}

	/**
	 * convenience method to update an index
	 * 
	 * @param word
	 * @param base
	 * @param index
	 */
	protected /*private*/ void updateIndex(WordElement word, String base,
			Map<String, List<WordElement>> index) {
		if (!index.containsKey(base))
			index.put(base, new ArrayList<WordElement>());
		index.get(base).add(word);
	}

	/**
	 * creates a default WordElement and adds it to the lexicon
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
		WordElement newWord = super.createWord(baseForm, category);
		words.add(newWord);
		IndexWord(newWord);
		return newWord; // return default
		// WordElement of this
		// baseForm, category
	}

	/**
	 * creates a default WordElement and adds it to the lexicon
	 * 
	 * @param baseForm
	 *            - base form of word
	 * @return WordElement entry for specified info
	 * @author vaudrypl
	 */
	@Override
	protected WordElement createWord(String baseForm) {
		WordElement newWord = super.createWord(baseForm);
		words.add(newWord);
		IndexWord(newWord);
		return newWord;  // return default WordElement of this
		// baseForm
	}

	/******************************************************************************************/
	// main methods to get data from lexicon
	/******************************************************************************************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.Lexicon#getWords(java.lang.String,
	 * simplenlg.features.LexicalCategory)
	 */
	@Override
	public List<WordElement> getWords(String baseForm, LexicalCategory category) {
		return getWordsFromIndex(baseForm, category, indexByBase);
	}

	/**
	 * get matching keys from an index map
	 * 
	 * @param indexKey
	 * @param category
	 * @param indexMap
	 * @return
	 */
	private List<WordElement> getWordsFromIndex(String indexKey,
			LexicalCategory category, Map<String, List<WordElement>> indexMap) {
		List<WordElement> result = new ArrayList<WordElement>();

		// case 1: unknown, return empty list
		if (!indexMap.containsKey(indexKey))
			return result;

		// case 2: category is ANY, return everything
		if (category == LexicalCategory.ANY)
			return indexMap.get(indexKey);

		// case 3: other category, search for match
		else
			for (WordElement word : indexMap.get(indexKey))
				if (word.getCategory() == category)
					result.add(word);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.Lexicon#getWordsByID(java.lang.String)
	 */
	@Override
	public List<WordElement> getWordsByID(String id) {
		List<WordElement> result = new ArrayList<WordElement>();
		if (indexByID.containsKey(id))
			result.add(indexByID.get(id));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.Lexicon#getWordsFromVariant(java.lang.String,
	 * simplenlg.features.LexicalCategory)
	 */
	@Override
	public List<WordElement> getWordsFromVariant(String variant,
			LexicalCategory category) {
		return getWordsFromIndex(variant, category, indexByVariant);
	}

	/**
	 * Looks for all words in the lexicon matching the category and features
	 * provided. If some of the features provided have a value of null or Boolean.FALSE,
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
	@Override
	public List<WordElement> getWords(LexicalCategory category,
			Map<String, Object> features) {
		List<WordElement> result = new ArrayList<WordElement>();
		Collection<WordElement> collection = null;
		Iterator<WordElement> iterator = null;

		if (category == LexicalCategory.ANY) {
			// use the whole lexicon
			collection = words;
		} else if (indexByCategory.containsKey(category)) {
			collection = indexByCategory.get(category);
		}
		
		if (collection != null) iterator = collection.iterator();
		
		// if the index by category doesn't contain the category wanted,
		// skip this part and return an empty list
		if (iterator != null) {
			if (features == null) {
				result.addAll(collection);
			}else {
				// must convert map to set to use contains()
				Set<Map.Entry<String, Object>> featuresToCheck = features.entrySet();
				while (iterator.hasNext()) {
					WordElement currentWord = iterator.next();
					Map<String, Object> currentFeaturesMap = currentWord.getAllFeatures();
					Set<Map.Entry<String, Object>> currentFeaturesSet = currentFeaturesMap.entrySet();
	
	/*				Doesn't add a word to the list if the following is not true for
					at least one feature received as argument :
					The word has this feature and its corresponding value OR
					The value of this feature is null or Boolean.FALSE and the word
					doesn't have this feature at all.
	*/				boolean addWord = true;
					for (Map.Entry<String, Object> entry : featuresToCheck) {
						if ( !( currentFeaturesSet.contains( entry ) || 
							((entry.getValue() == null || entry.getValue() == Boolean.FALSE)
									&& !currentFeaturesMap.containsKey(entry.getKey())) ) ) {
							addWord = false;
							break;
						}
					}
					if (addWord) result.add(currentWord);
				}
			}
		}

		return result;
	}
}