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
package simplenlg.syntax.english.nonstatic;

import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.LexicalFeature;
import simplenlg.features.Tense;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.AdvPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.syntax.AbstractClauseHelper;

/**
 * <p>
 * This is a helper class containing the main methods for realising the syntax
 * of clauses for English. It is a non static version by vaudrypl of the class
 * of the same name in the <code>simplenlg.syntax.english</code> package.
 * </p>
 * modified by vaudrypl :
 * abstract class replaced by public class
 * private static methods replaced by protected methods
 * parent.realise(element) replaced by element.realiseSyntax()
 * SyntaxProcessor parent arguments removed
 * PhraseHelper replaced by phrase.getPhraseHelper()
 * now implements AbstractClauseHelper
 * 
 * most methods now moved to AbstractClauseHelper
 * 
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 */
public class ClauseHelper extends AbstractClauseHelper {

	/**
	 * Adds <em>to</em> to the end of interrogatives concerning indirect
	 * objects. For example, <em>who did John give the flower <b>to</b></em>.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 */
	@Override
	protected void addEndingTo(PhraseElement phrase,
			ListElement realisedElement,
			NLGFactory phraseFactory) {

		if (InterrogativeType.WHO_INDIRECT_OBJECT.equals(phrase
				.getFeature(Feature.INTERROGATIVE_TYPE))) {
			NLGElement word = phraseFactory.createWord(
					"to", LexicalCategory.PREPOSITION); //$NON-NLS-1$
			realisedElement.addComponent(word.realiseSyntax());
		}
	}

	/**
	 * This is the main controlling method for handling interrogative clauses.
	 * The actual steps taken are dependent on the type of question being asked.
	 * The method also determines if there is a subject that will split the verb
	 * group of the clause. For example, the clause
	 * <em>the man <b>should give</b> the woman the flower</em> has the verb
	 * group indicated in <b>bold</b>. The phrase is rearranged as yes/no
	 * question as
	 * <em><b>should</b> the man <b>give</b> the woman the flower</em> with the
	 * subject <em>the man</em> splitting the verb group.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 * @return an <code>NLGElement</code> representing a subject that should
	 *         split the verb
	 */
	@Override
	protected NLGElement realiseInterrogative(PhraseElement phrase,
			ListElement realisedElement,
			NLGFactory phraseFactory, NLGElement verbElement) {
		NLGElement splitVerb = null;

		if (phrase.getParent() != null) {
			phrase.getParent().setFeature(InternalFeature.INTERROGATIVE, true);
		}
		Object type = phrase.getFeature(Feature.INTERROGATIVE_TYPE);

		if (type instanceof InterrogativeType) {
			switch ((InterrogativeType) type) {
			case YES_NO:
				splitVerb = realiseYesNo(phrase, verbElement,
						phraseFactory, realisedElement);
				break;

			case WHO_SUBJECT:
				realiseInterrogativeKeyWord("who", realisedElement, //$NON-NLS-1$
						phraseFactory);
				// Commented out by vaudrypl to keep phrase intact.
				// Modified addSubjectsToFront() accordingly.
				// phrase.removeFeature(InternalFeature.SUBJECTS);
				break;

			case HOW:
			case WHY:
			case WHERE:
				realiseInterrogativeKeyWord(type.toString().toLowerCase(),
						realisedElement, //$NON-NLS-1$
						phraseFactory);
				splitVerb = realiseYesNo(phrase, verbElement,
						phraseFactory, realisedElement);
				break;

			case HOW_MANY:
				realiseInterrogativeKeyWord("how", realisedElement, //$NON-NLS-1$
						phraseFactory);
				realiseInterrogativeKeyWord("many", realisedElement, //$NON-NLS-1$
						phraseFactory);
				break;

			case WHO_OBJECT:
			case WHO_INDIRECT_OBJECT:
				realiseInterrogativeKeyWord("who", realisedElement, //$NON-NLS-1$
						phraseFactory);
				addDoAuxiliary(phrase, phraseFactory, realisedElement);
				break;

			case WHAT_OBJECT:
				splitVerb = realiseWhatInterrogative(phrase,
						realisedElement, phraseFactory);
				break;

			default:
				break;
			}
		}
		return splitVerb;
	}

	/**
	 * Controls the realisation of <em>what</em> questions.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 * @param subjects
	 *            the <code>List</code> of subjects in the clause.
	 * @return an <code>NLGElement</code> representing a subject that should
	 *         split the verb
	 */
	protected NLGElement realiseWhatInterrogative(PhraseElement phrase,
			ListElement realisedElement, NLGFactory phraseFactory) {
		NLGElement splitVerb = null;

		realiseInterrogativeKeyWord("what", realisedElement, //$NON-NLS-1$
				phraseFactory);
		if (!Tense.FUTURE.equals(phrase.getTense())) {
			addDoAuxiliary(phrase, phraseFactory, realisedElement);
		} else {
			if (!phrase.getFeatureAsBoolean(Feature.PASSIVE).booleanValue()) {
				splitVerb = realiseSubjects(phrase);
			}
		}
		return splitVerb;
	}

