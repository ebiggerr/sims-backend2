CREATE TYPE accountstatus AS enum ('APPROVED', 'PENDING', 'REVOKED');

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

--
-- Name: account account_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE IF EXISTS ONLY public.account
    ADD CONSTRAINT account_pk PRIMARY KEY (id);


--
-- Name: accountRole accountrole_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE IF EXISTS ONLY public."accountRole"
    ADD CONSTRAINT accountrole_pk PRIMARY KEY ("accountId", "roleId");

--
-- Name: roleDetails roledetails_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE IF EXISTS ONLY public."roleDetails"
    ADD CONSTRAINT roledetails_pk PRIMARY KEY ("roleId");

--
-- Name: roledetails_roleid_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX roledetails_roleid_uindex ON public."roleDetails" USING btree ("roleId");


--
-- Name: accountRole accountrole_accountId_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE IF EXISTS ONLY public."accountRole"
    ADD CONSTRAINT "accountrole_accountId_fk" FOREIGN KEY ("accountId") REFERENCES public.account(id);


--
-- Name: accountRole accountrole_roleId_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE IF EXISTS ONLY public."accountRole"
    ADD CONSTRAINT "accountrole_roleId_fk" FOREIGN KEY ("roleId") REFERENCES public."roleDetails"("roleId") ON UPDATE CASCADE;
