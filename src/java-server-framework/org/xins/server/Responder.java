/*
 * $Id$
 */
package org.xins.server;

import java.io.IOException;

/**
 * XINS responder. Classes that implement this interface can generate a XINS
 * response.
 *
 * <p />Initially the state is {@link #BEFORE_START}. The state is
 * changed by calls to the output methods.
 *
 * <p />The following table defines what the state transitions are when one of
 * the output methods is called in a certain state. Horizontally are the
 * current states, vertically the output methods. The cells self contain the
 * new state.
 *
 * <p /><table class="states">
 *    <tr>
 *       <th></th>
 *       <th><acronym title="BEFORE_START">S0</acronym></th>
 *       <th><acronym title="WITHIN_PARAMS">S1</acronym></th>
 *       <th><acronym title="START_TAG_OPEN">S2</acronym></th>
 *       <th><acronym title="WITHIN_ELEMENT">S3</acronym></th>
 *       <th><acronym title="AFTER_END">S4</acronym></th>
 *    </tr>
 *    <tr>
 *       <th>{@link #startResponse(ResultCode)}</th>
 *       <td><acronym title="WITHIN_PARAMS">S1</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *    </tr>
 *    <tr>
 *       <th>{@link #startResponse(boolean)}</th>
 *       <td><acronym title="WITHIN_PARAMS">S1</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *    </tr>
 *    <tr>
 *       <th>{@link #startResponse(boolean,String)}</th>
 *       <td><acronym title="WITHIN_PARAMS">S1</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *    </tr>
 *    <tr>
 *       <th>{@link #param(String,String)}</th>
 *       <td><acronym title="WITHIN_PARAMS">S1</acronym></td>
 *       <td class="nochange"><acronym title="WITHIN_PARAMS">S1</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *    </tr>
 *    <tr>
 *       <th>{@link #startTag(String)}</th>
 *       <td><acronym title="START_TAG_OPEN">S2</acronym></td>
 *       <td><acronym title="START_TAG_OPEN">S2</acronym></td>
 *       <td class="nochange"><acronym title="START_TAG_OPEN">S2</acronym></td>
 *       <td><acronym title="START_TAG_OPEN">S2</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *    </tr>
 *    <tr>
 *       <th>{@link #attribute(String,String)}</th>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="nochange"><acronym title="START_TAG_OPEN">S2</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *    </tr>
 *    <tr>
 *       <th>{@link #pcdata(String)}</th>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td><acronym title="WITHIN_ELEMENT">S3</acronym></td>
 *       <td class="nochange"><acronym title="WITHIN_ELEMENT">S3</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *    </tr>
 *    <tr>
 *       <th>{@link #endTag()}</th>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td><acronym title="WITHIN_ELEMENT">S3</acronym></acronym></td>
 *       <td class="nochange"><acronym title="WITHIN_ELEMENT">S3</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *    </tr>
 *    <tr>
 *       <th>{@link #fail(ResultCode)}</th>
 *       <td><acronym title="AFTER_END">S4</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *       <td class="err"><acronym title="IllegalStateException">ISE</acronym></td>
 *    </tr>
 *    <tr>
 *       <th>{@link #endResponse()}</th>
 *       <td><acronym title="AFTER_END">S4</acronym></td>
 *       <td><acronym title="AFTER_END">S4</acronym></td>
 *       <td><acronym title="AFTER_END">S4</acronym></td>
 *       <td><acronym title="AFTER_END">S4</acronym></td>
 *       <td class="nochange"><acronym title="AFTER_END">S4</acronym></td>
 *    </tr>
 * </table>
 *
 * <p />List of states as used in the table:
 *
 * <ul>
 *    <li>S0: BEFORE_START</li>
 *    <li>S1: WITHIN_PARAMS</li>
 *    <li>S2: START_TAG_OPEN</li>
 *    <li>S3: WITHIN_ELEMENT</li>
 *    <li>S4: AFTER_END</li>
 * </ul>
 *
 * <p />If {@link #startResponse(boolean,String)},
 * {@link #startResponse(boolean)}, {@link #startResponse(ResultCode)} or
 * {@link #fail(ResultCode)} is not called, then a <code>result</code> element
 * is written with <code>success="true"</code> and no result code.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public interface Responder
extends ResponderStates {

   /**
    * Starts the response output. This is done by writing a
    * <code>result</code> start tag.
    *
    * @param resultCode
    *    the result code, can be <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the response output has already started.
    *
    * @throws IOException
    *    if an I/O error occurred.
    *
    * @since XINS 0.15.
    */
   void startResponse(ResultCode resultCode)
   throws IllegalStateException, IOException;

   /**
    * Starts the response output. This is done by writing a
    * <code>result</code> start tag with the specified value for the
    * <em>result</em> attribute.
    *
    * @param success
    *    success indication.
    *
    * @throws IllegalStateException
    *    if the response output has already started.
    *
    * @throws IOException
    *    if an I/O error occurred.
    *
    * @since XINS 0.15
    */
   void startResponse(boolean success)
   throws IllegalStateException, IOException;

   /**
    * Starts the response output. This is done by writing a
    * <code>result</code> start tag.
    *
    * @param success
    *    success indication.
    *
    * @param returnCode
    *    the return code, or <code>null</code> if none.
    *
    * @throws IllegalStateException
    *    if the response output has already started.
    *
    * @throws IOException
    *    if an I/O error occurred.
    *
    * @deprecated
    *    Deprecated since XINS 0.15.
    *    Use {@link #startResponse(ResultCode)}.
    */
   void startResponse(boolean success, String returnCode)
   throws IllegalStateException, IOException;

   /**
    * Adds an output parameter to the response. The name and the value must
    * both be specified.
    *
    * @param name
    *    the name of the output parameter, not <code>null</code>.
    *
    * @param value
    *    the value of the output parameter, not <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the current state is either within the data section or after the
    *    end of the response.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || value == null
    *          || "".equals(name) || "".equals(value)</code>.
    *
    * @throws IOException
    *    if an I/O error occurred.
    */
   void param(String name, String value)
   throws IllegalStateException, IllegalArgumentException, IOException;

   /**
    * Writes a start tag within the data section.
    *
    * @param type
    *    the element type, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null</code>.
    *
    * @throws IllegalStateException
    *    if the state is already past the data section.
    *
    * @throws IOException
    *    if an I/O error occurred.
    */
   void startTag(String type)
   throws IllegalStateException, IllegalArgumentException, IOException;

   /**
    * Writes an attribute within the current element.
    *
    * @param name
    *    the name of the attribute, not <code>null</code> and not an empty
    *    string.
    *
    * @param value
    *    the name of the attribute, not <code>null</code> and not an empty
    *    string.
    *
    * @throws IllegalStateException
    *    if the state is not so that a start tag is currently open.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || value == null
    *          || "".equals(name) || "".equals(value)</code>.
    *
    * @throws IOException
    *    if an I/O error occurred.
    */
   void attribute(String name, String value)
   throws IllegalStateException, IllegalArgumentException, IOException;

   /**
    * Writes parsed character data.
    *
    * @param text
    *    the text to be written, not <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the current state is not within an element.
    *
    * @throws IllegalArgumentException
    *    if <code>text == null</code>.
    *
    * @throws IOException
    *    if an I/O error occurred.
    */
   void pcdata(String text)
   throws IllegalStateException, IllegalArgumentException, IOException;

   /**
    * Ends the current element by writing an end tag.
    *
    * @throws IllegalStateException
    *    if the current state is not so that there is a start tag to be ended.
    *
    * @throws IOException
    *    if an I/O error occurred.
    */
   void endTag()
   throws IllegalStateException, IOException;

   /**
    * Indicates a failure.
    *
    * @param resultCode
    *    the result code, can be <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the response output has already started.
    *
    * @throws IllegalArgumentException
    *    if <code>resultCode.getSuccess() == true</code>.
    *
    * @throws IOException
    *    if an I/O error occurred.
    *
    * @since XINS 0.29.
    */
   void fail(ResultCode resultCode)
   throws IllegalArgumentException, IllegalStateException, IOException;

   /**
    * Ends the response output. This is done by writing a <code>result</code>
    * end tag. If necessary a <code>result</code> start tag is written as
    * well.
    *
    * @throws IOException
    *    if an I/O error occurred.
    */
   void endResponse() throws IOException;
}
