package com.example.contentproviderwithroom.Model;

import android.content.ContentValues;
import android.provider.BaseColumns;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = Menu.TABLE_NAME)
public class Menu {
    /** The name of the Menu table. */
    public static final String TABLE_NAME = "menus";

    /** The name of the ID column. */
    public static final String COLUMN_ID = BaseColumns._ID;

    /** The name of the name column. */
    public static final String COLUMN_NAME = "name";

    /** The unique ID of the menu. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = COLUMN_ID)
    public long id;

    /** The name of the menu. */
    @ColumnInfo(name = COLUMN_NAME)
    public String name;

    /**
     * Create a new {@link Menu} from the specified {@link ContentValues}.
     *
     * @param values A {@link ContentValues} that at least contain {@link #COLUMN_NAME}.
     * @return A newly created {@link Menu} instance.
     */
    public static Menu fromContentValues(ContentValues values) {
        final Menu menu = new Menu();
        if (values.containsKey(COLUMN_ID)) {
            menu.id = values.getAsLong(COLUMN_ID);
        }
        if (values.containsKey(COLUMN_NAME)) {
            menu.name = values.getAsString(COLUMN_NAME);
        }
        return menu;
    }

    public static final String[] MENUS = {"Son (76)","Ngoc(16)","Thu(76)","Ngan(61)","Tan(95)","Duong(49)","Hai(47)"
            ,"Phong(63)","Ngan(61)","Nguyen(62)","Dan(76)","Thay(Khong biet?)","F0","F1","F2","F3","F4","F5","F6","F7","F8","F9","F12"
    };
}