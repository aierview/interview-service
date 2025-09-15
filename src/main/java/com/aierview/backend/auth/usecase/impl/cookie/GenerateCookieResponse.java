package com.aierview.backend.auth.usecase.impl.cookie;

import com.aierview.backend.auth.domain.enums.Environment;
import com.aierview.backend.auth.domain.model.cookie.CookieResponse;
import com.aierview.backend.auth.usecase.contract.cookie.IGenerateCookieResponse;

public class GenerateCookieResponse implements IGenerateCookieResponse {
    private final Environment environment;

    public GenerateCookieResponse(Environment environment) {
        this.environment = environment;
    }

    @Override
    public CookieResponse generate(String name, String value) {
        boolean secure = this.environment.equals(Environment.PROD) || this.environment.equals(Environment.HOMOLOG);
        String sameSite = secure ? "NONE" : "LAX";
        return new CookieResponse(name, value, true, secure, sameSite, "/");
    }
}
