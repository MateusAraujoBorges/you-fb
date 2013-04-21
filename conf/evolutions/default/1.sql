# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table fbuser (
  uid                       bigint,
  username                  varchar(255),
  name                      varchar(255))
;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists fbuser;

SET REFERENTIAL_INTEGRITY TRUE;

