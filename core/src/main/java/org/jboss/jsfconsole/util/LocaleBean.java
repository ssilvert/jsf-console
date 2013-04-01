/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.jsfconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2013 Red Hat Inc.
 */
@ManagedBean(name="localeBean")
@RequestScoped
public class LocaleBean {

    private static final String LOCALE_COOKIE = "org.jboss.as.console.locale";
    private static final List<Locale> supportedLocales;
    static {
        Application application = FacesContext.getCurrentInstance().getApplication();

        List<Locale> localeList = new ArrayList<Locale>();
        localeList.add(application.getDefaultLocale());

        Iterator<Locale> localesIt = application.getSupportedLocales();
        while (localesIt.hasNext()) {
            localeList.add(localesIt.next());
        }
        supportedLocales = Collections.unmodifiableList(localeList);
    }

    public static final Locale USE_BROWSER_SETTING_LOCALE = new Locale("use-browser-setting");

    public List<Locale> getSupportedLocales() {
        return supportedLocales;
    }

    public Locale getDefaultLocale() {
        return USE_BROWSER_SETTING_LOCALE;
    }

    public static Cookie getLocaleCookie() {
        Map<String, Object> cookies = FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap();
        return (Cookie) cookies.get(LOCALE_COOKIE);
    }

    public Locale getSelectedLocale() {
        Cookie localeCookie = getLocaleCookie();

        if (localeCookie == null) {
            return getDefaultLocale();
        }

        return new Locale(localeCookie.getValue());
    }

    public void setSelectedLocale(Locale locale) {
        ExternalContext extCtx = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> cookieProps = new HashMap<String, Object>();

        if (locale == USE_BROWSER_SETTING_LOCALE) {
            cookieProps.put("maxAge", 0);
            extCtx.addResponseCookie(LOCALE_COOKIE, null, cookieProps);
            return;
        }

        cookieProps.put("maxAge", -1);
        extCtx.addResponseCookie(LOCALE_COOKIE, locale.toString(), cookieProps);
    }
}
