package net.jamezo97.clonecraft.clone;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

public class FakeGameProfile extends GameProfile{

	
	
	
//	private UUID id;
//    private String name;
    private PropertyMap properties = new PropertyMap();
    private boolean legacy;

    EntityClone clone;
    
    /**
     * Constructs a new Game Profile with the specified ID and name.
     * <p />
     * Either ID or name may be null/empty, but at least one must be filled.
     *
     * @param id Unique ID of the profile
     * @param name Display name of the profile
     * @throws java.lang.IllegalArgumentException Both ID and name are either null or empty
     */
    public FakeGameProfile(EntityClone clone) 
    {
    	super(null, "Steve");
    	this.clone = clone;
    }

    /**
     * Gets the unique ID of this game profile.
     * <p />
     * This may be null for partial profile data if constructed manually.
     *
     * @return ID of the profile
     */
	@Override
    public UUID getId() {
        return null;
    }

    /**
     * Gets the display name of this game profile.
     * <p />
     * This may be null for partial profile data if constructed manually.
     *
     * @return Name of the profile
     */
	@Override
    public String getName() {
        return clone.getCommandSenderName();
    }

    /**
     * Returns any known properties about this game profile.
     *
     * @return Modifiable map of profile properties.
     */
	@Override
    public PropertyMap getProperties() {
        return properties;
    }

    /**
     * Checks if this profile is complete.
     * <p />
     * A complete profile has no empty fields. Partial profiles may be constructed manually and used as input to methods.
     *
     * @return True if this profile is complete (as opposed to partial)
     */
	@Override
    public boolean isComplete() {
        return false;//id != null && StringUtils.isNotBlank(getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FakeGameProfile that = (FakeGameProfile) o;

        return that.clone == clone;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (this.getName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("clone", clone)
                .append("properties", properties)
                .append("legacy", legacy)
                .toString();
    }

	@Override
    public boolean isLegacy() {
        return legacy;
    }
}
