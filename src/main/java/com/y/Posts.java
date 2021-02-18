package com.y;

/**
 *帖子
 */
public class Posts {

    //帖子id
    private String id;
    //帖子标题
    private String title;
    //帖子发布者
    private String posterName;
    //帖子角色
    private String posterRoles;
    //回帖数量
    private Long replyers;
    //老师回帖数量
    private Long lectorReplyers;
    //助教回帖数量
    private Long assistantReplyers;

    public Posts() {
    }

    public Posts(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPosterRoles() {
        return posterRoles;
    }

    public void setPosterRoles(String posterRoles) {
        this.posterRoles = posterRoles;
    }

    public Long getReplyers() {
        return replyers;
    }

    public void setReplyers(Long replyers) {
        this.replyers = replyers;
    }

    public Long getLectorReplyers() {
        return lectorReplyers;
    }

    public void setLectorReplyers(Long lectorReplyers) {
        this.lectorReplyers = lectorReplyers;
    }

    public Long getAssistantReplyers() {
        return assistantReplyers;
    }

    public void setAssistantReplyers(Long assistantReplyers) {
        this.assistantReplyers = assistantReplyers;
    }
}
