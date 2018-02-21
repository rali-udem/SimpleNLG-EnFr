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

package simplenlg.examples;

import simplenlg.framework.*;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.english.XMLLexicon;
import simplenlg.realiser.*;
import simplenlg.phrasespec.*;

/**
 * SimpleNLG-EnFr example in English.
 * Prints "The man greets the crowd."
 * 
 * @author vaudrypl
 *
 */
public class GreetCrowdEn {

	public static void main(String[] args) {

        Lexicon lexicon = new XMLLexicon();
        NLGFactory factory = new NLGFactory(lexicon);
        Realiser realiser = new Realiser();
        
        NPPhraseSpec theMan = factory.createNounPhrase("the", "man");
        NPPhraseSpec theCrowd = factory.createNounPhrase("the", "crowd");
        
        SPhraseSpec greeting = factory.createClause(theMan, "greet", theCrowd);
        
        String outString = realiser.realiseSentence(greeting);
        System.out.println(outString);
	}

}
