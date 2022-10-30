create table paper
(
    id          int auto_increment
        primary key,
    answer      varchar(36)                null,
    result      varchar(36) charset latin1 null,
    student_id  int                        null,
    question_id int                        null,
    create_time datetime                   null,
    homework_id int                        null
);

create index homework_index
    on paper (id, homework_id);

create index index_name
    on paper (student_id);

create index index_name2
    on paper (question_id);

INSERT INTO worksubmit.paper (id, answer, result, student_id, question_id, create_time, homework_id) VALUES (5, 'B', 'RIGHT', 2, 3, '2022-10-30 21:50:57', 4);
INSERT INTO worksubmit.paper (id, answer, result, student_id, question_id, create_time, homework_id) VALUES (6, 'A', 'WAIT', 2, 4, '2022-10-30 21:50:57', 4);
INSERT INTO worksubmit.paper (id, answer, result, student_id, question_id, create_time, homework_id) VALUES (7, 'B', 'WRONG', 2, 1, '2022-10-30 23:04:46', 3);
INSERT INTO worksubmit.paper (id, answer, result, student_id, question_id, create_time, homework_id) VALUES (8, 'A', 'WAIT', 2, 2, '2022-10-30 23:04:46', 3);
