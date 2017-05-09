CREATE TABLE identifiers (
    ID  VARCHAR(32) NOT NULL,
	PRIMARY KEY (ID) 
) ;

-- IDENTIFIERS does require an index	
CREATE INDEX id_index USING BTREE ON identifiers (id);

-- IDRANGES will never be large enough to require an index
-- so just create the table...
CREATE TABLE idranges (
    PK        VARCHAR(64) NOT NULL,
    PREFIX    VARCHAR(8) NOT NULL,  
    FROMVALUE BIGINT NOT NULL,
    TOVALUE   BIGINT NOT NULL,
    STATUS    VARCHAR(8) NOT NULL,  
	PRIMARY KEY (PK) 
) ;

-- Add two initial open ranges, one for participants and one for samples	
INSERT INTO IDRANGES VALUES ( '01', 'PAR', 1, 1000000, 'OPEN' ) ;
INSERT INTO IDRANGES VALUES ( '02', 'SAM', 1, 1000000, 'OPEN' ) ;
