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

package simplenlg.lexicon.english;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import simplenlg.framework.ElementCategory;
import simplenlg.framework.Language;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;
import simplenlg.features.LexicalFeature;

/**
 * Extension of simplenlg.lexicon.XMLLexicon for English.
 * Code specific to English was moved from simplenlg.lexicon.XMLLexicon
 * to this file.
 * 
 * @author vaudrypl
 *
 */
public class XMLLexicon extends simplenlg.lexicon.XMLLexicon {

	/**********************************************************************/
	// constructors
	/**********************************************************************/

	/**
	 * Load an XML Lexicon from a named file
	 * 
	 * @param filename
	 */
	public XMLLexicon(String filename) {
		super(Language.ENGLISH, filename);
		addSpecialCases();
	}

	/**
	 * Load an XML Lexicon from a File
	 * 
	 * @param file
	 */
	public XMLLexicon(File file) {
		super(Language.ENGLISH, file);
		addSpecialCases();
	}

	/**
	 * Load an XML Lexicon from a URI
	 * 
	 * @param lexiconURI
	 */
	public XMLLexicon(URI lexiconURI) {
		super(Language.ENGLISH, lexiconURI);
		addSpecialCases();
	}

	public XMLLexicon() {
		addSpecialCases();
	}

	/**
	 * add special cases to lexicon
	 * moved from simplenlg.lexicon.XMLLexicon
	 * 
	 */
	protected void addSpecialCases() {
		// add variants of "be"
		WordElement be = getWord("be", LexicalCategory.VERB);
		if (be != null) {
			updateIndex(be, "is", indexByVariant);
			updateIndex(be, "am", indexByVariant);
			updateIndex(be, "are", indexByVariant);
			updateIndex(be, "was", indexByVariant);
			updateIndex(be, "were", indexByVariant);
		}
	}

	/**
	 * quick-and-dirty routine for getting morph variants should be replaced by
	 * something better!
	 * moved by vaudrypl from simplenlg.lexicon.XMLLexicon
	 * 
	 * @param word
	 * @return set of variants of the word
	 */
	@Override
	protected Set<String> getVariants(WordElement word) {
		Set<String> variants = new HashSet<String>();
		variants.add(word.getBaseForm());
		ElementCategory category = word.getCategory();
		if (category instanceof LexicalCategory) {
			switch ((LexicalCategory) category) {
			case NOUN:
				variants.add(getVariant(word, LexicalFeature.PLURAL, "s"));
				break;

			case ADJECTIVE:
				variants
						.add(getVariant(word, LexicalFeature.COMPARATIVE, "er"));
				variants
						.add(getVariant(word, LexicalFeature.SUPERLATIVE, "est"));
				break;

			case VERB:
				variants.add(getVariant(word, LexicalFeature.PRESENT3S, "s"));
				variants.add(getVariant(word, LexicalFeature.PAST, "ed"));
				variants.add(getVariant(word, LexicalFeature.PAST_PARTICIPLE,
						"ed"));
				variants.add(getVariant(word,
						LexicalFeature.PRESENT_PARTICIPLE, "ing"));
				break;

			default:
				// only base needed for other forms
				break;
			}
		}
		return variants;
	}

	/**
	 * quick-and-dirty routine for computing morph forms Should be replaced by
	 * something better!
	 * moved by vaudrypl from simplenlg.lexicon.XMLLexicon
	 * 
	 * @param word
	 * @param feature
	 * @param string
	 * @return
	 */
	protected String getVariant(WordElement word, String feature, String suffix) {
		if (word.hasFeature(feature))
			return word.getFeatureAsString(feature);
		else
			return getForm(word.getBaseForm(), suffix);
	}

	/**
	 * quick-and-dirty routine for standard orthographic changes Should be
	 * replaced by something better!
	 * moved by vaudrypl from simplenlg.lexicon.XMLLexicon
	 * 
	 * @param base
	 * @param suffix
	 * @return
	 */
	protected String getForm(String base, String suffix) {
		// add a suffix to a base form, with orthographic changes

		// rule 1 - convert final "y" to "ie" if suffix does not start with "i"
		// eg, cry + s = cries , not crys
		if (base.endsWith("y") && !suffix.startsWith("i"))
			base = base.substring(0, base.length() - 1) + "ie";

		// rule 2 - drop final "e" if suffix starts with "e" or "i"
		// eg, like+ed = liked, not likeed
		if (base.endsWith("e")
				&& (suffix.startsWith("e") || suffix.startsWith("i")))
			base = base.substring(0, base.length() - 1);

		// rule 3 - insert "e" if suffix is "s" and base ends in s, x, z, ch, sh
		// eg, watch+s -> watches, not watchs
		if (suffix.startsWith("s")
				&& (base.endsWith("s") || base.endsWith("x")
						|| base.endsWith("z") || base.endsWith("ch") || base
						.endsWith("sh")))
			base = base + "e";

		// have made changes, now append and return
		return base + suffix; // eg, want + s = wants
	}
}
