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
import simplenlg.framework.WordElement;
import simplenlg.syntax.AbstractCoordinatedPhraseHelper;

/**
 * <p>
 * This class contains methods to help the syntax processor realise
 * coordinated phrases for English. It is a non static version by vaudrypl
 * of the class of the same name in the <code>simplenlg.syntax.english</code> package.
 * </p>
 * modified by vaudrypl :
 * abstract class replaced by public class
 * private static methods replaced by protected methods
 * parent.realise(element) replaced by element.realiseSyntax()
 * SyntaxProcessor parent arguments removed
 * PhraseHelper replaced by phrase.getPhraseHelper()
 * now extends AbstractCoordinatedPhraseHelper
 * 
 * most methods moved to simplenlg.syntax.AbstractCoordinatedPhraseHelper
 * 
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 */
public class CoordinatedPhraseHelper extends AbstractCoordinatedPhraseHelper {

	/**
	 * Checks to see if the specifier can be raised and then raises it. In order
	 * to be raised the specifier must be the same on all coordinates. For
	 * example, <em>the cat and the dog</em> will be realised as
	 * <em>the cat and dog</em> while <em>the cat and any dog</em> will remain
	 * <em>the cat and any dog</em>.
	 * 
	 * @param children
	 *            the <code>List</code> of coordinates in the
	 *            <code>CoordinatedPhraseElement</code>
	 */
	@Override
	protected void raiseSpecifier(List<NLGElement> children) {
		boolean allMatch = true;
		NLGElement child = children.get(0);
		NLGElement specifier = null;
		String test = null;

		if (child != null) {
			specifier = child.getFeatureAsElement(InternalFeature.SPECIFIER);

			if (specifier != null) {
				// AG: this assumes the specifier is an InflectedWordElement or
				// phrase.
				// it could be a Wordelement, in which case, we want the
				// baseform
				test = (specifier instanceof WordElement) ? ((WordElement) specifier)
						.getBaseForm()
						: specifier
								.getFeatureAsString(LexicalFeature.BASE_FORM);
			}

			if (test != null) {
				int index = 1;

				while (index < children.size() && allMatch) {
					child = children.get(index);

					if (child == null) {
						allMatch = false;

					} else {
						specifier = child
								.getFeatureAsElement(InternalFeature.SPECIFIER);
						String childForm = (specifier instanceof WordElement) ? ((WordElement) specifier)
								.getBaseForm()
								: specifier
										.getFeatureAsString(LexicalFeature.BASE_FORM);

						if (!test.equals(childForm)) {
							allMatch = false;
						}
					}
					index++;
				}
				if (allMatch) {
					for (int eachChild = 1; eachChild < children.size(); eachChild++) {
						child = children.get(eachChild);
						child.setFeature(InternalFeature.RAISED, true);
					}
				}
			}
		}
	}
}
