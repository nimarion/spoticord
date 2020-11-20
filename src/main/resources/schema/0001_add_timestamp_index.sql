-- liquibase formatted sql
-- changeset nimarion:1605901236-1
ALTER TABLE
    `Listens`
ADD
    INDEX `listens_idx_guildid_userid_timestamp` (`GuildId`, `UserId`, `Timestamp`);

-- rollback DROP INDEX listens_idx_guildid_userid_timestamp

-- changeset nimarion:1605901236-2
ALTER TABLE
    `Listens`
ADD
    INDEX `listens_idx_guildid_userid_trackid` (`GuildId`, `UserId`, `TrackId`);

-- rollback DROP INDEX listens_idx_guildid_userid_trackid