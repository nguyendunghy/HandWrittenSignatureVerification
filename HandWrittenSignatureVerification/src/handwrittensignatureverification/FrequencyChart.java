package handwrittensignatureverification;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class FrequencyChart extends ApplicationFrame {

    public FrequencyChart(String applicationTitle, String chartTitle, float[] value,String tung,String hoanh) {
        super(applicationTitle);
        //type 1 :error, type 2 : wave file type 3: coefficient
        DefaultCategoryDataset dataset
                = new DefaultCategoryDataset();

        dataset = (DefaultCategoryDataset) createDataset(value,"số điểm");
        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
                tung,
                hoanh,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);
    }

    private CategoryDataset createDataset(float[] value,String tile) {
        final DefaultCategoryDataset dataset
                = new DefaultCategoryDataset();

        String mytitle = tile;

        for (int i = 0; i < value.length; i++) {
            dataset.addValue(value[i], mytitle, String.valueOf(i));
        }

        return dataset;
    }

    public static void main(String[] args) {
        float[] Value = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 2, 3, 4, 5, 6, 7, 8, 90};
        FrequencyChart chart = new FrequencyChart("dung", "nguyen van", Value,"khoảng","số lượng");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);

    }
}
