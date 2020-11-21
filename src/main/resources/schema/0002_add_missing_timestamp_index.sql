-- liquibase formatted sql
-- changeset nimarion:1605963946-1
ALTER TABLE `Listens` ADD INDEX `listens_idx_guildid_timestamp` (`GuildId`, `Timestamp`);

-- rollback DROP INDEX listens_idx_guildid_timestamp