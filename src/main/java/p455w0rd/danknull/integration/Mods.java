package p455w0rd.danknull.integration;

import java.util.function.Predicate;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;

public enum Mods {

    // spotless:off
    WAILA("Waila"),
    NEI("NotEnoughItems"),

    ;
    //spotless:on

    public final String modid;
    private final Predicate<ModContainer> modPredicate;
    private Boolean loaded;

    Mods(String modid) {
        this(modid, mod -> true); // Default: any version is OK
    }

    Mods(String modid, Predicate<ModContainer> predicate) {
        this.modid = modid;
        this.modPredicate = predicate;
    }

    public boolean isLoaded() {
        if (loaded != null) return loaded;
        ModContainer mod = Loader.instance()
            .getIndexedModList()
            .get(modid);
        if (mod == null) return loaded = false;
        return loaded = Loader.isModLoaded(modid) && modPredicate.test(mod);
    }

    /**
     * Creates a predicate that checks if a mod's version matches a given version range string.
     *
     * @param range A standard Maven version range string. Examples:
     *              <ul>
     *              <li>{@code "[2.2,)"} - Version 2.2 or higher</li>
     *              <li>{@code "(,2.1]"} - Version 2.1 or lower</li>
     *              <li>{@code "[2.0,3.0]"} - Version 2.0 up to and including 3.0</li>
     *              <li>{@code "[2.0,3.0)"} - Version 2.0 up to, but excluding, 3.0</li>
     *              <li>{@code "(,3.0)"} - Any version up to, but excluding, 3.0</li>
     *              <li>{@code "(2.0,3.0)"} - Any version between 2.0 and 3.0, exclusive</li>
     *              </ul>
     * @return A predicate for use in the enum constructor.
     */
    public static Predicate<ModContainer> versionMatches(String range) {
        VersionRange versionRange = VersionParser.parseRange(range);
        return mod -> {
            ArtifactVersion modVersion = mod.getProcessedVersion();
            return versionRange.containsVersion(modVersion);
        };
    }
}
