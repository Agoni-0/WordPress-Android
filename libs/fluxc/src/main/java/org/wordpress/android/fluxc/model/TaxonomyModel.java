package org.wordpress.android.fluxc.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.annotation.Column;
import com.yarolegovich.wellsql.core.annotation.PrimaryKey;
import com.yarolegovich.wellsql.core.annotation.Table;

import org.wordpress.android.fluxc.Payload;
import org.wordpress.android.fluxc.network.BaseRequest.BaseNetworkError;
import org.wordpress.android.util.StringUtils;

import java.io.Serializable;

@Table
@SuppressWarnings("NotNullFieldNotInitialized")
public class TaxonomyModel extends Payload<BaseNetworkError> implements Identifiable, Serializable {
    private static final long serialVersionUID = 8855881690971305398L;

    @PrimaryKey
    @Column private int mId;
    @Column private int mLocalSiteId;
    @NonNull @Column private String mName;
    @Nullable @Column private String mLabel;
    @Nullable @Column private String mDescription;
    @Column private boolean mIsHierarchical;
    @Column private boolean mIsPublic;

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public void setId(int id) {
        mId = id;
    }

    public int getLocalSiteId() {
        return mLocalSiteId;
    }

    public void setLocalSiteId(int localSiteId) {
        mLocalSiteId = localSiteId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        mName = name;
    }

    @Nullable
    public String getLabel() {
        return mLabel;
    }

    public void setLabel(@Nullable String label) {
        mLabel = label;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(@Nullable String description) {
        mDescription = description;
    }

    public boolean isHierarchical() {
        return mIsHierarchical;
    }

    public void setIsHierarchical(boolean isHierarchical) {
        mIsHierarchical = isHierarchical;
    }

    public boolean isPublic() {
        return mIsPublic;
    }

    public void setIsPublic(boolean isPublic) {
        mIsPublic = isPublic;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof TaxonomyModel)) return false;

        TaxonomyModel otherTaxonomy = (TaxonomyModel) other;

        return getId() == otherTaxonomy.getId()
                && getLocalSiteId() == otherTaxonomy.getLocalSiteId()
                && isHierarchical() == otherTaxonomy.isHierarchical()
                && isPublic() == otherTaxonomy.isPublic()
                && StringUtils.equals(getName(), otherTaxonomy.getName())
                && StringUtils.equals(getLabel(), otherTaxonomy.getLabel())
                && StringUtils.equals(getDescription(), otherTaxonomy.getDescription());
    }
}
