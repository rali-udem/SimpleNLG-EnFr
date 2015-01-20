package simplenlg.test.english;

import junit.framework.Assert;

import org.junit.Test;

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * Some tests for coordination, especially of coordinated VPs with modifiers.
 * 
 * @author Albert Gatt
 * 
 */
public class CoordinationTest extends SimpleNLG4TestBase {

	public CoordinationTest(String name) {
		super(name);
	}

	/**
	 * Test pre and post-modification of coordinate VPs inside a sentence.
	 */
	@Test
	public void testModifiedCoordVP() {
		CoordinatedPhraseElement coord = this.phraseFactory
				.createCoordinatedPhrase(this.getUp, this.fallDown);
		coord.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals("got up and fell down", this.realiser
				.realise(coord).getRealisation());

		// add a premodifier
		coord.addPreModifier("slowly");
		Assert.assertEquals("slowly got up and fell down", this.realiser
				.realise(coord).getRealisation());

		// adda postmodifier
		coord.addPostModifier(this.behindTheCurtain);
		Assert.assertEquals("slowly got up and fell down behind the curtain",
				this.realiser.realise(coord).getRealisation());

		// put within the context of a sentence
		SPhraseSpec s = this.phraseFactory.createClause("Jake", coord);
		s.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals(
				"Jake slowly got up and fell down behind the curtain",
				this.realiser.realise(s).getRealisation());

		// add premod to the sentence
		s.addPreModifier(this.lexicon
				.getWord("however", LexicalCategory.ADVERB));
		Assert.assertEquals(
				"Jake however slowly got up and fell down behind the curtain",
				this.realiser.realise(s).getRealisation());

		// add postmod to the sentence
		s.addPostModifier(this.inTheRoom);
		Assert
				.assertEquals(
						"Jake however slowly got up and fell down behind the curtain in the room",
						this.realiser.realise(s).getRealisation());
	}

	/**
	 * Test due to Chris Howell -- create a complex sentence with front modifier
	 * and coordinateVP. This is a version in which we create the coordinate
	 * phrase directly.
	 */
	@Test
	public void testCoordinateVPComplexSubject() {
		// "As a result of the procedure the patient had an adverse contrast media reaction and went into cardiogenic shock."
		SPhraseSpec s = this.phraseFactory.createClause();
		
		s.setSubject(this.phraseFactory.createNounPhrase("the", "patient"));

		// first VP
		VPPhraseSpec vp1 = this.phraseFactory.createVerbPhrase(this.lexicon
				.getWord("have", LexicalCategory.VERB));
		NPPhraseSpec np1 = this.phraseFactory.createNounPhrase("a",
				this.lexicon.getWord("contrast media reaction",
						LexicalCategory.NOUN));
		np1.addPreModifier(this.lexicon.getWord("adverse",
				LexicalCategory.ADJECTIVE));
		vp1.addComplement(np1);

		// second VP
		VPPhraseSpec vp2 = this.phraseFactory.createVerbPhrase(this.lexicon
				.getWord("go", LexicalCategory.VERB));
		PPPhraseSpec pp = this.phraseFactory.createPrepositionPhrase("into", this.lexicon.getWord("cardiogenic shock", LexicalCategory.NOUN));
		vp2.addComplement(pp);
		
		//coordinate
		CoordinatedPhraseElement coord = this.phraseFactory.createCoordinatedPhrase(vp1, vp2);
		coord.setFeature(Feature.TENSE, Tense.PAST);		
		Assert.assertEquals("had an adverse contrast media reaction and went into cardiogenic shock", this.realiser.realise(coord).getRealisation());
		
		//now put this in the sentence
		s.setVerbPhrase(coord);
		s.addFrontModifier("As a result of the procedure");
		Assert.assertEquals("As a result of the procedure the patient had an adverse contrast media reaction and went into cardiogenic shock", this.realiser.realise(s).getRealisation());
		
	}
}
