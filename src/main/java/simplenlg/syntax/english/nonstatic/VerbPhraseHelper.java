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

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.InternalFeature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.syntax.AbstractVerbPhraseHelper;

/**
 * <p>
 * This class contains methods to help the syntax processor realise verb
 * phrases for English. It adds auxiliary verbs into the element tree as required.
 * It is a non static version by vaudrypl of the class of
 * the same name in the <code>simplenlg.syntax.english</code> package.
 * </p>
 * modified by vaudrypl :
 * abstract class replaced by public class
 * private static methods replaced by protected methods
 * parent.realise(element) replaced by element.realiseSyntax()
 * SyntaxProcessor parent arguments removed
 * PhraseHelper replaced by phrase.getPhraseHelper()
 * now extends AbstractVerbPhraseHelper
 * 
 * some methods now moved to AbstractClauseHelper
 *
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 */
public class VerbPhraseHelper extends AbstractVerbPhraseHelper {

	/**
	 * Splits the stack of verb components into two sections. One being the verb
	 * associated with the main verb group, the other being associated with the
	 * auxiliary verb group.
	 * 
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param mainVerbRealisation
	 *            the main group of verbs.
	 * @param auxiliaryRealisation
	 *            the auxiliary group of verbs.
	 */
	@Override
	protected void splitVerbGroup(Stack<NLGElement> vgComponents,
			Stack<NLGElement> mainVerbRealisation,
			Stack<NLGElement> auxiliaryRealisation) {

		boolean mainVerbSeen = false;

		for (NLGElement word : vgComponents) {
			if (!mainVerbSeen) {
				mainVerbRealisation.push(word);
				if (!word.equals("not")) { //$NON-NLS-1$
					mainVerbSeen = true;
				}
			} else {
				auxiliaryRealisation.push(word);
			}
		}

	}

	/**
	 * Creates a stack of verbs for the verb phrase. Additional auxiliary verbs
	 * are added as required based on the features of the verb phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @return the verb group as a <code>Stack</code> of <code>NLGElement</code>
	 *         s.
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected Stack<NLGElement> createVerbGroup(PhraseElement phrase) {

		String actualModal = null;
		Object formValue = phrase.getFeature(Feature.FORM);
		Tense tenseValue = phrase.getTense();
		String modal = phrase.getFeatureAsString(Feature.MODAL);
		boolean modalPast = false;
		Stack<NLGElement> vgComponents = new Stack<NLGElement>();
		boolean interrogative = phrase.hasFeature(Feature.INTERROGATIVE_TYPE);

		if (Form.GERUND.equals(formValue) || Form.INFINITIVE.equals(formValue)) {
			tenseValue = Tense.PRESENT;
		}
		
		if (Form.INFINITIVE.equals(formValue)) {
			actualModal = "to"; //$NON-NLS-1$
		
		} else if (formValue == null || Form.NORMAL.equals(formValue)
				|| Form.SUBJUNCTIVE.equals(formValue)) {
			if (Tense.FUTURE.equals(tenseValue)
					&& modal == null
					&& ((!(phrase.getHead() instanceof CoordinatedPhraseElement)) || (phrase
							.getHead() instanceof CoordinatedPhraseElement && interrogative))) {

				actualModal = "will"; //$NON-NLS-1$
		
			} else if (Tense.CONDITIONAL.equals(tenseValue)
					&& modal == null
					&& ((!(phrase.getHead() instanceof CoordinatedPhraseElement)) || (phrase
							.getHead() instanceof CoordinatedPhraseElement && interrogative))) {

				actualModal = "could"; //$NON-NLS-1$
		
			} else if (modal != null) {
				actualModal = modal;

				if (Tense.PAST.equals(tenseValue)) {
					modalPast = true;
				}
			}
		}
		
		pushParticles(phrase, vgComponents);
		NLGElement frontVG = grabHeadVerb(phrase, tenseValue, modal != null);
		checkImperativeInfinitive(formValue, frontVG);
		
		if (phrase.getFeatureAsBoolean(Feature.PASSIVE).booleanValue()) {
			frontVG = addBe(frontVG, vgComponents, Form.PAST_PARTICIPLE);
		}
	
		if (phrase.getFeatureAsBoolean(Feature.PROGRESSIVE).booleanValue()) {
			frontVG = addBe(frontVG, vgComponents, Form.PRESENT_PARTICIPLE);
		}
		
		if (phrase.getFeatureAsBoolean(Feature.PERFECT).booleanValue()
				|| modalPast) {
			frontVG = addHave(frontVG, vgComponents, modal, tenseValue);
		}
		
		frontVG = pushIfModal(actualModal != null, phrase, frontVG,
				vgComponents);
		frontVG = createNot(phrase, vgComponents, frontVG, modal != null);
		
		if (frontVG != null) {
			pushFrontVerb(phrase, vgComponents, frontVG, formValue,
					interrogative);
		}
		
		pushModal(actualModal, phrase, vgComponents);
		return vgComponents;
	}

	/**
	 * Pushes the modal onto the stack of verb components.
	 * 
	 * @param actualModal
	 *            the modal to be used.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 */
	protected void pushModal(String actualModal, PhraseElement phrase,
			Stack<NLGElement> vgComponents) {
		if (actualModal != null
				&& !phrase.getFeatureAsBoolean(InternalFeature.IGNORE_MODAL)
						.booleanValue()) {
			vgComponents.push(new InflectedWordElement(actualModal,
					LexicalCategory.MODAL));
		}
	}

