package com.portol.common.model.app;

import com.portol.common.model.SeekStatus;

import java.io.Serializable;

//goes from app to server
public class AppCommand implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5494498786991978185L;
    private Command cmd;
    private String newContentKey;
    private String targetPlayerId;
    private SeekStatus newStatus;

    public AppCommand() {
        super();

    }

    public AppCommand(Command cmd, String targetPlayerId) {
        super();
        this.cmd = cmd;
        this.targetPlayerId = targetPlayerId;
    }

    public String getTargetPlayerId() {
        return targetPlayerId;
    }

    public void setTargetPlayerId(String targetPlayerId) {
        this.targetPlayerId = targetPlayerId;
    }

    public SeekStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(SeekStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getNewContentKey() {
        return newContentKey;
    }

    public void setNewContentKey(String newContentKey) {
        this.newContentKey = newContentKey;
    }

    public Command getCmd() {
        return cmd;
    }

    public void setCmd(Command cmd) {
        this.cmd = cmd;
    }

    public enum Command {
        PAUSE, SEEK, CHANGE_CONTENT, STOP, CHANGE_SOURCE, PLAY, SUBTITLE, VOL_UP, VOL_DOWN, GET_INDEX_INFO, STATUS_CHECK
    }
}
