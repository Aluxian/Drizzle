package com.aluxian.drizzle.lists;

public class DrawerListItem {

    public static final int TYPE_ICON_TEXT = 0;
    public static final int TYPE_SUBHEADER = 1;
    public static final int TYPE_DIVIDER = 2;

    public final int type;
    public final int titleResourceId;
    public final int iconResourceId;

    public DrawerListItem(int type, int titleResourceId, int iconResourceId) {
        this.type = type;
        this.titleResourceId = titleResourceId;
        this.iconResourceId = iconResourceId;
    }

}
