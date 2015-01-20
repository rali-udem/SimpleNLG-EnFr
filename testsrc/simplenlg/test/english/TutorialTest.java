/*
 * 
 * Copyright (C) 2010, University of Aberdeen
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package simplenlg.test.english;

import junit.framework.Assert;

import org.junit.Test;

import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

/**
 * Tests from tutorial
 * <hr>
 * 
 * <p>
 * Copyright (C) 2011, University of Aberdeen
 * </p>
 * 
 * <p>
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * </p>
 * 
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * </p>
 * 
 * <p>
 * You should have received a copy of the GNU Lesser General Public License in the zip
 * file. If not, see <a
 * href="http://www.gnu.org/licenses/">www.gnu.org/licenses</a>.
 * </p>
 * 
 * <p>
 * For more details on SimpleNLG visit the project website at <a
 * href="http://www.csd.abdn.ac.uk/research/simplenlg/"
 * >www.csd.abdn.ac.uk/research/simplenlg</a> or email Dr Ehud Reiter at
 * e.reiter@abdn.ac.uk
 * </p>
 * 
 * @author ereiter
 * 
 */
public class TutorialTest extends SimpleNLG4TestBase {

	public TutorialTest(String name) {
		super(name);
	}


	// no code in sections 1 and 2
	
	/**
	 * test section 3 code
	 */
	@Test
	public void testSection3() {
		Lexicon lexicon = Lexicon.getDefaultLexicon();                         // default simplenlg lexicon
		NLGFactory nlgFactory = new NLGFactory(lexicon);             // factory based on lexicon

		NLGElement s1 = nlgFactory.createSentence("my dog is happy");
		
		Realiser r = new Realiser();
		
		String output = r.realiseSentence(s1);
		
		Assert.assertEquals("My dog is happy.", output);
	 }
	
	/**
	 * test section 5 code
	 */
	@Test
	public void testSection5() {
		Lexicon lexicon = Lexicon.getDefaultLexicon();                         // default simplenlg lexicon
		NLGFactory nlgFactory = new NLGFactory(lexicon);             // factory based on lexicon
		Realiser realiser = new Realiser();
		
		SPhraseSpec p = nlgFactory.createClause();
		p.setSubject("my dog");
		p.setVerb("chase");
		p.setObject("George");
		
		String output = realiser.realiseSentence(p);
		Assert.assertEquals("My dog chases George.", output);
	 }
	
	/**
	 * test section 6 code
	 */
	@Test
	public void testSection6() {
		Lexicon lexicon = Lexicon.getDefaultLexicon();                         // default simplenlg lexicon
		NLGFactory nlgFactory = new NLGFactory(lexicon);             // factory based on lexicon
		Realiser realiser = new Realiser();
		
		SPhraseSpec p = nlgFactory.createClause();
		p.setSubject("Mary");
		p.setVerb("chase");
		p.setObject("George");
		
		p.setFeature(Feature.TENSE, Tense.PAST); 
		String output = realiser.realiseSentence(p);
		Assert.assertEquals("Mary chased George.", output);

		p.setFeature(Feature.TENSE, Tense.FUTURE); 
		output = realiser.realiseSentence(p);
		Assert.assertEquals("Mary will chase George.", output);

		p.setFeature(Feature.NEGATED, true); 
		output = realiser.realiseSentence(p);
		Assert.assertEquals("Mary will not chase George.", output);

		p = nlgFactory.createClause();
		p.setSubject("Mary");
		p.setVerb("chase");
		p.setObject("George");
 
		p.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.YES_NO);
		output = realiser.realiseSentence(p);
		Assert.assertEquals("Does Mary chase George?", output);

		p.setSubject("Mary");
		p.setVerb("chase");
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT);
		output = realiser.realiseSentence(p);
		Assert.assertEquals("Who does Mary chase?", output);

		p = nlgFactory.createClause();
		p.setSubject("the dog");
		p.setVerb("wake up");
		output = realiser.realiseSentence(p);
		Assert.assertEquals("The dog wakes up.", output);

	 }
	
	/**
	 * test ability to use variant words
	 */
	@Test
	public void testVariants() {
		Lexicon lexicon = Lexicon.getDefaultLexicon();                         // default simplenlg lexicon
		NLGFactory nlgFactory = new NLGFactory(lexicon);             // factory based on lexicon
		Realiser realiser = new Realiser();
		
		SPhraseSpec p = nlgFactory.createClause();
		p.setSubject("my dog");
		p.setVerb("is");  // variant of be
		p.setObject("George");
		
		String output = realiser.realiseSentence(p);
		Assert.assertEquals("My dog is George.", output);
		
		p = nlgFactory.createClause();
		p.setSubject("my dog");
		p.setVerb("chases");  // variant of chase
		p.setObject("George");
		
		output = realiser.realiseSentence(p);
		Assert.assertEquals("My dog chases George.", output);
		

        p = nlgFactory.createClause();
		p.setSubject(nlgFactory.createNounPhrase("the", "dogs"));   // variant of "dog"
		p.setVerb("is");  // variant of be
		p.setObject("happy");  // variant of happy
		output = realiser.realiseSentence(p);
		Assert.assertEquals("The dog is happy.", output);
		
		p = nlgFactory.createClause();
		p.setSubject(nlgFactory.createNounPhrase("the", "children"));   // variant of "child"
		p.setVerb("is");  // variant of be
		p.setObject("happy");  // variant of happy
		output = realiser.realiseSentence(p);
		Assert.assertEquals("The child is happy.", output);

		// following functionality is enabled
		p = nlgFactory.createClause();
		p.setSubject(nlgFactory.createNounPhrase("the", "dogs"));   // variant of "dog"
		p.setVerb("is");  // variant of be
		p.setObject("happy");  // variant of happy
		output = realiser.realiseSentence(p);
		Assert.assertEquals("The dog is happy.", output); //corrected automatically				
	 }
}
