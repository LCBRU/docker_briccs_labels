package org.bru.briccs.uidgen;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command line test programme
 *
 */
public class UniqueIdManager {
	
	public static final String WELCOME = 
        "Welcome to the BRICCS unique id generator...\n\n" +
        " USAGE: Press the ENTER key to generate a batch of id's.\n" +
        "        You will be prompted for a yes/no confirmation.\n" +
        "        The default response is \"no\".\n" +
        "        Type \"bye\" to exit.\n\n" +
        " COMMAND LINE OPTIONS: \n" +
        "        -b=nnn\n" +
        "           Where nnn is the number per batch to be generated.\n" +
        "           Batch size must be within the range 1-100.\n" +
        "           The default is 5.\n" +
        "        -p=CCC\n" +
        "           Where CC is the prefix. For example \"PAR\" for Participant.\n" +
        "           Defaults to PAR.";
	
	private static final int MAX_BATCH_SIZE = 100 ;
	
	private static final Logger log = LoggerFactory.getLogger( UniqueIdManager.class ) ;
	
	private int batchSize = 5 ;
	private Pattern pattern = Pattern.Participant ;
	
    public static void main( String[] args ) {
    	
    	UniqueIdManager manager = new UniqueIdManager() ;
    	
    	manager.printToConsole( WELCOME );
        
        if( !manager.retrieveArgs( args ) ) {
            return ;
        }
        
        manager.printToConsole( "\nBatch size set to " + manager.batchSize ) ;
        
        try {
            UidGen uidgen = new UidGen( manager.pattern ) ;
            while( true ) {
            	if( manager.getUserInput() == true ) {
                    String[] ids = uidgen.generate( manager.batchSize ) ;
                    manager.printToConsole( "" ) ;
                    for( int i=0; i<ids.length; i++ ) {
                    	manager.printToConsole( ids[i] ) ;
                    }
            	}
            	else {
            		uidgen.printIdentifiers_Debug() ;
            		manager.printToConsole( "\nGoodbye!" ) ;
            		return ;
            	}
            }
    		
    	}
    	catch( Exception ex ) {
    		log.error( "Error within UniqueIdManager", ex ) ;
    	}
    }
    
    private boolean retrieveArgs( String[] args ) {
    	boolean retVal = true ;
    	if( args != null ) {
    		if( args.length <= 2 ) {
    			for( int i=0; i<args.length; i++ ) {
    				if( args[i].startsWith( "-b") ) {
    					if( args[i].length() > 3 ) {
    						String bz = args[i].substring(3) ; 
    						try {
    							batchSize = Integer.valueOf( bz ).intValue() ;
    							if( batchSize > MAX_BATCH_SIZE || batchSize < 1 ) {
    								retVal = false ;
    							}
    						}
    						catch( NumberFormatException nfx ) {
    							retVal = false ;
    						}                   	
    					}
    				}
    				else if( args[i].startsWith( "-p") ) {
    					if( args[i].length() > 3 ) {
    						if( args[i].substring(3).equalsIgnoreCase( Pattern.Participant.toString() ) ) {
    							pattern = Pattern.Participant ;
    						}
    						else if(  args[i].substring(3).equalsIgnoreCase( Pattern.Sample.toString() ) ) {
    							pattern = Pattern.Sample ;
    						}
    						else {
    							retVal = false ;
    						}
    					}
    				}
    			} 
    		}
    		else if( args.length > 1 ){
    			retVal = false ;
    		}
    	} 
    	if( retVal == false ) {
    		printToConsole( "Invalid parameters:" ) ; 
    		for( int i=0; i<args.length; i++ ) {
    			printToConsole( args[i] ) ;
    		}
    	}
    	return retVal ;
    }
    
    private boolean getUserInput() throws IOException {
    	while( true ) {
    		printToConsole( "\nGenerate batch? Answer y/n." ) ;
    		String line = readLineFromConsole() ;
    		if( line.equalsIgnoreCase( "y") || line.equalsIgnoreCase( "yes" ) 
    		) {
    			return true ;
    		}
    		else if( line.equalsIgnoreCase( "n") || line.equalsIgnoreCase( "no" )  ) {
    			continue ;
    		}
    		else if( line.equalsIgnoreCase( "bye" ) ) {
    			return false ;
    		}
    		else {
    			printToConsole( "Didn't understand that." ) ;
    		}
    	}   	
    }
    
    private String readLineFromConsole() throws IOException {
    	StringBuilder lineBuffer = new StringBuilder() ;
    	char c = (char)System.in.read() ;
    	while( c != '\n' ) {
    		lineBuffer.append( c ) ;
    		c = (char)System.in.read() ;
    	}
    	return lineBuffer.toString().trim() ;
    }
    
    private void printToConsole( String message ) {
    	System.out.println( message ) ;
    }

}
