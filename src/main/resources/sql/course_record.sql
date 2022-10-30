create table course_record
(
    id          int auto_increment
        primary key,
    course_id   int      null,
    student_id  int      null,
    create_time datetime null
);

create index index_name
    on course_record (course_id);

INSERT INTO worksubmit.course_record (id, course_id, student_id, create_time) VALUES (1, 1, 2, '2022-10-30 00:33:10');
INSERT INTO worksubmit.course_record (id, course_id, student_id, create_time) VALUES (3, 2, 2, '2022-10-30 19:48:22');
INSERT INTO worksubmit.course_record (id, course_id, student_id, create_time) VALUES (4, 3, 2, '2022-10-30 19:49:19');
