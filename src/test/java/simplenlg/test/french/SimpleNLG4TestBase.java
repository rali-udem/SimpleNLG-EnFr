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
package simplenlg.test.french;

import junit.framework.TestCase;

import org.junit.Before;

import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.features.french.FrenchLexicalFeature;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.Realiser;
import simplenlg.phrasespec.NPPhraseSpec;;

/**
 * This class is the base class for all JUnit simplenlg.test cases for
 * simplenlg. It sets up a a JUnit fixture, i.e. the basic objects (basic
 * constituents) that all other tests can use.
 * @author agatt
 * translated and adapted by vaudrypl
 */
public abstract class SimpleNLG4TestBase extends TestCase {

	/** The realiser. */
	static Lexicon lexicon = new simplenlg.lexicon.french.XMLLexicon();
	
	static NLGFactory factory;
	
	static Realiser realiser = new Realiser();

	/** The pro test2. */
	static NPPhraseSpec homme, femme, chien, garcon, np4, np5, np6, proTest1, proTest2;

	/** The salacious. */
	static PhraseElement beau, stupefiant, salace;

	/** The under the table. */
	static PhraseElement surLeRocher, derriereLeRideau, dansLaPiece, sousLaTable;

	/** The say. */
	static VPPhraseSpec frapper, embrasser, marcher, parler, seLever, tomber, donner, dire;

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
		factory = new NLGFactory(lexicon);
		
		homme = factory.createNounPhrase("le", "homme"); //$NON-NLS-1$ //$NON-NLS-2$
		femme = factory.createNounPhrase("le", "femme");  //$NON-NLS-1$//$NON-NLS-2$
		chien = factory.createNounPhrase("le", "chien"); //$NON-NLS-1$ //$NON-NLS-2$
		garcon = factory.createNounPhrase("le", "garçon"); //$NON-NLS-1$ //$NON-NLS-2$

		beau = factory.createAdjectivePhrase("beau"); //$NON-NLS-1$
		stupefiant = factory.createAdjectivePhrase("stupéfiant"); //$NON-NLS-1$
		salace = factory.createAdjectivePhrase("salace"); //$NON-NLS-1$

		surLeRocher = factory.createPrepositionPhrase("sur"); //$NON-NLS-1$
		np4 = factory.createNounPhrase("le", "rocher"); //$NON-NLS-1$ //$NON-NLS-2$
		surLeRocher.addComplement(np4);

		derriereLeRideau = factory.createPrepositionPhrase("derrière"); //$NON-NLS-1$
		np5 = factory.createNounPhrase("le", "rideau"); //$NON-NLS-1$ //$NON-NLS-2$
		derriereLeRideau.addComplement(np5);

		dansLaPiece = factory.createPrepositionPhrase("dans"); //$NON-NLS-1$
		np6 = factory.createNounPhrase("le", "pièce"); //$NON-NLS-1$ //$NON-NLS-2$
		dansLaPiece.addComplement(np6);

		sousLaTable = factory.createPrepositionPhrase("sous"); //$NON-NLS-1$
		sousLaTable.addComplement(factory.createNounPhrase("le", "table")); //$NON-NLS-1$ //$NON-NLS-2$

		proTest1 = factory.createNounPhrase("le", "chanteur"); //$NON-NLS-1$ //$NON-NLS-2$
		proTest2 = factory.createNounPhrase("un", "personne"); //$NON-NLS-1$ //$NON-NLS-2$

		frapper = factory.createVerbPhrase("frapper"); //$NON-NLS-1$
		embrasser = factory.createVerbPhrase("embrasser"); //$NON-NLS-1$
		marcher = factory.createVerbPhrase("marcher"); //$NON-NLS-1$
		parler = factory.createVerbPhrase("parler"); //$NON-NLS-1$
		seLever = factory.createVerbPhrase("lever"); //$NON-NLS-1$
		seLever.setObject("se");
		seLever.getVerb().setFeature(FrenchLexicalFeature.AUXILIARY_ETRE, true);
		tomber = factory.createVerbPhrase("tomber"); //$NON-NLS-1$
		donner = factory.createVerbPhrase("donner"); //$NON-NLS-1$
		dire = factory.createVerbPhrase("dire"); //$NON-NLS-1$
	}
	
	protected String realise(NLGElement element) {
		return realiser.realise(element).getRealisation();
	}
}
