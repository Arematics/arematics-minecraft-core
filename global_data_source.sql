create schema if not exists soulpvp;

use soulpvp;

create table if not exists REVINFO
(
    rev int auto_increment
        primary key,
    revtstmp bigint null
);

create table if not exists ignored_player
(
    id bigint auto_increment
        primary key,
    ignorer varchar(36) not null,
    ignored varchar(36) not null
);

create table if not exists permission
(
    permission varchar(64) not null
        primary key,
    description varchar(64) not null
);

create table if not exists permission_AUD
(
    permission varchar(64) not null,
    rev int not null,
    description varchar(120) null,
    revtype tinyint null,
    primary key (permission, rev),
    constraint permission_aud_rev_uindex
        unique (rev)
);

create table if not exists quests
(
    id bigint auto_increment
        primary key,
    name varchar(32) not null,
    description varchar(120) not null,
    quest_type varchar(32) not null,
    rewards longblob null
);

create table if not exists ranks
(
    id bigint auto_increment
        primary key,
    name varchar(16) not null,
    short_name varchar(8) not null,
    color_code varchar(16) not null,
    in_team tinyint(1) not null,
    last_change timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint ranks_name_uindex
        unique (name)
);

create table if not exists ranks_AUD
(
    id bigint not null,
    rev int not null,
    revtype tinyint null,
    color_code varchar(255) null,
    last_change datetime null,
    name varchar(255) null,
    short_name varchar(255) null,
    primary key (id, rev)
)
    engine=MyISAM;

create index FK9502yxwlwlrhjh6uw6k2gmrbq
    on ranks_AUD (rev);

create table if not exists ranks_permission
(
    id bigint not null,
    permission varchar(64) not null,
    until timestamp null,
    mode varchar(128) null,
    primary key (id, permission),
    constraint UK_ow1w1097pqv61rf5x3ky0o6lq
        unique (permission),
    constraint ranks_permission_id_fk
        foreign key (id) references ranks (id)
            on update cascade on delete cascade
);

create table if not exists seasons
(
    name varchar(32) not null primary key ,
    season_from timestamp default CURRENT_TIMESTAMP not null,
    season_to timestamp default '2000-12-14 00:00:00' not null,
    constraint seasons_name_uindex
        unique (name)
);

create table if not exists user
(
    arematics_connection varchar(36) not null,
    uuid varchar(36) not null primary key,
    last_name varchar(255) not null,
    last_join timestamp null,
    last_ip varchar(64) null,
    last_ip_change timestamp null,
    `rank` bigint default 1 not null,
    display_rank bigint null,
    karma int not null,
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
    banned_until timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
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
    banned_until timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint bans_history_banned_by_uuid_fk
        foreign key (banned_by) references user (uuid)
            on update cascade,
    constraint bans_history_user_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade
);

create table if not exists user_AUD
(
    arematics_connection varchar(36) not null,
    uuid varchar(36) not null,
    rev int not null,
    last_name varchar(255) null,
    last_join timestamp null,
    last_ip varchar(64) null,
    last_ip_change timestamp null,
    `rank` bigint null,
    display_rank bigint null,
    karma int not null,
    revtype tinyint null,
    primary key (uuid, rev),
    constraint user_aud_rev_uindex
        unique (rev)
);

create table if not exists user_configurations
(
    uuid varchar(36) not null,
    name varchar(255) not null,
    value varchar(255) not null,
    primary key (uuid, name),
    constraint user_configurations_user_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade on delete cascade
);

create table if not exists user_friends
(
    uuid varchar(36) not null,
    target_uuid varchar(36) not null,
    primary key (uuid, target_uuid)
);

create table if not exists user_permission
(
    uuid varchar(36) not null,
    permission varchar(64) not null,
    until timestamp null,
    mode varchar(128) null,
    primary key (uuid, permission),
    constraint UK_c0210mhu8ug1x46rokkiorusj
        unique (permission),
    constraint user_permission_user_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade on delete cascade
);

create table if not exists user_quests
(
    id bigint not null
        primary key,
    uuid varchar(36) not null,
    end_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint user_quests_quests_id_fk
        foreign key (id) references quests (id)
            on update cascade on delete cascade,
    constraint user_quests_user_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade on delete cascade
);

create table if not exists warns
(
    uuid varchar(36) not null,
    warned_by varchar(36) not null,
    amount int default 1 not null,
    reason varchar(120) not null,
    warned_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
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
    warned_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint warns_history_user_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade on delete cascade,
    constraint warns_history_warned_by_uuid_fk
        foreign key (warned_by) references user (uuid)
            on update cascade on delete cascade
);

create table online_time
(
    uuid varchar(36) not null,
    time bigint null,
    afk bigint null,
    constraint online_time_uuid_fk
        foreign key (uuid) references user (uuid)
            on update cascade on delete cascade
);

create table chat_click_action
(
    id     bigint auto_increment
        primary key,
    placeholderAction varchar(255) null,
    value  varchar(255) null
);

create table chat_hover_action
(
    id     bigint auto_increment
        primary key,
    placeholderAction varchar(255) null,
    value  varchar(255) null
);

create table command_entity
(
    uuid varchar(255) not null
        primary key,
    cmd  varchar(255) not null
);

create table global_placeholder
(
    placeholder_key varchar(255) not null
        primary key
);

create table global_placeholder_actions
(
    id              bigint       not null
        primary key,
    placeholder_key varchar(255) null,
    click_action_id bigint       null,
    hover_action_id bigint       null,
    constraint FK7v4ifkptknqdibg3gp2thosm7
        foreign key (hover_action_id) references chat_hover_action (id),
    constraint FKt7k32h82dgn2vvlccpm4ngac5
        foreign key (click_action_id) references chat_click_action (id)
);

create table theme
(
    theme_key varchar(255) not null
        primary key,
    format    varchar(255) null
);

create table theme_global_placeholder_actions
(
    chat_theme_theme_key          varchar(255) not null,
    global_placeholder_actions_id bigint       not null,
    constraint FK7kkji6rpsgecxrfl7fmfpn2bq
        foreign key (global_placeholder_actions_id) references global_placeholder_actions (id),
    constraint FKjs4he1hg3q4k5s3ls91nr21ag
        foreign key (chat_theme_theme_key) references theme (theme_key)
);

create table theme_placeholder
(
    id              bigint auto_increment
        primary key,
    placeholder_key varchar(255) null,
    value           varchar(255) null,
    click_action_id bigint       null,
    hover_action_id bigint       null,
    constraint FK736dtr2gyg6dct4qnhw6e8b7i
        foreign key (click_action_id) references chat_click_action (id),
    constraint FKfxh3ieib5oc9885i40mtno3gx
        foreign key (hover_action_id) references chat_hover_action (id)
);

create table theme_mapping
(
    chat_theme_theme_key  varchar(255) not null,
    theme_placeholders_id bigint       not null,
    primary key (chat_theme_theme_key, theme_placeholders_id),
    constraint FK63gp6xbegaeg3c97lgfhusmep
        foreign key (theme_placeholders_id) references theme_placeholder (id),
    constraint FK643mqse5m14biwfg4uhd6qanw
        foreign key (chat_theme_theme_key) references theme (theme_key)
);
