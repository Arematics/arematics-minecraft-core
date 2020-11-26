create schema if not exists pvp;

use pvp;

create table if not exists clan
(
    id bigint auto_increment
        primary key,
    clan_name varchar(16) not null,
    tag varchar(6) not null,
    colorCode varchar(4) default '§b' not null,
    slots tinyint not null,
    kills int default 0 not null,
    deaths int default 0 not null,
    coins bigint default 0 not null,
    constraint clan_clan_name_uindex
        unique (clan_name),
    constraint clan_tag_uindex
        unique (tag)
);

create table if not exists clan_permission
(
    permission varchar(255) not null primary key,
    constraint clan_permission_permission_uindex
        unique (permission)
);

create table if not exists clan_ranks
(
    clan_id bigint not null,
    rank_name varchar(16) not null,
    colorCode varchar(4) not null,
    rank_level int null,
    primary key (clan_id, rank_name),
    constraint clan_ranks__clan_fk
        foreign key (clan_id) references clan (id)
            on update cascade on delete cascade
);

create table if not exists clan_member
(
    clan_id bigint not null,
    uuid varchar(36) not null,
    clan_rank varchar(16) not null,
    clan_kills int default 0 not null,
    clan_deaths int default 0 not null,
    primary key (uuid, clan_id),
    constraint clan_member__rank_fk
        foreign key (clan_id, clan_rank) references clan_ranks (clan_id, rank_name)
            on update cascade on delete cascade
);

create table if not exists clan_rank_permission
(
    clan_id bigint not null,
    rank_name varchar(16) not null,
    permission varchar(255) not null,
    primary key (clan_id, rank_name, permission),
    constraint clan_rank_permission__rank_fk
        foreign key (clan_id, rank_name) references clan_ranks (clan_id, rank_name)
            on update cascade on delete cascade
);

create table if not exists cooldown
(
    `key` varchar(255) not null,
    second_key varchar(255) not null,
    end_time bigint not null,
    primary key (`key`, second_key)
);

create table if not exists inventories
(
    id bigint auto_increment
        primary key,
    data_key varchar(128) not null,
    title varchar(32) not null,
    slots tinyint not null,
    items longblob null
);

create table if not exists kit
(
    id bigint auto_increment
        primary key,
    name varchar(255) not null,
    permission_permission varchar(255) null,
    cooldown bigint not null,
    min_play_time bigint null,
    display_item longblob not null
);

create table if not exists player_game_stats
(
    uuid varchar(36) not null
        primary key,
    kills int default 0 null,
    deaths int default 0 null,
    bounty int null
);

create table online_time
(
    uuid varchar(36) not null,
    time bigint null,
    afk bigint null
);



alter table clan
    add slots tinyint not null after colorCode;
