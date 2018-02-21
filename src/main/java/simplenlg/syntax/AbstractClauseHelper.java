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

package simplenlg.syntax;

import java.util.List;

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
import simplenlg.features.french.FrenchFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;

/**
 * Abstract class for the clause syntax helper.
 * Most methods taken from or based on English clause syntax helper.
 * 
 * @author vaudrypl
 */
public abstract class AbstractClauseHelper {
	/**
	 * The main method for controlling the syntax realisation of clauses.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representation of the clause.
	 * @return the <code>NLGElement</code> representing the realised clause.
	 */
	public NLGElement realise(PhraseElement phrase) {
		ListElement realisedElement = null;
		NLGFactory phraseFactory = phrase.getFactory();
		NLGElement splitVerb = null;

		if (phrase != null) {
			// vaudrypl added phrase argument to ListElement constructor
			// to copy all features from the PhraseElement
			realisedElement = new ListElement(phrase);
			
			NLGElement verbElement = phrase
					.getFeatureAsElement(InternalFeature.VERB_PHRASE);

			if (verbElement == null) {
				verbElement = phrase.getHead();
			}

			checkClausalSubjects(phrase);
			checkSubjectNumberPerson(phrase, verbElement);
			checkDiscourseFunction(phrase);
			copyFrontModifiers(phrase, verbElement);
			addComplementiser(phrase, realisedElement);
			addCuePhrase(phrase, realisedElement);

			if (phrase.hasFeature(Feature.INTERROGATIVE_TYPE)
					|| phrase.hasFeature(FrenchFeature.RELATIVE_PHRASE)) {
				splitVerb = realiseInterrogative(phrase,
						realisedElement, phraseFactory, verbElement);
			} else {
				phrase.getPhraseHelper()
						.realiseList(
								realisedElement,
								phrase.getFeatureAsElementList(InternalFeature.FRONT_MODIFIERS),
								DiscourseFunction.FRONT_MODIFIER);
			}
			addSubjectsToFront(phrase, realisedElement, splitVerb);

			NLGElement passiveSplitVerb = addPassiveComplementsNumberPerson(
					phrase, realisedElement, verbElement);

			if (passiveSplitVerb != null) {
				splitVerb = passiveSplitVerb;
			}
			realiseVerb(phrase, realisedElement, splitVerb, verbElement);
			addPassiveSubjects(phrase, realisedElement, phraseFactory);
			addInterrogativeFrontModifiers(phrase, realisedElement);
			addEndingTo(phrase, realisedElement, phraseFactory);
		}
		return realisedElement;
	}

	/**
	 * This method does nothing by default, but can be overrided in
	 * language specific subclasses. It should be used to process any
	 * clausal subjects as necessary in that language.
	 * 
	 * @param phrase
	 * 
	 * @author vaudrypl
	 */
	protected void checkClausalSubjects(PhraseElement phrase) {}

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
	abstract protected void addEndingTo(PhraseElement phrase, ListElement realisedElement,
			NLGFactory phraseFactory);

	/**
	 * Adds the front modifiers to the end of the clause when dealing with
	 * interrogatives.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 */
	protected void addInterrogativeFrontModifiers(PhraseElement phrase,
			ListElement realisedElement) {
		NLGElement currentElement = null;
		if (phrase.hasFeature(Feature.INTERROGATIVE_TYPE)
				|| phrase.hasFeature(FrenchFeature.RELATIVE_PHRASE)) {
			for (NLGElement subject : phrase
					.getFeatureAsElementList(InternalFeature.FRONT_MODIFIERS)) {
				currentElement = subject.realiseSyntax();
				if (currentElement != null) {
					currentElement.setFeature(
							InternalFeature.DISCOURSE_FUNCTION,
							DiscourseFunction.POST_MODIFIER);

					realisedElement.addComponent(currentElement);
				}
			}
		}
	}

