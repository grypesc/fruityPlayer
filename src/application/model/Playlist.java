package application.model;
import java.io.File;
import java.util.*;

public class Playlist {
	
	public Vector<File> vector;

	public Playlist()
	{
		vector = new Vector<File>(2);
	}

        public	Playlist(File newMedia)
	{
		vector = new Vector<File>(2);
		vector.addElement(newMedia);
	}


	
	public void add(File newSong)
	{
		vector.add(newSong);
	}
	
	public boolean isEmpty()
	{
		return vector.isEmpty();
	}
        
        public List<String> getTracksNames()
        {
            List<String> list = new ArrayList<String>();
            if (vector==null) return list;
            for (File temp : vector) 
                list.add(temp.getName());
            return list;
        }
        
        public void remove(String name)
        {
            int index=0;
            for (File temp : vector) 
            {
                if (temp.getName().equals(name))
                    break;
                index++;
            }
            vector.remove(index);
        }
        
        public int getTracksIndex(String name)
        {
            int index=0;
            for (File temp : vector) 
            {
                if (temp.getName().equals(name))
                    break;
                index++;
            }
            return index;
        }
}
