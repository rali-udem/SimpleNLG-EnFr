package simplenlg.test;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import simplenlg.format.english.TextFormatter;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;

public class OrthographyFormatTests extends SimpleNLG4Test {

	private DocumentElement list1, list2;
	private DocumentElement listItem1, listItem2, listItem3;
	private String list1Realisation = new StringBuffer("* in the room ")
			.append("\n* behind the curtain ").append("\n").toString();
	private String list2Realisation = new StringBuffer("* on the rock ")
			.append("\n* ").append(list1Realisation).append(' ').append("\n").toString();

	public OrthographyFormatTests(String name) {
		super(name);
	}

	@Before
	public void setUp() {
		super.setUp();

		// need to set formatter for realiser (set to null in the test
		// superclass)
		this.realiser.setFormatter(new TextFormatter());

		// a couple phrases as list items
		this.listItem1 = this.phraseFactory.createListItem(this.inTheRoom);
		this.listItem2 = this.phraseFactory
				.createListItem(this.behindTheCurtain);
		this.listItem3 = this.phraseFactory.createListItem(this.onTheRock);

		// a simple depth-1 list of phrases
		this.list1 = this.phraseFactory
				.createList(Arrays.asList(new DocumentElement[] {
						this.listItem1, this.listItem2 }));

		// a list consisting of one phrase (depth-1) + a list )(depth-2)
		this.list2 = this.phraseFactory.createList(Arrays
				.asList(new DocumentElement[] { this.listItem3,
						this.phraseFactory.createListItem(this.list1) }));
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
