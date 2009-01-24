package eu.carlossanchez.continuouslava.continuum;

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

import java.net.URL;
import java.util.List;

import org.apache.maven.continuum.xmlrpc.client.ContinuumXmlRpcClient;
import org.apache.maven.continuum.xmlrpc.project.ContinuumProjectState;
import org.apache.maven.continuum.xmlrpc.project.ProjectGroup;
import org.apache.maven.continuum.xmlrpc.project.ProjectSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pragauto.X10DeviceController;
import com.pragauto.X10DeviceException;

/**
 * 
 * @author Carlos Sanchez http://www.carlossanchez.eu
 *
 */
public class ContinuumRemoteClient
{

    final Logger logger = LoggerFactory.getLogger( ContinuumRemoteClient.class );

    private X10DeviceController controller;

    private URL feedUrl;

    public ContinuumRemoteClient( URL feedUrl, String comPort, String successDevice, String failureDevice )
    {
        this.feedUrl = feedUrl;
        controller = new X10DeviceController( comPort, successDevice, failureDevice );
    }

    public X10DeviceController getController()
    {
        return controller;
    }

    public void checkStatus()
        throws Exception
    {
        ContinuumXmlRpcClient client = new ContinuumXmlRpcClient( feedUrl );

        if ( isSuccess( client ) )
        {
            onSuccess();
        }
        else
        {
            onFailure();
        }
    }

    private boolean isSuccess( ContinuumXmlRpcClient client )
        throws Exception
    {
        List<ProjectGroup> groups = client.getAllProjectGroupsWithAllDetails();

        for ( ProjectGroup group : groups )
        {
            List<ProjectSummary> projects = group.getProjects();
            for ( ProjectSummary project : projects )
            {
                String projectName = project.getName();

                if ( project.getState() == ContinuumProjectState.OK )
                {
                    logger.debug( "'{}' status is OK", projectName );
                }
                else if ( project.getState() == ContinuumProjectState.FAILED )
                {
                    logger.info( "'{}' status is FAILED", projectName );
                    return false;
                }
                else if ( project.getState() == ContinuumProjectState.ERROR )
                {
                    logger.info( "'{}' status is ERROR", projectName );
                    return false;
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
