/*
 * $Id$
 */

create table if not exists item (
    id BIGINT not null AUTO_INCREMENT primary key,
    eveId BIGINT null, -- for future use.
    name VARCHAR(256) NOT NULL,
    CONSTRAINT unique UK_ItemName(name)
);

create table if not exists snapshot (
    id BIGINT not null AUTO_INCREMENT primary key,
    itemId BIGINT  not null,
    time VARCHAR(20) not null, -- format is YYYYY.MM.DD hhmmss
    contributor VARCHAR(50) NOT NULL,
    CONSTRAINT foreign key FK_Item(itemId) references item(id),
    CONSTRAINT unique key AK_Snapshot(itemId, time, contributor)
);

create table if not exists orderLine (
    id BIGINT not null AUTO_INCREMENT primary key,
    snapshotId BIGINT not null,
    isSellOrder TINYINT not null,
    price DOUBLE not null,
    volume BIGINT not null,
    CONSTRAINT foreign key FK_Snapshot(snapshotId) references snapshot(id)
);