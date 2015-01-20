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
package simplenlg.orthography;

import java.util.List;

import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.StringElement;

/**
 * Interface for the orthography helpers.
 * Theses methods put commas, periods, capitals, etc.
 * in the realised text. 
 * 
 * Most of the code taken from OrthographyProcessor by D. Westwater, University of Aberdeen.
 * 
 * @author vaudrypl
 * 
 */
public interface OrthographyHelperInterface {

	/**
	 * Realise the orthograph of a ListElement.
	 * 
	 * @param element ListElement to be realised
	 * @return orthographically realised form
	 */
	public StringElement realiseListElement(ListElement element);

	/**
	 * Performs the realisation on a sentence. This includes adding the
	 * terminator and capitalising the first letter.
	 * 
	 * @param components
	 *            the <code>List</code> of <code>NLGElement</code>s representing
	 *            the components that make up the sentence.
	 * @param element
	 *            the <code>NLGElement</code> representing the sentence.
	 * @return the realised element as an <code>NLGElement</code>.
	 */
	public NLGElement realiseSentence(List<NLGElement> components,
			NLGElement element);

	/**
	 * Realises coordinated phrases. Where there are more than two coordinates,
	 * then a comma replaces the conjunction word between all the coordinates
	 * save the last two. For example, <em>John and Peter and Simon</em> becomes
	 * <em>John, Peter and Simon</em>.
	 * 
	 * @param components
	 *            the <code>List</code> of <code>NLGElement</code>s representing
	 *            the components that make up the sentence.
	 * @return the realised element as an <code>NLGElement</code>.
	 */
	public NLGElement realiseCoordinatedPhrase(List<NLGElement> components);
}
