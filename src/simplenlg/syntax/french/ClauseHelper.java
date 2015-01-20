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

import java.util.ArrayList;
import java.util.List;

import simplenlg.features.ClauseStatus;
import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.InternalFeature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.LexicalFeature;
import simplenlg.features.Gender;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.features.french.FrenchFeature;
import simplenlg.features.french.FrenchInternalFeature;
import simplenlg.features.french.FrenchLexicalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * This is a helper class containing the main methods for realising the syntax
 * of clauses for French.
 * 
 * Reference :
 * Grevisse, Maurice (1993). Le bon usage, grammaire française,
 * 12e édition refondue par André Goosse, 8e tirage, Éditions Duculot,
 * Louvain-la-Neuve, Belgique.
 * 
 * @author vaudrypl
 */
public class ClauseHelper extends simplenlg.syntax.english.nonstatic.ClauseHelper {
	/**
	 * This method does nothing in the French clause syntax helper.
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
			ListElement realisedElement, NLGFactory phraseFactory) {}

	/**
	 * Checks the subjects of the phrase to determine if there is more than one
	 * subject. This ensures that the verb phrase is correctly set. Also set
	 * person and gender correctly.
	 * Also sets FrenchLexicalFeature.NE_ONLY_NEGATION
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 */
	@Override
	protected void checkSubjectNumberPerson(PhraseElement phrase,
			NLGElement verbElement) {
		boolean passive = phrase.getFeatureAsBoolean(Feature.PASSIVE);
		// If the clause has a relativised subject, make subject agreement
		// with parent noun phrase instead.
		List<NLGElement> subjects =
				phrase.getFeatureAsElementList(InternalFeature.SUBJECTS);
		List<NLGElement> normalSubjects = subjects;
		if ((!passive && phrase.hasRelativePhrase(DiscourseFunction.SUBJECT))
				|| (passive && phrase.hasRelativePhrase(DiscourseFunction.OBJECT))) {
			subjects = new ArrayList<NLGElement>();
			NLGElement parentNP = phrase.getParent();
			if (parentNP instanceof NPPhraseSpec) subjects.add(parentNP);
			phrase.setFeature(InternalFeature.SUBJECTS, subjects);
		}
		
		super.checkSubjectNumberPerson(phrase, verbElement);
		// Put the original subject back if it was changed (to call the superclass
		// version of the function) because the subject or object was relativised.
		if (subjects != normalSubjects) {
			phrase.setFeature(InternalFeature.SUBJECTS, normalSubjects);
		}

		boolean noOnlyNegation = false;
		boolean feminine = false;
		Person person = Person.THIRD;
		
		if (subjects != null && subjects.size() >= 1) {
			feminine = true;
			for (NLGElement currentElement : subjects) {
				Object gender = currentElement.getFeature(LexicalFeature.GENDER);
				if (gender != Gender.FEMININE) {
					feminine = false;
				}
				// If there's at least one first person subject, the subjects as a whole
				// are first person. Otherwise, if there's at least on second person subject,
				// the subjects as a whole are second person. Otherwise they are third person
				// by default.
				Object currentPerson = currentElement.getFeature(Feature.PERSON);
				if (currentPerson == Person.FIRST) {
					person = Person.FIRST;
				} else if (person == Person.THIRD && currentPerson == Person.SECOND) {
					person = Person.SECOND;
				}

				if (!noOnlyNegation) {
					noOnlyNegation = currentElement.checkIfNeOnlyNegation();
				}
			}
		}
		// If there is at least one feminine subject and nothing else, the gender
		// of the subject group is feminine. Otherwise, it is masculine.
		if (feminine) {
			verbElement.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		}
		else {
			verbElement.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
		}
		
		verbElement.setFeature(Feature.PERSON, person);
		setNeOnlyNegation(verbElement, noOnlyNegation);
	}
	
	/**
	 * Check complements and sets FrenchLexicalFeature.NE_ONLY_NEGATION
	 * accordingly for the verb phrase.
	 * 
	 * @param phrase	the verb phrase
	 */
	protected void setNeOnlyNegation(NLGElement verbElement, boolean noOnlyNegation) {
		// check complements if subject doesn't already have the feature
		if (!noOnlyNegation) {
			
			List<NLGElement> complements =
				verbElement.getFeatureAsElementList(InternalFeature.COMPLEMENTS);
			
			for (NLGElement current : complements) {
				if ( current.checkIfNeOnlyNegation() ) {
					noOnlyNegation = true;
					break;
				}
			}
		}
		
		verbElement.setFeature(FrenchLexicalFeature.NE_ONLY_NEGATION, noOnlyNegation);
	}

