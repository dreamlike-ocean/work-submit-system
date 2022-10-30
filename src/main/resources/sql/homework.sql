create table homework
(
    id          int auto_increment
        primary key,
    `desc`      text     not null,
    course_id   int      not null,
    create_time datetime null
);

create index index_name
    on homework (course_id);

INSERT INTO worksubmit.homework (id, `desc`, course_id, create_time) VALUES (1, '第一次zuoye', 1, '2022-10-30 01:00:59');
INSERT INTO worksubmit.homework (id, `desc`, course_id, create_time) VALUES (2, '第二次作业', 1, '2022-10-30 01:00:59');
INSERT INTO worksubmit.homework (id, `desc`, course_id, create_time) VALUES (3, '第三次作业', 1, '2022-10-30 01:35:50');
INSERT INTO worksubmit.homework (id, `desc`, course_id, create_time) VALUES (4, '第一次作业', 3, '2022-10-30 19:19:14');
