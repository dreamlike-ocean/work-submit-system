package cn.njupt.course.work_submit_system.entity;


import java.time.LocalDateTime;

public class Course {

  private Integer id;
  private Integer teacherId;
  private String desc;

  private String name;

  private LocalDateTime creatTime = LocalDateTime.now();


  public LocalDateTime getCreatTime() {
    return creatTime;
  }

  public void setCreatTime(LocalDateTime creatTime) {
    this.creatTime = creatTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public Integer getTeacherId() {
    return teacherId;
  }

  public void setTeacherId(Integer teacherId) {
    this.teacherId = teacherId;
  }


  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  @Override
  public String toString() {
    return "Course{" +
      "id=" + id +
      ", teacherId=" + teacherId +
      ", desc='" + desc + '\'' +
      ", name='" + name + '\'' +
      '}';
  }

  public boolean check(){
    return !(teacherId == null || teacherId <= 0 || name == null || name.isBlank());
  }
}
