create table question
(
    id          int auto_increment
        primary key,
    `desc`      text                       null,
    answer      varchar(36)                null,
    homework_id int                        not null,
    type        varchar(36) charset latin1 null
);

create index index_name
    on question (homework_id);

INSERT INTO worksubmit.question (id, `desc`, answer, homework_id, type) VALUES (1, '选择1', 'D', 3, 'CHOICE');
INSERT INTO worksubmit.question (id, `desc`, answer, homework_id, type) VALUES (2, '问答2', null, 3, 'QA');
INSERT INTO worksubmit.question (id, `desc`, answer, homework_id, type) VALUES (3, '第一题', 'A', 4, 'CHOICE');
INSERT INTO worksubmit.question (id, `desc`, answer, homework_id, type) VALUES (4, '第2题', null, 4, 'QA');
