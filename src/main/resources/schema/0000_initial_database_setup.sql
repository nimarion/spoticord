-- liquibase formatted sql

-- changeset markusk:1596819373758-1
create table if not exists Listens
(
    Id        int auto_increment primary key,
    Timestamp timestamp default CURRENT_TIMESTAMP not null,
    TrackId   varchar(22)                         not null,
    GuildId   varchar(100)                        not null,
    UserId    varchar(100)                        not null,
    INDEX Id (Id),
    INDEX listens_idx_guildid_userid (GuildId, UserId)
);
-- rollback DROP TABLE Listens


-- changeset markusk:1596819373758-2
create table if not exists Tracks
(
    Id            varchar(22)     not null primary key,
    Artists       varchar(200)    not null,
    AlbumImageUrl varchar(2083)   not null,
    AlbumTitle    varchar(200)    not null,
    TrackTitle    varchar(200)    not null,
    Duration      bigint unsigned not null
);
-- rollback DROP TABLE Tracks
