package com.limnj.noticeboardadmin.audit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
    enum EventType {
        SEND_VERIFICATION_CODE, SAVE_CATEGORY, DELETE_CATEGORY,
        SAVE_FILE, DELETE_FILE,
        UPLOAD_INVENTORIES, REGISTER_MEMBER, FIRST_LOGIN,
        QR_SECRET_KEY,
        SAVE_NOTICE, UPDATE_NOTICE, DELETE_NOTICE
    }
    enum ActionType { CREATE, UPDATE, DELETE, EMAIL, LOGIN }

    EventType eventType();
    ActionType actionType();
}