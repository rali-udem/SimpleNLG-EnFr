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

package simplenlg.realiser.english;

import java.util.List;

import simplenlg.format.english.TextFormatter;
import simplenlg.framework.DocumentCategory;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGModule;
import simplenlg.lexicon.Lexicon;
import simplenlg.morphology.english.MorphologyProcessor;
import simplenlg.orthography.english.OrthographyProcessor;
import simplenlg.syntax.english.SyntaxProcessor;

/**
 * @author D. Westwater, Data2Text Ltd
 *
 */
public class Realiser extends NLGModule {

	private MorphologyProcessor morphology;
	private OrthographyProcessor orthography;
	private SyntaxProcessor syntax;
	private NLGModule formatter = null;
	private boolean debug = false;
	
	/**
	 * create a realiser (no lexicon)
	 */
	public Realiser() {
		super();
		initialise();
	}
	
	/** Create a realiser with a lexicon (should match lexicon used for NLGFactory)
	 * @param lexicon
	 */
	public Realiser(Lexicon lexicon) {
		this();
		setLexicon(lexicon);
	}

    /**
     * Check whether this processor separates premodifiers using a comma.
     *
     * <br/>
     * <strong>Implementation note:</strong> this method checks whether the
     * {@link simplenlg.orthography.english.OrthographyProcessor} has the
     * parameter set.
     *
     * @return <code>true</code> if premodifiers in the noun phrase are
     *         comma-separated.
     */
    public boolean isCommaSepPremodifiers() {
        return this.orthography == null ? false : this.orthography.isCommaSepPremodifiers();
    }

    /**
     * Set whether to separate premodifiers using a comma. If <code>true</code>,
     * premodifiers will be comma-separated, as in <i>the long, dark road</i>.
     * If <code>false</code>, they won't. <br/>
     * <strong>Implementation note:</strong>: this method sets the relevant
     * parameter in the
     * {@link simplenlg.orthography.english.OrthographyProcessor}.
     *
     * @param commaSepPremodifiers
     *            the commaSepPremodifiers to set
     */
    public void setCommaSepPremodifiers(boolean commaSepPremodifiers) {
        if(this.orthography != null) {
            this.orthography.setCommaSepPremodifiers(commaSepPremodifiers);
        }
    }

	@Override
	public void initialise() {
		this.morphology = new MorphologyProcessor();
		this.morphology.initialise();
		this.orthography = new OrthographyProcessor();
		this.orthography.initialise();
		this.syntax = new SyntaxProcessor();
		this.syntax.initialise();
		this.formatter = new TextFormatter();
		//AG: added call to initialise for formatter
		this.formatter.initialise();
	}

	@Override
	public NLGElement realise(NLGElement element) {
		if (this.debug) {
			System.out.println("INITIAL TREE\n"); //$NON-NLS-1$
			System.out.println(element.printTree(null));
		}
		NLGElement postSyntax = this.syntax.realise(element);
		if (this.debug) {
			System.out.println("\nPOST-SYNTAX TREE\n"); //$NON-NLS-1$
			System.out.println(postSyntax.printTree(null));
		}
		NLGElement postMorphology = this.morphology.realise(postSyntax);
		if (this.debug) {
			System.out.println("\nPOST-MORPHOLOGY TREE\n"); //$NON-NLS-1$
			System.out.println(postMorphology.printTree(null));
		}
		NLGElement postOrthography = this.orthography.realise(postMorphology);
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
			DocumentElement sentence = new DocumentElement(DocumentCategory.SENTENCE, null);
			sentence.addComponent(element);
			realised = realise(sentence);
		}
		
		if (realised == null)
			return null;
		else
			return realised.getRealisation();
	}

	@Override
	public List<NLGElement> realise(List<NLGElement> elements) {
		return null;
	}

	@Override
	public void setLexicon(Lexicon newLexicon) {
		this.syntax.setLexicon(newLexicon);
		this.morphology.setLexicon(newLexicon);
		this.orthography.setLexicon(newLexicon);
	}

	public void setFormatter(NLGModule formatter) {
		this.formatter = formatter;
	}
	
	public void setDebugMode(boolean debugOn) {
		this.debug = debugOn;
	}
}
