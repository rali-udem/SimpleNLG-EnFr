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

import simplenlg.features.DiscourseFunction;
import simplenlg.features.french.FrenchInternalFeature;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;

/**
 * This class contains methods to help the syntaxical realisation
 * of phrases for French.
 * It is the same as the generic phrase helper.
 * 
 * @author vaudrypl
 */
public class PhraseHelper extends simplenlg.syntax.GenericPhraseHelper
{

	/**
	 * The main method for realising phrases. In French, checks that the
	 * phrase is not relativised before realising it.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> to be realised.
	 * @return the realised <code>NLGElement</code>.
	 */
	@Override
	public NLGElement realise(PhraseElement phrase) {
		NLGElement realisedElement = null;

		if (phrase != null) {
			if (!phrase.getFeatureAsBoolean(FrenchInternalFeature.RELATIVISED)) {
				realisedElement = super.realise(phrase);
			} else {
				phrase.removeFeature(FrenchInternalFeature.RELATIVISED);
			}
		}
		return realisedElement;
	}

}
