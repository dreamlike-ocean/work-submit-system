package cn.njupt.course.work_submit_system.entity;

import java.time.LocalDateTime;

public class Paper {

  private Integer id;
  private String answer;
  private Result result;
  private Integer studentId;
  private Integer questionId;

  private Integer homeworkId;


  public boolean check(){
    return !(
      answer == null || answer.isBlank() || questionId == null || questionId <= 0 ||
        homeworkId == null || homeworkId <= 0
      );

  }

  public Integer getHomeworkId() {
    return homeworkId;
  }

  public void setHomeworkId(Integer homeworkId) {
    this.homeworkId = homeworkId;
  }

  public enum Result{
    RIGHT,WRONG,WAIT
  }

  private LocalDateTime createTime = LocalDateTime.now();

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }


  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }


  public Integer getStudentId() {
    return studentId;
  }

  public void setStudentId(Integer studentId) {
    this.studentId = studentId;
  }


  public Integer getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Integer questionId) {
    this.questionId = questionId;
  }
}
