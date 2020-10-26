create schema if not exists pvp;

use pvp;

create table permission
(
    permission  varchar(255) not null
        primary key,
    description varchar(255) not null
);

create table kit
(
    id                    bigint auto_increment
        primary key,
    content               longblob     not null,
    cooldown              bigint       not null,
    display_item          longblob     not null,
    min_play_time         bigint       null,
    name                  varchar(255) not null,
    permission_permission varchar(255) null,
    constraint FK7uhnx9b0wbk671gf27o3jih7g
        foreign key (permission_permission) references permission (permission)
);

create table revinfo
(
    rev      int auto_increment
        primary key,
    revtstmp bigint null
);

create table permission_aud
(
    permission  varchar(255) not null,
    rev         int          not null,
    revtype     tinyint      null,
    description varchar(255) null,
    primary key (permission, rev),
    constraint FK8p00qhf8aau42hacp13k6x5hh
        foreign key (rev) references revinfo (rev)
);

create table hibernate_sequence
(
    next_val bigint null
);

INSERT INTO hibernate_sequence (next_val) VALUES (1);


