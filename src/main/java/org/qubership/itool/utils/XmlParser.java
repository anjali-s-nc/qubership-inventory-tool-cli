/*
 * Copyright 2024-2025 NetCracker Technology Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qubership.itool.utils;

import io.vertx.core.buffer.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlParser.class);

    static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

    /** Parse XML document from a file.
     *
     * @param fileName File name
     * @return Parsed document
     * @throws Exception Parse exception
     */
    public static Document parseXmlFile(String fileName) throws Exception {
        DocumentBuilder builder = FACTORY.newDocumentBuilder();
        try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
            return builder.parse(is, fileName);
        }
    }

    /** Parse XML document from a string.
     *
     * @param xmlData Input data
     * @param inputId Name of input
     * @return Parsed document
     * @throws Exception Parse exception
     */
    public static Document parseXmlString(String xmlData, String inputId) throws Exception {
        DocumentBuilder builder = FACTORY.newDocumentBuilder();
        InputSource source = new InputSource(new StringReader(xmlData));
        source.setSystemId(inputId);
        return builder.parse(source);
    }

    /** Parse XML from InputStream. The stream is automatically closed by a parser.
     *
     * @param is Input stream
     * @param inputId Name of input
     * @return Parsed document
     * @throws Exception Parse exception
     */
    public static Document parseXmlData(InputStream is, String inputId) throws Exception {
        DocumentBuilder builder = FACTORY.newDocumentBuilder();
        return builder.parse(is, inputId);
    }

    /** Parse XML from byte array.
     *
     * @param bytes Input data
     * @param inputId Name of input
     * @return Parsed document
     * @throws Exception Parse exception
     */
    public static Document parseXmlData(byte[] bytes, String inputId) throws Exception {
        InputStream is = new ByteArrayInputStream(bytes);
        return parseXmlData(is, inputId);
    }

    /** Parse XML from VertX Buffer.
     *
     * @param buffer Input data
     * @param inputId Name of input
     * @return Parsed document
     * @throws Exception Parse exception
     */
    public static Document parseXmlData(Buffer buffer, String inputId) throws Exception {
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        return parseXmlData(is, inputId);
    }

}