	/**
	 * Pushes the front verb onto the stack of verb components.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param formValue
	 *            the <code>Form</code> of the phrase.
	 * @param interrogative
	 *            <code>true</code> if the phrase is interrogative.
	 */
	protected void pushFrontVerb(PhraseElement phrase,
			Stack<NLGElement> vgComponents, NLGElement frontVG,
			Object formValue, boolean interrogative) {
		
		if (Form.GERUND.equals(formValue)) {
			frontVG.setFeature(Feature.FORM, Form.PRESENT_PARTICIPLE);
			vgComponents.push(frontVG);
		
		} else if (Form.PAST_PARTICIPLE.equals(formValue)) {
			frontVG.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
			vgComponents.push(frontVG);
		
		} else if (Form.PRESENT_PARTICIPLE.equals(formValue)) {
			frontVG.setFeature(Feature.FORM, Form.PRESENT_PARTICIPLE);
			vgComponents.push(frontVG);
		
		} else if ((!(formValue == null || Form.NORMAL.equals(formValue)
							|| Form.SUBJUNCTIVE.equals(formValue)) || interrogative)
				&& !isCopular(phrase.getHead()) && vgComponents.isEmpty()) {

			if (!InterrogativeType.WHO_SUBJECT.equals(phrase
					.getFeature(Feature.INTERROGATIVE_TYPE))) {
				frontVG.setFeature(InternalFeature.NON_MORPH, true);
			}
			vgComponents.push(frontVG);
		
		} else {
			NumberAgreement numToUse = determineNumber(phrase.getParent(),
					phrase);
			frontVG.setTense(phrase.getTense());
			frontVG.setFeature(Feature.PERSON, phrase
					.getFeature(Feature.PERSON));
			frontVG.setFeature(Feature.NUMBER, numToUse);
			vgComponents.push(frontVG);
		}
	}

	/**
	 * Adds <em>not</em> to the stack if the phrase is negated.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param hasModal
	 *            the phrase has a modal
	 * @return the new element for the front of the group.
	 */
	protected NLGElement createNot(PhraseElement phrase,
			Stack<NLGElement> vgComponents, NLGElement frontVG, boolean hasModal) {
		NLGElement newFront = frontVG;

		if (phrase.isNegated()) {
			if (!vgComponents.empty() || frontVG != null && isCopular(frontVG)) {
				vgComponents.push(new InflectedWordElement(
						"not", LexicalCategory.ADVERB)); //$NON-NLS-1$
			} else {
				if (frontVG != null && !hasModal) {
					frontVG.setNegated(true);
					vgComponents.push(frontVG);
				}

				vgComponents.push(new InflectedWordElement(
						"not", LexicalCategory.ADVERB)); //$NON-NLS-1$
				// vaudrypl changed InflectedWordElement constructor call
				WordElement auxiliary = (WordElement) phrase.getFactory().createWord("do", LexicalCategory.VERB);
				newFront = new InflectedWordElement(auxiliary); //$NON-NLS-1$
			}
		}
		return newFront;
	}

