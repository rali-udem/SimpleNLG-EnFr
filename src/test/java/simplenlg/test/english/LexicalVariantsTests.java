package simplenlg.test.english;


import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import simplenlg.features.Feature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.NIHDBLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.realiser.english.Realiser;

/**
 * Tests on the use of spelling and inflectional variants, using the NIHDBLexicon.
 * @author bertugatt
 *
 */
public class LexicalVariantsTests extends TestCase {

	// lexicon object -- an instance of Lexicon
	NIHDBLexicon lexicon = null;

	//factory for phrases
	NLGFactory factory;
	
	//realiser
	Realiser realiser;
	
	// DB location -- change this to point to the lex access data dir
	static String DB_FILENAME = "res/NIHLexicon/lexAccess2013.data";

	@Override
	@Before
	/*
	 * * Sets up the accessor and runs it -- takes ca. 26 sec
	 */
	public void setUp() {
		this.lexicon = new NIHDBLexicon(DB_FILENAME);
		this.factory = new NLGFactory(lexicon);
		this.realiser = new Realiser(this.lexicon);
	}

	/**
	 * Close the lexicon
	 */
	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		if (lexicon != null)
			lexicon.close();
	}
	
	/**
	 * check that spelling variants are properly set
	 */
	@Test
	public void testSpellingVariants() {
		WordElement asd = lexicon.getWord("Adams-Stokes disease");
		List<String> spellVars = asd
				.getFeatureAsStringList(LexicalFeature.SPELL_VARS);
		Assert.assertTrue(spellVars.contains("Adams Stokes disease"));
		Assert.assertTrue(spellVars.contains("Adam-Stokes disease"));
		Assert.assertEquals(2, spellVars.size());
		Assert.assertEquals(asd.getBaseForm(), asd
				.getFeatureAsString(LexicalFeature.DEFAULT_SPELL));

		//default spell variant is baseform
		Assert.assertEquals("Adams-Stokes disease", asd
				.getDefaultSpellingVariant());		
		
		//default spell variant changes
		asd.setDefaultSpellingVariant("Adams Stokes disease");
		Assert.assertEquals("Adams Stokes disease", asd
				.getDefaultSpellingVariant());
	}
	
	/**
	 * 
	 */
	public void testSpellingVariantWithInflection() {
		realiser.setDebugMode(true);
		WordElement word = lexicon.getWord("formalization");
		List<String> spellVars = word.getSpellingVariants();
		Assert.assertTrue(spellVars.contains("formalisation"));
		Assert.assertEquals("reg", word.getDefaultInflectionalVariant());
		//hydro.setDefaultSpellingVariant("hydroxy-benzonitrile");
		NPPhraseSpec np = factory.createNounPhrase("the", "formalization");
		//NPPhraseSpec np = factory.createNounPhrase(lexicon.getWord("the"), lexicon.getWord("formalization"));
		np.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		System.out.println(realiser.realise(np));
		
	}

}
