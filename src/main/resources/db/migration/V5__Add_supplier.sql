CREATE TABLE IF NOT EXISTS supplier
(
    id                     uuid not null
        constraint supplier_pk
            primary key,
    "name"                 varchar(64),
    "contactNumber"        varchar(64),
    "creationTime"         timestamp,
    "lastModificationTime" timestamp,
    "isDeleted"            boolean,
    "remark"          text
);

ALTER TABLE public."item"
    ADD CONSTRAINT "item_supplierId_fk" FOREIGN KEY ("supplierId") REFERENCES public."supplier"("id") ON UPDATE CASCADE;

