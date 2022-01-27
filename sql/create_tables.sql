create table "WhiteListedDomain" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"AllowedDomain" VARCHAR NOT NULL), create table "FilteredTerm" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"term" VARCHAR NOT NULL);

create table "RecurringNotification" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"notification" VARCHAR NOT NULL,"frequency" BIGINT NOT NULL,"lastExecuted" VARCHAR NOT NULL);

create table "UserCommand" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"signature" VARCHAR NOT NULL,"output" VARCHAR NOT NULL);

create table "Bettor" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"nickname" VARCHAR NOT NULL,"pool" BIGINT NOT NULL,"unsafePool" BIGINT NOT NULL,"outcome" VARCHAR NOT NULL);

create table "BetSession" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"state" VARCHAR NOT NULL,"winPool" BIGINT NOT NULL,"losePool" BIGINT NOT NULL);
 