	/**
	 * Pushes the front verb on to the stack if the phrase has a modal.
	 * 
	 * @param hasModal
	 *            the phrase has a modal
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @return the new element for the front of the group.
	 */
	protected NLGElement pushIfModal(boolean hasModal,
			PhraseElement phrase, NLGElement frontVG,
			Stack<NLGElement> vgComponents) {

		NLGElement newFront = frontVG;
		if (hasModal
				&& !phrase.getFeatureAsBoolean(InternalFeature.IGNORE_MODAL)
						.booleanValue()) {
			if (frontVG != null) {
				frontVG.setFeature(InternalFeature.NON_MORPH, true);
				vgComponents.push(frontVG);
			}
			newFront = null;
		}
		return newFront;
	}

	/**
	 * Adds <em>have</em> to the stack.
	 * 
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param modal
	 *            the modal to be used.
	 * @param tenseValue
	 *            the <code>Tense</code> of the phrase.
	 * @return the new element for the front of the group.
	 */
	protected NLGElement addHave(NLGElement frontVG,
			Stack<NLGElement> vgComponents, String modal, Tense tenseValue) {
		NLGElement newFront = frontVG;

		if (frontVG != null) {
			frontVG.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
			vgComponents.push(frontVG);
		}
		// vaudrypl changed InflectedWordElement constructor call
		WordElement auxiliary = frontVG.getLexicon().lookupWord("have", LexicalCategory.VERB);
		newFront = new InflectedWordElement(auxiliary); //$NON-NLS-1$
		newFront.setTense(tenseValue);
		if (modal != null) {
			newFront.setFeature(InternalFeature.NON_MORPH, true);
		}
		return newFront;
	}

	/**
	 * Adds the <em>be</em> verb to the front of the group.
	 * 
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param frontForm
	 *            the form the current front verb is to take.
	 * @return the new element for the front of the group.
	 */
	protected NLGElement addBe(NLGElement frontVG,
			Stack<NLGElement> vgComponents, Form frontForm) {

		if (frontVG != null) {
			frontVG.setFeature(Feature.FORM, frontForm);
			vgComponents.push(frontVG);
		}
		// vaudrypl changed InflectedWordElement constructor call
		WordElement auxiliary = frontVG.getLexicon().lookupWord("be", LexicalCategory.VERB);
		return new InflectedWordElement(auxiliary); //$NON-NLS-1$
	}

	/**
	 * Checks to see if the phrase is in imperative, infinitive or bare
	 * infinitive form. If it is then no morphology is done on the main verb.
	 * 
	 * @param formValue
	 *            the <code>Form</code> of the phrase.
	 * @param frontVG
	 *            the first verb in the verb group.
	 */
	protected void checkImperativeInfinitive(Object formValue,
			NLGElement frontVG) {

		if ((Form.IMPERATIVE.equals(formValue)
				|| Form.INFINITIVE.equals(formValue) || Form.BARE_INFINITIVE
				.equals(formValue))
				&& frontVG != null) {
			frontVG.setFeature(InternalFeature.NON_MORPH, true);
		}
	}

	/**
	 * Grabs the head verb of the verb phrase and sets it to future tense if the
	 * phrase is future tense. It also turns off negation if the group has a
	 * modal.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param tenseValue
	 *            the <code>Tense</code> of the phrase.
	 * @param hasModal
	 *            <code>true</code> if the verb phrase has a modal.
	 * @return the modified head element
	 */
	protected NLGElement grabHeadVerb(PhraseElement phrase,
			Tense tenseValue, boolean hasModal) {
		NLGElement frontVG = phrase.getHead();
		
		if (frontVG instanceof WordElement)
			frontVG = new InflectedWordElement((WordElement) frontVG);

		if (Tense.FUTURE.equals(tenseValue) && frontVG != null) {
			frontVG.setTense(Tense.FUTURE);
		}
		
		if (hasModal && frontVG != null) {
			frontVG.setNegated(false);
		}
		
		return frontVG;
	}

	/**
	 * Pushes the particles of the main verb onto the verb group stack.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 */
	protected void pushParticles(PhraseElement phrase,
			Stack<NLGElement> vgComponents) {
		Object particle = phrase.getFeature(Feature.PARTICLE);

		if (particle instanceof String) {
			vgComponents.push(new StringElement((String) particle));
			
		} else if (particle instanceof NLGElement) {
			vgComponents.push((NLGElement)particle).realiseSyntax();
		}
	}

