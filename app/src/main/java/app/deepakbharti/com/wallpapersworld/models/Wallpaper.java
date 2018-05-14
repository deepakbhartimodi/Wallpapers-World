package app.deepakbharti.com.wallpapersworld.models;

import com.google.firebase.database.Exclude;

public class Wallpaper {

    @Exclude
    public String id;

    public String title, desc, wallpaper;

    @Exclude
    public String category;

    @Exclude
    public boolean isFavourite = false ;

    public Wallpaper(){

    }

    public Wallpaper(String id, String title, String desc, String wallpaper, String category) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.wallpaper = wallpaper;
        this.category = category;
    }
}
