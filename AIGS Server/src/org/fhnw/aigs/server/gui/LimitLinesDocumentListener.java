package org.fhnw.aigs.server.gui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/*------------------------------------------------------------------------------
 * IMPORTANT NOTE!!!
 * #################
 * I assume that this class was written by Rob Camick. It was found at:
 * https://tips4java.wordpress.com/2008/10/15/limit-lines-in-document/
 * Unfortunately, no author nor license information was added to this class
 * nor mentioned on the above stated website.
 * The class "LimitLinesDocumentListener"(.java) is used in many projects
 * according the results of the web search engine of your choice.
 * The class has most times the license GPLv2/3.
 * I ASSUME GULLIBLE THAT THIS CLASS CAN BE USED UNDER A GPL-COMPATIBLE LICENSE
 * LIKE GPLV3, WHICH IS USED BY AIGS. PLEASE CONTACT A MAINTAINER OF AGIS IF
 * THIS ASSUMPTION IS A FALLACY.
 * ----------------------------------------------------------------------------- 
 */

/**
 *  A class to control the maximum number of lines to be stored in a Document<br>
 *  Excess lines can be removed from the start or end of the Document
 *  depending on your requirement.<br><br>
 *  a) if you append text to the Document, then you would want to remove lines from the start.<br>
 *  b) if you insert text at the beginning of the Document, then you would want to remove lines from the end.<br>
 * @author Rob Camick (Adapted by Raphael Stoeckli)
 * @version 1.0
 */
public class LimitLinesDocumentListener implements DocumentListener
{
	private int maximumLines;
	private boolean isRemoveFromStart;

        /**
	 *  Specify the number of lines to be stored in the Document. 
	 *  Extra lines will be removed from the start of the Document.
         * @param maximumLines Number of displayed lines
         */
	public LimitLinesDocumentListener(int maximumLines)
	{
		this(maximumLines, true);
	}

        /**
	 *  Specify the number of lines to be stored in the Document. 
	 *  Extra lines will be removed from the start or end of the Document, 
	 *  depending on the boolean value specified.
         * @param maximumLines Number of displayed lines
         * @param isRemoveFromStart If true, the lines will be removed at the top of the document, otherwise at the bottom
         */
	public LimitLinesDocumentListener(int maximumLines, boolean isRemoveFromStart)
	{
		setLimitLines(maximumLines);
		this.isRemoveFromStart = isRemoveFromStart;
	}

        /**
         * Return the maximum number of lines to be stored in the Document
         * @return Maximum number of lines
         */
	public int getLimitLines()
	{
		return maximumLines;
	}

        /**
         * Set the maximum number of lines to be stored in the Document
         * @param maximumLines Maximum number of lines
         */
	public void setLimitLines(int maximumLines)
	{
		if (maximumLines < 1)
		{
			String message = "Maximum lines must be greater than 0";
			throw new IllegalArgumentException(message);
		}

		this.maximumLines = maximumLines;
	}

	//  Handle insertion of new text into the Document

        /**
         * Method to handle the insertation of new text into the document
         * @param e DocumentEvent to monitor
         */
        @Override
	public void insertUpdate(final DocumentEvent e)
	{
		//  Changes to the Document can not be done within the listener
		//  so we need to add the processing to the end of the EDT

		SwingUtilities.invokeLater( new Runnable()
		{
                        @Override
			public void run()
			{
				removeLines(e);
			}
		});
	}

        /**
         * Dummy: Implemented method. Must not be specified in this case
         * @param e DocumentEvent to monitor
         */
        @Override
	public void removeUpdate(DocumentEvent e) {}
        /**
         * Dummy: Implemented method. Must not be specified in this case
         * @param e DocumentEvent to monitor
         */
        @Override
	public void changedUpdate(DocumentEvent e) {}

        /**
         * Method to remove lines from the Document when necessary
         * @param e DocumentEvent to monitor
         */
	private void removeLines(DocumentEvent e)
	{
		//  The root Element of the Document will tell us the total number
		//  of line in the Document.

		Document document = e.getDocument();
		Element root = document.getDefaultRootElement();

		while (root.getElementCount() > maximumLines)
		{
			if (isRemoveFromStart)
			{
				removeFromStart(document, root);
			}
			else
			{
				removeFromEnd(document, root);
			}
		}
	}

        /**
         * Method to remove lines from the start of the Document
         * @param document Document to handle
         * @param root Root element of the document
         */
	private void removeFromStart(Document document, Element root)
	{
		Element line = root.getElement(0);
		int end = line.getEndOffset();

		try
		{
			document.remove(0, end);
		}
		catch(BadLocationException ble)
		{
			System.out.println(ble);
		}
	}

        /**
         * Method to remove lines from the end of the Document
         * @param document Document to handle
         * @param root Root element of the document
         */
	private void removeFromEnd(Document document, Element root)
	{
		//  We use start minus 1 to make sure we remove the newline
		//  character of the previous line

		Element line = root.getElement(root.getElementCount() - 1);
		int start = line.getStartOffset();
		int end = line.getEndOffset();

		try
		{
			document.remove(start - 1, end - start);
		}
		catch(BadLocationException ble)
		{
			System.out.println(ble);
		}
	}
}
