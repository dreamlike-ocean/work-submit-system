package cn.njupt.course.work_submit_system.entity;

import java.util.List;

public class PaperVo {
  private Integer userId;
  private List<Paper> papers;

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public List<Paper> getPapers() {
    return papers;
  }

  public void setPapers(List<Paper> papers) {
    this.papers = papers;
  }
}
