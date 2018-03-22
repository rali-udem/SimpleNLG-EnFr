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
package simplenlg.syntax.english.nonstatic;

import java.util.List;

import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.syntax.GenericPhraseHelper;

/**
 * <p>
 * This class contains methods to help the syntax processing realise
 * phrases for English. It is a non static version by vaudrypl of the class of
 * the same name in the <code>simplenlg.syntax.english</code> package.
 * </p>
 * modified by vaudrypl :
 * abstract class replaced by public class
 * private static methods replaced by protected methods
 * parent.realise(element) replaced by element.realiseSyntax()
 * SyntaxProcessor parent arguments removed
 * now extends GenericPhraseHelper
 * 
 * most methods moved to simplenlg.syntax.GenericPhraseHelper
 * 
 * @author E. Reiter and D. Westwater, University of Aberdeen.
 * @version 4.0
 */
public class PhraseHelper extends GenericPhraseHelper {

	/**
	 * Determines if the given phrase has an expletive as a subject.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> to be examined.
	 * @return <code>true</code> if the phrase has an expletive subject.
	 */
	@Override
	public boolean isExpletiveSubject(PhraseElement phrase) {
		List<NLGElement> subjects = phrase
				.getFeatureAsElementList(InternalFeature.SUBJECTS);
		boolean expletive = false;

		if (subjects.size() == 1) {
			NLGElement subjectNP = subjects.get(0);

			if (subjectNP.isA(PhraseCategory.NOUN_PHRASE)) {
				expletive = subjectNP.getFeatureAsBoolean(
						LexicalFeature.EXPLETIVE_SUBJECT).booleanValue();
			} else if (subjectNP.isA(PhraseCategory.CANNED_TEXT)) {
				expletive = "there".equalsIgnoreCase(subjectNP.getRealisation()); //$NON-NLS-1$
			}
		}
		return expletive;
	}
}
