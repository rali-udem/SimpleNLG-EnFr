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

package simplenlg.morphology.french;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.features.*;
import simplenlg.features.french.*;
import simplenlg.lexicon.Lexicon;
import simplenlg.morphology.MorphologyRulesInterface;

/**
 * Morphology rules for French.
 * To allow overriding of it's methods, this class's methods are not static.
 * 
 * References :
 * 
 * Grevisse, Maurice (1993). Le bon usage, grammaire française,
 * 12e édition refondue par André Goosse, 8e tirage, Éditions Duculot,
 * Louvain-la-Neuve, Belgique.
 * 
 * Mansouri, Mohammed Issaoui (1996). Le Mansouris, tous les verbes usuels
 * entièrement conjugués et orthographiés. CAPT, Éditeurs, Montréal, Canada.
 * 
 * @author vaudrypl
 *
 */
public class MorphologyRules extends simplenlg.morphology.english.NonStaticMorphologyRules
		implements MorphologyRulesInterface {
	
	public static final String a_o_regex = "\\A(a|ä|à|â|o|ô).*";

	/**
	 * This method performs the morphology for determiners.
	 * It returns a StringElement made from the baseform, or
	 * the plural or feminine singular form of the determiner
	 * if it applies.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 */
	@Override
	public NLGElement doDeterminerMorphology(InflectedWordElement element) {
		String inflectedForm;
		// Get gender from parent, or from self if there is no parent
		NLGElement parent = element.getParent();
		Object gender = null;
		
		if (parent != null) gender = parent.getFeature(LexicalFeature.GENDER);
		else gender = element.getFeature(LexicalFeature.GENDER);
		
		boolean feminine = Gender.FEMININE.equals( gender );
		
		// plural form
		if (element.isPlural() && 
			element.hasFeature(LexicalFeature.PLURAL)) {
			inflectedForm = element.getFeatureAsString(LexicalFeature.PLURAL);
			
			if (feminine && element.hasFeature(FrenchLexicalFeature.FEMININE_PLURAL)) {
				inflectedForm = element.getFeatureAsString(FrenchLexicalFeature.FEMININE_PLURAL);
			}
			
			// "des" -> "de" in front of noun premodifiers
			if (parent != null && "des".equals(inflectedForm)) {
				List<NLGElement> preModifiers = parent.getFeatureAsElementList(InternalFeature.PREMODIFIERS);
				if (!preModifiers.isEmpty()) {
					inflectedForm = "de";
				}
			}
			
		// feminine singular form
		} else if (feminine	&& element.hasFeature(FrenchLexicalFeature.FEMININE_SINGULAR)) {
			inflectedForm = element.getFeatureAsString(FrenchLexicalFeature.FEMININE_SINGULAR);
		// masculine singular form
		} else {
			inflectedForm = element.getBaseForm();
			// remove particle if the determiner has one
			String particle = getParticle(element);
			inflectedForm = inflectedForm.replaceFirst(particle, "");
			inflectedForm = inflectedForm.trim();
		}
		
		StringElement realisedElement = new StringElement(inflectedForm, element);
		return realisedElement;
	}
	
	/**
	 * This method performs the morphology for adjectives.
	 * Based in part on the same method in the english rules
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	@Override
	public NLGElement doAdjectiveMorphology(
			InflectedWordElement element, WordElement baseWord) {

		String realised = null;

		// base form from baseWord if it exists, otherwise from element
		String baseForm = getBaseForm(element, baseWord);

		// Comparatives and superlatives are mainly treated by syntax
		// in French. Only exceptions, provided by the lexicon, are
		// treated by morphology.
		if (element.getFeatureAsBoolean(Feature.IS_COMPARATIVE).booleanValue()) {
			realised = element.getFeatureAsString(LexicalFeature.COMPARATIVE);

			if (realised == null && baseWord != null) {
				realised = baseWord
						.getFeatureAsString(LexicalFeature.COMPARATIVE);
			}
			if (realised == null) realised = baseForm;
		} else {
			realised = baseForm;
		}
		
		// Get gender from parent or "grandparent" or self, in that order
		NLGElement parent = element.getParent();
		Object function = element.getFeature(InternalFeature.DISCOURSE_FUNCTION);
		boolean feminine = false;
		if (parent != null) {
			if (function == DiscourseFunction.HEAD) {
				function = parent.getFeature(InternalFeature.DISCOURSE_FUNCTION);
			}

			if (!parent.hasFeature(LexicalFeature.GENDER) && parent.getParent() != null) {
				parent = parent.getParent();
			}
		} else {
			parent = element;
		}
		// if parent or grandparent is a verb phrase and the adjective is a modifier,
		// assume it's a direct object attribute if there is one
		if (parent.isA(PhraseCategory.VERB_PHRASE) && (function == DiscourseFunction.FRONT_MODIFIER
				|| function == DiscourseFunction.PRE_MODIFIER
				|| function == DiscourseFunction.POST_MODIFIER)) {
			List<NLGElement> complements = parent.getFeatureAsElementList(InternalFeature.COMPLEMENTS);
			NLGElement directObject = null;
			for (NLGElement complement: complements) {
				if (complement.getFeature(InternalFeature.DISCOURSE_FUNCTION) == DiscourseFunction.OBJECT) {
					directObject = complement;
				}
			}
			if (directObject != null) parent = directObject;
		}
		feminine = Gender.FEMININE.equals( parent.getFeature(LexicalFeature.GENDER) );

		// Feminine
		// The rules used here apply to the most general cases.
		// Exceptions are meant to be specified in the lexicon or by the user
		// by means of the FrenchLexicalFeature.FEMININE_SINGULAR feature.
		// Reference : sections 528-534 of Grevisse (1993)
		if ( feminine ) {
			if (element.hasFeature(FrenchLexicalFeature.FEMININE_SINGULAR)) {
				realised = element.getFeatureAsString(FrenchLexicalFeature.FEMININE_SINGULAR);
			}
			else if (realised.endsWith("e")) {
				// do nothing
			}
			else if ( realised.endsWith("el") || realised.endsWith("eil") ) {
				realised += "le";
			}
			else if (realised.endsWith("en") || realised.endsWith("on")) {
				realised += "ne";
			}
			else if (realised.endsWith("et")) {
				realised += "te";
			}
			else if (realised.endsWith("eux")) {
				// replace 'x' by 's'
				realised = realised.substring(0, realised.length()-1) + "se";
			}
			else if (realised.endsWith("er")) {
				realised = realised.substring(0, realised.length()-2) + "ère";
			}
			else if (realised.endsWith("eau")) {
				realised = realised.substring(0, realised.length()-3) + "elle";
			}
			else if (realised.endsWith("gu")) {
				realised += "ë";
			}
			else if (realised.endsWith("g")) {
				realised += "ue";
			}
			else if (realised.endsWith("eur")
					// if replacing -eur by -ant gives a valid present participle
					&& element.getLexicon().hasWordFromVariant(
							realised.substring(0, realised.length()-3)+"ant")) {
				// replace 'r' by 's'
				realised = realised.substring(0, realised.length()-1) + "se";
			}
			else if (realised.endsWith("teur")) {
				realised = realised.substring(0, realised.length()-4) + "trice";
			}
			else if (realised.endsWith("if")) {
				realised = realised.substring(0, realised.length()-1) + "ve";
			}
			else {
				realised += "e";
			}
		}

		// Plural
		// The rules used here apply to the most general cases.
		// Exceptions are meant to be specified in the lexicon or by the user
		// by means of the LexicalFeature.PLURAL and
		// FrenchLexicalFeature.FEMININE_PLURAL features.
		// Reference : sections 538-539 of Grevisse (1993)
		if (parent.isPlural()) {
			if (feminine) {
				if (element.hasFeature(FrenchLexicalFeature.FEMININE_PLURAL)) {
					realised = element.getFeatureAsString(FrenchLexicalFeature.FEMININE_PLURAL);
				}
				else {
					realised += "s";
				}
			}
			else if (element.hasFeature(LexicalFeature.PLURAL)) {
				realised = element.getFeatureAsString(LexicalFeature.PLURAL);
			}
			else {
				realised = buildRegularPlural(realised);
			}
		}
		
		realised += getParticle(element);
		StringElement realisedElement = new StringElement(realised, element);
		return realisedElement;
	}

	/**
	 * Return an empty string if the element doesn't have a particle.
	 * If it has a non empty one, it returns it prepended by a dash.
	 * 
	 * @param element
	 * @return	the String to be appended to the element's realisation
	 */
	protected String getParticle(InflectedWordElement element) {
		String particle = element.getFeatureAsString(Feature.PARTICLE);
		
		if (particle == null) particle = "";
		else if (!particle.isEmpty()) particle = "-" + particle;
		
		return particle;
	}

//	/**
//	 * @param element
//	 * @return true if parent or grandparent of element is feminine
//	 */
//	public boolean getParentOrGrandParentFeminine(InflectedWordElement element) {
//		// Get gender from parent or "grandparent" for adjectives
//		NLGElement parent = element.getParent();
//		boolean feminine = false;
//		if (parent != null ) {
//			if (!parent.hasFeature(LexicalFeature.GENDER) && parent.getParent() != null) {
//				parent = parent.getParent();
//			}
//			feminine = Gender.FEMININE.equals( parent.getFeature(LexicalFeature.GENDER) );
//		}
//		return feminine;
//	}

	
	/**
	 * Builds the plural form of a noun or adjective following regular rules.
	 * Reference : sections 504-505, 538-539 of Grevisse (1993)
	 * 
	 * @param form form being realised on wich to apply the plural morphology
	 * @return the plural form
	 */
	public String buildRegularPlural(String form) {
		if (form.endsWith("s") || form.endsWith("x") || form.endsWith("z")) {
			// do nothing
		}
		// "au" covers also -eau
		else if (form.endsWith("au") || form.endsWith("eu")) {
			form += "x";
		}
		else if (form.endsWith("al")) {
			form = form.substring(0, form.length()-2) + "aux";
		}
		else {
			form += "s";
		}
		return form;
	}

	/**
	 * This method performs the morphology for nouns.
	 * Based in part on the same method in the english rules
	 * Reference : sections 504-505 of Grevisse (1993)
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	@Override
	public StringElement doNounMorphology(
			InflectedWordElement element, WordElement baseWord) {
		String realised = "";

		// Check if the noun's gender needs to be changed
		// and change base form and baseWord accordingly
		if (baseWord != null) {
			Object elementGender = element.getFeature(LexicalFeature.GENDER);
			Object baseWordGender = baseWord.getFeature(LexicalFeature.GENDER);
			// The gender of the inflected word is opposite to the base word 
			if ((Gender.MASCULINE.equals(baseWordGender) &&	Gender.FEMININE.equals(elementGender))
				|| (Gender.FEMININE.equals(baseWordGender) && Gender.MASCULINE.equals(elementGender))) {
				
				String oppositeGenderForm = baseWord.getFeatureAsString(FrenchLexicalFeature.OPPOSITE_GENDER);
				
				if (oppositeGenderForm == null) {
					// build opposite gender form if possible
					if (Gender.MASCULINE.equals(baseWordGender)) {
						// the base word is masculine and the feminine must be build
						// (to be completed if necessary)
					}
					else {
						// the base word is feminine and the masculine must be build
						// (to be completed if necessary)
					}
				}
				// if oppositeGenderForm is specified or has been built
				if (oppositeGenderForm != null) {
					// change base form and base word
					element.setFeature(LexicalFeature.BASE_FORM, oppositeGenderForm);
					baseWord = baseWord.getLexicon().lookupWord(oppositeGenderForm, LexicalCategory.NOUN);
					element.setBaseWord(baseWord);
				}
			}
		}
		
		// base form from element if it exists, otherwise from baseWord 
		String baseForm = getBaseForm(element, baseWord);
		
		if (element.isPlural()
				&& !element.getFeatureAsBoolean(LexicalFeature.PROPER)) {

			String pluralForm = null;

			pluralForm = element.getFeatureAsString(LexicalFeature.PLURAL);

			if (pluralForm == null && baseWord != null) {
				pluralForm = baseWord.getFeatureAsString(LexicalFeature.PLURAL);
			}
			
			if (pluralForm == null) {
				pluralForm = buildRegularPlural(baseForm);
			}
			realised = pluralForm;
		} else {
			realised = baseForm;
		}
		
		realised += getParticle(element);
		StringElement realisedElement = new StringElement(realised.toString(), element);
		return realisedElement;
	}

	/**
	 * This method performs the morphology for verbs.
	 * Based in part on the same method in the english rules
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	@Override
	public NLGElement doVerbMorphology(InflectedWordElement element,
			WordElement baseWord) {

		String realised = null;
		
		Object numberValue = element.getFeature(Feature.NUMBER);
		// default number is SINGULAR
		NumberAgreement number = NumberAgreement.SINGULAR;
		if (numberValue instanceof NumberAgreement) {
			number = (NumberAgreement) numberValue;
		}
		
		Object personValue = element.getFeature(Feature.PERSON);
		// default person is THIRD
		Person person = Person.THIRD;
		if (personValue instanceof Person) {
			person = (Person) personValue;
		}
		
		Object genderValue = element.getFeature(LexicalFeature.GENDER);
		// default gender is MASCULINE
		Gender gender = Gender.MASCULINE;
		if (genderValue instanceof Gender) {
			gender = (Gender) genderValue;
		}
		
		Object tenseValue = element.getFeature(Feature.TENSE);
		// default tense is PRESENT
		Tense tense = Tense.PRESENT;
		if (tenseValue instanceof Tense) {
			tense = (Tense) tenseValue;
		}
		
		Object formValue = element.getFeature(Feature.FORM);

		// participles that are not directly in a verb phrase
		// get their gender and number like adjectives
		if (formValue == Form.PRESENT_PARTICIPLE || formValue == Form.PAST_PARTICIPLE) {
			// Get gender and number from parent or "grandparent" or self, in that order
			NLGElement parent = element.getParent();
			if ( parent != null) {
				boolean aggreement = false;
				Object function = element.getFeature(InternalFeature.DISCOURSE_FUNCTION);
				// used as epithet or as attribute of the subject
				if (!parent.isA(PhraseCategory.VERB_PHRASE) || function == DiscourseFunction.OBJECT) {
					if (!parent.hasFeature(LexicalFeature.GENDER) && parent.getParent() != null) {
						parent = parent.getParent();
					}
					aggreement = true;
				} else {
					// used as attribute of the direct object
					if (function == DiscourseFunction.FRONT_MODIFIER
							|| function == DiscourseFunction.PRE_MODIFIER
							|| function == DiscourseFunction.POST_MODIFIER) {
						List<NLGElement> complements =
							parent.getFeatureAsElementList(InternalFeature.COMPLEMENTS);
						NLGElement directObject = null;
						for (NLGElement complement: complements) {
							if (complement.getFeature(InternalFeature.DISCOURSE_FUNCTION) ==
									DiscourseFunction.OBJECT) {
								directObject = complement;
							}
						}
						if (directObject != null) parent = directObject;
						aggreement = true;
					}
				}
				
				if (aggreement) {
					Object parentGender = parent.getFeature(LexicalFeature.GENDER);
					if (parentGender instanceof Gender) {
						gender = (Gender) parentGender;
					}
					
					Object parentNumber = parent.getFeature(Feature.NUMBER);
					if (parentNumber instanceof NumberAgreement) {
						number = (NumberAgreement) parentNumber;
					}
				}
			}
		}
			
		// base form from baseWord if it exists, otherwise from element
		String baseForm = getBaseForm(element, baseWord);

		if (Form.BARE_INFINITIVE.equals(formValue) || Form.INFINITIVE.equals(formValue) ) {
			realised = baseForm;
			
		} else if ( Form.PRESENT_PARTICIPLE.equals(formValue)
		         || Form.GERUND.equals(formValue) ) {
			// Reference : section 777 of Grevisse (1993)
			realised = element
					.getFeatureAsString(LexicalFeature.PRESENT_PARTICIPLE);

			if (realised == null && baseWord != null) {
				realised = baseWord
						.getFeatureAsString(LexicalFeature.PRESENT_PARTICIPLE);
			}
			if (realised == null) {
				String radical = getImparfaitPresPartRadical(element, baseWord, baseForm);
				realised = radical + "ant";
			}
			// Note : The gender and number features must only be
			// passed to the present participle by the syntax when
			// the present participle is used as an adjective.
			// Otherwise it is immutable.
			if (gender == Gender.FEMININE) realised += "e";
			if (number == NumberAgreement.PLURAL) realised += "s";
			
		} else if (Form.PAST_PARTICIPLE.equals(formValue)) {
			// Reference : section 778 of Grevisse (1993)
			// get or build masculine form
			realised = element
					.getFeatureAsString(LexicalFeature.PAST_PARTICIPLE);
			
			if (realised == null && baseWord != null) {
				realised = baseWord
						.getFeatureAsString(LexicalFeature.PAST_PARTICIPLE);
			}
			
			if (realised == null) {
				realised = buildPastParticipleVerb(baseForm);
			}
			
			// get or build feminine form
			if (gender == Gender.FEMININE) {
				String feminineForm = element
					.getFeatureAsString(FrenchLexicalFeature.FEMININE_PAST_PARTICIPLE);
				if (feminineForm == null && baseWord != null) {
					feminineForm = baseWord
						.getFeatureAsString(FrenchLexicalFeature.FEMININE_PAST_PARTICIPLE);
				}
				if (feminineForm == null) realised += "e";
				else realised = feminineForm;
			}
			
			// build plural form
			if (number == NumberAgreement.PLURAL && !realised.endsWith("s")) {
				realised += "s";
			}

		} else if (formValue == Form.SUBJUNCTIVE) {
			// try to get inflected form from user feature or lexicon
			switch ( number ) {
			case SINGULAR: case BOTH:
				switch ( person ) {
				case FIRST:
					realised = element.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE1S);
					if (realised == null && baseWord != null) {
						realised = baseWord.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE1S);
					}
					break;
				case SECOND:
					realised = element.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE2S);
					if (realised == null && baseWord != null) {
						realised = baseWord.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE2S);
					}
					break;
				case THIRD:
					realised = element.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE3S);
					if (realised == null && baseWord != null) {
						realised = baseWord.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE3S);
					}
					break;
				}
				break;
			case PLURAL:
				switch ( person ) {
				case FIRST:
					realised = element.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE1P);
					if (realised == null && baseWord != null) {
						realised = baseWord.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE1P);
					}
					break;
				case SECOND:
					realised = element.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE2P);
					if (realised == null && baseWord != null) {
						realised = baseWord.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE2P);
					}
					break;
				case THIRD:
					realised = element.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE3P);
					if (realised == null && baseWord != null) {
						realised = baseWord.getFeatureAsString(FrenchLexicalFeature.SUBJUNCTIVE3P);
					}
					break;
				}
				break;
			}
			// build inflected form if none was specified by the user or lexicon
			if (realised == null) {
				realised = buildSubjunctiveVerb(baseForm, number, person);
			}

		} else if (tense == null || tense == Tense.PRESENT || formValue == Form.IMPERATIVE) {
			
			if (formValue == Form.IMPERATIVE) {
				switch (number) {
				case  SINGULAR: case BOTH:
					realised = element.getFeatureAsString(FrenchLexicalFeature.IMPERATIVE2S);
					if (realised == null && baseWord != null) {
						realised = baseWord.getFeatureAsString(FrenchLexicalFeature.IMPERATIVE2S);
					}
					// generally, imperative present 2S = indicative present 1S
					if (realised == null) person = Person.FIRST;
					break;
				case PLURAL:
					switch (person) {
					case FIRST:
						realised = element.getFeatureAsString(FrenchLexicalFeature.IMPERATIVE1P);
						if (realised == null && baseWord != null) {
							realised = baseWord.getFeatureAsString(FrenchLexicalFeature.IMPERATIVE1P);
						}
						// generally, imperative 1P = indicative 1P
						break;
					default:
						realised = element.getFeatureAsString(FrenchLexicalFeature.IMPERATIVE2P);
						if (realised == null && baseWord != null) {
							realised = baseWord.getFeatureAsString(FrenchLexicalFeature.IMPERATIVE2P);
						}
						// generally, imperative 2P = indicative 2P
						if (realised == null) person = Person.SECOND;
						break;
					}
					break;
				}
			}
			
			// indicative
			if (realised == null) {
				// try to get inflected form from user feature or lexicon
				switch ( number ) {
				case SINGULAR: case BOTH:
					switch ( person ) {
					case FIRST:
						realised = element.getFeatureAsString(FrenchLexicalFeature.PRESENT1S);
						if (realised == null && baseWord != null) {
							realised = baseWord.getFeatureAsString(FrenchLexicalFeature.PRESENT1S);
						}
						break;
					case SECOND:
						realised = element.getFeatureAsString(FrenchLexicalFeature.PRESENT2S);
						if (realised == null && baseWord != null) {
							realised = baseWord.getFeatureAsString(FrenchLexicalFeature.PRESENT2S);
						}
						break;
					case THIRD:
						realised = element.getFeatureAsString(FrenchLexicalFeature.PRESENT3S);
						if (realised == null && baseWord != null) {
							realised = baseWord.getFeatureAsString(FrenchLexicalFeature.PRESENT3S);
						}
						break;
					}
					break;
				case PLURAL:
					switch ( person ) {
					case FIRST:
						realised = element.getFeatureAsString(FrenchLexicalFeature.PRESENT1P);
						if (realised == null && baseWord != null) {
							realised = baseWord.getFeatureAsString(FrenchLexicalFeature.PRESENT1P);
						}
						break;
					case SECOND:
						realised = element.getFeatureAsString(FrenchLexicalFeature.PRESENT2P);
						if (realised == null && baseWord != null) {
							realised = baseWord.getFeatureAsString(FrenchLexicalFeature.PRESENT2P);
						}
						break;
					case THIRD:
						realised = element.getFeatureAsString(FrenchLexicalFeature.PRESENT3P);
						if (realised == null && baseWord != null) {
							realised = baseWord.getFeatureAsString(FrenchLexicalFeature.PRESENT3P);
						}
						break;
					}
					break;
				}
				// build inflected form if none was specified by the user or lexicon
				if (realised == null) {
					realised = buildPresentVerb(baseForm, number, person);
				}
			}
		
		} else if ((tense == Tense.FUTURE) || (tense == Tense.CONDITIONAL)) {
			String radical = getFutureConditionalRadical(baseForm, baseWord, baseWord);
			if (tense == Tense.FUTURE) realised = buildFutureVerb(radical, number, person);
			else realised = buildConditionalVerb(radical, number, person);
		
			// "imparfait" (progressive not perfect past)
		} else if (tense == Tense.PAST)  {
			String radical = getImparfaitPresPartRadical(element, baseWord, baseForm);
			// build inflected form with radical
			realised = addImparfCondSuffix(radical, number, person);
			
		} else {
			realised = baseForm;
		}

		realised += getParticle(element);
		StringElement realisedElement = new StringElement(realised, element);
		return realisedElement;
	}

	/**
	 * Gets or builds the radical used for "imparfait" and present participle.
	 * Reference : Mansouri (1996)
	 * 
	 * @param element
	 * @param baseWord
	 * @param baseForm
	 * @return the "imparfait" and present participle radical
	 */
	protected String getImparfaitPresPartRadical(InflectedWordElement element, WordElement baseWord, String baseForm) {
		// try to get inflected form from user feature or lexicon
		// otherwise take infinitive (base form)
		String radical = element.getFeatureAsString(FrenchLexicalFeature.IMPARFAIT_RADICAL);
		if (radical == null && baseWord != null) {
			radical = baseWord.getFeatureAsString(FrenchLexicalFeature.IMPARFAIT_RADICAL);
		}
		// uses first person plural present radical
		if (radical == null) {
			radical = element.getFeatureAsString(FrenchLexicalFeature.PRESENT1P);
			if (radical == null && baseWord != null) {
				radical = baseWord.getFeatureAsString(FrenchLexicalFeature.PRESENT1P);
				if (radical == null){
					radical = buildPresentVerb(baseForm, NumberAgreement.PLURAL, Person.FIRST);
				} 
			}
			// removes -ons suffix
			int radicalLength = radical.length();
			if (radicalLength > 3) radical = radical.substring(0, radicalLength-3);
		}
		return radical;
	}
	
	/**
	 * Builds the present form for regular verbs. 
	 * Reference : Mansouri (1996)
	 *
	 * @param baseForm
	 *            the base form of the word.
	 * @param number
	 * @param person
	 * @return the inflected word.
	 */
	protected String buildPresentVerb(String baseForm, NumberAgreement number,
			Person person) {
		// Get radical and verbEndingCategory.
		GetPresentRadicalReturn multReturns =
				getPresentRadical(baseForm, number, person);
		String radical = multReturns.radical;
		int verbEndingCategory = multReturns.verbEndingCategory;
		
		// Determine suffix with verb ending category.
		String suffix = "";		
		if (verbEndingCategory != 0) {
			switch ( number ) {
			case SINGULAR: case BOTH:
				// verb ending categories for present singular
				switch (verbEndingCategory) {
				// first verb ending category
				case 1:
					switch ( person ) {
					case FIRST: case THIRD:
						suffix = "e";
						break;
					case SECOND:
						suffix = "es";
						break;
					}
					break;
				// second verb ending category
				case 2:
					switch ( person ) {
					case FIRST: case SECOND:
						suffix = "s";
						break;
					case THIRD:
						suffix = "t";
						break;
					}
					break;
				// third verb ending category
				case 3:
					switch ( person ) {
					case FIRST: case SECOND:
						suffix = "s";
						break;
					case THIRD:
						suffix = "";
						break;
					}
					break;
				}
				break;
			case PLURAL:
				switch ( person ) {
				case FIRST:
					suffix = "ons";
					break;
				case SECOND:
					suffix = "ez";
					break;
				case THIRD:
					suffix = "ent";
					break;
				}
				break;
			}
		}
		
		return addSuffix(radical, suffix);
	}
	
	/**
	 * Builds the subjunctive present form for regular verbs. 
	 * Reference : Mansouri (1996)
	 *
	 * @param baseForm
	 *            the base form of the word.
	 * @param number
	 * @param person
	 * @return the inflected word.
	 */
	protected String buildSubjunctiveVerb( String baseForm,
			NumberAgreement number, Person person) {
		Person radicalPerson = person;
		NumberAgreement radicalNumber = number;
		
		// Compared to indicative present, singular persons
		// take the radical of third person plural.
		if (number == NumberAgreement.SINGULAR) {
			radicalNumber = NumberAgreement.PLURAL;
			radicalPerson = Person.THIRD;
		}
		
		// Get radical.
		GetPresentRadicalReturn multReturns =
				getPresentRadical(baseForm, radicalNumber, radicalPerson);
		String radical = multReturns.radical;
		
		// Determine suffix.
		String suffix = "";		
		switch ( number ) {
		case SINGULAR: case BOTH:
			switch ( person ) {
			case FIRST: case THIRD:
				suffix = "e";
				break;
			case SECOND:
				suffix = "es";
				break;
			}
			break;
		case PLURAL:
			switch ( person ) {
			case FIRST:
				suffix = "ions";
				break;
			case SECOND:
				suffix = "iez";
				break;
			case THIRD:
				suffix = "ent";
				break;
			}
			break;
		}
		
		return addSuffix(radical, suffix);
	}
	
	/**
	 * @param baseForm
	 * @param element 
	 * @param baseWord 
	 * @return the radical used in indicative simple future
	 *         and conditional present
	 */
	protected GetPresentRadicalReturn getPresentRadical( String baseForm,
			NumberAgreement number, Person person) {
		int length = baseForm.length();
		String radical = baseForm;
		// verb ending category for present singular
		int verbEndingCategory = 0;
		
		// with verb ending, determine verb category and radical  
		// based on "aimer"
		if (baseForm.endsWith("er") ) {
			radical = baseForm.substring(0, length-2);
			verbEndingCategory = 1;
		// base on "voir"
		} else if (baseForm.endsWith("oir")) {
			radical = baseForm.substring(0, length-2);
			verbEndingCategory = 2;
			if (number == NumberAgreement.PLURAL) {
				radical += "y";
			} else {
				radical += "i";
			}
		// based on "finir"
		} else if (baseForm.endsWith("ir")) {
			radical = baseForm.substring(0, length-1);
			verbEndingCategory = 2;
			if (number == NumberAgreement.PLURAL) {
				radical += "ss";
			// for verbs like "haïr"
			} else if (radical.endsWith("ï")) {
				radical = radical.substring(0, length-1) + "i";
			}
		// based on "vendre" and "mettre"
		} else if (baseForm.endsWith("re")) {
			radical = baseForm.substring(0, length-2);
			verbEndingCategory = 3;
			// "mettre" singular (and NumberAgreement.BOTH)
			if (number != NumberAgreement.PLURAL && radical.endsWith("t")) {
				// remove last "t"
				radical = radical.substring(0, radical.length()-1);
			}
		}
		return new GetPresentRadicalReturn(radical, verbEndingCategory);
	}

	/**
	 * Class used to get two return values from the getPresentRadical method
	 * @author vaudrypl
	 */
	protected class GetPresentRadicalReturn {
		public final String radical;
		public final int verbEndingCategory;
		
		public GetPresentRadicalReturn(String radical, int verbEndingCategory) {
			this.radical = radical;
			this.verbEndingCategory = verbEndingCategory;
		}
	}

	/**
	 * Adds a radical and a suffix applying phonological rules
	 * Reference : sections 760-761 of Grevisse (1993)
	 * 
	 * @param radical
	 * @param suffix
	 * @return resultant form
	 */
	public String addSuffix(String radical, String suffix) {
		int length = radical.length();
		// change "c" to "ç" and "g" to "ge" before "a" and "o";
		if (suffix.matches(a_o_regex)) {
			if (radical.endsWith("c")) {
				radical = radical.substring(0, length-1) + "ç";
			} else if (radical.endsWith("g")) {
				radical += "e";
			}
		}
		// if suffix begins with mute "e"
		if (!suffix.equals("ez") && suffix.startsWith("e")) {
			// change "y" to "i" if not in front of "e"
			if (!radical.endsWith("ey") && radical.endsWith("y")) {
				radical = radical.substring(0,length-1) + "i";
			}
			// change "e" and "é" to "è" in last sillable of radical
			char penultimate = radical.charAt(length-2);
			if (penultimate == 'e' || penultimate == 'é') {
				radical = radical.substring(0,length-2) + "è"
						+ radical.substring(length-1);
			}
		}
		return radical + suffix;
	}

	/**
	 * Builds the simple future form for all verbs. 
	 * Reference : Mansouri (1996)
	 *
	 * @param radical
	 *            the future radical of the word.
	 * @param number
	 * @param person
	 * @return the inflected word.
	 */
	protected String buildFutureVerb(String radical, NumberAgreement number,
			Person person) {
		String suffix = "";
		
		switch ( number ) {
		case SINGULAR: case BOTH:
			switch ( person ) {
			case FIRST:
				suffix = "ai";
				break;
			case SECOND:
				suffix = "as";
				break;
			case THIRD:
				suffix = "a";
				break;
			}
			break;
		case PLURAL:
			switch ( person ) {
			case FIRST:
				suffix = "ons";
				break;
			case SECOND:
				suffix = "ez";
				break;
			case THIRD:
				suffix = "ont";
				break;
			}
			break;
		}
		
		return radical + suffix;
	}

	/**
	 * Builds the conditional present form for all verbs. 
	 * Reference : Mansouri (1996)
	 *
	 * @param radical
	 *            the future radical of the word.
	 * @param number
	 * @param person
	 * @return the inflected word.
	 */
	protected String buildConditionalVerb(String radical, NumberAgreement number,
			Person person) {
		String suffix = "";
		
		switch ( number ) {
		case SINGULAR: case BOTH:
			switch ( person ) {
			case FIRST:
				suffix = "ais";
				break;
			case SECOND:
				suffix = "ais";
				break;
			case THIRD:
				suffix = "ait";
				break;
			}
			break;
		case PLURAL:
			switch ( person ) {
			case FIRST:
				suffix = "ions";
				break;
			case SECOND:
				suffix = "iez";
				break;
			case THIRD:
				suffix = "aient";
				break;
			}
			break;
		}
		
		return radical + suffix;
	}

	/**
	 * @param baseForm
	 * @param element 
	 * @param baseWord 
	 * @return the radical used in indicative simple future
	 *         and conditional present
	 */
	protected String getFutureConditionalRadical(String baseForm, NLGElement element, NLGElement baseWord) {
		// try to get inflected form from user feature or lexicon
		String radical = element.getFeatureAsString(FrenchLexicalFeature.FUTURE_RADICAL);
		if (radical == null && baseWord != null) {
			radical = baseWord.getFeatureAsString(FrenchLexicalFeature.FUTURE_RADICAL);
			}
		
		// otherwise apply general rules based on infinitive (base form)
		if (radical == null) {
			int length = baseForm.length();
			char penultimateVowel = baseForm.charAt(length-4);
			
			// for verbs like "mettre" and "vendre"
			if (baseForm.endsWith("e")) {
				// remove last "e" to leave a final "r"
				radical = baseForm.substring(0, length-1);
			// for verbs like "aboyer"
			} else if (baseForm.endsWith("yer")) {
				radical = baseForm.substring(0, length-3) + "ier";
			// change "e" and "é" to "è" in second last sillable of radical
			} else if (penultimateVowel == 'e' || penultimateVowel == 'é') {
				radical = baseForm.substring(0,length-4) + "è"
						+ baseForm.substring(length-3);
			} else {
				radical = baseForm;
			}				
		}
		
		return radical;
	}

	/**
	 * Adds the "imparfait" and "conditionel" suffix to a verb radical. 
	 * Reference : Mansouri (1996)
	 *
	 * @param baseForm
	 *            the base form of the word.
	 * @param number
	 * @param person
	 * @return the inflected word.
	 */
	protected String addImparfCondSuffix(String radical, NumberAgreement number,
			Person person) {
		String suffix = "";
		
		switch ( number ) {
		case SINGULAR: case BOTH:
			switch ( person ) {
			case FIRST:
				suffix = "ais";
				break;
			case SECOND:
				suffix = "ais";
				break;
			case THIRD:
				suffix = "ait";
				break;
			}
			break;
		case PLURAL:
			switch ( person ) {
			case FIRST:
				suffix = "ions";
				break;
			case SECOND:
				suffix = "iez";
				break;
			case THIRD:
				suffix = "aient";
				break;
			}
			break;
		}
		
		return radical + suffix;
	}

	/**
	 * Builds the past participle form for regular verbs. 
	 * Reference : section 778 of Grevisse (1993)
	 *
	 * @param baseForm
	 *            the base form of the word.
	 * @return the inflected word.
	 */
	protected String buildPastParticipleVerb(String baseForm) {
		int length = baseForm.length();
		String realised = baseForm;
		
		// based on "aimer"
		if (baseForm.endsWith("er")) {
			realised = baseForm.substring(0, length-2) + "é";
		// base on "voir"
		} else if (baseForm.endsWith("oir")) {
			realised = baseForm.substring(0, length-3) + "u";
		// based on "finir"
		} else if (baseForm.endsWith("ir")) {
			realised = baseForm.substring(0, length-1);
		// based on "mettre"
		} else if (baseForm.endsWith("mettre")) {
			realised = baseForm.substring(0, length-5) + "is";
		// based on "vendre"
		} else if (baseForm.endsWith("re")) {
			realised = baseForm.substring(0, length-2) + "u";
		}
		
		return realised;
	}

	/**
	 * This method performs the morphology for adverbs.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public NLGElement doAdverbMorphology(InflectedWordElement element,
			WordElement baseWord) {

		String realised = null;

		// base form from baseWord if it exists, otherwise from element
		String baseForm = getBaseForm(element, baseWord);

		// Comparatives and superlatives are mainly treated by syntax
		// in French. Only exceptions, provided by the lexicon, are
		// treated by morphology.
		if (element.getFeatureAsBoolean(Feature.IS_COMPARATIVE).booleanValue()) {
			realised = element.getFeatureAsString(LexicalFeature.COMPARATIVE);

			if (realised == null && baseWord != null) {
				realised = baseWord
						.getFeatureAsString(LexicalFeature.COMPARATIVE);
			}
			if (realised == null) realised = baseForm;
		} else {
			realised = baseForm;
		}

		realised += getParticle(element);
		StringElement realisedElement = new StringElement(realised, element);
		return realisedElement;
	}

	/**
	 * This method performs the morphology for pronouns.
	 * Reference : sections 633-634 of Grevisse (1993)
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public NLGElement doPronounMorphology(InflectedWordElement element) {
		String realised = element.getBaseForm();
		
		Object type = element.getFeature(FrenchLexicalFeature.PRONOUN_TYPE);
		// inflect only personal pronouns, exluding complement pronouns ("y" and "en")
		if (type == PronounType.PERSONAL
			&& element.getFeature(InternalFeature.DISCOURSE_FUNCTION)
				!= DiscourseFunction.COMPLEMENT) {
			
			// this will contain the features we want the pronoun to have
			Map<String, Object> pronounFeatures = new HashMap<String, Object>();
	
			pronounFeatures.put(FrenchLexicalFeature.PRONOUN_TYPE, type);
			
			boolean passive = element.getFeatureAsBoolean(Feature.PASSIVE);
			boolean reflexive = element.getFeatureAsBoolean(LexicalFeature.REFLEXIVE);
			boolean detached = isDetachedPronoun(element);
			NLGElement parent = element.getParent();
			
			Object gender = element.getFeature(LexicalFeature.GENDER);
			if (!(gender instanceof Gender) || gender == Gender.NEUTER) gender = Gender.MASCULINE;
			
			Object person = element.getFeature(Feature.PERSON);
			Object number = element.getFeature(Feature.NUMBER);			
			// agree the reflexive pronoun with the subject
			if (reflexive && parent != null) {
				NLGElement grandParent =  parent.getParent();
				if (grandParent != null && grandParent.getCategory().equalTo(PhraseCategory.VERB_PHRASE)) {
					person = grandParent.getFeature(Feature.PERSON);
					number = grandParent.getFeature(Feature.NUMBER);
					
					// If the verb phrase is in imperative form,
					// the reflexive pronoun can only be in 2S, 1P or 2P.
					if (grandParent.getFeature(Feature.FORM) == Form.IMPERATIVE) {
						if (number == NumberAgreement.PLURAL) {
							if (person != Person.FIRST && person != Person.SECOND) {
								person = Person.SECOND;
							}
						} else {
							person = Person.SECOND;
						}
					}
				}
			}
			if (!(person instanceof Person)) person = Person.THIRD;
			if (!(number instanceof NumberAgreement)) number = NumberAgreement.SINGULAR;
			
			Object function = element.getFeature(InternalFeature.DISCOURSE_FUNCTION);
			// If the pronoun is the head of a noun phrase,
			// take the discourse function of this noun phrase
			if (function == DiscourseFunction.SUBJECT && parent != null
					&& parent.isA(PhraseCategory.NOUN_PHRASE)) {
				function = parent.getFeature(InternalFeature.DISCOURSE_FUNCTION);
			}
			if (!detached && !(function instanceof DiscourseFunction)) function = DiscourseFunction.SUBJECT;
			if (passive) {
				if (function == DiscourseFunction.SUBJECT) function = DiscourseFunction.OBJECT;
				else if (function == DiscourseFunction.OBJECT) function = DiscourseFunction.SUBJECT;
			}
			
			if (function != DiscourseFunction.OBJECT && function != DiscourseFunction.INDIRECT_OBJECT
					&& !detached) {
				reflexive = false;
			}
			
			pronounFeatures.put(Feature.PERSON, person);
			
			// select wich features to include in search depending on pronoun features,
			// syntactic function and wether the pronoun is detached from the verb
			if (person == Person.THIRD) {
				pronounFeatures.put(LexicalFeature.REFLEXIVE, reflexive);
				pronounFeatures.put(FrenchLexicalFeature.DETACHED, detached);
				if (!reflexive) {
					pronounFeatures.put(Feature.NUMBER, number);
					if (!detached) {
						pronounFeatures.put(InternalFeature.DISCOURSE_FUNCTION, function);
						if ((number != NumberAgreement.PLURAL && function != DiscourseFunction.INDIRECT_OBJECT)
								|| function == DiscourseFunction.SUBJECT) {
							pronounFeatures.put(LexicalFeature.GENDER, gender);
						}
					} else {
						pronounFeatures.put(LexicalFeature.GENDER, gender);
					}
				}
			} else {
				pronounFeatures.put(Feature.NUMBER, number);
				if (!element.isPlural()) {
					pronounFeatures.put(FrenchLexicalFeature.DETACHED, detached);
					if (!detached) {
						if (function != DiscourseFunction.SUBJECT) function = null;
						pronounFeatures.put(InternalFeature.DISCOURSE_FUNCTION, function);
					}
				}
			}
	
			Lexicon lexicon = element.getLexicon();
			// search the lexicon for the right pronoun
			WordElement proElement =
				lexicon.getWord(LexicalCategory.PRONOUN, pronounFeatures);
			
			// if the right pronoun is not found in the lexicon,
			// leave the original pronoun
			if (proElement != null) {
				element = new InflectedWordElement(proElement);
				realised = proElement.getBaseForm();
			}
			
		// Agreement of relative pronouns with parent noun phrase.
		} else if (type == PronounType.RELATIVE) {
			// Get parent clause.
			NLGElement antecedent = element.getParent();
			while (antecedent != null
					&& !antecedent.isA(PhraseCategory.CLAUSE)) {
				antecedent = antecedent.getParent();
			}
/*			// Skip parent noun phrase if necessary.
			if (antecedent != null && antecedent.isA(PhraseCategory.NOUN_PHRASE)) {
				antecedent = antecedent.getParent();
			}
			
			// Skip parent prepositional phrase if necessary.
			if (antecedent != null && antecedent.isA(PhraseCategory.PREPOSITIONAL_PHRASE)) {
				antecedent = antecedent.getParent();
			}
*/
			
			if (antecedent != null) {
				// Get parent noun phrase of parent clause.
				antecedent = antecedent.getParent();
				if (antecedent != null) {
					boolean feminine = antecedent.getFeature(LexicalFeature.GENDER)
							== Gender.FEMININE;
					boolean plural = antecedent.getFeature(Feature.NUMBER)
							== NumberAgreement.PLURAL;
					
					// Lookup lexical entry for appropriate form.
					// If the corresponding form is not found :
					// Feminine plural defaults to masculine plural.
					// Feminine singular and masculine plural default
					// to masculine singular.
					String feature = null;
					if (feminine && plural) {
						feature = element.getFeatureAsString(
								FrenchLexicalFeature.FEMININE_PLURAL);
					} else if (feminine) {
						feature = element.getFeatureAsString(
								FrenchLexicalFeature.FEMININE_SINGULAR);
					}
					
					if (plural && feature == null ) {
						feature = element.getFeatureAsString(
								LexicalFeature.PLURAL);
					}
					
					if (feature != null) realised = feature;
				}
			}
		}
	
		realised += getParticle(element);
		StringElement realisedElement = new StringElement(realised, element);

		return realisedElement;
	}

	/**
	 * Determine if the pronoun is detached ("disjoint") from the verb.
	 * 
	 * @param element the pronoun
	 * @return true if element is a pronoun and is detached from the web
	 */
	public boolean isDetachedPronoun(InflectedWordElement element) {
		boolean detached = false;
		
		if (element.getCategory().equalTo(LexicalCategory.PRONOUN)) {
			NLGElement parent = element.getParent();
			Object function;
			
			if (parent != null) {
				function = parent.getFeature(InternalFeature.DISCOURSE_FUNCTION);
				// If the pronoun isn't a suject or an object, it is detached.
				
				if (!(function == DiscourseFunction.SUBJECT
						|| function == DiscourseFunction.OBJECT
						|| function == DiscourseFunction.INDIRECT_OBJECT)) {
					detached = true;
				} else {
					NLGElement grandParent = parent.getParent();
					// If the pronoun is in a prepositional phrase,
					// or it is 1rst or 2nd person and the verb is in imperative form
					// but not negated, it is detached.
					Object person = element.getFeature(Feature.PERSON);
					boolean reflexive = element.getFeatureAsBoolean(LexicalFeature.REFLEXIVE);
					boolean person1or2 = (person == Person.FIRST || person == Person.SECOND);
					if ( PhraseCategory.PREPOSITIONAL_PHRASE.equalTo(parent.getCategory())
						|| ((person1or2 || reflexive)
								&& parent.getFeature(Feature.FORM) == Form.IMPERATIVE
								&& !parent.getFeatureAsBoolean(Feature.NEGATED)) ||
							parent instanceof CoordinatedPhraseElement ||
						(grandParent != null &&
							(PhraseCategory.PREPOSITIONAL_PHRASE.equalTo(grandParent.getCategory())
								|| ((person1or2 || reflexive)
										&& grandParent.getFeature(Feature.FORM) == Form.IMPERATIVE
										&& !grandParent.getFeatureAsBoolean(Feature.NEGATED))
								|| grandParent instanceof CoordinatedPhraseElement))) {
						detached = true;
					}
				}
			// if there's no parent
			} else detached = true;
		}
		
		return detached;
	}

}
