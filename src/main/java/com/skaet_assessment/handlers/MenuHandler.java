package com.skaet_assessment.handlers;

import com.skaet_assessment.dto.UssdRequest;
import com.skaet_assessment.enums.UssdState;

public interface MenuHandler {

    //-- returns the State this handler is responsible for
    UssdState getHandlerName();

    //-- processes input and return Response
    String handle(UssdRequest request);
}