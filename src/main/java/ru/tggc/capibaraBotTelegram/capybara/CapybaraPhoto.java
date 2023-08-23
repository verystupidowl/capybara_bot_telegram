package ru.tggc.capibaraBotTelegram.capybara;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CapybaraPhoto {
    private int ownerID;
    private int mediaID;
    private String accessKey;
    private String type;

    private String url;

    private static List<CapybaraPhoto> setDefaultPhotos() {
        List<CapybaraPhoto> capybaraPhotos = new ArrayList<>();
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239574));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239686));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239456));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239409));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239396));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239375));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239334));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239317));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239262));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239201));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239189));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239167));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239164));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239104));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239095));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239073));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239037));
        capybaraPhotos.add(new CapybaraPhoto("photo", -206143282, 457239025));
        return capybaraPhotos;
    }

    public static List<CapybaraPhoto> workingPhotos() {
        List<CapybaraPhoto> capybaraPhotos;
        {
            capybaraPhotos = new ArrayList<>();
            capybaraPhotos.add(new CapybaraPhoto("photo", -209917797, 457242284));
            capybaraPhotos.add(new CapybaraPhoto("photo", -209917797, 457242285));
            capybaraPhotos.add(new CapybaraPhoto("photo", -209917797, 457242283));
        }
        return capybaraPhotos;
    }

    public static List<CapybaraPhoto> winCapybara() {
        List<CapybaraPhoto> capybaraPhotos = new ArrayList<>();
        capybaraPhotos.add(new CapybaraPhoto("photo", -209917797, 457245512));
        capybaraPhotos.add(new CapybaraPhoto("photo", -209917797, 457245513));
        capybaraPhotos.add(new CapybaraPhoto("photo", -209917797, 457245514));
        capybaraPhotos.add(new CapybaraPhoto("photo", -209917797, 457245515));
        return capybaraPhotos;
    }

    public static List<CapybaraPhoto> drawCapybara() {
        List<CapybaraPhoto> capybaraPhotos = new ArrayList<>();
        capybaraPhotos.add(new CapybaraPhoto("photo", -209917797, 457245517));
        capybaraPhotos.add(new CapybaraPhoto("photo", -209917797, 457245518));
        capybaraPhotos.add(new CapybaraPhoto("photo", -209917797, 457245519));
        return capybaraPhotos;
    }


    public static CapybaraPhoto getDefaultPhoto() {
        Random random = new Random();
        return setDefaultPhotos().get(random.nextInt(17));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    private String photo;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public CapybaraPhoto(int ownerID, int mediaID) {
        this.mediaID = mediaID;
        this.ownerID = ownerID;
    }

    public CapybaraPhoto(String photo) {
        if (photo.contains("photo")) {
            this.type = photo.substring(0, 5);
            int ownerID = Integer.parseInt(photo.substring(5, photo.indexOf('_')));
            if (photo.length() >= 30) {
                this.ownerID = ownerID;
                this.mediaID = Integer.parseInt(photo.substring(photo.indexOf('_') + 1, photo.indexOf('_', 17)));
                this.accessKey = photo.substring(photo.indexOf('_', 17) + 1);
            } else {
                this.ownerID = ownerID;
                this.mediaID = Integer.parseInt(photo.substring(photo.indexOf('_') + 1));
            }
        } else if (photo.contains("doc")) {
            this.type = photo.substring(0, 3);
            int ownerID = Integer.parseInt(photo.substring(3, photo.indexOf('_')));
            if (photo.length() >= 17) {
                this.ownerID = ownerID;
                this.mediaID = Integer.parseInt(photo.substring(photo.indexOf('_') + 1, photo.indexOf('_', 15)));
                this.accessKey = photo.substring(photo.indexOf('_', 17) + 1);
            } else {
                this.ownerID = ownerID;
                this.mediaID = Integer.parseInt(photo.substring(photo.indexOf('_') + 1));
            }
        } else {
            this.url = photo;
        }

    }

    public CapybaraPhoto(int ownerID, int mediaID, String accessKey) {
        this.ownerID = ownerID;
        this.mediaID = mediaID;
        this.accessKey = accessKey;
    }

    public CapybaraPhoto(String type, int ownerID, int mediaID) {
        this.type = type;
        this.ownerID = ownerID;
        this.mediaID = mediaID;
    }

    public CapybaraPhoto(String type, int ownerID, int mediaID, String accessKey) {
        this.type = type;
        this.ownerID = ownerID;
        this.mediaID = mediaID;
        this.accessKey = accessKey;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public int getMediaID() {
        return mediaID;
    }

    public void setMediaID(int mediaID) {
        this.mediaID = mediaID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toUrl() {
        if (url == null)
            return "https://vk.com/" + this;
        else
            return url;
    }

    @Override
    public String toString() {
        if (type != null) {
            if (("" + accessKey).equals("null"))
                return type + ownerID + "_" + mediaID;
            else
                return type + ownerID + "_" + mediaID + "_" + accessKey;
        } else {
            return url;
        }
    }
}