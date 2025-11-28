package com.skaet_assessment.enums;

public enum UssdState {
    // Entry Points
    WELCOME,     //-- first screen
    REGISTER_NAME,   //-- registration, step 1
    REGISTER_PIN,    //-- step 2
    REGISTER_CONFIRM, //-- step 3

//    MAIN_MENU,

    //-- Deposit Flow
    DEPOSIT_ENTER_AMOUNT,
    DEPOSIT_CONFIRM,

    //-- Withdraw Flow
    WITHDRAW_ENTER_AMOUNT,
    WITHDRAW_ENTER_PIN,

    //-- Balance Flow
    BALANCE_CHECK_PIN,

    //-- History
    MINI_STATEMENT,

    //-- conversion to other currency
    MULTICURRENCY
}