	/**
	 * Add a modifier to a clause. Use heuristics to decide where it goes.
	 * Based on method of the same name in English clause helper
	 * Reference : section 935 of Grevisse (1993)
	 * 
	 * @param clause
	 * @param modifier
	 * 
	 * @author vaudrypl
	 */
	@Override
	public void addModifier(SPhraseSpec clause, Object modifier) {
		// Everything is postModifier

		if (modifier != null) {
		
			// get modifier as NLGElement if possible
			NLGElement modifierElement = null;
			if (modifier instanceof NLGElement)
				modifierElement = (NLGElement) modifier;
			else if (modifier instanceof String) {
				String modifierString = (String) modifier;
				if (modifierString.length() > 0 && !modifierString.contains(" "))
					modifierElement = clause.getFactory().createWord(modifier,
							LexicalCategory.ADVERB);
			}
		
			// if no modifier element, must be a complex string
			if (modifierElement == null) {
				clause.addPostModifier((String) modifier);
			} else {
				// default case
				clause.addPostModifier(modifierElement);
			}
		}
	}
		
	/**
	 * Checks the discourse function of the clause and alters the form of the
	 * clause as necessary.
	 * 
	 * Based on method of the same name in English syntax processor
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 */
	@Override
	protected void checkDiscourseFunction(PhraseElement phrase) {
		Object clauseForm = phrase.getFeature(Feature.FORM);
		Object discourseValue = phrase
				.getFeature(InternalFeature.DISCOURSE_FUNCTION);

		if (DiscourseFunction.OBJECT.equals(discourseValue)
				|| DiscourseFunction.INDIRECT_OBJECT.equals(discourseValue)) {

			if (Form.IMPERATIVE.equals(clauseForm)) {
				phrase.setFeature(Feature.FORM, Form.INFINITIVE);
			}
		}
	}

