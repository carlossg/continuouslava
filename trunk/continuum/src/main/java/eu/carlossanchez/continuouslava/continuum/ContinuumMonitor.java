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
import java.util.Calendar;

import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pragauto.X10DeviceController;

/**
 * 
 * @author Carlos Sanchez http://www.carlossanchez.eu
 *
 */
public class ContinuumMonitor
{
    final static Logger logger = LoggerFactory.getLogger( ContinuumMonitor.class );

    public static void main( String[] args )
        throws Exception
    {
        if ( ( args.length < 1 ) || ( args.length > 6 ) )
        {
            System.out.println( "Usage " + ContinuumMonitor.class.getName()
                + " Continuum_URL [COMPort] [SuccessDevice] [FailureDevice] [startingAtHour] [endingAtHour]" );
            System.out.println( "  Continuum_URL : url of the Continuum server, usually http://host:port/continuum/xmlrpc" );
            System.out.println( "  COMPort: COM1 by default" );
            System.out.println( "  SuccessDevice: X10 Code of device to turn on on success, by default A1" );
            System.out.println( "  FailureDevice: X10 Code of device to turn on on failure, by default A2" );
            System.out.println( "  startingAtHour: hour to start turning on the lamps, by default 8" );
            System.out.println( "  endingAtHour: hour to turn off both the lamps, by default 20" );
            System.exit( -1 );
        }

        String url = args[0];
        String comPort = "COM1";
        String successDevice = "A1";
        String failureDevice = "A2";
        int startingAtHour = 8;
        int endingAtHour = 20;

        if ( args.length == 6 )
        {
            endingAtHour = Integer.parseInt( args[5] );
        }
        if ( args.length >= 5 )
        {
            startingAtHour = Integer.parseInt( args[4] );
        }
        if ( args.length >= 4 )
        {
            failureDevice = args[3];
        }
        if ( args.length >= 3 )
        {
            successDevice = args[2];
        }
        if ( args.length >= 2 )
        {
            comPort = args[1];
        }

        System.out.println( "Running with parameters:" );
        System.out.println( "  url=" + url );
        System.out.println( "  comPort=" + comPort );
        System.out.println( "  successDevice=" + successDevice );
        System.out.println( "  failureDevice=" + failureDevice );
        System.out.println( "  startingAtHour=" + startingAtHour );
        System.out.println( "  endingAtHour=" + endingAtHour );
        ContinuumRemoteClient reader =
            new ContinuumRemoteClient( new URL( url ), comPort, successDevice, failureDevice );
        X10DeviceController controller = reader.getController();

        while ( true )
        {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get( Calendar.HOUR_OF_DAY );
            int day = calendar.get( Calendar.DAY_OF_WEEK );

            if ( ( hour < startingAtHour ) || ( hour >= endingAtHour ) || ( day == Calendar.SATURDAY )
                || ( day == Calendar.SUNDAY ) )
            {
                /* turn off lamps at night and weekends */
                controller.passDevice().off();
                controller.failDevice().off();
                Thread.sleep( 1000 * 60 * 60 ); // 1 hour
            }
            else
            {
                try
                {
                    reader.checkStatus();
                }
                catch ( XmlRpcException e )
                {
                    // error connecting to server
                    logger.error( "Error connecting to server, turning lamps off", e );
                    controller.passDevice().off();
                    controller.failDevice().off();
                }
                Thread.sleep( 1000 * 60 ); // 60 seconds
            }
        }
    }
}
