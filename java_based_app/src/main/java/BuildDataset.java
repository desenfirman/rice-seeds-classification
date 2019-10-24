
import javax.sound.midi.Soundbank;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BuildDataset {
    private String path;
    private ArrayList<ArrayList<String>> dataset;

    public BuildDataset(String path){
        this.path = path;
        this.dataset = new ArrayList<ArrayList<String>>();
        this.processDirectory();
    }

    public void processDirectory(){
        ArrayList<File> classes = this.getDirListing(this.path);

        for (File dir: classes) {
            ArrayList<File> files = this.getFileListing(dir.toString()) ;
            for ( File f: files){
                ImageObj imageObj = new ImageObj(f.toString());
                Map<String, Integer> dimension =  imageObj.getObjectDimension();
                String width = dimension.get("width").toString();
                String height = dimension.get("height").toString();
                String class_name = dir.getName();
                this.dataset.add(new ArrayList<>(Arrays.asList(width, height, class_name)));
            }
        }
        System.out.println(dataset);
    }

    public ArrayList<File> getDirListing(String path){
        File fh = new File(path);
        ArrayList<File> file_list = new ArrayList<File>(Arrays.asList(fh.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return new File(file, s).isDirectory();
            }
        })));
        return file_list;
    }

    public ArrayList<File> getFileListing(String path){
        File fh = new File(path);
        ArrayList<File> file_list = new ArrayList<File>(Arrays.asList(fh.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return new File(file, s).isFile();
            }
        })));
        return file_list;
    }

    public void createCSV(String output_path){
        FileWriter csvWriter = null;
        try {
            csvWriter = new FileWriter(output_path);
            csvWriter.append("width");
            csvWriter.append(",");
            csvWriter.append("height");
            csvWriter.append(",");
            csvWriter.append("class_name");
            csvWriter.append("\n");

            for (ArrayList<String> row: this.dataset){
                csvWriter.append(String.join(",", row));
                csvWriter.append("\n");
            }

            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BuildDataset bd = new BuildDataset("in/data");
        bd.createCSV("out/data.csv");
    }
}
