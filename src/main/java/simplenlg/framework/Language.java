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

package simplenlg.framework;

/**
 * Enum type listing language constants available along
 * with their ISO 639-1 two letter code. It also has methods
 * to compare Strings with these constants.
 * 
 * @author vaudrypl
 *
 */
public enum Language {
	
	ENGLISH("en"), FRENCH("fr"); 

	final public String code;

	final public static Language DEFAULT_LANGUAGE = ENGLISH;
	
	Language(String code)
	{
		this.code = code;
	}
	
	/**
	 * @param code
	 * @return true if this language has this code
	 */
	public boolean isCode(String code) {
		return this.code.equals( code.toLowerCase() );
	}
	
	/**
	 * @param code
	 * @return the language constant corresponding to this code
	 *         null if no constant matches this code
	 *         
	 * Note : You can use the DEFAULT_LANGUAGE constant if this
	 * method returns null.
	 */
	public static Language convertCodeToLanguage(String code) {
		Language returnValue = null;
		for (Language language : Language.values()) {
			if (language.isCode(code)) {
				returnValue = language;
				break;
			}
		}
		return returnValue;
	}
}
