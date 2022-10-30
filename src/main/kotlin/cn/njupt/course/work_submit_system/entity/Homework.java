package cn.njupt.course.work_submit_system.entity;

import java.time.LocalDateTime;

public class Homework {

  private Integer id;
  private String desc;
  private Integer courseId;

  private LocalDateTime creatTime = LocalDateTime.now();


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


  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }


  public Integer getCourseId() {
    return courseId;
  }

  public void setCourseId(Integer courseId) {
    this.courseId = courseId;
  }

  @Override
  public String toString() {
    return "Homework{" +
      "id=" + id +
      ", desc='" + desc + '\'' +
      ", courseId=" + courseId +
      ", creatTime=" + creatTime +
      '}';
  }
}
