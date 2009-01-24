package eu.carlossanchez.continuouslava.cruisecontrol;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pragauto.X10DeviceController;
import com.pragauto.X10DeviceException;

/**
 * 
 * @author Carlos Sanchez http://www.carlossanchez.eu
 *
 */
public class FeedReader
{

    private static final String SUCCESS = "Success";

    final Logger logger = LoggerFactory.getLogger( FeedReader.class );

    private X10DeviceController controller;

    private URL feedUrl;

    public FeedReader( URL feedUrl, String comPort, String successDevice, String failureDevice )
    {
        this.feedUrl = feedUrl;
        controller = new X10DeviceController( comPort, successDevice, failureDevice );
    }

    public X10DeviceController getController()
    {
        return controller;
    }

    public void checkStatus()
        throws IOException, ParserConfigurationException, SAXException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = dbf.newDocumentBuilder();

        InputStream is = null;
        try
        {
            is = feedUrl.openStream();
            Document doc = db.parse( is );
            if ( isSuccess( doc ) )
            {
                onSuccess();
            }
            else
            {
                onFailure();
            }
        }
        finally
        {
            IOUtils.closeQuietly( is );
        }
    }

    private boolean isSuccess( Document doc )
    {
        Element element = doc.getDocumentElement();

        NodeList projects = element.getElementsByTagName( "Project" );
        if ( projects != null && projects.getLength() > 0 )
        {
            for ( int i = 0; i < projects.getLength(); i++ )
            {

                // get the employee element
                Element project = (Element) projects.item( i );

                String projectName = project.getAttribute( "name" );
                String buildStatus = project.getAttribute( "lastBuildStatus" );

                if ( !SUCCESS.equals( buildStatus ) )
                {
                    logger.info( "{} status is {}", projectName, buildStatus );
                    return false;
                }
                else
                {
                    logger.debug( "{} status is {}", projectName, buildStatus );
                }
            }
        }
        return true;
    }

    protected void onSuccess()
    {
        logger.info( "SUCCESS" );
        try
        {
            getController().pass();
        }
        catch ( X10DeviceException e )
        {
            throw new RuntimeException( e );
        }
    }

    protected void onFailure()
    {
        logger.info( "FAILURE" );
        try
        {
            getController().fail();
        }
        catch ( X10DeviceException e )
        {
            throw new RuntimeException( e );
        }
    }

}
