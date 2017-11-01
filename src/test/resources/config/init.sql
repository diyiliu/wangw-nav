drop table if exists nav_site;

drop table if exists nav_type;

/*==============================================================*/
/* Table: nav_site                                              */
/*==============================================================*/
create table nav_site
(
   ID                   int not null auto_increment,
   NAME                 varchar(100),
   URL                  varchar(200),
   TYPE                 int,
   MARK                 varchar(100),
   TOP                  int,
   primary key (ID)
);

alter table nav_site comment '网站地址';

/*==============================================================*/
/* Table: nav_type                                              */
/*==============================================================*/
create table nav_type
(
   ID                   int not null auto_increment,
   CODE                 varchar(5),
   NAME                 varchar(20),
   TOP                  int,
   primary key (ID)
);

alter table nav_type comment '网址类型';
