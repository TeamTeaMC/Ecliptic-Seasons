package com.teamtea.eclipticseasons.compat.eclipticseasons_bundles;


import lombok.Data;

import java.util.List;

@Data
public class BundleConfig {
    private final String id;
    private final List<String> require;
    private final int version;
    private final boolean enable;
}
