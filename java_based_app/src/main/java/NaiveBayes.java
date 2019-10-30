import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;

public class NaiveBayes {

    private ArrayList<ArrayList<Double>>    x_dataset;
    private ArrayList<String>               y_dataset;
    private ArrayList<String>               y_pred;

    HashMap<String, ArrayList<DescriptiveStats>> model;

    private int FEATURE_SIZE;
    private int DATASET_SIZE;

    public void loadDataset(String input_path, boolean hasHeader){
        this.x_dataset = new ArrayList<ArrayList<Double>>();
        this.y_dataset = new ArrayList<String>();

        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(input_path));

            // array initialization
            FEATURE_SIZE = (csvReader.readLine()).split(",").length - 1;
            for (int i = 0; i < FEATURE_SIZE; i++) {
                this.x_dataset.add(new ArrayList<Double>());
            }

            csvReader.mark(0);
            if (hasHeader) csvReader.readLine();

            String row;
            DATASET_SIZE = 0;
            while ((row = csvReader.readLine()) != null){
                String[] row_data = row.split(",");
                for (int i = 0; i < FEATURE_SIZE; i++) {
                    ArrayList<Double> row_x = this.x_dataset.get(i);
                    row_x.add(Double.parseDouble(row_data[i]));
                    x_dataset.set(i, row_x);
                }
                int idx_class = FEATURE_SIZE; // index of column class in array = Feature size
                y_dataset.add(row_data[idx_class]);
                DATASET_SIZE++;
            }
//            System.out.println(DATASET_SIZE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void buildModel(){
        this.model = new HashMap<String, ArrayList<DescriptiveStats>>();
        for (int i = 0; i < this.DATASET_SIZE; i++) {
            String class_name = this.y_dataset.get(i);
            if (!model.containsKey(class_name))
            {
                ArrayList<DescriptiveStats> feature_set = new ArrayList<DescriptiveStats>();
                for (int j = 0; j < this.FEATURE_SIZE; j++) {
                    feature_set.add(new DescriptiveStats("" + j));
                }
                this.model.put(class_name, feature_set);
            }

            ArrayList<DescriptiveStats> feature_set = this.model.get(class_name);
            for (int j = 0; j < this.FEATURE_SIZE; j++) {
                DescriptiveStats new_value = feature_set.get(j);
                Double new_value_from_data = (this.x_dataset.get(j)).get(i);
                new_value.addNewData(new_value_from_data);
                feature_set.set(j, new_value);
            }
            this.model.put(class_name, feature_set);
        }
    }


    // PDF: Probability Density Function
    private static Double calculatePDF(Double x, Double mean, Double std_dev){
        Double x_minus_mean = x - mean;
        if (std_dev == 0.0){
            if (mean == 0.0) {
                return 1.0;
            }
            else {
                return  0.0;
            }
        }
        Double exp = Math.exp(-1 * (Math.pow(x_minus_mean, 2) / (2 * Math.pow(std_dev.doubleValue(), 2))));
        return (1 / (Math.sqrt(2 * Math.PI) * std_dev)) * exp;
    }


    public ArrayList<String> predict(ArrayList<Double> input_vector){
        HashMap<String, Double> probabilities = new HashMap<String, Double>();

        for (Map.Entry<String, ArrayList<DescriptiveStats>> entry : this.model.entrySet()) {
            String class_name = entry.getKey();
            ArrayList<DescriptiveStats> class_model = entry.getValue();
            probabilities.put(class_name, 1.0);
            for (int i = 0; i < class_model.size(); i++) {
                DescriptiveStats feature = class_model.get(i);
                Double mean = feature.getMean();
                Double std_dev = feature.getStd_dev();
                Double x = input_vector.get(i);

                Double new_prob = probabilities.get(class_name) * NaiveBayes.calculatePDF(x, mean, std_dev);
                probabilities.put(class_name, new_prob);
            }
        }

        //find best label
        String best_label = "";
        Double best_prob = -1.0;
        for (Map.Entry<String, Double> prob : probabilities.entrySet()) {
            String class_value = prob.getKey();
            Double prob_value = prob.getValue();
            if (class_value == "" || prob_value > best_prob){
                best_prob = prob_value;
                best_label = class_value;
            }
//            System.out.printf( "\n%s %.5f", best_label, best_prob);
        }
        ArrayList<String> result = new ArrayList<String>(Arrays.asList(best_label, best_prob.toString()));
        return result;
    }


    public void makeYPrediction(){
        this.y_pred = new ArrayList<String>(this.y_dataset);
        for (int i = 0; i < this.DATASET_SIZE; i++) {
            ArrayList<Double> input_vector =  new ArrayList<Double>();
            for (int j = 0; j < FEATURE_SIZE; j++) {
                input_vector.add(this.x_dataset.get(j).get(i));
            }

            ArrayList<String> y_pred_res = predict(input_vector);
            y_pred.set(i, y_pred_res.get(0));
        }
    }


    public Double getAccuracyMetric(){
        int correct = 0;
        for (int i = 0; i < this.DATASET_SIZE; i++) {
            if (y_pred.get(i).equals(y_dataset.get(i))){
                correct++;
            }
        }
        return (((double) correct) / this.DATASET_SIZE) * 100;
    }

    public void makePredictiosUsingTest(ArrayList<ArrayList<Double>> x_test_dataset, ArrayList<String> y_test_dataset){
        this.y_dataset = new ArrayList<String>(y_test_dataset);
        this.x_dataset = new ArrayList<ArrayList<Double>>(x_test_dataset);
        makeYPrediction();
    }


    public void importModel(String input_filepath){
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


        Type mapType = new TypeToken<Map<String, ArrayList<DescriptiveStats>>>(){}.getType();
        Gson gsonBuilder = new GsonBuilder().create();
        Map<String, ArrayList<DescriptiveStats>> loaded_model = gsonBuilder.fromJson(jsonString, mapType);

        this.model = new HashMap<String, ArrayList<DescriptiveStats>>(loaded_model);
    }


    public void exportModel(String output_filepath){
        Gson gson = new Gson();
        String jsonString = gson.toJson(this.model);
        try(FileWriter fileWriter = new FileWriter(output_filepath)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }


    public NaiveBayes(ArrayList<ArrayList<Double>> x_dataset, ArrayList<String> y_dataset){
        this.x_dataset = x_dataset;
        this.y_dataset = y_dataset;
        buildModel();
        makeYPrediction();
    }


    public NaiveBayes(String input_path, boolean hasHeader){
        loadDataset(input_path, hasHeader);
        buildModel();
        makeYPrediction();
//        System.out.println(getAccuracyMetric());
    }

    public NaiveBayes(String model_path){
        importModel(model_path);
    }


    public static void kCrossValidation(String input_path, boolean hasHeader, int k_folds){

    }

    public static void main(String[] args) {
        NaiveBayes nv = new NaiveBayes("out/data.csv", true);
//        nv.exportModel("out/model.json");
//        nv.importModel("out/model.json");
    }

}


class DescriptiveStats{

    private String feature_id = "";
    private Double mean;
    private Double variance;
    private int n_data;

    public DescriptiveStats(String feature_id){
        this.feature_id = feature_id;
        this.mean = 0.0;
        this.variance  = 0.0;
        this.n_data = 0;
    }

    public void addNewData(Double incoming_value){
        int n_data_new = n_data + 1;

        Double new_mean = ((this.mean * this.n_data) + incoming_value) / n_data_new;
        Double new_variance = 0.0;
        if (n_data_new > 1){
            new_variance = ((double) this.n_data / n_data_new) * (this.variance + (Math.pow((incoming_value - this.mean), 2) / n_data_new));
        }else{
            new_variance = Math.pow((incoming_value - new_mean), 2) / n_data_new;
        }

        this.mean = new_mean;
        this.variance = new_variance;
        this.n_data = n_data_new;
    }

    public Double getMean(){
        return this.mean;
    }

    public  Double getVariance(){
        return this.variance;
    }

    public Double getStd_dev(){
        return Math.sqrt(this.variance);
    }

    @Override
    public String toString() {
        return "feature_id: " + this.feature_id + " mean: " + this.mean + " variance: " + this.variance + " std_dev: " + this.getStd_dev();
    }
}