package cn.njupt.course.work_submit_system.entity;


import kotlin.text.StringsKt;

public class LoginUser {

  private Integer id;
  private String username;
  private String password;
  private Role role;

  public boolean loginCheck(){
    return !(username == null || username.isBlank() || password ==null || password.isBlank());
  }

  public enum Role{
    STUDENT,TEACHER,ADMIN
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  @Override
  public String toString() {
    return "LoginUser{" +
      "id=" + id +
      ", username='" + username + '\'' +
      ", password='" + password + '\'' +
      ", role=" + role +
      '}';
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  public boolean check(){
    return !(username == null || username.isBlank() || password == null || password.isBlank() || role == null);
  }
}
