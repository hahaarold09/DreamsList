package harold.delacerna.com.dreamlist;

/**
 * Created by gwapogerald on 10/11/2017.
 */

public class Dream {
    private int id;
    private String name;
    private String price;
    private String desc;
    private byte[] image;

    public Dream(int id, String name, String price, String desc, byte[] image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
