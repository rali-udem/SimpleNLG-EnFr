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

package simplenlg.features.french;

/**
 * <p>
 * An enumeration representing the types of pronoun. The pronoun type is recorded
 * in the {@code FrenchLexicalFeature.PRONOUN_TYPE} feature and applies to pronouns.
 * </p>
 * 
 * @author vaudrypl
 * 
 */

public enum PronounType {

	/**
	 * Personal pronoun. For example, <em>il</em>.
	 */
	PERSONAL,

	/**
	 * Special personal pronoun. For example, <em>y</em>.
	 * These personal pronouns have special behaviors.
	 */
	SPECIAL_PERSONAL,

	/**
	 * Numeral pronoun. For example, <em>un</em>.
	 */
	NUMERAL,

	/**
	 * Possessive pronoun. For example, <em>mien</em>.
	 */
	POSSESSIVE,
	
	/**
	 * Demonstrative pronoun. For example, <em>ceci</em>.
	 */
	DEMONSTRATIVE,
	
	/**
	 * Relative pronoun. For example, <em>qui</em>.
	 */
	RELATIVE,
	
	/**
	 * Interrogative pronoun. For example, <em>qui</em>.
	 */
	INTERROGATIVE,
	
	/**
	 * Indefinite pronoun. For example, <em>tout</em>.
	 */
	INDEFINITE;
	
}
