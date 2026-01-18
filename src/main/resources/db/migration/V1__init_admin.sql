CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

insert into profile(id, name, username, password, status, visible, created_date)
values(1, 'Admin', 'adminjon@gmail.com',
        '$2a$10$Nj.rYR8ffPgwf2zi9cO/6OVJ3QtcQNNJuRhSs1adRkuHo5AVaszX.', 'ACTIVE', true, now());

SELECT setval('profile_id_seq', max(id)) FROM profile;

insert into profile_role(profile_id, roles, created_date)
values(1, 'ROLE_USER', now()),
      (1, 'ROLE_ADMIN', now());
-- in the above codes you can write this code instead of 1 -- > (SELECT MAX(id) FROM profile)
-- do not forget to delete id variable in insert into profile(.....) when changed