package cn.njupt.course.work_submit_system.entity;

import java.util.List;

public class OnceHomework {
  private Homework homework;
  private List<Question> questions;

  public OnceHomework() {
  }

  public OnceHomework(Homework homework, List<Question> questions) {
    this.homework = homework;
    this.questions = questions;
  }

  public boolean check(){
    return !(
      homework.getCourseId() == null || homework.getCourseId() <=0 || questions.isEmpty() || questions.stream().anyMatch(q -> !q.check())
  );
  }

  public Homework getHomework() {
    return homework;
  }

  public void setHomework(Homework homework) {
    this.homework = homework;
  }

  public List<Question> getQuestions() {
    return questions;
  }

  public void setQuestions(List<Question> questions) {
    this.questions = questions;
  }
}
