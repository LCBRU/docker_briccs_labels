package org.bru.briccs.labels.webapp;

import javax.servlet.http.HttpServletRequest ;

import org.bru.briccs.labels.webapp.Printer.PrintException;

public class PrintLabels {
	
	private static final String PRINT_PACK = "PACK" ;
	private static final String PRINT_SAMPLES = "SAMPLES" ;
	private static final String PRINT_BAGS = "BAGS" ;
	private static final String PRINT_EXTERNAL_BAGS = "ExternalBags" ;
	private static final String PRINT_MERMAID_PACK = "MERMAID" ;
	private static final String REQUEST_PARAMETER_NAME = "PrintLabels" ;
	
	public String print(HttpServletRequest request) {
		String message = null ;

		String requestType = request.getParameter( REQUEST_PARAMETER_NAME );
		
		if( isPack(request) ) {
			message = printPack() ;
		} 
		else if( isSamples(request) ) {
			message = printSamples() ;
		} 
		else if( isBags(request) ) {
			message = printBags() ;
		} 
		else if( isExternalBags(request) ) {
			message = printExternalBags() ;
		} 
		else if( PRINT_MERMAID_PACK.equalsIgnoreCase(requestType) ) {
			message = printMermaidPack() ;
		} 
		else {
			message = "Ready!";
		}
		return message;
	}
	
	private boolean isPack( HttpServletRequest request ) {
		if( PRINT_PACK.equalsIgnoreCase( request.getParameter( REQUEST_PARAMETER_NAME ) ) ) {
			return true ;
		}
		return false ;
	}
	
	private boolean isSamples( HttpServletRequest request ) {
		if( PRINT_SAMPLES.equalsIgnoreCase( request.getParameter( REQUEST_PARAMETER_NAME ) ) ) {
			return true ;
		}	
		return false ;
	}
	
	private boolean isBags( HttpServletRequest request ) {
		if( PRINT_BAGS.equalsIgnoreCase( request.getParameter( REQUEST_PARAMETER_NAME ) ) ) {
			return true ;
		}	
		return false ;
	}
	
	private boolean isExternalBags( HttpServletRequest request ) {
		if( PRINT_EXTERNAL_BAGS.equalsIgnoreCase( request.getParameter( REQUEST_PARAMETER_NAME ) ) ) {
			return true ;
		}	
		return false ;
	}
	
	private String printPack() {
		String message = null ;
		synchronized( PrintLabels.class ) {
			try {
				( Printer.Factory.newBagPrinter() ).print() ;
				( Printer.Factory.newSamplePrinter() ).print() ;
				message = "Ready!" ;
			}
			catch (PrintException lpx ){ 
				message = lpx.getLocalizedMessage() ;
			}
		}
		return message ;
	}
	
	private String printSamples() {
		String message = null ;
		synchronized( PrintLabels.class ) {
			try {
				( Printer.Factory.newSamplePrinter() ).print() ;
				message = "Ready!" ;
			}
			catch (PrintException lpx ){ 
				message = lpx.getLocalizedMessage() ;
			}
		}
		return message ;
	}
		
	private String printBags() {
		String message = null ;
		synchronized( PrintLabels.class ) {
			try {
				( Printer.Factory.newBagPrinter() ).print() ;
				message = "Ready!" ;
			}
			catch (PrintException lpx ){ 
				message = lpx.getLocalizedMessage() ;
			}
		}
		return message ;
	}
		
	private String printExternalBags() {
		String message = null ;
		synchronized( PrintLabels.class ) {
			try {
				( Printer.Factory.newExternalBagPrinter() ).print() ;
				message = "Ready!" ;
			}
			catch (PrintException lpx ){ 
				message = lpx.getLocalizedMessage() ;
			}
		}
		return message ;
	}
		
	private String printMermaidPack() {
		String message = null ;
		synchronized( PrintLabels.class ) {
			try {
				for (int i = 1; i < 4; i++) {
					String labelInfor = String.format("MERMAID 1 Visit %d", i);
					( Printer.Factory.newSerumBagPrinter(labelInfor) ).print() ;
					( Printer.Factory.newCitrateBagPrinter(labelInfor) ).print() ;
					( Printer.Factory.newUrineBagPrinter(labelInfor) ).print() ;
					( Printer.Factory.newSamplePrinter() ).print() ;
				}
				message = "Ready!" ;
			}
			catch (PrintException lpx ){ 
				message = lpx.getLocalizedMessage() ;
			}
		}
		return message ;
	}
}
