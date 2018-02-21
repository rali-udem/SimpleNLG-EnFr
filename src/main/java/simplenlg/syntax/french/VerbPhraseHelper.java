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
package simplenlg.syntax.french;

import java.util.List;
import java.util.Stack;

import simplenlg.features.ClauseStatus;
import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.LexicalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.features.french.FrenchFeature;
import simplenlg.features.french.FrenchLexicalFeature;
import simplenlg.features.french.PronounType;
import simplenlg.features.french.FrenchInternalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * This class contains static methods to help the syntax processor realise verb
 * phrases for French.
 * 
 * Reference :
 * 
 * Grevisse, Maurice (1993). Le bon usage, grammaire française,
 * 12e édition refondue par André Goosse, 8e tirage, Éditions Duculot,
 * Louvain-la-Neuve, Belgique.
 * 
 * @author vaudrypl
 */
public class VerbPhraseHelper extends simplenlg.syntax.english.nonstatic.VerbPhraseHelper {

	/**
	 * The main method for realising verb phrases.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> to be realised.
	 * @return the realised <code>NLGElement</code>.
	 */
	@Override
	public NLGElement realise(PhraseElement phrase) {
		ListElement realisedElement = null;
		Stack<NLGElement> vgComponents = null;
		Stack<NLGElement> mainVerbRealisation = new Stack<NLGElement>();
		Stack<NLGElement> auxiliaryRealisation = new Stack<NLGElement>();

		if (phrase != null) {
			vgComponents = createVerbGroup(phrase);
			splitVerbGroup(vgComponents, mainVerbRealisation,
					auxiliaryRealisation);

			// vaudrypl added phrase argument to ListElement constructor
			// to copy all features from the PhraseElement
			realisedElement = new ListElement(phrase);

			if ((!phrase.hasFeature(InternalFeature.REALISE_AUXILIARY)
					|| phrase.getFeatureAsBoolean(InternalFeature.REALISE_AUXILIARY))
							&& !auxiliaryRealisation.isEmpty()) {

				realiseAuxiliaries(realisedElement,
						auxiliaryRealisation);

				NLGElement verb = mainVerbRealisation.peek();
				Object verbForm = verb.getFeature(Feature.FORM);
				if (verbForm == Form.INFINITIVE) {
					realiseMainVerb(phrase, mainVerbRealisation,
							realisedElement);
					phrase.getPhraseHelper().realiseList(realisedElement, phrase
							.getPreModifiers(), DiscourseFunction.PRE_MODIFIER);
				} else {
					phrase.getPhraseHelper().realiseList(realisedElement, phrase
							.getPreModifiers(), DiscourseFunction.PRE_MODIFIER);
					realiseMainVerb(phrase, mainVerbRealisation,
							realisedElement);
				}
			} else {
				realiseMainVerb(phrase, mainVerbRealisation,
						realisedElement);
				phrase.getPhraseHelper().realiseList(realisedElement, phrase
						.getPreModifiers(), DiscourseFunction.PRE_MODIFIER);
			
			}
			realiseComplements(phrase, realisedElement);
			phrase.getPhraseHelper().realiseList(realisedElement, phrase
					.getPostModifiers(), DiscourseFunction.POST_MODIFIER);
		}
		
		return realisedElement;
	}

	/**
	 * Checks to see if the base form of the word is copular.
	 * 
	 * @param element
	 *            the element to be checked
	 * @return <code>true</code> if the element is copular.
	 */
	@Override
	public boolean isCopular(NLGElement element) {
		if (element != null) {
			return element.getFeatureAsBoolean(FrenchLexicalFeature.COPULAR);
		} else return true;
	}

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
		boolean cliticsSeen = false;

