create schema if not exists soulpvp;

use soulpvp;

create table if not exists player_account
(
	arematics_connection binary(255) not null,
	uuid binary(255) not null primary key,
	constraint player_account_arematics_connection_uindex
		unique (arematics_connection),
	constraint player_account_uuid_uindex
		unique (uuid)
);

