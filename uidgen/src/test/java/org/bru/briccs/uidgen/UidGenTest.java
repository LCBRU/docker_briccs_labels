package org.bru.briccs.uidgen;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import org.bru.briccs.uidgen.UidGen.UidGenException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UidGenTest {
	
	private static UidGen uidgenSA = null ;
	private static UidGen uidgenPR = null ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		uidgenSA = new UidGen( Pattern.Sample ) ;
		uidgenPR = new UidGen( Pattern.Participant, 100 ) ;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenerateInt_SA() throws UidGenException {
		String[] ids = uidgenSA.generate( 100 ) ;
		if( ids.length != 100 ) {
			fail( "Incorrect number of identifiers generated" ) ;
		}	
		//
		// Print out one for a sanity check ...
		System.out.println( ids[0] ) ;
	}
	
	@Test
	public void testGenerateInt_PR() throws UidGenException {
		HashSet<String> identifiers = new HashSet<String>( 1000 ) ;
		for( int i=0; i<20; i++ ) {
			String[] ids = uidgenPR.generate( 50 ) ;
			if( ids.length != 50 ) {
				fail( "Incorrect number of identifiers generated" ) ;
			}	
			for( int j=0; j< ids.length; j++ ) {
				boolean bUnique = identifiers.add( ids[j] ) ;
				assertTrue( bUnique ) ;
			}
		}
		//
		// Print out one for a sanity check ...
		System.out.println( identifiers.iterator().next() ) ;
	}

}