		for (NLGElement word : vgComponents) {
			if (!mainVerbSeen) {
				mainVerbRealisation.push(word);
//				if (!word.equals("pas") &&
				if (!word.isA(LexicalCategory.ADVERB) &&
						!word.getFeatureAsBoolean(FrenchInternalFeature.CLITIC)) {
					mainVerbSeen = true;
				}
			} else if (!cliticsSeen) {
//				if (!word.equals("ne") &&
				if (!"ne".equals(word.getFeatureAsString(LexicalFeature.BASE_FORM)) &&
						!word.getFeatureAsBoolean(FrenchInternalFeature.CLITIC)) {
					cliticsSeen = true;
					auxiliaryRealisation.push(word);
				} else {
					mainVerbRealisation.push(word);
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
	 * Based on English method of the same name.
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
		boolean progressive = phrase.getFeatureAsBoolean(Feature.PROGRESSIVE);
		boolean perfect = phrase.getFeatureAsBoolean(Feature.PERFECT);
		boolean passive = phrase.getFeatureAsBoolean(Feature.PASSIVE);
		boolean negative = phrase.getFeatureAsBoolean(Feature.NEGATED);
		NLGFactory factory = phrase.getFactory();
		boolean insertClitics = true;
		
		// With "si" as complemetiser, change future to present,
		// conditional present to "imparfait"
		// and conditional past to "plus-que-parfait".
		NLGElement parent = phrase.getParent();
		if ( parent != null
				&& parent.getFeature(InternalFeature.CLAUSE_STATUS)
					== ClauseStatus.SUBORDINATE
				&& !parent.getFeatureAsBoolean(Feature.SUPRESSED_COMPLEMENTISER) ) {

			NLGElement complementiser = factory.createWord(
					parent.getFeature(Feature.COMPLEMENTISER), LexicalCategory.COMPLEMENTISER);
			NLGElement si = factory.createWord("si", LexicalCategory.COMPLEMENTISER);
			if (complementiser == si) {
				if (tenseValue == Tense.FUTURE) tenseValue = Tense.PRESENT;
				else if (tenseValue == Tense.CONDITIONAL) {
					tenseValue = Tense.PAST;
					if (!perfect) progressive = true;
				}
			}
		}
		
		WordElement modalWord =	null;
		boolean cliticRising = false;
		if (modal != null) {
			modalWord = phrase.getLexicon().lookupWord(modal, LexicalCategory.VERB);
			cliticRising = modalWord.getFeatureAsBoolean(FrenchLexicalFeature.CLITIC_RISING);
		}

		if (Form.INFINITIVE.equals(formValue)) {
			actualModal = null;
		
		} else if (formValue == null || formValue == Form.NORMAL
				|| (formValue == Form.IMPERATIVE && cliticRising)) {
			if (modal != null) {
				actualModal = modal;

				if (Tense.PAST.equals(tenseValue)) {
					modalPast = true;
				}
			}
		}
		
		if (actualModal == null) modalWord = null;
		
		NLGElement frontVG = grabHeadVerb(phrase, tenseValue, modal != null);
		if (frontVG == null) return vgComponents;
		frontVG.setFeature(Feature.TENSE, tenseValue);
		
		if (passive) {
			frontVG = addPassiveAuxiliary(frontVG, vgComponents, phrase);
			frontVG.setFeature(Feature.TENSE, tenseValue);
		}
		
		// progressive not perfect past = "imparfait"
		// the rest is with "être en train de" auxiliary
		if (progressive	&& (tenseValue != Tense.PAST
					|| perfect || actualModal != null
					|| formValue == Form.SUBJUNCTIVE)) {
			NLGElement newFront =
					addProgressiveAuxiliary(frontVG, vgComponents, factory, phrase);
			if (frontVG != newFront) {
				frontVG = newFront;
				frontVG.setFeature(Feature.TENSE, tenseValue);
				insertClitics = false;
			}
		}
		
		// "avoir" or "être" auxiliary for "temps composés"
		// past not perfect not progressive and present perfect = "passé composé"
		// past perfect = "plus-que-parfait"
		AddAuxiliaryReturn auxReturn = null;
		if ((tenseValue == Tense.PAST &&
					(!progressive || perfect || formValue == Form.SUBJUNCTIVE))
				|| (tenseValue == Tense.PRESENT && perfect)
				|| modalPast) {
			Tense tense = perfect ? tenseValue : Tense.PRESENT;
			auxReturn = addAuxiliary(frontVG, vgComponents, modal, tense, phrase);
			frontVG = auxReturn.newFront;
			// subjunctive past "surcomposé"
			if (formValue == Form.SUBJUNCTIVE && tenseValue == Tense.PAST && perfect) {
				// Auxiliary "être" goes before auxiliary "avoir" with pronominal verbs.
				if (hasReflexiveObject(phrase)) {
					NLGElement avoirPastParticiple = factory.createWord("avoir", LexicalCategory.VERB);
					avoirPastParticiple.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
					vgComponents.push(avoirPastParticiple);
				} else {
					auxReturn = addAuxiliary(frontVG, vgComponents, modal, tense, phrase);
					frontVG = auxReturn.newFront;
				}
			}
		// future perfect = "futur antérieur" and conditional past ("conditionnel passé")
		} else if ((tenseValue == Tense.FUTURE || tenseValue == Tense.CONDITIONAL) && perfect) {
			auxReturn = addAuxiliary(frontVG, vgComponents, modal, tenseValue, phrase);
			frontVG = auxReturn.newFront;
		}
		
		frontVG = pushIfModal(actualModal != null, phrase, frontVG,	vgComponents);
		// insert clitics here if imperative and not negative
		// or if there is a modal verb without clitic rising 
		NLGElement cliticDirectObject = null;
		if (insertClitics) {
			if (!negative && formValue == Form.IMPERATIVE) {
				cliticDirectObject = insertCliticComplementPronouns(phrase, vgComponents);
				insertClitics = false;
			} else if (frontVG == null) {
				if (!cliticRising) {
					cliticDirectObject = insertCliticComplementPronouns(phrase, vgComponents);
					insertClitics = false;
				}
			}
		}
		
		createPas(phrase, vgComponents, frontVG, modal != null);
		
		pushModal(modalWord, phrase, vgComponents);
		
		if (frontVG != null) {
			pushFrontVerb(phrase, vgComponents, frontVG, formValue,
					interrogative);
			frontVG.setFeature(Feature.FORM, formValue);
		}
		// default place for inserting clitic complement pronouns
		if (insertClitics) {
			cliticDirectObject = insertCliticComplementPronouns(phrase, vgComponents);
			insertClitics = false;
		}
		createNe(phrase, vgComponents);
		
		if (auxReturn != null) {
			// Check if verb phrase is part of a relative clause with
			// the relative phrase being a direct object. In that case,
			// Make object agreement with the parent NP of the clause.
			if (!passive && parent != null && parent.hasRelativePhrase(DiscourseFunction.OBJECT)) {
				NLGElement grandParent = parent.getParent();
				if (grandParent instanceof NPPhraseSpec) {
					cliticDirectObject = grandParent;
				}
			}
			makePastParticipleWithAvoirAgreement(auxReturn.pastParticipleAvoir, cliticDirectObject);
		}

		return vgComponents;
	}

	/**
	 * Transfers the agreement features from the direct object to
	 * the past participle with auxiliary "avoir" if the direct object
	 * is placed before the past participle. (For now, this only means
	 * if there is a direct object clitic pronoun. Eventually it will
	 * include checks for relative clause, etc.)
	 * 
	 * @param auxReturn
	 * @param cliticDirectObject
	 */
	protected void makePastParticipleWithAvoirAgreement(
			NLGElement pastParticiple, NLGElement cliticDirectObject) {
		
		if (pastParticiple != null && cliticDirectObject != null) {
			Object gender = cliticDirectObject.getFeature(LexicalFeature.GENDER);
			if (gender instanceof Gender) {
				pastParticiple.setFeature(LexicalFeature.GENDER, gender);
			}
			
			Object number = cliticDirectObject.getFeature(Feature.NUMBER);
			if (number instanceof NumberAgreement) {
				pastParticiple.setFeature(Feature.NUMBER, number);
			}
		}
	}

	/**
	 * Determine wich pronominal complements are clitics and inserts
	 * them in the verb group components.
	 * Reference : section 657 of Grevisse (1993)
	 * 
	 * @param phrase
	 * @param vgComponents
	 */
	protected NLGElement insertCliticComplementPronouns(PhraseElement phrase,
			Stack<NLGElement> vgComponents) {
		List<NLGElement> complements =
			phrase.getFeatureAsElementList(InternalFeature.COMPLEMENTS);
		boolean passive = phrase.getFeatureAsBoolean(Feature.PASSIVE);
		NLGElement pronounEn = null, pronounY = null,
					directObject = null, indirectObject = null;

		// identify clitic candidates
		for (NLGElement complement : complements) {
			if (complement != null && !complement.getFeatureAsBoolean(Feature.ELIDED)) {
				Object discourseValue = complement.getFeature(InternalFeature.DISCOURSE_FUNCTION);
				if (!(discourseValue instanceof DiscourseFunction)) {
					discourseValue = DiscourseFunction.COMPLEMENT;
				}
				// Realise complement only if it is not the relative phrase of
				// the parent clause and not a phrase with the same function in case
				// of a direct or indirect object.
				NLGElement parent = phrase.getParent();
				if ( parent == null ||
						(complement != parent.getFeatureAsElement(FrenchFeature.RELATIVE_PHRASE) &&
							(discourseValue == DiscourseFunction.COMPLEMENT ||
								!parent.hasRelativePhrase((DiscourseFunction) discourseValue)))) {
					NLGElement head = null;
					Object type = null;
					
					// if a complement is or contains a pronoun, or will be pronominalised
					if (complement.isA(LexicalCategory.PRONOUN)) {
						head = complement;
					} else if (complement instanceof NPPhraseSpec
							&& ((NPPhraseSpec)complement).getHead() != null
							&& ((NPPhraseSpec)complement).getHead().isA(LexicalCategory.PRONOUN)) {
						head = ((NPPhraseSpec)complement).getHead();
					}
					else if (complement.getFeatureAsBoolean(Feature.PRONOMINAL)) {
						type = PronounType.PERSONAL;
					}
					
					if (head != null) {
						type = head.getFeature(FrenchLexicalFeature.PRONOUN_TYPE);
					}
					
					if (type != null) {
						complement.setFeature(FrenchInternalFeature.CLITIC, false);
						if (type == PronounType.SPECIAL_PERSONAL) {
							String baseForm = ((WordElement)head).getBaseForm();
							if (baseForm.equals("en")) {
								pronounEn = complement;
							}
							else if (baseForm.equals("y")) {
								pronounY = complement;
							}
						} else if (type == PronounType.PERSONAL) {
							Object discourseFunction = complement.getFeature(InternalFeature.DISCOURSE_FUNCTION);
							if (discourseFunction == DiscourseFunction.OBJECT && !passive) {
								directObject = complement;
							} else if (discourseFunction == DiscourseFunction.INDIRECT_OBJECT) {
								indirectObject = complement;
							}
						}
					}
				}
			}
		}
		
		// place clitics in order :
		// (indirect object) (direct object) y en
		
		if (pronounEn != null) {
			pronounEn.setFeature(FrenchInternalFeature.CLITIC, true);
			vgComponents.push(pronounEn);
		}
		
		if (pronounY != null) {
			pronounY.setFeature(FrenchInternalFeature.CLITIC, true);
			vgComponents.push(pronounY);
		}
		
		if (directObject != null) {
			directObject.setFeature(FrenchInternalFeature.CLITIC, true);
			vgComponents.push(directObject);
		}
		
		// the indirect object is clitic if there's no direct object
		// or if it is third person and not reflexive
		if ( indirectObject != null && (directObject == null || 
				((directObject.getFeature(Feature.PERSON) == Person.THIRD
						|| directObject.getFeature(Feature.PERSON) == null)
					&& !directObject.getFeatureAsBoolean(LexicalFeature.REFLEXIVE) )) ) {
			
			indirectObject.setFeature(FrenchInternalFeature.CLITIC, true);

			Object person = indirectObject.getFeature(Feature.PERSON);
			boolean luiLeurPronoun = (person == null || person == Person.THIRD);
			// place indirect object after direct object if indirect object is "lui" or "leur"
			if (directObject != null && luiLeurPronoun) vgComponents.pop();
			vgComponents.push(indirectObject);
			if (directObject != null && luiLeurPronoun) {
				vgComponents.push(directObject);
			}
		}
		
		// return the direct object for use with past participle agreement with auxiliary "avoir"
		return directObject;
	}

	/**
	 * Checks to see if the phrase is in infinitive form. If it is then
	 * no morphology is done on the main verb.
	 * 
	 * Based on English method checkImperativeInfinitive(...)
	 * 
	 * @param formValue
	 *            the <code>Form</code> of the phrase.
	 * @param frontVG
	 *            the first verb in the verb group.
	 */
	protected void checkInfinitive(Object formValue,
			NLGElement frontVG) {

		if ((Form.INFINITIVE.equals(formValue) || Form.BARE_INFINITIVE.equals(formValue))
				&& frontVG != null) {
			frontVG.setFeature(InternalFeature.NON_MORPH, true);
		}
	}

	/**
	 * Adds the passive auxiliary verb to the front of the group.
	 * 
	 * Based on English method addBe(...)
	 * 
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @return the new element for the front of the group.
	 */
	protected NLGElement addPassiveAuxiliary(NLGElement frontVG,
			Stack<NLGElement> vgComponents, PhraseElement phrase) {

		// adds the current front verb in pas participle form
		// with aggreement with the subject (auxiliary "être")
		if (frontVG != null) {
			frontVG.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
			Object number = phrase.getFeature(Feature.NUMBER);
			frontVG.setFeature(Feature.NUMBER, number);
			Object gender = phrase.getFeature(LexicalFeature.GENDER);
			frontVG.setFeature(LexicalFeature.GENDER, gender);
			vgComponents.push(frontVG);
		}
		// adds auxiliary "être"
		WordElement passiveAuxiliary = (WordElement)
			frontVG.getLexicon().lookupWord("être", LexicalCategory.VERB); //$NON-NLS-1$
		return new InflectedWordElement(passiveAuxiliary);
	}

	/**
	 * Adds the progressive auxiliary verb to the front of the group.
	 * 
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @return the new element for the front of the group.
	 */
	protected NLGElement addProgressiveAuxiliary(NLGElement frontVG,
			Stack<NLGElement> vgComponents, NLGFactory factory, PhraseElement phrase) {

		// pushes on stack "en train de " + clitics + verb in infinitive form
		if (frontVG != null) {
			frontVG.setFeature(Feature.FORM, Form.INFINITIVE);
			vgComponents.push(frontVG);
			insertCliticComplementPronouns(phrase, vgComponents);
			
			PPPhraseSpec deVerb = factory.createPrepositionPhrase("de");
//				deVerb.setComplement(frontVG);
			NPPhraseSpec train = factory.createNounPhrase("train");
			train.addPostModifier(deVerb);
			PPPhraseSpec enTrain = factory.createPrepositionPhrase("en", train);
			vgComponents.push(enTrain);
			
			// adds auxiliary "être"
			WordElement passiveAuxiliary = (WordElement)
				frontVG.getLexicon().lookupWord("être", LexicalCategory.VERB); //$NON-NLS-1$
			frontVG = new InflectedWordElement(passiveAuxiliary);
		}
		return frontVG;
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
	protected AddAuxiliaryReturn addAuxiliary(NLGElement frontVG,
			Stack<NLGElement> vgComponents, String modal, Tense tenseValue,
			PhraseElement phrase) {
		NLGElement newFront = frontVG, pastParticipleAvoir = null;
		WordElement auxiliaryWord = null;

		if (frontVG != null) {
			frontVG.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
			vgComponents.push(frontVG);
			// choose between "avoir" or "être" as auxiliary
			String auxiliary = "avoir"; //$NON-NLS-1$
			if ( frontVG.getFeatureAsBoolean(FrenchLexicalFeature.AUXILIARY_ETRE)
					|| hasReflexiveObject(phrase) ) {
				// if auxiliary "être", the past participle agrees with the subject
				auxiliary = "être"; //$NON-NLS-1$
				Object number = phrase.getFeature(Feature.NUMBER);
				frontVG.setFeature(Feature.NUMBER, number);
				Object gender = phrase.getFeature(LexicalFeature.GENDER);
				frontVG.setFeature(LexicalFeature.GENDER, gender);
			} else {
				pastParticipleAvoir = frontVG;
			}
			
			auxiliaryWord = (WordElement)
				frontVG.getLexicon().lookupWord(auxiliary, LexicalCategory.VERB); //$NON-NLS-1$
		}
		newFront = new InflectedWordElement(auxiliaryWord);
		newFront.setFeature(Feature.FORM, Form.NORMAL);
		newFront.setFeature(Feature.TENSE, tenseValue);
		
		if (modal != null) {
			newFront.setFeature(InternalFeature.NON_MORPH, true);
		}
		return new AddAuxiliaryReturn(newFront, pastParticipleAvoir);
	}
	
	/**
	 * Says if the verb phrase has a reflexive object (direct or indirect)
	 * 
	 * @param phrase	the verb phrase
	 * @return			true if the verb phrase has a reflexive object (direct or indirect)
	 */
	protected boolean hasReflexiveObject(PhraseElement phrase) {
		boolean reflexiveObjectFound = false;
		List<NLGElement> complements =
			phrase.getFeatureAsElementList(InternalFeature.COMPLEMENTS);
		boolean passive = phrase.getFeatureAsBoolean(Feature.PASSIVE);
		Object subjectPerson = phrase.getFeature(Feature.PERSON);
		Object subjectNumber = phrase.getFeature(Feature.NUMBER);
		if (subjectNumber != NumberAgreement.PLURAL) {
			subjectNumber = NumberAgreement.SINGULAR;
		}
		
		for (NLGElement complement : complements) {
			if (complement != null && !complement.getFeatureAsBoolean(Feature.ELIDED)) {
				
				Object function = complement.getFeature(InternalFeature.DISCOURSE_FUNCTION);
				boolean reflexive = complement.getFeatureAsBoolean(LexicalFeature.REFLEXIVE);
				Object person = complement.getFeature(Feature.PERSON);
				Object number = complement.getFeature(Feature.NUMBER);
				if (number != NumberAgreement.PLURAL) {
					number = NumberAgreement.SINGULAR;
				}
				
				// if the complement is a direct or indirect object
				if ( (function == DiscourseFunction.INDIRECT_OBJECT
						|| (!passive && function == DiscourseFunction.OBJECT))
					// and if it is reflexive, or the same as the subject if not third person
					&& ( reflexive ||
						((person == Person.FIRST || person == Person.SECOND)
								&& person == subjectPerson && number == subjectNumber) )) {
					reflexiveObjectFound = true;
					break;
				}
			}
		}
		
		return reflexiveObjectFound;
	}

	/**
	 * Class used to get two return values from the addAuxiliary method
	 * @author vaudrypl
	 */
	protected class AddAuxiliaryReturn {
		public final NLGElement newFront, pastParticipleAvoir;
		
		public AddAuxiliaryReturn(NLGElement newFront, NLGElement pastParticipleAvoir) {
			this.newFront = newFront;
			this.pastParticipleAvoir = pastParticipleAvoir;
		}
	}

	/**
	 * Adds <em>pas</em> to the stack if the phrase is negated.
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
	protected void createPas(PhraseElement phrase,
			Stack<NLGElement> vgComponents, NLGElement frontVG, boolean hasModal) {
		boolean pasForbiddenByArgument = phrase.checkIfNeOnlyNegation();
		if (phrase.getFeatureAsBoolean(Feature.NEGATED)) {
			// first get negation auxiliary; if not specified, it is "pas" by default
			WordElement negation = null;
			Lexicon lexicon = phrase.getLexicon();
			
			Object negationObject = phrase.getFeature(FrenchFeature.NEGATION_AUXILIARY);
			if (negationObject instanceof WordElement) {
				negation = (WordElement) negationObject;
			} else if (negationObject != null) {
				String negationString;
				if (negationObject instanceof StringElement) {
					negationString = ((StringElement)negationObject).getRealisation();
				} else {
					negationString = negationObject.toString();
				}
				negation = lexicon.lookupWord(negationString);
			}
			
			if (negation == null) {
				negation = lexicon.lookupWord("pas", LexicalCategory.ADVERB);
			}
			// push negation auxiliary if it's not forbidden by arguments that provoke
			// "ne" only negation or if the auxiliary is "plus"
			WordElement plus = lexicon.lookupWord("plus", LexicalCategory.ADVERB);
			if (!pasForbiddenByArgument || plus.equals(negation)) {
				InflectedWordElement inflNegation = new InflectedWordElement( negation ); //$NON-NLS-1$
				vgComponents.push(inflNegation);
			}
		}
	}

	/**
	 * Adds <em>ne</em> to the stack if the phrase is negated or if
	 * it has a suject or complement that provokes "ne" only negation.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 */
	protected void createNe(PhraseElement phrase, Stack<NLGElement> vgComponents) {
		
		boolean neRequiredByArgument = phrase.checkIfNeOnlyNegation();

		if (phrase.getFeatureAsBoolean(Feature.NEGATED) || neRequiredByArgument) {
			InflectedWordElement ne = new InflectedWordElement( (WordElement)
				phrase.getFactory().createWord("ne", LexicalCategory.ADVERB) ); //$NON-NLS-1$
	
			 vgComponents.push(ne);
		}
	}
	
	/**
	 * Determines the number agreement for the phrase.
	 * 
	 * @param parent
	 *            the parent element of the phrase.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @return the <code>NumberAgreement</code> to be used for the phrase.
	 */
	@Override
	protected NumberAgreement determineNumber(NLGElement parent,
			PhraseElement phrase) {
		Object numberValue = phrase.getFeature(Feature.NUMBER);
		NumberAgreement number = null;
		
		if (numberValue instanceof NumberAgreement) {
			number = (NumberAgreement) numberValue;
		} else {
			number = NumberAgreement.SINGULAR;
		}
		
		return number;
	}

	/**
	 * Pushes the front verb onto the stack of verb components.
	 * Sets the front verb features.
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
	@Override
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
		
		} else if (!(formValue == null || Form.NORMAL.equals(formValue)
						|| formValue == Form.SUBJUNCTIVE
						|| formValue == Form.IMPERATIVE )
				&& !isCopular(phrase.getHead()) && vgComponents.isEmpty()) {

			vgComponents.push(frontVG);
		
		} else {
			NumberAgreement numToUse = determineNumber(phrase.getParent(),
					phrase);
			frontVG.setFeature(Feature.PERSON, phrase
					.getFeature(Feature.PERSON));
			frontVG.setFeature(Feature.NUMBER, numToUse);
			vgComponents.push(frontVG);
		}
	}

	/**
	 * Add a modifier to a verb phrase. Use heuristics to decide where it goes.
	 * Based on method of the same name in English verb phrase helper.
	 * Reference : section 935 of Grevisse (1993)
	 * 
	 * @param verbPhrase
	 * @param modifier
	 * 
	 * @author vaudrypl
	 */
	@Override
	public void addModifier(VPPhraseSpec verbPhrase, Object modifier) {
		// Everything is postModifier

		if (modifier != null) {
		
			// get modifier as NLGElement if possible
			NLGElement modifierElement = null;
			if (modifier instanceof NLGElement)
				modifierElement = (NLGElement) modifier;
			else if (modifier instanceof String) {
				String modifierString = (String) modifier;
				if (modifierString.length() > 0 && !modifierString.contains(" "))
					modifierElement = verbPhrase.getFactory().createWord(modifier,
							LexicalCategory.ADVERB);
			}
		
			// if no modifier element, must be a complex string
			if (modifierElement == null) {
				verbPhrase.addPostModifier((String) modifier);
			} else {
				// default case
				verbPhrase.addPostModifier(modifierElement);
			}
		}
	}
		
	/**
	 * Realises the complements of this phrase.
	 * Based on English method of the same name.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	@Override
	protected void realiseComplements(PhraseElement phrase,
			ListElement realisedElement) {

		ListElement indirects = new ListElement();
		ListElement directs = new ListElement();
		ListElement unknowns = new ListElement();
		Object discourseValue = null;
		NLGElement currentElement = null;

		for (NLGElement complement : phrase
				.getFeatureAsElementList(InternalFeature.COMPLEMENTS)) {
			if (!complement.getFeatureAsBoolean(FrenchInternalFeature.CLITIC)) {
				
				discourseValue = complement.getFeature(InternalFeature.DISCOURSE_FUNCTION);

				if (!(discourseValue instanceof DiscourseFunction)) {
					discourseValue = DiscourseFunction.COMPLEMENT;
				}

				// Realise complement only if it is not the relative phrase of
				// the parent clause and not a phrase with the same function in case
				// of a direct or indirect object.
				NLGElement parent = phrase.getParent();
				if ( parent == null ||
						(!complement.getFeatureAsBoolean(FrenchInternalFeature.RELATIVISED) &&
							complement != parent.getFeatureAsElement(FrenchFeature.RELATIVE_PHRASE) &&
							(discourseValue == DiscourseFunction.COMPLEMENT ||
								!parent.hasRelativePhrase((DiscourseFunction) discourseValue)))) {
					
					if (DiscourseFunction.INDIRECT_OBJECT.equals(discourseValue)) {
						complement = checkIndirectObject(complement);
					}
					
					currentElement = complement.realiseSyntax();
	
					if (currentElement != null) {
						currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
								discourseValue);
	
						if (DiscourseFunction.INDIRECT_OBJECT.equals(discourseValue)) {
							indirects.addComponent(currentElement);
						} else if (DiscourseFunction.OBJECT.equals(discourseValue)) {
							directs.addComponent(currentElement);
						} else {
							unknowns.addComponent(currentElement);
						}
					}
				} else {
				// Reset relativised feature if the complement was a relative phrase.
					complement.removeFeature(FrenchInternalFeature.RELATIVISED);
				}
			}
			// Reset the clitic selection feature after use.
			complement.removeFeature(FrenchInternalFeature.CLITIC);
		}
		
		
		// Reference : section 657 of Grevisse (1993)
		// normal order, when complements are all of the same length :
		// direct objects + indirect objects + other complements
		// when objects are longer than others, they are placed after them
		int numberOfWordDirects = NLGElement.countWords(directs.getChildren());
		int numberOfWordIndirects = NLGElement.countWords(indirects.getChildren());
		int numberOfWordUnknowns = NLGElement.countWords(unknowns.getChildren());
		// there are 3*2*1 = 6 orders possible
		if (numberOfWordDirects <= numberOfWordIndirects) {
			if (numberOfWordIndirects <= numberOfWordUnknowns) {
				// normal order
				addDirectObjects(directs, phrase, realisedElement);
				addIndirectObjects(indirects, phrase, realisedElement);
				addUnknownComplements(unknowns, phrase, realisedElement);
			} else if (numberOfWordDirects <= numberOfWordUnknowns) {
				addDirectObjects(directs, phrase, realisedElement);
				addUnknownComplements(unknowns, phrase, realisedElement);
				addIndirectObjects(indirects, phrase, realisedElement);
			} else {
				addUnknownComplements(unknowns, phrase, realisedElement);
				addDirectObjects(directs, phrase, realisedElement);
				addIndirectObjects(indirects, phrase, realisedElement);
			}
		} else {
			if (numberOfWordDirects <= numberOfWordUnknowns) {
				addIndirectObjects(indirects, phrase, realisedElement);
				addDirectObjects(directs, phrase, realisedElement);
				addUnknownComplements(unknowns, phrase, realisedElement);
			} else if (numberOfWordIndirects <= numberOfWordUnknowns) {
				addIndirectObjects(indirects, phrase, realisedElement);
				addUnknownComplements(unknowns, phrase, realisedElement);
				addDirectObjects(directs, phrase, realisedElement);
			} else {
				addUnknownComplements(unknowns, phrase, realisedElement);
				addIndirectObjects(indirects, phrase, realisedElement);
				addDirectObjects(directs, phrase, realisedElement);
			}
		}
	}

	/**
	 * Adds realised direct objects to the complements realisation
	 * @param directs			realised direct objects
	 * @param phrase			the verb phrase to wich belongs those complements
	 * @param realisedElement	complements realisation
	 */
	protected void addDirectObjects(ListElement directs, PhraseElement phrase,
			ListElement realisedElement) {
		boolean passive = phrase.getFeatureAsBoolean(Feature.PASSIVE);
		if (!passive && !InterrogativeType.isObject(phrase
					.getFeature(Feature.INTERROGATIVE_TYPE))) {
			realisedElement.addComponents(directs.getChildren());
		}
	}

	/**
	 * Adds realised indirect objects to the complements realisation
	 * @param indirects			realised indirect objects
	 * @param phrase			the verb phrase to wich belongs those complements
	 * @param realisedElement	complements realisation
	 */
	protected void addIndirectObjects(ListElement indirects, PhraseElement phrase,
			ListElement realisedElement) {
		if (!InterrogativeType.isIndirectObject(phrase
				.getFeature(Feature.INTERROGATIVE_TYPE))) {
			realisedElement.addComponents(indirects.getChildren());
		}
	}

	/**
	 * Adds unknown complements to the complements realisation
	 * @param unknowns			unknown complements
	 * @param phrase			the verb phrase to wich belongs those complements
	 * @param realisedElement	complements realisation
	 */
	protected void addUnknownComplements(ListElement unknowns, PhraseElement phrase,
			ListElement realisedElement) {
		if (!phrase.getFeatureAsBoolean(Feature.PASSIVE)) {
			realisedElement.addComponents(unknowns.getChildren());
		}
	}

	/**
	 * Adds a default preposition to all indirect object noun phrases.
	 * Checks also inside coordinated phrases.
	 * 
	 * @param nounPhrase
	 * @return the new complement
	 * 
	 * @vaudrypl
	 */
	@SuppressWarnings("unchecked")
	protected NLGElement checkIndirectObject(NLGElement element) {
		if (element instanceof NPPhraseSpec) {
			NLGFactory factory = element.getFactory();
			NPPhraseSpec elementCopy = new NPPhraseSpec((NPPhraseSpec) element);
			PPPhraseSpec newElement = factory.createPrepositionPhrase("à", elementCopy);
			element.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.INDIRECT_OBJECT);
			element = newElement;
		} else if (element instanceof CoordinatedPhraseElement) {
			element = new CoordinatedPhraseElement( (CoordinatedPhraseElement) element );
			Object coordinates = element.getFeature(InternalFeature.COORDINATES);
			if (coordinates instanceof List) {
				List<NLGElement> list = (List<NLGElement>) coordinates;
				for (int index = 0; index < list.size(); ++index) {
					list.set(index, checkIndirectObject(list.get(index)));
				}
			}
		}
		
		return element;
	}

	/**
	 * Pushes the modal onto the stack of verb components.
	 * Sets the modal features.
	 * Based on English VerbPhraseHelper
	 * 
	 * @param actualModal
	 *            the modal to be used.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 */
	protected void pushModal(WordElement modalWord, PhraseElement phrase,
			Stack<NLGElement> vgComponents) {
		if (modalWord != null
				&& !phrase.getFeatureAsBoolean(InternalFeature.IGNORE_MODAL)
						.booleanValue()) {
			InflectedWordElement inflectedModal = new InflectedWordElement(modalWord);
			
			Object form = phrase.getFeature(Feature.FORM);
			inflectedModal.setFeature(Feature.FORM, form);
			
			Object tense = phrase.getFeature(Feature.TENSE);
			tense = (tense != Tense.PAST) ? tense : Tense.PRESENT;
			inflectedModal.setFeature(Feature.TENSE, tense);
			
			inflectedModal.setFeature(Feature.PERSON, phrase.getFeature(Feature.PERSON));
			
			NumberAgreement numToUse = determineNumber(phrase.getParent(), phrase);
			inflectedModal.setFeature(Feature.NUMBER, numToUse);
			
			vgComponents.push(inflectedModal);
		}
	}

	/**
	 * Realises the auxiliary verbs in the verb group.
	 * 
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 * @param auxiliaryRealisation
	 *            the stack of auxiliary verbs.
	 */
	@Override
	protected void realiseAuxiliaries(ListElement realisedElement,
			Stack<NLGElement> auxiliaryRealisation) {

		NLGElement aux = null;
		NLGElement currentElement = null;
		while (!auxiliaryRealisation.isEmpty()) {
			aux = auxiliaryRealisation.pop();
			currentElement = aux.realiseSyntax();
			
			if (currentElement != null) {
				realisedElement.addComponent(currentElement);
				
				if (currentElement.isA(LexicalCategory.VERB)
					|| currentElement.isA(LexicalCategory.MODAL)
					|| currentElement.isA(PhraseCategory.VERB_PHRASE)) {
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						DiscourseFunction.AUXILIARY);
				}
			}
		}
	}

}
