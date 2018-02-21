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

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import simplenlg.format.english.TextFormatter;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;

// translated and adapted by vaudrypl

public class OrthographyFormatTest extends SimpleNLG4TestBase {

	private DocumentElement list1, list2;
	private DocumentElement listItem1, listItem2, listItem3;
	private String list1Realisation = new StringBuffer("* dans la pièce ")
			.append("\n* derrière le rideau ").append("\n").toString();
	private String list2Realisation = new StringBuffer("* sur le rocher ")
			.append("\n* ").append(list1Realisation).append(' ').append("\n").toString();

	public OrthographyFormatTest(String name) {
		super(name);
	}

	@Before
	public void setUp() {
		super.setUp();

		// need to set formatter for realiser (set to null in the test
		// superclass)
		this.realiser.setFormatter(new TextFormatter());

		// a couple phrases as list items
		this.listItem1 = this.factory.createListItem(this.dansLaPiece);
		this.listItem2 = this.factory
				.createListItem(this.derriereLeRideau);
		this.listItem3 = this.factory.createListItem(this.surLeRocher);

		// a simple depth-1 list of phrases
		this.list1 = this.factory
				.createList(Arrays.asList(new DocumentElement[] {
						this.listItem1, this.listItem2 }));

		// a list consisting of one phrase (depth-1) + a list )(depth-2)
		this.list2 = this.factory.createList(Arrays
				.asList(new DocumentElement[] { this.listItem3,
						this.factory.createListItem(this.list1) }));
	}

	/**
	 * Test the realisation of a simple list
	 */
	@Test
	public void testSimpleListOrthography() {
		NLGElement realised = this.realiser.realise(this.list1);
		Assert.assertEquals(this.list1Realisation, realised.getRealisation());
	}

	/**
	 * Test the realisation of a list with an embedded list
	 */
	@Test
	public void testEmbeddedListOrthography() {
		NLGElement realised = this.realiser.realise(this.list2);
		Assert.assertEquals(this.list2Realisation, realised.getRealisation());
	}

}
