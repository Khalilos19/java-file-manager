package filemanager;

public class FichierTag {
    private int fichierId;
    private int tagId;

    public FichierTag(int fichierId, int tagId) {
        this.fichierId = fichierId;
        this.tagId = tagId;
    }

    public int getFichierId() { return fichierId; }
    public void setFichierId(int fichierId) { this.fichierId = fichierId; }
    public int getTagId() { return tagId; }
    public void setTagId(int tagId) { this.tagId = tagId; }
}