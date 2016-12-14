package org.bru.briccs.labels.webapp ;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.bru.briccs.uidgen.UidGen;
import org.bru.briccs.uidgen.Pattern ;
import org.bru.briccs.uidgen.UidGen.UidGenException;

public class Printer {
	
	private static final int DEFAULT_PORT = 9100;
	//
	// InetAddress and ipAddress are alternatives...
	private InetAddress inetAddress ;
	private String ipAddress ;
	private String barcodeTemplate;
	private UidGen uidgen = null;
	private String[] barcodes;
	private int batchSize ;
	
	private Printer( String ipAddress, String barcodeTemplate, Pattern pattern, int batchSize ) {
		this.ipAddress = ipAddress ;
		this.barcodeTemplate = barcodeTemplate;
		this.batchSize = batchSize ;
		uidgen = new UidGen( pattern );
	}
	
	private Printer( InetAddress inetAddress, String barcodeTemplate, Pattern pattern, int batchSize ) {
		this.inetAddress = inetAddress ;
		this.barcodeTemplate = barcodeTemplate;
		this.batchSize = batchSize ;
		uidgen = new UidGen( pattern );
	}
	
	public void print() throws PrintException {
		this.print( this.batchSize ) ;
	}

	public void print( int size ) throws PrintException {

		Socket clientSocket = null;
		try {
			clientSocket = getSocket() ;
			DataOutputStream os = new DataOutputStream( clientSocket.getOutputStream() );
			barcodes = uidgen.generate(size);
			for (int i = 0; i < barcodes.length; i++) {
				printOne( os, barcodes[i] );
			}
		} catch ( IOException iox ) {
			throw new PrintException( iox.getLocalizedMessage() ) ;
		} catch ( UidGenException ugx ) {
			throw new PrintException( ugx.getLocalizedMessage() ) ;
		} finally {
			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException iox) {
				}
			}
		}
	}
	
	private Socket getSocket() throws PrintException {
		Socket socket = null ;
		try {
			if( this.inetAddress != null ) {
				socket = new Socket( inetAddress, DEFAULT_PORT ) ;
			}
			else if( this.ipAddress != null ) {
				socket = new Socket( ipAddress, DEFAULT_PORT ) ;
			}
		}
		catch( IOException iox ) {
			throw new PrintException( iox.getLocalizedMessage() ) ;
		}		
		return socket ;
	}
	
	private void printOne(DataOutputStream os, String barcodeNumber) throws IOException {
		String barcode = this.barcodeTemplate.replace("${number}",	barcodeNumber);
  		barcode = barcode.replace("{STUDY_PLACEHOLDER}", "w" + barcodeNumber);
		os.writeBytes(barcode);
	}
	
	private static int formatSampleBatchSize( ResourceBundle config ) throws PrintException {
		String string = config.getString( Printer.Factory.SAMPLE_BATCH_SIZE ) ;
		try {
			int batchSize = ( new Integer( string ) ).intValue() ;
			return batchSize ;
		} 
		catch( Exception ex ) {
			throw new PrintException( "Problem with sample labels batch size." ) ;
		}		
	}
	
	public static class PrintException extends Exception {

		private static final long serialVersionUID = 1L;

		public PrintException(String message) {
			super(message);
		}

	}
	
	public static class Factory {
		
		private static final String SAMPLE_PRINTER_IPADDRESS = "org.bru.briccs.sample.printer.ipaddress" ;
		private static final String SAMPLE_PRINTER_HOSTNAME = "org.bru.briccs.sample.printer.hostname" ;
		private static final String SAMPLE_TEMPLATE_FILE = "org.bru.briccs.sample.template.file" ;
		private static final String SAMPLE_BATCH_SIZE = "org.bru.briccs.sample.batch.size" ;

		private static final String BAG_SERUM_LABEL = "org.bru.briccs.serum.bag.template.file" ;
		private static final String BAG_CITRATE_LABEL = "org.bru.briccs.citrate.bag.template.file" ;
		private static final String BAG_URINE_LABEL = "org.bru.briccs.urine.bag.template.file" ;
		private static final String BAG_PRINTER_IPADDRESS = "org.bru.briccs.bag.printer.ipaddress" ;
		private static final String BAG_PRINTER_HOSTNAME = "org.bru.briccs.bag.printer.hostname" ;
		private static final String BAG_TEMPLATE_FILE = "org.bru.briccs.bag.template.file" ;
		private static final String EXTERNAL_BAG_TEMPLATE_FILE = "org.bru.briccs.external.bag.template.file" ;
		private static final int BAG_BATCH_SIZE = 1 ;
		
		public static Printer newBagPrinter() throws PrintException {
			ResourceBundle config = ResourceBundle.getBundle( Printer.class.getCanonicalName() ) ;			
			String pathTemplate = config.getString( BAG_TEMPLATE_FILE ) ;
			String barcodeTemplate = buildBarcodeTemplate( pathTemplate ) ;
			String hostname = getHostName( BAG_PRINTER_HOSTNAME, config ) ;
			if( hostname != null ) {
				try {
					return new Printer( InetAddress.getByName( hostname ), barcodeTemplate, Pattern.Participant, BAG_BATCH_SIZE ) ;
				}
				catch( UnknownHostException uhx ) {
					throw new PrintException( uhx.getLocalizedMessage() ) ;
				}				
			}
			String ipAddress = config.getString( BAG_PRINTER_IPADDRESS ) ;
			return new Printer( ipAddress, barcodeTemplate, Pattern.Participant, BAG_BATCH_SIZE ) ;
		}
		
		public static Printer newExternalBagPrinter() throws PrintException {
			ResourceBundle config = ResourceBundle.getBundle( Printer.class.getCanonicalName() ) ;			
			String pathTemplate = config.getString( EXTERNAL_BAG_TEMPLATE_FILE ) ;
			String barcodeTemplate = buildBarcodeTemplate( pathTemplate ) ;
			String hostname = getHostName( BAG_PRINTER_HOSTNAME, config ) ;
			if( hostname != null ) {
				try {
					return new Printer( InetAddress.getByName( hostname ), barcodeTemplate, Pattern.Participant, BAG_BATCH_SIZE ) ;
				}
				catch( UnknownHostException uhx ) {
					throw new PrintException( uhx.getLocalizedMessage() ) ;
				}				
			}
			String ipAddress = config.getString( BAG_PRINTER_IPADDRESS ) ;
			return new Printer( ipAddress, barcodeTemplate, Pattern.Participant, BAG_BATCH_SIZE ) ;
		}
		
		public static Printer newSamplePrinter() throws PrintException {
			ResourceBundle config = ResourceBundle.getBundle( Printer.class.getCanonicalName() ) ;
			String pathTemplate = config.getString( SAMPLE_TEMPLATE_FILE ) ;
			String barcodeTemplate = buildBarcodeTemplate( pathTemplate ) ;
			int batchSize = formatSampleBatchSize( config ) ;
			String hostname = getHostName( SAMPLE_PRINTER_HOSTNAME, config ) ;
			if( hostname != null ) {
				try {
					return new Printer( InetAddress.getByName( hostname ), barcodeTemplate, Pattern.Participant, batchSize ) ;
				}
				catch( UnknownHostException uhx ) {
					throw new PrintException( uhx.getLocalizedMessage() ) ;
				}				
			}
			String ipAddress = config.getString( SAMPLE_PRINTER_IPADDRESS ) ;
			return new Printer( ipAddress, barcodeTemplate, Pattern.Sample, batchSize ) ;
		}
		
		public static Printer newSerumBagPrinter(String study_name) throws PrintException {
			ResourceBundle config = ResourceBundle.getBundle( Printer.class.getCanonicalName() ) ;			
			String pathTemplate = config.getString( BAG_SERUM_LABEL ) ;
			String barcodeTemplate = buildBarcodeTemplate( pathTemplate ) ;
     		barcodeTemplate = barcodeTemplate.replace("{STUDY_PLACEHOLDER}", study_name);
			String hostname = getHostName( BAG_PRINTER_HOSTNAME, config ) ;
			if( hostname != null ) {
				try {
					return new Printer( InetAddress.getByName( hostname ), barcodeTemplate, Pattern.Participant, BAG_BATCH_SIZE ) ;
				}
				catch( UnknownHostException uhx ) {
					throw new PrintException( uhx.getLocalizedMessage() ) ;
				}				
			}
			String ipAddress = config.getString( BAG_PRINTER_IPADDRESS ) ;
			return new Printer( ipAddress, barcodeTemplate, Pattern.Participant, BAG_BATCH_SIZE ) ;
		}
		
		public static Printer newCitrateBagPrinter(String study_name) throws PrintException {
			ResourceBundle config = ResourceBundle.getBundle( Printer.class.getCanonicalName() ) ;			
			String pathTemplate = config.getString( BAG_CITRATE_LABEL ) ;
			String barcodeTemplate = buildBarcodeTemplate( pathTemplate ) ;
     		barcodeTemplate = barcodeTemplate.replace("{STUDY_PLACEHOLDER}", study_name);
			String hostname = getHostName( BAG_PRINTER_HOSTNAME, config ) ;
			if( hostname != null ) {
				try {
					return new Printer( InetAddress.getByName( hostname ), barcodeTemplate, Pattern.Participant, BAG_BATCH_SIZE ) ;
				}
				catch( UnknownHostException uhx ) {
					throw new PrintException( uhx.getLocalizedMessage() ) ;
				}				
			}
			String ipAddress = config.getString( BAG_PRINTER_IPADDRESS ) ;
			return new Printer( ipAddress, barcodeTemplate, Pattern.Participant, BAG_BATCH_SIZE ) ;
		}
		
		public static Printer newUrineBagPrinter(String study_name) throws PrintException {
			ResourceBundle config = ResourceBundle.getBundle( Printer.class.getCanonicalName() ) ;			
			String pathTemplate = config.getString( BAG_URINE_LABEL ) ;
			String barcodeTemplate = buildBarcodeTemplate( pathTemplate ) ;
     		barcodeTemplate = barcodeTemplate.replace("{STUDY_PLACEHOLDER}", study_name);
			String hostname = getHostName( BAG_PRINTER_HOSTNAME, config ) ;
			if( hostname != null ) {
				try {
					return new Printer( InetAddress.getByName( hostname ), barcodeTemplate, Pattern.Participant, BAG_BATCH_SIZE ) ;
				}
				catch( UnknownHostException uhx ) {
					throw new PrintException( uhx.getLocalizedMessage() ) ;
				}				
			}
			String ipAddress = config.getString( BAG_PRINTER_IPADDRESS ) ;
			return new Printer( ipAddress, barcodeTemplate, Pattern.Participant, BAG_BATCH_SIZE ) ;
		}
		
		private static String getHostName( String key, ResourceBundle config ) {
			try {
				return config.getString( key ) ;
			}
			catch( MissingResourceException mrx ) {
				return null ;
			}			
		}
		
		private static String buildBarcodeTemplate( String pathTemplate ) throws PrintException {
	    	BufferedReader reader = null ;
	    	StringBuilder b = new StringBuilder( 1024 ) ;
	    	try {
	    		reader = new BufferedReader( new FileReader( pathTemplate ) ) ;	    		
	    		int c = reader.read()  ;
	    		while( c != -1 ) {
	    			b.append( (char)c ) ;
	    			c=reader.read();
	    		}
	    	}
	    	catch( FileNotFoundException fnfx ) {
	    		throw new PrintException( "Could not find barcode template file." ) ;
	    	}
	    	catch( IOException iox ) {
	    		throw new PrintException( "Problems reading barcode template file." ) ;
	    	} 
	    	finally {
	    		if( reader != null ) {
	    			try { reader.close() ; } catch( Exception ex ) {} ;
	    		}
	    	}
	    	return b.toString() ;
	    }
		
	}

}