	/**
	 * Adds a <em>do</em> verb to the realisation of this clause.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 */
	protected void addDoAuxiliary(PhraseElement phrase,	NLGFactory phraseFactory,
			ListElement realisedElement) {

		PhraseElement doPhrase = phraseFactory.createVerbPhrase("do"); //$NON-NLS-1$
		doPhrase.setTense(phrase.getTense());
		doPhrase.setFeature(Feature.PERSON, phrase.getFeature(Feature.PERSON));
		doPhrase.setFeature(Feature.NUMBER, phrase.getFeature(Feature.NUMBER));
		realisedElement.addComponent(doPhrase.realiseSyntax());
	}

	/**
	 * Realises the key word of the interrogative. For example, <em>who</em>,
	 * <em>what</em>
	 * 
	 * @param keyWord
	 *            the key word of the interrogative.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 */
	protected void realiseInterrogativeKeyWord(String keyWord,
			ListElement realisedElement, NLGFactory phraseFactory) {

		if (keyWord != null) {
			NLGElement question = phraseFactory.createWord(keyWord,
					LexicalCategory.NOUN);
			NLGElement currentElement = question.realiseSyntax();
			if (currentElement != null) {
				realisedElement.addComponent(currentElement);
			}
		}
	}

	/**
	 * Performs the realisation for YES/NO types of questions. This may involve
	 * adding an optional <em>do</em> auxiliary verb to the beginning of the
	 * clause. The method also determines if there is a subject that will split
	 * the verb group of the clause. For example, the clause
	 * <em>the man <b>should give</b> the woman the flower</em> has the verb
	 * group indicated in <b>bold</b>. The phrase is rearranged as yes/no
	 * question as
	 * <em><b>should</b> the man <b>give</b> the woman the flower</em> with the
	 * subject <em>the man</em> splitting the verb group.
	 * 
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 * @param subjects
	 *            the <code>List</code> of subjects in the clause.
	 * @return an <code>NLGElement</code> representing a subject that should
	 *         split the verb
	 */
	protected NLGElement realiseYesNo(PhraseElement phrase,
			NLGElement verbElement,
			NLGFactory phraseFactory, ListElement realisedElement) {

		NLGElement splitVerb = null;

		if (!(verbElement instanceof VPPhraseSpec && phrase.getVerbPhraseHelper()
				.isCopular(((VPPhraseSpec) verbElement).getVerb()))
				&& !phrase.getFeatureAsBoolean(Feature.PROGRESSIVE)
						.booleanValue()
				&& !phrase.hasFeature(Feature.MODAL)
				&& !Tense.FUTURE.equals(phrase.getTense())
				&& !phrase.isNegated()
				&& !phrase.getFeatureAsBoolean(Feature.PASSIVE).booleanValue()) {
			addDoAuxiliary(phrase, phraseFactory, realisedElement);
		} else {
			splitVerb = realiseSubjects(phrase);
		}
		return splitVerb;
	}

	/**
	 * Add a modifier to a clause Use heuristics to decide where it goes
	 * 
	 * code moved from simplenl.phrasespec.SPhraseSpec.addModifier(Object modifier)
	 * by vaudrypl
	 * 
	 * @param clause
	 * @param modifier
	 * 
	 */
	@Override
	public void addModifier(SPhraseSpec clause, Object modifier) {
		// adverb is frontModifier if sentenceModifier
		// otherwise adverb is preModifier
		// string which is one lexicographic word is looked up in lexicon,
		// above rules apply if adverb
		// Everything else is postModifier

		if (modifier == null)
			return;

		// get modifier as NLGElement if possible
		NLGElement modifierElement = null;
		if (modifier instanceof NLGElement)
			modifierElement = (NLGElement) modifier;
		else if (modifier instanceof String) {
			String modifierString = (String) modifier;
			if (modifierString.length() > 0 && !modifierString.contains(" "))
				modifierElement = clause.getFactory().createWord(modifier,
						LexicalCategory.ANY);
		}

		// if no modifier element, must be a complex string
		if (modifierElement == null) {
			clause.addPostModifier((String) modifier);
			return;
		}

		// AdvP is premodifer (probably should look at head to see if
		// sentenceModifier)
		if (modifierElement instanceof AdvPhraseSpec) {
			clause.addPreModifier(modifierElement);
			return;
		}

		// extract WordElement if modifier is a single word
		WordElement modifierWord = null;
		if (modifierElement != null && modifierElement instanceof WordElement)
			modifierWord = (WordElement) modifierElement;
		else if (modifierElement != null
				&& modifierElement instanceof InflectedWordElement)
			modifierWord = ((InflectedWordElement) modifierElement)
					.getBaseWord();

		if (modifierWord != null
				&& modifierWord.getCategory() == LexicalCategory.ADVERB) {
			// adverb rules
			if (modifierWord
					.getFeatureAsBoolean(LexicalFeature.SENTENCE_MODIFIER))
				clause.addFrontModifier(modifierWord);
			else
				clause.addPreModifier(modifierWord);
			return;
		}

		// default case
		clause.addPostModifier(modifierElement);
	}
		
}
