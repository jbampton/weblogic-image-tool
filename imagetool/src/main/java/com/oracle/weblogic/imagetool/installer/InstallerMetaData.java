// Copyright (c) 2024, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package com.oracle.weblogic.imagetool.installer;

public class InstallerMetaData {
    private String platform;
    private String location;
    private String hash;
    private String dateAdded;
    private String productVersion;

    /**
     * Constructor InstallerMetaData stores details about this installer.
     * @param platform   platform linux/arm64, linux/amd64
     * @param location   file path of the installer
     * @param hash      hash value
     * @param dateAdded  date added
     */
    public InstallerMetaData(String platform, String location, String hash, String dateAdded, String productVersion) {
        this.platform = platform;
        this.location = location;
        this.hash = hash;
        this.dateAdded = dateAdded;
        this.productVersion = productVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public String getLocation() {
        return location;
    }

    public String getHash() {
        return hash;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public String toString() {
        return "InstallerMetaData [platform=" + platform + ", location=" + location + ", hash=" + hash + ", "
            + "dateAdded=" + dateAdded + ", version=" + productVersion + "]";
    }
}
