create table SCHEDULE_TEST
(
  ID         NUMBER,
  DEAL_COUNT NUMBER,
  STS        VARCHAR2(10),
  OWN_SIGN   VARCHAR2(10)
);
create index IND_ID on SCHEDULE_TEST (ID);

create or replace procedure CREATE_TEST_DATA(ownSign in varchar2,datanum in  NUMBER) is
I NUMBER;
POINT NUMBER;

begin
I :=1;
LOOP
  EXIT WHEN I > datanum;
  insert into SCHEDULE_TEST VALUES(i,0,'N',ownSign); 
  I:=I+1;
  POINT :=POINT + 1;
  IF POINT > 1000 THEN
    POINT :=0;
    COMMIT;
  END IF;
END LOOP;

COMMIT;

end ;
/

EXECUTE CREATE_TEST_DATA('BASE',300000);