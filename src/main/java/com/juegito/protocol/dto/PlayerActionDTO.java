package com.juegito.protocol.dto;

/**
 * DTO para acciones de jugador.
 */
public class PlayerActionDTO {
    private String actionType;
    private Object actionData;
    
    public PlayerActionDTO() {}
    
    public PlayerActionDTO(String actionType, Object actionData) {
        this.actionType = actionType;
        this.actionData = actionData;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public Object getActionData() {
        return actionData;
    }
    
    public void setActionData(Object actionData) {
        this.actionData = actionData;
    }
}
