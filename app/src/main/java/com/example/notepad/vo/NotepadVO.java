package com.example.notepad.vo;

public class NotepadVO {
    private int notepadNo;
    private String subject;
    private String description;

    public NotepadVO(int notepadNo, String subject, String description) {
        this.notepadNo = notepadNo;
        this.subject = subject;
        this.description = description;
    }

    public int getNotepadNo() {
        return notepadNo;
    }

    public void setNotepadNo(int notepadNo) {
        this.notepadNo = notepadNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
