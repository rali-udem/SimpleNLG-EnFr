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

import junit.framework.Assert;

import org.junit.Test;

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.SPhraseSpec;

/**
 * test suite for simple XXXPhraseSpec classes
 * @author ereiter
 * 
 * translated and adapted by vaudrypl
 */

public class PhraseSpecTest extends SimpleNLG4TestBase {

	public PhraseSpecTest(String name) {
		super(name);
	}

	/**
	 * Test SPhraseSpec
	 */
	@Test
	public void testSPhraseSpec() {
		
		// simple test of methods
		SPhraseSpec c1 = (SPhraseSpec) factory.createClause();
		c1.setVerb("donner");
		c1.setSubject("Jean");
		c1.setObject("une pomme");
		c1.setIndirectObject("Marie");
		c1.setFeature(Feature.TENSE, Tense.PAST);
		c1.setFeature(Feature.NEGATED, true);
		
		// check getXXX methods
		Assert.assertEquals("donner",  getBaseForm(c1.getVerb()));
		Assert.assertEquals("Jean", getBaseForm(c1.getSubject()));
		Assert.assertEquals("une pomme", getBaseForm(c1.getObject()));
		Assert.assertEquals("Marie", getBaseForm(c1.getIndirectObject()));
		
//		Assert.assertEquals("John did not give Mary an apple", this.realiser //$NON-NLS-1$
		Assert.assertEquals("Jean n'a pas donné une pomme à Marie", this.realiser //$NON-NLS-1$
				.realise(c1).getRealisation());
		

		
		// test modifier placement
		SPhraseSpec c2 = (SPhraseSpec) factory.createClause();
		c2.setVerb("voir");
		c2.setSubject("l'homme");
		c2.setObject("moi");
		c2.addFrontModifier("heureusement");
		c2.addPreModifier("rapidement");
		c2.addModifier("dans le parc");
		// try setting tense directly as a feature
		c2.setFeature(Feature.TENSE, Tense.PAST);
//		Assert.assertEquals("fortunately the man quickly saw me in the park", this.realiser //$NON-NLS-1$
		Assert.assertEquals("heureusement, l'homme m'a rapidement vu dans le parc", this.realiser //$NON-NLS-1$
				.realise(c2).getRealisation());
	}

	// get string for head of constituent
	private String getBaseForm(NLGElement constituent) {
		if (constituent == null)
			return null;
		else if (constituent instanceof StringElement)
			return constituent.getRealisation();
		else if (constituent instanceof WordElement)
			return ((WordElement)constituent).getBaseForm();
		else if (constituent instanceof InflectedWordElement)
			return getBaseForm(((InflectedWordElement)constituent).getBaseWord());
		else if (constituent instanceof PhraseElement)
			return getBaseForm(((PhraseElement)constituent).getHead());
		else
			return null;
	}
}
