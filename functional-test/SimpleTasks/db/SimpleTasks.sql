/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
CREATE TABLE "public"."users" (
  "user_id" SERIAL,
  "user_email" VARCHAR(256) NOT NULL,
  "user_password" VARCHAR(16) NOT NULL,
  "user_name" VARCHAR(32) NOT NULL,
  "user_last_name" VARCHAR(32) NOT NULL,
  CONSTRAINT "users_pkey" PRIMARY KEY("user_id"),
  CONSTRAINT "users_user_email_key" UNIQUE("user_email")
) WITH OIDS;

CREATE TABLE "public"."tasks" (
  "task_id" SERIAL,
  "user_id" INTEGER NOT NULL,
  "task_title" VARCHAR(64) NOT NULL,
  "task_detail" VARCHAR(1024),
  "task_finished" BOOLEAN DEFAULT false,
  "task_create_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
  "task_end_date" TIMESTAMP(0) WITHOUT TIME ZONE,
  CONSTRAINT "tasks_pkey" PRIMARY KEY("task_id"),
  CONSTRAINT "tasks_users_fk" FOREIGN KEY ("user_id")
    REFERENCES "public"."users"("user_id")
    MATCH FULL
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE
) WITH OIDS;

ALTER TABLE "public"."tasks"
  ALTER COLUMN "user_id" SET STATISTICS 0;