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
package simplenlg.test.english;

import junit.framework.TestCase;

import org.junit.Before;

import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.Realiser;

/**
 * This class is the base class for all JUnit simplenlg.test cases for
 * simplenlg. It sets up a a JUnit fixture, i.e. the basic objects (basic
 * constituents) that all other tests can use.
 * @author agatt
 */
public abstract class SimpleNLG4TestBase extends TestCase {

	/** The realiser. */
	static Lexicon lexicon = new simplenlg.lexicon.english.XMLLexicon();
	
	static NLGFactory phraseFactory;
	
	static Realiser realiser = new Realiser();

	/** The pro test2. */
	static PhraseElement man, woman, dog, boy, np4, np5, np6, proTest1, proTest2;

	/** The salacious. */
	static PhraseElement beautiful, stunning, salacious;

	/** The under the table. */
	static PhraseElement onTheRock, behindTheCurtain, inTheRoom, underTheTable;

	/** The say. */
	static VPPhraseSpec kick, kiss, walk, talk, getUp, fallDown, give, say;

	/**
	 * Instantiates a new simplenlg test.
	 * 
	 * @param name
	 *            the name
	 */
	public SimpleNLG4TestBase(String name) {
		super(name);
	}

	/**
	 * Set up the variables we'll need for this simplenlg.test to run (Called
	 * automatically by JUnit)
	 */
	@Override
	@Before
	protected void setUp() {
		//lexicon = new NIHDBLexicon("E:\\NIHDB\\lexAccess2009"); // NIH lexicon
		//lexicon = new XMLLexicon("E:\\NIHDB\\default-lexicon.xml");    // default XML lexicon
		phraseFactory = new NLGFactory(lexicon);
		
		man = phraseFactory.createNounPhrase("the", "man"); //$NON-NLS-1$ //$NON-NLS-2$
		woman = phraseFactory.createNounPhrase("the", "woman");  //$NON-NLS-1$//$NON-NLS-2$
		dog = phraseFactory.createNounPhrase("the", "dog"); //$NON-NLS-1$ //$NON-NLS-2$
		boy = phraseFactory.createNounPhrase("the", "boy"); //$NON-NLS-1$ //$NON-NLS-2$

		beautiful = phraseFactory.createAdjectivePhrase("beautiful"); //$NON-NLS-1$
		stunning = phraseFactory.createAdjectivePhrase("stunning"); //$NON-NLS-1$
		salacious = phraseFactory.createAdjectivePhrase("salacious"); //$NON-NLS-1$

		onTheRock = phraseFactory.createPrepositionPhrase("on"); //$NON-NLS-1$
		np4 = phraseFactory.createNounPhrase("the", "rock"); //$NON-NLS-1$ //$NON-NLS-2$
		onTheRock.addComplement(np4);

		behindTheCurtain = phraseFactory.createPrepositionPhrase("behind"); //$NON-NLS-1$
		np5 = phraseFactory.createNounPhrase("the", "curtain"); //$NON-NLS-1$ //$NON-NLS-2$
		behindTheCurtain.addComplement(np5);

		inTheRoom = phraseFactory.createPrepositionPhrase("in"); //$NON-NLS-1$
		np6 = phraseFactory.createNounPhrase("the", "room"); //$NON-NLS-1$ //$NON-NLS-2$
		inTheRoom.addComplement(np6);

		underTheTable = phraseFactory.createPrepositionPhrase("under"); //$NON-NLS-1$
		underTheTable.addComplement(phraseFactory.createNounPhrase("the", "table")); //$NON-NLS-1$ //$NON-NLS-2$

		proTest1 = phraseFactory.createNounPhrase("the", "singer"); //$NON-NLS-1$ //$NON-NLS-2$
		proTest2 = phraseFactory.createNounPhrase("some", "person"); //$NON-NLS-1$ //$NON-NLS-2$

		kick = phraseFactory.createVerbPhrase("kick"); //$NON-NLS-1$
		kiss = phraseFactory.createVerbPhrase("kiss"); //$NON-NLS-1$
		walk = phraseFactory.createVerbPhrase("walk"); //$NON-NLS-1$
		talk = phraseFactory.createVerbPhrase("talk"); //$NON-NLS-1$
		getUp = phraseFactory.createVerbPhrase("get up"); //$NON-NLS-1$
		fallDown = phraseFactory.createVerbPhrase("fall down"); //$NON-NLS-1$
		give = phraseFactory.createVerbPhrase("give"); //$NON-NLS-1$
		say = phraseFactory.createVerbPhrase("say"); //$NON-NLS-1$
	}
}
