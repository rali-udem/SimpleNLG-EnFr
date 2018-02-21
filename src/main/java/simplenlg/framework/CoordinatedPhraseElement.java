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

package simplenlg.framework;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Person;
import simplenlg.syntax.AbstractCoordinatedPhraseHelper;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.LexicalCategory;

/**
 * <p>
 * This class defines coordination between two or more phrases. Coordination
 * involves the linking of phrases together through the use of key words such as
 * <em>and</em> or <em>but</em>.
 * </p>
 * 
 * <p>
 * The class does not perform any ordering on the coordinates and when realised
 * they appear in the same order they were added to the coordination.
 * </p>
 * 
 * <p>
 * As this class appears similar to the <code>PhraseElement</code> class from an
 * API point of view, it could have extended from the <code>PhraseElement</code>
 * class. However, they are fundamentally different in their nature and thus
 * form two distinct classes with similar APIs.
 * </p>
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 * 
 */
public class CoordinatedPhraseElement extends NLGElement {

	// added by vaudrypl
	private static Map<Language, AbstractCoordinatedPhraseHelper> coordinatedPhraseHelpers = null; 
	
	// not used anymore, commented out by vaudrypl
//	/** Coordinators which make the coordinate plural (eg, "and" but not "or")*/
//	@SuppressWarnings("nls")
//	private static final List<String> PLURAL_COORDINATORS = Arrays.asList("and");
	
	/**
	 * Creates a blank coordinated phrase ready for new coordinates to be added.
	 * The default conjunction used is the addition coordination conjunction.
	 * (<em>and</em> in English, <em>et</em> in French, etc.)
	 * 
	 * @param factory
	 * 			  the factory who created this phrase
	 */
	public CoordinatedPhraseElement(NLGFactory factory) {
		super();
		// changed by vaudrypl to make more generic
		setFactory(factory);
		setConjunction(getAdditionCoordConjunction());
	}

	/**
	 * Creates a blank coordinated phrase ready for new coordinates to be added.
	 * The default conjunction used is the addition coordination conjunction.
	 * (<em>and</em> in English, <em>et</em> in French, etc.)
	 */
	public CoordinatedPhraseElement() {
		setFactory(null); // default factory (vaudrypl)
		setConjunction(getAdditionCoordConjunction());
	}

	/**
	 * Creates a coordinated phrase linking the two phrase together.
	 * The default conjunction used is the addition coordination conjunction.
	 * (<em>and</em> in English, <em>et</em> in French, etc.)
	 * 
	 * @param factory
	 * 			  the factory who created this phrase
	 * @param coordinate1
	 *            the first coordinate.
	 * @param coordinate2
	 *            the second coordinate.
	 */
	public CoordinatedPhraseElement(NLGFactory factory, Object coordinate1, Object coordinate2) {
		this(factory); // added by vaudrypl
		this.addCoordinate(coordinate1);
		this.addCoordinate(coordinate2);
	}

	/**
	 * Creates a coordinated phrase linking the two phrase together.
	 * The default conjunction used is the addition coordination conjunction.
	 * (<em>and</em> in English, <em>et</em> in French, etc.)
	 * 
	 * @param coordinate1
	 *            the first coordinate.
	 * @param coordinate2
	 *            the second coordinate.
	 */
	public CoordinatedPhraseElement(Object coordinate1, Object coordinate2) {
		this(null, coordinate1, coordinate2); // default factory (vaudrypl)
	}

