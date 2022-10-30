package cn.njupt.course.work_submit_system.entity;


import java.time.LocalDateTime;

public class CourseRecord {

  protected Integer id;
  protected Integer courseId;
  protected Integer studentId;

  protected LocalDateTime creatTime = LocalDateTime.now();

  public LocalDateTime getCreatTime() {
    return creatTime;
  }

  public void setCreatTime(LocalDateTime creatTime) {
    this.creatTime = creatTime;
  }
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public Integer getCourseId() {
    return courseId;
  }

  public void setCourseId(Integer courseId) {
    this.courseId = courseId;
  }


  public Integer getStudentId() {
    return studentId;
  }

  public void setStudentId(Integer studentId) {
    this.studentId = studentId;
  }

  @Override
  public String toString() {
    return "CourseRecord{" +
      "id=" + id +
      ", courseId=" + courseId +
      ", studentId=" + studentId +
      ", creatTime=" + creatTime +
      '}';
  }
}