	/**
	 * Checks if there are any clausal subjects and if so, put each of them
	 * in a "le fait" + (conjunction) construction.
	 * 
	 * @param phrase
	 * 
	 * @author vaudrypl
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void checkClausalSubjects(PhraseElement phrase) {
		Object subjects = phrase.getFeature(InternalFeature.SUBJECTS);
		List<NLGElement> subjectList = null;
		if (subjects instanceof CoordinatedPhraseElement) {
			subjects = ((CoordinatedPhraseElement)subjects).getFeature(InternalFeature.COORDINATES);
		}
		if (subjects instanceof List) subjectList = (List<NLGElement>) subjects;
		
		if (subjectList != null) {
			for (int index = 0; index < subjectList.size(); ++index) {
				NLGElement currentSubject = subjectList.get(index);
				
				if (currentSubject instanceof SPhraseSpec) {
					Object form = currentSubject.getFeature(Feature.FORM);
					NLGElement verbPhrase = ((SPhraseSpec)currentSubject).getVerbPhrase();
					if (form == null && verbPhrase != null) form = verbPhrase.getFeature(Feature.FORM);
					if (form == Form.NORMAL || form == null) {
						NLGFactory factory = phrase.getFactory();
						NPPhraseSpec newSubject = factory.createNounPhrase("le", "fait");
						newSubject.addPostModifier(currentSubject);
						
						currentSubject.setFeature(InternalFeature.CLAUSE_STATUS, ClauseStatus.SUBORDINATE);
						currentSubject.setFeature(Feature.SUPRESSED_COMPLEMENTISER, false);
						
						currentSubject = newSubject;
					}
				}
				
				subjectList.set(index, currentSubject);
			}
		}
	}

	/**
	 * Copies the front modifiers of the clause to the list of post-modifiers of
	 * the verb only if the phrase has infinitive form.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 */
	@Override
	protected void copyFrontModifiers(PhraseElement phrase,
			NLGElement verbElement) {
		super.copyFrontModifiers(phrase, verbElement);

		// If the complementiser of an infinitive clause is "que", it is suppressed,
		// otherwise it is not suppressed.
		Object clauseForm = phrase.getFeature(Feature.FORM);
		Object clauseStatus = phrase.getFeature(InternalFeature.CLAUSE_STATUS);
		Object complementiser = phrase.getFeature(Feature.COMPLEMENTISER);
		WordElement que = phrase.getLexicon().lookupWord("que", LexicalCategory.COMPLEMENTISER);
		if (clauseForm == Form.INFINITIVE && clauseStatus == ClauseStatus.SUBORDINATE) {
			if (que.equals(complementiser)) phrase.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);
			else phrase.setFeature(Feature.SUPRESSED_COMPLEMENTISER, false);
		}
	}

	/**
	 * Realises the cue phrase for the clause if it exists. In French,
	 * checks if the phrase is infinitive and doesn't realise the cue phrase
	 * if so.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 */
	@Override
	protected void addCuePhrase(PhraseElement phrase,
			ListElement realisedElement) {
		Object form = phrase.getFeature(Feature.FORM);
		if (form != Form.INFINITIVE) super.addCuePhrase(phrase, realisedElement); 
	}

	/**
	 * Checks to see if this clause is a subordinate clause or is in the
	 * subjunctive mood. If it is then the complementiser is added
	 * as a component to the realised element <b>unless</b> the complementiser
	 * has been suppressed.
	 * 
	 * If this is a relative clause, the correct relative pronoun is added instead.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 */
	@Override
	protected void addComplementiser(PhraseElement phrase,
			ListElement realisedElement) {

		// Relative clause.
		NLGElement relativePhrase = phrase.getFeatureAsElement(FrenchFeature.RELATIVE_PHRASE);
		if ( relativePhrase != null) {
			
			// Get discourse function.
			Object functionObject = relativePhrase.getFeature(InternalFeature.DISCOURSE_FUNCTION);
			DiscourseFunction function;
			List<NLGElement> subjects = phrase.getFeatureAsElementList(InternalFeature.SUBJECTS);
			if (functionObject instanceof DiscourseFunction) {
				function = (DiscourseFunction) functionObject;
			} else if (subjects != null && subjects.contains(relativePhrase)) {
				function = DiscourseFunction.SUBJECT;
			} else {
				function = DiscourseFunction.COMPLEMENT;
			}			
			// Decide which relative pronoun to use.
			NLGFactory factory = phrase.getFactory();
			NLGElement relativePronoun;
			NLGElement preposition = null;
			boolean passive = phrase.getFeatureAsBoolean(Feature.PASSIVE);
			switch (function) {
			case SUBJECT:
				relativePronoun = factory.createNounPhrase(
						factory.createWord("qui", LexicalCategory.PRONOUN) );
				if (passive) {
					relativePronoun =factory.createPrepositionPhrase(
							"par", relativePronoun);
				}
				break;
			case OBJECT:
				if (passive) {
					relativePronoun = factory.createNounPhrase(
							factory.createWord("qui", LexicalCategory.PRONOUN) );
				} else {
					relativePronoun = factory.createNounPhrase(
							factory.createWord("que", LexicalCategory.PRONOUN) );
				}
				break;
			case INDIRECT_OBJECT:
				preposition = factory.createWord("à", LexicalCategory.PREPOSITION);
			default:
				if (relativePhrase instanceof PPPhraseSpec) {
					relativePhrase.setFeature(FrenchInternalFeature.RELATIVISED, true);
					PPPhraseSpec relativePP = (PPPhraseSpec) relativePhrase;
					preposition = relativePP.getPreposition();
				}
				if (preposition != null) {
					/* Use relative pronoun "dont" if the relative phrase
					 * has preposition "de" and is not itself in a PP.
					 * Also use "dont" when the relative phrase is the
					 * pronoun "en" (which always replaces a PP with "de"). */
					NLGElement de = factory.createWord("de", LexicalCategory.PREPOSITION);
					NLGElement en = factory.createWord("en", LexicalCategory.PRONOUN);
					NLGElement relativeParent = relativePhrase.getParent();
					boolean dontNotException = relativeParent == null
							|| (relativeParent.getFeature(InternalFeature.DISCOURSE_FUNCTION)
									!= DiscourseFunction.INDIRECT_OBJECT
								&& !(relativeParent.getParent() instanceof PPPhraseSpec));
					
					if ((preposition == de && dontNotException)
							|| relativePhrase.getFeature(InternalFeature.HEAD) == en) {
						
						relativePronoun = factory.createNounPhrase(
								factory.createWord("dont", LexicalCategory.PRONOUN));
					} else {
						String relProString = "lequel";					
						// Check antecedent for special cases.
						NLGElement parent = phrase.getParent();
						if (parent instanceof NPPhraseSpec) {
							Object gender = parent.getFeature(LexicalFeature.GENDER);
							Object person = parent.getFeature(Feature.PERSON);
							// Use "qui" if the antecedent is a person.
							// Approximate this for now by checking if it's 1rst or 2nd person
							// or if it's a proper name.
							if (person == Person.FIRST || person == Person.SECOND
									|| parent.getFeatureAsBoolean(LexicalFeature.PROPER)) {
								relProString = "qui";
							// Use "quoi" for neuter antecedents.
							} else if (gender == Gender.NEUTER) {
								relProString = "quoi";
							}
						}						
						relativePronoun = factory.createPrepositionPhrase(
							preposition, factory.createNounPhrase(
									factory.createWord(relProString, LexicalCategory.PRONOUN)));
						
						/* Exception to the "dont" rule. The outer prepositional phrase (PP)
						 * in which the relativised PP (with prepostion "de") is embedded is
						 * moved in front of the relative clause. "lequel" is used as
						 * a relative pronoun to replace the relativised noun phrase. */
						if (!dontNotException) {
							relativePhrase.removeFeature(FrenchInternalFeature.RELATIVISED);
							NLGElement grandParent = relativeParent.getParent();
							NLGElement parentPreposition;
							if (grandParent instanceof PPPhraseSpec) {
								parentPreposition = ((PPPhraseSpec) grandParent).getPreposition();
								grandParent.setFeature(FrenchInternalFeature.RELATIVISED, true);
							} else {
								parentPreposition = factory.createWord(
									"à", LexicalCategory.PREPOSITION);
								relativeParent.setFeature(FrenchInternalFeature.RELATIVISED, true);
							}
							NPPhraseSpec nounPhraseCopy =
								factory.createNounPhrase(
									relativeParent.getFeatureAsElement(InternalFeature.SPECIFIER),
									relativeParent.getFeatureAsElement(InternalFeature.HEAD));
							nounPhraseCopy.addComplement(relativePronoun);
							relativePronoun = factory.createPrepositionPhrase(
								parentPreposition, nounPhraseCopy);
						}
					}
				} else {
					relativePronoun = factory.createNounPhrase(
							factory.createWord("que", LexicalCategory.PRONOUN));
				}					
				break;
			}			
			// Add relative pronoun.
			if (relativePronoun != null) {
				relativePronoun.setFeature(InternalFeature.DISCOURSE_FUNCTION, function);
				relativePronoun.setParent(phrase);
				relativePronoun = relativePronoun.realiseSyntax();
				if (relativePronoun != null) {
					realisedElement.addComponent(relativePronoun);
				}
			}
			
		// Realise complementiser if appropriate.
		} else if ((phrase.getFeature(InternalFeature.CLAUSE_STATUS) == ClauseStatus.SUBORDINATE
					|| phrase.getFeature(Feature.FORM) == Form.SUBJUNCTIVE)
				&& !phrase.getFeatureAsBoolean(Feature.SUPRESSED_COMPLEMENTISER)) {
	
			Object complementiser = phrase.getFeature(Feature.COMPLEMENTISER);
			NLGFactory factory = phrase.getFactory();
			NLGElement currentElement = factory.createNLGElement(complementiser, LexicalCategory.COMPLEMENTISER);
			if (currentElement != null) {
				currentElement = currentElement.realiseSyntax();
				if (currentElement != null) {
					realisedElement.addComponent(currentElement);
				}
			}
		}
	}

	/**
	 * Adds the subjects to the beginning of the clause unless the clause is
	 * infinitive, imperative or passive, the subjects split the verb or,
	 * in French, the relative phrase discourse function is subject.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param splitVerb
	 *            an <code>NLGElement</code> representing the subjects that
	 *            should split the verb
	 */
	@Override
	protected void addSubjectsToFront(PhraseElement phrase,
			ListElement realisedElement,
			NLGElement splitVerb) {
		
		if (!phrase.hasRelativePhrase(DiscourseFunction.SUBJECT)) {
			super.addSubjectsToFront(phrase, realisedElement, splitVerb);
		}
	}

	/**
	 * Realises the subjects of a passive clause unless, in French,
	 * the relative phrase discourse function is subject.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 */
	@Override
	protected void addPassiveSubjects(PhraseElement phrase,
			ListElement realisedElement,
			NLGFactory phraseFactory) {
		
		if (!phrase.hasRelativePhrase(DiscourseFunction.SUBJECT)) {
			super.addPassiveSubjects(phrase, realisedElement, phraseFactory);
		}
	}
	
	/**
	 * Realises the complements of passive clauses; also sets number, person for
	 * passive.
	 * In French, checks before that the clause doesn't have a relativised object.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 */
	@Override
	protected NLGElement addPassiveComplementsNumberPerson(
			PhraseElement phrase,
			ListElement realisedElement, NLGElement verbElement) {
		NLGElement splitVerb = null;
		if (!phrase.hasRelativePhrase(DiscourseFunction.OBJECT)) {
			splitVerb = super.addPassiveComplementsNumberPerson(
					phrase, realisedElement, verbElement);
		}
		return splitVerb;
	}
	
	/**
	 * This is the main controlling method for handling interrogative clauses.
	 * The actual steps taken are dependent on the type of question being asked.
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
	protected NLGElement realiseInterrogative(
			PhraseElement phrase, ListElement realisedElement,
			NLGFactory phraseFactory, NLGElement verbElement) {
		NLGElement splitVerb = null;

		return splitVerb;
	}

	/**
	 * The main method for controlling the syntax realisation of clauses.
	 * The French version takes care of interrogative clauses, using the result
	 * of its superclass as a base.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representation of the clause.
	 * @return the <code>NLGElement</code> representing the realised clause.
	 */
	@Override
	public NLGElement realise(PhraseElement phrase) {
		ListElement realisedElement = null;
		NLGFactory factory = phrase.getFactory();

		if (phrase != null) {
			Object interrogativeType = phrase.getFeature(Feature.INTERROGATIVE_TYPE);
			if (interrogativeType instanceof InterrogativeType) {
				phrase.removeFeature(Feature.INTERROGATIVE_TYPE);
				realisedElement = new ListElement(phrase);

				addCuePhrase(phrase, realisedElement);
				NLGElement cuePhrase = phrase.getFeatureAsElement(
						Feature.CUE_PHRASE);
				phrase.removeFeature(Feature.CUE_PHRASE);
				
				NLGElement parent = phrase.getParent();
				if (parent != null) {
					parent.setFeature(InternalFeature.INTERROGATIVE, true);
				}
				
				Object clauseStatus = phrase.getFeature(InternalFeature.CLAUSE_STATUS);
				NLGElement relativePhrase = phrase.getFeatureAsElement(
						FrenchFeature.RELATIVE_PHRASE);
				Object complementiser = phrase.getFeature(Feature.COMPLEMENTISER);
				boolean passive = phrase.getFeatureAsBoolean(Feature.PASSIVE);
				
//				phrase.setFeature( InternalFeature.CLAUSE_STATUS,
//						ClauseStatus.SUBORDINATE);
				
				// Create dummy element to indicate which kind of complement
				// is to be relativised.
				NLGElement dummyElement = factory.createNounPhrase();
				DiscourseFunction interrogativeFunction = null;
				switch ((InterrogativeType) interrogativeType) {
				case WHO_SUBJECT:
					interrogativeFunction = DiscourseFunction.SUBJECT;
					break;
				case WHO_OBJECT:
				case WHAT_OBJECT:
					interrogativeFunction = DiscourseFunction.OBJECT;
					break;
				case WHO_INDIRECT_OBJECT:
					interrogativeFunction = DiscourseFunction.INDIRECT_OBJECT;
					break;
				}
				dummyElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						interrogativeFunction);
				phrase.setFeature(FrenchFeature.RELATIVE_PHRASE, dummyElement);
				
				NLGElement realisedAffirmative;
				List<NLGElement> components;
				switch ((InterrogativeType) interrogativeType) {
				case YES_NO:
					if (clauseStatus == ClauseStatus.SUBORDINATE) {
						// Wrap "si" in a bare StringElement so that it's not
						// detected as the complementiser "si" for the purpose
						// of the conditional rule.
						phrase.setFeature(Feature.COMPLEMENTISER, new StringElement("si"));
						phrase.removeFeature(FrenchFeature.RELATIVE_PHRASE);
						realisedElement.addComponent(super.realise(phrase));
					} else {
						realisedElement.addComponent(new StringElement("est-ce"));
						realisedElement.addComponent(super.realise(phrase));
					}
					break;

				case WHO_SUBJECT:
					if (passive) {
						realisedAffirmative = super.realise(phrase);
						components = realisedAffirmative.
								getFeatureAsElementList(InternalFeature.COMPONENTS);
						components.add(1, new StringElement("est-ce que"));
						realisedAffirmative.setFeature(InternalFeature.COMPONENTS, components);
						realisedElement.addComponent(realisedAffirmative);
					} else {
						realisedElement.addComponent(new StringElement("qui est-ce"));
						realisedElement.addComponent(super.realise(phrase));
					}
					break;
				case WHO_OBJECT:
					realisedElement.addComponent(new StringElement("qui est-ce"));
					realisedElement.addComponent(super.realise(phrase));
				break;
				case WHO_INDIRECT_OBJECT:
					dummyElement.setFeature(LexicalFeature.PROPER, true);
					phrase.setParent(dummyElement);
					realisedAffirmative = super.realise(phrase);
					phrase.setParent(parent);
					components = realisedAffirmative.
							getFeatureAsElementList(InternalFeature.COMPONENTS);
					components.add(1, new StringElement("est-ce que"));
					realisedAffirmative.setFeature(InternalFeature.COMPONENTS, components);
					realisedElement.addComponent(realisedAffirmative);
					break;
					
				case HOW:
					realisedElement.addComponent(new StringElement("comment est-ce"));
					realisedElement.addComponent(super.realise(phrase));
					break;
				case WHY:
					realisedElement.addComponent(new StringElement("pourquoi est-ce"));
					realisedElement.addComponent(super.realise(phrase));
					break;
				case WHERE:
					realisedElement.addComponent(new StringElement("où est-ce"));
					realisedElement.addComponent(super.realise(phrase));
					break;

				case HOW_MANY:
					// Now hasFeature(FrenchFeature.RELATIVE_PHRASE) == true
					// but getFeatureAsElement(FrenchFeature.RELATIVE_PHRASE) == null,
					// so that no relative pronoun is printed but the front modifiers
					// are still put at the end.
					phrase.setFeature(FrenchFeature.RELATIVE_PHRASE, this);
					
					// Change specifier of surface subject to "combien".
					NLGElement surfaceSubject;
					SPhraseSpec s = (SPhraseSpec) phrase;
					NLGElement combien = factory.createWord("combien", LexicalCategory.ADVERB);
					if (passive) {
						surfaceSubject = s.getObject();
						s.setObject( changeSpecifier(surfaceSubject, combien) );
					} else {
						surfaceSubject = s.getSubject();
						s.setSubject( changeSpecifier(surfaceSubject, combien) );
					}
					
					realisedElement.addComponent(super.realise(phrase));
					
					// After realisation, change back surface subject.
					if (passive) {
						s.setObject( surfaceSubject );
					} else {
						s.setSubject( surfaceSubject );
					}
					break;

				case WHAT_OBJECT:
					realisedElement.addComponent(new StringElement("qu'est-ce"));
					realisedElement.addComponent(super.realise(phrase));
					break;
				}
				// Restore original values of features.		
				phrase.setFeature(Feature.CUE_PHRASE, cuePhrase);
				phrase.setFeature(Feature.INTERROGATIVE_TYPE, interrogativeType);
				phrase.setFeature(InternalFeature.CLAUSE_STATUS, clauseStatus);
				phrase.setFeature(FrenchFeature.RELATIVE_PHRASE,relativePhrase);
				phrase.setFeature(Feature.COMPLEMENTISER, complementiser);
			} else {
				return super.realise(phrase);
			}
		}
		return realisedElement;
	}

	/**
	 * Create a copy of a noun phrase or coordinated noun phrases
	 * and changes the specifier on the copy.
	 * 
	 * @param nounPhrase
	 * @param specifier
	 * @return
	 */
	protected NLGElement changeSpecifier(NLGElement nounPhrase, NLGElement specifier) {
		NLGElement modifiedElement = nounPhrase;
		
		if (nounPhrase instanceof NPPhraseSpec) {
			modifiedElement = new NPPhraseSpec((NPPhraseSpec) nounPhrase);
			((NPPhraseSpec) modifiedElement).setSpecifier(specifier);
			
		} else if (nounPhrase instanceof CoordinatedPhraseElement) {
			modifiedElement = new CoordinatedPhraseElement((CoordinatedPhraseElement) nounPhrase);
			
			List<NLGElement> coordinates = modifiedElement.getFeatureAsElementList(InternalFeature.COORDINATES);
			List<NLGElement> modifiedCoordinates = new ArrayList<NLGElement>();
			for (NLGElement element : coordinates) {
				modifiedCoordinates.add( changeSpecifier(element, specifier) );
			}
			modifiedElement.setFeature(InternalFeature.COORDINATES, modifiedCoordinates);
		}
		
		return modifiedElement;
	}
}

