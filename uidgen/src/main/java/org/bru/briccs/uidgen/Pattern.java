package org.bru.briccs.uidgen;

public enum Pattern {
	
	Participant ( "PAR", "%1$08d" ),
    Sample ( "SAM", "%1$010d" ) ;
	
	private static final String SEPARATOR = "_" ;   
    private final String shortName ; 
    private final String format ;
    
    Pattern( String shortName, String format ) {
        this.shortName = shortName ;
        this.format = format ;
    }
     
    static public Pattern translate( String prefix ) {
    	if( prefix.equals( Participant.toString() ) ) {
    		return Participant ;
    	}
    	else if( prefix.equals( Sample.toString() ) ) {
    		return Sample ;
    	}
    	return null ;
    }
    
    public String toString() {
    	return shortName ;
    }
    
    public String getFormat() {
    	return format ;
    }
    
    public static void setId( UniqueId uid, Pattern prefix, String descriminator ) {
		uid.setId( prefix.toString() + descriminator ) ;
	}
	
	public static Pattern getPattern( UniqueId uid ) {
		String[] parts = uid.getId().split( SEPARATOR ) ;
		return Pattern.translate( parts[0] ) ;
	}
	
	public static String getIdDiscriminator( UniqueId uid ) {
		String[] parts =  uid.getId().split( SEPARATOR ) ;
		return parts[1] ;
	}
	
	public static String formId( Pattern pattern, String descriminator ) {
		return pattern.toString() + SEPARATOR + descriminator ;
	}
	
	public static String formId( String pattern, String descriminator ) {
		return pattern + SEPARATOR + descriminator ;
	}
	
}
