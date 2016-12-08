package org.bru.briccs.uidgen;

import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import org.hibernate.transaction.JDBCTransactionFactory ;
import org.hibernate.transaction.TransactionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UidGen { 
		
	private static final int MAX_BATCH_SIZE = 100 ;
	private static final int DEFAULT_BATCH_SIZE = 1 ;
	
	private static final int DEFAULT_RANGE = 1000000 ;
	private static final String OPEN_STATUS = "OPEN" ;
	private static final String CLOSED_STATUS = "CLOSED" ;
	private static final String DELETED_STATUS = "DELETED" ;
	
	/*
	 * Represents approximately 95% density
	 */
	private static final int CYCLE_DENSITY = 19 ;
	
	private static final Logger log = LoggerFactory.getLogger( UidGen.class ) ;
	
	private IdRange openRange = null ;
	private IdRange closedRange = null ;
	private int range = DEFAULT_RANGE ;
	
	private SessionFactory sessionFactory ;
	private Random random = new Random() ;
	private Pattern pattern ;
	
	public class UidGenException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UidGenException() {
			super();
		}

		public UidGenException(String message, Throwable cause) {
			super(message, cause);
		}

		public UidGenException(String message) {
			super(message);
		}

		public UidGenException(Throwable cause) {
			super(cause);
		}
		
	}
	
	public UidGen( Pattern pattern ) {
		this( pattern, DEFAULT_RANGE ) ;
	}
	
	public UidGen( Pattern pattern, int range ) {
		this.pattern = pattern ;
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = HibernateUtil.getSessionFactory() ;
			if( sessionFactory == null ) {
				log.error( "SessionFactory is null." ) ;
			}
			else {
				if( range <= 100 ) {
					this.range = DEFAULT_RANGE ;
				}
				primeRange( pattern ) ;
			}
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			log.error("Initial SessionFactory creation failed." + ex) ;
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	private void primeRange( Pattern pattern ) {
		Session session = sessionFactory.openSession() ;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			openRange = 
				(IdRange) session
				.createQuery( "from IdRange as range where range.prefix = :prefix and range.status = 'OPEN'")
				.setText( "prefix", pattern.toString() )
				.uniqueResult() ;
			if( openRange == null ) {
				openRange = new IdRange() ;
				openRange.setPrefix( pattern.toString() ) ;
				openRange.setFromValue( 1 ) ;
				openRange.setToValue( range ) ;
				openRange.setStatus( OPEN_STATUS ) ;
				session.save( openRange ) ;
				if( log.isDebugEnabled() ) {
					log.debug( "IdRange pk: " + openRange.getPk() ) ;
				}
			}
			tx.commit();
		}
		catch( RuntimeException ex ) {
			if (tx != null) tx.rollback() ;
			throw ex ; 
		}
		finally {
			session.close();
		}
	}
	
	public String[] generate( int batchSize ) throws UidGenException {
		batchSize = checkBatchSize( batchSize ) ;
		String[] ids = new String[batchSize] ;
        Session session = sessionFactory.openSession();
        Transaction tx = null ;
        try {
        	tx = session.beginTransaction();
        	for( int i=0; i<batchSize; i++ ) {      	
        		UniqueId uid = generate( session ) ;      		
        		ids[i] = Pattern.getIdDiscriminator( uid ) ;     	
        	}
            tx.commit();
        }
        catch( RuntimeException ex ) {
        	if (tx != null) tx.rollback() ;
			throw ex ; 
        }
        finally {
        	session.close() ;
        }
        return ids ;
	}
	
	static public int getBatchSize( String batchSize ) {
		int iSize = DEFAULT_BATCH_SIZE ;
		try {
			iSize = Integer.valueOf( batchSize ).intValue() ;
		}
		catch( NumberFormatException nfx ) {
			;
		}
		return iSize ;
	}
	
	private int checkBatchSize( int batchSize ) {
		if( batchSize < 1 || batchSize > MAX_BATCH_SIZE ) {
			return DEFAULT_BATCH_SIZE ;
		}
		return batchSize ;
	}
	
	private UniqueId generate( Session session ) throws UidGenException {
			
		String sId = null ;
		UniqueId existingUid = null;
		int cc = 0 ;
		
		do {
			long id = randomize() ;
			StringBuilder sb = new StringBuilder();
			Formatter formatter = new Formatter(sb, Locale.US);
			formatter.format( pattern.getFormat() , new Object[] { new Long(id) } );
			sId = Pattern.formId( openRange.getPrefix(), sb.toString() ) ;
			if( cc > CYCLE_DENSITY ) {
				moveToNextRange( session ) ;
				cc = 0 ;
			}
			if( cc > 0 && log.isDebugEnabled() ) {
				log.debug( "Circulating: " + existingUid.getId() + ". cc==" + cc ) ;
			}
			existingUid = null ;
			existingUid = 
				(UniqueId) session
					.createQuery( "from UniqueId as uid where uid.id = :id")
					.setText( "id", sId)
					.uniqueResult() ;
			cc++ ;
		} while ( existingUid != null ) ;
				
		UniqueId uid = new UniqueId() ;
		uid.setId( sId ) ;
		session.save( uid ) ;
		return uid ;		
	}
	
	private long randomize() {
		long id = new Long( openRange.getFromValue() + random.nextInt( range ) ).longValue() ;
		return id ;
	}
	
	private void moveToNextRange( Session session ) {
		//
		// Close the last range ...
		openRange.setStatus( CLOSED_STATUS ) ;
		session.update( openRange ) ;
		closedRange = openRange ;
		//
		// Create a new range ...
		openRange = new IdRange() ;
		openRange.setPrefix( closedRange.getPrefix() ) ;
		openRange.setFromValue( closedRange.getToValue() + 1 ) ;
		openRange.setToValue( openRange.getFromValue() + range - 1 ) ;
		openRange.setStatus( OPEN_STATUS ) ;
		session.save( openRange ) ;
		
		printRanges_Debug( session ) ;		
	}
	
	private void printRanges_Debug( Session session ) {
		if( log.isDebugEnabled() ) {
			StringBuilder b = new StringBuilder() ;
			b.append( "Density above " + CYCLE_DENSITY ) 
			 .append( "\nMoved to new open range...\n" ) 
			 .append( openRange.toString() ) ;
			
			List ranges = session
				.createQuery( "from IdRange" )
				.list();
			Iterator it = ranges.iterator() ;
			b.append( "\nAll recorded ranges...\n") ;
			while( it.hasNext() ) {
				IdRange range = (IdRange)it.next() ;
				b.append( range.toString() ).append( "\n" ) ;
			}
			log.debug( b.toString() ) ;
		}		
	}
	
	public void printIdentifiers_Debug() {
		if( log.isDebugEnabled() ) {
			StringBuilder b = new StringBuilder() ;
			b.append( "\nPrinting all identifiers...\n" ) ; 
			Session session = sessionFactory.getCurrentSession();
		    Transaction tx = session.beginTransaction();
			List ranges = sessionFactory.getCurrentSession().createQuery(
			   "from UniqueId")
			   .list();
			Iterator it = ranges.iterator() ;
			while( it.hasNext() ) {
				UniqueId uid = (UniqueId)it.next() ;
				b.append( uid.toString() ).append( "\n" ) ;
			}
			tx.commit() ;
			session.close() ;
			log.debug( b.toString() ) ;
		}		
	}

	/**
	 * @return the range
	 */
	public int getRange() {
		return range;
	}

}
