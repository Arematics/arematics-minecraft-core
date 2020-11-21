create schema if not exists soulpvp;

use soulpvp;create table chat_click_action
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


create table permission
(
    permission  varchar(255) not null
        primary key,
    description varchar(255) not null
);

create table ranks
(
    id          bigint auto_increment
        primary key,
    color_code  varchar(255) not null,
    last_change datetime     not null,
    name        varchar(255) not null,
    short_name  varchar(255) not null
);

create table ranks_permission
(
    id         bigint       not null,
    permission varchar(255) not null,
    primary key (id, permission),
    constraint UK_ow1w1097pqv61rf5x3ky0o6lq
        unique (permission),
    constraint FKfdi2iww4o6ml4em4oocj6dfdf
        foreign key (id) references ranks (id),
    constraint FKntd4bgq8goac83kd4r6js46wt
        foreign key (permission) references permission (permission)
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

create table ranks_aud
(
    id          bigint       not null,
    rev         int          not null,
    revtype     tinyint      null,
    color_code  varchar(255) null,
    last_change datetime     null,
    name        varchar(255) null,
    short_name  varchar(255) null,
    primary key (id, rev),
    constraint FK9502yxwlwlrhjh6uw6k2gmrbq
        foreign key (rev) references revinfo (rev)
);

create table theme
(
    theme_key varchar(255) not null
        primary key,
    format    varchar(255) null
);

create table chat_theme_user
(
    player_id              varchar(255) not null
        primary key,
    active_theme_theme_key varchar(255) null,
    constraint FKjkx75ynifr3wuqicb6lmhbiqe
        foreign key (active_theme_theme_key) references theme (theme_key)
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

create table user
(
    uuid                 varchar(255) not null
        primary key,
    arematics_connection varchar(255) not null,
    last_ip              varchar(255) null,
    last_ip_change       datetime     null,
    last_join            datetime     null,
    display_rank         bigint       null,
    `rank`               bigint       null,
    constraint FKhk6vk7jhbqnhgoiev9qgqi4ie
        foreign key (`rank`) references ranks (id),
    constraint FKke9qwi7l889y60f3sl96a49p2
        foreign key (display_rank) references ranks (id)
);

create table user_aud
(
    uuid                 varchar(255) not null,
    rev                  int          not null,
    revtype              tinyint      null,
    arematics_connection varchar(255) null,
    last_ip_change       datetime     null,
    last_join            datetime     null,
    display_rank         bigint       null,
    `rank`               bigint       null,
    primary key (uuid, rev),
    constraint FK89ntto9kobwahrwxbne2nqcnr
        foreign key (rev) references revinfo (rev)
);

create table user_configurations
(
    uuid  varchar(255) not null,
    value varchar(255) null,
    name  varchar(255) not null,
    primary key (uuid, name),
    constraint FK71riswrhdnlx8r35ekibi7c1d
        foreign key (uuid) references user (uuid)
);

create table user_permission
(
    uuid       varchar(255) not null,
    permission varchar(255) not null,
    primary key (uuid, permission),
    constraint UK_c0210mhu8ug1x46rokkiorusj
        unique (permission),
    constraint FK5ofq6wb991hui77e9s3imm5y7
        foreign key (uuid) references user (uuid),
    constraint FKduatvkhed1pnadilqxx0ok5bx
        foreign key (permission) references permission (permission)
);

create table hibernate_sequence
(
    next_val bigint null
);

INSERT INTO hibernate_sequence (next_val) VALUES (1);

