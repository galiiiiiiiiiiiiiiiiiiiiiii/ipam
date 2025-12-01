package ipamapp.model;

public class IPEntry {
    private int no;
    private String ip;
    private int vlan;
    private String name;
    private String description;
    private String subnetMask;
    private String deviceType;
    private String location;

    public IPEntry(int no, String ip, int vlan, String name, String description,
                   String subnetMask, String deviceType, String location) {
        this.no = no;
        this.ip = ip;
        this.vlan = vlan;
        this.name = name;
        this.description = description;
        this.subnetMask = subnetMask;
        this.deviceType = deviceType;
        this.location = location;
    }

    public int getNo() { return no; }
    public String getIp() { return ip; }
    public int getVlan() { return vlan; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSubnetMask() { return subnetMask; }
    public String getDeviceType() { return deviceType; }
    public String getLocation() { return location; }

    public void setNo(int no) { this.no = no; }
    public void setIp(String ip) { this.ip = ip; }
    public void setVlan(int vlan) { this.vlan = vlan; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setSubnetMask(String subnetMask) { this.subnetMask = subnetMask; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public void setLocation(String location) { this.location = location; }
}
