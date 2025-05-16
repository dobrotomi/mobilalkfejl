package com.example.mobilalk;

import android.os.Parcel;
import android.os.Parcelable;

public class ShoppingItem implements Parcelable {
    private String id;
    private String name;
    private String info;
    private String price;
    private float ratedInfo;
    private int imageResource;
    private int cartedCount;


    public ShoppingItem(int imageResource, String info, String name, String price, float ratedInfo, int cartedCount) {
        this.imageResource = imageResource;
        this.info = info;
        this.name = name;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.cartedCount = cartedCount;
    }

    public ShoppingItem() {}

    public float getRatedInfo() {
        return ratedInfo;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getCartedCount() {
        return cartedCount;
    }

    public String _getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    // Add Parcelable implementation
    protected ShoppingItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        info = in.readString();
        price = in.readString();
        ratedInfo = in.readFloat();
        imageResource = in.readInt();
        cartedCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(info);
        dest.writeString(price);
        dest.writeFloat(ratedInfo);
        dest.writeInt(imageResource);
        dest.writeInt(cartedCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShoppingItem> CREATOR = new Creator<ShoppingItem>() {
        @Override
        public ShoppingItem createFromParcel(Parcel in) {
            return new ShoppingItem(in);
        }

        @Override
        public ShoppingItem[] newArray(int size) {
            return new ShoppingItem[size];
        }
    };
}
