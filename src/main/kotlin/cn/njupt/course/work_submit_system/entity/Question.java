package cn.njupt.course.work_submit_system.entity;


public class Question {

  private Integer id;
  private String desc;
  private String answer;
  private Integer homeworkId;
  private QuestionType type;

  public enum QuestionType{
    CHOICE(true),
    SIMPLE_VALUE(true),
    QA(false)
    ;

    private boolean autoCheck;

    QuestionType(boolean autoCheck) {
      this.autoCheck = autoCheck;
    }

    public boolean isAutoCheck() {
      return autoCheck;
    }
  }


  public boolean check(){
    if (desc == null || desc.isBlank() || type == null) return false;
    if (type.isAutoCheck() && (answer == null || answer.isBlank())) return false;
    return true;
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


  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }


  public Integer getHomeworkId() {
    return homeworkId;
  }

  public void setHomeworkId(Integer homeworkId) {
    this.homeworkId = homeworkId;
  }


  public QuestionType getType() {
    return type;
  }

  public void setType(QuestionType type) {
    this.type = type;
  }

}
