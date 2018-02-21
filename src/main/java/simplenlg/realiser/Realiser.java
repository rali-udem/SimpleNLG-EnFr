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

package simplenlg.realiser;

import simplenlg.framework.DocumentCategory;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGModule;
import simplenlg.format.english.TextFormatter;

/**
 * This is a modified copy of the English Realiser class.
 * It uses the NLGElement.realiseSyntax(), realiseMorphology()
 * realiseMorphophonology() and realiseOrthography()
 * methods instead of syntax, morphology and orthography processors,
 * thus being language independant and needing no lexicon.
 * It uses the same text formatter.
 * 
 * @author vaudrypl
 *
 */
public class Realiser {

	private NLGModule formatter = null;
	private boolean debug = false;
	
	/**
	 * create a realiser (no lexicon)
	 */
	public Realiser() {
		initialise();
	}
	
	public void initialise() {
		this.formatter = new TextFormatter();
		//AG: added call to initialise for formatter
		this.formatter.initialise();
	}

	public NLGElement realise(NLGElement element) {
		if (this.debug) {
			System.out.println("INITIAL TREE\n"); //$NON-NLS-1$
			System.out.println(element.printTree(null));
		}
		NLGElement postSyntax = element.realiseSyntax();
		if (this.debug) {
			System.out.println("\nPOST-SYNTAX TREE\n"); //$NON-NLS-1$
			System.out.println(postSyntax.printTree(null));
		}
		NLGElement postMorphology = postSyntax!=null ? postSyntax.realiseMorphology() : null;
		if (this.debug) {
			System.out.println("\nPOST-MORPHOLOGY TREE\n"); //$NON-NLS-1$
			System.out.println(postMorphology.printTree(null));
		}
		NLGElement postMorphophonology = postMorphology!=null ? postMorphology.realiseMorphophonology() : null;
		if (this.debug) {
			System.out.println("\nPOST-MORPHOPHONOLOGY TREE\n"); //$NON-NLS-1$
			System.out.println(postMorphophonology.printTree(null));
		}
		NLGElement postOrthography = postMorphophonology!=null ? postMorphophonology.realiseOrthography() : null;
		if (this.debug) {
			System.out.println("\nPOST-ORTHOGRAPHY TREE\n"); //$NON-NLS-1$
			System.out.println(postOrthography.printTree(null));
		}
		NLGElement postFormatter = null;
		if (this.formatter != null) {
			postFormatter = this.formatter.realise(postOrthography);
			if (this.debug) {
				System.out.println("\nPOST-FORMATTER TREE\n"); //$NON-NLS-1$
				System.out.println(postFormatter.printTree(null));
			}
		} else {
			postFormatter = postOrthography;
		}
		return postFormatter;
	}
	
	/** Convenience class to realise any NLGElement as a sentence
	 * @param element
	 * @return String realisation of the NLGElement
	 */
	public String realiseSentence(NLGElement element) {
		NLGElement realised = null;
		if (element instanceof DocumentElement)
			realised = realise(element);
		else {
			DocumentElement sentence
				= new DocumentElement(DocumentCategory.SENTENCE, null, element.getFactory());
			sentence.addComponent(element);
			realised = realise(sentence);
		}
		
		if (realised == null)
			return null;
		else
			return realised.getRealisation();
	}

	public void setFormatter(NLGModule formatter) {
		this.formatter = formatter;
	}
	
	public void setDebugMode(boolean debugOn) {
		this.debug = debugOn;
	}
}
