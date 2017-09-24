package test.lrucache;

import java.util.ArrayList;

public class TestUtils {

    public static ArrayList<Category> getCats() {

        ArrayList<Category> cats = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            Category cat = new Category();
            cat.id = String.valueOf(i);
            cat.name = "cat" + i;

            cats.add(cat);

        }

        return cats;

    }

    public static ArrayList<SubCategory> getSubCats(Category cat) {
        ArrayList<SubCategory> cats = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            SubCategory subCat = new SubCategory();
            subCat.id = String.valueOf(i);
            subCat.name = "subcat" + i;
            subCat.parentCategoryId = cat.id;
            cats.add(subCat);

        }

        return cats;

    }
}
