create table course
(
    id          int auto_increment
        primary key,
    teacher_id  int         not null,
    `desc`      text        null,
    name        varchar(36) null,
    create_time datetime    null
);

create index index_name
    on course (teacher_id);

INSERT INTO worksubmit.course (id, teacher_id, `desc`, name, create_time) VALUES (1, 1, 'ADawdadwa', '高数C', '2022-10-30 01:00:30');
INSERT INTO worksubmit.course (id, teacher_id, `desc`, name, create_time) VALUES (2, 1, 'ADawdadwa', '高数B', '2022-10-30 01:00:33');
INSERT INTO worksubmit.course (id, teacher_id, `desc`, name, create_time) VALUES (3, 4, '123123123', 'os', '2022-10-30 17:41:29');
INSERT INTO worksubmit.course (id, teacher_id, `desc`, name, create_time) VALUES (4, 4, '123123123', 'network', '2022-10-30 19:57:19');
