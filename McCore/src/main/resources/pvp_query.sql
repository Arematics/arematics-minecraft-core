create schema if not exists pvp;

use pvp;

create table REVINFO
(
	REV int auto_increment
		primary key,
	REVTSTMP bigint null
);

create table cooldown
(
	`key` varchar(255) not null,
	second_key varchar(255) not null,
	end_time bigint not null,
	primary key (`key`, second_key)
);

create table inventories
(
	id bigint auto_increment
		primary key,
	`key` varchar(128) not null,
	title varchar(32) not null,
	slots tinyint not null,
	items longblob null
);

create table kit
(
	id bigint auto_increment
		primary key,
	name varchar(255) not null,
	permission varchar(255) null,
	cooldown bigint not null,
	min_play_time bigint null,
	display_item longblob not null,
	content longblob not null
);

create table permission
(
	permission varchar(255) not null
		primary key,
	description varchar(255) not null,
	mode varchar(255) not null
);

create table permission_AUD
(
	permission varchar(255) not null,
	REV int not null
		primary key,
	REVTYPE tinyint null,
	description varchar(255) null,
    mode varchar(255) not null,
	constraint FKai3y09cgfyxlwcubml6wj3n2i
		foreign key (REV) references REVINFO (REV)
);

create table ranks_permission
(
	id bigint not null,
	permission varchar(255) not null,
	until timestamp null,
	primary key (id, permission),
	constraint UK_ow1w1097pqv61rf5x3ky0o6lq
		unique (permission),
	constraint FKfdi2iww4o6ml4em4oocj6dfdf
		foreign key (id) references soulpvp.ranks (id),
	constraint FKntd4bgq8goac83kd4r6js46wt
		foreign key (permission) references soulpvp.permission (permission)
);

create table user_permission
(
	uuid varchar(36) not null,
	permission varchar(64) not null,
	until timestamp null,
	primary key (uuid, permission),
	constraint UK_c0210mhu8ug1x46rokkiorusj
		unique (permission),
	constraint user_permission_permission_permission_fk
		foreign key (permission) references soulpvp.permission (permission)
			on update cascade on delete cascade,
	constraint user_permission_user_uuid_fk
		foreign key (uuid) references soulpvp.user (uuid)
			on update cascade on delete cascade
);

