package com.amazonaws.lambda.capbankbot;

import java.util.LinkedHashMap;
import java.util.Map;

public class LexResponse {
    private DialogAction dialogAction;
    public LexResponse(DialogAction dialogAction) {
        this.dialogAction = dialogAction;
       }

    public DialogAction getDialogAction() {
        return dialogAction;
    }

    public void setDialogAction(DialogAction dialogAction) {
        this.dialogAction = dialogAction;
    }

}
