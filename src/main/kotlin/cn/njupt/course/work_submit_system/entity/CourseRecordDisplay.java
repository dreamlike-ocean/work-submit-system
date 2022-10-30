package cn.njupt.course.work_submit_system.entity;

public class CourseRecordDisplay extends CourseRecord {

  private String courseName;

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  @Override
  public String toString() {
    return "CourseRecordDisplay{" +
      "courseName='" + courseName + '\'' +
      ", id=" + id +
      ", courseId=" + courseId +
      ", studentId=" + studentId +
      ", creatTime=" + creatTime +
      '}';
  }
}
