package com.robam.roki.bean;

import java.util.List;

public class CookBookTagGroup {
    public long id;

    // @DatabaseField()
    public String name;

    // @DatabaseField()
    public int type;

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    public List<CookBookTag> cookbookTags;
}
