package pers.cyz.bigdatatool.node.uiservice.bean.pojo;

public class FileStatusPojo {
    int accessTime;
    long blockSize;
    int childrenNum;
    int fileId;
    String group;
    long length;
    long modificationTime;
    String owner;
    String pathSuffix;
    int permission;
    int replication;
    int storagePolicy;
    String type;

    public FileStatusPojo() {
    }

    public FileStatusPojo(int accessTime, long blockSize, int childrenNum, int fileId, String group, long length, long modificationTime, String owner, String pathSuffix, int permission, int replication, int storagePolicy, String type) {
        this.accessTime = accessTime;
        this.blockSize = blockSize;
        this.childrenNum = childrenNum;
        this.fileId = fileId;
        this.group = group;
        this.length = length;
        this.modificationTime = modificationTime;
        this.owner = owner;
        this.pathSuffix = pathSuffix;
        this.permission = permission;
        this.replication = replication;
        this.storagePolicy = storagePolicy;
        this.type = type;
    }

    public int getChildrenNum() {
        return childrenNum;
    }

    public void setChildrenNum(int childrenNum) {
        this.childrenNum = childrenNum;
    }

    public int getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(int accessTime) {
        this.accessTime = accessTime;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(long modificationTime) {
        this.modificationTime = modificationTime;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPathSuffix() {
        return pathSuffix;
    }

    public void setPathSuffix(String pathSuffix) {
        this.pathSuffix = pathSuffix;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public int getReplication() {
        return replication;
    }

    public void setReplication(int replication) {
        this.replication = replication;
    }

    public int getStoragePolicy() {
        return storagePolicy;
    }

    public void setStoragePolicy(int storagePolicy) {
        this.storagePolicy = storagePolicy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
