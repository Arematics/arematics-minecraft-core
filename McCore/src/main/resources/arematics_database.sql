create schema arematics;

create table arematics_account
(
	uuid binary(255) not null primary key,
	verified tinyint(1) not null,
	soul_connection binary(255) null,
	constraint arematics_account_uuid_uindex
		unique (uuid)
);