	/**
	 * Copy constructor
	 * 
	 * @param coordinatedPhrase
	 * 			  the factory who created this phrase
	 * 
	 * @author vaudrypl
	 */
	public CoordinatedPhraseElement(CoordinatedPhraseElement coordinatedPhrase) {
		if (coordinatedPhrase != null) {
			//the new coordinated phrase inherits all features from the base word
			for(String feature : coordinatedPhrase.getAllFeatureNames()) {
				setFeature(feature, coordinatedPhrase.getFeature(feature));
			}
			
			setFactory(coordinatedPhrase.getFactory());
			
			// copy separately element lists using getFeatureAsElementList to get copies
			// of thoses lists and avoid modifying the originals
			setFeature(InternalFeature.COMPLEMENTS, getFeatureAsElementList(InternalFeature.COMPLEMENTS));
			setFeature(InternalFeature.POSTMODIFIERS, getFeatureAsElementList(InternalFeature.POSTMODIFIERS));
			setFeature(InternalFeature.PREMODIFIERS, getFeatureAsElementList(InternalFeature.PREMODIFIERS));
			setFeature(InternalFeature.COORDINATES, getFeatureAsElementList(InternalFeature.COORDINATES));
		} else {
			setConjunction(getAdditionCoordConjunction());
		}
	}

	/**
	 * @return the coordinated phrase helper to be used to do the syntax of this word
	 * @author vaudrypl
	 */
	public AbstractCoordinatedPhraseHelper getCoordinatedPhraseHelper()
	{
		return getCoordinatedPhraseHelper( getLanguage() );
	}
	
	/**
	 * This static method returns the coordinated phrase helper corresponding to
	 * a particular language and instantiates it if necessary.
	 * 
	 * @param language
	 * @return the coordinated phrase helper to be used to do the syntax of this language
	 * @author vaudrypl
	 */
	public static AbstractCoordinatedPhraseHelper getCoordinatedPhraseHelper(Language language)
	{
		if (coordinatedPhraseHelpers == null) {
			coordinatedPhraseHelpers =
				new EnumMap<Language,AbstractCoordinatedPhraseHelper>(Language.class); 
		}
		AbstractCoordinatedPhraseHelper coordinatedPhraseHelper =
			coordinatedPhraseHelpers.get(language);
		if (coordinatedPhraseHelper == null) {
			switch (language) {
			case ENGLISH:
				coordinatedPhraseHelper =
					new simplenlg.syntax.english.nonstatic.CoordinatedPhraseHelper();
				break;
			case FRENCH:
				coordinatedPhraseHelper =
					new simplenlg.syntax.french.CoordinatedPhraseHelper();
				break;
			}
			coordinatedPhraseHelpers.put(language, coordinatedPhraseHelper);
		}
		return coordinatedPhraseHelper;
	}

