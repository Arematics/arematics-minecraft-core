create schema if not exists soulpvp;

use soulpvp;

create table if not exists permission
(
    permission varchar(64) not null
        primary key,
    description varchar(64) not null
);

create table if not exists ranks
(
    id bigint auto_increment
        primary key,
    name varchar(16) not null,
    short_name varchar(8) not null,
    color_code varchar(16) not null,
    last_change timestamp default current_timestamp() not null on update current_timestamp(),
    constraint ranks_name_uindex
        unique (name)
);

create table if not exists ranks_permission
(
    id bigint not null,
    permission varchar(64) not null,
    until timestamp null,
    primary key (id, permission),
    constraint ranks_permission_id_fk
        foreign key (id) references ranks (id)
            on update cascade on delete cascade,
    constraint ranks_permission_permission_permission_fk
        foreign key (permission) references permission (permission)
            on update cascade on delete cascade
);

create table if not exists seasons
(
    name varchar(32) not null
        primary key,
    season_from timestamp default current_timestamp() not null on update current_timestamp(),
    season_to timestamp default '2000-12-14 00:00:00' not null,
    constraint seasons_name_uindex
        unique (name)
);

create table if not exists user
(
    arematics_connection varchar(36) not null,
    uuid varchar(36) not null
        primary key,
    last_join timestamp null,
    last_ip varchar(64) null,
    last_ip_change timestamp null,
    `rank` bigint default 0 not null,
    display_rank bigint null,
    muted timestamp null,
    constraint user_arematics_connection_uindex
        unique (arematics_connection),
    constraint user_uuid_uindex
        unique (uuid),
    constraint user_ranks_id_fk
        foreign key (`rank`) references ranks (id)
            on update cascade,
    constraint user_ranks_id_fk_2
        foreign key (display_rank) references ranks (id)
            on update cascade
);

create table if not exists bans
(
    uuid varchar(36) not null
        primary key,
    banned_by varchar(36) not null,
    reason varchar(120) not null,
    banned_until timestamp default current_timestamp() not null on update current_timestamp(),
    constraint bans_banned_by_uuid_fk
        foreign key (banned_by) references user (uuid)
            on update cascade on delete cascade,
    constraint bans_banned_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade on delete cascade
);

create table if not exists bans_history
(
    uuid varchar(36) not null
        primary key,
    banned_by varchar(36) not null,
    reason varchar(120) not null,
    banned_until timestamp default current_timestamp() not null on update current_timestamp(),
    constraint bans_history_banned_by_uuid_fk
        foreign key (banned_by) references user (uuid)
            on update cascade,
    constraint bans_history_user_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade
);

create table if not exists user_permission
(
    uuid varchar(36) not null,
    permission varchar(64) not null,
    until timestamp null,
    primary key (uuid, permission),
    constraint user_permission_permission_permission_fk
        foreign key (permission) references permission (permission)
            on update cascade on delete cascade,
    constraint user_permission_user_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade on delete cascade
);

create table if not exists user_rank_history
(
    uuid varchar(36) not null
        primary key,
    new_rank bigint not null,
    old_rank bigint not null,
    changer varchar(64) not null,
    change_time timestamp default current_timestamp() not null on update current_timestamp(),
    constraint user_rank_history_new_rank_fk
        foreign key (new_rank) references ranks (id)
            on update cascade,
    constraint user_rank_history_old_rank_fk
        foreign key (old_rank) references ranks (id)
            on update cascade,
    constraint user_rank_history_user_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade on delete cascade
);

create table if not exists warns
(
    uuid varchar(36) not null,
    warned_by varchar(36) not null,
    amount int default 1 not null,
    reason varchar(120) not null,
    warned_time timestamp default current_timestamp() not null on update current_timestamp(),
    constraint warns_user_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade on delete cascade,
    constraint warns_warned_by_uuid_fk
        foreign key (warned_by) references user (uuid)
            on update cascade on delete cascade
);

create table if not exists warns_history
(
    uuid varchar(36) not null,
    warned_by varchar(36) not null,
    amount int default 1 not null,
    reason varchar(120) not null,
    warned_time timestamp default current_timestamp() not null on update current_timestamp(),
    constraint warns_history_user_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade on delete cascade,
    constraint warns_history_warned_by_uuid_fk
        foreign key (warned_by) references user (uuid)
            on update cascade on delete cascade
);

