
CREATE TABLE IF NOT EXISTS public.account (
    id uuid NOT NULL,
    username character varying(64),
    password character(60),
    "creationTime" timestamp without time zone,
    "lastModificationTime" timestamp without time zone,
    "isDeleted" boolean,
    "firstName" character varying(64),
    "lastName" character varying(64),
    "emailAddress" character varying(128),
    "accountStatus" public.accountstatus DEFAULT 'PENDING'::public.accountstatus,
    remarks text
);

CREATE TABLE IF NOT EXISTS public."accountRole" (
    "accountId" uuid NOT NULL,
    "roleId" character varying(32) NOT NULL,
    "creationTime" timestamp without time zone,
    "isDeleted" boolean,
    "lastModificationTime" timestamp without time zone,
    username character varying(64)
);

CREATE TABLE IF NOT EXISTS public."roleDetails" (
    "roleId" character varying(32) NOT NULL,
    "roleName" character varying(64),
    "roleDescription" text,
    "creationTime" timestamp without time zone,
    "lastModificationTime" timestamp without time zone,
    "isDeleted" boolean
);

CREATE TABLE IF NOT EXISTS item
(
    id                     uuid not null
        constraint item_pk
            primary key,
    "creationTime"         timestamp,
    "lastModificationTime" timestamp,
    sku                    varchar(64),
    name                   varchar(128),
    imagePath              varchar(128),
    volume                 varchar(32),
    dimensions             varchar(32),
    weight                 varchar(32),
    "isDeleted"            boolean,
    "description"          text
);

--
-- Name: index item_sku_uindex; Type: CONSTRAINT; Schema: public; Owner: postgres
--

create unique index item_sku_uindex
    on item (sku);
