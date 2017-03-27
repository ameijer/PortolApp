package com.portol.repository;

import com.portol.common.model.user.UserSettings;

import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by alex on 10/20/15.
 */

public class CurrencyColumn implements RushColumn<UserSettings.Currency> {

    @Override
    public String sqlColumnType() {
        return "integer";
    }

    @Override
    public String serialize(UserSettings.Currency myEnum, RushStringSanitizer rushStringSanitizer) {
        return rushStringSanitizer.sanitize(Integer.toString(myEnum.ordinal()));
    }

    @Override
    public UserSettings.Currency deserialize(String s) {
        return UserSettings.Currency.values()[Integer.parseInt(s)];
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{UserSettings.Currency.class};
    }
}
