package test.lrucache;

import java.io.Serializable;

public class SubCategory implements Serializable {

    private static final long serialVersionUID = -1398818336569218110L;

    public String id;

    public String name;

    public String parentCategoryId;


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((parentCategoryId == null) ? 0 : parentCategoryId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SubCategory other = (SubCategory) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (parentCategoryId == null) {
            if (other.parentCategoryId != null) {
                return false;
            }
        } else if (!parentCategoryId.equals(other.parentCategoryId)) {
            return false;
        }
        return true;
    }


}