	/**
	 * Realises the subjects of a passive clause.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 */
	protected void addPassiveSubjects(PhraseElement phrase,
			ListElement realisedElement,
			NLGFactory phraseFactory) {
		NLGElement currentElement = null;

		if (phrase.getFeatureAsBoolean(Feature.PASSIVE).booleanValue()) {
			List<NLGElement> allSubjects = phrase
					.getFeatureAsElementList(InternalFeature.SUBJECTS);

			if (allSubjects.size() > 0
					|| phrase.hasFeature(Feature.INTERROGATIVE_TYPE)) {
				// vaudrypl changed "by" for a call to a lexicon method
				// and made the new preposition phrase the new list to
				// wich the other components will be added
				Lexicon lexicon = phraseFactory.getLexicon();
				WordElement preposition = lexicon.getPassivePreposition();
				ListElement prepositionPhrase = (ListElement)
					phraseFactory.createPrepositionPhrase(preposition).realiseSyntax();
				realisedElement.addComponent(prepositionPhrase);
				realisedElement = prepositionPhrase;
			}
			// Test added by vaudrypl to compensate for modification to realiseInterrogative().
			if (phrase.getFeature(Feature.INTERROGATIVE_TYPE) != InterrogativeType.WHO_SUBJECT) {
				for (NLGElement subject : allSubjects) {
	
	//				subject.setFeature(Feature.PASSIVE, true);
					if (subject.isA(PhraseCategory.NOUN_PHRASE)
							|| subject instanceof CoordinatedPhraseElement) {
						
						// changed by vaudrypl : the PASSIVE feature is given only
						// if the subject is a NP, and it is given to a copy of this NP
						if (subject instanceof NPPhraseSpec) {
							currentElement = new NPPhraseSpec((NPPhraseSpec)subject);
							currentElement.setFeature(Feature.PASSIVE, true);
						} else {
							currentElement = subject;
						}
						currentElement = currentElement.realiseSyntax();
						
						currentElement.setFeature(Feature.PASSIVE, true);
						if (currentElement != null) {
							currentElement.setFeature(
									InternalFeature.DISCOURSE_FUNCTION,
									DiscourseFunction.SUBJECT);
							realisedElement.addComponent(currentElement);
						}
					}
				}
			}
		}
	}

