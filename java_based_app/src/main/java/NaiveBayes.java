import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.MathArrays;
import org.omg.CORBA.PRIVATE_MEMBER;
import sun.security.krb5.internal.crypto.Des;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
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
            System.out.println(DATASET_SIZE);
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



    public NaiveBayes(){
        loadDataset("out/data.csv", true);
        buildModel();
        System.out.println(this.model);

    }

    public static void main(String[] args) {
        NaiveBayes nv = new NaiveBayes();
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


class ModelKey{
    private final String key1, key2;
    public ModelKey(String args1, String args2){
            this.key1 = args1;
            this.key2 = args2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelKey)) return false;
        ModelKey key = (ModelKey) o;
        return this.key1 == key.key1 && this.key2 == key.key2;
    }

    @Override
    public int hashCode() {
        int result = this.key1.hashCode();
        result = 31 * result + this.key2.hashCode();
        return result;
    }
}
