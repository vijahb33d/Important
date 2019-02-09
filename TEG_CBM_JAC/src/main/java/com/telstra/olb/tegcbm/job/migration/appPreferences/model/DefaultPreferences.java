/*
 * Created on Feb 8, 2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.appPreferences.model;

/**
 * @author hariharan.venkat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultPreferences {

    private String ownCode;

    private String cbmRoleType;

    private String defaultPreference;

    /**
     * @param ownCode2
     * @param roleType
     * @param defaultPreference2
     */
    public DefaultPreferences(String ownCode, String roleType, String defaultPreference) {
        this.ownCode = ownCode;
        this.cbmRoleType = roleType;
        this.defaultPreference = defaultPreference;

    }

    /**
     * @return Returns the cbmRoleType.
     */
    public String getCbmRoleType() {
        return cbmRoleType;
    }

    /**
     * @param cbmRoleType The cbmRoleType to set.
     */
    public void setCbmRoleType(String cbmRoleType) {
        this.cbmRoleType = cbmRoleType;
    }

    /**
     * @return Returns the defaultPreference.
     */
    public String getDefaultPreference() {
        return defaultPreference;
    }

    /**
     * @param defaultPreference The defaultPreference to set.
     */
    public void setDefaultPreference(String defaultPreference) {
        this.defaultPreference = defaultPreference;
    }

    /**
     * @return Returns the ownCode.
     */
    public String getOwnCode() {
        return ownCode;
    }

    /**
     * @param ownCode The ownCode to set.
     */
    public void setOwnCode(String ownCode) {
        this.ownCode = ownCode;
    }
}
