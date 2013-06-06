package gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ExtendedFileFilter extends FileFilter{

    String description,extension;

    public ExtendedFileFilter(String desc, String ext) {
        description = desc;
        extension = ext;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public boolean accept(File file)
    {
        if(file.isDirectory()) return true;
        String path=file.getAbsolutePath().toLowerCase();
        if(path.endsWith(extension)) return true;
        return false;
    }
}