	/**
	 * Determines the number agreement for the phrase ensuring that any number
	 * agreement on the parent element is inherited by the phrase.
	 * 
	 * @param parent
	 *            the parent element of the phrase.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @return the <code>NumberAgreement</code> to be used for the phrase.
	 */
	protected NumberAgreement determineNumber(NLGElement parent,
			PhraseElement phrase) {
		Object numberValue = phrase.getFeature(Feature.NUMBER);
		NumberAgreement number = null;
		if (numberValue != null && numberValue instanceof NumberAgreement) {
			number = (NumberAgreement) numberValue;
		} else {
			number = NumberAgreement.SINGULAR;
		}

		if (parent instanceof PhraseElement) {
			if (parent.isA(PhraseCategory.CLAUSE)
					&& phrase.getPhraseHelper().isExpletiveSubject((PhraseElement) parent)
					&& isCopular(phrase.getHead())) {

				if (hasPluralComplement(phrase
						.getFeatureAsElementList(InternalFeature.COMPLEMENTS))) {
					number = NumberAgreement.PLURAL;
				} else {
					number = NumberAgreement.SINGULAR;
				}
			}
		}
		return number;
	}

	/**
	 * Checks to see if any of the complements to the phrase are plural.
	 * 
	 * @param complements
	 *            the list of complements of the phrase.
	 * @return <code>true</code> if any of the complements are plural.
	 */
	protected boolean hasPluralComplement(List<NLGElement> complements) {
		boolean plural = false;
		Iterator<NLGElement> complementIterator = complements.iterator();
		NLGElement eachComplement = null;
		Object numberValue = null;

		while (complementIterator.hasNext() && !plural) {
			eachComplement = complementIterator.next();

			if (eachComplement != null
					&& eachComplement.isA(PhraseCategory.NOUN_PHRASE)) {

				numberValue = eachComplement.getFeature(Feature.NUMBER);
				if (numberValue != null
						&& NumberAgreement.PLURAL.equals(numberValue)) {
					plural = true;
				}
			}
		}
		return plural;
	}

	/**
	 * Checks to see if the base form of the word is copular, i.e. <em>be</em>.
	 * 
	 * @param element
	 *            the element to be checked
	 * @return <code>true</code> if the element is copular.
	 */
	@Override
	public boolean isCopular(NLGElement element) {
		boolean copular = false;
		if (element instanceof InflectedWordElement) {
			copular = "be".equalsIgnoreCase(((InflectedWordElement) element) //$NON-NLS-1$
					.getBaseForm());
		} else if (element instanceof WordElement) {
			copular = "be".equalsIgnoreCase(((WordElement) element) //$NON-NLS-1$
					.getBaseForm());
		}
		return copular;
	}
	
	/**
	 * Add a modifier to a verb phrase. Use heuristics to decide where it goes.
	 * 
	 * code moved from simplenl.phrasespec.VPPhraseSpec.addModifier(Object modifier)
	 * by vaudrypl
	 * 
	 * @param verbPhrase
	 * @param modifier
	 * 
	 */
	@Override
	public void addModifier(VPPhraseSpec verbPhrase, Object modifier) {
		// adverb is preModifier
		// string which is one lexicographic word is looked up in lexicon,
		// if it is an adverb than it becomes a preModifier
		// Everything else is postModifier
		
		if (modifier == null)
			return;
		
		// get modifier as NLGElement if possible
		NLGElement modifierElement = null;
		if (modifier instanceof NLGElement)
			modifierElement = (NLGElement) modifier;
		else if (modifier instanceof String) {
			String modifierString = (String)modifier;
			if (modifierString.length() > 0 && !modifierString.contains(" "))
				modifierElement = verbPhrase.getFactory().createWord(modifier, LexicalCategory.ANY);
		}
		
		// if no modifier element, must be a complex string
		if (modifierElement == null) {
			verbPhrase.addPostModifier((String)modifier);
			return;
		}
		
		// extract WordElement if modifier is a single word
		WordElement modifierWord = null;
		if (modifierElement != null && modifierElement instanceof WordElement)
			modifierWord = (WordElement) modifierElement;
		else if (modifierElement != null && modifierElement instanceof InflectedWordElement)
			modifierWord = ((InflectedWordElement) modifierElement).getBaseWord();
		
		if (modifierWord != null && modifierWord.getCategory() == LexicalCategory.ADVERB) {
			verbPhrase.addPreModifier(modifierWord);
			return;
		}
		
		// default case
		verbPhrase.addPostModifier(modifierElement);
	}
}
