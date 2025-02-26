create table if not exists sys_bucket
(
    id varchar(32) NOT NULL,
    name varchar(128) null,
    primary key (id)
);