	/**
	 * Realises the verb part of the clause.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param splitVerb
	 *            an <code>NLGElement</code> representing the subjects that
	 *            should split the verb
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 */
	protected void realiseVerb(PhraseElement phrase,
			ListElement realisedElement,
			NLGElement splitVerb, NLGElement verbElement) {

		NLGElement currentElement = verbElement.realiseSyntax();
		if (currentElement != null) {
			if (splitVerb == null) {
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						DiscourseFunction.VERB_PHRASE);

				realisedElement.addComponent(currentElement);
			} else {
				if (currentElement instanceof ListElement) {
					List<NLGElement> children = currentElement.getChildren();
					currentElement = children.get(0);
					currentElement.setFeature(
							InternalFeature.DISCOURSE_FUNCTION,
							DiscourseFunction.VERB_PHRASE);
					realisedElement.addComponent(currentElement);
					realisedElement.addComponent(splitVerb);
					for (int eachChild = 1; eachChild < children.size(); eachChild++) {
						currentElement = children.get(eachChild);
						currentElement.setFeature(
								InternalFeature.DISCOURSE_FUNCTION,
								DiscourseFunction.VERB_PHRASE);
						realisedElement.addComponent(currentElement);
					}
				} else {
					currentElement.setFeature(
							InternalFeature.DISCOURSE_FUNCTION,
							DiscourseFunction.VERB_PHRASE);
					realisedElement.addComponent(currentElement);
					realisedElement.addComponent(splitVerb);
				}
			}
		}
	}

	/**
	 * Realises the complements of passive clauses; also sets number, person for
	 * passive
	 * 
	 * modified by vaudrypl
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 */
	protected NLGElement addPassiveComplementsNumberPerson(
			PhraseElement phrase,
			ListElement realisedElement, NLGElement verbElement) {
		Object passiveNumber = null;
		Object passivePerson = null;
		NLGElement currentElement = null;
		NLGElement splitVerb = null;
		NLGElement verbPhrase = phrase
				.getFeatureAsElement(InternalFeature.VERB_PHRASE);
		// added by vaudrypl
		boolean feminine = true;
		boolean atLeastOne = false;

		if (phrase.getFeatureAsBoolean(Feature.PASSIVE).booleanValue()
				&& verbPhrase != null
				&& !InterrogativeType.WHAT_OBJECT.equals(phrase
						.getFeature(Feature.INTERROGATIVE_TYPE))) {
			// complements of a clause are stored in the VPPhraseSpec
			for (NLGElement subject : verbPhrase
					.getFeatureAsElementList(InternalFeature.COMPLEMENTS)) {

				// first condition commented out by vaudrypl to eliminate
				// bug in original version of simplenlg 4.2
				// (coordinated object not realised (in subject position) in passive sentence)
				if (/*subject.isA(PhraseCategory.NOUN_PHRASE)
						&&*/ DiscourseFunction.OBJECT
								.equals(subject
										.getFeature(InternalFeature.DISCOURSE_FUNCTION))) {
					
					// changed by vaudrypl : the PASSIVE feature is given only
					// if the subject is a NP, and it is given to a copy of this NP
//					subject.setFeature(Feature.PASSIVE, true);
					if (subject instanceof NPPhraseSpec) {
						currentElement = new NPPhraseSpec((NPPhraseSpec)subject);
						currentElement.setFeature(Feature.PASSIVE, true);
					} else {
						currentElement = subject;
					}
					currentElement = currentElement.realiseSyntax();
					
					if (currentElement != null) {
						currentElement.setFeature(
								InternalFeature.DISCOURSE_FUNCTION,
								DiscourseFunction.OBJECT);

						if (phrase.hasFeature(Feature.INTERROGATIVE_TYPE)) {
							splitVerb = currentElement;
						} else {
							realisedElement.addComponent(currentElement);
						}
					}
					
					// checks number of subject if it is the first;
					// if there's more than one subject with NUMBER specified,
					// the subjects are considered plural
					if (passiveNumber == null) {
						if (subject instanceof CoordinatedPhraseElement) {
							if (((CoordinatedPhraseElement)subject).checkIfPlural()) {
								passiveNumber = NumberAgreement.PLURAL;
							} else {
								passiveNumber = NumberAgreement.SINGULAR;
							}
						} else {
							passiveNumber = subject.getFeature(Feature.NUMBER);
						}
					} else {
						passiveNumber = NumberAgreement.PLURAL;
					}
					
					// Checks person of subjects :
					// When there's more than one subject
					// with different PERSON feature,
					// first person is prefered over second person
					// which is prefered over third person.
					if (Person.FIRST.equals(subject.getFeature(Feature.PERSON))) {
						passivePerson = Person.FIRST;
					} else if (Person.SECOND.equals(subject
							.getFeature(Feature.PERSON))
							&& !Person.FIRST.equals(passivePerson)) {
						passivePerson = Person.SECOND;
					} else if (passivePerson == null) {
						passivePerson = Person.THIRD;
					}
					
					// added by vaudrypl
					atLeastOne = true;
					Object gender = subject.getFeature(LexicalFeature.GENDER);
					if (gender != Gender.FEMININE) {
						feminine = false;
					}

					if (Form.GERUND.equals(phrase.getFeature(Feature.FORM))
							&& !phrase.getFeatureAsBoolean(
									Feature.SUPPRESS_GENITIVE_IN_GERUND)
									.booleanValue()) {
						subject.setFeature(Feature.POSSESSIVE, true);
					}
				}
			}
			// added by vaudrypl
			// If there is at least one feminine subject and nothing else, the gender
			// of the subject group is feminine. Otherwise, it is masculine.
			if (atLeastOne && feminine) {
				verbElement.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
			}
			else {
				verbElement.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
			}
		}
		
		if (verbElement != null) {
			if (passivePerson != null) {
				verbElement.setFeature(Feature.PERSON, passivePerson);
				// below commented out. for non-passive, number and person set
				// by checkSubjectNumberPerson
				// } else {
				// verbElement.setFeature(Feature.PERSON, phrase
				// .getFeature(Feature.PERSON));
			}
			if (passiveNumber != null) {
				verbElement.setFeature(Feature.NUMBER, passiveNumber);
			}
		}
		
		return splitVerb;
	}

	/**
	 * Adds the subjects to the beginning of the clause unless the clause is
	 * infinitive, imperative or passive, or the subjects split the verb.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param splitVerb
	 *            an <code>NLGElement</code> representing the subjects that
	 *            should split the verb
	 */
	protected void addSubjectsToFront(PhraseElement phrase,
			ListElement realisedElement,
			NLGElement splitVerb) {
		// Condition on interrogative type added by vaudrypl to
		// compensate for modification in realiseInterrogative().
		Object interrogative = phrase.getFeature(Feature.INTERROGATIVE_TYPE);
		
		if (!Form.INFINITIVE.equals(phrase.getFeature(Feature.FORM))
				&& !Form.IMPERATIVE.equals(phrase.getFeature(Feature.FORM))
				&& !phrase.getFeatureAsBoolean(Feature.PASSIVE).booleanValue()
				&& splitVerb == null
				&& interrogative != InterrogativeType.WHO_SUBJECT) {
			
			realisedElement.addComponents(realiseSubjects(phrase)
					.getChildren());
		}
	}

	/**
	 * Realises the subjects for the clause.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 */
	protected ListElement realiseSubjects(PhraseElement phrase) {

		NLGElement currentElement = null;
		// vaudrypl added phrase argument to ListElement constructor
		// to copy all features from the PhraseElement
		ListElement realisedElement = new ListElement(phrase);

		for (NLGElement subject : phrase
				.getFeatureAsElementList(InternalFeature.SUBJECTS)) {

			subject.setFeature(InternalFeature.DISCOURSE_FUNCTION,
					DiscourseFunction.SUBJECT);
			if (Form.GERUND.equals(phrase.getFeature(Feature.FORM))
					&& !phrase.getFeatureAsBoolean(
							Feature.SUPPRESS_GENITIVE_IN_GERUND).booleanValue()) {
				subject.setFeature(Feature.POSSESSIVE, true);
			}
			currentElement = subject.realiseSyntax();
			if (currentElement != null) {
				realisedElement.addComponent(currentElement);
			}
		}
		return realisedElement;
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
	abstract protected NLGElement realiseInterrogative(PhraseElement phrase,
			ListElement realisedElement,
			NLGFactory phraseFactory, NLGElement verbElement);

	/**
	 * Realises the cue phrase for the clause if it exists.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 */
	protected void addCuePhrase(PhraseElement phrase,
			ListElement realisedElement) {

		NLGElement cuePhrase = phrase.getFeatureAsElement(Feature.CUE_PHRASE);
		NLGElement currentElement = null;
		if (cuePhrase != null) currentElement = cuePhrase.realiseSyntax();

		if (currentElement != null) {
			currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
					DiscourseFunction.CUE_PHRASE);
			realisedElement.addComponent(currentElement);
		}
	}

	/**
	 * Checks to see if this clause is a subordinate clause. If it is then the
	 * complementiser is added as a component to the realised element
	 * <b>unless</b> the complementiser has been suppressed.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 */
	protected void addComplementiser(PhraseElement phrase,
			ListElement realisedElement) {

		NLGElement currentElement;

		if (ClauseStatus.SUBORDINATE.equals(phrase
				.getFeature(InternalFeature.CLAUSE_STATUS))
				&& !phrase
						.getFeatureAsBoolean(Feature.SUPRESSED_COMPLEMENTISER)
						.booleanValue()) {

			// modified by vaudrypl
			Object complementiser = phrase.getFeature(Feature.COMPLEMENTISER);
			NLGFactory factory = phrase.getFactory();
			currentElement = factory.createNLGElement(complementiser, LexicalCategory.COMPLEMENTISER);
			if (currentElement != null) {
				currentElement = currentElement.realiseSyntax();
				if (currentElement != null) {
					realisedElement.addComponent(currentElement);
				}
			}
		}
	}

	/**
	 * Copies the post-modifiers of the clause to its verb phrase.
	 * Copies the front modifiers of the clause to the list of post-modifiers of
	 * the verb only if the phrase has infinitive form.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 */
	protected void copyFrontModifiers(PhraseElement phrase,
			NLGElement verbElement) {
		List<NLGElement> frontModifiers = phrase
				.getFeatureAsElementList(InternalFeature.FRONT_MODIFIERS);
		Object clauseForm = phrase.getFeature(Feature.FORM);

		// bug fix by Chris Howell (Agfa) -- do not overwrite existing post-mods
		// in the VP
		if (verbElement != null) {
			List<NLGElement> phrasePostModifiers = phrase
					.getFeatureAsElementList(InternalFeature.POSTMODIFIERS);

			// vaudrypl added support for CoordinatedPhraseElement as verb phrase
			if (verbElement instanceof PhraseElement
					|| verbElement instanceof CoordinatedPhraseElement) {
				List<NLGElement> verbPostModifiers = verbElement
						.getFeatureAsElementList(InternalFeature.POSTMODIFIERS);

				for (NLGElement eachModifier : phrasePostModifiers) {

					// need to check that VP doesn't already contain the
					// post-modifier
					// this only happens if the phrase has already been realised
					// and later modified, with realiser called again. In that
					// case, postmods will be copied over twice
					if (!verbPostModifiers.contains(eachModifier)) {
						if (verbElement instanceof PhraseElement) {
						((PhraseElement) verbElement)
								.addPostModifier(eachModifier);
						} else {
							((CoordinatedPhraseElement) verbElement)
							.addPostModifier(eachModifier);
						}
					}
				}
			}
		}

		// if (verbElement != null) {
		// verbElement.setFeature(InternalFeature.POSTMODIFIERS, phrase
		// .getFeature(InternalFeature.POSTMODIFIERS));
		// }

		if (Form.INFINITIVE.equals(clauseForm)) {
			phrase.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);

			for (NLGElement eachModifier : frontModifiers) {
				if (verbElement instanceof PhraseElement) {
					((PhraseElement) verbElement).addPostModifier(eachModifier);
				}
			}
			phrase.removeFeature(InternalFeature.FRONT_MODIFIERS);
			if (verbElement != null) {
				verbElement.setFeature(InternalFeature.NON_MORPH, true);
			}
		}
	}

	/**
	 * Checks the discourse function of the clause and alters the form of the
	 * clause as necessary. The following algorithm is used: <br>
	 * 
	 * <pre>
	 * If the clause represents a direct or indirect object then 
	 *      If form is currently Imperative then
	 *           Set form to Infinitive
	 *           Suppress the complementiser
	 *      If form is currently Gerund and there are no subjects
	 *      	 Suppress the complementiser
	 * If the clause represents a subject then
	 *      Set the form to be Gerund
	 *      Suppress the complementiser
	 * </pre>
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 */
	protected void checkDiscourseFunction(PhraseElement phrase) {
		List<NLGElement> subjects = phrase
				.getFeatureAsElementList(InternalFeature.SUBJECTS);
		Object clauseForm = phrase.getFeature(Feature.FORM);
		Object discourseValue = phrase
				.getFeature(InternalFeature.DISCOURSE_FUNCTION);

		if (DiscourseFunction.OBJECT.equals(discourseValue)
				|| DiscourseFunction.INDIRECT_OBJECT.equals(discourseValue)) {

			if (Form.IMPERATIVE.equals(clauseForm)) {
				phrase.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);
				phrase.setFeature(Feature.FORM, Form.INFINITIVE);
			} else if (Form.GERUND.equals(clauseForm) && subjects.size() == 0) {
				phrase.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);
			}
		} else if (DiscourseFunction.SUBJECT.equals(discourseValue)) {
			phrase.setFeature(Feature.FORM, Form.GERUND);
			phrase.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);
		}
	}

	/**
	 * Checks the subjects of the phrase to determine if there is more than one
	 * subject. This ensures that the verb phrase is correctly set. Also set
	 * person correctly
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 */
	protected void checkSubjectNumberPerson(PhraseElement phrase,
			NLGElement verbElement) {
		NLGElement currentElement = null;
		List<NLGElement> subjects = phrase
				.getFeatureAsElementList(InternalFeature.SUBJECTS);
		boolean pluralSubjects = false;
		Person person = null;

		if (subjects != null) {
			switch (subjects.size()) {
			case 0:
				break;

			case 1:
				currentElement = subjects.get(0);
				// coordinated NP with "and" are plural (not coordinated NP with
				// "or")
				if (currentElement instanceof CoordinatedPhraseElement
						&& ((CoordinatedPhraseElement) currentElement)
								.checkIfPlural())
					pluralSubjects = true;
				else if (currentElement.getFeature(Feature.NUMBER) == NumberAgreement.PLURAL)
					pluralSubjects = true;
				else if (currentElement.isA(PhraseCategory.NOUN_PHRASE)) {
					NLGElement currentHead = currentElement
							.getFeatureAsElement(InternalFeature.HEAD);
					person = (Person) currentElement.getFeature(Feature.PERSON);
					if ((currentHead.getFeature(Feature.NUMBER) == NumberAgreement.PLURAL))
						pluralSubjects = true;
					else if (currentHead instanceof ListElement) {
						pluralSubjects = true;
						/*
						 * } else if (currentElement instanceof
						 * CoordinatedPhraseElement &&
						 * "and".equals(currentElement.getFeatureAsString(
						 * //$NON-NLS-1$ Feature.CONJUNCTION))) { pluralSubjects
						 * = true;
						 */
					}
				}
				break;

			default:
				pluralSubjects = true;
				break;
			}
		}
		if (verbElement != null) {
			verbElement.setFeature(Feature.NUMBER,
					pluralSubjects ? NumberAgreement.PLURAL : phrase
							.getFeature(Feature.NUMBER));
			if (person != null)
				verbElement.setFeature(Feature.PERSON, person);
		}
	}
	
	/**
	 * Add a modifier to a clause Use heuristics to decide where it goes
	 * 
	 * @param clause
	 * @param modifier
	 * 
	 */
	abstract public void addModifier(SPhraseSpec clause, Object modifier);
}
