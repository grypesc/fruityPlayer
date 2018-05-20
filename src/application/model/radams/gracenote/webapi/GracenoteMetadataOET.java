package application.model.radams.gracenote.webapi;

// A simple class to encapsulate OET data

public class GracenoteMetadataOET
{
    private String _id   = "";
    private String _text = "";

    public GracenoteMetadataOET(String id, String text)
    {
        this._id   = id;
        this._text = text;
    }

    public String getID()   { return this._id; }
    public String getText() { return this._text; }

    public void print()
    {
        System.out.println("     + OET id:" + this._id + ", text:" + this._text);
    }
    
        public String getInfo()
    {
        String info= "     + " + this._text+"\n";
        return info;
        
    }
}
