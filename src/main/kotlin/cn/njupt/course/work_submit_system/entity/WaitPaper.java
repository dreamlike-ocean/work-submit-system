package cn.njupt.course.work_submit_system.entity;

public class WaitPaper {
  private Integer paperId;
  private Paper.Result result;

  public boolean check(){
    return !(paperId == null || paperId <= 0 || result == null);
  }

  public Integer getPaperId() {
    return paperId;
  }

  public void setPaperId(Integer paperId) {
    this.paperId = paperId;
  }

  public Paper.Result getResult() {
    return result;
  }

  public void setResult(Paper.Result result) {
    this.result = result;
  }
}
