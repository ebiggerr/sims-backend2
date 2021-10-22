CREATE TABLE IF NOT EXISTS batch
(
    id                     uuid not null
        constraint batch_pk
            primary key,
    "itemId"               uuid,
    "supplierId"               uuid,
    "creationTime"         timestamp,
    "lastModificationTime" timestamp,
    "expiryDateOfBatch" timestamp,
    "isDeleted"            boolean,
    "remark"          text
);

ALTER TABLE public."batch"
    ADD CONSTRAINT "batch_itemId_fk" FOREIGN KEY ("itemId") REFERENCES public."item"("id") ON UPDATE CASCADE;

ALTER TABLE public."batch"
    ADD CONSTRAINT "batch_supplierId_fk" FOREIGN KEY ("supplierId") REFERENCES public."supplier"("id") ON UPDATE CASCADE;

