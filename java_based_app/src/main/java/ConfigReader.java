import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfigReader {
    private Double gblur_kernel_size, canny_threshold, canny_range, dilate_size, erode_size;
    private HashMap<String, ArrayList<DescriptiveStats>> model;

    public ConfigReader() {
    }

    public ConfigReader(String input_filepath){
        String jsonString = "";
        try(FileReader fileReader = new FileReader(input_filepath)) {
            int ch = fileReader.read();
            while(ch != -1) {
                jsonString += (char)ch;
                ch = fileReader.read();
            }
        } catch (FileNotFoundException e) {
            // exception handling
        } catch (IOException e) {
            // exception handling
        }

        JsonParser parser = new JsonParser();
        JsonObject array = parser.parse(jsonString).getAsJsonObject();
        JsonElement config = array.get("imgproc_config");

        Gson gsonBuilder = new GsonBuilder().create();

        HashMap<String, Double> config_map;
        config_map = gsonBuilder.fromJson(config.toString(), HashMap.class);

        this.gblur_kernel_size = config_map.get("gblur_kernel_size");
        this.canny_threshold = config_map.get("canny_threshold");
        this.canny_range = config_map.get("canny_range");
        this.dilate_size = config_map.get("dilate_size");
        this.erode_size = config_map.get("erode_size");

        if (!array.has("model")) return;

        JsonElement model = array.get("model");
        Type mapType = new TypeToken<Map<String, ArrayList<DescriptiveStats>>>(){}.getType();
        Map<String, ArrayList<DescriptiveStats>> model_map = gsonBuilder.fromJson(model, mapType);
        this.model = new HashMap<String, ArrayList<DescriptiveStats>>(model_map);
    }

    public void exportConfig(String path){
        Gson gson = new Gson();
        HashMap<String, Double> configs = new HashMap<String, Double>();
        configs.put("gblur_kernel_size", gblur_kernel_size);
        configs.put("canny_threshold", canny_threshold);
        configs.put("canny_range", canny_range);
        configs.put("dilate_size", dilate_size);
        configs.put("erode_size", erode_size);

        JsonObject config_model = new JsonObject();
        config_model.add("imgproc_config", gson.toJsonTree(configs));
        if (this.model != null){
            config_model.add("model", gson.toJsonTree(this.model));
        }


        try(FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(gson.toJson(config_model));
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public Double getGblur_kernel_size() {
        return gblur_kernel_size;
    }

    public Double getCanny_threshold() {
        return canny_threshold;
    }

    public Double getCanny_range() {
        return canny_range;
    }

    public Double getDilate_size() {
        return dilate_size;
    }

    public Double getErode_size() {
        return erode_size;
    }

    public HashMap<String, ArrayList<DescriptiveStats>> getModel() {
        return model;
    }

    public void setGblur_kernel_size(Double gblur_kernel_size) {
        this.gblur_kernel_size = gblur_kernel_size;
    }

    public void setCanny_threshold(Double canny_threshold) {
        this.canny_threshold = canny_threshold;
    }

    public void setCanny_range(Double canny_range) {
        this.canny_range = canny_range;
    }

    public void setDilate_size(Double dilate_size) {
        this.dilate_size = dilate_size;
    }

    public void setErode_size(Double erode_size) {
        this.erode_size = erode_size;
    }

    public void setModel(HashMap<String, ArrayList<DescriptiveStats>> model) {
        this.model = model;
    }
}