	/**
	 * Adds a new coordinate to this coordination. If the new coordinate is a
	 * <code>NLGElement</code> then it is added directly to the coordination. If
	 * the new coordinate is a <code>String</code> a <code>StringElement</code>
	 * is created and added to the coordination. <code>StringElement</code>s
	 * will have their complementisers suppressed by default. In the case of
	 * clauses, complementisers will be suppressed if the clause is not the
	 * first element in the coordination.
	 * 
	 * @param newCoordinate
	 *            the new coordinate to be added.
	 */
	public void addCoordinate(Object newCoordinate) {
		List<NLGElement> coordinates = getFeatureAsElementList(InternalFeature.COORDINATES);
		if (coordinates == null) {
			coordinates = new ArrayList<NLGElement>();
			setFeature(InternalFeature.COORDINATES, coordinates);
		} else if (coordinates.size() == 0) {
			setFeature(InternalFeature.COORDINATES, coordinates);
		}
		if (newCoordinate instanceof NLGElement) {
			if (((NLGElement) newCoordinate).isA(PhraseCategory.CLAUSE)
					&& coordinates.size() > 0) {

				((NLGElement) newCoordinate).setFeature(
						Feature.SUPRESSED_COMPLEMENTISER, true);
			}
			coordinates.add((NLGElement) newCoordinate);
			
			// added by vaudrypl
			((NLGElement)newCoordinate).setParent(this);
			determineCoordinationGender((NLGElement) newCoordinate);
			determineCoordinationPerson((NLGElement) newCoordinate);
			
		} else if (newCoordinate instanceof String) {
			NLGElement coordElement = new StringElement((String) newCoordinate);
			coordElement.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);
			coordinates.add(coordElement);
			//added by vaudrypl
			coordElement.setParent(this);
		}
		setFeature(InternalFeature.COORDINATES, coordinates);
		
	}
	
	/**
	 * Determines the gender of the coordinated phrase based on the gender of
	 * the newly added coordinate and these rules :
	 * 
	 * - If the new coordinate is not a pronoun, noun or noun phrase and the
	 *   coordinated phrase doesn't gender isn't set yet, it is set to neuter.
	 * - If the new coordinate is a pronoun, noun or noun phrase, then :
	 * 		~ If the new coordinate is masculine, the coordinated phrase
	 *   	  gender is set to masculine.
	 * 		~ If the new coordinate is neuter and the coordinated phrase
	 *   	  gender isn't masculine, it is set to neuter.
	 * 		~ If the new coordinate is feminine and the coordinated phrase
	 *   	  gender isn't set yet, it is set to feminine.
	 * 
	 * @param newCoordinate
	 * 
	 * @author vaudrypl
	 */
	protected void determineCoordinationGender(NLGElement newCoordinate) {
		ElementCategory category = newCoordinate.getCategory();
		Object coordPhraseGender = getFeature(LexicalFeature.GENDER);
		
		if ( newCoordinate instanceof CoordinatedPhraseElement
			|| category == PhraseCategory.NOUN_PHRASE ||
			category == LexicalCategory.PRONOUN || category == LexicalCategory.NOUN ) {
			
			Object newCoordinateGender = newCoordinate.getFeature(LexicalFeature.GENDER);
			
			if (newCoordinateGender == Gender.MASCULINE) {
				setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
			} else if (newCoordinateGender == Gender.NEUTER) {
				if (coordPhraseGender != Gender.MASCULINE) {
					setFeature(LexicalFeature.GENDER, Gender.NEUTER);
				}
			} else if (newCoordinateGender == Gender.FEMININE) {
				if (coordPhraseGender == null) {
					setFeature(LexicalFeature.GENDER, Gender.FEMININE);
				}
			}
			
		} else if (coordPhraseGender == null) {
			setFeature(LexicalFeature.GENDER, Gender.NEUTER);
		}
	}

	/**
	 * Determines the person of the coordinated phrase based on the person of
	 * the newly added coordinate.
	 * 
	 * @param newCoordinate
	 * 
	 * @author vaudrypl
	 */
	protected void determineCoordinationPerson(NLGElement newCoordinate) {
		ElementCategory category = newCoordinate.getCategory();
		Object coordPhrasePerson = getFeature(Feature.PERSON);
		
		// If the person of the coordination is already first, nothing changes.
		if ( coordPhrasePerson != Person.FIRST && 
			(newCoordinate instanceof CoordinatedPhraseElement
				|| category == PhraseCategory.NOUN_PHRASE ||
				category == LexicalCategory.PRONOUN || category == LexicalCategory.NOUN) ) {
			
			Object newCoordinatePerson = newCoordinate.getFeature(Feature.PERSON);
			
			// If the new coordinate is first person or if the coordination person is
			// not first nor second, take the new coordinate person for the coordination.
			if (newCoordinatePerson == Person.FIRST || coordPhrasePerson != Person.SECOND) {
				setFeature(Feature.PERSON, newCoordinatePerson);
			} 
		}
	}
	
	@Override
	public List<NLGElement> getChildren() {
		return this.getFeatureAsElementList(InternalFeature.COORDINATES);
	}

	/**
	 * Clears the existing coordinates in this coordination. It performs exactly
	 * the same as <code>removeFeature(Feature.COORDINATES)</code>.
	 */
	public void clearCoordinates() {
		removeFeature(InternalFeature.COORDINATES);
		// added by vaudrypl
		removeFeature(Feature.NUMBER);
		removeFeature(Feature.PERSON);
		removeFeature(LexicalFeature.GENDER);
	}

	/**
	 * Adds a new pre-modifier to the phrase element. Pre-modifiers will be
	 * realised in the syntax before the coordinates.
	 * 
	 * @param newPreModifier
	 *            the new pre-modifier as an <code>NLGElement</code>.
	 */
	public void addPreModifier(NLGElement newPreModifier) {
		List<NLGElement> preModifiers = getFeatureAsElementList(InternalFeature.PREMODIFIERS);
		if (preModifiers == null) {
			preModifiers = new ArrayList<NLGElement>();
		}
		preModifiers.add(newPreModifier);
		setFeature(InternalFeature.PREMODIFIERS, preModifiers);
		//added by vaudrypl
		newPreModifier.setParent(this);
	}

	/**
	 * Adds a new pre-modifier to the phrase element. Pre-modifiers will be
	 * realised in the syntax before the coordinates.
	 * 
	 * @param newPreModifier
	 *            the new pre-modifier as a <code>String</code>. It is used to
	 *            create a <code>StringElement</code>.
	 */
	public void addPreModifier(String newPreModifier) {
		List<NLGElement> preModifiers = getFeatureAsElementList(InternalFeature.PREMODIFIERS);
		if (preModifiers == null) {
			preModifiers = new ArrayList<NLGElement>();
		}
		//added by vaudrypl
		StringElement stringElemPreModifier = new StringElement(newPreModifier);
		stringElemPreModifier.setParent(this);

		preModifiers.add(stringElemPreModifier);
		setFeature(InternalFeature.PREMODIFIERS, preModifiers);
	}

	/**
	 * Retrieves the list of pre-modifiers currently associated with this
	 * coordination.
	 * 
	 * @return a <code>List</code> of <code>NLGElement</code>s.
	 */
	public List<NLGElement> getPreModifiers() {
		return getFeatureAsElementList(InternalFeature.PREMODIFIERS);
	}

	/**
	 * Retrieves the list of complements currently associated with this
	 * coordination.
	 * 
	 * @return a <code>List</code> of <code>NLGElement</code>s.
	 */
	public List<NLGElement> getComplements() {
		return getFeatureAsElementList(InternalFeature.COMPLEMENTS);
	}

	/**
	 * Adds a new post-modifier to the phrase element. Post-modifiers will be
	 * realised in the syntax after the coordinates.
	 * 
	 * @param newPostModifier
	 *            the new post-modifier as an <code>NLGElement</code>.
	 */
	public void addPostModifier(NLGElement newPostModifier) {
		List<NLGElement> postModifiers = getFeatureAsElementList(InternalFeature.POSTMODIFIERS);
		if (postModifiers == null) {
			postModifiers = new ArrayList<NLGElement>();
		}
		postModifiers.add(newPostModifier);
		setFeature(InternalFeature.POSTMODIFIERS, postModifiers);
		//added by vaudrypl
		newPostModifier.setParent(this);
	}

	/**
	 * Adds a new post-modifier to the phrase element. Post-modifiers will be
	 * realised in the syntax after the coordinates.
	 * 
	 * @param newPostModifier
	 *            the new post-modifier as a <code>String</code>. It is used to
	 *            create a <code>StringElement</code>.
	 */
	public void addPostModifier(String newPostModifier) {
		List<NLGElement> postModifiers = getFeatureAsElementList(InternalFeature.POSTMODIFIERS);
		if (postModifiers == null) {
			postModifiers = new ArrayList<NLGElement>();
		}
		//added by vaudrypl
		StringElement stringElemPostModifier = new StringElement(newPostModifier);
		stringElemPostModifier.setParent(this);

		postModifiers.add(stringElemPostModifier);
		setFeature(InternalFeature.POSTMODIFIERS, postModifiers);
	}

	/**
	 * Retrieves the list of post-modifiers currently associated with this
	 * coordination.
	 * 
	 * @return a <code>List</code> of <code>NLGElement</code>s.
	 */
	public List<NLGElement> getPostModifiers() {
		return getFeatureAsElementList(InternalFeature.POSTMODIFIERS);
	}

	@Override
	public String printTree(String indent) {
		String thisIndent = indent == null ? " |-" : indent + " |-"; //$NON-NLS-1$ //$NON-NLS-2$
		String childIndent = indent == null ? " | " : indent + " | "; //$NON-NLS-1$ //$NON-NLS-2$
		String lastIndent = indent == null ? " \\-" : indent + " \\-"; //$NON-NLS-1$ //$NON-NLS-2$
		String lastChildIndent = indent == null ? "   " : indent + "   "; //$NON-NLS-1$ //$NON-NLS-2$
		StringBuffer print = new StringBuffer();
		// modified by vaudrypl
		// print.append("CoordinatedPhraseElement:\n"); //$NON-NLS-1$
		print.append("CoordinatedPhraseElement: " + this.toString() + "\n"); //$NON-NLS-1$

		List<NLGElement> children = getChildren();
		int length = children.size() - 1;
		int index = 0;

		for (index = 0; index < length; index++) {
			print.append(thisIndent).append(
					children.get(index).printTree(childIndent));
		}
		if (length >= 0) {
			print.append(lastIndent).append(
					children.get(length).printTree(lastChildIndent));
		}
		return print.toString();
	}

	/**
	 * Adds a new complement to the phrase element. Complements will be realised
	 * in the syntax after the coordinates. Complements differ from
	 * post-modifiers in that complements are crucial to the understanding of a
	 * phrase whereas post-modifiers are optional.
	 * 
	 * @param newComplement
	 *            the new complement as an <code>NLGElement</code>.
	 */
	public void addComplement(NLGElement newComplement) {
		List<NLGElement> complements = getFeatureAsElementList(InternalFeature.COMPLEMENTS);
		if (complements == null) {
			complements = new ArrayList<NLGElement>();
		}
		complements.add(newComplement);
		setFeature(InternalFeature.COMPLEMENTS, complements);
		// added by vaudrypl
		newComplement.setParent(this);
	}

	/**
	 * Adds a new complement to the phrase element. Complements will be realised
	 * in the syntax after the coordinates. Complements differ from
	 * post-modifiers in that complements are crucial to the understanding of a
	 * phrase whereas post-modifiers are optional.
	 * 
	 * @param newComplement
	 *            the new complement as a <code>String</code>. It is used to
	 *            create a <code>StringElement</code>.
	 */
	public void addComplement(String newComplement) {
		List<NLGElement> complements = getFeatureAsElementList(InternalFeature.COMPLEMENTS);
		if (complements == null) {
			complements = new ArrayList<NLGElement>();
		}
		// added by vaudrypl
		StringElement stringElemComplement = new StringElement(newComplement);
		stringElemComplement.setParent(this);

		complements.add(stringElemComplement);
		setFeature(InternalFeature.COMPLEMENTS, complements);
	}

	/**
	 * A convenience method for retrieving the last coordinate in this
	 * coordination.
	 * 
	 * @return the last coordinate as represented by a <code>NLGElement</code>
	 */
	public NLGElement getLastCoordinate() {
		List<NLGElement> children = getChildren();
		return children != null && children.size() > 0 ? children.get(children
				.size() - 1) : null;
	}
	
	/** set the conjunction to be used in a coordinatedphraseelement
	 * modified by vaudrypl
	 * @param conjunction
	 */
	public void setConjunction(String conjunction) {
		// for backward compatibility, if a String, create a WordElement
		WordElement word = getLexicon().lookupWord(conjunction,
				LexicalCategory.CONJUNCTION);
		setConjunction(word);
	}
	
	/** set the conjunction to be used in a coordinatedphraseelement
	 * @param conjunction
	 * @author vaudrypl
	 */
	public void setConjunction(WordElement conjunction) {
		setFeature(Feature.CONJUNCTION, conjunction);
	}
	
	/**
	 * @return  conjunction used in coordinatedPhraseElement
	 * modified by vaudrypl
	 */
	public WordElement getConjunction() {
		WordElement conjunction;
		Object conjunctionObject = getFeature(Feature.CONJUNCTION);
		// for backward compatibility, if a String, create a WordElement
		if (conjunctionObject instanceof String) {
			conjunction = getLexicon()
				.lookupWord((String)conjunctionObject, LexicalCategory.CONJUNCTION);
		} else {
			conjunction = (WordElement) conjunctionObject;
		}
		return conjunction;
	}
	
	/**
	 * @return true if this coordinate is plural in a syntactic sense
	 */
	public boolean checkIfPlural() {
		// doing this right is quite complex, take simple approach for now
		int size = getChildren().size();
		if (size == 1)
			return (NumberAgreement.PLURAL.equals(getLastCoordinate().getFeature(Feature.NUMBER)));
		else {
			// changed by vaudrypl to make more generic
//			return PLURAL_COORDINATORS.contains(getConjunction());
			WordElement conjunction = getConjunction();
			boolean pluralConjunction =
				conjunction.getFeature(Feature.NUMBER) == NumberAgreement.PLURAL;
			return pluralConjunction || getAdditionCoordConjunction().equals(conjunction);
		}
	}

	/**
	 * Realisation method for the syntax stage.
	 * based on english SyntaxProcessor
	 * 
	 * @return syntactically realised form
	 * @author vaudrypl
	 */
	public NLGElement realiseSyntax()
	{
		if (getFeatureAsBoolean(Feature.ELIDED).booleanValue()) {
			return null;
		}
		
		return getCoordinatedPhraseHelper().realise(this);
	}
	/**
	 * Realisation method for the morphology stage.
	 * based on english MorphologyProcessor
	 * 
	 * @return morphologically realised form
	 * @author vaudrypl
	 */
	public NLGElement realiseMorphology()
	{
		NLGElement realisedElement = null;
		List<NLGElement> children = getChildren();
		clearCoordinates();

		if (children != null && children.size() > 0) {
			addCoordinate(children.get(0).realiseMorphology());
			for (int index = 1; index < children.size(); index++) {
				addCoordinate(children.get(index).realiseMorphology());
			}
			realisedElement = this;
		}
		return realisedElement;
	}
	
	/**
	 * Realisation method for the orthography stage.
	 * based on english OrthographyProcessor
	 * 
	 * @return orthographically realised form
	 * @author vaudrypl
	 */
	public NLGElement realiseOrthography()
	{
		NLGElement realisedElement =
			getOrthographyHelper().realiseCoordinatedPhrase(getChildren());
		if (realisedElement != null) {
			realisedElement.setCategory(getCategory());
		}
		return realisedElement;
	}
	
	/**
	 * Get the base form of the coordination conjunction used for addition
	 * in this lexicon. (normally "and" in English, "et" in French, etc.)
	 * 
	 * @return	the coordination conjunction used for addition in this lexicon
	 * 
	 * @author vaudrypl
	 */
	public WordElement getAdditionCoordConjunction() {
		return getLexicon().getAdditionCoordConjunction();
	}

	/**
	 * @return 				the number of words in the element
	 * @author vaudrypl
	 */
	public int countWords() {
		int wordCount = 0;
		
		wordCount += countWords(getChildren());
		wordCount += countWords(getComplements());
		wordCount += countWords(getPreModifiers());
		wordCount += countWords(getPostModifiers());
		
		return wordCount;
	}

	/**
	 * Checks if this element must provoke a negation, but with only
	 * the adverb "ne", in French.
	 * 
	 * @return true if the element provokes a negation with only "ne"
	 * 
	 * @author vaudrypl
	 */
	@Override
	public boolean checkIfNeOnlyNegation() {
		boolean returnValue = false;
		
		WordElement conjunction = getConjunction();
		if (conjunction != null) {
			returnValue = conjunction.checkIfNeOnlyNegation();
		}
		
		return returnValue;
	}
}
