CREATE TABLE SCHEDULE_TEST (
  ID bigint(15) default NULL,
  DEAL_COUNT int(11) default NULL,
  STS varchar(2) default NULL,
  OWN_SIGN varchar(50) not NULL,
  PRIMARY KEY (ID)
);


--动态的造几十万数据
CREATE  PROCEDURE CREATE_TEST_DATA(IN ownSign varchar(50),IN datanum INTEGER(11))
BEGIN
 declare i int DEFAULT 1;
  WHILE i <= datanum DO
    insert into SCHEDULE_TEST VALUES(i,0,'N',ownSign); 
   set i = i + 1;
  END WHILE;
END;

CALL CREATE_TEST_DATA('BASE',300000);